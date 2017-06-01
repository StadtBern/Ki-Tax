ALTER TABLE ebegu_vorlage ADD pro_gesuchsperiode BIT NOT NULL DEFAULT TRUE;

ALTER TABLE ebegu_vorlage_aud ADD pro_gesuchsperiode BIT;

INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('1ad1111d-5078-420d-a10c-61d4c0586a81', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_ADMIN', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('2ad1111d-5078-420d-a10c-61d4c0586a82', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_INSTITUTION', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('3ad1111d-5078-420d-a10c-61d4c0586a83', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_JUGENDAMT', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('4ad1111d-5078-420d-a10c-61d4c0586a84', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_JURIST', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('5ad1111d-5078-420d-a10c-61d4c0586a85', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_REVISOR', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('6ad1111d-5078-420d-a10c-61d4c0586a86', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_SCHULAMT', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('7ad1111d-5078-420d-a10c-61d4c0586a87', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_STV', NULL, false);
INSERT INTO ebegu.ebegu_vorlage (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, vorgaenger_id, gueltig_ab, gueltig_bis, name, vorlage_id, pro_gesuchsperiode)
  VALUES ('8ad1111d-5078-420d-a10c-61d4c0586a88', '2017-06-01 00:00:00', '2017-06-01 00:00:00', 'flyway', 'flyway', 0, NULL, '1000-01-01', '9999-12-31', 'VORLAGE_BENUTZERHANDBUCH_TRAEGERSCHAFT', NULL, false);

