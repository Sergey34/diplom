/* пользователи */
CREATE  TABLE users (
  id       INT         NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL UNIQUE,
  password VARCHAR(45) NOT NULL ,
  enabled  TINYINT     NOT NULL DEFAULT 1,
  PRIMARY KEY (id));

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

INSERT INTO users(username,password,enabled)
VALUES ('mkyong','123456', true);
INSERT INTO users(username,password,enabled)
VALUES ('alex','123456', true);

INSERT INTO roles ( role)
VALUES ('ROLE_USER');
INSERT INTO roles ( role)
VALUES ('ROLE_ADMIN');


INSERT INTO user_role (id_user, id_role)
  VALUE (1, 1);
INSERT INTO user_role (id_user, id_role)
  VALUE (2, 1);
INSERT INTO user_role (id_user, id_role)
  VALUE (2, 2);

SELECT username, role
FROM user_role, roles, users
WHERE
  users.id=2 AND
  user_role.id_role=roles.role_id AND
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
  id_prefix INT PRIMARY KEY AUTO_INCREMENT,
  prefix    CHAR UNIQUE
);

CREATE TABLE airfoil (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(255) UNIQUE,
  description VARCHAR(255),
  image       VARCHAR(60),
  prefix      INT,
  FOREIGN KEY (prefix) REFERENCES prefix (id_prefix)
);

CREATE TABLE links (
  id   INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  link VARCHAR(255) UNIQUE,
  PRIMARY KEY (id)
);

CREATE TABLE airfoil_links (
  id_airfoil INT,
  id_links   INT UNIQUE,
  FOREIGN KEY (id_airfoil) REFERENCES airfoil (id),
  FOREIGN KEY (id_links) REFERENCES links (id)
);
