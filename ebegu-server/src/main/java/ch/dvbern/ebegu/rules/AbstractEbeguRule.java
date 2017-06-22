package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import javax.validation.Valid;
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

	@Valid
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
	 */
	@Nonnull
	protected abstract List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte);

	/**
	 * Fuehrt die eigentliche Rule auf einem einzelnen Zeitabschnitt aus.
	 * Hier kann man davon ausgehen, dass die Zeitabschnitte schon validiert und gemergt sind.
	 */
	protected abstract void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt);

	/**
	 * Hauptmethode der Regelberechnung. Diese wird von Aussen aufgerufen
	 */
	@Nonnull
	@Override
	public final List<VerfuegungZeitabschnitt> calculate(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {

		Collections.sort(zeitabschnitte);

		// Zuerst muessen die neuen Zeitabschnitte aus den Daten meiner Rule zusammengestellt werden:

		List<VerfuegungZeitabschnitt> abschnitteCreatedInRule = createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte);
		Collections.sort(abschnitteCreatedInRule);


		// In dieser Funktion muss sichergestellt werden, dass in der neuen Liste keine Ueberschneidungen mehr bestehen
		// Jetzt muessen diese mit den bestehenden Zeitabschnitten aus früheren Rules gemergt werden
		List<VerfuegungZeitabschnitt> mergedZeitabschnitte = mergeZeitabschnitte(zeitabschnitte, abschnitteCreatedInRule);
		Collections.sort(mergedZeitabschnitte);

		// Die Zeitabschnitte (jetzt ohne Überschneidungen) normalisieren:
		// - Muss innerhalb Gesuchsperiode sein
		// - Müssen sich unterscheiden (d.h. 20+20 vs 40 soll nur einen Schnitz geben)
		Gesuchsperiode gesuchsperiode = betreuung.extractGesuchsperiode();
		List<VerfuegungZeitabschnitt> normalizedZeitabschn = normalizeZeitabschnitte(mergedZeitabschnitte,
			gesuchsperiode);

		// Die eigentliche Rule anwenden
		for (VerfuegungZeitabschnitt zeitabschnitt : normalizedZeitabschn) {
			executeRule(betreuung, zeitabschnitt);
		}
		return normalizedZeitabschn;
	}

	/**
	 * Prüft, dass die Zeitabschnitte innerhalb der Gesuchperiode liegen (und kürzt sie falls nötig bzw. lässt
	 * Zeitschnitze weg, welche ganz ausserhalb der Periode liegen)
	 * Stellt ausserdem sicher, dass zwei aufeinander folgende Zeitabschnitte nie dieselben Daten haben. Falls
	 * dies der Fall wäre, werden sie zu einem neuen Schnitz gemergt.
	 */
	@Nonnull
	protected List<VerfuegungZeitabschnitt> normalizeZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> mergedZeitabschnitte, @Nonnull Gesuchsperiode gesuchsperiode) {
		List<VerfuegungZeitabschnitt> normalizedZeitabschnitte = new LinkedList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : mergedZeitabschnitte) {
			// Zuerst überprüfen, ob der Zeitabschnitt innerhalb der Gesuchsperiode liegt
			boolean startsBefore = zeitabschnitt.getGueltigkeit().startsBefore(gesuchsperiode.getGueltigkeit());
			boolean endsAfter = zeitabschnitt.getGueltigkeit().endsAfter(gesuchsperiode.getGueltigkeit());
			if (startsBefore || endsAfter) {
				boolean zeitabschnittInPeriode = false;
				if (startsBefore && zeitabschnitt.getGueltigkeit().getGueltigBis().isAfter(gesuchsperiode.getGueltigkeit().getGueltigAb())) { // die Regel WOHNSITZ darf nicht normalisiert werden, da auch wohnsitze ausserhalb der Gesuchsperiode beruecksichtigt werden muessen
					// Datum Von liegt vor der Periode
					// Falls Datum Bis ebenfalls vor der Periode liegt, kann der Abschnitt gelöscht werden, ansonsten muss er verkürzt werden
					if (!RuleKey.WOHNSITZ.equals(ruleKey)) {
						zeitabschnitt.getGueltigkeit().setGueltigAb(gesuchsperiode.getGueltigkeit().getGueltigAb());
					}
					zeitabschnittInPeriode = true;
				}
				if (endsAfter && zeitabschnitt.getGueltigkeit().getGueltigAb().isBefore(gesuchsperiode.getGueltigkeit().getGueltigBis())) {
					// Datum Bis liegt nach der Periode
					// Falls Datum Von auch schon nach der Periode lag, kann der Abschnitt gelöscht werden, ansonsten muss er verkürzt werden
					zeitabschnitt.getGueltigkeit().setGueltigBis(gesuchsperiode.getGueltigkeit().getGueltigBis());
					zeitabschnittInPeriode = true;
				}
				if (zeitabschnittInPeriode) {
					addToNormalizedZeitabschnitte(normalizedZeitabschnitte, zeitabschnitt);
				}
			} else {
				addToNormalizedZeitabschnitte(normalizedZeitabschnitte, zeitabschnitt);
			}
		}
		return normalizedZeitabschnitte;
	}

	/**
	 * Stellt sicher, dass zwei aufeinander folgende Zeitabschnitte nie dieselben Daten haben. Falls
	 * dies der Fall wäre, werden sie zu einem neuen Schnitz gemergt.
	 */
	private void addToNormalizedZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> validZeitabschnitte, @Nonnull VerfuegungZeitabschnitt zeitabschnitt) {
		// Zuerst vergleichen, ob sich der neue Zeitabschnitt vom letzt hinzugefügten (und angrenzenden) unterscheidet
		int indexOfLast = validZeitabschnitte.size() - 1;
		if (indexOfLast >= 0) {
			VerfuegungZeitabschnitt lastZeitabschnitt = validZeitabschnitte.get(indexOfLast);
			if (lastZeitabschnitt.isSame(zeitabschnitt) && zeitabschnitt.getGueltigkeit().startsDayAfter(lastZeitabschnitt.getGueltigkeit())) {
				// Gleiche Berechnungsgrundlagen: Den alten um den neuen verlängern
				lastZeitabschnitt.getGueltigkeit().setGueltigBis(zeitabschnitt.getGueltigkeit().getGueltigBis());
				// Die Bemerkungen hinzufügen
				lastZeitabschnitt.addBemerkung(zeitabschnitt.getBemerkungen());
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
	 * <pre>
	 * |------------------------|
	 * 40
	 * 	           |-------------------------------------|
	 * 			   60
	 * ergibt:
	 * |-----------|------------|------------------------|
	 * 40          100                60
	 * </pre>
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
			LocalDate datumBis = iterator.next().minusDays(1);   //wir haben bei den Bis Daten jeweils einen Tag hinzugefuegt
			VerfuegungZeitabschnitt mergedZeitabschnitt = new VerfuegungZeitabschnitt(new DateRange(datumVon, datumBis));
			// Alle Zeitabschnitte suchen, die mit  diesem Range ueberlappen
			boolean foundOverlapping = false;
			for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : entitiesToMerge) {
				Optional<DateRange> optionalOverlap = verfuegungZeitabschnitt.getGueltigkeit().getOverlap(mergedZeitabschnitt.getGueltigkeit());
				if (optionalOverlap.isPresent()) {
					mergedZeitabschnitt.add(verfuegungZeitabschnitt); //Zeitabschnitt hinzumergen
					foundOverlapping = true;
				}
			}
			if (foundOverlapping) {
				result.add(mergedZeitabschnitt);
			}
			datumVon = datumBis.plusDays(1); //naechstes vondatum
		}
		return result;
	}

	/**
	 * Sammelt alle Start und Enddaten aus der Liste der Zeitabschnitte zusammen.
	 * Dabei ist es so, dass das bis Datum der Zeitabschnitte inklusiv - inklusiv ist. Wir moechten aber die Daten jeweils
	 * inklusv - exklusive enddatum. Daher wird zum endtaum jeweils ein Tag hinzugezaehlt
	 *
	 * @param entitiesUnmerged liste der Abschnitte
	 * @return Liste aller Dates die potentiell als Zeitraumgrenze dienen werden
	 */
	private Set<LocalDate> createSetOfPotentialZeitraumGrenzen(@Nonnull List<VerfuegungZeitabschnitt> entitiesUnmerged) {
		Set<LocalDate> setOfDates = new TreeSet<>();
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : entitiesUnmerged) {
			setOfDates.add(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb());
			setOfDates.add(verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis().plusDays(1));
		}
		return setOfDates;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("ruleKey", ruleKey)
			.append("ruleType", ruleType)
			.append("validityPeriod", validityPeriod)
			.toString();
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return false;
	}
}
