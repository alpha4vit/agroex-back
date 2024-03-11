create table if not exists user_role (
    user_id uuid references users(id),
    roles varchar(12)
);