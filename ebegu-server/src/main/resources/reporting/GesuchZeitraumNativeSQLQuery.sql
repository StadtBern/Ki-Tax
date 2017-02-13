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
  IFNULL(EingegangeneGesuch.AnzahlGesuchOnline, 0)                           AnzahlGesuchOnline,
  IFNULL(EingegangeneGesuch.AnzahlGesuchPapier, 0)                           AnzahlGesuchPapier,
  IFNULL(EingegangeneGesuch.AnzahlMutationOnline, 0)                         AnzahlMutationOnline,
  IFNULL(EingegangeneGesuch.AnzahlMutationPapier, 0)                         AnzahlMutationPapier,
  IFNULL(Mutationen.AnzahlMutationAbwesenheit, 0)                            AnzahlMutationAbwesenheit,
  IFNULL(Mutationen.AnzahlMutationBetreuung, 0)                              AnzahlMutationBetreuung,
  IFNULL(Mutationen.AnzahlMutationDokumente, 0)                              AnzahlMutationDokumente,
  IFNULL(Mutationen.AnzahlMutationEV, 0)                                     AnzahlMutationEV,
  IFNULL(Mutationen.AnzahlMutationEwerbspensum, 0)                           AnzahlMutationEwerbspensum,
  IFNULL(Mutationen.AnzahlMutationFamilienSitutation, 0)                     AnzahlMutationFamilienSitutation,
  IFNULL(Mutationen.AnzahlMutationFinanzielleSituation, 0)                   AnzahlMutationFinanzielleSituation,
  IFNULL(Mutationen.AnzahlMutationFreigabe, 0)                               AnzahlMutationFreigabe,
  IFNULL(Mutationen.AnzahlMutationGesuchErstellen, 0)                        AnzahlMutationGesuchErstellen,
  IFNULL(Mutationen.AnzahlMutationGesuchsteller, 0)                          AnzahlMutationGesuchsteller,
  IFNULL(Mutationen.AnzahlMutationKinder, 0)                                 AnzahlMutationKinder,
  IFNULL(Mutationen.AnzahlMutationUmzug, 0)                                  AnzahlMutationUmzug,
  IFNULL(Mutationen.AnzahlMutationVerfuegen, 0)                              AnzahlMutationVerfuegen,
  IFNULL(Mahnungen.AnzahlMahnungen, 0)                                       AnzahlMahnungen,
  IFNULL(Beschwerde.AnzahlBeschwerde, 0)                                     AnzahlBeschwerde,
  IFNULL(Verfuegungen.AnzahlVerfuegungen, 0)                                 AnzahlVerfuegungen,
  IFNULL(Verfuegungen.kategorie_normal, 0)                                   AnzahlVerfuegungenNormal,
  IFNULL(Verfuegungen.kategorie_max_einkommen, 0)                            AnzahlVerfuegungenMaxEinkommen,
  IFNULL(Verfuegungen.kategorie_kein_pensum, 0)                              AnzahlVerfuegungenKeinPensum,
  IFNULL(Verfuegungen.kategorie_zuschlag_zum_erwerbspensum, 0)               AnzahlVerfuegungenZuschlagZumPensum,
  IFNULL(Verfuegungen.kategorie_nicht_eintreten, 0)                          AnzahlVerfuegungenNichtEintreten

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
                      CASE WHEN eingangsart = 'ONLINE' AND typ = 'GESUCH'
                        THEN 1 END AnzahlGesuchOnline,
                      CASE WHEN eingangsart = 'PAPIER' AND typ = 'GESUCH'
                        THEN 1 END AnzahlGesuchPapier,
                      CASE WHEN eingangsart = 'ONLINE' AND typ = 'MUTATION'
                        THEN 1 END AnzahlMutationOnline,
                      CASE WHEN eingangsart = 'PAPIER' AND typ = 'MUTATION'
                        THEN 1 END AnzahlMutationPapier,
                      gesuch.id AS gesuch_id
                    FROM gesuch
                     WHERE eingangsdatum BETWEEN :fromDate AND :toDate
                  ) EingegangeneGesuch ON EingegangeneGesuch.gesuch_id = gesuch.id

  LEFT OUTER JOIN
  (
    SELECT
      gesuch_id,
      CASE WHEN wizard_step_name = 'ABWESENHEIT'
        THEN 1 END AnzahlMutationAbwesenheit,
      CASE WHEN wizard_step_name = 'BETREUUNG'
        THEN 1 END AnzahlMutationBetreuung,
      CASE WHEN wizard_step_name = 'DOKUMENTE'
        THEN 1 END AnzahlMutationDokumente,
      CASE WHEN wizard_step_name = 'EINKOMMENSVERSCHLECHTERUNG'
        THEN 1 END AnzahlMutationEV,
      CASE WHEN wizard_step_name = 'ERWERBSPENSUM'
        THEN 1 END AnzahlMutationEwerbspensum,
      CASE WHEN wizard_step_name = 'FAMILIENSITUATION'
        THEN 1 END AnzahlMutationFamilienSitutation,
      CASE WHEN wizard_step_name = 'FINANZIELLE_SITUATION'
        THEN 1 END AnzahlMutationFinanzielleSituation,
      CASE WHEN wizard_step_name = 'FREIGABE'
        THEN 1 END AnzahlMutationFreigabe,
      CASE WHEN wizard_step_name = 'GESUCH_ERSTELLEN'
        THEN 1 END AnzahlMutationGesuchErstellen,
      CASE WHEN wizard_step_name = 'GESUCHSTELLER'
        THEN 1 END AnzahlMutationGesuchsteller,
      CASE WHEN wizard_step_name = 'KINDER'
        THEN 1 END AnzahlMutationKinder,
      CASE WHEN wizard_step_name = 'UMZUG'
        THEN 1 END AnzahlMutationUmzug,
      CASE WHEN wizard_step_name = 'VERFUEGEN'
        THEN 1 END AnzahlMutationVerfuegen
    FROM wizard_step
    WHERE wizard_step_status = 'MUTIERT'
  ) Mutationen ON Mutationen.gesuch_id = gesuch.id

  LEFT OUTER JOIN (
                    SELECT DISTINCT
                      1 AS AnzahlMahnungen,
                      gesuch_id
                    FROM mahnung
                    WHERE timestamp_erstellt BETWEEN :fromDateTime AND :toDateTime
                          OR timestamp_abgeschlossen BETWEEN :fromDateTime AND :toDateTime
                          OR (:fromDateTime >= timestamp_erstellt AND
                              (:toDateTime <= timestamp_abgeschlossen OR timestamp_abgeschlossen IS NULL))
                  ) Mahnungen ON Mahnungen.gesuch_id = gesuch.id

  LEFT OUTER JOIN (
                    SELECT
                      1         AS AnzahlBeschwerde,
                      gesuch.id AS gesuch_id
                    FROM antrag_status_history
                      INNER JOIN gesuch ON gesuch.id = antrag_status_history.gesuch_id
                      INNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id
                    WHERE antrag_status_history.status IN ('BESCHWERDE_HAENGIG')
                          AND (
                            timestamp_von BETWEEN :fromDateTime AND :toDateTime
                            OR timestamp_bis BETWEEN :fromDateTime AND :toDateTime
                            OR (:fromDateTime >= timestamp_von AND (:toDateTime <= timestamp_bis OR timestamp_bis IS NULL))
                          )
                  ) Beschwerde ON Beschwerde.gesuch_id = gesuch.id

  LEFT OUTER JOIN (
                    SELECT
                      id,
                      1 AS AnzahlVerfuegungen,
                      kategorie_normal,
                      kategorie_max_einkommen,
                      kategorie_kein_pensum,
                      kategorie_zuschlag_zum_erwerbspensum,
                      kategorie_nicht_eintreten
                    FROM verfuegung
                    WHERE timestamp_erstellt BETWEEN :fromDateTime AND :toDateTime
                  ) Verfuegungen ON Verfuegungen.id = betreuung.verfuegung_id
