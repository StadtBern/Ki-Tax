package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

/**
 *
 */
public class PrintUtil {

	private static final int FALLNUMMER_MAXLAENGE = 6;

	/**
	 * Gibt die Korrespondenzadresse zurueck wenn vorhanden, ansonsten die aktuelle Wohnadresse wenn vorhanden, wenn keine
	 * vorhanden dann empty
	 */
	@Nonnull
	private static Optional<GesuchstellerAdresse> getGesuchstellerAdresse(@Nullable Gesuchsteller gesuchsteller) {

		if (gesuchsteller != null) {
			List<GesuchstellerAdresse> adressen = gesuchsteller.getAdressen();

			// Zuerst suchen wir die Korrespondenzadresse wenn vorhanden
			final Optional<GesuchstellerAdresse> korrespondenzadresse = adressen.stream().filter(GesuchstellerAdresse::isKorrespondenzAdresse)
				.reduce(throwExceptionIfMoreThanOneAdresse(gesuchsteller));
			if (korrespondenzadresse.isPresent()) {
				return korrespondenzadresse;
			}

			// Sonst suchen wir die aktuelle Wohnadresse. Die ist keine KORRESPONDENZADRESSE und das aktuelle Datum liegt innerhalb ihrer Gueltigkeit
			final LocalDate now = LocalDate.now();
			for (GesuchstellerAdresse gesuchstellerAdresse : adressen) {
				if (!gesuchstellerAdresse.getAdresseTyp().equals(AdresseTyp.KORRESPONDENZADRESSE)
					&& !gesuchstellerAdresse.getGueltigkeit().getGueltigAb().isAfter(now)
					&& !gesuchstellerAdresse.getGueltigkeit().getGueltigBis().isBefore(now)) {
					return Optional.of(gesuchstellerAdresse);
				}
			}
		}
		return Optional.empty();
	}

	@Nonnull
	private static BinaryOperator<GesuchstellerAdresse> throwExceptionIfMoreThanOneAdresse(@Nonnull Gesuchsteller gesuchsteller) {
		return (element, otherElement) -> {
            throw new EbeguRuntimeException("getGesuchstellerAdresse_Korrespondenzadresse", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, gesuchsteller.getId());
        };
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
				if (gesuchstellerAdresse.get().getHausnummer() != null) {
					return gesuchstellerAdresse.get().getStrasse() + " " + gesuchstellerAdresse.get().getHausnummer();
				} else {
					return gesuchstellerAdresse.get().getStrasse();
				}
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

	/**
	 * Gibt die Organisationsbezeichnung falls sie eingegeben worden ist, sons leer.
	 *
	 * @return GesuchstellerName
	 */

	public static String getOrganisation(Gesuch gesuch) {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1(gesuch);
		if (gesuchsteller.isPresent()) {
			final List<GesuchstellerAdresse> adressen = gesuchsteller.get().getAdressen();
			for (GesuchstellerAdresse ad : adressen) {
				if (ad.getAdresseTyp() == AdresseTyp.KORRESPONDENZADRESSE) {
					return ad.getOrganisation();
				}
			}
		}
		return null;

	}

	/**
	 * Liefer den Adresszusatz
	 */
	@Nullable
	public static String getAdresszusatz(Gesuch gesuch) {
		if (extractGesuchsteller1(gesuch).isPresent()) {
			Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse(extractGesuchsteller1(gesuch).get());
			if (gesuchstellerAdresse.isPresent()) {
				return gesuchstellerAdresse.get().getZusatzzeile();
			}
		}
		return null;
	}
}
