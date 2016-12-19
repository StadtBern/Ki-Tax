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
 * Dieser TestFall ist eine Kopie von Waelti Dagmar (im Status vom 10.11.2016) aber mit
 * - Wohnadresse NICHT in Bern
 * - Umzugsadresse ab 15.12.2016 in Bern
 * - Umzugsadresse ab 01.01.2017 NICHT in Bern
 * <p>
 * PS: Die Daten von Waelti Dagmar werden direkt kopiert anstatt die Methoden aufzurufen. Dieses dupliziert Code aber
 * macht einfacher, diesen Fall einzeln betrachten und verwalten zu koennen, anstatt vom Test von Waelti Dagmar abzuhaengen
 */
public class Testfall08_UmzugAusInAusBern extends AbstractTestfall {

	public Testfall08_UmzugAusInAusBern(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
										boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall08_UmzugAusInAusBern(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	@Override
	public Gesuch fillInGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer();
		gesuch.setGesuchsteller1(gesuchsteller1);

		//Wohnadresse NICHT in Bern
		gesuchsteller1.getAdressen().get(0).getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		final int gesuchsperiodeFirstYear = gesuchsperiode.getGueltigkeit().getGueltigAb().getYear();
		gesuchsteller1.getAdressen().get(0).getGesuchstellerAdresseJA()
			.setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(gesuchsperiodeFirstYear, 12, 14)));

		// Umzugsadresse am 15.12.2016 in Bern
		GesuchstellerAdresseContainer umzugInBern = createWohnadresseContainer(gesuchsteller1);
		umzugInBern.getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		umzugInBern.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(gesuchsperiodeFirstYear, 12, 15),
			LocalDate.of(gesuchsperiodeFirstYear, 12, 31)));
		gesuchsteller1.getAdressen().add(umzugInBern);

		// Umzugsadresse am 01.01.2017 NICHT in Bern
		GesuchstellerAdresseContainer umzugAusBern = createWohnadresseContainer(gesuchsteller1);
		umzugAusBern.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		umzugAusBern.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(gesuchsperiodeFirstYear + 1, 1, 1), Constants.END_OF_TIME));
		gesuchsteller1.getAdressen().add(umzugAusBern);

		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(60, 20);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		// Kinder
		KindContainer kind = createKind(Geschlecht.MAENNLICH, getNachname(), "Paco", LocalDate.of(2014, Month.APRIL, 13), Kinderabzug.GANZER_ABZUG, true);
		kind.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind);
		// Betreuungen
		// Kita Weissenstein
		Betreuung betreuungKitaAaregg = createBetreuung(BetreuungsangebotTyp.KITA, ID_INSTITUTION_WEISSENSTEIN, betreuungenBestaetigt);
		betreuungKitaAaregg.setKind(kind);
		kind.getBetreuungen().add(betreuungKitaAaregg);
		BetreuungspensumContainer betreuungspensumKitaAaregg = createBetreuungspensum(80, LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JANUARY, 31));
		betreuungspensumKitaAaregg.setBetreuung(betreuungKitaAaregg);
		betreuungKitaAaregg.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg);
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
		return "Zügelmann";
	}

	@Override
	public String getVorname() {
		return "Manolo";
	}
}
