DROP TABLE IF EXISTS adresse_aud;
CREATE TABLE adresse_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INT4        NOT NULL,
  revtype            INT2,
  timestamp_erstellt TIMESTAMP,
  timestamp_mutiert  TIMESTAMP,
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
  person_id          VARCHAR(36),
  CONSTRAINT PK_adresse_aud PRIMARY KEY (id, rev)
);
DROP TABLE IF EXISTS adresse;
CREATE TABLE adresse (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt TIMESTAMP    NOT NULL,
  timestamp_mutiert  TIMESTAMP    NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            INT8         NOT NULL,
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
  person_id          VARCHAR(36)  NOT NULL,
  CONSTRAINT PK_adresse PRIMARY KEY (id)
);


CREATE TABLE person (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt TIMESTAMP    NOT NULL,
  timestamp_mutiert  TIMESTAMP    NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            INT8         NOT NULL,
  geburtsdatum       DATE         NOT NULL,
  geschlecht         VARCHAR(255) NOT NULL,
  mail               VARCHAR(255) NOT NULL,
  mobile             VARCHAR(255),
  nachname           VARCHAR(255) NOT NULL,
  telefon            VARCHAR(255),
  telefon_ausland    VARCHAR(255),
  vorname            VARCHAR(255) NOT NULL,
  zpv_number         VARCHAR(255),
  CONSTRAINT PK_person PRIMARY KEY (id)
);

CREATE TABLE person_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INT4        NOT NULL,
  revtype            INT2,
  timestamp_erstellt TIMESTAMP,
  timestamp_mutiert  TIMESTAMP,
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
  CONSTRAINT PK_person_aud PRIMARY KEY (id, rev)
);


ALTER TABLE adresse_aud
ADD CONSTRAINT FK_adresse_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo;


ALTER TABLE adresse
ADD CONSTRAINT FK_adresse_person_id
FOREIGN KEY (person_id)
REFERENCES person;

ALTER TABLE person_aud
ADD CONSTRAINT FK_person_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo;

