package ch.dvbern.ebegu.rest.test.util;

import ch.dvbern.ebegu.api.dtos.*;
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

	public static JaxGesuchsteller createTestJaxGesuchsteller(){

		JaxGesuchsteller jaxGesuchsteller = new JaxGesuchsteller();
		jaxGesuchsteller.setNachname("Jaxter");
		jaxGesuchsteller.setVorname("Jack");
		jaxGesuchsteller.setWohnAdresse(createTestJaxAdr(null));
		jaxGesuchsteller.setGeburtsdatum(LocalDate.now().minusYears(18));
		jaxGesuchsteller.setMail("jax.jaxter@example.com");
		jaxGesuchsteller.setGeschlecht(Geschlecht.MAENNLICH);
		jaxGesuchsteller.setMobile("+41 78 987 65 54");
		jaxGesuchsteller.setTelefonAusland("+49 12 123 42 12");
		jaxGesuchsteller.setZpvNumber("1234");

		return jaxGesuchsteller;

	}

	public static JaxGesuchsteller createTestJaxGesuchstellerWithUmzug(){
		JaxGesuchsteller jaxGesuchsteller = createTestJaxGesuchsteller();
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

		jaxGesuchsteller.setUmzugAdresse(umzugAdr);
		JaxAdresse altAdr = createTestJaxAdr("alternativ");
		altAdr.setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		jaxGesuchsteller.setAlternativeAdresse(altAdr);
		return jaxGesuchsteller;

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

	public static JaxFall createTestJaxFall() {
		return new JaxFall();
	}

	public static JaxGesuch createTestJaxGesuch() {
		JaxGesuch jaxGesuch = new JaxGesuch();
		jaxGesuch.setFall(createTestJaxFall());
		jaxGesuch.setGesuchsteller1(createTestJaxGesuchsteller());
		JaxGesuchsteller testJaxGesuchsteller = createTestJaxGesuchsteller();
		testJaxGesuchsteller.setNachname("Gesuchsteller2");
		jaxGesuch.setGesuchsteller2(testJaxGesuchsteller);
		return jaxGesuch;
	}

	public static JaxFachstelle createTestJaxFachstelle() {
		JaxFachstelle jaxFachstelle = new JaxFachstelle();
		jaxFachstelle.setName("Fachstelle_Test");
		jaxFachstelle.setBehinderungsbestaetigung(false);
		jaxFachstelle.setBeschreibung("Notizen der Fachstelle");
		return jaxFachstelle;
	}

	public static JaxKind createTestJaxKind() {
		JaxKind jaxKind = new JaxKind();
		jaxKind.setNachname("Kind_Mustermann");
		jaxKind.setVorname("Kind_Max");
		jaxKind.setGeburtsdatum(LocalDate.now().minusYears(18));
		jaxKind.setGeschlecht(Geschlecht.WEIBLICH);
		jaxKind.setBetreuungspensumFachstelle(50);
		jaxKind.setBemerkungen("Notizen");
		jaxKind.setMutterspracheDeutsch(false);
		jaxKind.setFamilienErgaenzendeBetreuung(true);
		jaxKind.setUnterstuetzungspflicht(true);
		jaxKind.setWohnhaftImGleichenHaushalt(75);
		jaxKind.setFachstelle(createTestJaxFachstelle());
		return jaxKind;
	}

	public static JaxKindContainer createTestJaxKindContainer() {
		JaxKindContainer jaxKindContainer = new JaxKindContainer();
		jaxKindContainer.setKindGS(createTestJaxKind());
		jaxKindContainer.setKindJA(createTestJaxKind());
		return jaxKindContainer;
	}
}
