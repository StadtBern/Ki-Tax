package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class PrintUtil {

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
}
