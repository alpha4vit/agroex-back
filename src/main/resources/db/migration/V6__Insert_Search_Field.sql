CREATE OR REPLACE VIEW lot_search_view AS
SELECT
    lot.id,
    lower(lot.title) || ' ' || lower(lot.variety) || ' ' || lower(lot.description) || ' ' ||
    lower(product_category.title) AS search_string
FROM
    lot
        JOIN
    product_category ON lot.product_category_id = product_category.id;
