# dummy parameter
INSERT INTO ebegu_parameter
VALUES (
  'a679e03e-58e2-446e-a7ee-bbadc87b7c90'
  , '2016-06-03 10:16:39'
  , '2016-06-03 10:16:39'
  , 'flyway'
  , 'flyway'
  , 0
  , '2017-01-01'
  , '2017-12-31'
  , 'PARAM_ABGELTUNG_PRO_TAG_KANTON'
  , '107.19'
);

ALTER TABLE betreuung
  ADD COLUMN verfuegung_id VARCHAR(36);

ALTER TABLE betreuung_aud
  ADD COLUMN verfuegung_id VARCHAR(36);
CREATE TABLE verfuegung (
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  generated_bemerkungen VARCHAR(1000),
  manuelle_bemerkungen  VARCHAR(1000),
  PRIMARY KEY (id)
);

CREATE TABLE verfuegung_aud (
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  generated_bemerkungen VARCHAR(1000),
  manuelle_bemerkungen  VARCHAR(1000),
  PRIMARY KEY (id, rev)
);

CREATE TABLE verfuegung_zeitabschnitt_aud (
  id                          VARCHAR(36) NOT NULL,
  rev                         INTEGER     NOT NULL,
  revtype                     TINYINT,
  timestamp_erstellt          DATETIME,
  timestamp_mutiert           DATETIME,
  user_erstellt               VARCHAR(36),
  user_mutiert                VARCHAR(36),
  gueltig_ab                  DATE,
  gueltig_bis                 DATE,
  abzug_fam_groesse           DECIMAL(19, 2),
  anspruchberechtigtes_pensum INTEGER,
  anspruchspensum_rest        INTEGER,
  bemerkungen                 VARCHAR(1000),
  betreuungspensum            INTEGER,
  betreuungsstunden           DECIMAL(19, 2),
  bg_pensum                   INTEGER,
  elternbeitrag               DECIMAL(19, 2),
  erwerbspensumgs1            INTEGER,
  erwerbspensumgs2            INTEGER,
  fachstellenpensum           INTEGER,
  massgebendes_einkommen      DECIMAL(19, 2),
  vollkosten                  DECIMAL(19, 2),
  verfuegung_id               VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE verfuegung_zeitabschnitt (
  id                          VARCHAR(36) NOT NULL,
  timestamp_erstellt          DATETIME    NOT NULL,
  timestamp_mutiert           DATETIME    NOT NULL,
  user_erstellt               VARCHAR(36) NOT NULL,
  user_mutiert                VARCHAR(36) NOT NULL,
  version                     BIGINT      NOT NULL,
  gueltig_ab                  DATE        NOT NULL,
  gueltig_bis                 DATE        NOT NULL,
  abzug_fam_groesse           DECIMAL(19, 2),
  anspruchberechtigtes_pensum INTEGER     NOT NULL,
  anspruchspensum_rest        INTEGER     NOT NULL,
  bemerkungen                 VARCHAR(1000),
  betreuungspensum            INTEGER     NOT NULL,
  betreuungsstunden           DECIMAL(19, 2),
  bg_pensum                   INTEGER     NOT NULL,
  elternbeitrag               DECIMAL(19, 2),
  erwerbspensumgs1            INTEGER     NOT NULL,
  erwerbspensumgs2            INTEGER     NOT NULL,
  fachstellenpensum           INTEGER     NOT NULL,
  massgebendes_einkommen      DECIMAL(19, 2),
  vollkosten                  DECIMAL(19, 2),
  verfuegung_id               VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);


ALTER TABLE betreuung
  ADD CONSTRAINT UK_betreuung_verfuegung_id UNIQUE (verfuegung_id);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_verfuegung_id
FOREIGN KEY (verfuegung_id)
REFERENCES verfuegung (id);

ALTER TABLE verfuegung_zeitabschnitt
  ADD CONSTRAINT FK_verfuegung_zeitabschnitt_verfuegung_id
FOREIGN KEY (verfuegung_id)
REFERENCES verfuegung (id);

ALTER TABLE verfuegung_aud
  ADD CONSTRAINT FK_verfuegung_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE verfuegung_zeitabschnitt_aud
  ADD CONSTRAINT FKl_verfuegung_zeitabschnitt_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

