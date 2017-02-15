CREATE TABLE zahlung (
  id                        VARCHAR(36)  NOT NULL,
  timestamp_erstellt        DATETIME     NOT NULL,
  timestamp_mutiert         DATETIME     NOT NULL,
  user_erstellt             VARCHAR(36)  NOT NULL,
  user_mutiert              VARCHAR(36)  NOT NULL,
  version                   BIGINT       NOT NULL,
  vorgaenger_id             VARCHAR(36),
  status                    VARCHAR(255) NOT NULL,
  institution_stammdaten_id VARCHAR(36)  NOT NULL,
  zahlungsauftrag_id        VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE zahlung_aud (
  id                        VARCHAR(36) NOT NULL,
  rev                       INTEGER     NOT NULL,
  revtype                   TINYINT,
  timestamp_erstellt        DATETIME,
  timestamp_mutiert         DATETIME,
  user_erstellt             VARCHAR(36),
  user_mutiert              VARCHAR(36),
  vorgaenger_id             VARCHAR(36),
  status                    VARCHAR(255),
  institution_stammdaten_id VARCHAR(36),
  zahlungsauftrag_id        VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE zahlungsauftrag (
  id                 VARCHAR(36)   NOT NULL,
  timestamp_erstellt DATETIME      NOT NULL,
  timestamp_mutiert  DATETIME      NOT NULL,
  user_erstellt      VARCHAR(36)   NOT NULL,
  user_mutiert       VARCHAR(36)   NOT NULL,
  version            BIGINT        NOT NULL,
  vorgaenger_id      VARCHAR(36),
  gueltig_ab         DATE          NOT NULL,
  gueltig_bis        DATE          NOT NULL,
  ausgeloest         BIT           NOT NULL,
  beschrieb          VARCHAR(255)  NOT NULL,
  datum_faellig      DATETIME      NOT NULL,
  datum_generiert    DATETIME      NOT NULL,
  filecontent        VARCHAR(4000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE zahlungsauftrag_aud (
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
  ausgeloest         BIT,
  beschrieb          VARCHAR(255),
  datum_faellig      DATETIME,
  datum_generiert    DATETIME,
  filecontent        VARCHAR(4000),
  PRIMARY KEY (id, rev)
);

CREATE TABLE zahlungsposition (
  id                          VARCHAR(36)    NOT NULL,
  timestamp_erstellt          DATETIME       NOT NULL,
  timestamp_mutiert           DATETIME       NOT NULL,
  user_erstellt               VARCHAR(36)    NOT NULL,
  user_mutiert                VARCHAR(36)    NOT NULL,
  version                     BIGINT         NOT NULL,
  vorgaenger_id               VARCHAR(36),
  betrag                      DECIMAL(19, 2) NOT NULL,
  status                      VARCHAR(255)   NOT NULL,
  verfuegung_zeitabschnitt_id VARCHAR(36)    NOT NULL,
  zahlung_id                  VARCHAR(36)    NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE zahlungsposition_aud (
  id                          VARCHAR(36) NOT NULL,
  rev                         INTEGER     NOT NULL,
  revtype                     TINYINT,
  timestamp_erstellt          DATETIME,
  timestamp_mutiert           DATETIME,
  user_erstellt               VARCHAR(36),
  user_mutiert                VARCHAR(36),
  vorgaenger_id               VARCHAR(36),
  betrag                      DECIMAL(19, 2),
  status                      VARCHAR(255),
  verfuegung_zeitabschnitt_id VARCHAR(36),
  zahlung_id                  VARCHAR(36),
  PRIMARY KEY (id, rev)
);

ALTER TABLE zahlung
  ADD CONSTRAINT FK_Zahlung_institutionStammdaten_id
FOREIGN KEY (institution_stammdaten_id)
REFERENCES institution_stammdaten (id);

ALTER TABLE zahlung
  ADD CONSTRAINT FK_Zahlung_zahlungsauftrag_id
FOREIGN KEY (zahlungsauftrag_id)
REFERENCES zahlungsauftrag (id);

ALTER TABLE zahlung_aud
  ADD CONSTRAINT FK_zahlung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE zahlungsauftrag_aud
  ADD CONSTRAINT FK_zahlungsauftrag_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE zahlungsposition
  ADD CONSTRAINT FK_Zahlungsposition_verfuegungZeitabschnitt_id
FOREIGN KEY (verfuegung_zeitabschnitt_id)
REFERENCES verfuegung_zeitabschnitt (id);

ALTER TABLE zahlungsposition
  ADD CONSTRAINT FK_Zahlungsposition_zahlung_id
FOREIGN KEY (zahlung_id)
REFERENCES zahlung (id);

ALTER TABLE zahlungsposition_aud
  ADD CONSTRAINT FK_zahlungsposition_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);