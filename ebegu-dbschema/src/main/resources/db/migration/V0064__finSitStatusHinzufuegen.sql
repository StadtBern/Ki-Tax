/*FinSitStatus hinzufuegen*/
ALTER TABLE gesuch_aud
	ADD COLUMN fin_sit_status VARCHAR(255);

ALTER TABLE gesuch
	ADD COLUMN fin_sit_status VARCHAR(255);
