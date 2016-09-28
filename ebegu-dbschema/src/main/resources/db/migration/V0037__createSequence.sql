CREATE TABLE sequence (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  current_value      BIGINT       NOT NULL,
  sequence_type      VARCHAR(100) NOT NULL,
  mandant_id         VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE sequence_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  current_value      BIGINT,
  sequence_type      VARCHAR(100),
  mandant_id         VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE INDEX sequence_ix1 ON sequence (mandant_id);

ALTER TABLE sequence
  ADD CONSTRAINT UK_sequence UNIQUE (sequence_type, mandant_id);

ALTER TABLE sequence
  ADD CONSTRAINT FK_sequence_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);

ALTER TABLE sequence_aud
  ADD CONSTRAINT FK_sequence_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE fall MODIFY fall_nummer BIGINT NOT NULL;
ALTER TABLE fall_aud MODIFY fall_nummer BIGINT;

ALTER TABLE fall
  ADD mandant_id VARCHAR(36) NULL;
ALTER TABLE fall_aud
  ADD mandant_id VARCHAR(36) NULL;

UPDATE fall
SET mandant_id = 'e3736eb8-6eef-40ef-9e52-96ab48d8f220';

CREATE INDEX IX_fall_mandant ON fall (mandant_id);

ALTER TABLE fall
  ADD CONSTRAINT FK_fall_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);
