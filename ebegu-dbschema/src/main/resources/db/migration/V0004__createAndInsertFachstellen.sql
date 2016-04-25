CREATE TABLE fachstelle (
  id                       VARCHAR(36)  NOT NULL,
  timestamp_erstellt       DATETIME     NOT NULL,
  timestamp_mutiert        DATETIME     NOT NULL,
  user_erstellt            VARCHAR(36)  NOT NULL,
  user_mutiert             VARCHAR(36)  NOT NULL,
  version                  BIGINT       NOT NULL,
  behinderungsbestaetigung BIT          NOT NULL,
  beschreibung             VARCHAR(255),
  name                     VARCHAR(100) NOT NULL,
  CONSTRAINT PK_fachstelle PRIMARY KEY (id)
);

CREATE TABLE fachstelle_aud (
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
CONSTRAINT fachstelle_aud PRIMARY KEY (id, rev)
);

ALTER TABLE fachstelle_aud
  ADD CONSTRAINT FK_fachstelle_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);


INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
         'Kindesschutzbehörde', 'Kindesschutzbehörde', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Erwachsenen-&Kindesschutz', 'Amt für Erwachsenen- und Kindesschutz der Stadt Bern', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Sozialdienst', 'Sozialdienst der Stadt Bern', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Gesundheitsdienst', 'Gesundheitsdienst der Stadt Bern', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Erziehungsberatung Kanton (Soziale Indikation)', 'Erziehungsberatung des Kantons Bern (Bereich Früherziehungsdienst ebenfalls für Kinder mit Behinderung)', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Erziehungsberatung Kanton (Erweiterte Bedürfnisse)', 'Erziehungsberatung Kanton (Erweiterte Bedürfnisse)', true);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Kompetenzzentrum Integration', 'Fachbereich Asyl und Sozialhilfe des Kompetenzzentrums Integration der Stadt Bern', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'SRK', 'SRK', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'HEKS', 'HEKS', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Caritas', 'Caritas', false);

INSERT INTO fachstelle (id, version, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, name, beschreibung, behinderungsbestaetigung)
  VALUES (uuid(), 0, current_timestamp, current_timestamp, 'anonymous', 'anonymous',
          'Andere Fachstelle', 'Andere Fachstelle', false);
