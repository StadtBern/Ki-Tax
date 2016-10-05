INSERT INTO ebegu_parameter (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, name, value)
  SELECT *
  FROM (SELECT
          UUID(),
          now()    AS gueltigAb,
          now()    AS gueltigBis,
          'flyway' AS userErstellt,
          'flyway' AS userMutiert,
          0,
          '2017-01-01',
          '2017-12-31',
          'PARAM_FIXBETRAG_STADT_PRO_TAG_KITA',
          '7.00') AS tmp
  WHERE NOT EXISTS(SELECT *
                   FROM ebegu_parameter
                   WHERE name = 'PARAM_FIXBETRAG_STADT_PRO_TAG_KITA' AND gueltig_ab = '2017-01-01')
  LIMIT 1;
