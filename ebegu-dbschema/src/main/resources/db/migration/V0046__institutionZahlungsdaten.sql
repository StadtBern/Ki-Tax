ALTER TABLE institution_stammdaten
	ADD kontoinhaber VARCHAR(255);
ALTER TABLE institution_stammdaten
	ADD adresse_kontoinhaber_id VARCHAR(36);

ALTER TABLE institution_stammdaten_aud
	ADD kontoinhaber VARCHAR(255);
ALTER TABLE institution_stammdaten_aud
	ADD adresse_kontoinhaber_id VARCHAR(36);

ALTER TABLE institution_stammdaten
	ADD CONSTRAINT UK_institution_stammdaten_adressekontoinhaber_id UNIQUE (adresse_kontoinhaber_id);
ALTER TABLE institution_stammdaten
	ADD CONSTRAINT FK_institution_stammdaten_adressekontoinhaber_id FOREIGN KEY (adresse_kontoinhaber_id) REFERENCES adresse (id);