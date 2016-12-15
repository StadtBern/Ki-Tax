package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ein Rechner mit den ganzen Operationen fuer Finanziellesituation
 * Created by imanol on 22.06.16.
 */
@Dependent
public class FinanzielleSituationRechner {

	/**
	 * Konstruktor, welcher einen Rechner erstellt, der die Paramter aus der DB liest
	 */
	public FinanzielleSituationRechner() {

	}

	@Nonnull
	public FinanzielleSituationResultateDTO calculateResultateFinanzielleSituation(@Nonnull Gesuch gesuch) {

		final FinanzielleSituationResultateDTO finSitResultDTO = new FinanzielleSituationResultateDTO();
		setFinanzielleSituationParameters(gesuch, finSitResultDTO);

		return finSitResultDTO;
	}

	/**
	 * Diese Methode wird momentan im Print gebraucht um die Finanzielle Situation zu berechnen. Der Abzug aufgrund Familiengroesse wird
	 * hier auf 0 gesetzt
	 *
	 * @param gesuch
	 * @param basisJahrPlus
	 * @return
	 */

	@Nonnull
	public FinanzielleSituationResultateDTO calculateResultateEinkommensverschlechterung(@Nonnull Gesuch gesuch, int basisJahrPlus) {
		Validate.notNull(gesuch.extractEinkommensverschlechterungInfo());

		final FinanzielleSituationResultateDTO einkVerResultDTO = new FinanzielleSituationResultateDTO();
		setEinkommensverschlechterungParameters(gesuch, basisJahrPlus, einkVerResultDTO);

		return einkVerResultDTO;
	}

