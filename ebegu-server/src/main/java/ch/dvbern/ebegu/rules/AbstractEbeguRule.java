package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.VerfuegungZeitabschnittComparator;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.*;

/**
 * This defines a Rule that has a unique Name given by RuleKey. The Rule is valid for a specified validityPeriod and
 * is of a given type
 */
public abstract class AbstractEbeguRule implements Rule {

	/**
	 * This is the name of the Rule, Can be used to create messages etc.
	 */
	private RuleKey ruleKey;

	private RuleType ruleType;

	private DateRange validityPeriod;


	public AbstractEbeguRule(@Nonnull RuleKey ruleKey, @Nonnull RuleType ruleType, @Nonnull DateRange validityPeriod) {
		this.ruleKey = ruleKey;
		this.ruleType = ruleType;
		this.validityPeriod = validityPeriod;
	}

	@Override
	@Nonnull
	public LocalDate validFrom() {
		return validityPeriod.getGueltigAb();
	}

	@Override
	@Nonnull
	public LocalDate validTo() {
		return validityPeriod.getGueltigBis();
	}


	@Override
	@Nonnull
	public RuleType getRuleType() {
		return ruleType;
	}

	@Override
	@Nonnull
	public RuleKey getRuleKey() {
		return ruleKey;
	}

	/**
	 * Zuerst muessen die neuen Zeitabschnitte aus den Daten der aktuellen Rule zusammengestellt werden:
	 * In dieser Funktion muss sichergestellt werden, dass in der neuen Liste keine Ueberschneidungen mehr bestehen
	 */
	@Nonnull
	protected abstract Collection<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);

	/**
	 * Fuehrt die eigentliche Rule auf einem einzelnen Zeitabschnitt aus.
	 * Hier kann ich davon ausgehen, dass die Zeitabschnitte schon validiert und gemergt sind.
	 */
	protected abstract void executeRule(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt);

	/**
	 * Hauptmethode der Regelberechnung. Diese wird von Aussen aufgerufen
     */
	@Nonnull
	@Override
	public final List<VerfuegungZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {

		// Zuerst muessen die neuen Zeitabschnitte aus den Daten meiner Rule zusammengestellt werden:
		// In dieser Funktion muss sichergestellt werden, dass in der neuen Liste keine Ueberschneidungen mehr bestehen
		Collection<VerfuegungZeitabschnitt> erwerbspensumAbschnitte = createVerfuegungsZeitabschnitte(betreuungspensumContainer, zeitabschnitte, finSitResultatDTO);

		// Jetzt muessen diese mit den bestehenden Zeitabschnitten aus früheren Rules gemergt werden
		List<VerfuegungZeitabschnitt> mergedZeitabschnitte = mergeZeitabschnitte(zeitabschnitte, (List<VerfuegungZeitabschnitt>) erwerbspensumAbschnitte);

		// Die Zeitabschnitte (jetzt ohne Überschneidungen) validieren:
		// - Muss innerhalb Gesuchsperiode sein
		// - Müssen sich unterscheiden (d.h. 20+20 vs 40 soll nur einen Schnitz geben)
		Gesuchsperiode gesuchsperiode = betreuungspensumContainer.extractGesuchsperiode();
		List<VerfuegungZeitabschnitt> validZeitabschnitte = validateZeitabschnitte(mergedZeitabschnitte, gesuchsperiode);

		// Die eigentliche Rule anwenden
		for (VerfuegungZeitabschnitt zeitabschnitt : validZeitabschnitte) {
			executeRule(betreuungspensumContainer, zeitabschnitt);
		}
		return validZeitabschnitte;
	}

	/**
	 * Prüft, dass die Zeitabschnitte innerhalb der Gesuchperiode liegen (und kürzt sie falls nötig bzw. lässt
	 * Zeitschnitze weg, welche ganz ausserhalb der Periode liegen)
	 * Stellt ausserdem sicher, dass zwei aufeinander folgende Zeitabschnitte nie dieselben Daten haben. Falls
	 * dies der Fall wäre, werden sie zu einem neuen Schnitz gemergt.
     */
	@Nonnull
	private List<VerfuegungZeitabschnitt> validateZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> mergedZeitabschnitte, @Nonnull Gesuchsperiode gesuchsperiode) {
		List<VerfuegungZeitabschnitt> validZeitabschnitte = new LinkedList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : mergedZeitabschnitte) {
			// Zuerst überprüfen, ob der Zeitabschnitt innerhalb der Gesuchsperiode liegt
			boolean startsBefore = zeitabschnitt.getGueltigkeit().startsBefore(gesuchsperiode.getGueltigkeit());
			boolean endsAfter = zeitabschnitt.getGueltigkeit().endsAfter(gesuchsperiode.getGueltigkeit());
			if (startsBefore || endsAfter) {
				boolean zeitabschnittInPeriode = false;
				if (startsBefore) {
					// Datum Von liegt vor der Periode
					// Falls Datum Bis ebenfalls vor der Periode liegt, kann der Abschnitt gelöscht werden, ansonsten muss er verkürzt werden
					if (zeitabschnitt.getGueltigkeit().getGueltigBis().isAfter(gesuchsperiode.getGueltigkeit().getGueltigAb())) {
						zeitabschnitt.getGueltigkeit().setGueltigAb(gesuchsperiode.getGueltigkeit().getGueltigAb());
						zeitabschnittInPeriode = true;
					}
				}
				if (endsAfter) {
					// Datum Bis liegt nach der Periode
					// Falls Datum Von auch schon nach der Periode lag, kann der Abschnitt gelöscht werden, ansonsten muss er verkürzt werden
					if (zeitabschnitt.getGueltigkeit().getGueltigAb().isBefore(gesuchsperiode.getGueltigkeit().getGueltigBis())) {
						zeitabschnitt.getGueltigkeit().setGueltigBis(gesuchsperiode.getGueltigkeit().getGueltigBis());
						zeitabschnittInPeriode = true;
					}
				}
				if (zeitabschnittInPeriode) {
					addToValidatedZeitabschnitte(validZeitabschnitte, zeitabschnitt);
				}
			} else {
				addToValidatedZeitabschnitte(validZeitabschnitte, zeitabschnitt);
			}
		}
		return validZeitabschnitte;
	}

	/**
	 * Stellt sicher, dass zwei aufeinander folgende Zeitabschnitte nie dieselben Daten haben. Falls
	 * dies der Fall wäre, werden sie zu einem neuen Schnitz gemergt.
     */
	private void addToValidatedZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> validZeitabschnitte, @Nonnull VerfuegungZeitabschnitt zeitabschnitt) {
		// Zuerst vergleichen, ob sich der neue Zeitabschnitt vom letzt hinzugefügten (und angrenzenden) unterscheidet
		int indexOfLast = validZeitabschnitte.size() - 1;
		if (indexOfLast >= 0) {
			VerfuegungZeitabschnitt lastZeitabschnitt = validZeitabschnitte.get(indexOfLast);
			if (lastZeitabschnitt.isSame(zeitabschnitt) && zeitabschnitt.getGueltigkeit().startsDayAfter(lastZeitabschnitt.getGueltigkeit())) {
				// Gleiche Berechnungsgrundlagen: Den alten um den neuen verlängern
				lastZeitabschnitt.getGueltigkeit().setGueltigBis(zeitabschnitt.getGueltigkeit().getGueltigBis());
				// Die Bemerkungen hinzufügen
				lastZeitabschnitt.addAllBemerkungen(zeitabschnitt.getBemerkungen());
				validZeitabschnitte.remove(indexOfLast);
				validZeitabschnitte.add(lastZeitabschnitt);
			} else {
				// Unterschiedliche Daten -> hinzufügen
				validZeitabschnitte.add(zeitabschnitt);
			}
		} else {
			// Erster Eintrag -> hinzufügen
			validZeitabschnitte.add(zeitabschnitt);
		}
	}

	/**
	 * Mergt zwei Listen von Verfuegungszeitschnitten.
     */
	@Nonnull
	private List<VerfuegungZeitabschnitt> mergeZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> bestehendeEntities, @Nonnull List<VerfuegungZeitabschnitt> neueEntities) {
		List<VerfuegungZeitabschnitt> alles = new ArrayList<>();
		alles.addAll(bestehendeEntities);
		alles.addAll(neueEntities);
		return mergeZeitabschnitte(alles);
	}

	/**
	 * Erstellt aus der übergebenen Liste von VerfuegungsZeitabschnitten eine neue Liste, die keine Überschneidungen mehr
	 * enthält. Überschneiden sich zwei Entitäten in der Ursprungsliste, so werden daraus drei Zeiträume erstellt:
	 * |------------------------|
	 *          40
	 *             |-------------------------------------|
	 *                               60
	 * ergibt:
	 * |-----------|------------|------------------------|
	 *     40          100                60
	 */
	@Nonnull
	protected List<VerfuegungZeitabschnitt> mergeZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt>  entitiesUnmerged) {
		// Resultat-Set, damit eventuell veränderte Elemente einfach wieder draufgelegt werden können
		Set<VerfuegungZeitabschnitt> entitiesWithoutUeberschneidungen = new LinkedHashSet<>();
		// Die Zeitabschnitte sortieren nach DatumVon und DatumBis
		Collections.sort(entitiesUnmerged, new VerfuegungZeitabschnittComparator());

		VerfuegungZeitabschnitt last = null;
		for (Iterator<VerfuegungZeitabschnitt> iterator = entitiesUnmerged.iterator(); iterator.hasNext(); ) {
			VerfuegungZeitabschnitt next = iterator.next();

			if (last != null && next.getGueltigkeit().getGueltigAb().isBefore(last.getGueltigkeit().getGueltigBis())) {
				// Wir haben in irgendeiner Form eine Überschneidung
				// Next kann gleichzeitig beginnen
				if (next.getGueltigkeit().startsSameDay(last.getGueltigkeit())) {
					// Next kann gleichzeitig enden oder später (früher geht nicht, wegen sortierung)
					if (next.getGueltigkeit().endsSameDay(last.getGueltigkeit())) {
						last.add(next); // Next wird ganz weggelassen bzw. geht in last auf
						entitiesWithoutUeberschneidungen.add(last); // Hat geändert
					} else {
						// Next endet später: Es müssen zwei Elemente gebildet werden
						last.add(next); // Last behält seine Gültigkeit, erhält aber zusätzlich die Daten von Next
						next.getGueltigkeit().startOnDayAfter(last.getGueltigkeit());
						entitiesWithoutUeberschneidungen.add(last); // Hat geändert
						entitiesWithoutUeberschneidungen.add(next); // ist neu
						last = next;
					}
				} else {
					// Oder next beginnt später. (früher geht nicht, wurde ja vorher sortiert)
					// Dann kann es immer noch gleichzeitig enden oder früher oder später
					if (next.getGueltigkeit().endsSameDay(last.getGueltigkeit())) {
						// Next startet später, sie hören aber gleichzeitig auf
						next.add(last);
						last.getGueltigkeit().endOnDayBefore(next.getGueltigkeit());
						entitiesWithoutUeberschneidungen.add(last); // Hat geändert
						entitiesWithoutUeberschneidungen.add(next); // ist neu
					} else {
						// Entweder Schnittmenge oder "klassische" Überschneidung
						LocalDate ueberschneidungStart = max(last.getGueltigkeit().getGueltigAb(), next.getGueltigkeit().getGueltigAb());
						LocalDate ueberschneidungEnde = min(last.getGueltigkeit().getGueltigBis(), next.getGueltigkeit().getGueltigBis());

						if (next.getGueltigkeit().endsBefore(last.getGueltigkeit())) {
							// Klassische Schnittmenge
							next.add(last); // Next ist die Mitte und erhält das Total, die Dates bleiben gleich
							// Am Ende wird ein neues mit denselben Daten wir "last" erstellt
							VerfuegungZeitabschnitt zeitabschnittUeberschneidung = new VerfuegungZeitabschnitt(new DateRange(ueberschneidungEnde.plusDays(1), last.getGueltigkeit().getGueltigBis()));
							zeitabschnittUeberschneidung.add(last);
							// Last beenden
							last.getGueltigkeit().endOnDayBefore(next.getGueltigkeit());
							entitiesWithoutUeberschneidungen.add(last); // Hat geändert
							entitiesWithoutUeberschneidungen.add(next); // ist neu
							entitiesWithoutUeberschneidungen.add(zeitabschnittUeberschneidung); // ist neu
						} else {
							// Klassische Überschneidung: Der mittlere wird neu erstellt aus dem Total der beiden bestehenden
							VerfuegungZeitabschnitt zeitabschnittUeberschneidung = new VerfuegungZeitabschnitt(new DateRange(ueberschneidungStart, ueberschneidungEnde));
							zeitabschnittUeberschneidung.add(last);
							zeitabschnittUeberschneidung.add(next);

							last.getGueltigkeit().endOnDayBefore(zeitabschnittUeberschneidung.getGueltigkeit());
							next.getGueltigkeit().startOnDayAfter(zeitabschnittUeberschneidung.getGueltigkeit());

							entitiesWithoutUeberschneidungen.add(last); // Hat geändert
							entitiesWithoutUeberschneidungen.add(next); // ist neu
							entitiesWithoutUeberschneidungen.add(zeitabschnittUeberschneidung); // ist neu
							last = next;
						}
					}
				}
			} else {
				// Es ist entweder das erste Element oder es gibt keine Üerschneidung
				last = next;
				entitiesWithoutUeberschneidungen.add(next);
			}
		}
		return new ArrayList<>(entitiesWithoutUeberschneidungen);
	}

	@Nonnull
	private LocalDate min(@Nonnull LocalDate date1, @Nonnull LocalDate date2) {
		if (date1.isBefore(date2)) {
			return date1;
		}
		return date2;
	}

	@Nonnull
	private LocalDate max(@Nonnull LocalDate date1, @Nonnull LocalDate date2) {
		if (date1.isAfter(date2)) {
			return date1;
		}
		return date2;
	}
}
