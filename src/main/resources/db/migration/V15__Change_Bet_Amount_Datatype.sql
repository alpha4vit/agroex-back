ALTER TABLE bet
    ALTER COLUMN amount TYPE real;
ALTER TABLE lot
    ALTER COLUMN min_price TYPE REAL USING min_price::REAL;
