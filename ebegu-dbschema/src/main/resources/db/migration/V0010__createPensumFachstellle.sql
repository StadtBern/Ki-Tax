CREATE TABLE pensum_fachstelle (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  pensum             INTEGER,
  fachstelle_id      VARCHAR(36) NOT NULL,
  CONSTRAINT PK_pensum_fachstelle PRIMARY KEY (id)
);

CREATE TABLE pensum_fachstelle_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  pensum             INTEGER,
  fachstelle_id      VARCHAR(36),
  CONSTRAINT PK_pensum_fachstelle_aud PRIMARY KEY (id, rev)
);

ALTER TABLE pensum_fachstelle
  ADD CONSTRAINT FK_pensum_fachstelle_fachstelle_id
FOREIGN KEY (fachstelle_id)
REFERENCES fachstelle (id);

ALTER TABLE pensum_fachstelle_aud
  ADD CONSTRAINT FK_pensum_fachstelle_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE kind
  ADD CONSTRAINT FK_kind_pensum_fachstelle_id
FOREIGN KEY (pensum_fachstelle_id)
REFERENCES pensum_fachstelle (id);
