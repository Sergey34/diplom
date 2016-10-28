
CREATE  TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(45) NOT NULL ,
  enabled TINYINT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (id));

CREATE TABLE roles (
  role_id INT NOT NULL AUTO_INCREMENT,
  role VARCHAR(45) NOT NULL,
  PRIMARY KEY (role_id)
);

CREATE TABLE user_role (
  id_user INT,
  id_role INT,
  FOREIGN KEY (id_user) REFERENCES users(id),
  FOREIGN KEY (id_role) REFERENCES roles(role_id)
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
    VALUE (1,1);
INSERT INTO user_role (id_user, id_role)
    VALUE (2,1);
INSERT INTO user_role (id_user, id_role)
    VALUE (2,2);

SELECT username, role
FROM user_role, roles, users
WHERE
  users.id=2 AND
  user_role.id_role=roles.role_id AND
  user_role.id_user = users.id;

CREATE TABLE links (
  id   INT NOT NULL AUTO_INCREMENT,
  name TEXT,
  link TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE menuHeader (
  id     INT NOT NULL AUTO_INCREMENT,
  header TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE menuItem (
  id   INT NOT NULL AUTO_INCREMENT,
  name TEXT,
  url  TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE menuHeader_menuItem (
  headerId INT,
  ItemId   INT,
  FOREIGN KEY (headerId) REFERENCES menuHeader (id),
  FOREIGN KEY (ItemId) REFERENCES menuItem (id)
)