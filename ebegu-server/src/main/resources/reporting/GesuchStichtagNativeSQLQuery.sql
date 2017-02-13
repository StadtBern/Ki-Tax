SELECT
  CONCAT_WS('.'
  , RIGHT(YEAR(gesuchsperiode.gueltig_ab), 2)
  , LPAD(fall.fall_nummer, 6, 0)
  , kind_container.kind_nummer
  , betreuung.betreuung_nummer
  )                                                                       AS BGNummer,
  CONCAT(IFNULL(CONCAT(traegerschaft.name, ' - '), ''), institution.name) AS Institution,
  institution_stammdaten.betreuungsangebot_typ                            AS BetreuungsTyp,
  CONCAT_WS('/', CAST(YEAR(gesuchsperiode.gueltig_ab) AS CHAR(4))
  , CAST(
                YEAR(gesuchsperiode.gueltig_bis) AS CHAR(4))
  )                                                                       AS Periode,
  IFNULL(NichtFreigegebeneGesuch.Anzahl, 0)                                  NichtFreigegeben,
  IFNULL(Mahnungen.Anzahl, 0)                                                Mahnungen,
  IFNULL(Beschwerde.Anzahl, 0)                                               Beschwerde

FROM kind_container
  INNER JOIN kind ON kind.id = kind_container.kindja_id
  INNER JOIN betreuung ON betreuung.kind_id = kind_container.id
  INNER JOIN institution_stammdaten ON institution_stammdaten.id = betreuung.institution_stammdaten_id
  INNER JOIN institution ON institution.id = institution_stammdaten.institution_id
  LEFT OUTER JOIN traegerschaft ON traegerschaft.id = institution.traegerschaft_id
  INNER JOIN gesuch ON gesuch.id = kind_container.gesuch_id
  INNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id
  INNER JOIN fall ON fall.id = gesuch.fall_id

  LEFT OUTER JOIN (
                    SELECT
                      1         AS Anzahl,
                      gesuch.id AS gesuch_id
                    FROM antrag_status_history
                      INNER JOIN gesuch ON gesuch.id = antrag_status_history.gesuch_id
                      INNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id
                    WHERE :stichTagDate >= timestamp_von
                          AND (:stichTagDate <= timestamp_bis OR timestamp_bis IS NULL)
                          AND antrag_status_history.status IN ('IN_BEARBEITUNG_GS', 'FREIGABEQUITTUNG')
                  ) NichtFreigegebeneGesuch ON NichtFreigegebeneGesuch.gesuch_id = gesuch.id

  LEFT OUTER JOIN (
                    SELECT DISTINCT
                      1 AS Anzahl,
                      gesuch_id
                    FROM mahnung
                    WHERE :stichTagDate >= timestamp_erstellt
                          AND (:stichTagDate <= timestamp_abgeschlossen OR timestamp_abgeschlossen IS NULL)
                  ) Mahnungen ON Mahnungen.gesuch_id = gesuch.id

  LEFT OUTER JOIN (
                    SELECT
                      1         AS Anzahl,
                      gesuch.id AS gesuch_id
                    FROM antrag_status_history
                      INNER JOIN gesuch ON gesuch.id = antrag_status_history.gesuch_id
                      INNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id
                    WHERE antrag_status_history.status IN ('BESCHWERDE_HAENGIG')
                          AND :stichTagDate >= timestamp_von
                          AND (:stichTagDate <= timestamp_bis OR timestamp_bis IS NULL)
                  ) Beschwerde ON Beschwerde.gesuch_id = gesuch.id

WHERE (gesuchsperiode.id = :gesuchPeriodeID OR :gesuchPeriodeID IS NULL)
      AND (institution_stammdaten.betreuungsangebot_typ = 'TAGESSCHULE' OR :onlySchulamt = 0)
      AND (
        IFNULL(NichtFreigegebeneGesuch.Anzahl, 0) = 1
        OR IFNULL(Mahnungen.Anzahl, 0) = 1
        OR IFNULL(Beschwerde.Anzahl, 0) = 1
      )