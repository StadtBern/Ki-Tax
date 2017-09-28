package ch.dvbern.ebegu.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Allgemeine Utils fuer EBEGU
 */
public class EbeguUtil {

	/**
	 * Berechnet ob die Daten bei der Familiensituation von einem GS auf 2 GS geaendert wurde.
	 */
	public static boolean fromOneGSToTwoGS(FamiliensituationContainer familiensituationContainer) {
		Validate.notNull(familiensituationContainer);
		Validate.notNull(familiensituationContainer.getFamiliensituationJA());
		Validate.notNull(familiensituationContainer.getFamiliensituationErstgesuch());

		return fromOneGSToTwoGS(familiensituationContainer.getFamiliensituationErstgesuch(), familiensituationContainer.getFamiliensituationJA());
	}

	public static boolean fromOneGSToTwoGS(Familiensituation oldFamiliensituation, Familiensituation newFamiliensituation) {
		Validate.notNull(oldFamiliensituation);
		Validate.notNull(newFamiliensituation);
		return !oldFamiliensituation.hasSecondGesuchsteller() && newFamiliensituation.hasSecondGesuchsteller();
	}

	/**
	 * Gibt aus einer Liste von Gesuchen nur das jeweils neueste (hoechste Laufummer) pro Fall zurueck.
	 * Die Rueckgabe erfolgt in einer Map mit GesuchId-Gesuch
	 */
	public static Map<String, Gesuch> groupByFallAndSelectNewestAntrag(List<Gesuch> allGesuche) {
		ArrayListMultimap<Fall, Gesuch> fallToAntragMultimap = ArrayListMultimap.create();
		allGesuche.forEach(gesuch -> fallToAntragMultimap.put(gesuch.getFall(), gesuch));
		// map erstellen in der nur noch das gesuch mit der hoechsten laufnummer drin ist
		Map<String, Gesuch> gesuchMap = new HashMap<>();
		for (Fall fall : fallToAntragMultimap.keySet()) {
			List<Gesuch> antraege = fallToAntragMultimap.get(fall);
			antraege.sort(Comparator.comparing(Gesuch::getLaufnummer).reversed());
			gesuchMap.put(antraege.get(0).getId(), antraege.get(0)); //nur neusten Antrag zurueckgeben
		}
		return gesuchMap;
	}

	public static boolean isSameObject(@Nullable AbstractEntity thisEntity, @Nullable AbstractEntity otherEntity) {
		return (thisEntity == null && otherEntity == null)
			|| (thisEntity != null && otherEntity != null && thisEntity.isSame(otherEntity));
	}

	/**
	 * Returns true if both strings have the same content or both are null or emptystrings
	 * or one is emptystring and the other is null
	 */
	public static boolean isSameOrNullStrings(@Nullable String thisString, @Nullable String otherString) {
		return (StringUtils.isBlank(thisString) && StringUtils.isBlank(otherString))
			|| Objects.equals(thisString, otherString);
	}

	/**
	 * Returns true if both strings have the same content or both are null or emptystrings
	 * or one is emptystring and the other is null
	 */
	public static boolean isSameOrNullBoolean(@Nullable Boolean thisBoolean, @Nullable Boolean otherBoolean) {
		return (isNullOrFalse(thisBoolean) && isNullOrFalse(otherBoolean))
			|| Objects.equals(thisBoolean, otherBoolean);
	}

	public static boolean isNullOrFalse(@Nullable Boolean value) {
		return value == null || !value;
	}
}
