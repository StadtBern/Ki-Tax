package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.dto.AbstractFinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.dto.EinkommensverschlechterungResultateDTO;
import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.Gesuch;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

/**
 * Util zum berechnen der abgeleiteten Werte der Finanziellen Sitution
 */
public class FinanzielleSituationUtil {

	@Nonnull
	public static FinanzielleSituationResultateDTO calculateResultate(@Nonnull FinanzielleSituationRechner rechner, @Nonnull Gesuch gesuch) {
		if (gesuch.getGesuchsperiode() != null) {
			double familiengroesse = rechner.calculateFamiliengroesse(gesuch, gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear());
			BigDecimal abzugAufgrundFamiliengroesse = rechner
				.calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);
			return new FinanzielleSituationResultateDTO(gesuch, familiengroesse, abzugAufgrundFamiliengroesse);
		}
		return new FinanzielleSituationResultateDTO(gesuch, 0, BigDecimal.ZERO);
	}

	@Nonnull
	public static AbstractFinanzielleSituationResultateDTO calculateResultateEinkommensverschlechterung(@Nonnull FinanzielleSituationRechner rechner, @Nonnull Gesuch gesuch, int basisJahrPlus) {
		Validate.notNull(gesuch.getEinkommensverschlechterungInfo());
		if (gesuch.getGesuchsperiode() != null) {

			//Bei der Berechnung der Einkommensverschlechterung werden die aktuellen Familienverhaeltnisse beruecksichtigt
			// (nicht Stand 31.12. des Vorjahres)!

			double familiengroesse = rechner.calculateFamiliengroesse(gesuch, null);
			BigDecimal abzugAufgrundFamiliengroesse = rechner
				.calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);
			return new EinkommensverschlechterungResultateDTO(gesuch, familiengroesse, abzugAufgrundFamiliengroesse, basisJahrPlus);
		}
		return new EinkommensverschlechterungResultateDTO(gesuch, 0, BigDecimal.ZERO, basisJahrPlus);
	}


	public static void calculateFinanzDaten(@Nonnull FinanzielleSituationRechner rechner, @Nonnull Gesuch gesuch) {
		FinanzDatenDTO finanzDatenDTO = new FinanzDatenDTO();

		// Finanzielle Situation berechnen
		FinanzielleSituationResultateDTO finanzielleSituationResultateDTO = calculateResultate(rechner, gesuch);
		finanzDatenDTO.setMassgebendesEinkommenBasisjahr(finanzielleSituationResultateDTO.getMassgebendesEinkommen());
		finanzDatenDTO.setDatumVonBasisjahr(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());

		if (gesuch.getEinkommensverschlechterungInfo() != null) {
			EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.getEinkommensverschlechterungInfo();
			AbstractFinanzielleSituationResultateDTO resultateEKV1 = calculateResultateEinkommensverschlechterung(rechner, gesuch, 1);
			if (gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1() != null && gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1()) {
				// In der EKV 1 vergleichen wir immer mit dem Basisjahr
				BigDecimal massgebendesEinkommenVorjahr = finanzielleSituationResultateDTO.getMassgebendesEinkommen();
				BigDecimal massgebendesEinkommenJahr = resultateEKV1.getMassgebendesEinkommen();
				if (acceptEKV(massgebendesEinkommenVorjahr, massgebendesEinkommenJahr)) {
					finanzDatenDTO.setMassgebendesEinkommenBasisjahrPlus1(resultateEKV1.getMassgebendesEinkommen());
					finanzDatenDTO.setDatumVonBasisjahrPlus1(einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus1());
				}
			}
			if (gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2() != null && gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2()) {
				AbstractFinanzielleSituationResultateDTO resultateEKV2 = calculateResultateEinkommensverschlechterung(rechner, gesuch, 2);
				// In der EKV 2 vergleichen wir immer mit dem EKV 1, egal ob diese akzeptiert war
				BigDecimal massgebendesEinkommenVorjahr = resultateEKV1.getMassgebendesEinkommen();
				BigDecimal massgebendesEinkommenJahr = resultateEKV2.getMassgebendesEinkommen();
				if (acceptEKV(massgebendesEinkommenVorjahr, massgebendesEinkommenJahr)) {
					finanzDatenDTO.setMassgebendesEinkommenBasisjahrPlus2(resultateEKV2.getMassgebendesEinkommen());
					finanzDatenDTO.setDatumVonBasisjahrPlus2(einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus2());
				}
			}
		}
		gesuch.setFinanzDatenDTO(finanzDatenDTO);
	}

	private static boolean acceptEKV(BigDecimal massgebendesEinkommenVorjahr, BigDecimal massgebendesEinkommenJahr) {
		BigDecimal minimumEKV = MathUtil.EINE_NACHKOMMASTELLE.from(0.8);
		if (massgebendesEinkommenVorjahr.compareTo(BigDecimal.ZERO) > 0 && MathUtil.EXACT.divide(massgebendesEinkommenJahr, massgebendesEinkommenVorjahr).compareTo(minimumEKV) <= 0) {
			// EKV gewährt
			return true;
		} else {
			return false;
		}
	}
}
