INSERT INTO berechtigung (
	SELECT
		UUID(),
		timestamp_erstellt,
		timestamp_mutiert,
		user_erstellt,
		user_mutiert,
		version,
		NULL,
		'1000-01-01',
		'9999-12-31',
		role,
		id,
		institution_id,
		traegerschaft_id
	FROM benutzer);

UPDATE benutzer b SET current_berechtigung_id = (SELECT id FROM berechtigung WHERE benutzer_id = b.id);

ALTER TABLE benutzer
	ADD CONSTRAINT FK_benutzer_currentBerechtigung_id
FOREIGN KEY (current_berechtigung_id)
REFERENCES berechtigung (id);
