package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class PrintUtil {

	private static final int FALLNUMMER_MAXLAENGE = 6;

	/**
	 * Gibt die Korrespondenzadresse zurueck wenn vorhanden, ansonsten die Wohnadresse wenn vorhanden, wenn keine vorhanden dann empty
	 * @param gesuchsteller
	 * @return
	 */
	@Nonnull
	public static  Optional<GesuchstellerAdresse> getGesuchstellerAdresse(@Nullable Gesuchsteller gesuchsteller) {

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
}
