INSERT INTO berechtigung (
	SELECT
		UUID(),
		timestamp_erstellt,
		timestamp_mutiert,
		user_erstellt,
		user_mutiert,
		version,
		NULL,
		'2017-01-01',
		'9999-12-31',
		role,
		id,
		institution_id,
		traegerschaft_id,
		true
	FROM benutzer);