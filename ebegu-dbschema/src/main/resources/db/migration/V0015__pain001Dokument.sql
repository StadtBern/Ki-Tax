CREATE TABLE pain001dokument_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  filename           VARCHAR(255),
  filepfad           VARCHAR(255),
  filesize           VARCHAR(255),
  typ                VARCHAR(255),
  zahlungsauftrag_id VARCHAR(36),
  write_protected    BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE pain001dokument (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  filename           VARCHAR(255) NOT NULL,
  filepfad           VARCHAR(255) NOT NULL,
  filesize           VARCHAR(255) NOT NULL,
  typ                VARCHAR(255) NOT NULL,
  zahlungsauftrag_id VARCHAR(36)  NOT NULL,
  write_protected    BIT          NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE pain001dokument_aud
  ADD CONSTRAINT FK_pain001dokument_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE pain001dokument
  ADD CONSTRAINT FK_pain001dokument_zahlungsauftrag_id
FOREIGN KEY (zahlungsauftrag_id)
REFERENCES zahlungsauftrag (id);