	/**
	 * Nimmt das uebergebene FinanzielleSituationResultateDTO und mit den Daten vom Gesuch, berechnet alle im
	 * FinanzielleSituationResultateDTO benoetigten Daten und setzt sie direkt im dto.
	 *
	 * @param gesuch
	 * @param finSitResultDTO
	 */
	private void setFinanzielleSituationParameters(@Nonnull Gesuch gesuch, final FinanzielleSituationResultateDTO finSitResultDTO) {
		final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		finSitResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS1));

		final FinanzielleSituation finanzielleSituationGS2 = getFinanzielleSituationGS(gesuch.getGesuchsteller2());
		finSitResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS2));

		calculateZusammen(finSitResultDTO, finanzielleSituationGS1,
			calculateNettoJahresLohn(finanzielleSituationGS1),
			finSitResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller1(),
			finanzielleSituationGS2, calculateNettoJahresLohn(finanzielleSituationGS2),
			finSitResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller2());
	}

	/**
	 * Nimmt das uebergebene FinanzielleSituationResultateDTO und mit den Daten vom Gesuch, berechnet alle im
	 * FinanzielleSituationResultateDTO benoetigten Daten.
	 *
	 * @param gesuch
	 * @param basisJahrPlus
	 * @param einkVerResultDTO
	 */
	private void setEinkommensverschlechterungParameters(@Nonnull Gesuch gesuch, int basisJahrPlus,
														 final FinanzielleSituationResultateDTO einkVerResultDTO) {
		Einkommensverschlechterung einkommensverschlechterungGS1_Bjp1 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), 1);
		Einkommensverschlechterung einkommensverschlechterungGS1_Bjp2 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), 2);
		final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(
			calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS1,einkommensverschlechterungGS1_Bjp1, einkommensverschlechterungGS1_Bjp2, basisJahrPlus));

		Einkommensverschlechterung einkommensverschlechterungGS2_Bjp1 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), 1);
		Einkommensverschlechterung einkommensverschlechterungGS2_Bjp2 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), 2);
		final FinanzielleSituation finanzielleSituationGS2 = getFinanzielleSituationGS(gesuch.getGesuchsteller2());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(
			calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS2,einkommensverschlechterungGS2_Bjp1, einkommensverschlechterungGS2_Bjp2, basisJahrPlus));

		if (basisJahrPlus == 2) {
			calculateZusammen(einkVerResultDTO, einkommensverschlechterungGS1_Bjp2,
				calculateNettoJahresLohn(einkommensverschlechterungGS1_Bjp2),
				einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller1(),
				einkommensverschlechterungGS2_Bjp2, calculateNettoJahresLohn(einkommensverschlechterungGS2_Bjp2),
				einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller2());
		} else {
			calculateZusammen(einkVerResultDTO, einkommensverschlechterungGS1_Bjp1,
				calculateNettoJahresLohn(einkommensverschlechterungGS1_Bjp1),
				einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller1(),
				einkommensverschlechterungGS2_Bjp1, calculateNettoJahresLohn(einkommensverschlechterungGS2_Bjp1),
				einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller2());
		}



	}


	/**
	 * Berechnet das FinazDaten DTO fuer die Finanzielle Situation
	 *
	 * @param gesuch das Gesuch dessen finazDatenDTO gesetzt werden soll
	 */
	public void calculateFinanzDaten(@Nonnull Gesuch gesuch) {
		FinanzDatenDTO finanzDatenDTO = new FinanzDatenDTO();

		// Finanzielle Situation berechnen
		FinanzielleSituationResultateDTO finanzielleSituationResultateDTO = calculateResultateFinanzielleSituation(gesuch);
		finanzDatenDTO.setMassgebendesEinkBjVorAbzFamGr(finanzielleSituationResultateDTO.getMassgebendesEinkVorAbzFamGr());
		finanzDatenDTO.setDatumVonBasisjahr(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());
		//Berechnung wird nur ausgefuehrt wenn Daten vorhanden, wenn es keine gibt machen wir nichts
		if (gesuch.extractEinkommensverschlechterungInfo() != null && gesuch.extractEinkommensverschlechterungInfo().getEinkommensverschlechterung()) {
			EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.extractEinkommensverschlechterungInfo();
			FinanzielleSituationResultateDTO resultateEKV1 = calculateResultateEinkommensverschlechterung(gesuch, 1);
			if (gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1() != null && gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1()) {
				// In der EKV 1 vergleichen wir immer mit dem Basisjahr
				BigDecimal massgebendesEinkommenVorjahr = finanzielleSituationResultateDTO.getMassgebendesEinkVorAbzFamGr();
				BigDecimal massgebendesEinkommenJahr = resultateEKV1.getMassgebendesEinkVorAbzFamGr();
				if (acceptEKV(massgebendesEinkommenVorjahr, massgebendesEinkommenJahr)) {
					finanzDatenDTO.setMassgebendesEinkBjP1VorAbzFamGr(resultateEKV1.getMassgebendesEinkVorAbzFamGr());
					finanzDatenDTO.setDatumVonBasisjahrPlus1(einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus1());
				}
			}
			if (gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2() != null && gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2()) {
				FinanzielleSituationResultateDTO resultateEKV2 = calculateResultateEinkommensverschlechterung(gesuch, 2);
				// In der EKV 2 vergleichen wir immer mit dem EKV 1, egal ob diese akzeptiert war
				BigDecimal massgebendesEinkommenVorjahr = resultateEKV1.getMassgebendesEinkVorAbzFamGr();
				BigDecimal massgebendesEinkommenJahr = resultateEKV2.getMassgebendesEinkVorAbzFamGr();
				if (acceptEKV(massgebendesEinkommenVorjahr, massgebendesEinkommenJahr)) {
					finanzDatenDTO.setMassgebendesEinkBjP2VorAbzFamGr(resultateEKV2.getMassgebendesEinkVorAbzFamGr());
					finanzDatenDTO.setDatumVonBasisjahrPlus2(einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus2());
				}
			}
		}
		gesuch.setFinanzDatenDTO(finanzDatenDTO);
	}

	/**
	 * @return Berechnet ob die Einkommensverschlechterung mehr als 20 % gegenueber dem vorjahr betraegt, gibt true zurueckk wen ja; false sonst
	 */
	private boolean acceptEKV(BigDecimal massgebendesEinkommenVorjahr, BigDecimal massgebendesEinkommenJahr) {
		BigDecimal minimumEKV = MathUtil.EINE_NACHKOMMASTELLE.from(0.8);
		// EKV gewährt
		return massgebendesEinkommenVorjahr.compareTo(BigDecimal.ZERO) > 0
			&& MathUtil.EXACT.divide(massgebendesEinkommenJahr, massgebendesEinkommenVorjahr).compareTo(minimumEKV) <= 0;
	}

	private void calculateZusammen(final FinanzielleSituationResultateDTO finSitResultDTO,
								   AbstractFinanzielleSituation finanzielleSituationGS1,
								   BigDecimal nettoJahresLohn1, BigDecimal geschaeftsgewinnDurchschnitt1,
								   AbstractFinanzielleSituation finanzielleSituationGS2,
								   BigDecimal nettoJahresLohn2, BigDecimal geschaeftsgewinnDurchschnitt2) {

		finSitResultDTO.setEinkommenBeiderGesuchsteller(calcEinkommen(finanzielleSituationGS1, nettoJahresLohn1,
			geschaeftsgewinnDurchschnitt1, finanzielleSituationGS2, nettoJahresLohn2, geschaeftsgewinnDurchschnitt2));
		finSitResultDTO.setNettovermoegenFuenfProzent(calcVermoegen5Prozent(finanzielleSituationGS1, finanzielleSituationGS2));
		finSitResultDTO.setAbzuegeBeiderGesuchsteller(calcAbzuege(finanzielleSituationGS1, finanzielleSituationGS2));

		finSitResultDTO.setAnrechenbaresEinkommen(add(finSitResultDTO.getEinkommenBeiderGesuchsteller(), finSitResultDTO.getNettovermoegenFuenfProzent()));
		finSitResultDTO.setMassgebendesEinkVorAbzFamGr(
			MathUtil.positiveNonNullAndRound(
				subtract(finSitResultDTO.getAnrechenbaresEinkommen(), finSitResultDTO.getAbzuegeBeiderGesuchsteller())));
	}

	/**
	 * Diese Methode aufrufen um den GeschaeftsgewinnDurchschnitt fuer die Finanzielle Situation zu berechnen.
	 *
	 * @param finanzielleSituation
	 * @return
	 */
	@Nullable
	public static BigDecimal calcGeschaeftsgewinnDurchschnitt(@Nullable FinanzielleSituation finanzielleSituation) {
		if (finanzielleSituation != null) {
			return calcGeschaeftsgewinnDurchschnitt(finanzielleSituation.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2());
		}
		return null;
	}

	/**
	 * Diese Methode aufrufen um den GeschaeftsgewinnDurchschnitt fuer die Einkommensverschlechterung zu berechnen. Die finanzielle Situation
	 * muss auch uebergeben werden, da manche Daten aus ihr genommen werden
	 *
	 * @param finanzielleSituation
	 * @return
	 */
	@Nullable
	public static BigDecimal calcGeschaeftsgewinnDurchschnitt(@Nullable FinanzielleSituation finanzielleSituation,
															  @Nullable Einkommensverschlechterung einkVersBjp1,
															  @Nullable Einkommensverschlechterung einkVersBjp2,
															  int basisJahrPlus) {
		if (basisJahrPlus == 1) {
			if (finanzielleSituation != null && einkVersBjp1 != null) {
				return calcGeschaeftsgewinnDurchschnitt(einkVersBjp1.getGeschaeftsgewinnBasisjahr(),
					finanzielleSituation.getGeschaeftsgewinnBasisjahr(),
					finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
			}
		}
		else if (basisJahrPlus == 2 && finanzielleSituation != null && einkVersBjp1 != null && einkVersBjp2 != null) {
			return calcGeschaeftsgewinnDurchschnitt(einkVersBjp2.getGeschaeftsgewinnBasisjahr(),
				einkVersBjp1.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahr());
		}
		return null;
	}

	/**
	 * Allgemeine Methode fuer die Berechnung des GeschaeftsgewinnDurchschnitt. Die drei benoetigten Felder werden uebergeben
	 *
	 * @param geschaeftsgewinnBasisjahr
	 * @param geschaeftsgewinnBasisjahrMinus1
	 * @param geschaeftsgewinnBasisjahrMinus2
	 * @return
	 */
	@Nullable
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
			final BigDecimal divided = total.divide(anzahlJahre, RoundingMode.HALF_UP);
			// Durschnitt darf NIE kleiner als 0 sein
			return divided.intValue() >= 0 ? divided : BigDecimal.ZERO;
		}

		return null;
	}


	/**
	 * Berechnet 5 prozent des Nettovermoegens von GS1 und GS2. Der Gesamtwert kann dabei nicht kleiner als 0 sein auch
	 * wenn ein einzelner Gesuchsteller ein negatives Nettovermoegen hat.
	 */
	public static BigDecimal calcVermoegen5Prozent(@Nullable AbstractFinanzielleSituation abstractFinanzielleSituation1, @Nullable AbstractFinanzielleSituation abstractFinanzielleSituation2) {
		final BigDecimal totalBruttovermoegen = add(abstractFinanzielleSituation1 != null ? abstractFinanzielleSituation1.getBruttovermoegen() : BigDecimal.ZERO,
			abstractFinanzielleSituation2 != null ? abstractFinanzielleSituation2.getBruttovermoegen() : BigDecimal.ZERO);

		final BigDecimal totalSchulden = add(abstractFinanzielleSituation1 != null ? abstractFinanzielleSituation1.getSchulden() : BigDecimal.ZERO,
			abstractFinanzielleSituation2 != null ? abstractFinanzielleSituation2.getSchulden() : BigDecimal.ZERO);

		BigDecimal total = subtract(totalBruttovermoegen, totalSchulden);
		if (total.compareTo(BigDecimal.ZERO) < 0) {
			total = BigDecimal.ZERO;
		} //total vermoegen + schulden muss gruesser null sein, individuell pro gs kann es aber negativ sein
		total = percent(total, 5);
		return MathUtil.GANZZAHL.from(total);
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

	private BigDecimal calcEinkommen(AbstractFinanzielleSituation abstractFinanzielleSituation1, BigDecimal nettoJahresLohn1, BigDecimal geschaeftsgewinnDurchschnitt1,
									 AbstractFinanzielleSituation abstractFinanzielleSituation2, BigDecimal nettoJahresLohn2, BigDecimal geschaeftsgewinnDurchschnitt2) {
		BigDecimal total = BigDecimal.ZERO;
		total = calcEinkommenProGS(abstractFinanzielleSituation1, nettoJahresLohn1, geschaeftsgewinnDurchschnitt1, total);
		total = calcEinkommenProGS(abstractFinanzielleSituation2, nettoJahresLohn2, geschaeftsgewinnDurchschnitt2, total);
		return total;
	}

	private BigDecimal calcEinkommenProGS(AbstractFinanzielleSituation abstractFinanzielleSituation, BigDecimal nettoJahresLohn,
										  BigDecimal geschaeftsgewinnDurchschnitt, BigDecimal total) {
		if (abstractFinanzielleSituation != null) {
			total = add(total, nettoJahresLohn);
			total = add(total, abstractFinanzielleSituation.getFamilienzulage());
			total = add(total, abstractFinanzielleSituation.getErsatzeinkommen());
			total = add(total, abstractFinanzielleSituation.getErhalteneAlimente());
			total = add(total, geschaeftsgewinnDurchschnitt);
		}
		return total;
	}

	private BigDecimal calcAbzuege(AbstractFinanzielleSituation finanzielleSituationGS1, AbstractFinanzielleSituation finanzielleSituationGS2) {
		BigDecimal totalAbzuege = BigDecimal.ZERO;
		if (finanzielleSituationGS1 != null) {
			totalAbzuege = add(totalAbzuege, finanzielleSituationGS1.getGeleisteteAlimente());
		}
		if (finanzielleSituationGS2 != null) {
			totalAbzuege = add(totalAbzuege, finanzielleSituationGS2.getGeleisteteAlimente());
		}
		return totalAbzuege;
	}

	/**
	 * Berechnet die NettoJahresLohn fuer ein Einkommensverschlechterung
	 *
	 * @param einkommensverschlechterung
	 * @return
	 */
	private BigDecimal calculateNettoJahresLohn(Einkommensverschlechterung einkommensverschlechterung) {
		BigDecimal total = BigDecimal.ZERO;
		if (einkommensverschlechterung != null) {
			total = add(total, einkommensverschlechterung.getNettolohnJan());
			total = add(total, einkommensverschlechterung.getNettolohnFeb());
			total = add(total, einkommensverschlechterung.getNettolohnMrz());
			total = add(total, einkommensverschlechterung.getNettolohnApr());
			total = add(total, einkommensverschlechterung.getNettolohnMai());
			total = add(total, einkommensverschlechterung.getNettolohnJun());
			total = add(total, einkommensverschlechterung.getNettolohnJul());
			total = add(total, einkommensverschlechterung.getNettolohnAug());
			total = add(total, einkommensverschlechterung.getNettolohnSep());
			total = add(total, einkommensverschlechterung.getNettolohnOkt());
			total = add(total, einkommensverschlechterung.getNettolohnNov());
			total = add(total, einkommensverschlechterung.getNettolohnDez());
			total = add(total, einkommensverschlechterung.getNettolohnZus());
		}
		return total;
	}

	/**
	 * Berechnet die NettoJahresLohn fuer eine Finanzielle Situation
	 *
	 * @param finanzielleSituation
	 * @return
	 */
	private BigDecimal calculateNettoJahresLohn(FinanzielleSituation finanzielleSituation) {
		if (finanzielleSituation != null) {
			return finanzielleSituation.getNettolohn();
		}
		return BigDecimal.ZERO;
	}

	private Einkommensverschlechterung getEinkommensverschlechterungGS(GesuchstellerContainer gesuchsteller, int basisJahrPlus) {
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

	private FinanzielleSituation getFinanzielleSituationGS(GesuchstellerContainer gesuchsteller) {
		if (gesuchsteller != null && gesuchsteller.getFinanzielleSituationContainer() != null) {
			return gesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationJA();
		}
		return null;
	}

}
