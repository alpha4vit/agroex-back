alter table public.lot drop column lot_id;

delete
from public.product_category
where parent_id = 10;
delete
from public.product_category
where id = 10;

update public.product_category
set parent_id=7
where id = 12;
update public.product_category
set parent_id=8
where id = 11;

insert into public.product_category(parent_id, title)
values (8, 'Carrots'),
       (8, 'Potatoes'),
       (7, 'Peaches'),
       (7, 'Oranges');

insert into public.lot (creation_date,
                        original_currency,
                        description,
                        enabled_by_admin,
                        expiration_date,
                        lot_type,
                        packaging,
                        original_price,
                        quantity,
                        size,
                        title,
                        variety,
                        location_id,
                        product_category_id,
                        user_id)
VALUES ('2024-02-01T22:47:36.828703Z',
        'USD',
        'Incredible taste and smell',
        TRUE,
        '2024-04-02T22:47:36.828703Z',
        'sell',
        'bins',
        1000,
        13,
        '70+',
        'apples',
        'antonovka',
        1,
        9,
        '44b8a488-0001-7040-7c17-e7436cf69e5e'),

       ('2024-02-01T22:47:36.828703Z',
        'EUR',
        'CUCUMBERS',
        TRUE,
        '2024-03-23T22:47:36.828703Z',
        'buy',
        'bins',
        1234,
        89,
        '50+',
        'сucumbers',
        'french',
        4,
        11,
        '64a844f8-7081-7050-7d86-ecd1b1dee86f'),

       ('2024-02-01T22:47:36.828703Z',
        'EUR',
        'Carrots',
        TRUE,
        '2024-03-21T17:47:36.828703Z',
        'sell',
        'bins',
        1234,
        13,
        '50+',
        'carrots',
        'english',
        3,
        (select id from public.product_category where product_category.title = 'Carrots'),
        '64a844f8-7081-7050-7d86-ecd1b1dee86f'),

       ('2024-02-01T22:47:36.828703Z',
        'BYN',
        'Potatoes',
        TRUE,
        '2024-03-12T10:47:36.828703Z',
        'auctionSell',
        'bins',
        500,
        28,
        '100+',
        'potatoes',
        'english',
        6,
        (select id from public.product_category where product_category.title = 'Potatoes'),
        '64a844f8-7081-7050-7d86-ecd1b1dee86f'),

       ('2024-02-01T22:47:36.828703Z',
        'USD',
        'Oranges',
        TRUE,
        '2024-03-12T10:47:36.828703Z',
        'auctionSell',
        'bins',
        799,
        45,
        '100+',
        'oranges',
        'english',
        5,
        (select id from public.product_category where product_category.title = 'Oranges'),
        '44b8a488-0001-7040-7c17-e7436cf69e5e'),

       ('2024-02-01T22:47:36.828703Z',
        'EUR',
        'Peaches',
        TRUE,
        '2024-03-12T10:47:36.828703Z',
        'buy',
        'bins',
        999,
        49,
        '30+',
        'peaches',
        'english',
        2,
        (select id from public.product_category where product_category.title = 'Peaches'),
        '44b8a488-0001-7040-7c17-e7436cf69e5e');



