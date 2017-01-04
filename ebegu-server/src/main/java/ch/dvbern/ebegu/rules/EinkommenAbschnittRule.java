package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Setzt das massgebende Einkommen in die benoetigten Zeitabschnitte
 */
public class EinkommenAbschnittRule extends AbstractAbschnittRule {


	public EinkommenAbschnittRule(DateRange validityPeriod) {
		super(RuleKey.EINKOMMEN, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> einkommensAbschnitte = new ArrayList<>();
		// Nur ausführen wenn Finanzdaten gesetzt
		// Der {@link FinanzielleSituationRechner} wurde verwendet um das jeweils geltende  Einkommen auszurechnen. Das heisst im DTO ist schon
		// jeweils das zu verwendende Einkommen gesetzt
		FinanzDatenDTO finanzDatenDTO_alleine = betreuung.extractGesuch().getFinanzDatenDTO_alleine();
		FinanzDatenDTO finanzDatenDTO_zuZweit = betreuung.extractGesuch().getFinanzDatenDTO_zuZweit();

		if (finanzDatenDTO_alleine != null && finanzDatenDTO_zuZweit != null) {
			VerfuegungZeitabschnitt lastAbschnitt;

			int lastYear_alleine = betreuung.extractGesuchsperiode().getBasisJahr();
			int lastYear_zuZweit = lastYear_alleine;

			// Abschnitt Finanzielle Situation (Massgebendes Einkommen fuer die Gesuchsperiode)
			VerfuegungZeitabschnitt abschnittFinanzielleSituation = new VerfuegungZeitabschnitt(betreuung.extractGesuchsperiode().getGueltigkeit());
			abschnittFinanzielleSituation.setMassgebendesEinkommenVorAbzugFamgr_alleine(finanzDatenDTO_alleine.getMassgebendesEinkBjVorAbzFamGr());
			abschnittFinanzielleSituation.setMassgebendesEinkommenVorAbzugFamgr_zuZweit(finanzDatenDTO_zuZweit.getMassgebendesEinkBjVorAbzFamGr());
			abschnittFinanzielleSituation.setEinkommensjahr_alleine(lastYear_alleine);
			abschnittFinanzielleSituation.setEinkommensjahr_zuZweit(lastYear_zuZweit);
			einkommensAbschnitte.add(abschnittFinanzielleSituation);
			lastAbschnitt = abschnittFinanzielleSituation;


			// Einkommensverschlechterung 1: In mind. 1 Kombination akzeptiert
			if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1() != null || finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1() != null) {
				DateRange rangeEKV1 = new DateRange(finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1(), betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis()); //TODO das datum, das nicht null ist
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung1 = new VerfuegungZeitabschnitt(rangeEKV1);

				if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1() != null) {
					// EKV1 fuer alleine akzeptiert
					abschnittEinkommensverschlechterung1.setMassgebendesEinkommenVorAbzugFamgr_alleine(finanzDatenDTO_alleine.getMassgebendesEinkBjP1VorAbzFamGr());
					abschnittEinkommensverschlechterung1.setEinkommensjahr_alleine(finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1().getYear());
					lastYear_alleine = finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1().getYear();
				} else {
					// EKV1 fuer alleine nicht akzeptiert
					abschnittEinkommensverschlechterung1.setMassgebendesEinkommenVorAbzugFamgr_alleine(finanzDatenDTO_alleine.getMassgebendesEinkBjVorAbzFamGr());
					abschnittEinkommensverschlechterung1.setEinkommensjahr_alleine(lastYear_alleine);
				}
				if (finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1() != null) {
					// EKV1 fuer zu Zweit akzeptiert
					abschnittEinkommensverschlechterung1.setMassgebendesEinkommenVorAbzugFamgr_zuZweit(finanzDatenDTO_zuZweit.getMassgebendesEinkBjP1VorAbzFamGr());
					abschnittEinkommensverschlechterung1.setEinkommensjahr_zuZweit(finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1().getYear());
					lastYear_zuZweit = finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1().getYear();
				} else {
					// EKV1 fuer zu Zweit nicht akzeptiert
					abschnittEinkommensverschlechterung1.setMassgebendesEinkommenVorAbzugFamgr_zuZweit(finanzDatenDTO_zuZweit.getMassgebendesEinkBjVorAbzFamGr());
					abschnittEinkommensverschlechterung1.setEinkommensjahr_zuZweit(lastYear_zuZweit);
				}
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung1);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung1.getGueltigkeit());
				lastAbschnitt = abschnittEinkommensverschlechterung1;
			}
			createBemerkungEVK1(lastAbschnitt,finanzDatenDTO_alleine, betreuung);
			createBemerkungEVK1(lastAbschnitt,finanzDatenDTO_zuZweit, betreuung);


			// Einkommensverschlechterung 2: In mind. 1 Kombination akzeptiert
			if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2() != null || finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus2() != null) {
				DateRange rangeEKV2 = new DateRange(finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2(), betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung2 = new VerfuegungZeitabschnitt(rangeEKV2);

				if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2() != null) {
					// EKV2 fuer alleine akzeptiert
					abschnittEinkommensverschlechterung2.setMassgebendesEinkommenVorAbzugFamgr_alleine(finanzDatenDTO_alleine.getMassgebendesEinkBjP2VorAbzFamGr());
					abschnittEinkommensverschlechterung2.setEinkommensjahr_alleine(finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2().getYear());
				} else {
					// EKV2 fuer alleine nicht akzeptiert
					abschnittEinkommensverschlechterung2.setMassgebendesEinkommenVorAbzugFamgr_alleine(finanzDatenDTO_alleine.getMassgebendesEinkBjVorAbzFamGr());
					abschnittEinkommensverschlechterung2.setEinkommensjahr_alleine(lastYear_alleine);
				}
				if (finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus2() != null) {
					// EKV2 fuer zu Zweit akzeptiert
					abschnittEinkommensverschlechterung2.setMassgebendesEinkommenVorAbzugFamgr_zuZweit(finanzDatenDTO_zuZweit.getMassgebendesEinkBjP2VorAbzFamGr());
					abschnittEinkommensverschlechterung2.setEinkommensjahr_zuZweit(finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus2().getYear());
				} else {
					// EKV2 fuer zu Zweit nicht akzeptiert
					abschnittEinkommensverschlechterung2.setMassgebendesEinkommenVorAbzugFamgr_zuZweit(finanzDatenDTO_zuZweit.getMassgebendesEinkBjVorAbzFamGr());
					abschnittEinkommensverschlechterung2.setEinkommensjahr_zuZweit(lastYear_zuZweit);
				}
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung2);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung2.getGueltigkeit());
			}
			createBemerkungEVK2(lastAbschnitt,finanzDatenDTO_alleine, betreuung);
			createBemerkungEVK2(lastAbschnitt,finanzDatenDTO_zuZweit, betreuung);
		}
		return einkommensAbschnitte;
	}


	private void createBemerkungEVK1(VerfuegungZeitabschnitt lastAbschnitt, FinanzDatenDTO finanzDatenDTO, Betreuung betreuung) {
		if (finanzDatenDTO.isEKV1Accepted()) {
			lastAbschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG1_ACCEPT_MSG);
		} else {
			//ekv wurde nicht akzeptiert
			if (betreuung.extractGesuch().extractEinkommensverschlechterungInfo() != null
				&& betreuung.extractGesuch().extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1() != null
				&& betreuung.extractGesuch().extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1()) {
				lastAbschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG1_NOT_ACCEPT_MSG);
			}
		}
	}

	private void createBemerkungEVK2(VerfuegungZeitabschnitt lastAbschnitt, FinanzDatenDTO finanzDatenDTO, Betreuung betreuung) {
		if (finanzDatenDTO.isEKV2Accepted()) {
			lastAbschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG2_ACCEPT_MSG);
		} else {
			//ekv2 wurde nicht akzeptiert
			if (betreuung.extractGesuch().extractEinkommensverschlechterungInfo() != null
				&&betreuung.extractGesuch().extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2() != null
				&&  betreuung.extractGesuch().extractEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2()) {
				lastAbschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG2_NOT_ACCEPT_MSG);
			}
		}

	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}

}
