package ch.dvbern.ebegu.rest.test.util;

import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.enums.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

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

	public static JaxGesuchsteller createTestJaxGesuchstellerWithErwerbsbensum() {
		JaxGesuchsteller testJaxGesuchsteller = createTestJaxGesuchsteller();
		JaxErwerbspensumContainer container = createTestJaxErwerbspensumContainer();
		JaxErwerbspensumContainer container2 = createTestJaxErwerbspensumContainer();
		container2.getErwerbspensumGS().setGueltigAb(LocalDate.now().plusYears(1));
		container2.getErwerbspensumGS().setGueltigBis(null);

		Collection<JaxErwerbspensumContainer> list = new ArrayList<>();
		list.add(container);
		list.add(container2);
		testJaxGesuchsteller.setErwerbspensenContainers(list);
		return testJaxGesuchsteller;

	}

	public static JaxErwerbspensumContainer createTestJaxErwerbspensumContainer(){
		JaxErwerbspensum testJaxErwerbspensum = createTestJaxErwerbspensum();
		JaxErwerbspensumContainer container = new JaxErwerbspensumContainer();
		container.setErwerbspensumGS(testJaxErwerbspensum);
		return container;
	}


	public static JaxErwerbspensum createTestJaxErwerbspensum(){
		JaxErwerbspensum jaxErwerbspensum = new JaxErwerbspensum();
		jaxErwerbspensum.setTaetigkeit(Taetigkeit.ANGESTELLT);
		jaxErwerbspensum.setGesundheitlicheEinschraenkungen(true);
		jaxErwerbspensum.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		jaxErwerbspensum.setZuschlagZuErwerbspensum(true);
		jaxErwerbspensum.setZuschlagsprozent(15);
		jaxErwerbspensum.setGueltigAb(LocalDate.now().minusYears(1));
		jaxErwerbspensum.setPensum(70);
		return jaxErwerbspensum;

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
		jaxGesuch.setGesuchsperiode(createTestJaxGesuchsperiode());
		jaxGesuch.setGesuchsteller1(createTestJaxGesuchsteller());
		jaxGesuch.setEingangsdatum(LocalDate.now());
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
		jaxKind.setPensumFachstelle(createTestJaxPensumFachstelle());
		jaxKind.setBemerkungen("Notizen");
		jaxKind.setMutterspracheDeutsch(false);
		jaxKind.setFamilienErgaenzendeBetreuung(true);
		jaxKind.setKinderabzug(Kinderabzug.GANZER_ABZUG);
		return jaxKind;
	}

	private static JaxPensumFachstelle createTestJaxPensumFachstelle() {
		JaxPensumFachstelle jaxPensumFachstelle = new JaxPensumFachstelle();
		jaxPensumFachstelle.setGueltigBis(LocalDate.now().plusMonths(1));
		jaxPensumFachstelle.setGueltigAb(LocalDate.now());
		jaxPensumFachstelle.setPensum(50);
		jaxPensumFachstelle.setFachstelle(createTestJaxFachstelle());
		return jaxPensumFachstelle;
	}

	public static JaxKindContainer createTestJaxKindContainer() {
		JaxKindContainer jaxKindContainer = new JaxKindContainer();
		jaxKindContainer.setKindGS(createTestJaxKind());
		jaxKindContainer.setKindJA(createTestJaxKind());
		return jaxKindContainer;
	}

	public static JaxBetreuungspensum createTestJaxBetreuungspensum(LocalDate from, LocalDate to){
		JaxBetreuungspensum jaxBetreuungspensum = new JaxBetreuungspensum();
		jaxBetreuungspensum.setGueltigAb(from);
		jaxBetreuungspensum.setGueltigBis(to);

		jaxBetreuungspensum.setPensum(40);
		return jaxBetreuungspensum;
	}

	public static  JaxBetreuungspensumContainer createBetreuungspensumContainer(int year){

		LocalDate from = LocalDate.of(year, 8, 1);
		LocalDate to = LocalDate.of(year  + 1 , 7, 31);

		JaxBetreuungspensumContainer jaxBetrPenCnt = new JaxBetreuungspensumContainer();
		jaxBetrPenCnt.setBetreuungspensumJA(createTestJaxBetreuungspensum(from, to));
		jaxBetrPenCnt.setBetreuungspensumGS(createTestJaxBetreuungspensum(from, to));
		return jaxBetrPenCnt;
	}

	public static JaxBetreuung createTestJaxBetreuung() {
		JaxBetreuung betreuung = new JaxBetreuung();
		JaxInstitutionStammdaten jaxInst = createTestJaxInstitutionsStammdaten();
		betreuung.setInstitutionStammdaten(jaxInst);
		betreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
		betreuung.setBetreuungspensumContainers(new ArrayList<>());
		betreuung.setBemerkungen("Betreuung_Bemerkungen");
		return betreuung;
	}

	private static JaxInstitutionStammdaten createTestJaxInstitutionsStammdaten() {
		JaxInstitutionStammdaten institutionStammdaten = new JaxInstitutionStammdaten();
		institutionStammdaten.setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		institutionStammdaten.setOeffnungsstunden(new BigDecimal(1000));
		institutionStammdaten.setGueltigAb(LocalDate.now());
		institutionStammdaten.setOeffnungstage(new BigDecimal(250));
		return institutionStammdaten;
	}

	public static JaxGesuchsperiode createTestJaxGesuchsperiode() {
		JaxGesuchsperiode jaxGesuchsperiode = new JaxGesuchsperiode();
		jaxGesuchsperiode.setGueltigAb(LocalDate.now());
		jaxGesuchsperiode.setGueltigBis(LocalDate.now().plusMonths(1));
		jaxGesuchsperiode.setActive(true);
		return jaxGesuchsperiode;
	}
}
