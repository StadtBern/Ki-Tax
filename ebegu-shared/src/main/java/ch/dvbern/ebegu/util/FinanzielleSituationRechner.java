package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.services.EbeguParameterService;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Ein Rechner mit den ganzen Operationen fuer Finanziellesituation
 * Created by imanol on 22.06.16.
 */
@Dependent
public class FinanzielleSituationRechner {

	private BigDecimal abzugFamiliengroesse3;
	private BigDecimal abzugFamiliengroesse4;
	private BigDecimal abzugFamiliengroesse5;
	private BigDecimal abzugFamiliengroesse6;

	/**
	 * Konstruktor, welcher einen Rechner erstellt, der die Paramter aus der DB liest
	 */
	public FinanzielleSituationRechner() {

	}

	/**
	 * Konstruktor, welcher die Parameter miterhält. Dieser kann in JUnit Tests verwendet werden
	 */
	public FinanzielleSituationRechner(BigDecimal abzugFamiliengroesse3, BigDecimal abzugFamiliengroesse4, BigDecimal abzugFamiliengroesse5, BigDecimal abzugFamiliengroesse6) {
		this.abzugFamiliengroesse3 = abzugFamiliengroesse3;
		this.abzugFamiliengroesse4 = abzugFamiliengroesse4;
		this.abzugFamiliengroesse5 = abzugFamiliengroesse5;
		this.abzugFamiliengroesse6 = abzugFamiliengroesse6;
	}

	@Inject
	private EbeguParameterService ebeguParameterService;

