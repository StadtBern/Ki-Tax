CREATE TABLE authorisierter_benutzer (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  auth_token         VARCHAR(255),
  first_login        DATETIME    NOT NULL,
  last_login         DATETIME    NOT NULL,
  password           VARCHAR(255),
  username           VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE INDEX IX_authorisierter_benutzer
  ON authorisierter_benutzer (auth_token, username);
