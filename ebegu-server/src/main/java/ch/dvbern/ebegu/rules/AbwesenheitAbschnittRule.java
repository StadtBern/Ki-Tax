package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Abwesenheit;
import ch.dvbern.ebegu.entities.AbwesenheitContainer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Regel für Abwesenheiten. Sie beachtet:
 * - Ab dem 31. Tag einer Abwesenheit (Krankheit oder Unfall des Kinds und bei Mutterschaft ausgeschlossen) entfällt der Gutschein.
 * 		Der Anspruch bleibt in dieser Zeit bestehen. D.h. ab dem 31. Tag einer Abwesenheit, wird den Eltern der Volltarif verrechnet.
 * - Hier wird mit Tagen und nicht mit Nettoarbeitstage gerechnet. D.h. eine Abwesenheit von 30 Tagen ist ok. Beim 31. Tag entfällt der Gutschein.
 * - Wann dieses Ereignis gemeldet wird, spielt keine Rolle.
 * Verweis 16.14.4
 */
public class AbwesenheitAbschnittRule extends AbstractAbschnittRule {

	public AbwesenheitAbschnittRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.ABWESENHEIT, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	/**
	 * Die Abwesenheiten der Betreuung werden zuerst nach gueltigkeit sortiert. Danach suchen wir die erste lange Abweseneheit und erstellen
	 * die 2 entsprechenden Zeitabschnitte. Alle anderen Abwesenheiten werden nicht beruecksichtigt
	 * Sollte es keine lange Abwesenheit geben, wird eine leere Liste zurueckgegeben
	 * Nur fuer Betreuungen die isAngebotJugendamtKleinkind
	 */
	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> resultlist = new ArrayList<>();
		if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {

			final List<AbwesenheitContainer> sortedAbwesenheiten = betreuung.getAbwesenheitContainers().stream().sorted().collect(Collectors.toList());
			for (final AbwesenheitContainer abwesenheit : sortedAbwesenheiten) {
				final Abwesenheit abwesenheitJA = abwesenheit.getAbwesenheitJA();
				if (abwesenheitJA != null && exceedsAbwesenheitTimeLimit(abwesenheitJA)) {
					LocalDate volltarifStart = calculateStartVolltarif(abwesenheitJA);
					LocalDate volltarifEnd = abwesenheitJA.getGueltigkeit().getGueltigBis();
					VerfuegungZeitabschnitt abschnitt = createAbwesenheitZeitAbschnitte(volltarifStart, volltarifEnd);
					resultlist.add(abschnitt);
				}
			}
		}
		return resultlist;
	}

	/**
	 * Es werden 2 Zeitabschnitte erstellt: [START_PERIODE, START_VOLLTARIF - 1Tag] und [START_VOLLTARIF, ENDE_PERIODE]
	 */
	private VerfuegungZeitabschnitt createAbwesenheitZeitAbschnitte(@Nonnull LocalDate volltarifStart, @Nonnull LocalDate volltarifEnd) {


		final VerfuegungZeitabschnitt zeitabschnitt2 = new VerfuegungZeitabschnitt(
			new DateRange(volltarifStart, volltarifEnd));
		zeitabschnitt2.setLongAbwesenheit(true);
		return zeitabschnitt2;
	}

	@NotNull
	private LocalDate calculateStartVolltarif(@Nonnull Abwesenheit abwesenheit) {
		return abwesenheit.getGueltigkeit().getGueltigAb().plusDays(Constants.ABWESENHEIT_DAYS_LIMIT);
	}

	/**
	 * True wenn die Gueltigkeit der Abwesenheit laenger als 30 Tage ist
	 */
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	@NotNull
	private boolean exceedsAbwesenheitTimeLimit(@Nonnull Abwesenheit abwesenheit) {
		return (abwesenheit.getGueltigkeit().getDays()) > Constants.ABWESENHEIT_DAYS_LIMIT;
	}
}
