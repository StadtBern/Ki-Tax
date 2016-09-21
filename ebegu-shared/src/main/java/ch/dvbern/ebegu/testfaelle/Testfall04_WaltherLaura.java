package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.util.MathUtil;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

/**
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/4
 * https://ebegu.dvbern.ch/ebegu/api/v1/testfaelle/testfall/4
 */
public class Testfall04_WaltherLaura extends AbstractTestfall {

	private static final String FAMILIENNAME = "Walther";

	public Testfall04_WaltherLaura(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList);
	}

	public Gesuch createGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createVerheiratet(LocalDate.of(2016, Month.FEBRUARY, 15)); // Wir wissen das Eingangsdatum nicht!
		Gesuchsteller gesuchsteller1 = createGesuchsteller(FAMILIENNAME, "Laura");
		gesuch.setGesuchsteller1(gesuchsteller1);
		Gesuchsteller gesuchsteller2 = createGesuchsteller(FAMILIENNAME, "Thomas");
		gesuch.setGesuchsteller2(gesuchsteller2);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensumGS1 = createErwerbspensum(90, 0);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensumGS1);
		ErwerbspensumContainer erwerbspensumGS2 = createErwerbspensum(60, 0);
		gesuchsteller2.addErwerbspensumContainer(erwerbspensumGS2);
		// Kinder
		KindContainer kind1 = createKind(Geschlecht.WEIBLICH, FAMILIENNAME, "Lorenz", LocalDate.of(2013, Month.FEBRUARY, 17), Kinderabzug.GANZER_ABZUG, true);
		kind1.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind1);

		// Betreuungen
		// Kind 1: Kita Aaregg
		Betreuung betreuungTagiAaregg = createBetreuung(BetreuungsangebotTyp.KITA, idInstitutionAaregg);
		betreuungTagiAaregg.setKind(kind1);
		kind1.getBetreuungen().add(betreuungTagiAaregg);
		BetreuungspensumContainer betreuungspensumTagiAaregg = createBetreuungspensum(50, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumTagiAaregg.setBetreuung(betreuungTagiAaregg);
		betreuungTagiAaregg.getBetreuungspensumContainers().add(betreuungspensumTagiAaregg);

		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationGS1 = createFinanzielleSituationContainer();
		finanzielleSituationGS1.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(99014.00));
		finanzielleSituationGS1.getFinanzielleSituationJA().setBruttovermoegen(MathUtil.DEFAULT.from(908746));
		finanzielleSituationGS1.getFinanzielleSituationJA().setSchulden(MathUtil.DEFAULT.from(451248));
		finanzielleSituationGS1.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationGS1);

		FinanzielleSituationContainer finanzielleSituationGS2 = createFinanzielleSituationContainer();
		finanzielleSituationGS2.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(2000));
		finanzielleSituationGS2.getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahr(MathUtil.DEFAULT.from(48023));
		finanzielleSituationGS2.getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus1(MathUtil.DEFAULT.from(54871));
		finanzielleSituationGS2.getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus2(MathUtil.DEFAULT.from(46017));
		finanzielleSituationGS2.setGesuchsteller(gesuchsteller2);
		gesuchsteller2.setFinanzielleSituationContainer(finanzielleSituationGS2);
		return gesuch;
	}

	@Override
	public String getNachname() {
		return FAMILIENNAME;
	}

	@Override
	public String getVorname() {
		return "Laura";
	}
}
