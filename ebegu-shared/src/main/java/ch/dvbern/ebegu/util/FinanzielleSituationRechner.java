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
import java.time.LocalDate;

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
	public FinanzielleSituationResultateDTO calculateResultateFinanzielleSituation(@Nonnull Gesuch gesuch, boolean hasSecondGesuchsteller) {

		final FinanzielleSituationResultateDTO finSitResultDTO = new FinanzielleSituationResultateDTO();
		setFinanzielleSituationParameters(gesuch, finSitResultDTO, hasSecondGesuchsteller);

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
	public FinanzielleSituationResultateDTO calculateResultateEinkommensverschlechterung(@Nonnull Gesuch gesuch, int basisJahrPlus, boolean hasSecondGesuchsteller) {
		Validate.notNull(gesuch.extractEinkommensverschlechterungInfo());

		final FinanzielleSituationResultateDTO einkVerResultDTO = new FinanzielleSituationResultateDTO();
		setEinkommensverschlechterungParameters(gesuch, basisJahrPlus, einkVerResultDTO, hasSecondGesuchsteller);

		return einkVerResultDTO;
	}

	/**
	 * Nimmt das uebergebene FinanzielleSituationResultateDTO und mit den Daten vom Gesuch, berechnet alle im
	 * FinanzielleSituationResultateDTO benoetigten Daten und setzt sie direkt im dto.
	 *
	 * @param gesuch
	 * @param finSitResultDTO
	 */
	private void setFinanzielleSituationParameters(@Nonnull Gesuch gesuch, final FinanzielleSituationResultateDTO finSitResultDTO, boolean hasSecondGesuchsteller) {
		final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		finSitResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS1));

		// Die Daten fuer GS 2 werden nur beruecksichtigt, wenn es (aktuell) zwei Gesuchsteller hat
		FinanzielleSituation finanzielleSituationGS2 = null;
		if (hasSecondGesuchsteller && gesuch.getGesuchsteller2() != null) {
			finanzielleSituationGS2 = getFinanzielleSituationGS(gesuch.getGesuchsteller2());
			finSitResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS2));
		}

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
														 final FinanzielleSituationResultateDTO einkVerResultDTO, boolean hasSecondGesuchsteller) {
		Einkommensverschlechterung einkommensverschlechterungGS1_Bjp1 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), 1);
		Einkommensverschlechterung einkommensverschlechterungGS1_Bjp2 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), 2);
		final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(
			calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS1, einkommensverschlechterungGS1_Bjp1, einkommensverschlechterungGS1_Bjp2,
				gesuch.extractEinkommensverschlechterungInfo(), basisJahrPlus));

		// Die Daten fuer GS 2 werden nur beruecksichtigt, wenn es (aktuell) zwei Gesuchsteller hat
		Einkommensverschlechterung einkommensverschlechterungGS2_Bjp1 = null;
		Einkommensverschlechterung einkommensverschlechterungGS2_Bjp2 = null;
		if (hasSecondGesuchsteller) {
			einkommensverschlechterungGS2_Bjp1 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), 1);
			einkommensverschlechterungGS2_Bjp2 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), 2);
			final FinanzielleSituation finanzielleSituationGS2 = getFinanzielleSituationGS(gesuch.getGesuchsteller2());
			einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(
				calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS2, einkommensverschlechterungGS2_Bjp1,
				einkommensverschlechterungGS2_Bjp2, gesuch.extractEinkommensverschlechterungInfo(), basisJahrPlus));
		}

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
	public void calculateFinanzDaten(@Nonnull Gesuch gesuch, BigDecimal minimumEKV) {
		FinanzDatenDTO finanzDatenDTO_alleine = new FinanzDatenDTO();
		FinanzDatenDTO finanzDatenDTO_zuZweit = new FinanzDatenDTO();

		// Finanzielle Situation berechnen
		FinanzielleSituationResultateDTO finanzielleSituationResultateDTO_alleine = calculateResultateFinanzielleSituation(gesuch, false);
		FinanzielleSituationResultateDTO finanzielleSituationResultateDTO_zuZweit = calculateResultateFinanzielleSituation(gesuch, true);

		finanzDatenDTO_alleine.setMassgebendesEinkBjVorAbzFamGr(finanzielleSituationResultateDTO_alleine.getMassgebendesEinkVorAbzFamGr());
		finanzDatenDTO_alleine.setDatumVonBasisjahr(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());
		finanzDatenDTO_zuZweit.setMassgebendesEinkBjVorAbzFamGr(finanzielleSituationResultateDTO_zuZweit.getMassgebendesEinkVorAbzFamGr());
		finanzDatenDTO_zuZweit.setDatumVonBasisjahr(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());

		//Berechnung wird nur ausgefuehrt wenn Daten vorhanden, wenn es keine gibt machen wir nichts
		if (gesuch.extractEinkommensverschlechterungInfo() != null && gesuch.extractEinkommensverschlechterungInfo().getEinkommensverschlechterung()) {
			EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.extractEinkommensverschlechterungInfo();
			FinanzielleSituationResultateDTO resultateEKV1_alleine = calculateResultateEinkommensverschlechterung(gesuch, 1, false);
			FinanzielleSituationResultateDTO resultateEKV1_zuZweit = calculateResultateEinkommensverschlechterung(gesuch, 1, true);
			BigDecimal massgebendesEinkommenBasisjahr_alleine = finanzielleSituationResultateDTO_alleine.getMassgebendesEinkVorAbzFamGr();
			BigDecimal massgebendesEinkommenBasisjahr_zuZweit = finanzielleSituationResultateDTO_zuZweit.getMassgebendesEinkVorAbzFamGr();
			if (gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1() != null && gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1()) {
				// In der EKV 1 vergleichen wir immer mit dem Basisjahr
				handleEKV1(finanzDatenDTO_alleine, einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus1(), resultateEKV1_alleine.getMassgebendesEinkVorAbzFamGr(),
					massgebendesEinkommenBasisjahr_alleine, minimumEKV);
				handleEKV1(finanzDatenDTO_zuZweit, einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus1(), resultateEKV1_zuZweit.getMassgebendesEinkVorAbzFamGr(),
					massgebendesEinkommenBasisjahr_zuZweit, minimumEKV);
			}

			BigDecimal massgebendesEinkommenVorjahr_alleine;
			if (finanzDatenDTO_alleine.isEkv1Accepted()) {
				massgebendesEinkommenVorjahr_alleine = resultateEKV1_alleine.getMassgebendesEinkVorAbzFamGr();
			} else {
				massgebendesEinkommenVorjahr_alleine = massgebendesEinkommenBasisjahr_alleine;
			}
			BigDecimal massgebendesEinkommenVorjahr_zuZweit;
			if (gesuch.extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1()) {
				massgebendesEinkommenVorjahr_zuZweit = resultateEKV1_zuZweit.getMassgebendesEinkVorAbzFamGr();
			} else {
				massgebendesEinkommenVorjahr_zuZweit = massgebendesEinkommenBasisjahr_zuZweit;
			}


			if (einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2() != null && einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2()) {

				FinanzielleSituationResultateDTO resultateEKV2_alleine = calculateResultateEinkommensverschlechterung(gesuch, 2, false);
				FinanzielleSituationResultateDTO resultateEKV2_zuZweit = calculateResultateEinkommensverschlechterung(gesuch, 2, true);
				// In der EKV 2 vergleichen wir immer mit dem EKV 1, egal ob diese akzeptiert war
				handleEKV2(finanzDatenDTO_alleine, einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus2(),
					resultateEKV2_alleine.getMassgebendesEinkVorAbzFamGr(), massgebendesEinkommenVorjahr_alleine,
					massgebendesEinkommenBasisjahr_alleine, minimumEKV);
				handleEKV2(finanzDatenDTO_zuZweit, einkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus2(),
					resultateEKV2_zuZweit.getMassgebendesEinkVorAbzFamGr(), massgebendesEinkommenVorjahr_zuZweit,
					massgebendesEinkommenBasisjahr_zuZweit, minimumEKV);
			} else {
				finanzDatenDTO_alleine.setMassgebendesEinkBjP2VorAbzFamGr(massgebendesEinkommenVorjahr_alleine);
				finanzDatenDTO_zuZweit.setMassgebendesEinkBjP2VorAbzFamGr(massgebendesEinkommenVorjahr_zuZweit);
			}
		}
		gesuch.setFinanzDatenDTO_alleine(finanzDatenDTO_alleine);
		gesuch.setFinanzDatenDTO_zuZweit(finanzDatenDTO_zuZweit);
	}

	private void handleEKV1(FinanzDatenDTO finanzDatenDTO, LocalDate stichtagEKV1, BigDecimal massgebendesEinkommenEKV1, BigDecimal massgebendesEinkommenBasisjahr,
							BigDecimal minimumEKV) {
        // In der EKV 1 vergleichen wir immer mit dem Basisjahr
		finanzDatenDTO.setDatumVonBasisjahrPlus1(stichtagEKV1);
		boolean accepted = acceptEKV(massgebendesEinkommenBasisjahr, massgebendesEinkommenEKV1, minimumEKV);
		finanzDatenDTO.setEkv1Accepted(accepted);
		if (accepted) {
			finanzDatenDTO.setMassgebendesEinkBjP1VorAbzFamGr(massgebendesEinkommenEKV1);
		} else {
			finanzDatenDTO.setMassgebendesEinkBjP1VorAbzFamGr(massgebendesEinkommenBasisjahr);
		}
	}

	private void handleEKV2(FinanzDatenDTO finanzDatenDTO, LocalDate stichtagEKV2, BigDecimal massgebendesEinkommenEKV2, BigDecimal massgebendesEinkommenVorjahr,
							BigDecimal massgebendesEinkommenBasisjahr, BigDecimal minimumEKV) {
		// In der EKV 2 vergleichen wir immer mit dem EKV 1, egal ob diese akzeptiert war
		finanzDatenDTO.setDatumVonBasisjahrPlus2(stichtagEKV2);
		boolean ekv2AlleineAccepted = acceptEKV2(massgebendesEinkommenVorjahr, massgebendesEinkommenBasisjahr, massgebendesEinkommenEKV2, minimumEKV);
		finanzDatenDTO.setEkv2Accepted(ekv2AlleineAccepted);
		if (ekv2AlleineAccepted) {
			finanzDatenDTO.setMassgebendesEinkBjP2VorAbzFamGr(massgebendesEinkommenEKV2);
		} else {
			finanzDatenDTO.setMassgebendesEinkBjP2VorAbzFamGr(massgebendesEinkommenVorjahr);
		}
	}

	/**
	 * @return Berechnet ob die Einkommensverschlechterung mehr als 20 % gegenueber dem vorjahr betraegt, gibt true zurueckk wen ja; false sonst
	 */
	private boolean acceptEKV(BigDecimal massgebendesEinkommenBasisjahr, BigDecimal massgebendesEinkommenJahr, BigDecimal minimumEKV) {
		// EKV gewährt. Es braucht VIER_NACHKOMMASTELLE weil wir mit 1-Prozentuell arbeiten und in 100-Prozentuell gilt ZWEI_NACHKOMMASTELLE
		return massgebendesEinkommenBasisjahr.compareTo(BigDecimal.ZERO) > 0
			&& MathUtil.VIER_NACHKOMMASTELLE.divide(massgebendesEinkommenJahr, massgebendesEinkommenBasisjahr).compareTo(minimumEKV) < 0;
	}

	/**
	 * @return Die Einkommensverschlechterung II kommt zum Zuge, falls diese grösser als die Einkommensverschlechterung I ist und auch grösser 20%
	 */
	private boolean acceptEKV2(BigDecimal massgebendesEinkommenVorjahr, BigDecimal massgebendesEinkommenBasisjahr, BigDecimal massgebendesEinkommenJahr,
							   BigDecimal minimumEKV) {
		// EKV gewährt. Es braucht VIER_NACHKOMMASTELLE weil wir mit 1-Prozentuell arbeiten und in 100-Prozentuell gilt ZWEI_NACHKOMMASTELLE
		return massgebendesEinkommenBasisjahr.compareTo(BigDecimal.ZERO) > 0 &&
			massgebendesEinkommenVorjahr.compareTo(massgebendesEinkommenJahr) > 0 &&
			MathUtil.VIER_NACHKOMMASTELLE.divide(massgebendesEinkommenJahr, massgebendesEinkommenBasisjahr).compareTo(minimumEKV) < 0;
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
	 */
	@Nullable
	public static BigDecimal calcGeschaeftsgewinnDurchschnitt(@Nullable FinanzielleSituation finanzielleSituation,
															  @Nullable Einkommensverschlechterung einkVersBjp1,
															  @Nullable Einkommensverschlechterung einkVersBjp2,
															  @Nullable EinkommensverschlechterungInfo ekvi,
															  int basisJahrPlus) {
		if (basisJahrPlus == 1) {
			if (finanzielleSituation != null && einkVersBjp1 != null) {
				return calcGeschaeftsgewinnDurchschnitt(einkVersBjp1.getGeschaeftsgewinnBasisjahr(),
					finanzielleSituation.getGeschaeftsgewinnBasisjahr(),
					finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
			}
		} else if (basisJahrPlus == 2 && finanzielleSituation != null && einkVersBjp2 != null) {
			if (ekvi != null && ekvi.getEkvFuerBasisJahrPlus1() && einkVersBjp1 != null) {
				return calcGeschaeftsgewinnDurchschnitt(einkVersBjp2.getGeschaeftsgewinnBasisjahr(),
					einkVersBjp1.getGeschaeftsgewinnBasisjahr(),
					finanzielleSituation.getGeschaeftsgewinnBasisjahr());
			} else {
				return calcGeschaeftsgewinnDurchschnitt(einkVersBjp2.getGeschaeftsgewinnBasisjahr(),
					einkVersBjp2.getGeschaeftsgewinnBasisjahrMinus1(),
					finanzielleSituation.getGeschaeftsgewinnBasisjahr());
			}
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
		if (gesuchsteller != null && gesuchsteller.getEinkommensverschlechterungContainer() != null) {
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
