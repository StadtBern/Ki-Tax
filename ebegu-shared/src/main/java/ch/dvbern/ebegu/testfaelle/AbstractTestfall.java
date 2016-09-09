package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.types.DateRange;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Superklasse für Testfaelle des JA
 */
public abstract class AbstractTestfall {

	public static final String idInstitutionAaregg = "11111111-1111-1111-1111-111111111101";
	public static final String idInstitutionBruennen = "11111111-1111-1111-1111-111111111107";

	protected Gesuchsperiode gesuchsperiode;
	protected Collection<InstitutionStammdaten> institutionStammdatenList;

	public AbstractTestfall(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		this.gesuchsperiode = gesuchsperiode;
		this.institutionStammdatenList = institutionStammdatenList;
	}

	public abstract Gesuch createGesuch();

	protected Gesuch createAlleinerziehend(LocalDate eingangsdatum) {
		// Fall
		Fall fall = new Fall();
		// Gesuch
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(gesuchsperiode);
		gesuch.setFall(fall);
		gesuch.setEingangsdatum(eingangsdatum);
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
		// Familiensituation
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.setFamiliensituation(familiensituation);
		return gesuch;
	}

	protected Gesuch createVerheiratet(LocalDate eingangsdatum) {
		// Fall
		Fall fall = new Fall();
		// Gesuch
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(gesuchsperiode);
		gesuch.setFall(fall);
		gesuch.setEingangsdatum(eingangsdatum);
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
		// Familiensituation
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		gesuch.setFamiliensituation(familiensituation);
		return gesuch;
	}

	protected Gesuchsteller createGesuchsteller(String name, String vorname) {
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setGeschlecht(Geschlecht.WEIBLICH);
		gesuchsteller.setNachname(name);
		gesuchsteller.setVorname(vorname);
		gesuchsteller.setGeburtsdatum(LocalDate.of(1980, Month.MARCH, 25));
		gesuchsteller.setAdressen(new ArrayList<>());
		gesuchsteller.getAdressen().add(createWohnadresse(gesuchsteller));
		gesuchsteller.setDiplomatenstatus(false);
		gesuchsteller.setMail("test@email.com");
		gesuchsteller.setMobile("079 000 00 00");
		return gesuchsteller;
	}

	protected GesuchstellerAdresse createWohnadresse(Gesuchsteller gesuchsteller) {
		GesuchstellerAdresse wohnadresse = new GesuchstellerAdresse();
		wohnadresse.setStrasse("Testweg");
		wohnadresse.setHausnummer("10");
		wohnadresse.setPlz("3000");
		wohnadresse.setOrt("Bern");
		wohnadresse.setLand(Land.CH);
		wohnadresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		wohnadresse.setGesuchsteller(gesuchsteller);
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
		kind.setFamilienErgaenzendeBetreuung(betreuung);
		if (betreuung) {
			kind.setMutterspracheDeutsch(Boolean.TRUE);
		}
		KindContainer kindContainer = new KindContainer();
		kindContainer.setKindJA(kind);
		return kindContainer;
	}

	protected Betreuung createBetreuung(BetreuungsangebotTyp betreuungsangebotTyp, String institutionsId) {
		Betreuung betreuung = new Betreuung();
		betreuung.setInstitutionStammdaten(createInstitutionStammdaten(betreuungsangebotTyp, institutionsId));
		betreuung.setBetreuungsstatus(Betreuungsstatus.AUSSTEHEND);
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
		finanzielleSituationContainer.setJahr(gesuchsperiode.getGueltigkeit().getGueltigAb().getYear()-1);
		finanzielleSituationContainer.setFinanzielleSituationJA(finanzielleSituation);
		return finanzielleSituationContainer;
	}


	/**
	 * Diese Methode erstellt alle WizardSteps fuer das uebergebene Gesuch. Alle Steps bekommen den Status OK by default (nur Dokumente
	 * hat IN_BEARBEITUNG und Verfuegen WARTEN). Sollte man andere Status haben wollen, muss man diese Methode ueberschreiben.
	 * Die WizardSteps werden erstellt aber nicht persisted
	 * @param gesuch
	 * @return
	 */
	public List<WizardStep> createWizardSteps(final Gesuch gesuch) {
		List<WizardStep> wizardSteps = new ArrayList<>();
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.GESUCH_ERSTELLEN, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.GESUCHSTELLER, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.KINDER, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.BETREUUNG, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.ERWERBSPENSUM, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.OK, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.DOKUMENTE, WizardStepStatus.IN_BEARBEITUNG, "", true));
		wizardSteps.add(createWizardStepObject(gesuch, WizardStepName.VERFUEGEN, WizardStepStatus.WARTEN, "", true));
		return wizardSteps;
	}

	private WizardStep createWizardStepObject(Gesuch gesuch, WizardStepName wizardStepName, WizardStepStatus stepStatus, String bemerkungen,
											  boolean verfuegbar) {
		final WizardStep wizardStep = new WizardStep();
		wizardStep.setGesuch(gesuch);
		wizardStep.setVerfuegbar(verfuegbar);
		wizardStep.setWizardStepName(wizardStepName);
		wizardStep.setWizardStepStatus(stepStatus);
		wizardStep.setBemerkungen(bemerkungen);
		return wizardStep;
	}
}
