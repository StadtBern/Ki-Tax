package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Superklasse für Testfaelle des JA
 * <p>
 * Um alles mit den Services durchfuehren zu koennen, muss man zuerst den Fall erstellen, dann
 * das Gesuch erstellen und dann das Gesuch ausfuellen und updaten. Nur so werden alle WizardSteps
 * erstellt und es gibt kein Problem mit den Verknuepfungen zwischen Entities
 * Der richtige Prozess findet man in TestfaelleService#createAndSaveGesuch()
 */
public abstract class AbstractTestfall {

	public static final String ID_INSTITUTION_AAREGG = "11111111-1111-1111-1111-111111111101";
	public static final String ID_INSTITUTION_BRUENNEN = "11111111-1111-1111-1111-111111111107";
	public static final String ID_INSTITUTION_AAREGG_TAGI = "11111111-1111-1111-1111-111111111174";


	protected Gesuchsperiode gesuchsperiode;
	protected Collection<InstitutionStammdaten> institutionStammdatenList;

	protected Fall fall = null;
	protected Gesuch gesuch = null;
	protected boolean betreuungenBestaetigt;

	public AbstractTestfall(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList,
							boolean betreuungenBestaetigt) {
		this.gesuchsperiode = gesuchsperiode;
		this.institutionStammdatenList = institutionStammdatenList;
		this.betreuungenBestaetigt = betreuungenBestaetigt;
	}

	public abstract Gesuch fillInGesuch();

	public abstract String getNachname();

	public abstract String getVorname();

	public Fall createFall(Benutzer verantwortlicher) {
		fall = new Fall();
		fall.setVerantwortlicher(verantwortlicher);
		fall.setTimestampErstellt(LocalDateTime.now().minusDays(7));
		return fall;
	}

	public Fall createFall() {
		return new Fall();
	}

	public void createGesuch(LocalDate eingangsdatum) {
		// Fall
		if (fall == null) {
			fall = createFall(null);
		}
		// Gesuch
		gesuch = new Gesuch();
		gesuch.setGesuchsperiode(gesuchsperiode);
		gesuch.setFall(fall);
		gesuch.setEingangsdatum(eingangsdatum);
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
	}

	protected Gesuch createAlleinerziehend() {
		// Familiensituation
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		FamiliensituationContainer familiensituationContainer = new FamiliensituationContainer();
		familiensituationContainer.setFamiliensituationJA(familiensituation);
		gesuch.setFamiliensituationContainer(familiensituationContainer);
		return gesuch;
	}

	protected Gesuch createVerheiratet() {
		// Familiensituation
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		familiensituation.setGemeinsameSteuererklaerung(Boolean.TRUE);
		FamiliensituationContainer familiensituationContainer = new FamiliensituationContainer();
		familiensituationContainer.setFamiliensituationJA(familiensituation);
		gesuch.setFamiliensituationContainer(familiensituationContainer);
		return gesuch;
	}

	protected GesuchstellerContainer createGesuchstellerContainer() {
		return createGesuchstellerContainer(getNachname(), getVorname());
	}

	protected GesuchstellerContainer createGesuchstellerContainer(String name, String vorname) {
		GesuchstellerContainer gesuchstellerCont = new GesuchstellerContainer();
		gesuchstellerCont.setAdressen(new ArrayList<>());
		gesuchstellerCont.setGesuchstellerJA(createGesuchsteller(name, vorname));
		gesuchstellerCont.getAdressen().add(createWohnadresseContainer(gesuchstellerCont));
		return gesuchstellerCont;
	}

	protected Gesuchsteller createGesuchsteller(String name, String vorname) {
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setGeschlecht(Geschlecht.WEIBLICH);
		gesuchsteller.setNachname(name);
		gesuchsteller.setVorname(vorname);
		gesuchsteller.setGeburtsdatum(LocalDate.of(1980, Month.MARCH, 25));
		gesuchsteller.setDiplomatenstatus(false);
		gesuchsteller.setMail("test@email.com");
		gesuchsteller.setMobile("079 000 00 00");
		return gesuchsteller;
	}

	protected Gesuchsteller createGesuchsteller() {
		return createGesuchsteller(getNachname(), getVorname());
	}

	protected GesuchstellerAdresseContainer createWohnadresseContainer(GesuchstellerContainer gesuchstellerCont) {
		GesuchstellerAdresseContainer wohnadresseCont = new GesuchstellerAdresseContainer();
		wohnadresseCont.setGesuchstellerContainer(gesuchstellerCont);
		wohnadresseCont.setGesuchstellerAdresseJA(createWohnadresse());
		return wohnadresseCont;
	}

	protected GesuchstellerAdresse createWohnadresse() {
		GesuchstellerAdresse wohnadresse = new GesuchstellerAdresse();
		wohnadresse.setStrasse("Testweg");
		wohnadresse.setHausnummer("10");
		wohnadresse.setPlz("3000");
		wohnadresse.setOrt("Bern");
		wohnadresse.setLand(Land.CH);
		wohnadresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		wohnadresse.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		return wohnadresse;
	}

