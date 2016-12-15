package ch.dvbern.ebegu.tests.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.rules.anlageverzeichnis.ErwerbspensumDokumente;
import ch.dvbern.ebegu.rules.anlageverzeichnis.FinanzielleSituationDokumente;
import ch.dvbern.ebegu.rules.anlageverzeichnis.KindDokumente;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests für die Regeln der Benötigten Dokumenten
 */
public class DokumentenverzeichnisEvaluatorTest {


	private DokumentenverzeichnisEvaluator evaluator = new DokumentenverzeichnisEvaluator();
	private KindDokumente kindDokumente = new KindDokumente();
	private ErwerbspensumDokumente erwerbspensumDokumente = new ErwerbspensumDokumente();
	private FinanzielleSituationDokumente finanzielleSituationDokumente = new FinanzielleSituationDokumente();
	private Gesuch testgesuch;

	@Before
	public void setUpCalculator() {

		testgesuch = new Gesuch();
		testgesuch.setGesuchsperiode(TestDataUtil.createDefaultGesuchsperiode());
		testgesuch.getGesuchsperiode().getGueltigkeit().setGueltigAb(LocalDate.of(2015, 1, 1));
		testgesuch.getGesuchsperiode().getGueltigkeit().setGueltigBis(LocalDate.of(2016, 1, 1));
		testgesuch.setKindContainers(new HashSet<>());
	}

	private Kind createKind(Gesuch gesuch, String vorname, Kinderabzug ganzerAbzug, String fachstellename, boolean behinderungsbest) {
		final KindContainer kindContainer = TestDataUtil.createDefaultKindContainer();
		kindContainer.getKindJA().setNachname("Chavez");
		kindContainer.getKindJA().setVorname(vorname);
		kindContainer.getKindJA().setKinderabzug(ganzerAbzug);

		if (fachstellename != null) {
			final PensumFachstelle defaultPensumFachstelle = TestDataUtil.createDefaultPensumFachstelle();
			defaultPensumFachstelle.getFachstelle().setName(fachstellename);
			defaultPensumFachstelle.getFachstelle().setBehinderungsbestaetigung(behinderungsbest);
			kindContainer.getKindJA().setPensumFachstelle(defaultPensumFachstelle);
		} else {
			kindContainer.getKindJA().setPensumFachstelle(null);
		}

		gesuch.getKindContainers().add(kindContainer);
		return kindContainer.getKindJA();
	}

	private void clearKinder(Gesuch gesuch) {
		gesuch.getKindContainers().clear();
	}

	private Erwerbspensum createErwerbspensum(Gesuch gesuch, String vorname, Taetigkeit taetigkeit,
											  boolean gesundheitlicheEinschraenkungen, boolean zuschlagZuErwerbspensum, Zuschlagsgrund zuschlagsgrund) {
		final ErwerbspensumContainer erwerbspensumContainer = TestDataUtil.createErwerbspensumContainer();

		final Erwerbspensum erwerbspensumJA = erwerbspensumContainer.getErwerbspensumJA();
		if (gesundheitlicheEinschraenkungen){
			erwerbspensumJA.setTaetigkeit(Taetigkeit.GESUNDHEITLICHE_EINSCHRAENKUNGEN);
		} else {
			erwerbspensumJA.setTaetigkeit(taetigkeit);
		}
		erwerbspensumJA.setZuschlagZuErwerbspensum(zuschlagZuErwerbspensum);
		erwerbspensumJA.setZuschlagsgrund(zuschlagsgrund);
		erwerbspensumJA.getGueltigkeit().setGueltigAb(LocalDate.of(1980, 1, 1));

		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		gesuchsteller.getGesuchstellerJA().setNachname("Chavez");
		gesuchsteller.getGesuchstellerJA().setVorname(vorname);

		gesuchsteller.getErwerbspensenContainers().add(erwerbspensumContainer);
		gesuch.setGesuchsteller1(gesuchsteller);

		return erwerbspensumJA;
	}

