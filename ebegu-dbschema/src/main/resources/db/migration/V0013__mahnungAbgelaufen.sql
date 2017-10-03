ALTER TABLE mahnung
	ADD abgelaufen BIT;
ALTER TABLE mahnung_aud
	ADD abgelaufen BIT;

UPDATE mahnung
SET abgelaufen = TRUE
WHERE DATE(datum_fristablauf) < CURDATE();