WHERE (gesuchsperiode.id = :gesuchPeriodeID OR :gesuchPeriodeID IS NULL)
      AND (institution_stammdaten.betreuungsangebot_typ = 'TAGESSCHULE' OR :onlySchulamt = 0)
      AND (
        IFNULL(EingegangeneGesuch.AnzahlGesuchOnline, 0) = 1
        OR IFNULL(EingegangeneGesuch.AnzahlGesuchPapier, 0) = 1
        OR IFNULL(EingegangeneGesuch.AnzahlMutationOnline, 0) = 1
        OR IFNULL(EingegangeneGesuch.AnzahlMutationPapier, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationAbwesenheit, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationBetreuung, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationDokumente, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationEV, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationEwerbspensum, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationFamilienSitutation, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationFinanzielleSituation, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationFreigabe, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationGesuchErstellen, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationGesuchsteller, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationKinder, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationUmzug, 0) = 1
        OR IFNULL(Mutationen.AnzahlMutationVerfuegen, 0) = 1
        OR IFNULL(Mahnungen.AnzahlMahnungen, 0) = 1
        OR IFNULL(Beschwerde.AnzahlBeschwerde, 0) = 1
        OR IFNULL(Verfuegungen.AnzahlVerfuegungen, 0) = 1
        OR IFNULL(Verfuegungen.kategorie_normal, 0) = 1
        OR IFNULL(Verfuegungen.kategorie_max_einkommen, 0) = 1
        OR IFNULL(Verfuegungen.kategorie_kein_pensum, 0) = 1
        OR IFNULL(Verfuegungen.kategorie_zuschlag_zum_erwerbspensum, 0) = 1
        OR IFNULL(Verfuegungen.kategorie_nicht_eintreten, 0) = 1
      )