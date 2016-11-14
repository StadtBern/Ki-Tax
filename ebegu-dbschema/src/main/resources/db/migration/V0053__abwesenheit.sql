CREATE TABLE abwesenheit (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  betreuung_id       VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE abwesenheit_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  betreuung_id       VARCHAR(36),
  PRIMARY KEY (id, rev)
);

ALTER TABLE abwesenheit
  ADD CONSTRAINT FK_abwesenheit_betreuung_id
FOREIGN KEY (betreuung_id)
REFERENCES betreuung (id);

ALTER TABLE abwesenheit_aud
  ADD CONSTRAINT FK_abwesenheit_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);