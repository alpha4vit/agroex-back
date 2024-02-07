ALTER TABLE lot
    RENAME COLUMN price_per_ton TO price;
ALTER TABLE lot
    ALTER COLUMN quantity TYPE real;

