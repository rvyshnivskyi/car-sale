DROP DATABASE IF EXISTS car_sales_db_test;
CREATE DATABASE car_sales_db_test;
USE car_sales_db_test;

DROP TABLE IF EXISTS offer;
DROP TABLE IF EXISTS sale_proposition;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS person;

CREATE TABLE person (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(20) NOT NULL,
  last_name VARCHAR(20),
  city VARCHAR(25),
  phone_number VARCHAR(13) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE car (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  owner_id INT UNSIGNED,
  plate_number VARCHAR(10) NOT NULL,
  brand VARCHAR(25) NOT NULL,
  year INT NOT NULL,
  color VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (owner_id) REFERENCES person(id),
  UNIQUE (plate_number)
);

CREATE TABLE sale_proposition (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  car_id INT UNSIGNED,
  price DOUBLE NOT NULL,
  status ENUM ('OPEN', 'CLOSED') NOT NULL DEFAULT 'OPEN',
  PRIMARY KEY (id),
  FOREIGN KEY (car_id) REFERENCES car(id)
);

CREATE TABLE offer (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  sale_proposition_id INT UNSIGNED NOT NULL,
  buyer_id INT UNSIGNED NOT NULL,
  date DATE NOT NULL,
  price DOUBLE NOT NULL,
  status ENUM ('ACTIVE', 'ACCEPTED', 'DECLINED') NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (id),
  FOREIGN KEY (sale_proposition_id) REFERENCES sale_proposition(id),
  FOREIGN KEY (buyer_id) REFERENCES person(id)
);