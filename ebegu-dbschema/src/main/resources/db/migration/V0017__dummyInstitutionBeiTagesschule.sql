-- Dummy Tagesschule
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile) VALUES ('13b57763-504b-4011-ac1a-f6a3ca9041c3', '2016-01-01 00:00:00', '2016-01-01 00:00:00', 'flyway', 'flyway', 0, null, '1000-01-01', '9999-12-31', '21', 'CH', 'Bern', '3008', 'Effingerstrasse', null);
INSERT INTO institution (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, name, mandant_id, traegerschaft_id, active, mail) VALUES ('46ebf31a-f336-4d32-b6d1-952fdcc9d793', '2016-01-01 00:00:00', '2016-01-01 00:00:00', 'flyway', 'flyway', 0, 'Tagesschule', 'e3736eb8-6eef-40ef-9e52-96ab48d8f220', null, true, 'tagesschulen@bern.ch');
INSERT INTO institution_stammdaten (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, betreuungsangebot_typ, iban, oeffnungsstunden, oeffnungstage, institution_id, adresse_id) VALUES ('62f5148d-a508-45f4-a664-510e4c0d8b7a', '2016-01-01 00:00:00', '2016-01-01 00:00:00', 'flyway', 'flyway', 0, '1000-01-01', '9999-12-31', 'TAGESSCHULE', null, null, null, '46ebf31a-f336-4d32-b6d1-952fdcc9d793', '13b57763-504b-4011-ac1a-f6a3ca9041c3');

-- Alle auf die neue Dummy-Tagesschule umhaengen
UPDATE betreuung SET institution_stammdaten_id = '62f5148d-a508-45f4-a664-510e4c0d8b7a' WHERE institution_stammdaten_id IN (SELECT id FROM institution_stammdaten WHERE betreuungsangebot_typ = 'TAGESSCHULE');

-- Constrains ausschalten
SET foreign_key_checks = 0;

-- Die "alten" Tagesschulen loeschen
DELETE FROM adresse WHERE id IN (SELECT adresse_id FROM institution_stammdaten WHERE betreuungsangebot_typ = 'TAGESSCHULE') and id <> '13b57763-504b-4011-ac1a-f6a3ca9041c3';
DELETE FROM institution WHERE id IN (SELECT institution_id FROM institution_stammdaten WHERE betreuungsangebot_typ = 'TAGESSCHULE') and id <> '46ebf31a-f336-4d32-b6d1-952fdcc9d793';
DELETE FROM institution_stammdaten WHERE betreuungsangebot_typ = 'TAGESSCHULE' and id <> '62f5148d-a508-45f4-a664-510e4c0d8b7a';

-- Constraints wieder setzen
SET foreign_key_checks = 1;