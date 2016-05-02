package ch.dvbern.ebegu.tets;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * comments homa
 */
public final class TestDataUtil {

	private TestDataUtil(){
	}

	public  static Adresse createDefaultAdresse() {
		Adresse adresse = new Adresse();
		adresse.setStrasse("Nussbaumstrasse");
		adresse.setHausnummer("21");
		adresse.setZusatzzeile("c/o Uwe Untermieter");
		adresse.setPlz("3014");
		adresse.setOrt("Bern");
		adresse.setGueltigkeit(new DateRange(LocalDate.now(), Constants.END_OF_TIME));
		return adresse;
	}

	public static Person createDefaultPerson(){
		Person person = new Person();
		person.setGeburtsdatum(LocalDate.of(1984,12,12));
		person.setVorname("Tim");
		person.setNachname("Tester");
		person.setGeschlecht(Geschlecht.MAENNLICH);
		person.setMail("tim.tester@example.com");
		person.setMobile("076 309 30 58");
		person.setTelefon("031 378 24 24");
		person.setZpvNumber("0761234567897");
		person.addAdresse(createDefaultAdresse());
		return person;
	}

	public static Familiensituation createDefaultFamiliensituation(){
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		familiensituation.setBemerkungen("DVBern");
		familiensituation.setGesuch(createDefaultGesuch());
		return familiensituation;
	}

	public static Gesuch createDefaultGesuch() {
		Gesuch gesuch = new Gesuch();
		gesuch.setFall(createDefaultFall());
		return gesuch;
	}

	public static Fall createDefaultFall() {
		return new Fall();
	}

	public static Mandant createDefaultMandant() {
		Mandant mandant = new Mandant();
		mandant.setName("Mandant1");
		return mandant;
	}

	public static Fachstelle createDefaultFachstelle() {
		Fachstelle fachstelle = new Fachstelle();
		fachstelle.setName("Fachstelle1");
		fachstelle.setBeschreibung("Kinder Fachstelle");
		fachstelle.setBehinderungsbestaetigung(true);
		return fachstelle;
	}

	public static Traegerschaft createDefaultTraegerschaft() {
		Traegerschaft traegerschaft = new Traegerschaft();
		traegerschaft.setName("Traegerschaft1");
		return traegerschaft;
	}

	public static Institution createDefaultInstitution() {
		Institution institution = new Institution();
		institution.setName("Institution1");
		institution.setMandant(createDefaultMandant());
		institution.setTraegerschaft(createDefaultTraegerschaft());
		return institution;
	}

	public static InstitutionStammdaten createDefaultInstitutionStammdaten() {
		InstitutionStammdaten institutionStammdaten = new InstitutionStammdaten();
		institutionStammdaten.setIban("CH123456789");
		institutionStammdaten.setOeffnungsstunden(BigDecimal.valueOf(24));
		institutionStammdaten.setOeffnungstage(BigDecimal.valueOf(365));
		institutionStammdaten.setGueltigkeit(new DateRange(LocalDate.of(2010,1,1), LocalDate.of(2010,12,31)));
		institutionStammdaten.setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		institutionStammdaten.setInstitution(createDefaultInstitution());
		return institutionStammdaten;
	}
}
