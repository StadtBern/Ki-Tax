CREATE TABLE adresse (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  adresse_typ        VARCHAR(255),
  gemeinde           VARCHAR(255),
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  hausnummer         VARCHAR(100),
  land               VARCHAR(255) NOT NULL,
  ort                VARCHAR(255) NOT NULL,
  plz                VARCHAR(100) NOT NULL,
  strasse            VARCHAR(255) NOT NULL,
  zusatzzeile        VARCHAR(255),
  gesuchsteller_id   VARCHAR(36)  NOT NULL,
  CONSTRAINT PK_adresse PRIMARY KEY (id)
);

CREATE TABLE adresse_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  adresse_typ        VARCHAR(255),
  gemeinde           VARCHAR(255),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  hausnummer         VARCHAR(100),
  land               VARCHAR(255),
  ort                VARCHAR(255),
  plz                VARCHAR(100),
  strasse            VARCHAR(255),
  zusatzzeile        VARCHAR(255),
  gesuchsteller_id   VARCHAR(36) NOT NULL,
  CONSTRAINT PK_adresse_aud PRIMARY KEY (id, rev)
);

CREATE TABLE application_property_aud (
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

CREATE TABLE application_property (
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

CREATE TABLE gesuchsteller (
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
  CONSTRAINT PK_gesuchsteller PRIMARY KEY (id)
);

CREATE TABLE gesuchsteller_aud (
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
  CONSTRAINT PK_gesuchsteller_aud PRIMARY KEY (id, rev)
);

CREATE TABLE revinfo (
  rev      INTEGER NOT NULL AUTO_INCREMENT,
  revtstmp BIGINT,
  PRIMARY KEY (rev)
);

ALTER TABLE adresse
  ADD CONSTRAINT FK_adresse_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE adresse_aud
  ADD CONSTRAINT FK_adresse_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE application_property_aud
  ADD CONSTRAINT FK_application_property_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE application_property
  ADD CONSTRAINT UK_application_property_name UNIQUE (name);

ALTER TABLE gesuchsteller_aud
  ADD CONSTRAINT FK_gesuchsteller_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);
