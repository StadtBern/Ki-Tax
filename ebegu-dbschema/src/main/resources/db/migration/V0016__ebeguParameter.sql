CREATE TABLE ebegu_parameter (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  name               VARCHAR(255) NOT NULL,
  value              VARCHAR(255) NOT NULL,
  CONSTRAINT PK_ebeguparameter PRIMARY KEY (id)
);

CREATE TABLE ebegu_parameter_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  name               VARCHAR(255),
  value              VARCHAR(255),
  CONSTRAINT PK_ebeguparameter_aud PRIMARY KEY (id, rev)
);

ALTER TABLE ebegu_parameter_aud
  ADD CONSTRAINT FK_ebeguparameter_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

-- Initialwerte
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('06c1e5d5-48c0-4f2d-af25-251b15de8ceb', '2016-06-03 10:16:38', '2016-06-03 10:20:59', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PENSUM_TAGESELTERN_MIN', '20');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('06c1e5d5-48c0-4f2d-af25-251b15de8cec', '2016-06-03 10:16:38', '2016-06-03 10:20:59', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PENSUM_TAGESSCHULE_MIN', '0');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('1ea4f569-4705-4165-8e00-4d78395817d4', '2016-06-03 10:16:34', '2016-06-03 10:20:57', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_ANZAHL_TAGE_KANTON', '240');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('3ee7d728-551d-4085-989a-846f092fbdf4', '2016-06-03 10:16:38', '2016-06-03 10:20:57', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PENSUM_KITA_MIN', '10');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('4fbd62ed-38db-48fd-9b98-3d3363f23257', '2016-06-03 10:16:34', '2016-06-03 10:20:57', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PENSUM_TAGI_MIN', '60');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('5676f112-e16b-4356-b6d6-6c80082c6de1', '2016-06-03 10:16:36', '2016-06-03 10:20:55', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_KOSTEN_PRO_STUNDE_MAX', '11.91');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('56a7e7db-3503-46b7-b41f-18216c0ec787', '2016-06-03 10:16:34', '2016-06-03 10:20:57', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_MASSGEBENDES_EINKOMMEN_MIN', '42540');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('5de5b68b-127b-487d-b4a4-7c6539d4b3be', '2016-06-03 10:16:34', '2016-06-03 10:20:58', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_MASSGEBENDES_EINKOMMEN_MAX', '158690');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('606551d2-d55b-4407-bf69-c3080fe8c50b', '2016-06-03 10:16:11', '2016-06-03 10:20:56', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN', '9.16');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('6c7af5ff-58f7-4e8f-9e70-9009bb1f1b92', '2016-06-03 10:16:37', '2016-06-03 10:20:58', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3', '3760');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('71a6c200-f249-4796-8784-10376a9fbd67', '2016-06-03 10:16:37', '2016-06-03 10:20:59', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4', '5900');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('76d45a75-10ab-46a3-ac24-ce1f50268519', '2016-06-03 10:16:39', '2016-06-03 10:16:39', 'flyway', 'flyway', 0, '2016-01-01', '2016-12-31', 'PARAM_ABGELTUNG_PRO_TAG_KANTON', '107.19');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('97eed21d-5c4e-4883-a2ac-1a0d9f17e5b6', '2016-06-03 10:16:38', '2016-06-03 10:20:59', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5', '6970');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('990591a5-62d5-4c28-9b86-6bf7f28552ad', '2016-06-03 10:16:11', '2016-06-03 10:20:56', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_ANZAL_TAGE_MAX_KITA', '244');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('9f373467-8a47-4bb1-9764-1399112f9638', '2016-06-03 10:16:34', '2016-06-03 10:20:58', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_STUNDEN_PRO_TAG_TAGI', '7');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('a4732a56-3050-40b6-8238-e668e98e8f18', '2016-06-03 10:16:11', '2016-06-03 10:20:56', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_STUNDEN_PRO_TAG_MAX_KITA', '11.5');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('a5cd4af5-52d0-4831-b596-7f7507aab973', '2016-06-03 10:16:11', '2016-06-03 10:20:56', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_FIXBETRAG_STADT_PRO_TAG_KITA', '7.00');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('c4877e99-c465-42ee-9299-207f9f546dc2', '2016-06-03 10:16:39', '2016-06-03 10:20:58', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_MAX_TAGE_ABWESENHEIT', '30');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('ccda60ff-7d67-4a65-be2c-db03bab1a9d5', '2016-06-03 10:16:11', '2016-06-03 10:20:56', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_KOSTEN_PRO_STUNDE_MIN', '0.75');
INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value) VALUES ('ccdd3efd-e829-4b3c-bc1c-28c8c3a463a2', '2016-06-03 10:16:37', '2016-06-03 10:20:58', 'flyway', 'flyway', 0, '2016-08-01', '2017-07-31', 'PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6', '7500');