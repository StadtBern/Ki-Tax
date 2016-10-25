package ch.dvbern.ebegu.vorlagen;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;

import com.google.common.base.Strings;

/**
 *
 */
public class PrintUtil {

	private static final int FALLNUMMER_MAXLAENGE = 6;

	/**
	 * Gibt die Korrespondenzadresse zurueck wenn vorhanden, ansonsten die Wohnadresse wenn vorhanden, wenn keine
	 * vorhanden dann empty
	 *
	 * @param gesuchsteller
	 * @return
	 */
	@Nonnull
	public static Optional<GesuchstellerAdresse> getGesuchstellerAdresse(@Nullable Gesuchsteller gesuchsteller) {

		if (gesuchsteller != null) {
			List<GesuchstellerAdresse> adressen = gesuchsteller.getAdressen();
			GesuchstellerAdresse wohnadresse = null;
			for (GesuchstellerAdresse gesuchstellerAdresse : adressen) {
				if (gesuchstellerAdresse.getAdresseTyp().equals(AdresseTyp.KORRESPONDENZADRESSE)) {
					return Optional.of(gesuchstellerAdresse);
				}
				wohnadresse = gesuchstellerAdresse;
			}
			if (wohnadresse != null) {
				return Optional.of(wohnadresse);
			}
		}
		return Optional.empty();
	}

	/**
	 * Ermittelt die Fallnummer im Form vom JJ.00xxxx. X ist die Fallnummer. Die Fallnummer wird in 6 Stellen
	 * dargestellt (mit 0 ergänzt)
	 *
	 * @param gesuch das Gesuch
	 * @return Fallnummer
	 */
	public static String createFallNummerString(Gesuch gesuch) {

		return Integer.toString(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()).substring(2, 4) + "."
				+ Strings.padStart(Long.toString(gesuch.getFall().getFallNummer()), FALLNUMMER_MAXLAENGE, '0');
	}

	/**
	 * @return GesuchstellerName
	 */

	public static String getGesuchstellerName(Gesuch gesuch) {

		StringBuilder name = new StringBuilder();
		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1(gesuch);
		if (gesuchsteller.isPresent()) {
			name.append(gesuchsteller.get().getFullName());
		}
		if (gesuch.getGesuchsteller2() != null) {
			Optional<Gesuchsteller> gesuchsteller2 = extractGesuchsteller2(gesuch);
			if (gesuchsteller.isPresent()) {
				name.append("\n");
				name.append(gesuchsteller2.get().getFullName());
			}
		}
		return name.toString();
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */

	public static String getGesuchstellerStrasse(Gesuch gesuch) {

		if (extractGesuchsteller1(gesuch).isPresent()) {
			Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse(extractGesuchsteller1(gesuch).get());
			if (gesuchstellerAdresse.isPresent()) {
				return gesuchstellerAdresse.get().getStrasse() + " " + gesuchstellerAdresse.get().getHausnummer();
			}
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */

	public static String getGesuchstellerPLZStadt(Gesuch gesuch) {

		if (extractGesuchsteller1(gesuch).isPresent()) {
			Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse(extractGesuchsteller1(gesuch).get());
			if (gesuchstellerAdresse.isPresent()) {
				return gesuchstellerAdresse.get().getPlz() + " " + gesuchstellerAdresse.get().getOrt();
			}
		}
		return "";
	}

	@Nonnull
	private static Optional<Gesuchsteller> extractGesuchsteller1(Gesuch gesuch) {

		Gesuchsteller gs1 = gesuch.getGesuchsteller1();
		if (gs1 != null) {
			return Optional.of(gs1);
		}
		return Optional.empty();
	}

	@Nonnull
	private static Optional<Gesuchsteller> extractGesuchsteller2(Gesuch gesuch) {

		Gesuchsteller gs2 = gesuch.getGesuchsteller2();
		if (gs2 != null) {
			return Optional.of(gs2);
		}
		return Optional.empty();
	}
}
