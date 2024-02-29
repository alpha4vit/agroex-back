alter table public.users
    add column enabled boolean default true;

update public.users set enabled = true where id='64a844f8-7081-7050-7d86-ecd1b1dee86f' or id='44b8a488-0001-7040-7c17-e7436cf69e5e';