	private FinanzielleSituation createFinanzielleSituationGS(int GS, Gesuch gesuch, String vorname, boolean steuerveranlagungErhalten) {
		final FinanzielleSituationContainer finanzielleSituationContainer = TestDataUtil.createFinanzielleSituationContainer();
		final FinanzielleSituation finanzielleSituation = TestDataUtil.createDefaultFinanzielleSituation();

		finanzielleSituation.setSteuerveranlagungErhalten(steuerveranlagungErhalten);
		finanzielleSituationContainer.setFinanzielleSituationJA(finanzielleSituation);

		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		gesuchsteller.getGesuchstellerJA().setNachname("Chavez");
		gesuchsteller.getGesuchstellerJA().setVorname(vorname);

		gesuchsteller.setFinanzielleSituationContainer(finanzielleSituationContainer);
		if (GS == 2) {
			gesuch.setGesuchsteller2(gesuchsteller);
		} else {
			gesuch.setGesuchsteller1(gesuchsteller);
		}


		return finanzielleSituation;
	}

	private void createEinkommensverschlechterungGS(int GS, Gesuch gesuch, String vorname, boolean steuerveranlagungErhalten) {
		final EinkommensverschlechterungContainer einkommensverschlechterungsContainer = TestDataUtil.createDefaultEinkommensverschlechterungsContainer();
		final Einkommensverschlechterung einkommensverschlechterung1 = TestDataUtil.createDefaultEinkommensverschlechterung();
		final Einkommensverschlechterung einkommensverschlechterung2 = TestDataUtil.createDefaultEinkommensverschlechterung();

		einkommensverschlechterungsContainer.setEkvJABasisJahrPlus1(einkommensverschlechterung1);
		einkommensverschlechterungsContainer.setEkvJABasisJahrPlus2(einkommensverschlechterung2);

		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		gesuchsteller.getGesuchstellerJA().setNachname("Chavez");
		gesuchsteller.getGesuchstellerJA().setVorname(vorname);

		gesuchsteller.setEinkommensverschlechterungContainer(einkommensverschlechterungsContainer);
		if (GS == 2) {
			gesuch.setGesuchsteller2(gesuchsteller);
		} else {
			gesuch.setGesuchsteller1(gesuchsteller);
		}
	}


	private void createFamilienSituation(Gesuch gesuch, boolean gemeinsam) {

		final FamiliensituationContainer familiensituation = TestDataUtil.createDefaultFamiliensituationContainer();
		familiensituation.extractFamiliensituation().setGemeinsameSteuererklaerung(gemeinsam);
		gesuch.setFamiliensituationContainer(familiensituation);
	}

