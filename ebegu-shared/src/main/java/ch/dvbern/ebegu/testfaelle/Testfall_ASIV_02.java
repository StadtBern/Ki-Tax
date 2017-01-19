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
 * Wechsel von 2 auf 1. Keine EKV
 */
public class Testfall_ASIV_02 extends AbstractASIVTestfall {

	public Testfall_ASIV_02(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
							boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall_ASIV_02(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	@Override
	public Gesuch fillInGesuch() {
		return createErstgesuch();
	}

	public Gesuch createErstgesuch() {
		// Gesuch, Gesuchsteller
		Gesuch erstgesuch = createVerheiratet();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer();
		erstgesuch.setGesuchsteller1(gesuchsteller1);
		GesuchstellerContainer gesuchsteller2 = createGesuchstellerContainer();
		erstgesuch.setGesuchsteller2(gesuchsteller2);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(100, 0);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		ErwerbspensumContainer erwerbspensumGS2 = createErwerbspensum(100, 0);
		gesuchsteller2.addErwerbspensumContainer(erwerbspensumGS2);
		// Kinder
		KindContainer kind = createKind(Geschlecht.MAENNLICH, "ASIV", "Kind", LocalDate.of(2014, Month.APRIL, 13), Kinderabzug.GANZER_ABZUG, true);
		kind.setGesuch(erstgesuch);
		erstgesuch.getKindContainers().add(kind);
		// Kita Brünnen
		Betreuung betreuungKitaBruennen = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_BRUENNEN, true);
		betreuungKitaBruennen.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaBruennen);
		BetreuungspensumContainer betreuungspensumKitaBruennen = createBetreuungspensum(100, LocalDate.of(gesuchsperiode.getBasisJahrPlus1(), Month.AUGUST, 1), LocalDate.of(gesuchsperiode.getBasisJahrPlus2(), Month.JULY, 31));
		betreuungspensumKitaBruennen.setBetreuung(betreuungKitaBruennen);
		betreuungKitaBruennen.getBetreuungspensumContainers().add(betreuungspensumKitaBruennen);
		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationContainer = createFinanzielleSituationContainer();
		finanzielleSituationContainer.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(70000));
		finanzielleSituationContainer.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationContainer);

		FinanzielleSituationContainer finanzielleSituationGS2 = createFinanzielleSituationContainer();
		finanzielleSituationGS2.getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(30000));
		finanzielleSituationGS2.setGesuchsteller(gesuchsteller2);
		gesuchsteller2.setFinanzielleSituationContainer(finanzielleSituationGS2);
		return erstgesuch;
	}

	public Gesuch createMutation(Gesuch erstgesuch) {
		Gesuch mutation = createAlleinerziehend(erstgesuch, LocalDate.of(gesuchsperiode.getBasisJahrPlus1(), Month.OCTOBER, 15));
		return mutation;
	}

	@Override
	public String getNachname() {
		return "ASIV_2";
	}

	@Override
	public String getVorname() {
		return "Testfall 2";
	}
}
