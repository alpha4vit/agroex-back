alter table public.users add column uuid uuid;

update public.users set uuid='24ccd094-646f-47db-b991-5408445e7847', creation_date='2024-02-16T01:37:01Z', password='Password_123' where id=1;
update public.users set uuid='b3fe2077-9ab9-45f6-b592-8fae3841ce24', creation_date='2024-02-16T01:37:01Z', password='Password_123' where id=2;

