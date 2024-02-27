ALTER TABLE lot
    ADD COLUMN IF NOT EXISTS admin_comment varchar(255) DEFAULT '';

ALTER TABLE lot
    RENAME COLUMN admin_status TO inner_status;