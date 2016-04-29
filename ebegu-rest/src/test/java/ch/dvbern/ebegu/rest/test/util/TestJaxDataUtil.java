package ch.dvbern.ebegu.rest.test.util;

import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxPerson;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.enums.Land;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.time.LocalDate;

/**
 * Generiert Testdaten fuer JAX DTOs
 */
public class TestJaxDataUtil {
	public static JaxPerson createTestJaxPerson(){

		JaxPerson jaxPerson = new JaxPerson();
		 jaxPerson.setNachname("Jaxter");
		jaxPerson.setVorname("Jack");
		jaxPerson.setWohnAdresse(createTestJaxAdr(null));
		jaxPerson.setGeburtsdatum(LocalDate.now().minusYears(18));
		jaxPerson.setMail("jax.jaxter@example.com");
		jaxPerson.setGeschlecht(Geschlecht.MAENNLICH);
		jaxPerson.setMobile("+41 78 987 65 54");
		jaxPerson.setTelefonAusland("+49 12 123 42 12");
		jaxPerson.setZpvNumber("1234");

		return jaxPerson;

	}

	public static JaxPerson createTestJaxPersonWithUmzug(){
		JaxPerson jaxPerson = createTestJaxPerson();
		JaxAdresse umzugAdr = new JaxAdresse();
		umzugAdr.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		umzugAdr.setGemeinde("neue gemeinde");
		umzugAdr.setHausnummer("99");
		umzugAdr.setLand(Land.CH);
		umzugAdr.setOrt("Umzugsort");
		umzugAdr.setPlz("999");
		umzugAdr.setZusatzzeile("Testzusatz");
		umzugAdr.setStrasse("neue Strasse");
		umzugAdr.setGueltigAb(LocalDate.now().plusMonths(1));  //gueltig 1 monat in zukunft

		jaxPerson.setUmzugAdresse(umzugAdr);
		JaxAdresse altAdr = createTestJaxAdr("alternativ");
		altAdr.setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		jaxPerson.setAlternativeAdresse(altAdr);
		return jaxPerson;

	}

	public static JaxAdresse createTestJaxAdr(@Nullable String postfix){
		postfix = StringUtils.isEmpty(postfix) ? "" : postfix;
		JaxAdresse jaxAdresse = new JaxAdresse();
		jaxAdresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		jaxAdresse.setGemeinde("Bern"+postfix);
		jaxAdresse.setHausnummer("1"+postfix);
		jaxAdresse.setLand(Land.CH);
		jaxAdresse.setOrt("Bern"+postfix);
		jaxAdresse.setPlz("3014"+postfix);
		jaxAdresse.setZusatzzeile("Test"+postfix);
		jaxAdresse.setStrasse("Nussbaumstrasse"+postfix);
		return jaxAdresse;

	}


}
