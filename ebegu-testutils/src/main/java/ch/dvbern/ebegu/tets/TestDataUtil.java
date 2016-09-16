package ch.dvbern.ebegu.tets;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import ch.dvbern.lib.cdipersistence.Persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * comments homa
 */
public final class TestDataUtil {

	private static final String iban = "CH39 0900 0000 3066 3817 2";

	public static final LocalDate STICHTAG_EKV_1 = LocalDate.of(2016, Month.SEPTEMBER, 1);
	public static final LocalDate STICHTAG_EKV_2 = LocalDate.of(2017, Month.APRIL, 1);

	private TestDataUtil() {
	}

	public static GesuchstellerAdresse createDefaultGesuchstellerAdresse() {
		GesuchstellerAdresse gesuchstellerAdresse = new GesuchstellerAdresse();
		gesuchstellerAdresse.setStrasse("Nussbaumstrasse");
		gesuchstellerAdresse.setHausnummer("21");
		gesuchstellerAdresse.setZusatzzeile("c/o Uwe Untermieter");
		gesuchstellerAdresse.setPlz("3014");
		gesuchstellerAdresse.setOrt("Bern");
		gesuchstellerAdresse.setGueltigkeit(new DateRange(LocalDate.now(), Constants.END_OF_TIME));
		gesuchstellerAdresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		return gesuchstellerAdresse;
	}

	public static Adresse createDefaultAdresse() {
		Adresse adresse = new Adresse();
		adresse.setStrasse("Nussbaumstrasse");
		adresse.setHausnummer("21");
		adresse.setZusatzzeile("c/o Uwe Untermieter");
		adresse.setPlz("3014");
		adresse.setOrt("Bern");
		adresse.setGueltigkeit(new DateRange(LocalDate.now(), Constants.END_OF_TIME));
		return adresse;
	}

	public static Gesuchsteller createDefaultGesuchsteller() {
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setGeburtsdatum(LocalDate.of(1984, 12, 12));
		gesuchsteller.setVorname("Tim");
		gesuchsteller.setNachname("Tester");
		gesuchsteller.setGeschlecht(Geschlecht.MAENNLICH);
		gesuchsteller.setMail("tim.tester@example.com");
		gesuchsteller.setMobile("076 309 30 58");
		gesuchsteller.setTelefon("031 378 24 24");
		gesuchsteller.setZpvNumber("0761234567897");
		gesuchsteller.addAdresse(createDefaultGesuchstellerAdresse());
		return gesuchsteller;
	}

	public static EinkommensverschlechterungContainer createDefaultEinkommensverschlechterungsContainer() {
		EinkommensverschlechterungContainer einkommensverschlechterungContainer = new EinkommensverschlechterungContainer();

		einkommensverschlechterungContainer.setEkvGSBasisJahrPlus1(createDefaultEinkommensverschlechterung());

		final Einkommensverschlechterung ekvGSBasisJahrPlus2 = createDefaultEinkommensverschlechterung();
		ekvGSBasisJahrPlus2.setNettolohnJan(BigDecimal.valueOf(2));
		einkommensverschlechterungContainer.setEkvGSBasisJahrPlus2(ekvGSBasisJahrPlus2);

		final Einkommensverschlechterung ekvJABasisJahrPlus1 = createDefaultEinkommensverschlechterung();
		ekvJABasisJahrPlus1.setNettolohnJan(BigDecimal.valueOf(3));
		einkommensverschlechterungContainer.setEkvJABasisJahrPlus1(ekvJABasisJahrPlus1);

		final Einkommensverschlechterung ekvJABasisJahrPlus2 = createDefaultEinkommensverschlechterung();
		ekvJABasisJahrPlus2.setNettolohnJan(BigDecimal.valueOf(4));
		einkommensverschlechterungContainer.setEkvJABasisJahrPlus2(ekvJABasisJahrPlus2);


		return einkommensverschlechterungContainer;
	}

