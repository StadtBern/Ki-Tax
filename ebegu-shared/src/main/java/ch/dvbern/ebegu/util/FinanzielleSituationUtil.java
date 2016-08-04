package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.dto.AbstractFinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.dto.EinkommensverschlechterungResultateDTO;
import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Util zum berechnen der abgeleiteten Werte der Finanziellen Sitution
 */
public class FinanzielleSituationUtil {

	@Nonnull
	public static FinanzielleSituationResultateDTO calculateResultateFinanzielleSituation(@Nonnull FinanzielleSituationRechner rechner, @Nonnull Gesuch gesuch) {

		double familiengroesse = gesuch.getGesuchsperiode() == null ? 0 : rechner.calculateFamiliengroesse(gesuch, gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear());
		BigDecimal abzugAufgrundFamiliengroesse = gesuch.getGesuchsperiode() == null ? BigDecimal.ZERO : rechner
			.calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);

		final FinanzielleSituationResultateDTO finSitResultDTO = new FinanzielleSituationResultateDTO(familiengroesse, abzugAufgrundFamiliengroesse);
		setFinanzielleSituationParameters(gesuch, finSitResultDTO);

		return finSitResultDTO;
	}

	@Nonnull
	public static AbstractFinanzielleSituationResultateDTO calculateResultateEinkommensverschlechterung(@Nonnull FinanzielleSituationRechner rechner,
																										@Nonnull Gesuch gesuch, int basisJahrPlus) {
		Validate.notNull(gesuch.getEinkommensverschlechterungInfo());
		//Bei der Berechnung der Einkommensverschlechterung werden die aktuellen Familienverhaeltnisse beruecksichtigt
		// (nicht Stand 31.12. des Vorjahres)!
		double familiengroesse = gesuch.getGesuchsperiode() == null ? 0 : rechner.calculateFamiliengroesse(gesuch, null);
		BigDecimal abzugAufgrundFamiliengroesse = gesuch.getGesuchsperiode() == null ? BigDecimal.ZERO : rechner
			.calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);

		final EinkommensverschlechterungResultateDTO einkVerResultDTO =  new EinkommensverschlechterungResultateDTO(familiengroesse, abzugAufgrundFamiliengroesse);
		setEinkommensverschlechterungParameters(gesuch, basisJahrPlus, einkVerResultDTO);

		return einkVerResultDTO;
	}

	/**
	 * Nimmt das uebergebene FinanzielleSituationResultateDTO und mit den Daten vom Gesuch, berechnet alle im
	 * FinanzielleSituationResultateDTO benoetigten Daten.
	 * @param gesuch
	 * @param finSitResultDTO
	 */
	private static void setFinanzielleSituationParameters(@Nonnull Gesuch gesuch, final FinanzielleSituationResultateDTO finSitResultDTO) {
		final FinanzielleSituation finanzielleSituationGS1 = FinanzielleSituationUtil.getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		finSitResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(FinanzielleSituationUtil.calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS1));

		final FinanzielleSituation finanzielleSituationGS2 = FinanzielleSituationUtil.getFinanzielleSituationGS(gesuch.getGesuchsteller2());
		finSitResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(FinanzielleSituationUtil.calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS2));

		FinanzielleSituationUtil.calculateZusammen(finSitResultDTO, finanzielleSituationGS1,
			FinanzielleSituationUtil.calculateNettoJahresLohn(finanzielleSituationGS1),
			finSitResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller1(),
			finanzielleSituationGS2, FinanzielleSituationUtil.calculateNettoJahresLohn(finanzielleSituationGS2),
			finSitResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller2());
	}

	/**
	 * Nimmt das uebergebene EinkommensverschlechterungResultateDTO und mit den Daten vom Gesuch, berechnet alle im
	 * EinkommensverschlechterungResultateDTO benoetigten Daten.
	 * @param gesuch
	 * @param basisJahrPlus
	 * @param einkVerResultDTO
	 */
	private static void setEinkommensverschlechterungParameters(@Nonnull Gesuch gesuch, int basisJahrPlus,
																final EinkommensverschlechterungResultateDTO einkVerResultDTO) {
		Einkommensverschlechterung einkommensverschlechterungGS1 = FinanzielleSituationUtil.getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), basisJahrPlus);
		final FinanzielleSituation finanzielleSituationGS1 = FinanzielleSituationUtil.getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(
			FinanzielleSituationUtil.calcGeschaeftsgewinnDurchschnitt(einkommensverschlechterungGS1, finanzielleSituationGS1));

		Einkommensverschlechterung einkommensverschlechterungGS2 = FinanzielleSituationUtil.getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), basisJahrPlus);
		final FinanzielleSituation finanzielleSituationGS2 = FinanzielleSituationUtil.getFinanzielleSituationGS(gesuch.getGesuchsteller2());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(
			FinanzielleSituationUtil.calcGeschaeftsgewinnDurchschnitt(einkommensverschlechterungGS2, finanzielleSituationGS2));

		FinanzielleSituationUtil.calculateZusammen(einkVerResultDTO, einkommensverschlechterungGS1,
			FinanzielleSituationUtil.calculateNettoJahresLohn(einkommensverschlechterungGS1),
			einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller1(),
			einkommensverschlechterungGS2, FinanzielleSituationUtil.calculateNettoJahresLohn(einkommensverschlechterungGS2),
			einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller2());

	}


	public static void calculateFinanzDaten(@Nonnull FinanzielleSituationRechner rechner, @Nonnull Gesuch gesuch) {
		FinanzDatenDTO finanzDatenDTO = new FinanzDatenDTO();

		// Finanzielle Situation berechnen
		FinanzielleSituationResultateDTO finanzielleSituationResultateDTO = calculateResultateFinanzielleSituation(rechner, gesuch);
		finanzDatenDTO.setMassgebendesEinkommenBasisjahr(finanzielleSituationResultateDTO.getMassgebendesEinkommen());
		finanzDatenDTO.setDatumVonBasisjahr(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());

		if (gesuch.getEinkommensverschlechterungInfo() != null) {
			EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.getEinkommensverschlechterungInfo();
			AbstractFinanzielleSituationResultateDTO resultateEKV1 = calculateResultateEinkommensverschlechterung(rechner, gesuch, 1);
			if (gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1() != null && gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1()) {
				// In der EKV 1 vergleichen wir immer mit dem Basisjahr
				BigDecimal massgebendesEinkommenVorjahr = finanzielleSituationResultateDTO.getMassgebendesEinkommen();
				BigDecimal massgebendesEinkommenJahr = resultateEKV1.getMassgebendesEinkommen();
				if (FinanzielleSituationUtil.acceptEKV(massgebendesEinkommenVorjahr, massgebendesEinkommenJahr)) {
					finanzDatenDTO.setMassgebendesEinkommenBasisjahrPlus1(resultateEKV1.getMassgebendesEinkommen());
					finanzDatenDTO.setDatumVonBasisjahrPlus1(einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus1());
				}
			}
			if (gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2() != null && gesuch.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2()) {
				AbstractFinanzielleSituationResultateDTO resultateEKV2 = calculateResultateEinkommensverschlechterung(rechner, gesuch, 2);
				// In der EKV 2 vergleichen wir immer mit dem EKV 1, egal ob diese akzeptiert war
				BigDecimal massgebendesEinkommenVorjahr = resultateEKV1.getMassgebendesEinkommen();
				BigDecimal massgebendesEinkommenJahr = resultateEKV2.getMassgebendesEinkommen();
				if (FinanzielleSituationUtil.acceptEKV(massgebendesEinkommenVorjahr, massgebendesEinkommenJahr)) {
					finanzDatenDTO.setMassgebendesEinkommenBasisjahrPlus2(resultateEKV2.getMassgebendesEinkommen());
					finanzDatenDTO.setDatumVonBasisjahrPlus2(einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus2());
				}
			}
		}
		gesuch.setFinanzDatenDTO(finanzDatenDTO);
	}

	private static boolean acceptEKV(BigDecimal massgebendesEinkommenVorjahr, BigDecimal massgebendesEinkommenJahr) {
		BigDecimal minimumEKV = MathUtil.EINE_NACHKOMMASTELLE.from(0.8);
		// EKV gewährt
		return massgebendesEinkommenVorjahr.compareTo(BigDecimal.ZERO) > 0
			&& MathUtil.EXACT.divide(massgebendesEinkommenJahr, massgebendesEinkommenVorjahr).compareTo(minimumEKV) <= 0;
	}

	private static void calculateZusammen(final AbstractFinanzielleSituationResultateDTO finSitResultDTO, AbstractFinanzielleSituation finanzielleSituationGS1,
										  BigDecimal nettoJahresLohn1, BigDecimal geschaeftsgewinnDurchschnitt1, AbstractFinanzielleSituation finanzielleSituationGS2,
										  BigDecimal nettoJahresLohn2, BigDecimal geschaeftsgewinnDurchschnitt2) {

		finSitResultDTO.setEinkommenBeiderGesuchsteller(FinanzielleSituationUtil.calcEinkommen(finanzielleSituationGS1, nettoJahresLohn1,
			geschaeftsgewinnDurchschnitt1, finanzielleSituationGS2, nettoJahresLohn2, geschaeftsgewinnDurchschnitt2));
		finSitResultDTO.setNettovermoegenFuenfProzent(FinanzielleSituationUtil.calcVermoegen5Prozent(finanzielleSituationGS1, finanzielleSituationGS2));
		finSitResultDTO.setAbzuegeBeiderGesuchsteller(FinanzielleSituationUtil.calcAbzuege(finanzielleSituationGS1, finanzielleSituationGS2));

		finSitResultDTO.setAnrechenbaresEinkommen(FinanzielleSituationUtil.add(finSitResultDTO.getEinkommenBeiderGesuchsteller(), finSitResultDTO.getNettovermoegenFuenfProzent()));
		finSitResultDTO.setTotalAbzuege(FinanzielleSituationUtil.add(finSitResultDTO.getAbzuegeBeiderGesuchsteller(), finSitResultDTO.getAbzugAufgrundFamiliengroesse()));
		finSitResultDTO.setMassgebendesEinkommen(FinanzielleSituationUtil.subtract(finSitResultDTO.getAnrechenbaresEinkommen(), finSitResultDTO.getTotalAbzuege()));
	}

	/**
	 * Diese Methode aufrufen um den GeschaeftsgewinnDurchschnitt fuer die Finanzielle Situation zu berechnen.
	 * @param finanzielleSituation
	 * @return
	 */
	private static BigDecimal calcGeschaeftsgewinnDurchschnitt(FinanzielleSituation finanzielleSituation) {
		if (finanzielleSituation != null) {
			return FinanzielleSituationUtil.calcGeschaeftsgewinnDurchschnitt(finanzielleSituation.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2());
		}
		return null;
	}

	/**
	 * Diese Methode aufrufen um den GeschaeftsgewinnDurchschnitt fuer die Einkommensverschlechterung zu berechnen. Die finanzielle Situation
	 * muss auch uebergeben werden, da manche Daten aus ihr genommen werden
	 * @param finanzielleSituation
	 * @return
	 */
	private static BigDecimal calcGeschaeftsgewinnDurchschnitt(Einkommensverschlechterung einkVers, FinanzielleSituation finanzielleSituation) {
		if (finanzielleSituation != null && einkVers != null) {
			return FinanzielleSituationUtil.calcGeschaeftsgewinnDurchschnitt(einkVers.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
		}
		return null;
	}

	/**
	 * Allgemeine Methode fuer die Berechnung des GeschaeftsgewinnDurchschnitt. Die drei benoetigten Felder werden uebergeben
	 * @param geschaeftsgewinnBasisjahr
	 * @param geschaeftsgewinnBasisjahrMinus1
	 * @param geschaeftsgewinnBasisjahrMinus2
	 * @return
	 */
	private static BigDecimal calcGeschaeftsgewinnDurchschnitt(@Nullable final BigDecimal geschaeftsgewinnBasisjahr,
															   @Nullable final BigDecimal geschaeftsgewinnBasisjahrMinus1,
															   @Nullable final BigDecimal geschaeftsgewinnBasisjahrMinus2) {

		BigDecimal total = BigDecimal.ZERO;
		BigDecimal anzahlJahre = BigDecimal.ZERO;
		if (geschaeftsgewinnBasisjahrMinus2 != null) {
			total = total.add(geschaeftsgewinnBasisjahrMinus2);
			anzahlJahre = anzahlJahre.add(BigDecimal.ONE);
		}
		if (geschaeftsgewinnBasisjahrMinus1 != null) {
			total = total.add(geschaeftsgewinnBasisjahrMinus1);
			anzahlJahre = anzahlJahre.add(BigDecimal.ONE);
		}
		if (geschaeftsgewinnBasisjahr != null) {
			total = total.add(geschaeftsgewinnBasisjahr);
			anzahlJahre = anzahlJahre.add(BigDecimal.ONE);
		}
		if (anzahlJahre.intValue() > 0) {
			return total.divide(anzahlJahre, RoundingMode.HALF_UP);
		}

		return null;
	}


	private static BigDecimal calcVermoegen5Prozent(AbstractFinanzielleSituation abstractFinanzielleSituation1, AbstractFinanzielleSituation abstractFinanzielleSituation2) {
		final BigDecimal totalBruttovermoegen = FinanzielleSituationUtil.add(abstractFinanzielleSituation1 != null ? abstractFinanzielleSituation1.getBruttovermoegen() : BigDecimal.ZERO,
			abstractFinanzielleSituation2 != null ? abstractFinanzielleSituation2.getBruttovermoegen() : BigDecimal.ZERO);

		final BigDecimal totalSchulden = FinanzielleSituationUtil.add(abstractFinanzielleSituation1 != null ? abstractFinanzielleSituation1.getSchulden() : BigDecimal.ZERO,
			abstractFinanzielleSituation2 != null ? abstractFinanzielleSituation2.getSchulden() : BigDecimal.ZERO);

		BigDecimal total = FinanzielleSituationUtil.subtract(totalBruttovermoegen, totalSchulden);
		if (total.compareTo(BigDecimal.ZERO) < 0) {
			total = BigDecimal.ZERO;
		}
		total = FinanzielleSituationUtil.percent(total, 5);
		return total;
	}

	protected static BigDecimal add(BigDecimal value1, BigDecimal value2) {
		value1 = value1 != null ? value1 : BigDecimal.ZERO;
		value2 = value2 != null ? value2 : BigDecimal.ZERO;
		return value1.add(value2);
	}

	private static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
		value1 = value1 != null ? value1 : BigDecimal.ZERO;
		value2 = value2 != null ? value2 : BigDecimal.ZERO;
		return value1.subtract(value2);
	}

	private static BigDecimal percent(BigDecimal value, int percent) {
		BigDecimal total = value != null ? value : BigDecimal.ZERO;
		total = total.multiply(new BigDecimal("" + percent));
		total = total.divide(new BigDecimal("100"), RoundingMode.HALF_UP);
		return total;
	}

	private static BigDecimal calcEinkommen(AbstractFinanzielleSituation abstractFinanzielleSituation1, BigDecimal nettoJahresLohn1, BigDecimal geschaeftsgewinnDurchschnitt1,
									 AbstractFinanzielleSituation abstractFinanzielleSituation2, BigDecimal nettoJahresLohn2, BigDecimal geschaeftsgewinnDurchschnitt2) {
		BigDecimal total = BigDecimal.ZERO;
		total = FinanzielleSituationUtil.calcEinkommenProGS(abstractFinanzielleSituation1, nettoJahresLohn1, geschaeftsgewinnDurchschnitt1, total);
		total = FinanzielleSituationUtil.calcEinkommenProGS(abstractFinanzielleSituation2, nettoJahresLohn2, geschaeftsgewinnDurchschnitt2, total);
		return total;
	}

	private static BigDecimal calcEinkommenProGS(AbstractFinanzielleSituation abstractFinanzielleSituation, BigDecimal nettoJahresLohn,
												 BigDecimal geschaeftsgewinnDurchschnitt, BigDecimal total) {
		if (abstractFinanzielleSituation != null) {
			total = FinanzielleSituationUtil.add(total, nettoJahresLohn);
			total = FinanzielleSituationUtil.add(total, abstractFinanzielleSituation.getFamilienzulage());
			total = FinanzielleSituationUtil.add(total, abstractFinanzielleSituation.getErsatzeinkommen());
			total = FinanzielleSituationUtil.add(total, abstractFinanzielleSituation.getErhalteneAlimente());
			total = FinanzielleSituationUtil.add(total, geschaeftsgewinnDurchschnitt);
		}
		return total;
	}

	private static BigDecimal calcAbzuege(AbstractFinanzielleSituation finanzielleSituationGS1, AbstractFinanzielleSituation finanzielleSituationGS2) {
		BigDecimal totalAbzuege = BigDecimal.ZERO;
		if (finanzielleSituationGS1 != null) {
			totalAbzuege = FinanzielleSituationUtil.add(totalAbzuege, finanzielleSituationGS1.getGeleisteteAlimente());
		}
		if (finanzielleSituationGS2 != null) {
			totalAbzuege = FinanzielleSituationUtil.add(totalAbzuege, finanzielleSituationGS2.getGeleisteteAlimente());
		}
		return totalAbzuege;
	}

	/**
	 * Berechnet die NettoJahresLohn fuer ein Einkommensverschlechterung
	 * @param einkommensverschlechterung
	 * @return
	 */
	private static BigDecimal calculateNettoJahresLohn(Einkommensverschlechterung einkommensverschlechterung) {
		BigDecimal total = BigDecimal.ZERO;
		if (einkommensverschlechterung != null) {
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnJan());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnFeb());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnMrz());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnApr());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnMai());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnJun());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnJul());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnAug());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnSep());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnOkt());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnNov());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnDez());
			total = FinanzielleSituationUtil.add(total, einkommensverschlechterung.getNettolohnZus());
		}
		return total;
	}

	/**
	 * Berechnet die NettoJahresLohn fuer eine Finanzielle Situation
	 * @param finanzielleSituation
	 * @return
	 */
	private static BigDecimal calculateNettoJahresLohn(FinanzielleSituation finanzielleSituation) {
		if (finanzielleSituation != null) {
			return finanzielleSituation.getNettolohn();
		}
		return BigDecimal.ZERO;
	}

	private static Einkommensverschlechterung getEinkommensverschlechterungGS(Gesuchsteller gesuchsteller, int basisJahrPlus) {
		if (gesuchsteller != null) {
			Validate.notNull(gesuchsteller.getEinkommensverschlechterungContainer());
			if (basisJahrPlus == 2) {
				return gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2();
			} else {
				return gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1();
			}
		}
		return null;
	}

	private static FinanzielleSituation getFinanzielleSituationGS(Gesuchsteller gesuchsteller) {
		if (gesuchsteller != null && gesuchsteller.getFinanzielleSituationContainer() != null) {
			return gesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationSV();
		}
		return null;
	}
}
