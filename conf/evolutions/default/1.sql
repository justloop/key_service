# databases initialization

# --- !Ups
CREATE TABLE skeys (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  key VARCHAR(255) NOT NULL,
  inserttime DATETIME NOT NULL
);

CREATE TABLE apikeys (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  apikey VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL
);

CREATE TABLE apitokens (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  token VARCHAR(255) NOT NULL,
  apikey VARCHAR(255) NOT NULL,
  expirationtime DATETIME NOT NULL,
  userid INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE apilogs (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  dateinsert DATETIME NOT NULL,
  ip VARCHAR(255) NOT NULL,
  apikey VARCHAR(255),
  token VARCHAR(255),
  method VARCHAR(255) NOT NULL,
  uri VARCHAR(255) NOT NULL,
  requestbody VARCHAR(255),
  responsestatus INTEGER NOT NULL,
  responsebody VARCHAR(255)
);

CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  emailconfirmed BOOLEAN NOT NULL,
  active BOOLEAN NOT NULL
);

INSERT INTO users (email,password,name,emailconfirmed,active) VALUES ('xxx@xxx.com', 'VL7Wu49+6q6kqKhC+f8c+Q==', 'SmarthubOps', 1, 1);
INSERT INTO apikeys (apikey,name,active) VALUES ('XXX','XXX',1;


# --- !Downs

DROP TABLE users;
DROP TABLE apilogs;
DROP TABLE apikeys;
DROP TABLE apitokens;
DROP TABLE skeys;
