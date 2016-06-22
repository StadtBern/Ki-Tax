package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.services.EbeguParameterService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Ein Rechner mit den ganzen Operationen fuer Finanziellesituation
 * Created by imanol on 22.06.16.
 */
@Dependent
public class FinanzielleSituationRechner {

	@Inject
	private EbeguParameterService ebeguParameterService;

	/**
	 * Die Familiengroesse wird folgendermassen kalkuliert:
	 * Familiengrösse = Gesuchsteller1 + Gesuchsteller2 (falls vorhanden) + Faktor Steuerabzug pro Kind (0, 0.5, oder 1)
	 *
	 * Der Faktor wird gemaess Wert des Felds kinderabzug von Kind berechnet:
	 * 	KEIN_ABZUG = 0
	 * 	HALBER_ABZUG = 0.5
	 * 	GANZER_ABZUG = 1
	 * 	KEINE_STEUERERKLAERUNG = 1
	 *
	 * Nur die Kinder die nach dem uebergebenen Datum geboren sind werden mitberechnet
	 *
	 * @param gesuch das Gesuch aus welchem die Daten geholt werden
	 * @param date das Datum fuer das die familiengroesse kalkuliert werden muss
	 * @return die familiengroesse als double
	 */
	public double calculateFamiliengroesse(Gesuch gesuch, LocalDate date) {
		double familiengroesse = 0;
		if (gesuch != null && date != null) {
			if (gesuch.getGesuchsteller1() != null) {
				familiengroesse++;
			}
			if (gesuch.getGesuchsteller2() != null) {
				familiengroesse++;
			}
			for (KindContainer kindContainer : gesuch.getKindContainers()) {
				if (kindContainer.getKindJA() != null && kindContainer.getKindJA().getGeburtsdatum().isAfter(date)) {
					if (kindContainer.getKindJA().getKinderabzug() == Kinderabzug.HALBER_ABZUG) {
						familiengroesse += 0.5;
					}
					if (kindContainer.getKindJA().getKinderabzug() == Kinderabzug.GANZER_ABZUG || kindContainer.getKindJA().getKinderabzug() == Kinderabzug.KEINE_STEUERERKLAERUNG) {
						familiengroesse++;
					}
				}
			}
		}
		return familiengroesse;
	}

	public BigDecimal calculateAbzugAufgrundFamiliengroesse(LocalDate stichtag, double familiengroesse) {
		BigDecimal abzugProPerson = BigDecimal.ZERO;
		if (familiengroesse < 3) {
			abzugProPerson = BigDecimal.ZERO;
		} else if (familiengroesse < 4) {
			Optional<EbeguParameter> famSize3 = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3, stichtag);
			if (famSize3.isPresent()) {
				abzugProPerson = famSize3.get().getAsBigDecimal();
			}
		} else if (familiengroesse < 5) {
			Optional<EbeguParameter> famSize4 = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4, stichtag);
			if (famSize4.isPresent()) {
				abzugProPerson = famSize4.get().getAsBigDecimal();
			}
		} else if (familiengroesse < 6) {
			Optional<EbeguParameter> famSize5 = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5, stichtag);
			if (famSize5.isPresent()) {
				abzugProPerson = famSize5.get().getAsBigDecimal();
			}
		} else { // >=6
			Optional<EbeguParameter> famSize6 = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6, stichtag);
			if (famSize6.isPresent()) {
				abzugProPerson = famSize6.get().getAsBigDecimal();
			}
		}
		return new BigDecimal(familiengroesse).multiply(abzugProPerson);
	}

}
