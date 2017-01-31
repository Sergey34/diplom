/* пользователи */
CREATE TABLE users (
  id       INT         NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL UNIQUE,
  password VARCHAR(256) NOT NULL,
  enabled  TINYINT     NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
);

CREATE TABLE roles (
  role_id INT         NOT NULL AUTO_INCREMENT,
  role    VARCHAR(45) NOT NULL UNIQUE,
  PRIMARY KEY (role_id)
);

CREATE TABLE user_role (
  id_user INT,
  id_role INT,
  CONSTRAINT FOREIGN KEY (id_user) REFERENCES users (id),
  CONSTRAINT FOREIGN KEY (id_role) REFERENCES roles (role_id)
);

INSERT INTO users (username, password, enabled)
VALUES ('mkyong', '123456', TRUE);
INSERT INTO users (username, password, enabled)
VALUES ('alex', '123456', TRUE);

INSERT INTO roles (role)
VALUES ('ROLE_USER');
INSERT INTO roles (role)
VALUES ('ROLE_ADMIN');


INSERT INTO user_role (id_user, id_role)
  VALUE (1, 1);
INSERT INTO user_role (id_user, id_role)
  VALUE (2, 1);
INSERT INTO user_role (id_user, id_role)
  VALUE (2, 2);

SELECT
  username,
  role
FROM user_role, roles, users
WHERE
  users.id = 2 AND
  user_role.id_role = roles.role_id AND
  user_role.id_user = users.id;

/* меню */
CREATE TABLE menuHeader (
  id     INT          NOT NULL AUTO_INCREMENT,
  header VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);

CREATE TABLE menuItem (
  id   INT          NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  url  VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);

CREATE TABLE menuHeader_menuItem (
  headerId INT,
  ItemId   INT,
  CONSTRAINT FOREIGN KEY (headerId) REFERENCES menuHeader (id),
  CONSTRAINT FOREIGN KEY (ItemId) REFERENCES menuItem (id)
);


/* Airfoils */

CREATE TABLE prefix (
  id_prefix INT PRIMARY KEY,
  prefix    CHAR
);

CREATE TABLE airfoil (
  shortName   VARCHAR(60) PRIMARY KEY,
  name        VARCHAR(255) UNIQUE,
  description TEXT,
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

SELECT count(*)
FROM airfoil;

SELECT
  table_schema                                  "database_name",
  sum(data_length + index_length) / 1024 / 1024 "Data Base Size in MB"
FROM information_schema.TABLES
GROUP BY table_schema;


