create table if not exists bet
(
    id      bigserial
        primary key,
    amount  bigint,
    bet_time timestamp(6) with time zone,
    lot_id bigint constraint fk_bet_lot references lot ON DELETE CASCADE,
    user_id bigint constraint fk_bet_user references users ON DELETE CASCADE
);
