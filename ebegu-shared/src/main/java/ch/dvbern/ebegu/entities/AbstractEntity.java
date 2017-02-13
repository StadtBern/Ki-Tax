package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.util.AbstractEntityListener;
import ch.dvbern.ebegu.util.Constants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.Hibernate;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ClassReferencesSubclass")
@MappedSuperclass
@Audited
@EntityListeners(AbstractEntityListener.class)
//Mappings for the native quries used by the report
@SqlResultSetMappings({
	@SqlResultSetMapping(name = "GesuchStichtagDataRowMapping", classes = {
		@ConstructorResult(targetClass = GesuchStichtagDataRow.class,
			columns = {
				@ColumnResult(name = "bgNummer", type = String.class),
				@ColumnResult(name = "institution", type = String.class),
				@ColumnResult(name = "betreuungsTyp", type = String.class),
				@ColumnResult(name = "periode", type = String.class),
				@ColumnResult(name = "nichtFreigegeben", type = Integer.class),
				@ColumnResult(name = "mahnungen", type = Integer.class),
				@ColumnResult(name = "beschwerde", type = Integer.class)}
		)}
	),
	@SqlResultSetMapping(name = "GesuchZeitraumDataRowMapping", classes = {
		@ConstructorResult(targetClass = GesuchZeitraumDataRow.class,
			columns = {
				@ColumnResult(name = "bgNummer", type = String.class),
				@ColumnResult(name = "institution", type = String.class),
				@ColumnResult(name = "betreuungsTyp", type = String.class),
				@ColumnResult(name = "periode", type = String.class),
				@ColumnResult(name = "anzahlGesuchOnline", type = Integer.class),
				@ColumnResult(name = "anzahlGesuchPapier", type = Integer.class),
				@ColumnResult(name = "anzahlMutationOnline", type = Integer.class),
				@ColumnResult(name = "anzahlMutationPapier", type = Integer.class),
				@ColumnResult(name = "anzahlMutationAbwesenheit", type = Integer.class),
				@ColumnResult(name = "anzahlMutationBetreuung", type = Integer.class),
				@ColumnResult(name = "anzahlMutationDokumente", type = Integer.class),
				@ColumnResult(name = "anzahlMutationEV", type = Integer.class),
				@ColumnResult(name = "anzahlMutationEwerbspensum", type = Integer.class),
				@ColumnResult(name = "anzahlMutationFamilienSitutation", type = Integer.class),
				@ColumnResult(name = "anzahlMutationFinanzielleSituation", type = Integer.class),
				@ColumnResult(name = "anzahlMutationFreigabe", type = Integer.class),
				@ColumnResult(name = "anzahlMutationGesuchErstellen", type = Integer.class),
				@ColumnResult(name = "anzahlMutationGesuchsteller", type = Integer.class),
				@ColumnResult(name = "anzahlMutationKinder", type = Integer.class),
				@ColumnResult(name = "anzahlMutationUmzug", type = Integer.class),
				@ColumnResult(name = "anzahlMutationVerfuegen", type = Integer.class),
				@ColumnResult(name = "anzahlMahnungen", type = Integer.class),
				@ColumnResult(name = "anzahlBeschwerde", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungen", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenNormal", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenMaxEinkommen", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenKeinPensum", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenZuschlagZumPensum", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenNichtEintreten", type = Integer.class)}
		)}
	)
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "GesuchStichtagNativeSQLQuery",
		query = "SELECT\n" +
			"\tCONCAT_WS('.', RIGHT(YEAR(gesuchsperiode.gueltig_ab), 2), LPAD(fall.fall_nummer, 6, 0), kind_container.kind_nummer,\n" +
			"\t\t\t  betreuung.betreuung_nummer)                                   AS BGNummer,\n" +
			"\tCONCAT(IFNULL(CONCAT(traegerschaft.name, ' - '), ''), institution.name) AS Institution,\n" +
			"\tinstitution_stammdaten.betreuungsangebot_typ                            AS BetreuungsTyp,\n" +
			"\tCONCAT_WS('/', CAST(YEAR(gesuchsperiode.gueltig_ab) AS CHAR(4)),\n" +
			"\t\t\t  CAST(YEAR(gesuchsperiode.gueltig_bis) AS CHAR(4)))            AS Periode,\n" +
			"\tIFNULL(NichtFreigegebeneGesuch.Anzahl, 0)                               AS NichtFreigegeben,\n" +
			"\tIFNULL(Mahnungen.Anzahl, 0)                                             AS Mahnungen,\n" +
			"\tIFNULL(Beschwerde.Anzahl, 0)                                            AS Beschwerde\n" +
			"FROM kind_container\n" +
			"\tINNER JOIN kind ON kind.id = kind_container.kindja_id\n" +
			"\tINNER JOIN betreuung ON betreuung.kind_id = kind_container.id\n" +
			"\tINNER JOIN institution_stammdaten ON institution_stammdaten.id = betreuung.institution_stammdaten_id\n" +
			"\tINNER JOIN institution ON institution.id = institution_stammdaten.institution_id\n" +
			"\tLEFT OUTER JOIN traegerschaft ON traegerschaft.id = institution.traegerschaft_id\n" +
			"\tINNER JOIN gesuch ON gesuch.id = kind_container.gesuch_id\n" +
			"\tINNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id\n" +
			"\tINNER JOIN fall ON fall.id = gesuch.fall_id\n" +
			"\tLEFT OUTER JOIN (SELECT\n" +
			"\t\t\t\t\t\t 1 AS Anzahl,\n" +
			"\t\t\t\t\t\t gesuch.id AS gesuch_id\n" +
			"\t\t\t\t\t FROM antrag_status_history\n" +
			"\t\t\t\t\t\t INNER JOIN gesuch ON gesuch.id = antrag_status_history.gesuch_id\n" +
			"\t\t\t\t\t\t INNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id\n" +
			"\t\t\t\t\t WHERE :stichTagDate >= timestamp_von AND (:stichTagDate <= timestamp_bis OR timestamp_bis IS NULL)\n" +
			"\t\t\t\t\t\t   AND antrag_status_history.status IN\n" +
			"\t\t\t\t\t\t\t   ('IN_BEARBEITUNG_GS', 'FREIGABEQUITTUNG')) NichtFreigegebeneGesuch\n" +
			"\t\tON NichtFreigegebeneGesuch.gesuch_id = gesuch.id\n" +
			"\tLEFT OUTER JOIN (SELECT DISTINCT\n" +
			"\t\t\t\t\t\t 1 AS Anzahl,\n" +
			"\t\t\t\t\t\t gesuch_id\n" +
			"\t\t\t\t\t FROM mahnung\n" +
			"\t\t\t\t\t WHERE :stichTagDate >= timestamp_erstellt AND\n" +
			"\t\t\t\t\t\t   (:stichTagDate <= timestamp_abgeschlossen OR timestamp_abgeschlossen IS NULL)) Mahnungen\n" +
			"\t\tON Mahnungen.gesuch_id = gesuch.id\n" +
			"\tLEFT OUTER JOIN (SELECT\n" +
			"\t\t\t\t\t\t 1         AS Anzahl,\n" +
			"\t\t\t\t\t\t gesuch.id AS gesuch_id\n" +
			"\t\t\t\t\t FROM antrag_status_history\n" +
			"\t\t\t\t\t\t INNER JOIN gesuch ON gesuch.id = antrag_status_history.gesuch_id\n" +
			"\t\t\t\t\t\t INNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id\n" +
			"\t\t\t\t\t WHERE antrag_status_history.status IN ('BESCHWERDE_HAENGIG') AND :stichTagDate >= timestamp_von AND\n" +
			"\t\t\t\t\t\t   (:stichTagDate <= timestamp_bis OR timestamp_bis IS NULL)) Beschwerde\n" +
			"\t\tON Beschwerde.gesuch_id = gesuch.id\n" +
			"WHERE (gesuchsperiode.id = :gesuchPeriodeID OR :gesuchPeriodeID IS NULL) AND\n" +
			"\t  (institution_stammdaten.betreuungsangebot_typ = 'TAGESSCHULE' OR :onlySchulamt = 0) AND\n" +
			"\t  (IFNULL(NichtFreigegebeneGesuch.Anzahl, 0) = 1 OR IFNULL(Mahnungen.Anzahl, 0) = 1 OR\n" +
			"\t   IFNULL(Beschwerde.Anzahl, 0) = 1)"
		, resultSetMapping = "GesuchStichtagDataRowMapping"),
	@NamedNativeQuery(name = "GesuchZeitraumNativeSQLQuery", resultSetMapping = "GesuchZeitraumDataRowMapping",
		query = "SELECT\n" +
			"\tCONCAT_WS('.'\n" +
			"\t, RIGHT(YEAR(gesuchsperiode.gueltig_ab), 2)\n" +
			"\t, LPAD(fall.fall_nummer, 6, 0)\n" +
			"\t, kind_container.kind_nummer\n" +
			"\t, betreuung.betreuung_nummer\n" +
			"\t)                                                                       AS BGNummer,\n" +
			"\tCONCAT(IFNULL(CONCAT(traegerschaft.name, ' - '), ''), institution.name) AS Institution,\n" +
			"\tinstitution_stammdaten.betreuungsangebot_typ                            AS BetreuungsTyp,\n" +
			"\tCONCAT_WS('/', CAST(YEAR(gesuchsperiode.gueltig_ab) AS CHAR(4))\n" +
			"\t, CAST(\n" +
			"\t\t\t\t  YEAR(gesuchsperiode.gueltig_bis) AS CHAR(4))\n" +
			"\t)                                                                       AS Periode,\n" +
			"\tIFNULL(EingegangeneGesuch.AnzahlGesuchOnline, 0)                           AnzahlGesuchOnline,\n" +
			"\tIFNULL(EingegangeneGesuch.AnzahlGesuchPapier, 0)                           AnzahlGesuchPapier,\n" +
			"\tIFNULL(EingegangeneGesuch.AnzahlMutationOnline, 0)                         AnzahlMutationOnline,\n" +
			"\tIFNULL(EingegangeneGesuch.AnzahlMutationPapier, 0)                         AnzahlMutationPapier,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationAbwesenheit, 0)                            AnzahlMutationAbwesenheit,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationBetreuung, 0)                              AnzahlMutationBetreuung,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationDokumente, 0)                              AnzahlMutationDokumente,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationEV, 0)                                     AnzahlMutationEV,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationEwerbspensum, 0)                           AnzahlMutationEwerbspensum,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationFamilienSitutation, 0)                     AnzahlMutationFamilienSitutation,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationFinanzielleSituation, 0)                   AnzahlMutationFinanzielleSituation,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationFreigabe, 0)                               AnzahlMutationFreigabe,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationGesuchErstellen, 0)                        AnzahlMutationGesuchErstellen,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationGesuchsteller, 0)                          AnzahlMutationGesuchsteller,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationKinder, 0)                                 AnzahlMutationKinder,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationUmzug, 0)                                  AnzahlMutationUmzug,\n" +
			"\tIFNULL(Mutationen.AnzahlMutationVerfuegen, 0)                              AnzahlMutationVerfuegen,\n" +
			"\tIFNULL(Mahnungen.AnzahlMahnungen, 0)                                       AnzahlMahnungen,\n" +
			"\tIFNULL(Beschwerde.AnzahlBeschwerde, 0)                                     AnzahlBeschwerde,\n" +
			"\tIFNULL(Verfuegungen.AnzahlVerfuegungen, 0)                                 AnzahlVerfuegungen,\n" +
			"\tIFNULL(Verfuegungen.kategorie_normal, 0)                                   AnzahlVerfuegungenNormal,\n" +
			"\tIFNULL(Verfuegungen.kategorie_max_einkommen, 0)                            AnzahlVerfuegungenMaxEinkommen,\n" +
			"\tIFNULL(Verfuegungen.kategorie_kein_pensum, 0)                              AnzahlVerfuegungenKeinPensum,\n" +
			"\tIFNULL(Verfuegungen.kategorie_zuschlag_zum_erwerbspensum, 0)               AnzahlVerfuegungenZuschlagZumPensum,\n" +
			"\tIFNULL(Verfuegungen.kategorie_nicht_eintreten, 0)                          AnzahlVerfuegungenNichtEintreten\n" +
			"\n" +
			"FROM kind_container\n" +
			"\tINNER JOIN kind ON kind.id = kind_container.kindja_id\n" +
			"\tINNER JOIN betreuung ON betreuung.kind_id = kind_container.id\n" +
			"\tINNER JOIN institution_stammdaten ON institution_stammdaten.id = betreuung.institution_stammdaten_id\n" +
			"\tINNER JOIN institution ON institution.id = institution_stammdaten.institution_id\n" +
			"\tLEFT OUTER JOIN traegerschaft ON traegerschaft.id = institution.traegerschaft_id\n" +
			"\tINNER JOIN gesuch ON gesuch.id = kind_container.gesuch_id\n" +
			"\tINNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id\n" +
			"\tINNER JOIN fall ON fall.id = gesuch.fall_id\n" +
			"\n" +
			"\tLEFT OUTER JOIN (\n" +
			"\t\t\t\t\t\tSELECT\n" +
			"\t\t\t\t\t\t\tCASE WHEN eingangsart = 'ONLINE' AND typ = 'GESUCH'\n" +
			"\t\t\t\t\t\t\t\tTHEN 1 END AnzahlGesuchOnline,\n" +
			"\t\t\t\t\t\t\tCASE WHEN eingangsart = 'PAPIER' AND typ = 'GESUCH'\n" +
			"\t\t\t\t\t\t\t\tTHEN 1 END AnzahlGesuchPapier,\n" +
			"\t\t\t\t\t\t\tCASE WHEN eingangsart = 'ONLINE' AND typ = 'MUTATION'\n" +
			"\t\t\t\t\t\t\t\tTHEN 1 END AnzahlMutationOnline,\n" +
			"\t\t\t\t\t\t\tCASE WHEN eingangsart = 'PAPIER' AND typ = 'MUTATION'\n" +
			"\t\t\t\t\t\t\t\tTHEN 1 END AnzahlMutationPapier,\n" +
			"\t\t\t\t\t\t\tgesuch.id AS   gesuch_id\n" +
			"\t\t\t\t\t\tFROM gesuch\n" +
			"\t\t\t\t\t\tWHERE eingangsdatum BETWEEN :fromDate AND :toDate\n" +
			"\t\t\t\t\t) EingegangeneGesuch ON EingegangeneGesuch.gesuch_id = gesuch.id\n" +
			"\n" +
			"\tLEFT OUTER JOIN\n" +
			"\t(\n" +
			"\t\tSELECT\n" +
			"\t\t\tgesuch_id,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'ABWESENHEIT'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationAbwesenheit,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'BETREUUNG'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationBetreuung,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'DOKUMENTE'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationDokumente,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'EINKOMMENSVERSCHLECHTERUNG'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationEV,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'ERWERBSPENSUM'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationEwerbspensum,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'FAMILIENSITUATION'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationFamilienSitutation,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'FINANZIELLE_SITUATION'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationFinanzielleSituation,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'FREIGABE'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationFreigabe,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'GESUCH_ERSTELLEN'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationGesuchErstellen,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'GESUCHSTELLER'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationGesuchsteller,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'KINDER'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationKinder,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'UMZUG'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationUmzug,\n" +
			"\t\t\tCASE WHEN wizard_step_name = 'VERFUEGEN'\n" +
			"\t\t\t\tTHEN 1 END AnzahlMutationVerfuegen\n" +
			"\t\tFROM wizard_step\n" +
			"\t\tWHERE wizard_step_status = 'MUTIERT'\n" +
			"\t) Mutationen ON Mutationen.gesuch_id = gesuch.id\n" +
			"\n" +
			"\tLEFT OUTER JOIN (\n" +
			"\t\t\t\t\t\tSELECT DISTINCT\n" +
			"\t\t\t\t\t\t\t1 AS AnzahlMahnungen,\n" +
			"\t\t\t\t\t\t\tgesuch_id\n" +
			"\t\t\t\t\t\tFROM mahnung\n" +
			"\t\t\t\t\t\tWHERE timestamp_erstellt BETWEEN :fromDateTime AND :toDateTime\n" +
			"\t\t\t\t\t\t\t  OR timestamp_abgeschlossen BETWEEN :fromDateTime AND :toDateTime\n" +
			"\t\t\t\t\t\t\t  OR (:fromDateTime >= timestamp_erstellt AND\n" +
			"\t\t\t\t\t\t\t\t  (:toDateTime <= timestamp_abgeschlossen OR timestamp_abgeschlossen IS NULL))\n" +
			"\t\t\t\t\t) Mahnungen ON Mahnungen.gesuch_id = gesuch.id\n" +
			"\n" +
			"\tLEFT OUTER JOIN (\n" +
			"\t\t\t\t\t\tSELECT\n" +
			"\t\t\t\t\t\t\t1         AS AnzahlBeschwerde,\n" +
			"\t\t\t\t\t\t\tgesuch.id AS gesuch_id\n" +
			"\t\t\t\t\t\tFROM antrag_status_history\n" +
			"\t\t\t\t\t\t\tINNER JOIN gesuch ON gesuch.id = antrag_status_history.gesuch_id\n" +
			"\t\t\t\t\t\t\tINNER JOIN gesuchsperiode ON gesuchsperiode.id = gesuch.gesuchsperiode_id\n" +
			"\t\t\t\t\t\tWHERE antrag_status_history.status IN ('BESCHWERDE_HAENGIG')\n" +
			"\t\t\t\t\t\t\t  AND (\n" +
			"\t\t\t\t\t\t\t\t  timestamp_von BETWEEN :fromDateTime AND :toDateTime\n" +
			"\t\t\t\t\t\t\t\t  OR timestamp_bis BETWEEN :fromDateTime AND :toDateTime\n" +
			"\t\t\t\t\t\t\t\t  OR (:fromDateTime >= timestamp_von AND\n" +
			"\t\t\t\t\t\t\t\t\t  (:toDateTime <= timestamp_bis OR timestamp_bis IS NULL))\n" +
			"\t\t\t\t\t\t\t  )\n" +
			"\t\t\t\t\t) Beschwerde ON Beschwerde.gesuch_id = gesuch.id\n" +
			"\n" +
			"\tLEFT OUTER JOIN (\n" +
			"\t\t\t\t\t\tSELECT\n" +
			"\t\t\t\t\t\t\tid,\n" +
			"\t\t\t\t\t\t\t1 AS AnzahlVerfuegungen,\n" +
			"\t\t\t\t\t\t\tkategorie_normal,\n" +
			"\t\t\t\t\t\t\tkategorie_max_einkommen,\n" +
			"\t\t\t\t\t\t\tkategorie_kein_pensum,\n" +
			"\t\t\t\t\t\t\tkategorie_zuschlag_zum_erwerbspensum,\n" +
			"\t\t\t\t\t\t\tkategorie_nicht_eintreten\n" +
			"\t\t\t\t\t\tFROM verfuegung\n" +
			"\t\t\t\t\t\tWHERE timestamp_erstellt BETWEEN :fromDateTime AND :toDateTime\n" +
			"\t\t\t\t\t) Verfuegungen ON Verfuegungen.id = betreuung.verfuegung_id\n" +
			"WHERE (gesuchsperiode.id = :gesuchPeriodeID OR :gesuchPeriodeID IS NULL)\n" +
			"\t  AND (institution_stammdaten.betreuungsangebot_typ = 'TAGESSCHULE' OR :onlySchulamt = 0)\n" +
			"\t  AND (\n" +
			"\t\t  IFNULL(EingegangeneGesuch.AnzahlGesuchOnline, 0) = 1\n" +
			"\t\t  OR IFNULL(EingegangeneGesuch.AnzahlGesuchPapier, 0) = 1\n" +
			"\t\t  OR IFNULL(EingegangeneGesuch.AnzahlMutationOnline, 0) = 1\n" +
			"\t\t  OR IFNULL(EingegangeneGesuch.AnzahlMutationPapier, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationAbwesenheit, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationBetreuung, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationDokumente, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationEV, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationEwerbspensum, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationFamilienSitutation, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationFinanzielleSituation, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationFreigabe, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationGesuchErstellen, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationGesuchsteller, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationKinder, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationUmzug, 0) = 1\n" +
			"\t\t  OR IFNULL(Mutationen.AnzahlMutationVerfuegen, 0) = 1\n" +
			"\t\t  OR IFNULL(Mahnungen.AnzahlMahnungen, 0) = 1\n" +
			"\t\t  OR IFNULL(Beschwerde.AnzahlBeschwerde, 0) = 1\n" +
			"\t\t  OR IFNULL(Verfuegungen.AnzahlVerfuegungen, 0) = 1\n" +
			"\t\t  OR IFNULL(Verfuegungen.kategorie_normal, 0) = 1\n" +
			"\t\t  OR IFNULL(Verfuegungen.kategorie_max_einkommen, 0) = 1\n" +
			"\t\t  OR IFNULL(Verfuegungen.kategorie_kein_pensum, 0) = 1\n" +
			"\t\t  OR IFNULL(Verfuegungen.kategorie_zuschlag_zum_erwerbspensum, 0) = 1\n" +
			"\t\t  OR IFNULL(Verfuegungen.kategorie_nicht_eintreten, 0) = 1\n" +
			"\t  )")
})
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = -979317154050183445L;

	@Id
	@Column(unique = true, nullable = false, updatable = false, length = Constants.UUID_LENGTH)
	@Size(min = Constants.UUID_LENGTH, max = Constants.UUID_LENGTH)
	private String id;

	@Version
	@NotNull
	private long version;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false)
	private LocalDateTime timestampErstellt;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false)
	private LocalDateTime timestampMutiert;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	// wir verwenden hier die Hibernate spezifische Annotation, weil diese vererbt wird
	@Size(max = Constants.UUID_LENGTH)
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userErstellt;

	@Size(max = Constants.UUID_LENGTH)
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userMutiert;

	@Column(nullable = true, length = Constants.UUID_LENGTH)
	@Size(min = Constants.UUID_LENGTH, max = Constants.UUID_LENGTH)
	private String vorgaengerId;

	public AbstractEntity() {
		//da wir teilweise schon eine id brauchen bevor die Entities gespeichert werden initialisieren wir die uuid hier
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(@Nullable String id) {
		this.id = id;
	}

	// Nullable, da erst im PrePersist gesetzt
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public LocalDateTime getTimestampErstellt() {
		return timestampErstellt;
	}

	public void setTimestampErstellt(LocalDateTime timestampErstellt) {
		this.timestampErstellt = timestampErstellt;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public LocalDateTime getTimestampMutiert() {
		return timestampMutiert;
	}

	public void setTimestampMutiert(LocalDateTime timestampMutiert) {
		this.timestampMutiert = timestampMutiert;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public String getUserErstellt() {
		return userErstellt;
	}

	public void setUserErstellt(@Nonnull String userErstellt) {
		this.userErstellt = userErstellt;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public String getUserMutiert() {
		return userMutiert;
	}

	public void setUserMutiert(@Nonnull String userMutiert) {
		this.userMutiert = userMutiert;
	}

	public String getVorgaengerId() {
		return vorgaengerId;
	}

	public void setVorgaengerId(String vorgaengerId) {
		this.vorgaengerId = vorgaengerId;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@SuppressFBWarnings(value = "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS", justification = "Es wird Hibernate.getClass genutzt um von Proxies (LazyInit) die konkrete Klasse zu erhalten")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}

		AbstractEntity that = (AbstractEntity) o;

		Objects.requireNonNull(getId());
		Objects.requireNonNull(that.getId());

		return getId().equals(that.getId());
	}

	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	/**
	 * @return true wenn das entity noch nicht in der DB gespeichert wurde (i.e. keinen timestamp gesetzt hat)
	 */
	public boolean isNew() {
		return timestampErstellt == null;
	}

	/**
	 * //todo team probably delete this
	 * Hilfsmethode fuer toString(): Wenn beim Debugging eine JPA-Referenz schon detached ist,
	 * kann nicht mehr auf den Wert zugegriffen werden und es kommt eine Exception.
	 * Diese Methode faengt die Exception ab und gibt einen fixen Text zurueck.
	 * <pre>
	 * {@code
	 * 	public String toString() {
	 * 		return MoreObjects.toStringHelper(this)
	 * 			.add("id", getId())
	 * 			.add("kontaktperson", getSilent(() -> kontaktperson))
	 * 			.add("kind", getSilent(() -> kind))
	 * 			.toString();
	 * 	}
	 * }
	 * </pre>
	 */
/*	protected <T extends Serializable> String getSilent(Supplier<T> supplier) {
		try {
			return String.valueOf(supplier.get());
		} catch (RuntimeException ignored) {
			return "<unknown>";
		}
	}*/


	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public boolean hasVorgaenger() {
		return vorgaengerId != null;
	}

	public AbstractEntity copyForMutation(AbstractEntity mutation) {
		mutation.setVorgaengerId(this.getId());
		return mutation;
	}
}
