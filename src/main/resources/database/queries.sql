
--------------------------------------------------------------------------------
---    List of cars available on sale with the best deal proposed at the moment;
--------------------------------------------------------------------------------

SELECT c.id, c.plate_number, c.brand, c.year, c.color, MAX(o.price) FROM car c INNER JOIN sale_proposition s
ON c.id = s.car_id INNER JOIN offer o
ON s.id = o.sale_proposition_id
GROUP BY c.id, c.plate_number, c.brand, c.year, c.color;

--------------------------------------------------------------------------------
---    The most expensive sold car;
--------------------------------------------------------------------------------

SELECT c.id, c.plate_number, c.brand, c.year, c.color, o.price FROM car c INNER JOIN sale_proposition s
    ON c.id = s.car_id INNER JOIN offer o
    ON s.id = o.sale_proposition_id
  WHERE o.status = 'Accepted'
GROUP BY c.id, c.plate_number, c.brand, c.year, c.color, o.price
ORDER BY o.price DESC
LIMIT 1;

--------------------------------------------------------------------------------
---    The most popular year of sold cars;
--------------------------------------------------------------------------------


SELECT c.year, count(*) as count FROM car c INNER JOIN sale_proposition s
    ON c.id = s.car_id INNER JOIN offer o
    ON s.id = o.sale_proposition_id
WHERE o.status = 'Accepted'
GROUP BY c.year
ORDER BY count DESC
LIMIT 1;