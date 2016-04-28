package ch.dvbern.ebegu.tets;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.enums.Geschlecht;

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
		adresse.setGueltigAb(LocalDate.now());
		adresse.setGueltigAb(LocalDate.now().plusMonths(1));
		LocalDate now = LocalDate.now();
		adresse.setGueltigAb(now);
		adresse.setGueltigBis(now);
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

	public static Fachstelle createDefaultFachstelle() {
		Fachstelle fachstelle = new Fachstelle();
		fachstelle.setName("Fachstelle1");
		fachstelle.setBeschreibung("Kinder Fachstelle");
		fachstelle.setBehinderungsbestaetigung(true);
		return fachstelle;
	}
}
