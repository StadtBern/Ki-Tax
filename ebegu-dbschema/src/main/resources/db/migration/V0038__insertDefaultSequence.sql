# default seq einfuegen
INSERT INTO sequence (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, sequence_type, current_value, mandant_id)
  (SELECT DISTINCT
     '06c1e5d5-48c0-4f2d-af25-251b15de8ceb',
     '2016-09-27 00:00:00',
     '2016-09-27 00:00:00',
     'flyway',
     'flyway',
     0,
     'FALL_NUMMER',
     coalesce(MAX(fall.fall_nummer), 0),
     # aktuell hoechste fallnummer oder 0  wenn keine vorhanden  (coalesce gibt ersten nonnull wert zurueck)
     'e3736eb8-6eef-40ef-9e52-96ab48d8f220' # mandant id des fixen mandanten
   FROM fall);

ALTER TABLE fall MODIFY mandant_id VARCHAR(36) NOT NULL;