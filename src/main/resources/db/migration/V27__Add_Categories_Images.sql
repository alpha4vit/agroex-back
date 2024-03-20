ALTER TABLE product_category ADD COLUMN image varchar(255);

ALTER TABLE product_category ADD CONSTRAINT unique_title UNIQUE (title);