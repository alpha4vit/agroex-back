create table if not exists bet
(
    id      bigserial
        primary key,
    amount  bigint,
    bet_time timestamp(6) with time zone,
    lot_id bigint constraint fk_bet_lot references lot ON DELETE CASCADE,
    user_id bigint constraint fk_bet_user references users ON DELETE CASCADE
);

ALTER TABLE lot
    ADD COLUMN bets_id BIGINT;

ALTER TABLE lot
    ADD CONSTRAINT fk_lot_bets
        FOREIGN KEY (bets_id) REFERENCES bet(id);
