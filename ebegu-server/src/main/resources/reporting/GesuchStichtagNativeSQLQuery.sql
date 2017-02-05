select
CONCAT_WS('.'
,RIGHT(YEAR(gesuchsperiode.gueltig_ab),2)
,LPAD(fall.fall_nummer,6,0)
,kind_container.kind_nummer
,betreuung.betreuung_nummer
) AS BGNummer
,CONCAT(IFNULL(CONCAT(traegerschaft.name, ' - '), ''), institution.name) AS Institution
,institution_stammdaten.betreuungsangebot_typ AS BetreuungsTyp
,CONCAT_WS('/',CAST(YEAR(gesuchsperiode.gueltig_ab) AS CHAR(4))
,CAST(
YEAR(gesuchsperiode.gueltig_bis) AS CHAR(4))
) AS Periode
,IFNULL(NichtFreigegebeneGesuch.Anzahl, 0) NichtFreigegeben
,IFNULL(Mahnungen.Anzahl, 0) Mahnungen
,IFNULL(Beschwerde.Anzahl, 0) Beschwerde

from kind_container
inner join kind on kind.id = kind_container.kindja_id
inner join betreuung on betreuung.kind_id = kind_container.id
inner join institution_stammdaten on institution_stammdaten.id = betreuung.institution_stammdaten_id
inner join institution on institution.id = institution_stammdaten.institution_id
left outer join traegerschaft on traegerschaft.id = institution.traegerschaft_id
inner join gesuch on gesuch.id = kind_container.gesuch_id
inner join gesuchsperiode on gesuchsperiode.id = gesuch.gesuchsperiode_id
inner join fall on fall.id = gesuch.fall_id

left outer join (
  select
  1 as Anzahl,
  gesuch.id as gesuch_id
  from antrag_status_history
  inner join gesuch on gesuch.id = antrag_status_history.gesuch_id
  inner join gesuchsperiode on gesuchsperiode.id = gesuch.gesuchsperiode_id
  where :stichTagDate >= timestamp_von
  and (:stichTagDate <= timestamp_bis or timestamp_bis is null)
  and antrag_status_history.status IN ('IN_BEARBEITUNG_GS','FREIGABEQUITTUNG')
) NichtFreigegebeneGesuch on NichtFreigegebeneGesuch.gesuch_id = gesuch.id

left outer join (
  select distinct
  1 as Anzahl,
  gesuch_id
  from mahnung
  where :stichTagDate >= timestamp_erstellt
  and (:stichTagDate <= timestamp_abgeschlossen or timestamp_abgeschlossen is null)
) Mahnungen on Mahnungen.gesuch_id = gesuch.id

left outer join (
  select
  1 as Anzahl,
  gesuch.id as gesuch_id
  from antrag_status_history
  inner join gesuch on gesuch.id = antrag_status_history.gesuch_id
  inner join gesuchsperiode on gesuchsperiode.id = gesuch.gesuchsperiode_id
  where antrag_status_history.status IN ('BESCHWERDE_HAENGIG')
  and :stichTagDate >= timestamp_von
  and (:stichTagDate <= timestamp_bis or timestamp_bis is null)
) Beschwerde on Beschwerde.gesuch_id = gesuch.id

where gesuchsperiode.id = :gesuchPeriodeID
