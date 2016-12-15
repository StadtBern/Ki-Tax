package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Dieser TestFall ist eine Kopie von Waelti Dagmar (im Status vom 10.11.2016) aber mit einer Abwesenheit fuer das Kind Julio
 * und die KITA Aaregg. Die Abwesenheit laeuft von 11.10.2016 bis 25.11.2016. Deshalb muss ab dem 03.10.2016 Volltarif eintretten
 */
public class Testfall09_Abwesenheit extends AbstractTestfall {

	public Testfall09_Abwesenheit(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
										boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall09_Abwesenheit(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	@Override
	public Gesuch fillInGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer();
		gesuch.setGesuchsteller1(gesuchsteller1);

		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(60, 20);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		// Kinder
		KindContainer kind = createKind(Geschlecht.MAENNLICH, getNachname(), "Julio", LocalDate.of(2014, Month.APRIL, 13), Kinderabzug.GANZER_ABZUG, true);
		kind.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind);
		// Betreuungen
		// Kita Aaregg
		Betreuung betreuungKitaAaregg = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_AAREGG, betreuungenBestaetigt);
		betreuungKitaAaregg.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaAaregg);
		BetreuungspensumContainer betreuungspensumKitaAaregg = createBetreuungspensum(80, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JANUARY, 31));
		betreuungspensumKitaAaregg.setBetreuung(betreuungKitaAaregg);
		betreuungKitaAaregg.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg);
		// Abwesenheit
		Set<AbwesenheitContainer> abwesenheitContainerSet = new HashSet<>();
		AbwesenheitContainer abwesenheitContainer = new AbwesenheitContainer();
		abwesenheitContainer.setBetreuung(betreuungKitaAaregg);
		Abwesenheit abwesenheitJA = new Abwesenheit();
		abwesenheitJA.setGueltigkeit(new DateRange(LocalDate.of(2016, Month.OCTOBER, 11), LocalDate.of(2016, Month.NOVEMBER, 25)));
		abwesenheitContainer.setAbwesenheitJA(abwesenheitJA);
		abwesenheitContainerSet.add(abwesenheitContainer);
		betreuungKitaAaregg.setAbwesenheitContainers(abwesenheitContainerSet);

		// Kita Brünnen
		Betreuung betreuungKitaBruennen = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_BRUENNEN, betreuungenBestaetigt);
		betreuungKitaBruennen.setBetreuungNummer(2);
		betreuungKitaBruennen.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaBruennen);
		BetreuungspensumContainer betreuungspensumKitaBruennen = createBetreuungspensum(40, LocalDate.of(2017, Month.FEBRUARY, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumKitaBruennen.setBetreuung(betreuungKitaBruennen);
		betreuungKitaBruennen.getBetreuungspensumContainers().add(betreuungspensumKitaBruennen);
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
		return "Abwesend";
	}

	@Override
	public String getVorname() {
		return "Cindy";
	}

}
