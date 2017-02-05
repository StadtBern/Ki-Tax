select
CONCAT_WS('.'
	,RIGHT(YEAR(gesuchsperiode.gueltig_ab), 2)
	,LPAD(fall.fall_nummer, 6 ,0)
	,kind_container.kind_nummer
	,betreuung.betreuung_nummer
) AS BGNummer
,CONCAT(IFNULL(CONCAT(traegerschaft.name, ' - '), ''), institution.name) AS Institution
,institution_stammdaten.betreuungsangebot_typ AS BetreuungsTyp
,CONCAT_WS('/',CAST(YEAR(gesuchsperiode.gueltig_ab) AS CHAR(4))
,CAST(
	YEAR(gesuchsperiode.gueltig_bis) AS CHAR(4))
	) AS Periode
,IFNULL(EingegangeneGesuch.AnzahlGesuchOnline, 0) AnzahlGesuchOnline
,IFNULL(EingegangeneGesuch.AnzahlGesuchPapier, 0) AnzahlGesuchPapier
,IFNULL(EingegangeneGesuch.AnzahlMutationOnline, 0) AnzahlMutationOnline
,IFNULL(EingegangeneGesuch.AnzahlMutationPapier, 0) AnzahlMutationPapier
,IFNULL(Mutationen.AnzahlMutationAbwesenheit, 0) AnzahlMutationAbwesenheit
,IFNULL(Mutationen.AnzahlMutationBetreuung, 0) AnzahlMutationBetreuung
,IFNULL(Mutationen.AnzahlMutationDokumente, 0) AnzahlMutationDokumente
,IFNULL(Mutationen.AnzahlMutationEV, 0) AnzahlMutationEV
,IFNULL(Mutationen.AnzahlMutationEwerbspensum, 0) AnzahlMutationEwerbspensum
,IFNULL(Mutationen.AnzahlMutationFamilienSitutation, 0) AnzahlMutationFamilienSitutation
,IFNULL(Mutationen.AnzahlMutationFinanzielleSituation, 0) AnzahlMutationFinanzielleSituation
,IFNULL(Mutationen.AnzahlMutationFreigabe, 0) AnzahlMutationFreigabe
,IFNULL(Mutationen.AnzahlMutationGesuchErstellen, 0) AnzahlMutationGesuchErstellen
,IFNULL(Mutationen.AnzahlMutationGesuchsteller, 0) AnzahlMutationGesuchsteller
,IFNULL(Mutationen.AnzahlMutationKinder, 0) AnzahlMutationKinder
,IFNULL(Mutationen.AnzahlMutationUmzug, 0) AnzahlMutationUmzug
,IFNULL(Mutationen.AnzahlMutationVerfuegen, 0) AnzahlMutationVerfuegen
,IFNULL(Mahnungen.AnzahlMahnungen, 0) AnzahlMahnungen
,IFNULL(Beschwerde.AnzahlBeschwerde, 0) AnzahlBeschwerde
,IFNULL(Verfuegungen.AnzahlVerfuegungen, 0) AnzahlVerfuegungen
,IFNULL(Verfuegungen.kategorie_normal, 0) AnzahlVerfuegungenNormal
,IFNULL(Verfuegungen.kategorie_max_einkommen, 0) AnzahlVerfuegungenMaxEinkommen
,IFNULL(Verfuegungen.kategorie_kein_pensum, 0) AnzahlVerfuegungenKeinPensum
,IFNULL(Verfuegungen.kategorie_zuschlag_zum_erwerbspensum, 0) AnzahlVerfuegungenZuschlagZumPensum
,IFNULL(Verfuegungen.kategorie_nicht_eintreten, 0) AnzahlVerfuegungenNichtEintreten

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
	CASE WHEN eingangsart = 'ONLINE' AND typ = 'GESUCH' THEN 1 END AnzahlGesuchOnline,
	CASE WHEN eingangsart = 'PAPIER' AND typ = 'GESUCH' THEN 1 END AnzahlGesuchPapier,
	CASE WHEN eingangsart = 'ONLINE' AND typ = 'MUTATION' THEN 1 END AnzahlMutationOnline,
	CASE WHEN eingangsart = 'PAPIER' AND typ = 'MUTATION' THEN 1 END AnzahlMutationPapier,
 	gesuch.id as gesuch_id
	from gesuch
	where eingangsdatum between :fromDate and :toDate
) EingegangeneGesuch on EingegangeneGesuch.gesuch_id = gesuch.id

