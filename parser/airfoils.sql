/* Airfoils */

CREATE TABLE prefix (
  id_prefix INT PRIMARY KEY AUTO_INCREMENT,
  prefix    CHAR UNIQUE
);

CREATE TABLE airfoil (
  shortName   VARCHAR(60) PRIMARY KEY,
  name        VARCHAR(255) UNIQUE,
  description VARCHAR(255),
  prefix      INT,
  coord       TEXT,
  FOREIGN KEY (prefix) REFERENCES prefix (id_prefix)
);

CREATE TABLE coordinates (
  id              INT NOT NULL PRIMARY KEY,
  coordinatesJson TEXT,
  fileName        VARCHAR(255),
  renolgs         VARCHAR(25),
  nCrit           INT(11),
  maxClCd         VARCHAR(30)
);

CREATE TABLE airfoil_coordinates (
  id_airfoil     VARCHAR(60),
  id_coordinates INT UNIQUE,
  FOREIGN KEY (id_airfoil) REFERENCES airfoil (shortName),
  FOREIGN KEY (id_coordinates) REFERENCES coordinates (id)
);