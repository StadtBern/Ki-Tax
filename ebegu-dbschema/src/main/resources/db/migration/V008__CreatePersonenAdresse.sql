ALTER TABLE adresse DROP FOREIGN KEY FK_adresse_gesuchsteller_id;
ALTER TABLE adresse DROP adresse_typ;
ALTER TABLE adresse DROP gesuchsteller_id;
ALTER TABLE adresse_aud DROP adresse_typ;
ALTER TABLE adresse_aud DROP gesuchsteller_id;


CREATE TABLE personen_adresse_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  adresse_typ        VARCHAR(255),
  adresse_id         VARCHAR(36),
  gesuchsteller_id   VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE personen_adresse (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  adresse_typ        VARCHAR(255),
  adresse_id         VARCHAR(36) NOT NULL,
  gesuchsteller_id   VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);





ALTER TABLE personen_adresse_aud
  ADD CONSTRAINT FK_personen_adresse_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE personen_adresse
  ADD CONSTRAINT UK_person_adresse_adresse_id UNIQUE (adresse_id);

ALTER TABLE personen_adresse
  ADD CONSTRAINT FK_pers_adresse_adresse_id
FOREIGN KEY (adresse_id)
REFERENCES adresse (id);

ALTER TABLE personen_adresse
  ADD CONSTRAINT FK_pers_adresse_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

