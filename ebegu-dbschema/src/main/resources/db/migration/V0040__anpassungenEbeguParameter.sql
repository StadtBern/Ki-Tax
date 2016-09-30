INSERT INTO ebegu_parameter
VALUES (
  'e3c3009a-84ba-11e6-ae22-56b6b6499611'
  ,'2016-09-27 00:00:00'
  ,'2016-09-27 00:00:00'
  ,'flyway'
  ,'flyway'
  ,0
  ,'2016-08-01'
  ,'2017-07-31'
  ,'PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM'
  ,'20'
);
INSERT INTO ebegu_parameter
VALUES (
  '9e7f72c7-d1df-4c29-a184-8e050fd57619'
  ,'2016-09-27 00:00:00'
  ,'2016-09-27 00:00:00'
  ,'flyway'
  ,'flyway'
  ,0
  ,'2017-08-01'
  ,'2018-07-31'
  ,'PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM'
  ,'20'
);
INSERT INTO ebegu_parameter
VALUES (
  'e184e104-0f31-4ad3-ae6b-291e348b1003'
  ,'2016-09-27 00:00:00'
  ,'2016-09-27 00:00:00'
  ,'flyway'
  ,'flyway'
  ,0
  ,'2018-08-01'
  ,'2019-07-31'
  ,'PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM'
  ,'20'
);

UPDATE ebegu_parameter set gueltig_ab = '2016-08-01', gueltig_bis = '2017-07-31' where name = 'PARAM_ABGELTUNG_PRO_TAG_KANTON' and gueltig_ab = '2016-01-01';
UPDATE ebegu_parameter set gueltig_ab = '2017-08-01', gueltig_bis = '2018-07-31' where name = 'PARAM_ABGELTUNG_PRO_TAG_KANTON' and gueltig_ab = '2017-01-01';
UPDATE ebegu_parameter set gueltig_ab = '2018-08-01', gueltig_bis = '2019-07-31' where name = 'PARAM_ABGELTUNG_PRO_TAG_KANTON' and gueltig_ab = '2018-01-01';

UPDATE ebegu_parameter set gueltig_ab = '2016-01-01', gueltig_bis = '2016-12-31' where name = 'PARAM_FIXBETRAG_STADT_PRO_TAG_KITA' and gueltig_ab = '2016-08-01';
UPDATE ebegu_parameter set gueltig_ab = '2017-01-01', gueltig_bis = '2017-12-31' where name = 'PARAM_FIXBETRAG_STADT_PRO_TAG_KITA' and gueltig_ab = '2017-08-01';
UPDATE ebegu_parameter set gueltig_ab = '2018-01-01', gueltig_bis = '2018-12-31' where name = 'PARAM_FIXBETRAG_STADT_PRO_TAG_KITA' and gueltig_ab = '2018-08-01';