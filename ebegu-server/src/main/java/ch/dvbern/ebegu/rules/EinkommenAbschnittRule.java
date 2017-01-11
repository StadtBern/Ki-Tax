package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
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

			// Abschnitt Finanzielle Situation (Massgebendes Einkommen fuer die Gesuchsperiode)
			VerfuegungZeitabschnitt abschnittFinanzielleSituation = new VerfuegungZeitabschnitt(betreuung.extractGesuchsperiode().getGueltigkeit());
			einkommensAbschnitte.add(abschnittFinanzielleSituation);
			lastAbschnitt = abschnittFinanzielleSituation;
			boolean hasEKV1 = false;

			// Einkommensverschlechterung 1: In mind. 1 Kombination eingegeben
			if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1() != null || finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1() != null) {
				LocalDate startEKV1 = finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1() != null ? finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1() : finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1();
				DateRange rangeEKV1 = new DateRange(startEKV1, betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung1 = new VerfuegungZeitabschnitt(rangeEKV1);

				if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus1() != null) {
					// EKV1 fuer alleine erfasst
					abschnittEinkommensverschlechterung1.setEkv1Alleine(true);
				}
				if (finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus1() != null) {
					// EKV1 fuer zu Zweit erfasst
					abschnittEinkommensverschlechterung1.setEkv1ZuZweit(true);
				}
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung1);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung1.getGueltigkeit());
				lastAbschnitt = abschnittEinkommensverschlechterung1;
				hasEKV1 = true;
			}

			// Einkommensverschlechterung 2: In mind. 1 Kombination akzeptiert
			if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2() != null || finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus2() != null) {
				LocalDate startEKV2 = finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2() != null ? finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2() : finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus2();
				DateRange rangeEKV2 = new DateRange(startEKV2, betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung2 = new VerfuegungZeitabschnitt(rangeEKV2);
				abschnittEinkommensverschlechterung2.setEkv1NotExisting(!hasEKV1);

				if (finanzDatenDTO_alleine.getDatumVonBasisjahrPlus2() != null) {
					// EKV2 fuer alleine erfasst
					abschnittEinkommensverschlechterung2.setEkv2Alleine(true);
				}
				if (finanzDatenDTO_zuZweit.getDatumVonBasisjahrPlus2() != null) {
					// EKV2 fuer zu Zweit erfasst
					abschnittEinkommensverschlechterung2.setEkv2ZuZweit(true);
				}
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung2);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung2.getGueltigkeit());
			}
		}
		return einkommensAbschnitte;
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}

}
