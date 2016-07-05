package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

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
	public boolean isValid(@Nonnull LocalDate stichtag) {
		return validityPeriod.contains(stichtag);
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
	protected abstract List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);

	/**
	 * Fuehrt die eigentliche Rule auf einem einzelnen Zeitabschnitt aus.
	 * Hier kann ich davon ausgehen, dass die Zeitabschnitte schon validiert und gemergt sind.
	 */
	protected abstract void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt);

	/**
	 * Hauptmethode der Regelberechnung. Diese wird von Aussen aufgerufen
     */
	@Nonnull
	@Override
	public final List<VerfuegungZeitabschnitt> calculate(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {

		// Zuerst muessen die neuen Zeitabschnitte aus den Daten meiner Rule zusammengestellt werden:
		// In dieser Funktion muss sichergestellt werden, dass in der neuen Liste keine Ueberschneidungen mehr bestehen
		List<VerfuegungZeitabschnitt> abschnitteCreatedInRule = createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte, finSitResultatDTO);

		// Jetzt muessen diese mit den bestehenden Zeitabschnitten aus früheren Rules gemergt werden
		List<VerfuegungZeitabschnitt> mergedZeitabschnitte = mergeZeitabschnitte(zeitabschnitte, abschnitteCreatedInRule);

		// Die Zeitabschnitte (jetzt ohne Überschneidungen) validieren:
		// - Muss innerhalb Gesuchsperiode sein
		// - Müssen sich unterscheiden (d.h. 20+20 vs 40 soll nur einen Schnitz geben)
		Gesuchsperiode gesuchsperiode = betreuung.extractGesuchsperiode();
		List<VerfuegungZeitabschnitt> validZeitabschnitte = validateZeitabschnitte(mergedZeitabschnitte, gesuchsperiode);

		// Die eigentliche Rule anwenden
		for (VerfuegungZeitabschnitt zeitabschnitt : validZeitabschnitte) {
			executeRule(betreuung, zeitabschnitt);
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
	protected List<VerfuegungZeitabschnitt> mergeZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> entitiesToMerge) {
		List<VerfuegungZeitabschnitt> result = new ArrayList<>();
		Set<LocalDate> setOfPotentialZeitraumGrenzen = createSetOfPotentialZeitraumGrenzen(entitiesToMerge);
		if (setOfPotentialZeitraumGrenzen.isEmpty()) {
			return result;
		}
		Iterator<LocalDate> iterator = setOfPotentialZeitraumGrenzen.iterator();
		LocalDate datumVon = iterator.next();
		while (iterator.hasNext()) {
			LocalDate datumBis = iterator.next().minusDays(1);
			VerfuegungZeitabschnitt mergedZeitabschnitt = new VerfuegungZeitabschnitt(new DateRange(datumVon, datumBis));
			// Alle Zeitabschnitte suchen, die zwischen diesem Range liegen
			boolean foundOverlapping = false;
			for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : entitiesToMerge) {
				Optional<DateRange> optionalOverlap = verfuegungZeitabschnitt.getGueltigkeit().getOverlap(mergedZeitabschnitt.getGueltigkeit());
				if (optionalOverlap.isPresent()) {
					mergedZeitabschnitt.add(verfuegungZeitabschnitt);
					foundOverlapping = true;
				}
			}
			if (foundOverlapping) {
				result.add(mergedZeitabschnitt);
			}
			datumVon = datumBis.plusDays(1);
		}
		return result;
	}

	private Set<LocalDate> createSetOfPotentialZeitraumGrenzen(@Nonnull List<VerfuegungZeitabschnitt>  entitiesUnmerged) {
		Set<LocalDate> setOfDates = new TreeSet<>();
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : entitiesUnmerged) {
			setOfDates.add(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb());
			setOfDates.add(verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis().plusDays(1));
		}
		return setOfDates;
	}
}
