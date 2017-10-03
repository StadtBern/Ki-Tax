ALTER TABLE dokument_grund
	ADD person_number DECIMAL(19, 2);
ALTER TABLE dokument_grund_aud
	ADD person_number DECIMAL(19, 2);

ALTER TABLE dokument_grund
	ADD person_type VARCHAR(255);
ALTER TABLE dokument_grund_aud
	ADD person_type VARCHAR(255);

UPDATE dokument_grund
SET person_type = 'FREETEXT';