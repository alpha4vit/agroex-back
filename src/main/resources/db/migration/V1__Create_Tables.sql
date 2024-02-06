create table if not exists country
(
    id   bigserial
        primary key,
    name varchar(255)
);

alter table country
    owner to postgres;

create table if not exists location
(
    id         bigserial
        primary key,
    latitude   varchar(255),
    longitude  varchar(255),
    region     varchar(255),
    country_id bigint
        constraint fk_location_country
            references country
);

alter table location
    owner to postgres;

create table if not exists product_category
(
    id        bigserial
        primary key,
    parent_id bigint,
    title     varchar(255)
);

alter table product_category
    owner to postgres;

create table if not exists tags
(
    id    bigserial
        primary key,
    title varchar(255)
);

alter table tags
    owner to postgres;

create table if not exists users
(
    id             bigserial
        primary key,
    creation_date  timestamp(6) with time zone,
    email          varchar(255),
    email_verified boolean,
    password       varchar(255),
    phone_number   varchar(255),
    username       varchar(255)
);

alter table users
    owner to postgres;

create table if not exists lot
(
    id                  bigserial
        primary key,
    creation_date       timestamp(6) with time zone,
    currency            varchar(255),
    description         varchar(255),
    enabled_by_admin    boolean,
    expiration_date     timestamp(6) with time zone,
    lot_type            varchar(255),
    packaging           varchar(255),
    price_per_ton       real,
    quantity            integer,
    size                varchar(255),
    title               varchar(255),
    variety             varchar(255),
    location_id         bigint
        constraint fk_lot_location
            references location,
    product_category_id bigint
        constraint fk_lot_product_category
            references product_category,
    user_id             bigint
        constraint fk_lot_user
            references users
);

alter table lot
    owner to postgres;

create table if not exists lot_images
(
    id        bigserial
        primary key,
    name      varchar(255),
    lot_id    bigint
        constraint fk_lot_images_lot
            references lot
);

alter table lot_images
    owner to postgres;

create table if not exists lot_tag
(
    lot_id bigint not null
        constraint fk_lot_tag_lot
            references lot,
    tag_id bigint not null
        constraint fk_lot_tag_tag
            references tags,
    constraint uk_lot_tag
        unique (lot_id, tag_id)
);

alter table lot_tag
    owner to postgres;