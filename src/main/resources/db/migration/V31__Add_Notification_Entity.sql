create table public.notification
(
    id          uuid not null
        primary key,
    type      varchar(255),
    message     varchar(255),
    read_status varchar(255) default 'unread',
    title   varchar(255),
    role   varchar(255),
    send_time   timestamp(6) with time zone,
    lot_id      bigint
        constraint fk_notification_lot
            references public.lot,
    user_id     uuid
        constraint fk_notification_user
            references public.users
);

alter table public.notification
    owner to postgres;