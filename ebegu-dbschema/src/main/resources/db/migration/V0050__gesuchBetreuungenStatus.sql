ALTER TABLE gesuch
	ADD gesuch_betreuungen_status VARCHAR(255) NOT NULL DEFAULT 'ALLE_BESTAETIGT';
ALTER TABLE gesuch_aud
	ADD gesuch_betreuungen_status VARCHAR(255);