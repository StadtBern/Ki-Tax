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

CREATE TABLE adresse (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  gemeinde           VARCHAR(255),
  hausnummer         VARCHAR(100),
  land               VARCHAR(255) NOT NULL,
  organisation       VARCHAR(255),
  ort                VARCHAR(255) NOT NULL,
  plz                VARCHAR(100) NOT NULL,
  strasse            VARCHAR(255) NOT NULL,
  zusatzzeile        VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE adresse_aud (
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
  gemeinde           VARCHAR(255),
  hausnummer         VARCHAR(100),
  land               VARCHAR(255),
  organisation       VARCHAR(255),
  ort                VARCHAR(255),
  plz                VARCHAR(100),
  strasse            VARCHAR(255),
  zusatzzeile        VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE antrag_status_history_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
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
  vorgaenger_id      VARCHAR(36),
  datum              DATETIME     NOT NULL,
  status             VARCHAR(255) NOT NULL,
  benutzer_id        VARCHAR(36)  NOT NULL,
  gesuch_id          VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE application_property_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  name               VARCHAR(255),
  value              VARCHAR(1000),
  PRIMARY KEY (id, rev)
);

CREATE TABLE application_property (
  id                 VARCHAR(36)   NOT NULL,
  timestamp_erstellt DATETIME      NOT NULL,
  timestamp_mutiert  DATETIME      NOT NULL,
  user_erstellt      VARCHAR(36)   NOT NULL,
  user_mutiert       VARCHAR(36)   NOT NULL,
  version            BIGINT        NOT NULL,
  vorgaenger_id      VARCHAR(36),
  name               VARCHAR(255)  NOT NULL,
  value              VARCHAR(1000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE authorisierter_benutzer (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  auth_token         VARCHAR(255),
  first_login        DATETIME     NOT NULL,
  last_login         DATETIME     NOT NULL,
  password           VARCHAR(255),
  role               VARCHAR(255) NOT NULL,
  samlidpentityid    VARCHAR(255),
  saml_name_id       VARCHAR(255),
  samlspentityid     VARCHAR(255),
  session_index      VARCHAR(255),
  username           VARCHAR(255) NOT NULL,
  benutzer_id        VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE benutzer (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  email              VARCHAR(255) NOT NULL,
  nachname           VARCHAR(255) NOT NULL,
  role               VARCHAR(255) NOT NULL,
  username           VARCHAR(255) NOT NULL,
  vorname            VARCHAR(255) NOT NULL,
  institution_id     VARCHAR(36),
  mandant_id         VARCHAR(36)  NOT NULL,
  traegerschaft_id   VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE benutzer_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  email              VARCHAR(255),
  nachname           VARCHAR(255),
  role               VARCHAR(255),
  username           VARCHAR(255),
  vorname            VARCHAR(255),
  institution_id     VARCHAR(36),
  mandant_id         VARCHAR(36),
  traegerschaft_id   VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuung (
  id                        VARCHAR(36)  NOT NULL,
  timestamp_erstellt        DATETIME     NOT NULL,
  timestamp_mutiert         DATETIME     NOT NULL,
  user_erstellt             VARCHAR(36)  NOT NULL,
  user_mutiert              VARCHAR(36)  NOT NULL,
  version                   BIGINT       NOT NULL,
  vorgaenger_id             VARCHAR(36),
  betreuung_nummer          INTEGER      NOT NULL,
  betreuungsstatus          VARCHAR(255) NOT NULL,
  datum_ablehnung           DATE,
  datum_bestaetigung        DATE,
  erweiterte_beduerfnisse   BIT          NOT NULL,
  grund_ablehnung           VARCHAR(1000),
  vertrag                   BIT          NOT NULL,
  institution_stammdaten_id VARCHAR(36)  NOT NULL,
  kind_id                   VARCHAR(36)  NOT NULL,
  verfuegung_id             VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE betreuung_aud (
  id                        VARCHAR(36) NOT NULL,
  rev                       INTEGER     NOT NULL,
  revtype                   TINYINT,
  timestamp_erstellt        DATETIME,
  timestamp_mutiert         DATETIME,
  user_erstellt             VARCHAR(36),
  user_mutiert              VARCHAR(36),
  vorgaenger_id             VARCHAR(36),
  betreuung_nummer          INTEGER,
  betreuungsstatus          VARCHAR(255),
  datum_ablehnung           DATE,
  datum_bestaetigung        DATE,
  erweiterte_beduerfnisse   BIT,
  grund_ablehnung           VARCHAR(1000),
  vertrag                   BIT,
  institution_stammdaten_id VARCHAR(36),
  kind_id                   VARCHAR(36),
  verfuegung_id             VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  pensum             INTEGER     NOT NULL,
  nicht_eingetreten  BIT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE betreuungspensum_aud (
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
  pensum             INTEGER,
  nicht_eingetreten  BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum_container_aud (
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  vorgaenger_id         VARCHAR(36),
  betreuung_id          VARCHAR(36),
  betreuungspensumgs_id VARCHAR(36),
  betreuungspensumja_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum_container (
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  vorgaenger_id         VARCHAR(36),
  betreuung_id          VARCHAR(36) NOT NULL,
  betreuungspensumgs_id VARCHAR(36),
  betreuungspensumja_id VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE dokument (
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
  dokument_grund_id  VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE dokument_aud (
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
  dokument_grund_id  VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE dokument_grund_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  dokument_grund_typ VARCHAR(255),
  dokument_typ       VARCHAR(255),
  full_name          VARCHAR(255),
  tag                VARCHAR(255),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE dokument_grund (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  dokument_grund_typ VARCHAR(255),
  dokument_typ       VARCHAR(255),
  full_name          VARCHAR(255),
  tag                VARCHAR(255),
  gesuch_id          VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE download_file (
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
  access_token       VARCHAR(36)  NOT NULL,
  ip                 VARCHAR(45)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE ebegu_parameter_aud (
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
  name               VARCHAR(255),
  value              VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE ebegu_vorlage_aud (
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
  name               VARCHAR(255),
  vorlage_id         VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE ebegu_parameter (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  name               VARCHAR(255) NOT NULL,
  value              VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE ebegu_vorlage (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  name               VARCHAR(255) NOT NULL,
  vorlage_id         VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE einkommensverschlechterung (
  id                           VARCHAR(36) NOT NULL,
  timestamp_erstellt           DATETIME    NOT NULL,
  timestamp_mutiert            DATETIME    NOT NULL,
  user_erstellt                VARCHAR(36) NOT NULL,
  user_mutiert                 VARCHAR(36) NOT NULL,
  version                      BIGINT      NOT NULL,
  vorgaenger_id                VARCHAR(36),
  bruttovermoegen              DECIMAL(19, 2),
  erhaltene_alimente           DECIMAL(19, 2),
  ersatzeinkommen              DECIMAL(19, 2),
  familienzulage               DECIMAL(19, 2),
  geleistete_alimente          DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr   DECIMAL(19, 2),
  schulden                     DECIMAL(19, 2),
  steuererklaerung_ausgefuellt BIT         NOT NULL,
  steuerveranlagung_erhalten   BIT         NOT NULL,
  nettolohn_apr                DECIMAL(19, 2),
  nettolohn_aug                DECIMAL(19, 2),
  nettolohn_dez                DECIMAL(19, 2),
  nettolohn_feb                DECIMAL(19, 2),
  nettolohn_jan                DECIMAL(19, 2),
  nettolohn_jul                DECIMAL(19, 2),
  nettolohn_jun                DECIMAL(19, 2),
  nettolohn_mai                DECIMAL(19, 2),
  nettolohn_mrz                DECIMAL(19, 2),
  nettolohn_nov                DECIMAL(19, 2),
  nettolohn_okt                DECIMAL(19, 2),
  nettolohn_sep                DECIMAL(19, 2),
  nettolohn_zus                DECIMAL(19, 2),
  PRIMARY KEY (id)
);

CREATE TABLE einkommensverschlechterung_aud (
  id                           VARCHAR(36) NOT NULL,
  rev                          INTEGER     NOT NULL,
  revtype                      TINYINT,
  timestamp_erstellt           DATETIME,
  timestamp_mutiert            DATETIME,
  user_erstellt                VARCHAR(36),
  user_mutiert                 VARCHAR(36),
  vorgaenger_id                VARCHAR(36),
  bruttovermoegen              DECIMAL(19, 2),
  erhaltene_alimente           DECIMAL(19, 2),
  ersatzeinkommen              DECIMAL(19, 2),
  familienzulage               DECIMAL(19, 2),
  geleistete_alimente          DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr   DECIMAL(19, 2),
  schulden                     DECIMAL(19, 2),
  steuererklaerung_ausgefuellt BIT,
  steuerveranlagung_erhalten   BIT,
  nettolohn_apr                DECIMAL(19, 2),
  nettolohn_aug                DECIMAL(19, 2),
  nettolohn_dez                DECIMAL(19, 2),
  nettolohn_feb                DECIMAL(19, 2),
  nettolohn_jan                DECIMAL(19, 2),
  nettolohn_jul                DECIMAL(19, 2),
  nettolohn_jun                DECIMAL(19, 2),
  nettolohn_mai                DECIMAL(19, 2),
  nettolohn_mrz                DECIMAL(19, 2),
  nettolohn_nov                DECIMAL(19, 2),
  nettolohn_okt                DECIMAL(19, 2),
  nettolohn_sep                DECIMAL(19, 2),
  nettolohn_zus                DECIMAL(19, 2),
  PRIMARY KEY (id, rev)
);

CREATE TABLE einkommensverschlechterung_container_aud (
  id                       VARCHAR(36) NOT NULL,
  rev                      INTEGER     NOT NULL,
  revtype                  TINYINT,
  timestamp_erstellt       DATETIME,
  timestamp_mutiert        DATETIME,
  user_erstellt            VARCHAR(36),
  user_mutiert             VARCHAR(36),
  vorgaenger_id            VARCHAR(36),
  ekvgsbasis_jahr_plus1_id VARCHAR(36),
  ekvgsbasis_jahr_plus2_id VARCHAR(36),
  ekvjabasis_jahr_plus1_id VARCHAR(36),
  ekvjabasis_jahr_plus2_id VARCHAR(36),
  gesuchsteller_id         VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE einkommensverschlechterung_info_aud (
  id                               VARCHAR(36) NOT NULL,
  rev                              INTEGER NOT NULL,
  revtype                          TINYINT,
  timestamp_erstellt               DATETIME,
  timestamp_mutiert                DATETIME,
  user_erstellt                    VARCHAR(36),
  user_mutiert                     VARCHAR(36),
  vorgaenger_id                    VARCHAR(36),
  einkommensverschlechterung       BIT,
  ekv_fuer_basis_jahr_plus1        BIT,
  ekv_fuer_basis_jahr_plus2        BIT,
  gemeinsame_steuererklaerung_bjp1 BIT,
  gemeinsame_steuererklaerung_bjp2 BIT,
  grund_fuer_basis_jahr_plus1      VARCHAR(255),
  grund_fuer_basis_jahr_plus2      VARCHAR(255),
  stichtag_fuer_basis_jahr_plus1   DATE,
  stichtag_fuer_basis_jahr_plus2   DATE,
  PRIMARY KEY (id, rev)
);

CREATE TABLE einkommensverschlechterung_container (
  id                       VARCHAR(36) NOT NULL,
  timestamp_erstellt       DATETIME    NOT NULL,
  timestamp_mutiert        DATETIME    NOT NULL,
  user_erstellt            VARCHAR(36) NOT NULL,
  user_mutiert             VARCHAR(36) NOT NULL,
  version                  BIGINT      NOT NULL,
  vorgaenger_id            VARCHAR(36),
  ekvgsbasis_jahr_plus1_id VARCHAR(36),
  ekvgsbasis_jahr_plus2_id VARCHAR(36),
  ekvjabasis_jahr_plus1_id VARCHAR(36),
  ekvjabasis_jahr_plus2_id VARCHAR(36),
  gesuchsteller_id         VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE einkommensverschlechterung_info (
  id                               VARCHAR(36) NOT NULL,
  timestamp_erstellt               DATETIME    NOT NULL,
  timestamp_mutiert                DATETIME    NOT NULL,
  user_erstellt                    VARCHAR(36) NOT NULL,
  user_mutiert                     VARCHAR(36) NOT NULL,
  version                          BIGINT      NOT NULL,
  vorgaenger_id                    VARCHAR(36),
  einkommensverschlechterung       BIT         NOT NULL,
  ekv_fuer_basis_jahr_plus1        BIT         NOT NULL,
  ekv_fuer_basis_jahr_plus2        BIT         NOT NULL,
  gemeinsame_steuererklaerung_bjp1 BIT,
  gemeinsame_steuererklaerung_bjp2 BIT,
  grund_fuer_basis_jahr_plus1      VARCHAR(255),
  grund_fuer_basis_jahr_plus2      VARCHAR(255),
  stichtag_fuer_basis_jahr_plus1   DATE,
  stichtag_fuer_basis_jahr_plus2   DATE,
  PRIMARY KEY (id)
);

CREATE TABLE einkommensverschlechterung_info_container_aud (
  id                                   VARCHAR(36) NOT NULL,
  rev                                  INTEGER NOT NULL,
  revtype                              TINYINT,
  timestamp_erstellt                   DATETIME,
  timestamp_mutiert                    DATETIME,
  user_erstellt                        VARCHAR(36),
  user_mutiert                         VARCHAR(36),
  vorgaenger_id                        VARCHAR(36),
  einkommensverschlechterung_infogs_id VARCHAR(36),
  einkommensverschlechterung_infoja_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE einkommensverschlechterung_info_container (
  id                                   VARCHAR(36) NOT NULL,
  timestamp_erstellt                   DATETIME NOT NULL,
  timestamp_mutiert                    DATETIME NOT NULL,
  user_erstellt                        VARCHAR(36) NOT NULL,
  user_mutiert                         VARCHAR(36) NOT NULL,
  version                              BIGINT NOT NULL,
  vorgaenger_id                        VARCHAR(36),
  einkommensverschlechterung_infogs_id VARCHAR(36),
  einkommensverschlechterung_infoja_id VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE erwerbspensum (
  id                        VARCHAR(36)  NOT NULL,
  timestamp_erstellt        DATETIME     NOT NULL,
  timestamp_mutiert         DATETIME     NOT NULL,
  user_erstellt             VARCHAR(36)  NOT NULL,
  user_mutiert              VARCHAR(36)  NOT NULL,
  version                   BIGINT       NOT NULL,
  vorgaenger_id             VARCHAR(36),
  gueltig_ab                DATE         NOT NULL,
  gueltig_bis               DATE         NOT NULL,
  pensum                    INTEGER      NOT NULL,
  bezeichnung               VARCHAR(255),
  taetigkeit                VARCHAR(255) NOT NULL,
  zuschlag_zu_erwerbspensum BIT          NOT NULL,
  zuschlagsgrund            VARCHAR(255),
  zuschlagsprozent          INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE erwerbspensum_aud (
  id                        VARCHAR(36) NOT NULL,
  rev                       INTEGER     NOT NULL,
  revtype                   TINYINT,
  timestamp_erstellt        DATETIME,
  timestamp_mutiert         DATETIME,
  user_erstellt             VARCHAR(36),
  user_mutiert              VARCHAR(36),
  vorgaenger_id             VARCHAR(36),
  gueltig_ab                DATE,
  gueltig_bis               DATE,
  pensum                    INTEGER,
  bezeichnung               VARCHAR(255),
  taetigkeit                VARCHAR(255),
  zuschlag_zu_erwerbspensum BIT,
  zuschlagsgrund            VARCHAR(255),
  zuschlagsprozent          INTEGER,
  PRIMARY KEY (id, rev)
);

CREATE TABLE erwerbspensum_container_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  erwerbspensumgs_id VARCHAR(36),
  erwerbspensumja_id VARCHAR(36),
  gesuchsteller_id   VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE erwerbspensum_container (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  erwerbspensumgs_id VARCHAR(36),
  erwerbspensumja_id VARCHAR(36),
  gesuchsteller_id   VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE fachstelle (
  id                       VARCHAR(36)  NOT NULL,
  timestamp_erstellt       DATETIME     NOT NULL,
  timestamp_mutiert        DATETIME     NOT NULL,
  user_erstellt            VARCHAR(36)  NOT NULL,
  user_mutiert             VARCHAR(36)  NOT NULL,
  version                  BIGINT       NOT NULL,
  vorgaenger_id            VARCHAR(36),
  behinderungsbestaetigung BIT          NOT NULL,
  beschreibung             VARCHAR(255),
  name                     VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE fachstelle_aud (
  id                       VARCHAR(36) NOT NULL,
  rev                      INTEGER     NOT NULL,
  revtype                  TINYINT,
  timestamp_erstellt       DATETIME,
  timestamp_mutiert        DATETIME,
  user_erstellt            VARCHAR(36),
  user_mutiert             VARCHAR(36),
  vorgaenger_id            VARCHAR(36),
  behinderungsbestaetigung BIT,
  beschreibung             VARCHAR(255),
  name                     VARCHAR(100),
  PRIMARY KEY (id, rev)
);

CREATE TABLE fall (
  id                  VARCHAR(36) NOT NULL,
  timestamp_erstellt  DATETIME    NOT NULL,
  timestamp_mutiert   DATETIME    NOT NULL,
  user_erstellt       VARCHAR(36) NOT NULL,
  user_mutiert        VARCHAR(36) NOT NULL,
  version             BIGINT      NOT NULL,
  vorgaenger_id       VARCHAR(36),
  fall_nummer         BIGINT,
  next_number_kind    INTEGER     NOT NULL,
  mandant_id          VARCHAR(36) NOT NULL,
  verantwortlicher_id VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE fall_aud (
  id                  VARCHAR(36) NOT NULL,
  rev                 INTEGER     NOT NULL,
  revtype             TINYINT,
  timestamp_erstellt  DATETIME,
  timestamp_mutiert   DATETIME,
  user_erstellt       VARCHAR(36),
  user_mutiert        VARCHAR(36),
  vorgaenger_id       VARCHAR(36),
  fall_nummer         BIGINT,
  next_number_kind    INTEGER,
  mandant_id          VARCHAR(36),
  verantwortlicher_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE familiensituation (
  id                          VARCHAR(36)  NOT NULL,
  timestamp_erstellt          DATETIME     NOT NULL,
  timestamp_mutiert           DATETIME     NOT NULL,
  user_erstellt               VARCHAR(36)  NOT NULL,
  user_mutiert                VARCHAR(36)  NOT NULL,
  version                     BIGINT       NOT NULL,
  vorgaenger_id               VARCHAR(36),
  aenderung_per               DATE,
  familienstatus              VARCHAR(255) NOT NULL,
  gemeinsame_steuererklaerung BIT,
  gesuchsteller_kardinalitaet VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE familiensituation_aud (
  id                          VARCHAR(36) NOT NULL,
  rev                         INTEGER     NOT NULL,
  revtype                     TINYINT,
  timestamp_erstellt          DATETIME,
  timestamp_mutiert           DATETIME,
  user_erstellt               VARCHAR(36),
  user_mutiert                VARCHAR(36),
  vorgaenger_id               VARCHAR(36),
  aenderung_per               DATE,
  familienstatus              VARCHAR(255),
  gemeinsame_steuererklaerung BIT,
  gesuchsteller_kardinalitaet VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE finanzielle_situation_aud (
  id                                VARCHAR(36) NOT NULL,
  rev                               INTEGER     NOT NULL,
  revtype                           TINYINT,
  timestamp_erstellt                DATETIME,
  timestamp_mutiert                 DATETIME,
  user_erstellt                     VARCHAR(36),
  user_mutiert                      VARCHAR(36),
  vorgaenger_id                     VARCHAR(36),
  bruttovermoegen                   DECIMAL(19, 2),
  erhaltene_alimente                DECIMAL(19, 2),
  ersatzeinkommen                   DECIMAL(19, 2),
  familienzulage                    DECIMAL(19, 2),
  geleistete_alimente               DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr        DECIMAL(19, 2),
  schulden                          DECIMAL(19, 2),
  steuererklaerung_ausgefuellt      BIT,
  steuerveranlagung_erhalten        BIT,
  geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
  nettolohn                         DECIMAL(19, 2),
  PRIMARY KEY (id, rev)
);

CREATE TABLE finanzielle_situation_container_aud (
  id                         VARCHAR(36) NOT NULL,
  rev                        INTEGER     NOT NULL,
  revtype                    TINYINT,
  timestamp_erstellt         DATETIME,
  timestamp_mutiert          DATETIME,
  user_erstellt              VARCHAR(36),
  user_mutiert               VARCHAR(36),
  vorgaenger_id              VARCHAR(36),
  jahr                       INTEGER,
  finanzielle_situationgs_id VARCHAR(36),
  finanzielle_situationja_id VARCHAR(36),
  gesuchsteller_id           VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE finanzielle_situation (
  id                                VARCHAR(36) NOT NULL,
  timestamp_erstellt                DATETIME    NOT NULL,
  timestamp_mutiert                 DATETIME    NOT NULL,
  user_erstellt                     VARCHAR(36) NOT NULL,
  user_mutiert                      VARCHAR(36) NOT NULL,
  version                           BIGINT      NOT NULL,
  vorgaenger_id                     VARCHAR(36),
  bruttovermoegen                   DECIMAL(19, 2),
  erhaltene_alimente                DECIMAL(19, 2),
  ersatzeinkommen                   DECIMAL(19, 2),
  familienzulage                    DECIMAL(19, 2),
  geleistete_alimente               DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr        DECIMAL(19, 2),
  schulden                          DECIMAL(19, 2),
  steuererklaerung_ausgefuellt      BIT         NOT NULL,
  steuerveranlagung_erhalten        BIT         NOT NULL,
  geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
  nettolohn                         DECIMAL(19, 2),
  PRIMARY KEY (id)
);

CREATE TABLE finanzielle_situation_container (
  id                         VARCHAR(36) NOT NULL,
  timestamp_erstellt         DATETIME    NOT NULL,
  timestamp_mutiert          DATETIME    NOT NULL,
  user_erstellt              VARCHAR(36) NOT NULL,
  user_mutiert               VARCHAR(36) NOT NULL,
  version                    BIGINT      NOT NULL,
  vorgaenger_id              VARCHAR(36),
  jahr                       INTEGER     NOT NULL,
  finanzielle_situationgs_id VARCHAR(36),
  finanzielle_situationja_id VARCHAR(36),
  gesuchsteller_id           VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE generated_dokument_aud (
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
  vorgaenger_id      VARCHAR(36),
  filename           VARCHAR(255) NOT NULL,
  filepfad           VARCHAR(255) NOT NULL,
  filesize           VARCHAR(255) NOT NULL,
  typ                VARCHAR(255) NOT NULL,
  gesuch_id          VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gesuch (
  id                                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt                 DATETIME     NOT NULL,
  timestamp_mutiert                  DATETIME     NOT NULL,
  user_erstellt                      VARCHAR(36)  NOT NULL,
  user_mutiert                       VARCHAR(36)  NOT NULL,
  version                            BIGINT       NOT NULL,
  vorgaenger_id                      VARCHAR(36),
  bemerkungen                        VARCHAR(1000),
  eingangsart                        VARCHAR(255) NOT NULL,
  eingangsdatum                      DATE,
  laufnummer                         INTEGER      NOT NULL,
  status                             VARCHAR(255) NOT NULL,
  typ                                VARCHAR(255) NOT NULL,
  einkommensverschlechterung_info_container_id varchar(36),
  fall_id                            VARCHAR(36)  NOT NULL,
  familiensituation_id               VARCHAR(36),
  familiensituation_erstgesuch_id    VARCHAR(36),
  gesuchsperiode_id                  VARCHAR(36)  NOT NULL,
  gesuchsteller1_id                  VARCHAR(36),
  gesuchsteller2_id                  VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE gesuch_aud (
  id                                 VARCHAR(36) NOT NULL,
  rev                                INTEGER     NOT NULL,
  revtype                            TINYINT,
  timestamp_erstellt                 DATETIME,
  timestamp_mutiert                  DATETIME,
  user_erstellt                      VARCHAR(36),
  user_mutiert                       VARCHAR(36),
  vorgaenger_id                      VARCHAR(36),
  bemerkungen                        VARCHAR(1000),
  eingangsart                        VARCHAR(255),
  eingangsdatum                      DATE,
  laufnummer                         INTEGER,
  status                             VARCHAR(255),
  typ                                VARCHAR(255),
  einkommensverschlechterung_info_container_id varchar(36),
  fall_id                            VARCHAR(36),
  familiensituation_id               VARCHAR(36),
  familiensituation_erstgesuch_id    VARCHAR(36),
  gesuchsperiode_id                  VARCHAR(36),
  gesuchsteller1_id                  VARCHAR(36),
  gesuchsteller2_id                  VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsperiode (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  active             BIT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gesuchsperiode_aud (
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
  active             BIT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsteller (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  geburtsdatum       DATE         NOT NULL,
  geschlecht         VARCHAR(255) NOT NULL,
  nachname           VARCHAR(255) NOT NULL,
  vorname            VARCHAR(255) NOT NULL,
  diplomatenstatus   BIT          NOT NULL,
  mail               VARCHAR(255) NOT NULL,
  mobile             VARCHAR(255),
  telefon            VARCHAR(255),
  telefon_ausland    VARCHAR(255),
  zpv_number         VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE gesuchsteller_adresse_aud (
  id                VARCHAR(36) NOT NULL,
  rev               INTEGER     NOT NULL,
  adresse_typ       VARCHAR(255),
  nicht_in_gemeinde BIT,
  gesuchsteller_id  VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsteller_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  geburtsdatum       DATE,
  geschlecht         VARCHAR(255),
  nachname           VARCHAR(255),
  vorname            VARCHAR(255),
  diplomatenstatus   BIT,
  mail               VARCHAR(255),
  mobile             VARCHAR(255),
  telefon            VARCHAR(255),
  telefon_ausland    VARCHAR(255),
  zpv_number         VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE gesuchsteller_adresse (
  adresse_typ       VARCHAR(255),
  nicht_in_gemeinde BIT         NOT NULL,
  id                VARCHAR(36) NOT NULL,
  gesuchsteller_id  VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE institution (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  active             BIT          NOT NULL,
  name               VARCHAR(255) NOT NULL,
  mandant_id         VARCHAR(36)  NOT NULL,
  traegerschaft_id   VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE institution_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  active             BIT,
  name               VARCHAR(255),
  mandant_id         VARCHAR(36),
  traegerschaft_id   VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE institution_stammdaten_aud (
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  vorgaenger_id         VARCHAR(36),
  gueltig_ab            DATE,
  gueltig_bis           DATE,
  betreuungsangebot_typ VARCHAR(255),
  iban                  VARCHAR(34),
  oeffnungsstunden      DECIMAL(19, 2),
  oeffnungstage         DECIMAL(19, 2),
  adresse_id            VARCHAR(36),
  institution_id        VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE institution_stammdaten (
  id                    VARCHAR(36)  NOT NULL,
  timestamp_erstellt    DATETIME     NOT NULL,
  timestamp_mutiert     DATETIME     NOT NULL,
  user_erstellt         VARCHAR(36)  NOT NULL,
  user_mutiert          VARCHAR(36)  NOT NULL,
  version               BIGINT       NOT NULL,
  vorgaenger_id         VARCHAR(36),
  gueltig_ab            DATE         NOT NULL,
  gueltig_bis           DATE         NOT NULL,
  betreuungsangebot_typ VARCHAR(255) NOT NULL,
  iban                  VARCHAR(34),
  oeffnungsstunden      DECIMAL(19, 2),
  oeffnungstage         DECIMAL(19, 2),
  adresse_id            VARCHAR(36)  NOT NULL,
  institution_id        VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE kind (
  id                             VARCHAR(36)  NOT NULL,
  timestamp_erstellt             DATETIME     NOT NULL,
  timestamp_mutiert              DATETIME     NOT NULL,
  user_erstellt                  VARCHAR(36)  NOT NULL,
  user_mutiert                   VARCHAR(36)  NOT NULL,
  version                        BIGINT       NOT NULL,
  vorgaenger_id                  VARCHAR(36),
  geburtsdatum                   DATE         NOT NULL,
  geschlecht                     VARCHAR(255) NOT NULL,
  nachname                       VARCHAR(255) NOT NULL,
  vorname                        VARCHAR(255) NOT NULL,
  einschulung                    BIT,
  familien_ergaenzende_betreuung BIT          NOT NULL,
  kinderabzug                    VARCHAR(255) NOT NULL,
  muttersprache_deutsch          BIT,
  wohnhaft_im_gleichen_haushalt  INTEGER,
  pensum_fachstelle_id           VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE kind_aud (
  id                             VARCHAR(36) NOT NULL,
  rev                            INTEGER     NOT NULL,
  revtype                        TINYINT,
  timestamp_erstellt             DATETIME,
  timestamp_mutiert              DATETIME,
  user_erstellt                  VARCHAR(36),
  user_mutiert                   VARCHAR(36),
  vorgaenger_id                  VARCHAR(36),
  geburtsdatum                   DATE,
  geschlecht                     VARCHAR(255),
  nachname                       VARCHAR(255),
  vorname                        VARCHAR(255),
  einschulung                    BIT,
  familien_ergaenzende_betreuung BIT,
  kinderabzug                    VARCHAR(255),
  muttersprache_deutsch          BIT,
  wohnhaft_im_gleichen_haushalt  INTEGER,
  pensum_fachstelle_id           VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE kind_container_aud (
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  vorgaenger_id         VARCHAR(36),
  kind_nummer           INTEGER,
  next_number_betreuung INTEGER,
  gesuch_id             VARCHAR(36),
  kindgs_id             VARCHAR(36),
  kindja_id             VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE kind_container (
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  vorgaenger_id         VARCHAR(36),
  kind_nummer           INTEGER     NOT NULL,
  next_number_betreuung INTEGER     NOT NULL,
  gesuch_id             VARCHAR(36) NOT NULL,
  kindgs_id             VARCHAR(36),
  kindja_id             VARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE mahnung (
  id                 VARCHAR(36)   NOT NULL,
  timestamp_erstellt DATETIME      NOT NULL,
  timestamp_mutiert  DATETIME      NOT NULL,
  user_erstellt      VARCHAR(36)   NOT NULL,
  user_mutiert       VARCHAR(36)   NOT NULL,
  version            BIGINT        NOT NULL,
  vorgaenger_id      VARCHAR(36),
  active             BIT           NOT NULL,
  bemerkungen        VARCHAR(1000) NOT NULL,
  datum_fristablauf  DATE          NOT NULL,
  mahnung_typ        VARCHAR(255)  NOT NULL,
  gesuch_id          VARCHAR(36)   NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mahnung_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  active             BIT,
  bemerkungen        VARCHAR(1000),
  datum_fristablauf  DATE,
  mahnung_typ        VARCHAR(255),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE mandant (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  name               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mandant_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  name               VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE pensum_fachstelle_aud (
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
  pensum             INTEGER,
  fachstelle_id      VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE pensum_fachstelle (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  pensum             INTEGER     NOT NULL,
  fachstelle_id      VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE revinfo (
  rev      INTEGER NOT NULL AUTO_INCREMENT,
  revtstmp BIGINT,
  PRIMARY KEY (rev)
);

CREATE TABLE sequence (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  current_value      BIGINT       NOT NULL,
  sequence_type      VARCHAR(100) NOT NULL,
  mandant_id         VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE traegerschaft (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  active             BIT          NOT NULL,
  name               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE traegerschaft_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  active             BIT,
  name               VARCHAR(255),
  PRIMARY KEY (id, rev)
);

CREATE TABLE verfuegung (
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  vorgaenger_id         VARCHAR(36),
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
  vorgaenger_id         VARCHAR(36),
  generated_bemerkungen VARCHAR(1000),
  manuelle_bemerkungen  VARCHAR(1000),
  PRIMARY KEY (id, rev)
);

CREATE TABLE verfuegung_zeitabschnitt_aud (
  id                                     VARCHAR(36) NOT NULL,
  rev                                    INTEGER     NOT NULL,
  revtype                                TINYINT,
  timestamp_erstellt                     DATETIME,
  timestamp_mutiert                      DATETIME,
  user_erstellt                          VARCHAR(36),
  user_mutiert                           VARCHAR(36),
  vorgaenger_id                          VARCHAR(36),
  gueltig_ab                             DATE,
  gueltig_bis                            DATE,
  abzug_fam_groesse                      DECIMAL(19, 2),
  anspruchberechtigtes_pensum            INTEGER,
  bemerkungen                            VARCHAR(1000),
  betreuungspensum                       INTEGER,
  betreuungsstunden                      DECIMAL(19, 2),
  elternbeitrag                          DECIMAL(19, 2),
  fam_groesse                            DECIMAL(19, 2),
  massgebendes_einkommen_vor_abzug_famgr DECIMAL(19, 2),
  vollkosten                             DECIMAL(19, 2),
  verfuegung_id                          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE verfuegung_zeitabschnitt (
  id                                     VARCHAR(36) NOT NULL,
  timestamp_erstellt                     DATETIME    NOT NULL,
  timestamp_mutiert                      DATETIME    NOT NULL,
  user_erstellt                          VARCHAR(36) NOT NULL,
  user_mutiert                           VARCHAR(36) NOT NULL,
  version                                BIGINT      NOT NULL,
  vorgaenger_id                          VARCHAR(36),
  gueltig_ab                             DATE        NOT NULL,
  gueltig_bis                            DATE        NOT NULL,
  abzug_fam_groesse                      DECIMAL(19, 2),
  anspruchberechtigtes_pensum            INTEGER     NOT NULL,
  bemerkungen                            VARCHAR(1000),
  betreuungspensum                       INTEGER     NOT NULL,
  betreuungsstunden                      DECIMAL(19, 2),
  elternbeitrag                          DECIMAL(19, 2),
  fam_groesse                            DECIMAL(19, 2),
  massgebendes_einkommen_vor_abzug_famgr DECIMAL(19, 2),
  vollkosten                             DECIMAL(19, 2),
  verfuegung_id                          VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE vorlage (
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
  PRIMARY KEY (id)
);

CREATE TABLE vorlage_aud (
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
  PRIMARY KEY (id, rev)
);

CREATE TABLE wizard_step_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  bemerkungen        VARCHAR(1000),
  verfuegbar         BIT,
  wizard_step_name   VARCHAR(255),
  wizard_step_status VARCHAR(255),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE wizard_step (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  vorgaenger_id      VARCHAR(36),
  bemerkungen        VARCHAR(1000),
  verfuegbar         BIT          NOT NULL,
  wizard_step_name   VARCHAR(255) NOT NULL,
  wizard_step_status VARCHAR(255) NOT NULL,
  gesuch_id          VARCHAR(36)  NOT NULL,
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

ALTER TABLE adresse_aud
  ADD CONSTRAINT FK_adresse_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

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

ALTER TABLE application_property_aud
  ADD CONSTRAINT FK_application_property_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE application_property
  ADD CONSTRAINT UK_application_property_name UNIQUE (name);

CREATE INDEX IX_authorisierter_benutzer
  ON authorisierter_benutzer (benutzer_id);

CREATE INDEX IX_authorisierter_benutzer_token
  ON authorisierter_benutzer (auth_token, benutzer_id);

ALTER TABLE authorisierter_benutzer
  ADD CONSTRAINT FK_authorisierter_benutzer_benutzer_id
FOREIGN KEY (benutzer_id)
REFERENCES benutzer (id);

CREATE INDEX IX_benutzer_username_mandant
  ON benutzer (username, mandant_id);

ALTER TABLE benutzer
  ADD CONSTRAINT FK_benutzer_institution_id
FOREIGN KEY (institution_id)
REFERENCES institution (id);

ALTER TABLE benutzer
  ADD CONSTRAINT FK_benutzer_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);

ALTER TABLE benutzer
  ADD CONSTRAINT FK_benutzer_traegerschaft_id
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE benutzer_aud
  ADD CONSTRAINT FK_benutzer_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuung
  ADD CONSTRAINT UK_betreuung_kind_betreuung_nummer UNIQUE (betreuung_nummer, kind_id);

ALTER TABLE betreuung
  ADD CONSTRAINT UK_betreuung_verfuegung_id UNIQUE (verfuegung_id);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_institution_stammdaten_id
FOREIGN KEY (institution_stammdaten_id)
REFERENCES institution_stammdaten (id);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_kind_id
FOREIGN KEY (kind_id)
REFERENCES kind_container (id);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_verfuegung_id
FOREIGN KEY (verfuegung_id)
REFERENCES verfuegung (id);

ALTER TABLE betreuung_aud
  ADD CONSTRAINT FK_betreuung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungspensum_aud
  ADD CONSTRAINT FK_betreuungspensum_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungspensum_container_aud
  ADD CONSTRAINT FK_betreuungspensum_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuung_id
FOREIGN KEY (betreuung_id)
REFERENCES betreuung (id);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuungspensum_gs
FOREIGN KEY (betreuungspensumgs_id)
REFERENCES betreuungspensum (id);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuungspensum_ja
FOREIGN KEY (betreuungspensumja_id)
REFERENCES betreuungspensum (id);

ALTER TABLE dokument
  ADD CONSTRAINT FK_dokument_dokumentgrund_id
FOREIGN KEY (dokument_grund_id)
REFERENCES dokument_grund (id);

ALTER TABLE dokument_aud
  ADD CONSTRAINT FK_dokument_aud_dokumentgrund_id
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE dokument_grund_aud
  ADD CONSTRAINT FK_dokumentgrund_aud_gesuch_id
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE dokument_grund
  ADD CONSTRAINT FK_dokumentGrund_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);

ALTER TABLE ebegu_parameter_aud
  ADD CONSTRAINT FK_ebegu_parameter_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE ebegu_vorlage_aud
  ADD CONSTRAINT FK_ebeguvorlage_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE ebegu_vorlage
  ADD CONSTRAINT FK_ebeguvorlage_vorlage_id
FOREIGN KEY (vorlage_id)
REFERENCES vorlage (id);

ALTER TABLE einkommensverschlechterung_aud
  ADD CONSTRAINT FK_einkommensverschlechterung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_container_aud
  ADD CONSTRAINT FK_einkommensverschlechterung_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_info_aud
  ADD CONSTRAINT FK_einkommensverschlechterung_info_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_info_container_aud
  ADD CONSTRAINT FK_einkommensverschlechterung_info_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_container
  ADD CONSTRAINT UK_einkommensverschlechterungcontainer_gesuchsteller UNIQUE (gesuchsteller_id);

ALTER TABLE einkommensverschlechterung_container
  ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvGSBasisJahrPlus1_id
FOREIGN KEY (ekvgsbasis_jahr_plus1_id)
REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container
  ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvGSBasisJahrPlus2_id
FOREIGN KEY (ekvgsbasis_jahr_plus2_id)
REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container
  ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvJABasisJahrPlus1_id
FOREIGN KEY (ekvjabasis_jahr_plus1_id)
REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container
  ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvJABasisJahrPlus2_id
FOREIGN KEY (ekvjabasis_jahr_plus2_id)
REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container
  ADD CONSTRAINT FK_einkommensverschlechterungcontainer_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE einkommensverschlechterung_info_container
  ADD CONSTRAINT FK_ekvinfocontainer_einkommensverschlechterunginfogs_id
FOREIGN KEY (einkommensverschlechterung_infogs_id)
REFERENCES einkommensverschlechterung_info (id);

ALTER TABLE einkommensverschlechterung_info_container
  ADD CONSTRAINT FK_ekvinfocontainer_einkommensverschlechterunginfoja_id
FOREIGN KEY (einkommensverschlechterung_infoja_id)
REFERENCES einkommensverschlechterung_info (id);

ALTER TABLE erwerbspensum_aud
  ADD CONSTRAINT FK_erwerbspensum_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE erwerbspensum_container_aud
  ADD CONSTRAINT FK_erwerbspensum_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_erwerbspensum_container_erwerbspensumgs_id
FOREIGN KEY (erwerbspensumgs_id)
REFERENCES erwerbspensum (id);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_erwerbspensum_container_erwerbspensumja_id
FOREIGN KEY (erwerbspensumja_id)
REFERENCES erwerbspensum (id);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_erwerbspensum_container_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE fachstelle_aud
  ADD CONSTRAINT FK_fachstelle_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

CREATE INDEX IX_fall_fall_nummer
  ON fall (fall_nummer);

CREATE INDEX IX_fall_mandant
  ON fall (mandant_id);

ALTER TABLE fall
  ADD CONSTRAINT UK_fall_nummer UNIQUE (fall_nummer);

ALTER TABLE fall
  ADD CONSTRAINT FK_fall_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);

ALTER TABLE fall
  ADD CONSTRAINT FK_fall_verantwortlicher_id
FOREIGN KEY (verantwortlicher_id)
REFERENCES benutzer (id);

ALTER TABLE fall_aud
  ADD CONSTRAINT FK_fall_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE familiensituation_aud
  ADD CONSTRAINT FK_familiensituation_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE finanzielle_situation_aud
  ADD CONSTRAINT FK_finanzielle_situation_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE finanzielle_situation_container_aud
  ADD CONSTRAINT FK_finanzielle_situation_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT UK_finanzielle_situation_container_gesuchsteller UNIQUE (gesuchsteller_id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanzielleSituationContainer_finanzielleSituationGS_id
FOREIGN KEY (finanzielle_situationgs_id)
REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanzielleSituationContainer_finanzielleSituationJA_id
FOREIGN KEY (finanzielle_situationja_id)
REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT FK_finanzielleSituationContainer_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE generated_dokument_aud
  ADD CONSTRAINT FK_generated_dokument_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE generated_dokument
  ADD CONSTRAINT FK_generated_dokument_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_einkommensverschlechterungInfoContainer_id
FOREIGN KEY (einkommensverschlechterung_info_container_id)
REFERENCES einkommensverschlechterung_info_container (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_fall_id
FOREIGN KEY (fall_id)
REFERENCES fall (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_familiensituation_id
FOREIGN KEY (familiensituation_id)
REFERENCES familiensituation (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_familiensituation_erstgesuch_id
FOREIGN KEY (familiensituation_erstgesuch_id)
REFERENCES familiensituation (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_antrag_gesuchsperiode_id
FOREIGN KEY (gesuchsperiode_id)
REFERENCES gesuchsperiode (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_gesuchsteller1_id
FOREIGN KEY (gesuchsteller1_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_gesuchsteller2_id
FOREIGN KEY (gesuchsteller2_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuch_aud
  ADD CONSTRAINT FK_gesuch_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE gesuchsperiode_aud
  ADD CONSTRAINT FK_gesuchsperiode_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE gesuchsteller_adresse_aud
  ADD CONSTRAINT FK_gesuchsteller_adresse_aud_revinfo
FOREIGN KEY (id, rev)
REFERENCES adresse_aud (id, rev);

ALTER TABLE gesuchsteller_aud
  ADD CONSTRAINT FK_gesuchsteller_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE gesuchsteller_adresse
  ADD CONSTRAINT FK_gesuchsteller_adresse_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuchsteller_adresse
  ADD CONSTRAINT FK_gesuchsteller_adresse_adresse
FOREIGN KEY (id)
REFERENCES adresse (id);

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_traegerschaft_id
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE institution_aud
  ADD CONSTRAINT FK_institution_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE institution_stammdaten_aud
  ADD CONSTRAINT FK_institution_stammdaten_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT UK_institution_stammdaten_adresse_id UNIQUE (adresse_id);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT FK_institution_stammdaten_adresse_id
FOREIGN KEY (adresse_id)
REFERENCES adresse (id);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT FK_institution_stammdaten_institution_id
FOREIGN KEY (institution_id)
REFERENCES institution (id);

ALTER TABLE kind
  ADD CONSTRAINT FK_kind_pensum_fachstelle_id
FOREIGN KEY (pensum_fachstelle_id)
REFERENCES pensum_fachstelle (id);

ALTER TABLE kind_aud
  ADD CONSTRAINT FK_kind_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE kind_container_aud
  ADD CONSTRAINT FK_kind_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE kind_container
  ADD CONSTRAINT UK_kindcontainer_gesuch_kind_nummer UNIQUE (kind_nummer, gesuch_id);

ALTER TABLE kind_container
  ADD CONSTRAINT FK_kind_container_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);

ALTER TABLE kind_container
  ADD CONSTRAINT FK_kind_container_kindgs_id
FOREIGN KEY (kindgs_id)
REFERENCES kind (id);

ALTER TABLE kind_container
  ADD CONSTRAINT FK_kind_container_kindja_id
FOREIGN KEY (kindja_id)
REFERENCES kind (id);

ALTER TABLE mahnung
  ADD CONSTRAINT FK_mahnung_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);

ALTER TABLE mahnung_aud
  ADD CONSTRAINT FK_mahnung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE mandant_aud
  ADD CONSTRAINT FK_mandant_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE pensum_fachstelle_aud
  ADD CONSTRAINT FK_pensum_fachstelle_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE pensum_fachstelle
  ADD CONSTRAINT FK_pensum_fachstelle_fachstelle_id
FOREIGN KEY (fachstelle_id)
REFERENCES fachstelle (id);

CREATE INDEX sequence_ix1
  ON sequence (mandant_id);

ALTER TABLE sequence
  ADD CONSTRAINT UK_sequence UNIQUE (sequence_type, mandant_id);

ALTER TABLE sequence
  ADD CONSTRAINT FK_sequence_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);

ALTER TABLE traegerschaft_aud
  ADD CONSTRAINT FK_traegerschaft_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE verfuegung_aud
  ADD CONSTRAINT FK_verfuegung_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE verfuegung_zeitabschnitt_aud
  ADD CONSTRAINT FKl_verfuegung_zeitabschnitt_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE verfuegung_zeitabschnitt
  ADD CONSTRAINT FK_verfuegung_zeitabschnitt_verfuegung_id
FOREIGN KEY (verfuegung_id)
REFERENCES verfuegung (id);

ALTER TABLE vorlage_aud
  ADD CONSTRAINT FK_vorlage_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE wizard_step_aud
  ADD CONSTRAINT FK_wizard_step_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE wizard_step
  ADD CONSTRAINT UK_wizardstep_gesuch_stepname UNIQUE (wizard_step_name, gesuch_id);

ALTER TABLE wizard_step
  ADD CONSTRAINT FK_wizardstep_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);
