package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

/**
 * Dieser TestFall stellt einen Umzug aus Bern vor dem Start der Gesuchsperiode dar
 * - Wohnadresse in Bern
 * - Umzugsadresse ab 15.06.2016 aus Bern <- vor Gesuchsperiode
 *
 */
public class Testfall10_UmzugVorGesuchsperiode extends AbstractTestfall {

	public Testfall10_UmzugVorGesuchsperiode(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
										boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall10_UmzugVorGesuchsperiode(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	@Override
	public Gesuch fillInGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer();
		gesuch.setGesuchsteller1(gesuchsteller1);

		//Wohnadresse in Bern
		gesuchsteller1.getAdressen().get(0).getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		final int gesuchsperiodeFirstYear = gesuchsperiode.getGueltigkeit().getGueltigAb().getYear();
		gesuchsteller1.getAdressen().get(0).getGesuchstellerAdresseJA()
			.setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(gesuchsperiodeFirstYear, 6, 14)));

		// Umzugsadresse am 15.06.2016 aus Bern <- vor der Gesuchsperiode
		GesuchstellerAdresseContainer umzugInBern = createWohnadresseContainer(gesuchsteller1);
		umzugInBern.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		umzugInBern.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(gesuchsperiodeFirstYear, 6, 15),
			Constants.END_OF_TIME));
		gesuchsteller1.getAdressen().add(umzugInBern);

		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(60, 20);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		// Kinder
		KindContainer kind = createKind(Geschlecht.WEIBLICH, getNachname(), "Estrella", LocalDate.of(2014, Month.APRIL, 13), Kinderabzug.GANZER_ABZUG, true);
		kind.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind);
		// Betreuungen
		// Kita Weissenstein
		Betreuung betreuungKitaAaregg = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_WEISSENSTEIN, betreuungenBestaetigt);
		betreuungKitaAaregg.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaAaregg);
		BetreuungspensumContainer betreuungspensumKitaAaregg = createBetreuungspensum(80, LocalDate.of(gesuchsperiode.getBasisJahrPlus1(), Month.AUGUST, 1), LocalDate.of(gesuchsperiode.getBasisJahrPlus2(), Month.JULY, 31));
		betreuungspensumKitaAaregg.setBetreuung(betreuungKitaAaregg);
		betreuungKitaAaregg.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg);
		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationContainer = createFinanzielleSituationContainer();
		finanzielleSituationContainer.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(53265));
		finanzielleSituationContainer.getFinanzielleSituationJA().setBruttovermoegen(MathUtil.DEFAULT.from(12147));
		finanzielleSituationContainer.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationContainer);
		return gesuch;
	}

	@Override
	public String getNachname() {
		return "Vorzügelmann";
	}

	@Override
	public String getVorname() {
		return "Ermenegildo";
	}
}
