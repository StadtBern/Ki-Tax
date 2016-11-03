package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Familiensituation;
import org.apache.commons.lang.Validate;

/**
 * Allgemeine Utils fuer EBEGU
 */
public class EbeguUtil {

	/**
	 * Berechnet ob die Daten bei der Familiensituation von einem GS auf 2 GS geaendert wurde.
	 */
	public static boolean fromOneGSToTwoGS(Familiensituation oldFamiliensituation, Familiensituation newFamiliensituation) {
		Validate.notNull(oldFamiliensituation);
		Validate.notNull(newFamiliensituation);
		return !oldFamiliensituation.hasSecondGesuchsteller() && newFamiliensituation.hasSecondGesuchsteller();
	}

}