	public static Einkommensverschlechterung createDefaultEinkommensverschlechterung() {
		Einkommensverschlechterung einkommensverschlechterung = new Einkommensverschlechterung();
		createDefaultAbstractFinanzielleSituation(einkommensverschlechterung);
		einkommensverschlechterung.setNettolohnJan(BigDecimal.ONE);
		return einkommensverschlechterung;
	}

	public static Familiensituation createDefaultFamiliensituation() {
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		familiensituation.setGemeinsameSteuererklaerung(Boolean.TRUE);
		return familiensituation;
	}

	public static Gesuch createDefaultGesuch() {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(createDefaultGesuchsperiode());
		gesuch.setFall(createDefaultFall());
		gesuch.setEingangsdatum(LocalDate.now());
		gesuch.setFamiliensituation(createDefaultFamiliensituation());
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
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

	public static FinanzielleSituationContainer createFinanzielleSituationContainer() {
		FinanzielleSituationContainer container = new FinanzielleSituationContainer();
		container.setJahr(LocalDate.now().minusYears(1).getYear());
		return container;
	}

	public static FinanzielleSituation createDefaultFinanzielleSituation() {

		FinanzielleSituation finanzielleSituation = new FinanzielleSituation();
		createDefaultAbstractFinanzielleSituation(finanzielleSituation);
		finanzielleSituation.setNettolohn(BigDecimal.valueOf(100000));
		return finanzielleSituation;
	}

	public static void createDefaultAbstractFinanzielleSituation(AbstractFinanzielleSituation abstractFinanzielleSituation) {
		abstractFinanzielleSituation.setSteuerveranlagungErhalten(Boolean.FALSE);
		abstractFinanzielleSituation.setSteuererklaerungAusgefuellt(Boolean.TRUE);
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
		InstitutionStammdaten instStammdaten = new InstitutionStammdaten();
		instStammdaten.setIban(new IBAN(iban));
		instStammdaten.setOeffnungsstunden(BigDecimal.valueOf(24));
		instStammdaten.setOeffnungstage(BigDecimal.valueOf(365));
		instStammdaten.setGueltigkeit(new DateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31)));
		instStammdaten.setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		instStammdaten.setInstitution(createDefaultInstitution());
		instStammdaten.setAdresse(createDefaultAdresse());
		return instStammdaten;
	}

	public static InstitutionStammdaten createInstitutionStammdatenKitaAaregg() {
		InstitutionStammdaten instStammdaten = new InstitutionStammdaten();
		instStammdaten.setId(AbstractTestfall.idInstitutionAaregg);
		instStammdaten.setIban(new IBAN(iban));
		instStammdaten.setOeffnungsstunden(BigDecimal.valueOf(11.50));
		instStammdaten.setOeffnungstage(BigDecimal.valueOf(240));
		instStammdaten.setGueltigkeit(Constants.DEFAULT_GUELTIGKEIT);
		instStammdaten.setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		instStammdaten.setInstitution(createDefaultInstitution());
		instStammdaten.getInstitution().setId(AbstractTestfall.idInstitutionAaregg);
		instStammdaten.getInstitution().setName("Kita Aaregg");
		instStammdaten.setAdresse(createDefaultAdresse());
		return instStammdaten;
	}

	public static InstitutionStammdaten createInstitutionStammdatenTagiAaregg() {
		InstitutionStammdaten instStammdaten = new InstitutionStammdaten();
		instStammdaten.setId("c10405d6-a905-4879-bb38-fca4cbb3f06f");
		instStammdaten.setIban(new IBAN(iban));
		instStammdaten.setOeffnungsstunden(BigDecimal.valueOf(9));
		instStammdaten.setOeffnungstage(BigDecimal.valueOf(244));
		instStammdaten.setGueltigkeit(Constants.DEFAULT_GUELTIGKEIT);
		instStammdaten.setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGI);
		instStammdaten.setInstitution(createDefaultInstitution());
		instStammdaten.getInstitution().setId("9253e9b1-9cae-4278-b578-f1ce93306d29");
		instStammdaten.getInstitution().setName("Tagi Aaregg");
		instStammdaten.setAdresse(createDefaultAdresse());
		return instStammdaten;
	}

	public static InstitutionStammdaten createInstitutionStammdatenKitaBruennen() {
		InstitutionStammdaten instStammdaten = new InstitutionStammdaten();
		instStammdaten.setId(AbstractTestfall.idInstitutionBruennen);
		instStammdaten.setIban(new IBAN(iban));
		instStammdaten.setOeffnungsstunden(BigDecimal.valueOf(11.50));
		instStammdaten.setOeffnungstage(BigDecimal.valueOf(240));
		instStammdaten.setGueltigkeit(Constants.DEFAULT_GUELTIGKEIT);
		instStammdaten.setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		instStammdaten.setInstitution(createDefaultInstitution());
		instStammdaten.getInstitution().setId(AbstractTestfall.idInstitutionBruennen);
		instStammdaten.getInstitution().setName("Kita Brünnen");
		instStammdaten.setAdresse(createDefaultAdresse());
		return instStammdaten;
	}

	public static Kind createDefaultKind() {
		Kind kind = new Kind();
		kind.setNachname("Kind_Mustermann");
		kind.setVorname("Kind_Max");
		kind.setGeburtsdatum(LocalDate.of(2010, 12, 12));
		kind.setGeschlecht(Geschlecht.WEIBLICH);
		kind.setKinderabzug(Kinderabzug.GANZER_ABZUG);
		kind.setPensumFachstelle(createDefaultPensumFachstelle());
		kind.setFamilienErgaenzendeBetreuung(true);
		kind.setMutterspracheDeutsch(true);
		return kind;
	}

	public static PensumFachstelle createDefaultPensumFachstelle() {
		PensumFachstelle pensumFachstelle = new PensumFachstelle();
		pensumFachstelle.setPensum(50);
		pensumFachstelle.setGueltigkeit(new DateRange(LocalDate.now(), Constants.END_OF_TIME));
		pensumFachstelle.setFachstelle(createDefaultFachstelle());
		return pensumFachstelle;
	}

	public static KindContainer createDefaultKindContainer() {
		KindContainer kindContainer = new KindContainer();
		Kind defaultKindGS = createDefaultKind();
		defaultKindGS.setNachname("GS_Kind");
		kindContainer.setKindGS(defaultKindGS);
		Kind defaultKindJA = createDefaultKind();
		defaultKindJA.setNachname("JA_Kind");
		kindContainer.setKindJA(defaultKindJA);
		return kindContainer;
	}

	public static ErwerbspensumContainer createErwerbspensumContainer() {
		ErwerbspensumContainer epCont = new ErwerbspensumContainer();
		epCont.setErwerbspensumGS(createErwerbspensumData());
		Erwerbspensum epKorrigiertJA = createErwerbspensumData();
		epKorrigiertJA.setTaetigkeit(Taetigkeit.RAV);
		epCont.setErwerbspensumJA(epKorrigiertJA);
		return epCont;
	}

	public static ErwerbspensumContainer createErwerbspensum(LocalDate von, LocalDate bis, int pensum, int zuschlag) {
		ErwerbspensumContainer erwerbspensumContainer = new ErwerbspensumContainer();
		Erwerbspensum erwerbspensum = new Erwerbspensum();
		erwerbspensum.setPensum(pensum);
		erwerbspensum.setZuschlagsprozent(zuschlag);
		erwerbspensum.setGueltigkeit(new DateRange(von, bis));
		erwerbspensumContainer.setErwerbspensumJA(erwerbspensum);
		return erwerbspensumContainer;
	}

	public static Erwerbspensum createErwerbspensumData() {
		Erwerbspensum ep = new Erwerbspensum();
		ep.setTaetigkeit(Taetigkeit.ANGESTELLT);
		ep.setPensum(50);
		ep.setZuschlagZuErwerbspensum(true);
		ep.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		ep.setZuschlagsprozent(10);
		return ep;
	}

	public static Betreuung createDefaultBetreuung() {
		Betreuung betreuung = new Betreuung();
		betreuung.setInstitutionStammdaten(createDefaultInstitutionStammdaten());
		betreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
		betreuung.setBetreuungspensumContainers(new HashSet<>());
		betreuung.setKind(createDefaultKindContainer());
		return betreuung;
	}

	public static BetreuungspensumContainer createBetPensContainer(Betreuung betreuung) {
		BetreuungspensumContainer container = new BetreuungspensumContainer();
		container.setBetreuung(betreuung);
		container.setBetreuungspensumGS(TestDataUtil.createBetreuungspensum());
		container.setBetreuungspensumJA(TestDataUtil.createBetreuungspensum());
		return container;
	}

	private static Betreuungspensum createBetreuungspensum() {
		Betreuungspensum betreuungspensum = new Betreuungspensum();
		betreuungspensum.setPensum(80);
		return betreuungspensum;
	}

	public static Gesuchsperiode createDefaultGesuchsperiode() {
		return createCurrentGesuchsperiode();
	}

	public static Gesuchsperiode createCurrentGesuchsperiode() {
		Gesuchsperiode gesuchsperiode = new Gesuchsperiode();
		gesuchsperiode.setActive(true);

		boolean isSecondHalbjahr = LocalDate.now().isAfter(LocalDate.of(LocalDate.now().getYear(), Month.JULY, 31));
		int startyear = isSecondHalbjahr ? LocalDate.now().getYear() : LocalDate.now().getYear() - 1;
		LocalDate start = LocalDate.of(startyear, Month.AUGUST, 1);
		LocalDate end = LocalDate.of(startyear + 1, Month.JULY, 31);
		gesuchsperiode.setGueltigkeit(new DateRange(start, end));
		return gesuchsperiode;
	}


	public static Gesuchsperiode createGesuchsperiode1617() {
		Gesuchsperiode gesuchsperiode = new Gesuchsperiode();
		gesuchsperiode.setActive(true);
		gesuchsperiode.setGueltigkeit(new DateRange(LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31)));
		return gesuchsperiode;
	}


	public static EbeguParameter createDefaultEbeguParameter() {
		EbeguParameter instStammdaten = new EbeguParameter();
		instStammdaten.setName(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA);
		instStammdaten.setValue("Wert");
		instStammdaten.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		return instStammdaten;
	}

	public static EinkommensverschlechterungInfo createDefaultEinkommensverschlechterungsInfo(Gesuch gesuch) {
		final EinkommensverschlechterungInfo einkommensverschlechterungInfo = new EinkommensverschlechterungInfo();
		einkommensverschlechterungInfo.setEinkommensverschlechterung(true);
		einkommensverschlechterungInfo.setEkvFuerBasisJahrPlus1(true);
		einkommensverschlechterungInfo.setStichtagFuerBasisJahrPlus1(LocalDate.now());
		einkommensverschlechterungInfo.setGrundFuerBasisJahrPlus1("Grund fuer basis Jahr Plus 1");
		einkommensverschlechterungInfo.setEkvFuerBasisJahrPlus2(false);
		einkommensverschlechterungInfo.setGesuch(gesuch);
		return einkommensverschlechterungInfo;

	}

	public static Gesuch createDefaultEinkommensverschlechterungsGesuch() {
		Gesuch gesuch = createDefaultGesuch();
		gesuch.setEinkommensverschlechterungInfo(createDefaultEinkommensverschlechterungsInfo(gesuch));
		return gesuch;
	}

	public static Gesuchsteller createDefaultGesuchstellerWithEinkommensverschlechterung() {
		final Gesuchsteller gesuchsteller = createDefaultGesuchsteller();
		gesuchsteller.setEinkommensverschlechterungContainer(createDefaultEinkommensverschlechterungsContainer());
		return gesuchsteller;
	}

	public static Benutzer createDefaultBenutzer() {
		Benutzer user = new Benutzer();
		user.setUsername("jula");
		user.setNachname("Julio");
		user.setVorname("Iglesias");
		user.setEmail("email@server.ch");
		user.setMandant(createDefaultMandant());
		user.setRole(UserRole.ADMIN);
		return user;
	}

	public static Betreuung createGesuchWithBetreuungspensum(boolean zweiGesuchsteller) {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		gesuch.setFamiliensituation(new Familiensituation());
		gesuch.getFamiliensituation().setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		if (zweiGesuchsteller) {
			gesuch.getFamiliensituation().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		} else {
			gesuch.getFamiliensituation().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		}
		gesuch.setGesuchsteller1(new Gesuchsteller());
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		if (zweiGesuchsteller) {
			gesuch.setGesuchsteller2(new Gesuchsteller());
			gesuch.getGesuchsteller2().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
			gesuch.getGesuchsteller2().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		}
		Betreuung betreuung = new Betreuung();
		betreuung.setKind(new KindContainer());
		betreuung.getKind().setKindJA(new Kind());
		betreuung.getKind().setGesuch(gesuch);
		betreuung.setInstitutionStammdaten(createDefaultInstitutionStammdaten());
		return betreuung;
	}

	public static void calculateFinanzDaten(Gesuch gesuch) {
		if (gesuch.getGesuchsperiode() == null) {
			gesuch.setGesuchsperiode(createGesuchsperiode1617());
		}
		FinanzielleSituationRechner finanzielleSituationRechner = new FinanzielleSituationRechner();
		finanzielleSituationRechner.calculateFinanzDaten(gesuch);
	}

	public static Gesuch createTestgesuchDagmar() {
		List<InstitutionStammdaten> insttStammdaten = new ArrayList<>();
		insttStammdaten.add(TestDataUtil.createDefaultInstitutionStammdaten());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), insttStammdaten);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		return gesuch;
	}

	public static void setFinanzielleSituation(Gesuch gesuch, BigDecimal einkommen) {
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(einkommen);
	}

	public static void setEinkommensverschlechterung(Gesuch gesuch, Gesuchsteller gesuchsteller, BigDecimal einkommen, boolean basisJahrPlus1) {
		if (gesuchsteller.getEinkommensverschlechterungContainer() == null) {
			gesuchsteller.setEinkommensverschlechterungContainer(new EinkommensverschlechterungContainer());
		}
		if (gesuch.getEinkommensverschlechterungInfo() == null) {
			gesuch.setEinkommensverschlechterungInfo(new EinkommensverschlechterungInfo());
			gesuch.getEinkommensverschlechterungInfo().setEinkommensverschlechterung(true);
		}
		if (basisJahrPlus1) {
			gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus1(new Einkommensverschlechterung());
			gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1().setNettolohnAug(einkommen);
			gesuch.getEinkommensverschlechterungInfo().setEkvFuerBasisJahrPlus1(true);
			gesuch.getEinkommensverschlechterungInfo().setStichtagFuerBasisJahrPlus1(STICHTAG_EKV_1);
			gesuch.getEinkommensverschlechterungInfo().setEinkommensverschlechterung(true);
		} else {
			gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus2(new Einkommensverschlechterung());
			gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2().setNettolohnAug(einkommen);
			gesuch.getEinkommensverschlechterungInfo().setEkvFuerBasisJahrPlus2(true);
			gesuch.getEinkommensverschlechterungInfo().setStichtagFuerBasisJahrPlus2(STICHTAG_EKV_2);
			gesuch.getEinkommensverschlechterungInfo().setEinkommensverschlechterung(true);
		}
	}

	public static DokumentGrund createDefaultDokumentGrund() {

		DokumentGrund dokumentGrund = new DokumentGrund();
		dokumentGrund.setDokumentGrundTyp(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
		dokumentGrund.setTag("tag");
		dokumentGrund.setFullName("Hugo");
		dokumentGrund.setDokumentTyp(DokumentTyp.JAHRESLOHNAUSWEISE);
		dokumentGrund.setDokumente(new HashSet<>());
		final Dokument dokument = new Dokument();
		dokument.setDokumentGrund(dokumentGrund);
		dokument.setDokumentName("testdokument");
		dokument.setDokumentPfad("testpfad/");
		dokument.setDokumentSize("123456");
		dokumentGrund.getDokumente().add(dokument);
		return dokumentGrund;
	}

	/**
	 * Hilfsmethode die den Testfall Waelti Dagmar erstellt und speichert
	 */
	public static Gesuch createAndPersistWaeltiDagmarGesuch(InstitutionService instService, Persistence<Gesuch> persistence) {
		instService.getAllInstitutionen();
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		Gesuch gesuch = testfall.createGesuch();
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				persistence.merge(betreuung.getInstitutionStammdaten().getInstitution().getTraegerschaft());
				persistence.merge(betreuung.getInstitutionStammdaten().getInstitution().getMandant());
				if (persistence.find(Institution.class, betreuung.getInstitutionStammdaten().getInstitution().getId()) == null) {
					persistence.merge(betreuung.getInstitutionStammdaten().getInstitution());
				}
				if (persistence.find(InstitutionStammdaten.class, betreuung.getInstitutionStammdaten().getId()) == null) {
					persistence.merge(betreuung.getInstitutionStammdaten());
				}
				if (betreuung.getKind().getKindJA().getPensumFachstelle() != null) {
					persistence.merge(betreuung.getKind().getKindJA().getPensumFachstelle().getFachstelle());
				}
			}
		}
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch.getGesuchsperiode());
		gesuch = persistence.persist(gesuch);
		return gesuch;
	}

	public static void persistEntities(Gesuch gesuch, Persistence<Gesuch> persistence) {
		Benutzer verantwortlicher = TestDataUtil.createDefaultBenutzer();
		persistence.persist(verantwortlicher.getMandant());
		persistence.persist(verantwortlicher);

		gesuch.getFall().setVerantwortlicher(verantwortlicher);
		persistence.persist(gesuch.getFall());
		gesuch.setGesuchsteller1(TestDataUtil.createDefaultGesuchsteller());
		persistence.persist(gesuch.getGesuchsperiode());

		Set<KindContainer> kindContainers = new LinkedHashSet<>();
		Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		KindContainer kind = betreuung.getKind();

		Set<Betreuung> betreuungen = new LinkedHashSet<>();
		betreuungen.add(betreuung);
		kind.setBetreuungen(betreuungen);

		persistence.persist(kind.getKindGS().getPensumFachstelle().getFachstelle());
		persistence.persist(kind.getKindJA().getPensumFachstelle().getFachstelle());
		kind.setGesuch(gesuch);
		kindContainers.add(kind);
		gesuch.setKindContainers(kindContainers);


		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getTraegerschaft());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getMandant());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution());
		persistence.persist(betreuung.getInstitutionStammdaten());

		persistence.persist(gesuch);
	}

	public static Gesuch createAndPersistGesuch(Persistence<Gesuch> persistence) {
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch.getGesuchsperiode());
		persistence.persist(gesuch);
		return gesuch;
	}

	public static WizardStep createWizardStepObject(Gesuch gesuch, WizardStepName wizardStepName, WizardStepStatus stepStatus) {
		final WizardStep jaxWizardStep = new WizardStep();
		jaxWizardStep.setGesuch(gesuch);
		jaxWizardStep.setWizardStepName(wizardStepName);
		jaxWizardStep.setWizardStepStatus(stepStatus);
		jaxWizardStep.setBemerkungen("");
		return jaxWizardStep;
	}

	public static Benutzer createBenutzer(UserRole role, Traegerschaft traegerschaft, Institution institution, Mandant mandant) {
		final Benutzer benutzer = new Benutzer();
		benutzer.setUsername("anonymous");
		benutzer.setNachname("anonymous");
		benutzer.setVorname("anonymous");
		benutzer.setEmail("e@e");
		benutzer.setTraegerschaft(traegerschaft);
		benutzer.setInstitution(institution);
		benutzer.setRole(role);
		benutzer.setMandant(mandant);
		return benutzer;
	}

	public static Benutzer createAndPersistBenutzer(Persistence<?> persistence, UserRole role) {
		final Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		persistence.persist(traegerschaft);
		final Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		return TestDataUtil.createBenutzer(role, traegerschaft, null, mandant);
	}
}
