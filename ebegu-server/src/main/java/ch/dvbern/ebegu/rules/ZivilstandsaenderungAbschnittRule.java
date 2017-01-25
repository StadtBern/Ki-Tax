package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.VerfuegungsBemerkung;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Umsetzung der ASIV Revision: Finanzielle Situation bei Mutation der Familiensituation anpassen
 * <p>
 * Gem. neuer ASIV Verordnung muss bei einem Wechsel von einem auf zwei Gesuchsteller oder umgekehrt die
 * finanzielle Situation ab dem Folgemonat angepasst werden.
 * </p>
 */
public class ZivilstandsaenderungAbschnittRule extends AbstractAbschnittRule {

	public ZivilstandsaenderungAbschnittRule(DateRange validityPeriod) {
		super(RuleKey.ZIVILSTANDSAENDERUNG, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Override
	@Nonnull
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {

		Gesuch gesuch = betreuung.extractGesuch();
		final List<VerfuegungZeitabschnitt> zivilstandsaenderungAbschnitte = new ArrayList<>();

		// Ueberpruefen, ob die Gesuchsteller-Kardinalität geändert hat. Nur dann muss evt. anders berechnet werden!
		if (gesuch.extractFamiliensituation() != null && gesuch.extractFamiliensituation().getAenderungPer() != null &&
			gesuch.extractFamiliensituation().hasSecondGesuchsteller() != gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller()) {

			// Die Zivilstandsaenderung gilt ab anfang nächstem Monat, die Bemerkung muss aber "per Heirat/Trennung" erfolgen
			final LocalDate ereignistag = gesuch.extractFamiliensituation().getAenderungPer();
			final LocalDate stichtag = ereignistag.plusMonths(1).withDayOfMonth(1);
			// Bemerkung erstellen
			VerfuegungsBemerkung bemerkungContainer;
			if (gesuch.extractFamiliensituation().hasSecondGesuchsteller()) {
				// Heirat
				bemerkungContainer = new VerfuegungsBemerkung(RuleKey.ZIVILSTANDSAENDERUNG, MsgKey.FAMILIENSITUATION_HEIRAT_MSG);
			} else {
				// Trennung
				bemerkungContainer = new VerfuegungsBemerkung(RuleKey.ZIVILSTANDSAENDERUNG, MsgKey.FAMILIENSITUATION_TRENNUNG_MSG);
			}

			VerfuegungZeitabschnitt abschnittVorMutation = new VerfuegungZeitabschnitt(new DateRange(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), ereignistag.minusDays(1)));
			abschnittVorMutation.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller());
			zivilstandsaenderungAbschnitte.add(abschnittVorMutation);

			VerfuegungZeitabschnitt abschnittVorStichtag = new VerfuegungZeitabschnitt(new DateRange(ereignistag, stichtag.minusDays(1)));
			abschnittVorStichtag.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller());
			abschnittVorStichtag.addBemerkung(bemerkungContainer);
			zivilstandsaenderungAbschnitte.add(abschnittVorStichtag);

			VerfuegungZeitabschnitt abschnittNachMutation = new VerfuegungZeitabschnitt(new DateRange(stichtag, gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis()));
			abschnittNachMutation.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituation().hasSecondGesuchsteller());
			abschnittNachMutation.addBemerkung(bemerkungContainer);
			zivilstandsaenderungAbschnitte.add(abschnittNachMutation);
		} else {
			VerfuegungZeitabschnitt abschnittOhneMutation = new VerfuegungZeitabschnitt(gesuch.getGesuchsperiode().getGueltigkeit());
			abschnittOhneMutation.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituation().hasSecondGesuchsteller());
			zivilstandsaenderungAbschnitte.add(abschnittOhneMutation);
		}
		return zivilstandsaenderungAbschnitte;
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}
}
