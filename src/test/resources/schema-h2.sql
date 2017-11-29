DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS sale_proposition;
DROP TABLE IF EXISTS offer;

###################################
###    Table person
###################################

CREATE TABLE person (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(20) NOT NULL,
  last_name VARCHAR(20),
  city VARCHAR(25),
  phone_number VARCHAR(13) NOT NULL,
  PRIMARY KEY (id)
);

###################################
###    Table car
###################################

CREATE TABLE car (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  owner_id INT UNSIGNED,
  plate_number VARCHAR(10) NOT NULL,
  brand VARCHAR(25) NOT NULL,
  year YEAR NOT NULL,
  color VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (owner_id) REFERENCES person(id),
  UNIQUE (plate_number)
);

###################################
###    Table sale_proposition
###################################

CREATE TABLE sale_proposition (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  car_id INT UNSIGNED,
  price DOUBLE NOT NULL,
  status ENUM ('Open', 'Closed') DEFAULT 'Open',
  PRIMARY KEY (id),
  FOREIGN KEY (car_id) REFERENCES car(id)
);

###################################
###    Table offer
###################################

CREATE TABLE offer (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  sale_proposition_id INT UNSIGNED NOT NULL,
  buyer_id INT UNSIGNED NOT NULL,
  date DATE NOT NULL,
  price DOUBLE NOT NULL,
  status ENUM ('Active', 'Accepted', 'Declined') DEFAULT 'Active',
  PRIMARY KEY (id),
  FOREIGN KEY (sale_proposition_id) REFERENCES sale_proposition(id),
  FOREIGN KEY (buyer_id) REFERENCES person(id)
);