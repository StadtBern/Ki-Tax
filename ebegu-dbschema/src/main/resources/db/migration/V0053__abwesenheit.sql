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
  PRIMARY KEY (id, rev)
);

CREATE TABLE abwesenheit_container_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  abwesenheitgs_id   VARCHAR(36),
  abwesenheitja_id   VARCHAR(36),
  betreuung_id       VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE abwesenheit_container (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  abwesenheitgs_id   VARCHAR(36),
  abwesenheitja_id   VARCHAR(36),
  betreuung_id       VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE abwesenheit_aud
  ADD CONSTRAINT FK_abwesenheit_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE abwesenheit_container_aud
  ADD CONSTRAINT FK_abwesenheit_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE abwesenheit_container
  ADD CONSTRAINT FK_abwesenheit_container_abwesenheit_gs
FOREIGN KEY (abwesenheitgs_id)
REFERENCES abwesenheit (id);

ALTER TABLE abwesenheit_container
  ADD CONSTRAINT FK_abwesenheit_container_abwesenheit_ja
FOREIGN KEY (abwesenheitja_id)
REFERENCES abwesenheit (id);

ALTER TABLE abwesenheit_container
  ADD CONSTRAINT FK_abwesenheit_container_betreuung_id
FOREIGN KEY (betreuung_id)
REFERENCES betreuung (id);