	/**
	 * Die Familiengroesse wird folgendermassen kalkuliert:
	 * Familiengrösse = Gesuchsteller1 + Gesuchsteller2 (falls vorhanden) + Faktor Steuerabzug pro Kind (0, 0.5, oder 1)
	 * <p>
	 * Der Faktor wird gemaess Wert des Felds kinderabzug von Kind berechnet:
	 * KEIN_ABZUG = 0
	 * HALBER_ABZUG = 0.5
	 * GANZER_ABZUG = 1
	 * KEINE_STEUERERKLAERUNG = 1
	 * <p>
	 * Nur die Kinder die nach dem uebergebenen Datum geboren sind werden mitberechnet
	 * <p>
	 * 8tung: Bei der Berechnung der Einkommensverschlechterung werden die aktuellen Familienverhältnisse berücksichtigt
	 * (nicht Stand 31.12. des Vorjahres)!
	 *
	 * @param gesuch das Gesuch aus welchem die Daten geholt werden
	 * @param date   das Datum fuer das die familiengroesse kalkuliert werden muss oder null für Einkommensverschlechterung
	 * @return die familiengroesse als double
	 */
	public double calculateFamiliengroesse(Gesuch gesuch, @Nullable LocalDate date) {
		double familiengroesse = 0;
		if (gesuch != null) {
			if (gesuch.getGesuchsteller1() != null) {
				familiengroesse++;
			}
			if (gesuch.getGesuchsteller2() != null) {
				familiengroesse++;
			}
			for (KindContainer kindContainer : gesuch.getKindContainers()) {
				if (kindContainer.getKindJA() != null && (date == null || kindContainer.getKindJA().getGeburtsdatum().isBefore(date))) {
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
		Optional<EbeguParameter> abzugFromServer = Optional.empty();
		if (familiengroesse < 3) {
			// Unter 3 Personen gibt es keinen Abzug!
			abzugFromServer = Optional.empty();
		} else if (familiengroesse < 4) {
			if (abzugFamiliengroesse3 != null) {
				abzugProPerson = abzugFamiliengroesse3;
			} else {
				abzugFromServer = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3, stichtag);
			}
		} else if (familiengroesse < 5) {
			if (abzugFamiliengroesse4 != null) {
				abzugProPerson = abzugFamiliengroesse4;
			} else {
				abzugFromServer = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4, stichtag);
			}
		} else if (familiengroesse < 6) {
			if (abzugFamiliengroesse5 != null) {
				abzugProPerson = abzugFamiliengroesse5;
			} else {
				abzugFromServer = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5, stichtag);
			}
		} else if (familiengroesse >= 6) {
			if (abzugFamiliengroesse6 != null) {
				abzugProPerson = abzugFamiliengroesse6;
			} else {
				abzugFromServer = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6, stichtag);
			}
		}
		if (abzugFromServer.isPresent()) {
			abzugProPerson = abzugFromServer.get().getValueAsBigDecimal();
		}
		// Ein Bigdecimal darf nicht aus einem double erzeugt werden, da das Ergebnis nicht genau die gegebene Nummer waere
		// deswegen muss man hier familiengroesse als String uebergeben. Sonst bekommen wir PMD rule AvoidDecimalLiteralsInBigDecimalConstructor
		return new BigDecimal(String.valueOf(familiengroesse)).multiply(abzugProPerson);
	}

	@Nonnull
	public FinanzielleSituationResultateDTO calculateResultateFinanzielleSituation(@Nonnull Gesuch gesuch) {

		double familiengroesse = gesuch.getGesuchsperiode() == null ? 0 : calculateFamiliengroesse(gesuch, gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear());
		BigDecimal abzugAufgrundFamiliengroesse = gesuch.getGesuchsperiode() == null ? BigDecimal.ZERO :
			calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);

		final FinanzielleSituationResultateDTO finSitResultDTO = new FinanzielleSituationResultateDTO(familiengroesse, abzugAufgrundFamiliengroesse);
		setFinanzielleSituationParameters(gesuch, finSitResultDTO);

		return finSitResultDTO;
	}

	@Nonnull
	public FinanzielleSituationResultateDTO calculateResultateEinkommensverschlechterung(@Nonnull Gesuch gesuch, int basisJahrPlus) {
		Validate.notNull(gesuch.getEinkommensverschlechterungInfo());
		//Bei der Berechnung der Einkommensverschlechterung werden die aktuellen Familienverhaeltnisse beruecksichtigt
		// (nicht Stand 31.12. des Vorjahres)!
		double familiengroesse = gesuch.getGesuchsperiode() == null ? 0 : calculateFamiliengroesse(gesuch, null);
		BigDecimal abzugAufgrundFamiliengroesse = gesuch.getGesuchsperiode() == null ? BigDecimal.ZERO :
			calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);

		final FinanzielleSituationResultateDTO einkVerResultDTO = new FinanzielleSituationResultateDTO(familiengroesse, abzugAufgrundFamiliengroesse);
		setEinkommensverschlechterungParameters(gesuch, basisJahrPlus, einkVerResultDTO);

		return einkVerResultDTO;
	}

	/**
	 * Nimmt das uebergebene FinanzielleSituationResultateDTO und mit den Daten vom Gesuch, berechnet alle im
	 * FinanzielleSituationResultateDTO benoetigten Daten.
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
		Einkommensverschlechterung einkommensverschlechterungGS1 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), basisJahrPlus);
		final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS(gesuch.getGesuchsteller1());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller1(
			calcGeschaeftsgewinnDurchschnitt(einkommensverschlechterungGS1, finanzielleSituationGS1));

		Einkommensverschlechterung einkommensverschlechterungGS2 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), basisJahrPlus);
		final FinanzielleSituation finanzielleSituationGS2 = getFinanzielleSituationGS(gesuch.getGesuchsteller2());
		einkVerResultDTO.setGeschaeftsgewinnDurchschnittGesuchsteller2(
			calcGeschaeftsgewinnDurchschnitt(einkommensverschlechterungGS2, finanzielleSituationGS2));

		calculateZusammen(einkVerResultDTO, einkommensverschlechterungGS1,
			calculateNettoJahresLohn(einkommensverschlechterungGS1),
			einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller1(),
			einkommensverschlechterungGS2, calculateNettoJahresLohn(einkommensverschlechterungGS2),
			einkVerResultDTO.getGeschaeftsgewinnDurchschnittGesuchsteller2());

	}


	public void calculateFinanzDaten(@Nonnull Gesuch gesuch) {
		FinanzDatenDTO finanzDatenDTO = new FinanzDatenDTO();

		// Finanzielle Situation berechnen
		FinanzielleSituationResultateDTO finanzielleSituationResultateDTO = calculateResultateFinanzielleSituation(gesuch);
		finanzDatenDTO.setMassgebendesEinkommenBasisjahr(finanzielleSituationResultateDTO.getMassgebendesEinkommen());
		finanzDatenDTO.setDatumVonBasisjahr(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());

		if (gesuch.getEinkommensverschlechterungInfo() != null && gesuch.getEinkommensverschlechterungInfo().getEinkommensverschlechterung()) {
			EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.getEinkommensverschlechterungInfo();
			FinanzielleSituationResultateDTO resultateEKV1 = calculateResultateEinkommensverschlechterung(gesuch, 1);
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
				FinanzielleSituationResultateDTO resultateEKV2 = calculateResultateEinkommensverschlechterung(gesuch, 2);
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

	private boolean acceptEKV(BigDecimal massgebendesEinkommenVorjahr, BigDecimal massgebendesEinkommenJahr) {
		BigDecimal minimumEKV = MathUtil.EINE_NACHKOMMASTELLE.from(0.8);
		// EKV gewährt
		return massgebendesEinkommenVorjahr.compareTo(BigDecimal.ZERO) > 0
			&& MathUtil.EXACT.divide(massgebendesEinkommenJahr, massgebendesEinkommenVorjahr).compareTo(minimumEKV) <= 0;
	}

	private void calculateZusammen(final FinanzielleSituationResultateDTO finSitResultDTO, AbstractFinanzielleSituation finanzielleSituationGS1,
								   BigDecimal nettoJahresLohn1, BigDecimal geschaeftsgewinnDurchschnitt1, AbstractFinanzielleSituation finanzielleSituationGS2,
								   BigDecimal nettoJahresLohn2, BigDecimal geschaeftsgewinnDurchschnitt2) {

		finSitResultDTO.setEinkommenBeiderGesuchsteller(calcEinkommen(finanzielleSituationGS1, nettoJahresLohn1,
			geschaeftsgewinnDurchschnitt1, finanzielleSituationGS2, nettoJahresLohn2, geschaeftsgewinnDurchschnitt2));
		finSitResultDTO.setNettovermoegenFuenfProzent(calcVermoegen5Prozent(finanzielleSituationGS1, finanzielleSituationGS2));
		finSitResultDTO.setAbzuegeBeiderGesuchsteller(calcAbzuege(finanzielleSituationGS1, finanzielleSituationGS2));

		finSitResultDTO.setAnrechenbaresEinkommen(add(finSitResultDTO.getEinkommenBeiderGesuchsteller(), finSitResultDTO.getNettovermoegenFuenfProzent()));
		finSitResultDTO.setTotalAbzuege(add(finSitResultDTO.getAbzuegeBeiderGesuchsteller(), finSitResultDTO.getAbzugAufgrundFamiliengroesse()));
		finSitResultDTO.setMassgebendesEinkommen(
			MathUtil.positiveNonNullAndRound(
				subtract(finSitResultDTO.getAnrechenbaresEinkommen(), finSitResultDTO.getTotalAbzuege())));
	}

	/**
	 * Diese Methode aufrufen um den GeschaeftsgewinnDurchschnitt fuer die Finanzielle Situation zu berechnen.
	 *
	 * @param finanzielleSituation
	 * @return
	 */
	private BigDecimal calcGeschaeftsgewinnDurchschnitt(FinanzielleSituation finanzielleSituation) {
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
	private BigDecimal calcGeschaeftsgewinnDurchschnitt(Einkommensverschlechterung einkVers, FinanzielleSituation finanzielleSituation) {
		if (finanzielleSituation != null && einkVers != null) {
			return calcGeschaeftsgewinnDurchschnitt(einkVers.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahr(),
				finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
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
	private BigDecimal calcGeschaeftsgewinnDurchschnitt(@Nullable final BigDecimal geschaeftsgewinnBasisjahr,
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


	private BigDecimal calcVermoegen5Prozent(AbstractFinanzielleSituation abstractFinanzielleSituation1, AbstractFinanzielleSituation abstractFinanzielleSituation2) {
		final BigDecimal totalBruttovermoegen = add(abstractFinanzielleSituation1 != null ? abstractFinanzielleSituation1.getBruttovermoegen() : BigDecimal.ZERO,
			abstractFinanzielleSituation2 != null ? abstractFinanzielleSituation2.getBruttovermoegen() : BigDecimal.ZERO);

		final BigDecimal totalSchulden = add(abstractFinanzielleSituation1 != null ? abstractFinanzielleSituation1.getSchulden() : BigDecimal.ZERO,
			abstractFinanzielleSituation2 != null ? abstractFinanzielleSituation2.getSchulden() : BigDecimal.ZERO);

		BigDecimal total = subtract(totalBruttovermoegen, totalSchulden);
		if (total.compareTo(BigDecimal.ZERO) < 0) {
			total = BigDecimal.ZERO;
		}
		total = percent(total, 5);
		return total;
	}

	protected BigDecimal add(BigDecimal value1, BigDecimal value2) {
		value1 = value1 != null ? value1 : BigDecimal.ZERO;
		value2 = value2 != null ? value2 : BigDecimal.ZERO;
		return value1.add(value2);
	}

	private BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
		value1 = value1 != null ? value1 : BigDecimal.ZERO;
		value2 = value2 != null ? value2 : BigDecimal.ZERO;
		return value1.subtract(value2);
	}

	private BigDecimal percent(BigDecimal value, int percent) {
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

	private Einkommensverschlechterung getEinkommensverschlechterungGS(Gesuchsteller gesuchsteller, int basisJahrPlus) {
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

	private FinanzielleSituation getFinanzielleSituationGS(Gesuchsteller gesuchsteller) {
		if (gesuchsteller != null && gesuchsteller.getFinanzielleSituationContainer() != null) {
			return gesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationJA();
		}
		return null;
	}

}
