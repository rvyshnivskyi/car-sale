INSERT INTO person(first_name, last_name, city, phone_number) VALUES ("Roma", "Vysh", "Kyiv", "123456");
INSERT INTO person(first_name, last_name, city, phone_number) VALUES ("Tania", "Turchik", "Ternopil", "123456");
INSERT INTO person(first_name, last_name, city, phone_number) VALUES ("Ira", "Diakon", "Kyiv", "123456");
INSERT INTO person(first_name, last_name, city, phone_number) VALUES ("Vova", "Mamatov", "Khmelnitskii", "123456");
INSERT INTO person(first_name, last_name, city, phone_number) VALUES ("Denis", NULL , "Khmelnitskii", "123456");
SELECT * FROM person WHERE id IS NOT NULL;

INSERT INTO car(owner_id, plate_number, brand, year, color) VALUES (1, 'AA3295BB', 'BMW', 2001, 'red');
INSERT INTO car(owner_id, plate_number, brand, year, color) VALUES (1, 'AA3296BB', 'Audi', 2003, 'yellow');
INSERT INTO car(owner_id, plate_number, brand, year, color) VALUES (2, 'AA3297BB', 'Ford', 1995, 'blue');
INSERT INTO car(owner_id, plate_number, brand, year, color) VALUES (2, 'AA3298BB', 'Opel', 2016, 'green');
INSERT INTO car(owner_id, plate_number, brand, year, color) VALUES (3, 'AA3299BB', 'Opel', 2000, 'green');
SELECT * FROM car WHERE id IS NOT NULL;


INSERT INTO sale_proposition(car_id, price) VALUES (1, 2000.4);
INSERT INTO sale_proposition(car_id, price) VALUES (2, 60000);
INSERT INTO sale_proposition(car_id, price) VALUES (3, 7000);
SELECT * FROM sale_proposition WHERE id IS NOT NULL;

INSERT INTO offer(sale_proposition_id, buyer_id, date, price) VALUES (1, 3, '2017-12-31', 1800.1);
INSERT INTO offer(sale_proposition_id, buyer_id, date, price) VALUES (1, 4, '2013-11-22', 1750.5);
INSERT INTO offer(sale_proposition_id, buyer_id, date, price) VALUES (2, 5, '2000-10-01', 61000);
INSERT INTO offer(sale_proposition_id, buyer_id, date, price) VALUES (1, 5, CURRENT_DATE, 1999);
SELECT * FROM offer WHERE id IS NOT NULL;