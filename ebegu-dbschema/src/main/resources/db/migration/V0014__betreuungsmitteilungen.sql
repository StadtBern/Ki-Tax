CREATE TABLE betreuungsmitteilung_pensum_aud (
  id                      VARCHAR(36) NOT NULL,
  rev                     INTEGER     NOT NULL,
  revtype                 TINYINT,
  timestamp_erstellt      DATETIME,
  timestamp_mutiert       DATETIME,
  user_erstellt           VARCHAR(36),
  user_mutiert            VARCHAR(36),
  vorgaenger_id           VARCHAR(36),
  gueltig_ab              DATE,
  gueltig_bis             DATE,
  pensum                  INTEGER,
  betreuungsmitteilung_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungsmitteilung_pensum (
  id                      VARCHAR(36) NOT NULL,
  timestamp_erstellt      DATETIME    NOT NULL,
  timestamp_mutiert       DATETIME    NOT NULL,
  user_erstellt           VARCHAR(36) NOT NULL,
  user_mutiert            VARCHAR(36) NOT NULL,
  version                 BIGINT      NOT NULL,
  vorgaenger_id           VARCHAR(36),
  gueltig_ab              DATE        NOT NULL,
  gueltig_bis             DATE        NOT NULL,
  pensum                  INTEGER     NOT NULL,
  betreuungsmitteilung_id VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE betreuungsmitteilung_pensum_aud
  ADD CONSTRAINT FK_betreuungsmitteilung_pensum_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungsmitteilung_pensum
  ADD CONSTRAINT FK_betreuungsmitteilung_pensum_betreuungsmitteilung_id
FOREIGN KEY (betreuungsmitteilung_id)
REFERENCES mitteilung (id);
