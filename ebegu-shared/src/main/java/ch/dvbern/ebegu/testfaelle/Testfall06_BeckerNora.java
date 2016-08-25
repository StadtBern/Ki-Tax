package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.enums.Kinderabzug;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

/**
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/2
 * https://ebegu.dvbern.ch/ebegu/api/v1/testfaelle/testfall/2
 */
public class Testfall06_BeckerNora extends AbstractTestfall {

	private static final String FAMILIENNAME = "Becker";

	public Testfall06_BeckerNora(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList);
	}

	public Gesuch createGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend(LocalDate.of(2016, Month.AUGUST, 25));
		Gesuchsteller gesuchsteller1 = createGesuchsteller(FAMILIENNAME, "Nora");
		gesuch.setGesuchsteller1(gesuchsteller1);
//		Gesuchsteller gesuchsteller2 = createGesuchsteller(FAMILIENNAME, "Tizian");
//		gesuch.setGesuchsteller2(gesuchsteller2);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensumGS1 = createErwerbspensum(60, 0);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensumGS1);
//		ErwerbspensumContainer erwerbspensumGS2 = createErwerbspensum(100, 0);
//		gesuchsteller2.addErwerbspensumContainer(erwerbspensumGS2);
		// Kinder
		KindContainer kind1 = createKind(Geschlecht.MAENNLICH, FAMILIENNAME, "Timon", LocalDate.of(2006, Month.DECEMBER, 25), Kinderabzug.HALBER_ABZUG, true);
		kind1.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind1);
		KindContainer kind2 = createKind(Geschlecht.WEIBLICH, FAMILIENNAME, "Yasmin", LocalDate.of(2011, Month.MARCH, 29), Kinderabzug.HALBER_ABZUG, true);
		kind2.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind2);

		// Betreuungen
		// Kind 1: Tagi Aaregg
		Betreuung betreuungTagiAaregg = createBetreuung(BetreuungsangebotTyp.TAGI, "9253e9b1-9cae-4278-b578-f1ce93306d29");
		betreuungTagiAaregg.setKind(kind1);
		kind1.getBetreuungen().add(betreuungTagiAaregg);
		BetreuungspensumContainer betreuungspensumTagiAaregg= createBetreuungspensum(100, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumTagiAaregg.setBetreuung(betreuungTagiAaregg);
		betreuungTagiAaregg.getBetreuungspensumContainers().add(betreuungspensumTagiAaregg);
		// Kind 2: Kita Aaregg
		Betreuung betreuungKitaAaregg = createBetreuung(BetreuungsangebotTyp.KITA, idInstitutionAaregg);
		betreuungKitaAaregg.setKind(kind2);
		kind2.getBetreuungen().add(betreuungKitaAaregg);
		BetreuungspensumContainer betreuungspensumKitaAaregg = createBetreuungspensum(100, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumKitaAaregg.setBetreuung(betreuungKitaAaregg);
		betreuungKitaAaregg.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg);

		// Finanzielle Situation
//		FinanzielleSituationContainer finanzielleSituationGS1 = createFinanzielleSituationContainer();
//		finanzielleSituationGS1.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(34022));
//		finanzielleSituationGS1.setGesuchsteller(gesuchsteller1);
//		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationGS1);
//
//		FinanzielleSituationContainer finanzielleSituationGS2 = createFinanzielleSituationContainer();
//		finanzielleSituationGS2.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(98521));
//		finanzielleSituationGS2.setGesuchsteller(gesuchsteller2);
//		gesuchsteller2.setFinanzielleSituationContainer(finanzielleSituationGS2);
		return gesuch;
	}
}
