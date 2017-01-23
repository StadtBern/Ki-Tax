CREATE TABLE mitteilung (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  empfaenger_typ     VARCHAR(255) NOT NULL,
  message            VARCHAR(255) NOT NULL,
  mitteilung_status  VARCHAR(255) NOT NULL,
  sender_typ         VARCHAR(255) NOT NULL,
  subject            VARCHAR(255) NOT NULL,
  empfaenger_id      VARCHAR(36),
  fall_id            VARCHAR(36),
  sender_id          VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mitteilung_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  empfaenger_typ     VARCHAR(255),
  message            VARCHAR(255),
  mitteilung_status  VARCHAR(255),
  sender_typ         VARCHAR(255),
  subject            VARCHAR(255),
  empfaenger_id      VARCHAR(36),
  fall_id            VARCHAR(36),
  sender_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

ALTER TABLE mitteilung
  ADD CONSTRAINT FK_Mitteilung_empfaenger
FOREIGN KEY (empfaenger_id)
REFERENCES benutzer (id);

ALTER TABLE mitteilung
  ADD CONSTRAINT FK_mitteilung_fall_id
FOREIGN KEY (fall_id)
REFERENCES fall (id);

ALTER TABLE mitteilung
  ADD CONSTRAINT FK_Mitteilung_sender
FOREIGN KEY (sender_id)
REFERENCES benutzer (id);

ALTER TABLE mitteilung_aud
  ADD CONSTRAINT FK_Mitteilung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev)