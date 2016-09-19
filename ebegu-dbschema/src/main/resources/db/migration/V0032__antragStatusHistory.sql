CREATE TABLE antrag_status_history_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  datum              DATETIME,
  status             VARCHAR(255),
  benutzer_id        VARCHAR(36),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE antrag_status_history (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  datum              DATETIME     NOT NULL,
  status             VARCHAR(255) NOT NULL,
  benutzer_id        VARCHAR(36)  NOT NULL,
  gesuch_id          VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE antrag_status_history_aud
  ADD CONSTRAINT FK_antrag_status_history_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE antrag_status_history
  ADD CONSTRAINT FK_antragstatus_history_benutzer_id
FOREIGN KEY (benutzer_id)
REFERENCES benutzer (id);

ALTER TABLE antrag_status_history
  ADD CONSTRAINT FK_antragstatus_history_antrag_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);
