package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.enums.Geschlecht;

import java.time.LocalDate;

/**
 * User: homa
 * Date: 21.03.16
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
		adresse.setPerson(createDefaultPerson());
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
		return person;

	}
}
