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
		FinanzDatenDTO finanzDatenDTO = betreuung.extractGesuch().getFinanzDatenDTO();
		if (finanzDatenDTO != null) {
			VerfuegungZeitabschnitt lastAbschnitt;

			// Abschnitt Finanzielle Situation (Massgebendes Einkommen fuer die Gesuchsperiode)
			VerfuegungZeitabschnitt abschnittFinanzielleSituation = new VerfuegungZeitabschnitt(betreuung.extractGesuchsperiode().getGueltigkeit());
			abschnittFinanzielleSituation.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
			abschnittFinanzielleSituation.setEinkommensjahr(betreuung.extractGesuchsperiode().getBasisJahr());
			einkommensAbschnitte.add(abschnittFinanzielleSituation);
			lastAbschnitt = abschnittFinanzielleSituation;

			// Einkommensverschlechterung 1
			if (finanzDatenDTO.getDatumVonBasisjahrPlus1() != null) {
				DateRange rangeEKV1 = new DateRange(finanzDatenDTO.getDatumVonBasisjahrPlus1(), betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung1 = new VerfuegungZeitabschnitt(rangeEKV1);
				abschnittEinkommensverschlechterung1.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
				abschnittEinkommensverschlechterung1.setEinkommensjahr(finanzDatenDTO.getDatumVonBasisjahrPlus1().getYear());
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung1);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung1.getGueltigkeit());
				lastAbschnitt = abschnittEinkommensverschlechterung1;
			}
			createBemerkungEVK1(lastAbschnitt,finanzDatenDTO, betreuung);
			// Einkommensverschlechterung 2
			if (finanzDatenDTO.getDatumVonBasisjahrPlus2() != null) {
				DateRange rangeEKV2 = new DateRange(finanzDatenDTO.getDatumVonBasisjahrPlus2(), betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung2 = new VerfuegungZeitabschnitt(rangeEKV2);
				abschnittEinkommensverschlechterung2.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP2VorAbzFamGr());
				abschnittEinkommensverschlechterung2.setEinkommensjahr(finanzDatenDTO.getDatumVonBasisjahrPlus2().getYear());
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung2);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung2.getGueltigkeit());
			}
			createBemerkungEVK2(lastAbschnitt,finanzDatenDTO, betreuung);
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
