ALTER TABLE dokument_grund add person_number DECIMAL(19, 2);
ALTER TABLE dokument_grund_aud add person_number DECIMAL(19, 2);

ALTER TABLE dokument_grund add person_type VARCHAR(255);
ALTER TABLE dokument_grund_aud add person_type VARCHAR(255);

UPDATE dokument_grund SET person_type='FREETEXT';