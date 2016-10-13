CREATE TABLE mutationsdaten (
  id                                  VARCHAR(36) NOT NULL,
  timestamp_erstellt                  DATETIME    NOT NULL,
  timestamp_mutiert                   DATETIME    NOT NULL,
  user_erstellt                       VARCHAR(36) NOT NULL,
  user_mutiert                        VARCHAR(36) NOT NULL,
  version                             BIGINT      NOT NULL,
  mutation_abwesenheit                BIT,
  mutation_betreuung                  BIT,
  mutation_einkommensverschlechterung BIT,
  mutation_erwerbspensum              BIT,
  mutation_familiensituation          BIT,
  mutation_finanzielle_situation      BIT,
  mutation_gesuchsteller              BIT,
  mutation_kind                       BIT,
  mutation_umzug                      BIT,
  PRIMARY KEY (id)
);

CREATE TABLE mutationsdaten_aud (
  id                                  VARCHAR(36) NOT NULL,
  rev                                 INTEGER     NOT NULL,
  revtype                             TINYINT,
  timestamp_erstellt                  DATETIME,
  timestamp_mutiert                   DATETIME,
  user_erstellt                       VARCHAR(36),
  user_mutiert                        VARCHAR(36),
  mutation_abwesenheit                BIT,
  mutation_betreuung                  BIT,
  mutation_einkommensverschlechterung BIT,
  mutation_erwerbspensum              BIT,
  mutation_familiensituation          BIT,
  mutation_finanzielle_situation      BIT,
  mutation_gesuchsteller              BIT,
  mutation_kind                       BIT,
  mutation_umzug                      BIT,
  PRIMARY KEY (id, rev)
);

ALTER TABLE gesuch ADD COLUMN mutationsdaten_id VARCHAR(36);
ALTER TABLE gesuch_aud ADD COLUMN mutationsdaten_id VARCHAR(36);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_mutationsdaten_id
FOREIGN KEY (mutationsdaten_id)
REFERENCES mutationsdaten (id);

ALTER TABLE mutationsdaten_aud
  ADD CONSTRAINT FK_mutationsdaten_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);
