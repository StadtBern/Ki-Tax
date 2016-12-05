-- Superuser DV Bern
INSERT INTO benutzer (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version,
                      email, nachname, role, username, vorname, mandant_id, traegerschaft_id, institution_id, vorgaenger_id)
VALUES
  ('11111111-1111-1111-1111-111111111111', '2016-01-01 00:00:00', '2016-01-01 00:00:00', 'anonymous', 'anonymous', 0,
                                           'hallo@dvbern.ch', 'Superuser', 'SUPER_ADMIN', 'ebegu', 'E-BEGU',
   'e3736eb8-6eef-40ef-9e52-96ab48d8f220', NULL, NULL, NULL);

-- Superuser System (fuer BatchJobs)
INSERT INTO benutzer (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version,
                      email, nachname, role, username, vorname, mandant_id, traegerschaft_id, institution_id, vorgaenger_id)
VALUES
  ('22222222-2222-2222-2222-222222222222', '2016-01-01 00:00:00', '2016-01-01 00:00:00', 'anonymous', 'anonymous', 0,
                                           'hallo@dvbern.ch', 'System', 'SUPER_ADMIN', 'system', '',
   'e3736eb8-6eef-40ef-9e52-96ab48d8f220', NULL, NULL, NULL);