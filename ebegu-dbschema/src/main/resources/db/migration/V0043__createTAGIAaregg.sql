-- TAGI: Villa Tagi (11111111-1111-1111-1111-111111111174) / KITA Aaregg (11111111-1111-1111-1111-111111111101)
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile)
VALUES ('11111111-1111-1111-1111-111111111174', '2016-07-26 00:00:00', '2016-07-26 00:00:00', 'flyway', 'flyway', 0, null, '1000-01-01', '9999-12-31', '51', 'CH', 'Bern', '3004', 'Oberer Aareggweg', null);

INSERT INTO institution_stammdaten (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, betreuungsangebot_typ, iban, oeffnungsstunden, oeffnungstage, institution_id, adresse_id)
VALUES ('11111111-1111-1111-1111-111111111174', '2016-09-27 00:00:00', '2016-09-27 00:00:00', 'flyway', 'flyway', 0, '1000-01-01', '9999-12-31', 'TAGI', null, null, null, '11111111-1111-1111-1111-111111111101', '11111111-1111-1111-1111-111111111174');

UPDATE institution SET name = 'Kita & Tagi Aaregg' WHERE name = 'Kita Aaregg';
