package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
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
		FinanzDatenDTO finanzDatenDTO = betreuung.extractGesuch().getFinanzDatenDTO();
		if (finanzDatenDTO != null) {
			VerfuegungZeitabschnitt lastAbschnitt;

			// Abschnitt Finanzielle Situation (Massgebendes Einkommen fuer die Gesuchsperiode)
			VerfuegungZeitabschnitt abschnittFinanzielleSituation = new VerfuegungZeitabschnitt(betreuung.extractGesuchsperiode().getGueltigkeit());
			abschnittFinanzielleSituation.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
			einkommensAbschnitte.add(abschnittFinanzielleSituation);
			lastAbschnitt = abschnittFinanzielleSituation;

			// Einkommensverschlechterung 1
			if (finanzDatenDTO.getDatumVonBasisjahrPlus1() != null) {
				DateRange rangeEKV1 = new DateRange(finanzDatenDTO.getDatumVonBasisjahrPlus1(), betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung1 = new VerfuegungZeitabschnitt(rangeEKV1);
				abschnittEinkommensverschlechterung1.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung1);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung1.getGueltigkeit());
				lastAbschnitt = abschnittEinkommensverschlechterung1;
			}
			// Einkommensverschlechterung 2
			if (finanzDatenDTO.getDatumVonBasisjahrPlus2() != null) {
				DateRange rangeEKV2 = new DateRange(finanzDatenDTO.getDatumVonBasisjahrPlus2(), betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung2 = new VerfuegungZeitabschnitt(rangeEKV2);
				abschnittEinkommensverschlechterung2.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP2VorAbzFamGr());
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung2);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung2.getGueltigkeit());
			}
		}
		return einkommensAbschnitte;
	}
}
