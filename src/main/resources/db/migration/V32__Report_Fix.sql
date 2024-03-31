ALTER TABLE public.lot ALTER COLUMN original_price TYPE real;
ALTER TABLE public.lot ALTER COLUMN original_min_price TYPE real;
ALTER TABLE public.currency_rates ALTER COLUMN rate TYPE real;
ALTER TABLE public.bet ALTER COLUMN amount TYPE real;

DROP FUNCTION IF EXISTS base_lot_filter(timestamptz, timestamptz, varchar, bigint);
CREATE OR REPLACE FUNCTION base_lot_filter(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null,
  vCountryId bigint default null
) RETURNS TABLE (
  id                     bigint,
  creation_date          timestamptz,
  original_currency      varchar,
  enabled_by_admin       boolean,
  expiration_date        timestamptz,
  lot_type               varchar,
  original_price         real,
  title                  varchar,
  location_region        varchar,
  location_country       varchar,
  country_id             bigint,
  product_category_title varchar,
  original_min_price     real,
  inner_status           varchar,
  user_status            varchar,
  status                 varchar,
  duration               bigint,
  user_id                uuid,
  admin_comment          varchar,
  actual_start_date      timestamptz
) AS $$
BEGIN
    RETURN QUERY (
        SELECT
            l.id,
            l.creation_date,
            l.currency,
            l.enabled_by_admin,
            l.expiration_date,
            l.lot_type,
            l.original_price,
            l.title,
            loc.region AS location_region,
            c.name AS location_country,
            c.id AS country_id,
            prc.title AS product_category_title,
            l.original_min_price,
            l.inner_status,
            l.user_status,
            l.status,
            l.duration,
            l.user_id,
            l.admin_comment,
            l.actual_start_date
          FROM lot l
          LEFT JOIN location loc ON loc.id = l.location_id
          LEFT JOIN country c ON c.id = loc.country_id
          LEFT JOIN product_category prc ON prc.id = l.product_category_id
         WHERE
           (
             NOT (COALESCE(l.actual_start_date, l.creation_date) <= vActualStartDate AND l.expiration_date <= vActualStartDate)
             AND NOT (COALESCE(l.actual_start_date, l.creation_date) >= vExpirationDate AND l.expiration_date >= vExpirationDate)
           )
           AND (vLotType ISNULL OR vLotType = l.lot_type)
           AND (vCountryId ISNULL OR vCountryId = c.id)
    );
END
$$ LANGUAGE PLPGSQL STABLE;


DROP FUNCTION IF EXISTS lot_filter_by_price(timestamptz, timestamptz, varchar, bigint);
CREATE OR REPLACE FUNCTION lot_filter_by_price(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null,
  vCountryId bigint default null
) RETURNS TABLE (
  id                     bigint,
  creation_date          timestamp with time zone,
  original_currency      varchar,
  enabled_by_admin       boolean,
  expiration_date        timestamp with time zone,
  lot_type               varchar,
  original_price         real,
  title                  varchar,
  location_region        varchar,
  location_country       varchar,
  country_id             bigint,
  product_category_title varchar,
  original_min_price     real,
  inner_status           varchar,
  user_status            varchar,
  status                 varchar,
  duration               bigint,
  user_id                uuid,
  admin_comment          varchar,
  actual_start_date      timestamp with time zone,
  calculated_price       real
) AS $$
BEGIN
  RETURN QUERY (
    SELECT
        l.*,
        (CASE
          WHEN l.original_currency <> 'USD' THEN COALESCE(l.original_price, l.original_min_price) * cr.rate
          ELSE COALESCE(l.original_price, l.original_min_price)
        END) AS calculated_price
      FROM base_lot_filter(vActualStartDate, vExpirationDate, vLotType, vCountryId) l
      LEFT JOIN currency_rates cr
        ON  cr.currency = l.original_currency
       AND cr.target_currency = 'USD'
     ORDER BY calculated_price DESC
     LIMIT 10
  );
END
$$ LANGUAGE PLPGSQL STABLE;


DROP FUNCTION IF EXISTS user_filter_by_lot_count(timestamptz, timestamptz, varchar, bigint);
CREATE OR REPLACE FUNCTION user_filter_by_lot_count(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null,
  vCountryId bigint default null
) RETURNS TABLE (
  id            uuid,
  username      varchar,
  email         varchar,
  lot_quantity  bigint,
  creation_date timestamp with time zone
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      lot_scope AS (
        SELECT
            lots.user_id,
            COUNT(lots.user_id) AS lot_count
          FROM base_lot_filter(vActualStartDate, vExpirationDate, vLotType, vCountryId) lots
         GROUP BY user_id
        )
    SELECT
        u.id,
        u.username,
        u.email,
        us.lot_count,
        u.creation_date
      FROM users u
      JOIN lot_scope us ON us.user_id = u.id
     ORDER BY us.lot_count DESC
  );
