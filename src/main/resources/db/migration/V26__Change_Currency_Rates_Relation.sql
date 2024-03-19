create table public.lot_currency_rates
(
    lot_currency      bigint not null,
    currency_rates_id bigint not null,
    constraint fk_lot_currency_lot_currency foreign key (lot_currency) references public.lot,
    constraint fk_lot_currency_rate_id foreign key (currency_rates_id) references public.currency_rates,
    unique (lot_currency, currency_rates_id)
);
