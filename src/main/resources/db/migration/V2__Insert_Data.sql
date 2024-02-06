insert into public.country (name)
VALUES ('United States');
insert into public.country (name)
VALUES ('United Kingdom');
insert into public.country (name)
VALUES ('Japan');

insert into public.location (latitude, longitude, region, country_id)
VALUES ('40.7128', '-74.0060', 'New York', 1);
insert into public.location (latitude, longitude, region, country_id)
VALUES ('34.0522', '-118.2437', 'Los Angeles', 1);
insert into public.location (latitude, longitude, region, country_id)
VALUES ('51.5074', '-0.1278', 'London', 2);
insert into public.location (latitude, longitude, region, country_id)
VALUES ('48.8566', '2.3522', 'Paris', 2);
insert into public.location (latitude, longitude, region, country_id)
VALUES ('35.6895', '139.6917', 'Tokyo', 3);
insert into public.location (latitude, longitude, region, country_id)
VALUES ('37.7749', '-122.4194', 'San Francisco', 3);

insert into public.product_category (parent_id, title)
VALUES (0, 'Fruits');
insert into public.product_category (parent_id, title)
VALUES (0, 'Vegetables');
insert into public.product_category (parent_id, title)
VALUES (1, 'Apples');
insert into public.product_category (parent_id, title)
VALUES (1, 'Citrus');
insert into public.product_category (parent_id, title)
VALUES (2, 'Cucumbers');
insert into public.product_category (parent_id, title)
VALUES (4, 'Oranges');



insert into public.users (creation_date, email, email_verified, password, phone_number, username)
VALUES ('2024-01-30T12:00:00Z', 'user1@example.com', true, 'password123', '+1234567890', 'user1');

insert into public.users (creation_date, email, email_verified, password, phone_number, username)
VALUES ('2024-01-31T12:00:00Z', 'user2@example.com', true, 'password456', '+9876543210', 'user2');

insert into public.lot (creation_date,
                        currency,
                        description,
                        enabled_by_admin,
                        expiration_date,
                        lot_type,
                        packaging,
                        price_per_ton,
                        quantity,
                        size,
                        title,
                        variety,
                        location_id,
                        product_category_id,
                        user_id)
VALUES ('2024-02-01T22:47:36.828703Z',
        'USD',
        'APPLES',
        TRUE,
        '2024-03-02T22:47:36.828703Z',
        'sell',
        'bins',
        1000,
        13,
        '70+',
        'apples',
        'antonovka',
        1,
        1,
        1);

insert into public.lot (creation_date,
                        currency,
                        description,
                        enabled_by_admin,
                        expiration_date,
                        lot_type,
                        packaging,
                        price_per_ton,
                        quantity,
                        size,
                        title,
                        variety,
                        location_id,
                        product_category_id,
                        user_id)
VALUES ('2024-02-01T22:47:36.828703Z',
        'USD',
        'ORANGES',
        TRUE,
        '2024-03-02T22:47:36.828703Z',
        'buy',
        'bins',
        1234,
        10,
        '50+',
        'oranges',
        'sicilian',
        2,
        4,
        1);

insert into public.tags (title)
VALUES ('Fresh');
insert into public.tags (title)
VALUES ('Tasty');
insert into public.tags (title)
VALUES ('Hot');
insert into public.tags (title)
VALUES ('Sale');


insert into public.lot_tag (lot_id, tag_id)
VALUES (1, 1);
insert into public.lot_tag (lot_id, tag_id)
VALUES (1, 2);

insert into public.lot_tag (lot_id, tag_id)
VALUES (2, 3);
insert into public.lot_tag (lot_id, tag_id)
VALUES (2, 4);