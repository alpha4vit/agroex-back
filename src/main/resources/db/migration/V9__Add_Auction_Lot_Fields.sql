ALTER TABLE lot
    ADD COLUMN IF NOT EXISTS min_price varchar(255) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS admin_status varchar(255) DEFAULT 'approved',
    ADD COLUMN IF NOT EXISTS user_status varchar(255) DEFAULT 'active',
    ADD COLUMN IF NOT EXISTS status varchar(255) DEFAULT 'active',
    ADD COLUMN IF NOT EXISTS duration bigint DEFAULT 0;