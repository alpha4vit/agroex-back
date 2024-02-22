CREATE TABLE IF NOT EXISTS currency_rates(
    id BIGSERIAL PRIMARY KEY,
    source_currency varchar(10),
    target_currency varchar(10),
    rate decimal,
    updated_at timestamp,
    constraint unique_currency_combination
                                    unique (source_currency, target_currency)
);

insert into public.currency_rates
    (source_currency, target_currency, rate)
        VALUES
    ('USD', 'BYN', 3.234),
    ('BYN', 'USD', 0.305955),
    ('BYN', 'EUR', 0.283105),
    ('BYN', 'RUB', 28.254879),
    ('EUR', 'USD', 1.080718),
    ('USD', 'EUR', 0.92531),
    ('USD', 'RUB', 92.349732),
    ('EUR', 'RUB', 99.792946),
    ('RUB', 'EUR', 0.010021),
    ('RUB', 'USD', 0.010828),
    ('RUB', 'BYN', 0.035392);

alter table public.lot rename column currency to original_currency;
alter table public.lot rename column price to original_price;



