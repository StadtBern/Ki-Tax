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
 * Kind mit 2 Kitas. Anspruch 60%
 * Kita A: ab 01.08.2016 - 31.12.2016 40%, ab 01.01.2017 30%
 * Kita B: ab 01.08.2016 - 31.12.2016 40%, ab 01.01.2017 50%
 *
 * -> Regel 3
 * -> Kita A wird zuerst bedient, weil sie bei gleichem Beginn und gleichem Pensum zuerst eingegeben wurde, auch ab 1.1., wo Kita B höher wäre!
 */
public class Testfall_DoppelKita_Regel3 extends AbstractTestfall {

	public Testfall_DoppelKita_Regel3(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
									  boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall_DoppelKita_Regel3(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	public Gesuch fillInGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer();
		gesuch.setGesuchsteller1(gesuchsteller1);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(60, 0);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		// Kinder
		KindContainer kind = createKind(Geschlecht.MAENNLICH, "Doppelkita", "Dodi", LocalDate.of(2014, Month.APRIL, 13), Kinderabzug.GANZER_ABZUG, true);
		kind.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind);
		// Betreuungen
		// Kita Weissenstein
		Betreuung betreuungKitaAaregg = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_WEISSENSTEIN, betreuungenBestaetigt);
		betreuungKitaAaregg.setBetreuungNummer(1);
		betreuungKitaAaregg.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaAaregg);

		BetreuungspensumContainer betreuungspensumKitaAaregg = createBetreuungspensum(40, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2016, Month.DECEMBER, 31));
		betreuungspensumKitaAaregg.setBetreuung(betreuungKitaAaregg);
		betreuungKitaAaregg.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg);

		BetreuungspensumContainer betreuungspensumKitaAaregg2 = createBetreuungspensum(30, LocalDate.of(2017, Month.JANUARY, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumKitaAaregg2.setBetreuung(betreuungKitaAaregg);
		betreuungKitaAaregg.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg2);

		// Kita Brünnen
		Betreuung betreuungKitaBruennen = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_BRUENNEN, betreuungenBestaetigt);
		betreuungKitaBruennen.setBetreuungNummer(2);
		betreuungKitaBruennen.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaBruennen);

		BetreuungspensumContainer betreuungspensumKitaBruennen = createBetreuungspensum(40, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2016, Month.DECEMBER, 31));
		betreuungspensumKitaBruennen.setBetreuung(betreuungKitaBruennen);
		betreuungKitaBruennen.getBetreuungspensumContainers().add(betreuungspensumKitaBruennen);

		BetreuungspensumContainer betreuungspensumKitaBruennen2 = createBetreuungspensum(50, LocalDate.of(2017, Month.JANUARY, 1), LocalDate.of(2017, Month.JULY, 31));
		betreuungspensumKitaBruennen2.setBetreuung(betreuungKitaBruennen);
		betreuungKitaBruennen.getBetreuungspensumContainers().add(betreuungspensumKitaBruennen2);

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
		return "Doppelkita";
	}

	@Override
	public String getVorname() {
		return "Doris";
	}
}