	@Test
	public void kindDokumentSorgerechtsvereinbarungTest() {

		clearKinder(testgesuch);
		final String kindName = "Sämi";
		Kind sämi = createKind(testgesuch, kindName, Kinderabzug.HALBER_ABZUG, null, false);

		Assert.assertTrue(kindDokumente.isDokumentNeeded(DokumentTyp.SORGERECHTSVEREINBARUNG, sämi));
		Assert.assertFalse(kindDokumente.isDokumentNeeded(DokumentTyp.FACHSTELLENBEST_BEH, sämi));
		Assert.assertFalse(kindDokumente.isDokumentNeeded(DokumentTyp.FACHSTELLENBEST_SOZ, sämi));

		final DokumentGrund dokumentGrund = getDokumentGrund(sämi);
		Assert.assertEquals(DokumentTyp.SORGERECHTSVEREINBARUNG, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void kindDokumentFachstelleSozTest() {

		clearKinder(testgesuch);
		final String kindName = "Jan";
		Kind kind = createKind(testgesuch, kindName, Kinderabzug.GANZER_ABZUG, "soz", false);

		Assert.assertFalse(kindDokumente.isDokumentNeeded(DokumentTyp.SORGERECHTSVEREINBARUNG, kind));
		Assert.assertFalse(kindDokumente.isDokumentNeeded(DokumentTyp.FACHSTELLENBEST_BEH, kind));
		Assert.assertTrue(kindDokumente.isDokumentNeeded(DokumentTyp.FACHSTELLENBEST_SOZ, kind));

		final DokumentGrund dokumentGrund = getDokumentGrund(kind);
		Assert.assertEquals(DokumentTyp.FACHSTELLENBEST_SOZ, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void kindDokumentFachstelleBehTest() {

		clearKinder(testgesuch);
		final String kindName = "Jan";
		Kind kind = createKind(testgesuch, kindName, Kinderabzug.GANZER_ABZUG, "beh", true);

		Assert.assertFalse(kindDokumente.isDokumentNeeded(DokumentTyp.SORGERECHTSVEREINBARUNG, kind));
		Assert.assertTrue(kindDokumente.isDokumentNeeded(DokumentTyp.FACHSTELLENBEST_BEH, kind));
		Assert.assertFalse(kindDokumente.isDokumentNeeded(DokumentTyp.FACHSTELLENBEST_SOZ, kind));

		final DokumentGrund dokumentGrund = getDokumentGrund(kind);

		Assert.assertEquals(DokumentTyp.FACHSTELLENBEST_BEH, dokumentGrund.getDokumentTyp());
	}

	private DokumentGrund getDokumentGrund(Kind kind) {
		final Set<DokumentGrund> calculate = evaluator.calculate(testgesuch);
		Assert.assertEquals(1, calculate.size());
		final DokumentGrund dokumentGrund = calculate.iterator().next();
		Assert.assertEquals(DokumentGrundTyp.KINDER, dokumentGrund.getDokumentGrundTyp());
		Assert.assertEquals(kind.getFullName(), dokumentGrund.getFullName());
		return dokumentGrund;
	}

	private DokumentGrund checkDokumentGrund(Erwerbspensum erwerbspensum) {
		final Set<DokumentGrund> calculate = evaluator.calculate(testgesuch);
		Assert.assertEquals(1, calculate.size());
		final DokumentGrund dokumentGrund = calculate.iterator().next();
		Assert.assertEquals(DokumentGrundTyp.ERWERBSPENSUM, dokumentGrund.getDokumentGrundTyp());
		Assert.assertEquals(testgesuch.getGesuchsteller1().extractFullName(), dokumentGrund.getFullName());
		Assert.assertEquals(erwerbspensum.getName(), dokumentGrund.getTag());
		return dokumentGrund;
	}

	@Test
	public void erwpDokuemntNeueintrittAfterTest() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, false, null);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_ERWERBSPENSUM, erwerbspensum));

		erwerbspensum.getGueltigkeit().setGueltigAb(LocalDate.of(2015, 9, 1));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_ERWERBSPENSUM, erwerbspensum, LocalDate.of(2015, 1, 1)));

		final Set<DokumentGrund> calculate = evaluator.calculate(testgesuch);
		final DokumentGrund dokumentGrund = calculate.iterator().next();

		Assert.assertEquals(DokumentTyp.NACHWEIS_ERWERBSPENSUM, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpDokuemntNeueintrittBeforeTest() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, false, null);


		erwerbspensum.getGueltigkeit().setGueltigAb(LocalDate.of(2000, 7, 1));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_ERWERBSPENSUM, erwerbspensum, LocalDate.of(2000, 8, 1)));

	}

	@Test
	public void erwpDokuemntSelbständigTest() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.SELBSTAENDIG, false, false, null);

		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_AUSBILDUNG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_RAV, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.BESTAETIGUNG_ARZT, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpDokuemntAusbildung() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.AUSBILDUNG, false, false, null);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_AUSBILDUNG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_RAV, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.BESTAETIGUNG_ARZT, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_AUSBILDUNG, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpDokuemntRAV() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.RAV, false, false, null);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_AUSBILDUNG, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_RAV, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.BESTAETIGUNG_ARZT, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_RAV, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpDokuemntArzt() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, true, false, null);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_AUSBILDUNG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_RAV, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.BESTAETIGUNG_ARZT, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.BESTAETIGUNG_ARZT, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpzUnregelm() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, true,
			Zuschlagsgrund.UNREGELMAESSIGE_ARBEITSZEITEN);

		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpzLangArbz() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, true,
			Zuschlagsgrund.LANGER_ARBWEITSWEG);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpzAndere() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, true,
			Zuschlagsgrund.ANDERE);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpzUEBERLAPPENDE_ARBEITSZEITEN() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, true,
			Zuschlagsgrund.UEBERLAPPENDE_ARBEITSZEITEN);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void erwpzFIXE_ARBEITSZEITEN() {
		final Erwerbspensum erwerbspensum = createErwerbspensum(testgesuch, "Hugo", Taetigkeit.ANGESTELLT, false, true,
			Zuschlagsgrund.FIXE_ARBEITSZEITEN);

		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensum));
		Assert.assertFalse(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensum));
		Assert.assertTrue(erwerbspensumDokumente.isDokumentNeeded(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensum));

		final DokumentGrund dokumentGrund = checkDokumentGrund(erwerbspensum);

		Assert.assertEquals(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, dokumentGrund.getDokumentTyp());
	}


	private Set<DokumentGrund> getDokumentGrundsForGS(GesuchstellerContainer gesuchsteller, Set<DokumentGrund> dokumentGrunds) {
		Set<DokumentGrund> grunds = new HashSet<>();

		for (DokumentGrund dokumentGrund : dokumentGrunds) {
			if (dokumentGrund.getFullName().equals(gesuchsteller.extractFullName())) {
				grunds.add(dokumentGrund);
			}
		}
		return grunds;
	}

	private Set<DokumentGrund> getDokumentGrundsForType(DokumentTyp dokumentTyp, Set<DokumentGrund> dokumentGrunds, String fullname, String year) {
		Set<DokumentGrund> grunds = new HashSet<>();

		for (DokumentGrund dokumentGrund : dokumentGrunds) {

			if (fullname != null) {
				if (year != null) {
					if (!fullname.equals(dokumentGrund.getFullName()) || !year.equals(dokumentGrund.getTag())) {
						continue;
					}
				} else {
					if (!fullname.equals(dokumentGrund.getFullName()) || !(dokumentGrund.getTag() == null)) {
						continue;
					}
				}
			} else {
				if (!(dokumentGrund.getFullName() == null) || !(dokumentGrund.getTag() == null)) {
					continue;
				}
			}


			if (dokumentGrund.getDokumentTyp().equals(dokumentTyp)) {
				grunds.add(dokumentGrund);
				break;
			}

		}
		return grunds;
	}

	@Test
	public void finSiSteuerveranlagungGemeinsam() {

		createFinanzielleSituationGS(1, testgesuch, "Sämi", true);
		createFinanzielleSituationGS(2, testgesuch, "Alex", true);

		createFamilienSituation(testgesuch, true);
		final Set<DokumentGrund> dokumentGrunds = evaluator.calculate(testgesuch);
		Assert.assertEquals(1, dokumentGrunds.size());

		final DokumentGrund dokumentGrund = dokumentGrunds.iterator().next();
		Assert.assertEquals(DokumentGrundTyp.FINANZIELLESITUATION, dokumentGrund.getDokumentGrundTyp());
		Assert.assertNull(dokumentGrund.getFullName());
		Assert.assertEquals(DokumentTyp.STEUERVERANLAGUNG, dokumentGrund.getDokumentTyp());
	}

	@Test
	public void finSiSteuerveranlagungNichtGemeinsam() {

		createFinanzielleSituationGS(1, testgesuch, "Sämi", true);
		createFinanzielleSituationGS(2, testgesuch, "Alex", true);

		createFamilienSituation(testgesuch, false);
		final Set<DokumentGrund> dokumentGrunds = evaluator.calculate(testgesuch);
		Assert.assertEquals(2, dokumentGrunds.size());

		final Set<DokumentGrund> dokumentGrundGS1 = getDokumentGrundsForGS(testgesuch.getGesuchsteller1(), dokumentGrunds);

		checkDokumentGrundGS(dokumentGrundGS1);

		final Set<DokumentGrund> dokumentGrundGS2 = getDokumentGrundsForGS(testgesuch.getGesuchsteller2(), dokumentGrunds);
		checkType(dokumentGrundGS2, DokumentTyp.STEUERVERANLAGUNG, testgesuch.getGesuchsteller2().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
	}

	private void checkDokumentGrundGS(Set<DokumentGrund> dokumentGrundGS1) {
		checkType(dokumentGrundGS1, DokumentTyp.STEUERVERANLAGUNG, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
	}

	@Test
	public void finSiNichtGemeinsam() {

		createFinanzielleSituationGS(1, testgesuch, "Sämi", false);
		createFinanzielleSituationGS(2, testgesuch, "Alex", false);

		createFamilienSituation(testgesuch, false);
		final Set<DokumentGrund> dokumentGrunds = evaluator.calculate(testgesuch);
		Assert.assertEquals(4, dokumentGrunds.size());

		final Set<DokumentGrund> dokumentGrundGS1 = getDokumentGrundsForGS(testgesuch.getGesuchsteller1(), dokumentGrunds);
		Assert.assertEquals(2, dokumentGrundGS1.size());

		checkType(dokumentGrundGS1, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.JAHRESLOHNAUSWEISE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);

		final Set<DokumentGrund> dokumentGrundGS2 = getDokumentGrundsForGS(testgesuch.getGesuchsteller2(), dokumentGrunds);
		Assert.assertEquals(2, dokumentGrundGS2.size());

		checkType(dokumentGrundGS2, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller2().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS2, DokumentTyp.JAHRESLOHNAUSWEISE, testgesuch.getGesuchsteller2().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
	}


	private void checkType(Set<DokumentGrund> dokumentGrundGS1, DokumentTyp dokumentTyp, String fullname, String year, DokumentGrundTyp dokumentGrundTyp) {
		final Set<DokumentGrund> dokumentGrundsForType = getDokumentGrundsForType(dokumentTyp, dokumentGrundGS1, fullname, year);
		Assert.assertEquals("No document with dokumentGrundTyp: " + dokumentGrundTyp + "; dokumentTyp: " + dokumentTyp + "; fullname: " + fullname + "; year: " + year,
			1, dokumentGrundsForType.size());
		final DokumentGrund dokumentGrund = dokumentGrundsForType.iterator().next();
		Assert.assertEquals(fullname, dokumentGrund.getFullName());
		Assert.assertEquals(dokumentTyp, dokumentGrund.getDokumentTyp());

	}


	@Test
	public void finSiDokumenteTest() {

		createFinanzielleSituationGS(1, testgesuch, "Sämi", false);

		final FinanzielleSituation finanzielleSituationJA = testgesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA();

		finanzielleSituationJA.setFamilienzulage(BigDecimal.valueOf(100000));
		finanzielleSituationJA.setErsatzeinkommen(BigDecimal.valueOf(100000));
		finanzielleSituationJA.setErhalteneAlimente(BigDecimal.valueOf(100000));
		finanzielleSituationJA.setGeleisteteAlimente(BigDecimal.valueOf(100000));
		finanzielleSituationJA.setBruttovermoegen(BigDecimal.valueOf(100000));
		finanzielleSituationJA.setSchulden(BigDecimal.valueOf(100000));
		finanzielleSituationJA.setGeschaeftsgewinnBasisjahr(BigDecimal.valueOf(100000));


		//Test wenn Steuererklärung ausgefüllt ist
		Set<DokumentGrund> dokumentGrunds = evaluator.calculate(testgesuch);
		Assert.assertEquals(7, dokumentGrunds.size());

		Set<DokumentGrund> dokumentGrundGS1 = getDokumentGrundsForGS(testgesuch.getGesuchsteller1(), dokumentGrunds);
		Assert.assertEquals(7, dokumentGrundGS1.size());

		checkType(dokumentGrundGS1, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.JAHRESLOHNAUSWEISE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_FAMILIENZULAGEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_ERSATZEINKOMMEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_ERHALTENE_ALIMENTE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_GELEISTETE_ALIMENTE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.ERFOLGSRECHNUNGEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);


		//Test wenn Steuererklärung nicht ausgefüllt ist
		finanzielleSituationJA.setSteuererklaerungAusgefuellt(false);
		dokumentGrunds = evaluator.calculate(testgesuch);
		Assert.assertEquals(8, dokumentGrunds.size());

		dokumentGrundGS1 = getDokumentGrundsForGS(testgesuch.getGesuchsteller1(), dokumentGrunds);
		Assert.assertEquals(8, dokumentGrundGS1.size());

		checkType(dokumentGrundGS1, DokumentTyp.JAHRESLOHNAUSWEISE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_FAMILIENZULAGEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_ERSATZEINKOMMEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_ERHALTENE_ALIMENTE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_GELEISTETE_ALIMENTE, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_VERMOEGEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_SCHULDEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
		checkType(dokumentGrundGS1, DokumentTyp.ERFOLGSRECHNUNGEN, testgesuch.getGesuchsteller1().extractFullName(), null, DokumentGrundTyp.FINANZIELLESITUATION);
	}

	@Test
	public void ekvDokumenteTest() {

		createEinkommensverschlechterungGS(1, testgesuch, "Sämi", false);
		createEinkommensverschlechterungGS(2, testgesuch, "Alex", false);
		createEinkommensverschlechterungInfo();
		final Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiode.setGueltigkeit(new DateRange(LocalDate.of(2016, 8, 1), LocalDate.of(2017, 8, 1)));

		testgesuch.setGesuchsperiode(gesuchsperiode);


		final Einkommensverschlechterung ekvJABasisJahrPlus1 = testgesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1();
		ekvJABasisJahrPlus1.setSteuererklaerungAusgefuellt(true);
		ekvJABasisJahrPlus1.setSteuerveranlagungErhalten(false);
		ekvJABasisJahrPlus1.setNettolohnJan(BigDecimal.valueOf(100000));

		final Einkommensverschlechterung ekvJABasisJahrPlus2 = testgesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2();
		ekvJABasisJahrPlus2.setSteuererklaerungAusgefuellt(true);
		ekvJABasisJahrPlus2.setSteuerveranlagungErhalten(false);
		ekvJABasisJahrPlus2.setNettolohnJan(BigDecimal.valueOf(200000));


		//Test wenn Steuererklärung ausgefüllt ist
		Set<DokumentGrund> dokumentGrunds = evaluator.calculate(testgesuch);
		Assert.assertEquals(8, dokumentGrunds.size());

		Set<DokumentGrund> dokumentGrundGS1 = getDokumentGrundsForGS(testgesuch.getGesuchsteller1(), dokumentGrunds);
		Assert.assertEquals(4, dokumentGrundGS1.size());

		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_EINKOMMENSSITUATION_MONAT, testgesuch.getGesuchsteller1().extractFullName(), "2016", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
		checkType(dokumentGrundGS1, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller1().extractFullName(), "2016", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);

		checkType(dokumentGrundGS1, DokumentTyp.NACHWEIS_EINKOMMENSSITUATION_MONAT, testgesuch.getGesuchsteller1().extractFullName(), "2017", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
		checkType(dokumentGrundGS1, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller1().extractFullName(), "2017", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);

		Set<DokumentGrund> dokumentGrundGS2 = getDokumentGrundsForGS(testgesuch.getGesuchsteller2(), dokumentGrunds);
		Assert.assertEquals(4, dokumentGrundGS2.size());

		checkType(dokumentGrundGS2, DokumentTyp.NACHWEIS_EINKOMMENSSITUATION_MONAT, testgesuch.getGesuchsteller2().extractFullName(), "2016", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
		checkType(dokumentGrundGS2, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller2().extractFullName(), "2016", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);

		checkType(dokumentGrundGS2, DokumentTyp.NACHWEIS_EINKOMMENSSITUATION_MONAT, testgesuch.getGesuchsteller2().extractFullName(), "2017", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
		checkType(dokumentGrundGS2, DokumentTyp.STEUERERKLAERUNG, testgesuch.getGesuchsteller2().extractFullName(), "2017", DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);

	}

	private void createEinkommensverschlechterungInfo() {
		final EinkommensverschlechterungInfoContainer einkommensverschlechterungsInfo = TestDataUtil.createDefaultEinkommensverschlechterungsInfoContainer(testgesuch);
		einkommensverschlechterungsInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus1(true);
		einkommensverschlechterungsInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus2(true);
	}


}
