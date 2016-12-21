package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.util.StreamsUtil;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

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
	public static Optional<GesuchstellerAdresseContainer> getGesuchstellerAdresse(@Nullable GesuchstellerContainer gesuchsteller) {

		if (gesuchsteller != null) {
			List<GesuchstellerAdresseContainer> adressen = gesuchsteller.getAdressen();

			// Zuerst suchen wir die Korrespondenzadresse wenn vorhanden
			final Optional<GesuchstellerAdresseContainer> korrespondenzadresse = adressen.stream().filter(GesuchstellerAdresseContainer::extractIsKorrespondenzAdresse)
				.reduce(throwExceptionIfMoreThanOneAdresse(gesuchsteller));
			if (korrespondenzadresse.isPresent() && korrespondenzadresse.get().getGesuchstellerAdresseJA() != null) {
				return korrespondenzadresse;
			}

			// Sonst suchen wir die aktuelle Wohnadresse. Die ist keine KORRESPONDENZADRESSE und das aktuelle Datum liegt innerhalb ihrer Gueltigkeit
			final LocalDate now = LocalDate.now();
			for (GesuchstellerAdresseContainer gesuchstellerAdresse : adressen) {
				if (!gesuchstellerAdresse.extractIsKorrespondenzAdresse()
					&& !gesuchstellerAdresse.extractGueltigkeit().getGueltigAb().isAfter(now)
					&& !gesuchstellerAdresse.extractGueltigkeit().getGueltigBis().isBefore(now)) {
					return Optional.of(gesuchstellerAdresse);
				}
			}
		}
		return Optional.empty();
	}

	@Nonnull
	private static BinaryOperator<GesuchstellerAdresseContainer> throwExceptionIfMoreThanOneAdresse(@Nonnull GesuchstellerContainer gesuchsteller) {
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
		Optional<GesuchstellerContainer> gesuchsteller = extractGesuchsteller1(gesuch);
		if (gesuchsteller.isPresent()) {
			name.append(gesuchsteller.get().extractFullName());
		}
		if (gesuch.getGesuchsteller2() != null) {
			Optional<GesuchstellerContainer> gesuchsteller2 = extractGesuchsteller2(gesuch);
			if (gesuchsteller.isPresent()) {
				name.append("\n");
				name.append(gesuchsteller2.get().extractFullName());
			}
		}
		return name.toString();
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */

	public static String getGesuchstellerStrasse(Gesuch gesuch) {

		final Optional<GesuchstellerContainer> gesuchsteller1 = extractGesuchsteller1(gesuch);
		if (gesuchsteller1.isPresent()) {
			Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = getGesuchstellerAdresse(gesuchsteller1.get());
			if (gesuchstellerAdresse.isPresent()) {
				final GesuchstellerAdresseContainer gsAdresseCont = gesuchstellerAdresse.get();
				if (gsAdresseCont.extractHausnummer() != null) {
					return gsAdresseCont.extractStrasse() + " " + gsAdresseCont.extractHausnummer();
				} else {
					return gsAdresseCont.extractStrasse();
				}
			}
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */

	public static String getGesuchstellerPLZStadt(Gesuch gesuch) {

		final Optional<GesuchstellerContainer> gesuchsteller1 = extractGesuchsteller1(gesuch);
		if (gesuchsteller1.isPresent()) {
			Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = getGesuchstellerAdresse(gesuchsteller1.get());
			if (gesuchstellerAdresse.isPresent()) {
				final GesuchstellerAdresseContainer gsAdresseCont = gesuchstellerAdresse.get();
				return gsAdresseCont.extractPlz() + " " + gsAdresseCont.extractOrt();
			}
		}
		return "";
	}

	@Nonnull
	private static Optional<GesuchstellerContainer> extractGesuchsteller1(Gesuch gesuch) {

		GesuchstellerContainer gs1 = gesuch.getGesuchsteller1();
		if (gs1 != null) {
			return Optional.of(gs1);
		}
		return Optional.empty();
	}

	@Nonnull
	private static Optional<GesuchstellerContainer> extractGesuchsteller2(Gesuch gesuch) {

		GesuchstellerContainer gs2 = gesuch.getGesuchsteller2();
		if (gs2 != null) {
			return Optional.of(gs2);
		}
		return Optional.empty();
	}

	/**
	 * Gibt die Organisationsbezeichnung falls sie eingegeben worden ist, sons leer. Die Organisation MUSS auf einer
	 * Korrespondenzadresse sein wenn vorhanden
	 *
	 * @return GesuchstellerName
	 */

	public static String getOrganisation(Gesuch gesuch) {
		Optional<GesuchstellerContainer> gesuchsteller = extractGesuchsteller1(gesuch);
		if (gesuchsteller.isPresent()) {
			final List<GesuchstellerAdresseContainer> adressen = gesuchsteller.get().getAdressen();
			Optional<GesuchstellerAdresseContainer> korrespondezaddrOpt = adressen.stream().
				filter(GesuchstellerAdresseContainer::extractIsKorrespondenzAdresse)
				.reduce(StreamsUtil.toOnlyElement());
			if (korrespondezaddrOpt.isPresent()) {
				return korrespondezaddrOpt.get().extractOrganisation();
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
			Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = getGesuchstellerAdresse(extractGesuchsteller1(gesuch).get());
			if (gesuchstellerAdresse.isPresent()) {
				return gesuchstellerAdresse.get().extractZusatzzeile();
			}
		}
		return null;
	}

	public static String getNameAdresseFormatiert(Gesuch gesuch, GesuchstellerContainer gesuchsteller){

		if (gesuch != null && gesuchsteller != null){
			String newlineMSWord = "\n";
			String adresse = StringUtils.EMPTY;

			adresse += gesuchsteller.extractFullName();

			Optional<GesuchstellerAdresseContainer> gsa = getGesuchstellerAdresse(gesuchsteller);
			if (gsa.isPresent()) {
				if (StringUtils.isNotEmpty(gsa.get().extractHausnummer())) {
					adresse += newlineMSWord + gsa.get().extractStrasse() + " " + gsa.get().extractHausnummer();
				} else {
					adresse += newlineMSWord + gsa.get().extractStrasse();
				}
			}

			String adrZusatz = getAdresszusatz(gesuch);
			if (StringUtils.isNotEmpty(adrZusatz)) {
				adresse += newlineMSWord + adrZusatz;
			}

			adresse += newlineMSWord + getGesuchstellerPLZStadt(gesuch);

			return adresse;
		} else{
			return StringUtils.EMPTY;
		}

	}

	@Nonnull
	public static StringBuilder parseDokumentGrundDataToString(DokumentGrund dokumentGrund) {
		StringBuilder bemerkungenBuilder = new StringBuilder();
		if (dokumentGrund.isNeeded() && dokumentGrund.isEmpty()) {
			bemerkungenBuilder.append(ServerMessageUtil.translateEnumValue(dokumentGrund.getDokumentTyp()));
			if (StringUtils.isNotEmpty(dokumentGrund.getFullName())) {
				bemerkungenBuilder.append(" (");
				bemerkungenBuilder.append(dokumentGrund.getFullName());

				if (dokumentGrund.getTag() != null) {
					bemerkungenBuilder.append(" / ").append(dokumentGrund.getTag());
				}
				bemerkungenBuilder.append(")");
			}
		}
		return bemerkungenBuilder;
	}
}
