alter table public.users
    alter column email_verified set default false;

alter table public.users
    add column time_zone varchar(30);

alter table public.users
    alter column creation_date type timestamptz;
alter table public.lot
    alter column expiration_date type timestamptz;
alter table public.lot
    alter column creation_date type timestamptz;

alter table public.users
    drop column password;

alter table public.users
    drop column uuid;

alter table public.users
    drop column phone_number;

truncate table public.users cascade;

alter table public.users
    drop column id cascade;

alter table public.users
    add column id uuid primary key default null;

truncate public.lot cascade;

truncate public.bet cascade;

alter table public.lot
    drop column user_id;

alter table public.lot
    add column user_id uuid references public.users (id);

alter table public.bet
    drop column user_id;

alter table public.bet
    add column user_id uuid references public.users (id);

insert into public.users(creation_date, email, email_verified, username, time_zone, id)
values ('2024-02-29 07:50:36.032127 +00:00', 'dovgvillojegor@gmail.com', true, 'Dovgvillo Egor Vyacheslav',
        'Europe/London', '64a844f8-7081-7050-7d86-ecd1b1dee86f'),
       ('2024-02-29 07:50:36.042000 +00:00', 'filimonchik1998@mail.ru', true, 'Dovgvillo Egor Vyacheslavovic',
        'Europe/London', '44b8a488-0001-7040-7c17-e7436cf69e5e');