left outer join
(
	select gesuch_id
	,CASE WHEN wizard_step_name = 'ABWESENHEIT' THEN 1 END AnzahlMutationAbwesenheit
	,CASE WHEN wizard_step_name = 'BETREUUNG' THEN 1 END AnzahlMutationBetreuung
	,CASE WHEN wizard_step_name = 'DOKUMENTE' THEN 1 END AnzahlMutationDokumente
	,CASE WHEN wizard_step_name = 'EINKOMMENSVERSCHLECHTERUNG' THEN 1 END AnzahlMutationEV
	,CASE WHEN wizard_step_name = 'ERWERBSPENSUM' THEN 1 END AnzahlMutationEwerbspensum
	,CASE WHEN wizard_step_name = 'FAMILIENSITUATION' THEN 1 END AnzahlMutationFamilienSitutation
	,CASE WHEN wizard_step_name = 'FINANZIELLE_SITUATION' THEN 1 END AnzahlMutationFinanzielleSituation
	,CASE WHEN wizard_step_name = 'FREIGABE' THEN 1 END AnzahlMutationFreigabe
	,CASE WHEN wizard_step_name = 'GESUCH_ERSTELLEN' THEN 1 END AnzahlMutationGesuchErstellen
	,CASE WHEN wizard_step_name = 'GESUCHSTELLER' THEN 1 END AnzahlMutationGesuchsteller
	,CASE WHEN wizard_step_name = 'KINDER' THEN 1 END AnzahlMutationKinder
	,CASE WHEN wizard_step_name = 'UMZUG' THEN 1 END AnzahlMutationUmzug
	,CASE WHEN wizard_step_name = 'VERFUEGEN' THEN 1 END AnzahlMutationVerfuegen
	from wizard_step
	where wizard_step_status = 'MUTIERT'
) Mutationen on Mutationen.gesuch_id = gesuch.id

left outer join (
	select distinct
	1 as AnzahlMahnungen,
	gesuch_id
	from mahnung
	where timestamp_erstellt between :fromDate and :toDate
	or timestamp_abgeschlossen between :fromDate and :toDate
	or (:fromDate >= timestamp_erstellt and (:toDate <= timestamp_abgeschlossen or timestamp_abgeschlossen is null))
) Mahnungen on Mahnungen.gesuch_id = gesuch.id

left outer join (
	select
	1 as AnzahlBeschwerde,
	gesuch.id as gesuch_id
	from antrag_status_history
	inner join gesuch on gesuch.id = antrag_status_history.gesuch_id
	inner join gesuchsperiode on gesuchsperiode.id = gesuch.gesuchsperiode_id
	where antrag_status_history.status IN ('BESCHWERDE_HAENGIG')
	and (
	timestamp_von between :fromDate and :toDate
	or timestamp_bis between :fromDate and :toDate
	or (:fromDate >= timestamp_von and (:toDate <= timestamp_bis or timestamp_bis is null))
	)
) Beschwerde on Beschwerde.gesuch_id = gesuch.id

left outer join(
	select
	id
	,1 as AnzahlVerfuegungen
	,kategorie_normal
	,kategorie_max_einkommen
	,kategorie_kein_pensum
	,kategorie_zuschlag_zum_erwerbspensum
	,kategorie_nicht_eintreten
	from verfuegung
	where timestamp_erstellt between :fromDate and :toDate
) Verfuegungen on Verfuegungen.id = betreuung.verfuegung_id
where gesuchsperiode.id = :gesuchPeriodeID
;