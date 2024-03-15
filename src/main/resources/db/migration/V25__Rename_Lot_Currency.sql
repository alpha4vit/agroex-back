alter table public.lot
    rename column original_currency to currency;
alter table public.currency_rates
    rename column source_currency to currency