END
$$ LANGUAGE PLPGSQL STABLE;

DROP FUNCTION IF EXISTS owner_filter_by_bets(timestamptz, timestamptz, varchar, bigint);
CREATE OR REPLACE FUNCTION owner_filter_by_bets(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null,
  vCountryId bigint default null
) RETURNS TABLE (
  id            uuid,
  username      varchar,
  email         varchar,
  bet_amount    real,
  creation_date timestamp with time zone
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      bet_scope AS (
        SELECT DISTINCT ON (b.lot_id)
            lots.user_id,
            (CASE
              WHEN lots.original_currency <> 'USD' THEN b.amount * cr.rate
              ELSE b.amount
            END) as amount
          FROM bet b
          JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType, vCountryId) lots ON lots.id = b.lot_id
          LEFT JOIN currency_rates cr
            ON  cr.currency = lots.original_currency
           AND cr.target_currency = 'USD'
         WHERE lots.status = 'finished'
         ORDER BY b.lot_id, b.bet_time DESC
      ),
      user_scope AS (
        SELECT
            bs.user_id,
            sum(bs.amount) AS amount
          FROM bet_scope bs
         GROUP BY user_id
      )
    SELECT
        u.id,
        u.username,
        u.email,
        us.amount,
        u.creation_date
      FROM user_scope us
      JOIN users u ON u.id = us.user_id
     ORDER BY us.amount DESC
     LIMIT 10
  );
END
$$ LANGUAGE PLPGSQL STABLE;

DROP FUNCTION IF EXISTS participant_filter_by_bets(timestamptz, timestamptz, varchar, bigint);
CREATE OR REPLACE FUNCTION participant_filter_by_bets(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null,
  vCountryId bigint default null
) RETURNS TABLE (
  id            uuid,
  username      varchar,
  email         varchar,
  price_sum     real,
  creation_date timestamp with time zone
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      bet_scope AS (
        SELECT DISTINCT ON (b.lot_id)
            b.user_id,
            (CASE
              WHEN lots.original_currency <> 'USD' THEN b.amount * cr.rate
              ELSE b.amount
            END) as amount
          FROM bet b
          JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType, vCountryId) lots ON lots.id = b.lot_id
          LEFT JOIN currency_rates cr
            ON  cr.currency = lots.original_currency
           AND cr.target_currency = 'USD'
         WHERE lots.status = 'finished'
         ORDER BY b.lot_id, b.bet_time DESC
      ),
      user_scope AS (
        SELECT
            bs.user_id,
            sum(bs.amount) AS amount
          FROM bet_scope bs
         GROUP BY user_id
      )
    SELECT
        u.id,
        u.username,
        u.email,
        us.amount,
        u.creation_date
      FROM users u
      JOIN user_scope us ON us.user_id = u.id
     ORDER BY us.amount DESC
     LIMIT 10
    );
END
$$ LANGUAGE PLPGSQL STABLE;


DROP FUNCTION IF EXISTS country_owner_filter_by_bets(timestamptz, timestamptz, varchar);
CREATE OR REPLACE FUNCTION country_owner_filter_by_bets(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null
) RETURNS TABLE (
  id            bigint,
  name          varchar,
  total_bet_sum real
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      bet_scope AS (
        SELECT DISTINCT ON (b.lot_id)
            lots.country_id,
            lots.user_id,
            (CASE
              WHEN lots.original_currency <> 'USD' THEN b.amount * cr.rate
              ELSE b.amount
            END) as amount
          FROM bet b
          JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType) lots ON lots.id = b.lot_id
          LEFT JOIN currency_rates cr
            ON cr.currency = lots.original_currency
           AND cr.target_currency = 'USD'
         WHERE lots.status = 'finished'
         ORDER BY b.lot_id, b.bet_time DESC
      ),
      user_scope AS (
        SELECT
            bs.user_id,
            bs.country_id,
            sum(bs.amount) AS amount
          FROM bet_scope bs
         GROUP BY bs.user_id, bs.country_id
      ),
      country_scope AS (
        SELECT
            c.id,
            c.name,
            SUM(us.amount) AS total_bet_sum
          FROM country c
          JOIN user_scope us ON us.country_id = c.id
         GROUP BY c.id
         ORDER BY total_bet_sum DESC
      )
      SELECT
          country_scope.id,
          country_scope.name,
          country_scope.total_bet_sum
        FROM country_scope
       LIMIT 10
    );
END
$$ LANGUAGE PLPGSQL STABLE;


