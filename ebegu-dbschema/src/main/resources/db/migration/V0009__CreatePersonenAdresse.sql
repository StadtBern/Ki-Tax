ALTER TABLE adresse DROP FOREIGN KEY FK_adresse_gesuchsteller_id;
ALTER TABLE adresse DROP adresse_typ;
ALTER TABLE adresse DROP gesuchsteller_id;
ALTER TABLE adresse_aud DROP adresse_typ;
ALTER TABLE adresse_aud DROP gesuchsteller_id;




CREATE TABLE gesuchsteller_adresse (
  adresse_typ      VARCHAR(255),
  id               VARCHAR(36) NOT NULL,
  gesuchsteller_id VARCHAR(36) NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE gesuchsteller_adresse_aud (
  id               VARCHAR(36) NOT NULL,
  rev              INTEGER     NOT NULL,
  adresse_typ      VARCHAR(255),
  gesuchsteller_id VARCHAR(36),
  PRIMARY KEY (id, rev)
);


ALTER TABLE gesuchsteller_adresse_aud
  ADD CONSTRAINT FK_personen_adresse_aud_revinfo
FOREIGN KEY (id, rev)
REFERENCES adresse_aud (id, rev);



ALTER TABLE gesuchsteller_adresse
  ADD CONSTRAINT FK_gesuchsteller_adresse_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuchsteller_adresse
  ADD CONSTRAINT FK_gesuchsteller_adresse_adresse_id
FOREIGN KEY (id)
REFERENCES adresse (id);



