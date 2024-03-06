alter table public.lot
ALTER COLUMN original_price TYPE NUMERIC(19, 4),
ALTER COLUMN original_min_price TYPE NUMERIC(19, 4);

alter table public.bet
    ALTER COLUMN amount TYPE NUMERIC(19, 4);

alter table public.currency_rates
    ALTER COLUMN rate TYPE NUMERIC(19, 4);
