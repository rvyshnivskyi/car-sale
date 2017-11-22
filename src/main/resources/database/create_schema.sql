DROP DATABASE car_sales_db;
CREATE DATABASE car_sales_db;
USE car_sales_db;

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
  FOREIGN KEY (owner_id) REFERENCES person(id)
);

DELIMITER $$
CREATE TRIGGER tr_unique_car_plate_number_on_insert
BEFORE INSERT ON car
FOR EACH ROW
  BEGIN
    IF (SELECT count(*) FROM car WHERE plate_number = NEW.plate_number) > 0
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'car.plate_number\' should be unique';
    END IF;
  END
$$
CREATE TRIGGER tr_unique_car_plate_number_on_update
AFTER UPDATE ON car
FOR EACH ROW
  BEGIN
    IF NEW.plate_number != OLD.plate_number
    AND (SELECT count(*) FROM car WHERE plate_number = NEW.plate_number) > 1
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'car.plate_number\' should be unique';
    END IF;
  END
$$

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

DELIMITER $$
CREATE TRIGGER tr_sale_proposition_price_equals_or_less_then_zero_on_insert
BEFORE INSERT ON sale_proposition
FOR EACH ROW
  BEGIN
    IF NEW.price <= 0
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'sale_proposition.price\' should be positive';
    END IF;
  END
$$
CREATE TRIGGER tr_sale_proposition_price_equals_or_less_then_zero_on_update
AFTER UPDATE ON sale_proposition
FOR EACH ROW
  BEGIN
    IF NEW.price <= 0
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'sale_proposition.price\' should be positive';
    END IF;
  END
$$
CREATE TRIGGER tr_sale_proposition_only_one_item_per_car_on_insert
BEFORE INSERT ON sale_proposition
FOR EACH ROW
  BEGIN
    IF (SELECT count(*) FROM sale_proposition WHERE status = 'Open' AND car_id = NEW.car_id) > 0
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Can\'t be more then one sale_proposition with \'sale_proposition.status\'=\'Open\' per each \'sale_proposition.car_id\'';
    END IF;
  END
$$
CREATE TRIGGER tr_sale_proposition_only_one_item_per_car_on_update
AFTER UPDATE ON sale_proposition
FOR EACH ROW
  BEGIN
    IF  NEW.status = 'Open'
    AND NEW.status != OLD.status
    AND (SELECT count(*) FROM sale_proposition WHERE status = 'Open' AND car_id = NEW.car_id) > 1
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Can\'t be more then one sale_proposition with \'sale_proposition.status\'=\'Open\' per each \'sale_proposition.car_id\'';
    END IF;
  END
$$
CREATE TRIGGER tr_sale_proposition_unmodified_car_id_on_update
AFTER UPDATE ON sale_proposition
FOR EACH ROW
  BEGIN
    IF NEW.car_id != OLD.car_id
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'sale_proposition.car_id\' can\'t be modified';
    END IF;
  END
$$
DELIMITER ;

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

DELIMITER $$
CREATE TRIGGER tr_offer_price_equals_or_less_then_zero_on_insert
BEFORE INSERT ON offer
FOR EACH ROW
  BEGIN
    IF NEW.price <= 0
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'offer.price\' should be positive';
    END IF;
  END
$$
CREATE TRIGGER tr_offer_price_equals_or_less_then_zero_on_update
AFTER UPDATE ON offer
FOR EACH ROW
  BEGIN
    IF NEW.price <= 0
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'offer.price\' should be positive';
    END IF;
  END
$$
CREATE TRIGGER tr_offer_status_not_accepted_on_insert
BEFORE INSERT ON offer
FOR EACH ROW
  BEGIN
    IF NEW.status IN ('Accepted', 'Declined')
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = '\'offer.status\' of new offer can\'t be \'Accepted\' or \'Declined\'';
    END IF;
  END
$$
CREATE TRIGGER tr_offer_only_one_accepted_offer_per_sale_proposition_on_update
AFTER UPDATE ON offer
FOR EACH ROW
  BEGIN
    IF NEW.status = 'Accepted'
    AND NEW.status != OLD.status
    AND (SELECT count(*) FROM offer WHERE status = 'Accepted' AND sale_proposition_id = NEW.sale_proposition_id) > 1
    THEN SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Can\'t be more then one offer with \'offer.status\'=\'Accepted\' per each \'offer.sale_proposition_id\'';
    END IF;
  END
$$
CREATE TRIGGER tr_update_sale_proposition_if_offer_accepted_on_update
AFTER UPDATE ON offer
FOR EACH ROW
  BEGIN
    IF NEW.status = 'Accepted'
       AND NEW.status != OLD.status
      THEN
        UPDATE sale_proposition SET status = 'Closed' WHERE sale_proposition.id = NEW.sale_proposition_id;
    END IF;
  END
$$
CREATE TRIGGER tr_update_car_if_offer_accepted_on_update
AFTER UPDATE ON offer
FOR EACH ROW
  BEGIN
    IF NEW.status = 'Accepted'
       AND NEW.status != OLD.status
      THEN
        UPDATE car SET owner_id = NEW.buyer_id WHERE car.id = (SELECT car_id FROM sale_proposition WHERE sale_proposition.id = NEW.sale_proposition_id);
    END IF;
  END
$$
DELIMITER ;