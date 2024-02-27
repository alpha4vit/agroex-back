ALTER TABLE lot
    ADD COLUMN IF NOT EXISTS lot_id bigint
        constraint fk_category_lots
            references lot