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
 * Wechsel von 1 auf 2. Mit vorheriger EKV, nach Heirat nicht mehr stattgegeben
 */
public class Testfall_ASIV_06 extends AbstractASIVTestfall {

	public Testfall_ASIV_06(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
							boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall_ASIV_06(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	@Override
	public Gesuch fillInGesuch() {
		return createErstgesuch();
	}

	public Gesuch createErstgesuch() {
		// Gesuch, Gesuchsteller
		Gesuch erstgesuch = createAlleinerziehend();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer();
		erstgesuch.setGesuchsteller1(gesuchsteller1);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(100, 0);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		// Kinder
		KindContainer kind = createKind(Geschlecht.MAENNLICH, "ASIV", "Kind", LocalDate.of(2014, Month.APRIL, 13), Kinderabzug.GANZER_ABZUG, true);
		kind.setGesuch(erstgesuch);
		erstgesuch.getKindContainers().add(kind);
		// Kita Brünnen
		Betreuung betreuungKitaBruennen = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_BRUENNEN, true);
		betreuungKitaBruennen.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaBruennen);
		BetreuungspensumContainer betreuungspensumKitaBruennen = createBetreuungspensum(100, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumKitaBruennen.setBetreuung(betreuungKitaBruennen);
		betreuungKitaBruennen.getBetreuungspensumContainers().add(betreuungspensumKitaBruennen);
		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationContainer = createFinanzielleSituationContainer();
		finanzielleSituationContainer.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(70000));
		finanzielleSituationContainer.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationContainer);
		// Einkommensverschlechterug
		EinkommensverschlechterungContainer ekvContainer = createEinkommensverschlechterungContainer(erstgesuch, LocalDate.of(2016, Month.OCTOBER, 1), null);
		ekvContainer.getEkvJABasisJahrPlus1().setNettolohnJan(MathUtil.DEFAULT.from(49000));
		gesuchsteller1.setEinkommensverschlechterungContainer(ekvContainer);
		return erstgesuch;
	}

	public Gesuch createMutation(Gesuch erstgesuch) {
		// Gesuch, Gesuchsteller
		Gesuch mutation = createVerheiratet(erstgesuch, LocalDate.of(2017, Month.JANUARY, 15));
		GesuchstellerContainer gesuchsteller2 = createGesuchstellerContainer();
		mutation.setGesuchsteller2(gesuchsteller2);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(100, 0);
		gesuchsteller2.addErwerbspensumContainer(erwerbspensum);
		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationContainerGS2 = createFinanzielleSituationContainer();
		finanzielleSituationContainerGS2.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(50000));
		finanzielleSituationContainerGS2.setGesuchsteller(gesuchsteller2);
		gesuchsteller2.setFinanzielleSituationContainer(finanzielleSituationContainerGS2);
        // Einkommensverschlechterug
		EinkommensverschlechterungContainer ekvContainerGS2 = createEinkommensverschlechterungContainer(true, false);
		ekvContainerGS2.getEkvJABasisJahrPlus1().setNettolohnJan(MathUtil.DEFAULT.from(50000));
		gesuchsteller2.setEinkommensverschlechterungContainer(ekvContainerGS2);
		return mutation;
	}

	@Override
	public String getNachname() {
		return "ASIV_6";
	}

	@Override
	public String getVorname() {
		return "Testfall 6";
	}
}