	protected ErwerbspensumContainer createErwerbspensum(int prozent, int zuschlagsprozent) {
		ErwerbspensumContainer erwerbspensumContainer = new ErwerbspensumContainer();
		Erwerbspensum erwerbspensum = new Erwerbspensum();
		erwerbspensum.setGueltigkeit(gesuchsperiode.getGueltigkeit());
		erwerbspensum.setTaetigkeit(Taetigkeit.ANGESTELLT);
		erwerbspensum.setPensum(prozent);
		if (zuschlagsprozent > 0) {
			erwerbspensum.setZuschlagZuErwerbspensum(true);
			erwerbspensum.setZuschlagsprozent(zuschlagsprozent);
			erwerbspensum.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		}
		erwerbspensumContainer.setErwerbspensumJA(erwerbspensum);
		return erwerbspensumContainer;
	}

	protected KindContainer createKind(Geschlecht geschlecht, String name, String vorname, LocalDate geburtsdatum, Kinderabzug kinderabzug, boolean betreuung) {
		Kind kind = new Kind();
		kind.setGeschlecht(geschlecht);
		kind.setNachname(name);
		kind.setVorname(vorname);
		kind.setGeburtsdatum(geburtsdatum);
		kind.setKinderabzug(kinderabzug);
		kind.setWohnhaftImGleichenHaushalt(100);
		kind.setFamilienErgaenzendeBetreuung(betreuung);
		if (betreuung) {
			kind.setMutterspracheDeutsch(Boolean.TRUE);
			kind.setEinschulung(Boolean.TRUE);
		}
		KindContainer kindContainer = new KindContainer();
		kindContainer.setKindJA(kind);
		return kindContainer;
	}

	protected Betreuung createBetreuung(BetreuungsangebotTyp betreuungsangebotTyp, String institutionsId) {
		return createBetreuung(betreuungsangebotTyp, institutionsId, false);
	}

	protected Betreuung createBetreuung(BetreuungsangebotTyp betreuungsangebotTyp, String institutionsId, boolean bestaetigt) {
		Betreuung betreuung = new Betreuung();
		betreuung.setInstitutionStammdaten(createInstitutionStammdaten(betreuungsangebotTyp, institutionsId));
		if (!bestaetigt) {
			betreuung.setBetreuungsstatus(Betreuungsstatus.WARTEN);
		} else {
			betreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
			betreuung.setDatumBestaetigung(LocalDate.now());
		}
		betreuung.setVertrag(Boolean.TRUE);
		return betreuung;
	}

	protected InstitutionStammdaten createInstitutionStammdaten(BetreuungsangebotTyp betreuungsangebotTyp, String institutionsId) {
		for (InstitutionStammdaten institutionStammdaten : institutionStammdatenList) {
			if (institutionStammdaten.getBetreuungsangebotTyp().equals(betreuungsangebotTyp) && institutionStammdaten.getInstitution().getId().equals(institutionsId)) {
				return institutionStammdaten;
			}
		}
		return null;
	}

	protected BetreuungspensumContainer createBetreuungspensum(int pensum) {
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		Betreuungspensum betreuungspensum = new Betreuungspensum();
		betreuungspensumContainer.setBetreuungspensumJA(betreuungspensum);
		betreuungspensum.setGueltigkeit(gesuchsperiode.getGueltigkeit());
		betreuungspensum.setPensum(pensum);
		return betreuungspensumContainer;
	}

	protected BetreuungspensumContainer createBetreuungspensum(int pensum, LocalDate datumVon, LocalDate datumBis) {
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		Betreuungspensum betreuungspensum = new Betreuungspensum();
		betreuungspensumContainer.setBetreuungspensumJA(betreuungspensum);
		betreuungspensum.setGueltigkeit(new DateRange(datumVon, datumBis));
		betreuungspensum.setPensum(pensum);
		return betreuungspensumContainer;
	}

	protected FinanzielleSituationContainer createFinanzielleSituationContainer() {
		FinanzielleSituationContainer finanzielleSituationContainer = new FinanzielleSituationContainer();
		FinanzielleSituation finanzielleSituation = new FinanzielleSituation();
		finanzielleSituation.setSteuerveranlagungErhalten(true);
		finanzielleSituation.setSteuererklaerungAusgefuellt(true);
		finanzielleSituationContainer.setJahr(gesuchsperiode.getGueltigkeit().getGueltigAb().getYear() - 1);
		finanzielleSituationContainer.setFinanzielleSituationJA(finanzielleSituation);
		return finanzielleSituationContainer;
	}

	public Fall getFall() {
		return fall;
	}

	public void setFall(Fall fall) {
		this.fall = fall;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}
}
