CREATE TABLE generated_dokument_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  filename           VARCHAR(255),
  filepfad           VARCHAR(255),
  filesize           VARCHAR(255),
  typ                VARCHAR(255),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE generated_dokument (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  filename           VARCHAR(255) NOT NULL,
  filepfad           VARCHAR(255) NOT NULL,
  filesize           VARCHAR(255) NOT NULL,
  typ                VARCHAR(255) NOT NULL,
  gesuch_id          VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE generated_dokument_aud
  ADD CONSTRAINT FK_generated_dokument_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE generated_dokument
  ADD CONSTRAINT FK_generated_dokument_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);
