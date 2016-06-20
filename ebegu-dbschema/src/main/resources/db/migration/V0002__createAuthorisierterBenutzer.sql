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
  benutzer_id        VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE benutzer (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  email              VARCHAR(255) NOT NULL,
  nachname           VARCHAR(255) NOT NULL,
  role               VARCHAR(255) NOT NULL,
  username           VARCHAR(255) NOT NULL,
  vorname            VARCHAR(255) NOT NULL,
  mandant_id         VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX IX_authorisierter_benutzer ON authorisierter_benutzer (benutzer_id);

CREATE INDEX IX_authorisierter_benutzer_token ON authorisierter_benutzer (auth_token, benutzer_id);

ALTER TABLE authorisierter_benutzer
  ADD CONSTRAINT FK_authorisierter_benutzer_benutzer_id
FOREIGN KEY (benutzer_id)
REFERENCES benutzer (id);

CREATE INDEX IX_benutzer_username_mandant ON benutzer (username, mandant_id);

ALTER TABLE benutzer
  ADD CONSTRAINT FK_benutzer_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);