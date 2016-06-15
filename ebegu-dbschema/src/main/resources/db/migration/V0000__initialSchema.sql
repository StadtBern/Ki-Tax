CREATE TABLE adresse
(
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  gemeinde           VARCHAR(255),
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  hausnummer         VARCHAR(100),
  land               VARCHAR(255) NOT NULL,
  ort                VARCHAR(255) NOT NULL,
  plz                VARCHAR(100) NOT NULL,
  strasse            VARCHAR(255) NOT NULL,
  zusatzzeile        VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE adresse_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gemeinde           VARCHAR(255),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  hausnummer         VARCHAR(100),
  land               VARCHAR(255),
  ort                VARCHAR(255),
  plz                VARCHAR(100),
  strasse            VARCHAR(255),
  zusatzzeile        VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE application_property_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  value              VARCHAR(1000),
  PRIMARY KEY (id, rev)
);

CREATE TABLE application_property
(
  id                 VARCHAR(36)   NOT NULL,
  timestamp_erstellt DATETIME      NOT NULL,
  timestamp_mutiert  DATETIME      NOT NULL,
  user_erstellt      VARCHAR(36)   NOT NULL,
  user_mutiert       VARCHAR(36)   NOT NULL,
  version            BIGINT        NOT NULL,
  name               VARCHAR(255)  NOT NULL,
  value              VARCHAR(1000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE betreuung
(
  id                        VARCHAR(36)  NOT NULL,
  timestamp_erstellt        DATETIME     NOT NULL,
  timestamp_mutiert         DATETIME     NOT NULL,
  user_erstellt             VARCHAR(36)  NOT NULL,
  user_mutiert              VARCHAR(36)  NOT NULL,
  version                   BIGINT       NOT NULL,
  bemerkungen               VARCHAR(1000),
  betreuungsstatus          VARCHAR(255) NOT NULL,
  schulpflichtig            BIT,
  institution_stammdaten_id VARCHAR(36)  NOT NULL,
  kind_id                   VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE betreuung_aud
(
  id                        VARCHAR(36) NOT NULL,
  rev                       INTEGER     NOT NULL,
  revtype                   TINYINT,
  timestamp_erstellt        DATETIME,
  timestamp_mutiert         DATETIME,
  user_erstellt             VARCHAR(36),
  user_mutiert              VARCHAR(36),
  bemerkungen               VARCHAR(1000),
  betreuungsstatus          VARCHAR(255),
  schulpflichtig            BIT,
  institution_stammdaten_id VARCHAR(36),
  kind_id                   VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum
(
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  pensum             INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE betreuungspensum_aud
(
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
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum_container_aud
(
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  betreuung_id          VARCHAR(36),
  betreuungspensumgs_id VARCHAR(36),
  betreuungspensumja_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum_container
(
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  betreuung_id          VARCHAR(36) NOT NULL,
  betreuungspensumgs_id VARCHAR(36),
  betreuungspensumja_id VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE ebegu_parameter_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  name               VARCHAR(255),
  value              VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE ebegu_parameter
(
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  name               VARCHAR(255) NOT NULL,
  value              VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE erwerbspensum
(
  id                               VARCHAR(36)  NOT NULL,
  timestamp_erstellt               DATETIME     NOT NULL,
  timestamp_mutiert                DATETIME     NOT NULL,
  user_erstellt                    VARCHAR(36)  NOT NULL,
  user_mutiert                     VARCHAR(36)  NOT NULL,
  version                          BIGINT       NOT NULL,
  gueltig_ab                       DATE         NOT NULL,
  gueltig_bis                      DATE         NOT NULL,
  pensum                           INTEGER,
  gesundheitliche_einschraenkungen BIT          NOT NULL,
  taetigkeit                       VARCHAR(255) NOT NULL,
  zuschlag_zu_erwerbspensum        BIT          NOT NULL,
  zuschlagsgrund                   VARCHAR(255),
  zuschlagsprozent                 INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE erwerbspensum_aud
(
  id                               VARCHAR(36) NOT NULL,
  rev                              INTEGER     NOT NULL,
  revtype                          TINYINT,
  timestamp_erstellt               DATETIME,
  timestamp_mutiert                DATETIME,
  user_erstellt                    VARCHAR(36),
  user_mutiert                     VARCHAR(36),
  gueltig_ab                       DATE,
  gueltig_bis                      DATE,
  pensum                           INTEGER,
  gesundheitliche_einschraenkungen BIT,
  taetigkeit                       VARCHAR(255),
  zuschlag_zu_erwerbspensum        BIT,
  zuschlagsgrund                   VARCHAR(255),
  zuschlagsprozent                 INTEGER,
  PRIMARY KEY (id, rev)
);

CREATE TABLE erwerbspensum_container_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  erwerbspensumgs_id VARCHAR(36),
  erwerbspensumja_id VARCHAR(36),
  gesuchsteller_id   VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE erwerbspensum_container
(
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  erwerbspensumgs_id VARCHAR(36),
  erwerbspensumja_id VARCHAR(36),
  gesuchsteller_id   VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE fachstelle
(
  id                       VARCHAR(36)  NOT NULL,
  timestamp_erstellt       DATETIME     NOT NULL,
  timestamp_mutiert        DATETIME     NOT NULL,
  user_erstellt            VARCHAR(36)  NOT NULL,
  user_mutiert             VARCHAR(36)  NOT NULL,
  version                  BIGINT       NOT NULL,
  behinderungsbestaetigung BIT          NOT NULL,
  beschreibung             VARCHAR(255),
  name                     VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE fachstelle_aud
(
  id                       VARCHAR(36) NOT NULL,
  rev                      INTEGER     NOT NULL,
  revtype                  TINYINT,
  timestamp_erstellt       DATETIME,
  timestamp_mutiert        DATETIME,
  user_erstellt            VARCHAR(36),
  user_mutiert             VARCHAR(36),
  behinderungsbestaetigung BIT,
  beschreibung             VARCHAR(255),
  name                     VARCHAR(100),
  PRIMARY KEY (id, rev)
);

CREATE TABLE fall
(
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  fall_nummer        INTEGER,
  PRIMARY KEY (id)
);

ALTER TABLE fall
  ADD CONSTRAINT UK_fall_nummer UNIQUE (fall_nummer);

CREATE INDEX IX_fall_fall_nummer ON fall (fall_nummer);

ALTER TABLE fall
  MODIFY fall_nummer INTEGER AUTO_INCREMENT NOT NULL;

CREATE TABLE fall_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  fall_nummer        INTEGER,
  PRIMARY KEY (id, rev)
);

CREATE TABLE familiensituation
(
  id                          VARCHAR(36)  NOT NULL,
  timestamp_erstellt          DATETIME     NOT NULL,
  timestamp_mutiert           DATETIME     NOT NULL,
  user_erstellt               VARCHAR(36)  NOT NULL,
  user_mutiert                VARCHAR(36)  NOT NULL,
  version                     BIGINT       NOT NULL,
  gesuchsteller_kardinalitaet VARCHAR(255),
  bemerkungen                 VARCHAR(1000),
  familienstatus              VARCHAR(255) NOT NULL,
  gemeinsame_steuererklaerung BIT,
  PRIMARY KEY (id)
);

CREATE TABLE familiensituation_aud
(
  id                          VARCHAR(36) NOT NULL,
  rev                         INTEGER     NOT NULL,
  revtype                     TINYINT,
  timestamp_erstellt          DATETIME,
  timestamp_mutiert           DATETIME,
  user_erstellt               VARCHAR(36),
  user_mutiert                VARCHAR(36),
  gesuchsteller_kardinalitaet VARCHAR(255),
  bemerkungen                 VARCHAR(1000),
  familienstatus              VARCHAR(255),
  gemeinsame_steuererklaerung BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE finanzielle_situation_aud
(
  id                                VARCHAR(36) NOT NULL,
  rev                               INTEGER     NOT NULL,
  revtype                           TINYINT,
  timestamp_erstellt                DATETIME,
  timestamp_mutiert                 DATETIME,
  user_erstellt                     VARCHAR(36),
  user_mutiert                      VARCHAR(36),
  bruttovermoegen                   DECIMAL(19, 2),
  erhaltene_alimente                DECIMAL(19, 2),
  ersatzeinkommen                   DECIMAL(19, 2),
  familienzulage                    DECIMAL(19, 2),
  geleistete_alimente               DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr        DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
  nettolohn                         DECIMAL(19, 2),
  schulden                          DECIMAL(19, 2),
  selbstaendig                      BIT,
  steuererklaerung_ausgefuellt      BIT,
  steuerveranlagung_erhalten        BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE finanzielle_situation_container_aud
(
  id                         VARCHAR(36) NOT NULL,
  rev                        INTEGER     NOT NULL,
  revtype                    TINYINT,
  timestamp_erstellt         DATETIME,
  timestamp_mutiert          DATETIME,
  user_erstellt              VARCHAR(36),
  user_mutiert               VARCHAR(36),
  jahr                       INTEGER,
  finanzielle_situationgs_id VARCHAR(36),
  finanzielle_situationja_id VARCHAR(36),
  finanzielle_situationsv_id VARCHAR(36),
  gesuchsteller_id           VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE finanzielle_situation
(
  id                                VARCHAR(36) NOT NULL,
  timestamp_erstellt                DATETIME    NOT NULL,
  timestamp_mutiert                 DATETIME    NOT NULL,
  user_erstellt                     VARCHAR(36) NOT NULL,
  user_mutiert                      VARCHAR(36) NOT NULL,
  version                           BIGINT      NOT NULL,
  bruttovermoegen                   DECIMAL(19, 2),
  erhaltene_alimente                DECIMAL(19, 2),
  ersatzeinkommen                   DECIMAL(19, 2),
  familienzulage                    DECIMAL(19, 2),
  geleistete_alimente               DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr        DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
  nettolohn                         DECIMAL(19, 2),
  schulden                          DECIMAL(19, 2),
  selbstaendig                      BIT         NOT NULL,
  steuererklaerung_ausgefuellt      BIT         NOT NULL,
  steuerveranlagung_erhalten        BIT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE finanzielle_situation_container
(
  id                         VARCHAR(36) NOT NULL,
  timestamp_erstellt         DATETIME    NOT NULL,
  timestamp_mutiert          DATETIME    NOT NULL,
  user_erstellt              VARCHAR(36) NOT NULL,
  user_mutiert               VARCHAR(36) NOT NULL,
  version                    BIGINT      NOT NULL,
  jahr                       INTEGER     NOT NULL,
  finanzielle_situationgs_id VARCHAR(36),
  finanzielle_situationja_id VARCHAR(36),
  finanzielle_situationsv_id VARCHAR(36),
  gesuchsteller_id           VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gesuch
(
  id                         VARCHAR(36) NOT NULL,
  timestamp_erstellt         DATETIME    NOT NULL,
  timestamp_mutiert          DATETIME    NOT NULL,
  user_erstellt              VARCHAR(36) NOT NULL,
  user_mutiert               VARCHAR(36) NOT NULL,
  version                    BIGINT      NOT NULL,
  fall_id                    VARCHAR(36) NOT NULL,
  familiensituation_id       VARCHAR(36),
  gesuchsteller1_id          VARCHAR(36),
  gesuchsteller2_id          VARCHAR(36),
  einkommensverschlechterung BIT,
  eingangsdatum              DATE        NOT NULL,
  gesuchsperiode_id          VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gesuch_aud
(
  id                         VARCHAR(36) NOT NULL,
  rev                        INTEGER     NOT NULL,
  revtype                    TINYINT,
  timestamp_erstellt         DATETIME,
  timestamp_mutiert          DATETIME,
  user_erstellt              VARCHAR(36),
  user_mutiert               VARCHAR(36),
  fall_id                    VARCHAR(36),
  familiensituation_id       VARCHAR(36),
  gesuchsteller1_id          VARCHAR(36),
  gesuchsteller2_id          VARCHAR(36),
  einkommensverschlechterung BIT,
  eingangsdatum              DATE,
  gesuchsperiode_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsperiode
(
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  active             BIT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gesuchsperiode_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  active             BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsteller
(
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  geburtsdatum       DATE         NOT NULL,
  geschlecht         VARCHAR(255) NOT NULL,
  mail               VARCHAR(255) NOT NULL,
  mobile             VARCHAR(255),
  nachname           VARCHAR(255) NOT NULL,
  telefon            VARCHAR(255),
  telefon_ausland    VARCHAR(255),
  vorname            VARCHAR(255) NOT NULL,
  zpv_number         VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE gesuchsteller_adresse_aud
(
  id               VARCHAR(36) NOT NULL,
  rev              INTEGER     NOT NULL,
  adresse_typ      VARCHAR(255),
  gesuchsteller_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsteller_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  geburtsdatum       DATE,
  geschlecht         VARCHAR(255),
  mail               VARCHAR(255),
  mobile             VARCHAR(255),
  nachname           VARCHAR(255),
  telefon            VARCHAR(255),
  telefon_ausland    VARCHAR(255),
  vorname            VARCHAR(255),
  zpv_number         VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsteller_adresse
(
  adresse_typ      VARCHAR(255),
  id               VARCHAR(36) NOT NULL,
  gesuchsteller_id VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE institution
(
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  name               VARCHAR(255) NOT NULL,
  mandant_id         VARCHAR(36)  NOT NULL,
  traegerschaft_id   VARCHAR(36),
  active             BIT          NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE institution_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  mandant_id         VARCHAR(36),
  traegerschaft_id   VARCHAR(36),
  active             BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE institution_stammdaten_aud
(
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  gueltig_ab            DATE,
  gueltig_bis           DATE,
  betreuungsangebot_typ VARCHAR(255),
  iban                  VARCHAR(34),
  oeffnungsstunden      DECIMAL(19, 2),
  oeffnungstage         DECIMAL(19, 2),
  institution_id        VARCHAR(36),
  adresse_id            VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE institution_stammdaten
(
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  gueltig_ab            DATE        NOT NULL,
  gueltig_bis           DATE        NOT NULL,
  betreuungsangebot_typ VARCHAR(255),
  iban                  VARCHAR(34),
  oeffnungsstunden      DECIMAL(19, 2),
  oeffnungstage         DECIMAL(19, 2),
  institution_id        VARCHAR(36) NOT NULL,
  adresse_id            VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE kind
(
  id                             VARCHAR(36)  NOT NULL,
  timestamp_erstellt             DATETIME     NOT NULL,
  timestamp_mutiert              DATETIME     NOT NULL,
  user_erstellt                  VARCHAR(36)  NOT NULL,
  user_mutiert                   VARCHAR(36)  NOT NULL,
  version                        BIGINT       NOT NULL,
  geburtsdatum                   DATE         NOT NULL,
  geschlecht                     VARCHAR(255) NOT NULL,
  nachname                       VARCHAR(255) NOT NULL,
  vorname                        VARCHAR(255) NOT NULL,
  bemerkungen                    VARCHAR(1000),
  familien_ergaenzende_betreuung BIT          NOT NULL,
  muttersprache_deutsch          BIT,
  unterstuetzungspflicht         BIT,
  wohnhaft_im_gleichen_haushalt  INTEGER      NOT NULL,
  pensum_fachstelle_id           VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE kind_aud
(
  id                             VARCHAR(36) NOT NULL,
  rev                            INTEGER     NOT NULL,
  revtype                        TINYINT,
  timestamp_erstellt             DATETIME,
  timestamp_mutiert              DATETIME,
  user_erstellt                  VARCHAR(36),
  user_mutiert                   VARCHAR(36),
  geburtsdatum                   DATE,
  geschlecht                     VARCHAR(255),
  nachname                       VARCHAR(255),
  vorname                        VARCHAR(255),
  bemerkungen                    VARCHAR(1000),
  familien_ergaenzende_betreuung BIT,
  muttersprache_deutsch          BIT,
  unterstuetzungspflicht         BIT,
  wohnhaft_im_gleichen_haushalt  INTEGER,
  pensum_fachstelle_id           VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE kind_container_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gesuch_id          VARCHAR(36),
  kindgs_id          VARCHAR(36),
  kindja_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE kind_container
(
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gesuch_id          VARCHAR(36) NOT NULL,
  kindgs_id          VARCHAR(36),
  kindja_id          VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE mandant
(
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  name               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mandant_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE pensum_fachstelle_aud
(
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
  PRIMARY KEY (id, rev)
);

CREATE TABLE pensum_fachstelle
(
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
  PRIMARY KEY (id)
);

CREATE TABLE revinfo
(
  rev      INTEGER NOT NULL AUTO_INCREMENT,
  revtstmp BIGINT,
  PRIMARY KEY (rev)
);

CREATE TABLE traegerschaft
(
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  name               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE traegerschaft_aud
(
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  PRIMARY KEY (id, rev)
);

ALTER TABLE adresse_aud
  ADD CONSTRAINT FK_adresse_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE application_property_aud
  ADD CONSTRAINT FK_application_property_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE application_property
  ADD CONSTRAINT UK_application_property_name UNIQUE (name);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_institution_stammdaten_id FOREIGN KEY (
  institution_stammdaten_id) REFERENCES institution_stammdaten (id);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_kind_id FOREIGN KEY (kind_id) REFERENCES
  kind_container (id);

ALTER TABLE betreuung_aud
  ADD CONSTRAINT FK_betreuung_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE betreuungspensum_aud
  ADD CONSTRAINT FK_betreuungspensum_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE betreuungspensum_container_aud
  ADD CONSTRAINT FK_betreuungspensum_container_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuung_id FOREIGN KEY (
  betreuung_id) REFERENCES betreuung (id);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuungspensum_gs FOREIGN KEY (
  betreuungspensumgs_id) REFERENCES betreuungspensum (id);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuungspensum_ja FOREIGN KEY (
  betreuungspensumja_id) REFERENCES betreuungspensum (id);

ALTER TABLE ebegu_parameter_aud
  ADD CONSTRAINT FK_ebegu_parameter_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE erwerbspensum_aud
  ADD CONSTRAINT FK_erwerbspensum_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE erwerbspensum_container_aud
  ADD CONSTRAINT FK_erwerbspensum_container_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_erwerbspensum_container_erwerbspensumgs_id FOREIGN KEY (
  erwerbspensumgs_id) REFERENCES erwerbspensum (id);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_erwerbspensum_container_erwerbspensumja_id FOREIGN KEY (
  erwerbspensumja_id) REFERENCES erwerbspensum (id);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_erwerbspensum_container_gesuchsteller_id FOREIGN KEY (
  gesuchsteller_id) REFERENCES gesuchsteller (id);

ALTER TABLE fachstelle_aud
  ADD CONSTRAINT FK_fachstelle_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE fall_aud
  ADD CONSTRAINT FK_fall_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE familiensituation_aud
  ADD CONSTRAINT FK_familiensituation_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE finanzielle_situation_aud
  ADD CONSTRAINT FK_finanzielle_situation_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE finanzielle_situation_container_aud
  ADD CONSTRAINT FK_finanzielle_situation_container_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT UK_finanzielle_situation_container_gesuchsteller UNIQUE (gesuchsteller_id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanziellesituationcontainer_finanziellesituationgs_id
FOREIGN KEY (finanzielle_situationgs_id) REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanziellesituationcontainer_finanziellesituationja_id
FOREIGN KEY (finanzielle_situationja_id) REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanziellesituationcontainer_finanziellesituationsv_id
FOREIGN KEY (finanzielle_situationsv_id) REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanziellesituationcontainer_gesuchsteller_id FOREIGN KEY (
  gesuchsteller_id) REFERENCES gesuchsteller (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_fall_id FOREIGN KEY (fall_id) REFERENCES fall (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_antrag_gesuchsperiode_id FOREIGN KEY (gesuchsperiode_id)
REFERENCES gesuchsperiode (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_familiensituation_id FOREIGN KEY (
  familiensituation_id) REFERENCES familiensituation (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_gesuchsteller1_id FOREIGN KEY (gesuchsteller1_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_gesuchsteller2_id FOREIGN KEY (gesuchsteller2_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuch_aud
  ADD CONSTRAINT FK_gesuch_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE gesuchsperiode_aud
  ADD CONSTRAINT FK_gesuchsperiode_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE gesuchsteller_adresse_aud
  ADD CONSTRAINT FK_gesuchsteller_adresse_aud_revinfo FOREIGN KEY (id, rev) REFERENCES
  adresse_aud (id, rev);

ALTER TABLE gesuchsteller_aud
  ADD CONSTRAINT FK_gesuchsteller_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE gesuchsteller_adresse
  ADD CONSTRAINT FK_gesuchsteller_adresse_gesuchsteller_id FOREIGN KEY (
  gesuchsteller_id) REFERENCES gesuchsteller (id);

ALTER TABLE gesuchsteller_adresse
  ADD CONSTRAINT FK_gesuchsteller_adresse_adresse FOREIGN KEY (id) REFERENCES adresse
(id);

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_mandant_id FOREIGN KEY (mandant_id) REFERENCES
  mandant (id);

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_traegerschaft_id FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE institution_aud
  ADD CONSTRAINT FK_institution_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE institution_stammdaten_aud
  ADD CONSTRAINT FK_institution_stammdaten_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT UK_institution_stammdaten_adresse UNIQUE (adresse_id);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT FK_institution_stammdaten_adresse_id FOREIGN KEY (adresse_id)
REFERENCES adresse (id);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT FK_institution_stammdaten_institution_id FOREIGN KEY (
  institution_id) REFERENCES institution (id);

ALTER TABLE kind
  ADD CONSTRAINT FK_kind_pensum_fachstelle_id FOREIGN KEY (pensum_fachstelle_id)
REFERENCES pensum_fachstelle (id);

ALTER TABLE kind_aud
  ADD CONSTRAINT FK_kind_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE kind_container_aud
  ADD CONSTRAINT FK_kind_container_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE kind_container
  ADD CONSTRAINT FK_kind_container_gesuch_id FOREIGN KEY (gesuch_id) REFERENCES
  gesuch (id);

ALTER TABLE kind_container
  ADD CONSTRAINT FK_kind_container_kindgs_id FOREIGN KEY (kindgs_id) REFERENCES
  kind (id);

ALTER TABLE kind_container
  ADD CONSTRAINT FK_kind_container_kindja_id FOREIGN KEY (kindja_id) REFERENCES
  kind (id);

ALTER TABLE mandant_aud
  ADD CONSTRAINT FK_mandant_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE pensum_fachstelle_aud
  ADD CONSTRAINT FK_pensum_fachstelle_aud_revinfo FOREIGN KEY (rev) REFERENCES
  revinfo (rev);

ALTER TABLE pensum_fachstelle
  ADD CONSTRAINT FK_pensum_fachstelle_fachstelle_id FOREIGN KEY (fachstelle_id)
REFERENCES fachstelle (id);

ALTER TABLE traegerschaft_aud
  ADD CONSTRAINT FK_traegerschaft_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo
(rev);