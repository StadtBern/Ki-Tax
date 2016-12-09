package ch.dvbern.ebegu.rest.test.util;

import ch.dvbern.ebegu.api.client.JaxOpenIdmResult;
import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.enums.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generiert Testdaten fuer JAX DTOs
 */
public class TestJaxDataUtil {

	public static JaxGesuchsteller createTestJaxGesuchsteller() {

		JaxGesuchsteller jaxGesuchsteller = new JaxGesuchsteller();
		jaxGesuchsteller.setNachname("Jaxter");
		jaxGesuchsteller.setVorname("Jack");
		jaxGesuchsteller.setAdressen(createTestJaxAdressenList(null));
		jaxGesuchsteller.setGeburtsdatum(LocalDate.now().minusYears(18));
		jaxGesuchsteller.setMail("jax.jaxter@example.com");
		jaxGesuchsteller.setGeschlecht(Geschlecht.MAENNLICH);
		jaxGesuchsteller.setMobile("+41 78 987 65 54");
		jaxGesuchsteller.setTelefonAusland("+49 12 123 42 12");
		jaxGesuchsteller.setZpvNumber("1234");

		return jaxGesuchsteller;

	}

	public static JaxGesuchsteller createTestJaxGesuchstellerWithUmzug() {
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

		jaxGesuchsteller.addAdresse(umzugAdr);
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

	public static JaxErwerbspensumContainer createTestJaxErwerbspensumContainer() {
		JaxErwerbspensum testJaxErwerbspensum = createTestJaxErwerbspensum();
		JaxErwerbspensumContainer container = new JaxErwerbspensumContainer();
		container.setErwerbspensumGS(testJaxErwerbspensum);
		return container;
	}


	public static JaxErwerbspensum createTestJaxErwerbspensum() {
		JaxErwerbspensum jaxErwerbspensum = new JaxErwerbspensum();
		jaxErwerbspensum.setTaetigkeit(Taetigkeit.ANGESTELLT);
		jaxErwerbspensum.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		jaxErwerbspensum.setZuschlagZuErwerbspensum(true);
		jaxErwerbspensum.setZuschlagsprozent(15);
		jaxErwerbspensum.setGueltigAb(LocalDate.now().minusYears(1));
		jaxErwerbspensum.setPensum(70);
		return jaxErwerbspensum;

	}

	public static List<JaxAdresse> createTestJaxAdressenList(@Nullable String postfix) {
		final List<JaxAdresse> adressen = new ArrayList<>();
		adressen.add(createTestJaxAdr(postfix));
		return adressen;
	}

	public static JaxAdresse createTestJaxAdr(@Nullable String postfix) {
		postfix = StringUtils.isEmpty(postfix) ? "" : postfix;
		JaxAdresse jaxAdresse = new JaxAdresse();
		jaxAdresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		jaxAdresse.setGemeinde("Bern" + postfix);
		jaxAdresse.setHausnummer("1" + postfix);
		jaxAdresse.setLand(Land.CH);
		jaxAdresse.setOrt("Bern" + postfix);
		jaxAdresse.setPlz("3014" + postfix);
		jaxAdresse.setZusatzzeile("Test" + postfix);
		jaxAdresse.setStrasse("Nussbaumstrasse" + postfix);
		jaxAdresse.setNichtInGemeinde(false);
		return jaxAdresse;
	}

	public static JaxFall createTestJaxFall() {
		JaxFall jaxFall = new JaxFall();
		jaxFall.setFallNummer(1);
		jaxFall.setVerantwortlicher(createTestJaxBenutzer());
		return jaxFall;
	}

	public static JaxAuthLoginElement createTestJaxBenutzer() {
		JaxAuthLoginElement jaxBenutzer = new JaxAuthLoginElement();
		jaxBenutzer.setRole(UserRole.ADMIN);
		jaxBenutzer.setUsername("TestUser");
		jaxBenutzer.setPassword("1234");
		jaxBenutzer.setEmail("e@e.ch");
		jaxBenutzer.setNachname("NachnameTest");
		jaxBenutzer.setVorname("VornameTest");
		return jaxBenutzer;
	}

	public static JaxGesuch createTestJaxGesuch() {
		JaxGesuch jaxGesuch = new JaxGesuch();
		jaxGesuch.setEingangsart(Eingangsart.PAPIER);
		jaxGesuch.setFall(createTestJaxFall());
		jaxGesuch.setGesuchsperiode(createTestJaxGesuchsperiode());
		jaxGesuch.setGesuchsteller1(createTestJaxGesuchsteller());
		jaxGesuch.setEingangsdatum(LocalDate.now());
		jaxGesuch.setStatus(AntragStatusDTO.IN_BEARBEITUNG_JA);
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
		jaxKind.setMutterspracheDeutsch(false);
		jaxKind.setEinschulung(false);
		jaxKind.setFamilienErgaenzendeBetreuung(true);
		jaxKind.setKinderabzug(Kinderabzug.GANZER_ABZUG);
		return jaxKind;
	}

	public static JaxPensumFachstelle createTestJaxPensumFachstelle() {
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

	public static JaxBetreuungspensum createTestJaxBetreuungspensum(LocalDate from, LocalDate to) {
		JaxBetreuungspensum jaxBetreuungspensum = new JaxBetreuungspensum();
		jaxBetreuungspensum.setGueltigAb(from);
		jaxBetreuungspensum.setGueltigBis(to);

		jaxBetreuungspensum.setPensum(40);
		return jaxBetreuungspensum;
	}

	public static JaxBetreuungspensumContainer createBetreuungspensumContainer(int year) {

		LocalDate from = LocalDate.of(year, 8, 1);
		LocalDate to = LocalDate.of(year + 1, 7, 31);

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
		return betreuung;
	}

	public static JaxInstitutionStammdaten createTestJaxInstitutionsStammdaten() {
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

	public static JaxEinkommensverschlechterungInfo createTestJaxEinkommensverschlechterungInfo() {
		JaxEinkommensverschlechterungInfo jaxEinkommensverschlechterungInfo = new JaxEinkommensverschlechterungInfo();
		jaxEinkommensverschlechterungInfo.setEinkommensverschlechterung(true);
		jaxEinkommensverschlechterungInfo.setEkvFuerBasisJahrPlus1(true);
		jaxEinkommensverschlechterungInfo.setStichtagFuerBasisJahrPlus1(LocalDate.now());
		jaxEinkommensverschlechterungInfo.setGrundFuerBasisJahrPlus1("Grund fuer basis Jahr Plus 1");
		jaxEinkommensverschlechterungInfo.setEkvFuerBasisJahrPlus2(false);
		return jaxEinkommensverschlechterungInfo;
	}


	public static JaxEinkommensverschlechterungContainer createTestJaxEinkommensverschlechterungContianer() {
		JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer = new JaxEinkommensverschlechterungContainer();

		einkommensverschlechterungContainer.setEkvGSBasisJahrPlus1(createDefaultJaxEinkommensverschlechterungs());

		final JaxEinkommensverschlechterung ekvGSBasisJahrPlus2 = createDefaultJaxEinkommensverschlechterungs();
		ekvGSBasisJahrPlus2.setNettolohnJan(BigDecimal.valueOf(2));
		einkommensverschlechterungContainer.setEkvGSBasisJahrPlus2(ekvGSBasisJahrPlus2);

		final JaxEinkommensverschlechterung ekvJABasisJahrPlus1 = createDefaultJaxEinkommensverschlechterungs();
		ekvJABasisJahrPlus1.setNettolohnJan(BigDecimal.valueOf(3));
		einkommensverschlechterungContainer.setEkvJABasisJahrPlus1(ekvJABasisJahrPlus1);

		final JaxEinkommensverschlechterung ekvJABasisJahrPlus2 = createDefaultJaxEinkommensverschlechterungs();
		ekvJABasisJahrPlus2.setNettolohnJan(BigDecimal.valueOf(4));
		einkommensverschlechterungContainer.setEkvJABasisJahrPlus2(ekvJABasisJahrPlus2);


		return einkommensverschlechterungContainer;
	}

	public static JaxEinkommensverschlechterung createDefaultJaxEinkommensverschlechterungs() {
		JaxEinkommensverschlechterung einkommensverschlechterung = new JaxEinkommensverschlechterung();
		createDefaultAbstractFinanzielleSituation(einkommensverschlechterung);
		einkommensverschlechterung.setNettolohnJan(BigDecimal.ONE);
		return einkommensverschlechterung;
	}

	public static void createDefaultAbstractFinanzielleSituation(JaxAbstractFinanzielleSituation abstractFinanzielleSituation) {
		abstractFinanzielleSituation.setSteuerveranlagungErhalten(Boolean.FALSE);
		abstractFinanzielleSituation.setSteuererklaerungAusgefuellt(Boolean.TRUE);
	}

	public static JaxMandant createTestMandant() {
		JaxMandant mandant = new JaxMandant();
		mandant.setName("TestMandant");
		return mandant;
	}

	public static JaxInstitution createTestJaxInstitution() {
		JaxInstitution institution = new JaxInstitution();
		institution.setMandant(createTestMandant());
		institution.setName("Inst1");
		institution.setTraegerschaft(createJaxTestTraegerschaft());
		return  institution;
	}

	public static JaxTraegerschaft createJaxTestTraegerschaft() {
		JaxTraegerschaft jaxTraegerschaft = new JaxTraegerschaft();
		jaxTraegerschaft.setName("Test_Traegerschaft");
		jaxTraegerschaft.setActive(true);
		return null;
	}

	public static JaxOpenIdmResult creatOpenIdmTraegerschaft(String name) {
		JaxOpenIdmResult jaxOpenIdmResult = new JaxOpenIdmResult();
		jaxOpenIdmResult.set_id(name);
		jaxOpenIdmResult.setName(name);
		jaxOpenIdmResult.setMail(name + "@" + name + ".ch");
		jaxOpenIdmResult.setType("sponsor");
		return jaxOpenIdmResult;
	}

	public static JaxOpenIdmResult creatOpenIdmInst(String name) {
		JaxOpenIdmResult jaxOpenIdmResult = new JaxOpenIdmResult();
		jaxOpenIdmResult.set_id(name);
		jaxOpenIdmResult.setName(name);
		jaxOpenIdmResult.setMail(name + "@" + name + ".ch");
		jaxOpenIdmResult.setType("institution");
		return jaxOpenIdmResult;
	}

}
