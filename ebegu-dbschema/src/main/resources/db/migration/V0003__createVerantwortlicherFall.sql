CREATE TABLE benutzer_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  email              VARCHAR(255),
  nachname           VARCHAR(255),
  role               VARCHAR(255),
  username           VARCHAR(255),
  vorname            VARCHAR(255),
  mandant_id         VARCHAR(36),
  PRIMARY KEY (id, rev)
);

ALTER TABLE benutzer_aud
  ADD CONSTRAINT FK_benutzer_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE fall ADD verantwortlicher_id VARCHAR(36);

ALTER TABLE fall_aud ADD verantwortlicher_id VARCHAR(36);

ALTER TABLE fall
  ADD CONSTRAINT FK_fall_verantwortlicher_id
FOREIGN KEY (verantwortlicher_id)
REFERENCES benutzer (id);
