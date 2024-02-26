truncate public.product_category cascade;

alter table public.product_category
    add constraint parent_fk
        foreign key (parent_id)
            references public.product_category (id);

insert into public.product_category (id, title)
VALUES (0, 'ROOT_CATEGORY');
insert into public.product_category (parent_id, title)
VALUES (0, 'Fruits');
insert into public.product_category (parent_id, title)
VALUES (0, 'Vegetables');
insert into public.product_category (parent_id, title)
VALUES ((select id from public.product_category where title = 'Fruits'), 'Apples');
insert into public.product_category (parent_id, title)
VALUES ((select id from public.product_category where title = 'Fruits'), 'Citrus');
insert into public.product_category (parent_id, title)
VALUES ((select id from public.product_category where title = 'Fruits'), 'Cucumbers');
insert into public.product_category (parent_id, title)
VALUES ((select id from public.product_category where title = 'Citrus'), 'Oranges');