DROP FUNCTION IF EXISTS country_filter_by_lot_count(timestamptz, timestamptz, varchar);
CREATE OR REPLACE FUNCTION country_filter_by_lot_count(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null
) RETURNS TABLE (
  id                 bigint,
  name               varchar,
  total_lot_quantity bigint
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      country_scope AS (
        SELECT
            c.id,
            c.name,
            COUNT(lots.id) AS total_lot_quantity
          FROM country c
          JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType) lots ON lots.country_id = c.id
         GROUP BY c.id
      )
    SELECT
        country_scope.id,
        country_scope.name,
        country_scope.total_lot_quantity
      FROM country_scope
     ORDER BY country_scope.total_lot_quantity DESC
     LIMIT 10
  );
END
$$ LANGUAGE PLPGSQL STABLE;

DROP FUNCTION IF EXISTS country_filter_by_owner_count(timestamptz, timestamptz, varchar);
CREATE OR REPLACE FUNCTION country_filter_by_owner_count(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null
) RETURNS TABLE (
  id                   bigint,
  name                 varchar,
  total_owners_quantity bigint
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      country_scope AS (
        SELECT
            c.id,
            c.name,
            COUNT(DISTINCT u.id) as total_owners_quantity
          FROM country c
          LEFT JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType) lots ON lots.country_id = c.id
          JOIN users u ON u.id = lots.user_id
         GROUP BY c.id
         ORDER BY total_owners_quantity
      )
    SELECT
        country_scope.id,
        country_scope.name,
        country_scope.total_owners_quantity
      FROM country_scope
     ORDER BY country_scope.total_owners_quantity DESC
     LIMIT 10
  );
END
$$ LANGUAGE PLPGSQL STABLE;


DROP FUNCTION IF EXISTS country_filter_by_lot_price(timestamptz, timestamptz, varchar);
CREATE OR REPLACE FUNCTION country_filter_by_lot_price(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null
) RETURNS TABLE (
  id                bigint,
  name              varchar,
  price_sum real
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      lot_scope AS (
        SELECT
            lots.id,
            lots.country_id,
            SUM(CASE
              WHEN lots.original_currency <> 'USD' THEN COALESCE(lots.original_price, lots.original_min_price) * cr.rate
              ELSE COALESCE(lots.original_price, lots.original_min_price)
            END) AS price_sum
          FROM base_lot_filter(vActualStartDate, vExpirationDate, vLotType) lots
          LEFT JOIN currency_rates cr
            ON cr.currency = lots.original_currency
           AND cr.target_currency = 'USD'
         GROUP BY lots.id, lots.country_id
      )
    SELECT
        c.id,
        c.name,
        SUM(ls.price_sum) as price_sum
      FROM country c
      JOIN lot_scope ls ON ls.country_id = c.id
     GROUP BY c.id, c.name
     ORDER BY price_sum DESC
     LIMIT 10
    );
END
$$ LANGUAGE PLPGSQL STABLE;

DROP FUNCTION IF EXISTS country_filter_by_participant_count(timestamptz, timestamptz, varchar);
CREATE OR REPLACE FUNCTION country_filter_by_participant_count(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null
) RETURNS TABLE (
  id                bigint,
  name              varchar,
  total_participant_count bigint
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      lot_scope AS (
        SELECT
            lots.country_id,
            count(DISTINCT b.user_id) as user_count
          FROM bet b
          JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType) lots ON lots.id = b.lot_id
         WHERE lots.status = 'finished'
         GROUP BY lots.country_id
      )
    SELECT
        c.id,
        c.name,
        ls.user_count
      FROM country c
      JOIN lot_scope ls ON ls.country_id = c.id
     ORDER BY ls.user_count
     LIMIT 10
    );
END
$$ LANGUAGE PLPGSQL STABLE;

DROP FUNCTION IF EXISTS country_participant_filter_by_bets(timestamptz, timestamptz, varchar);
CREATE OR REPLACE FUNCTION country_participant_filter_by_bets(
  vActualStartDate timestamptz,
  vExpirationDate timestamptz,
  vLotType varchar default null
) RETURNS TABLE (
  id         bigint,
  name       varchar,
  total_bet_sum real
) AS $$
BEGIN
  RETURN QUERY (
    WITH
      bet_scope AS (
        SELECT DISTINCT ON (b.lot_id)
            lots.country_id,
            b.user_id,
            (CASE
              WHEN lots.original_currency <> 'USD' THEN b.amount * cr.rate
              ELSE b.amount
            END) as amount
          FROM bet b
          JOIN base_lot_filter(vActualStartDate, vExpirationDate, vLotType) lots ON lots.id = b.lot_id
          LEFT JOIN currency_rates cr
            ON cr.currency = lots.original_currency
           AND cr.target_currency = 'USD'
         WHERE lots.status = 'finished'
         ORDER BY b.lot_id, b.bet_time DESC
      )
    SELECT
        c.id,
        c.name,
        sum(bs.amount) as total_bet_sum
      FROM country c
      JOIN bet_scope bs ON bs.country_id = c.id
     GROUP BY c.id
     ORDER BY total_bet_sum DESC
     LIMIT 10
  );
END
$$ LANGUAGE PLPGSQL STABLE;
