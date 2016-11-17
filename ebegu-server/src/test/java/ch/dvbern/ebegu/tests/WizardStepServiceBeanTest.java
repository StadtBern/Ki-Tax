package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.List;

/**
 * Test fuer WizardStep Service
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class WizardStepServiceBeanTest extends AbstractEbeguTest {


	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private InstitutionService instService;

	private Gesuch gesuch;
	private WizardStep betreuungStep;
	private WizardStep kinderStep;
	private WizardStep erwerbStep;
	private WizardStep familienStep;
	private WizardStep gesuchstellerStep;
	private WizardStep finanSitStep;
	private WizardStep einkVerStep;
	private WizardStep dokStep;
	private WizardStep verfStep;


	@Before
	public void setUp() {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.GESUCH_ERSTELLEN, WizardStepStatus.OK));
		familienStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.UNBESUCHT));
		gesuchstellerStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.GESUCHSTELLER, WizardStepStatus.UNBESUCHT));
		kinderStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.KINDER, WizardStepStatus.UNBESUCHT));
		betreuungStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.BETREUUNG, WizardStepStatus.UNBESUCHT));
		erwerbStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.ERWERBSPENSUM, WizardStepStatus.UNBESUCHT));
		finanSitStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.UNBESUCHT));
		einkVerStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.UNBESUCHT));
		dokStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.DOKUMENTE, WizardStepStatus.UNBESUCHT));
		verfStep = wizardStepService.saveWizardStep(TestDataUtil.createWizardStepObject(gesuch, WizardStepName.VERFUEGEN, WizardStepStatus.UNBESUCHT));
	}

	@Test
	public void createWizardStepListForGesuchTest() {
		final Gesuch myGesuch = TestDataUtil.createAndPersistGesuch(persistence);
		final List<WizardStep> wizardStepList = wizardStepService.createWizardStepList(myGesuch);
		Assert.assertNotNull(wizardStepList);
		Assert.assertEquals(12, wizardStepList.size());

		wizardStepList.forEach(wizardStep -> {
			if (WizardStepName.GESUCH_ERSTELLEN.equals(wizardStep.getWizardStepName())) {
				Assert.assertTrue(wizardStep.getVerfuegbar());
				Assert.assertEquals(WizardStepStatus.OK, wizardStep.getWizardStepStatus());
			} else {
				Assert.assertFalse(wizardStep.getVerfuegbar());
				Assert.assertEquals(WizardStepStatus.UNBESUCHT, wizardStep.getWizardStepStatus());
			}
		});
	}

	@Test
	public void createWizardStepListForMutationTest() {
		final Gesuch mutation = TestDataUtil.createAndPersistGesuch(persistence);
		mutation.setTyp(AntragTyp.MUTATION);
		final Mutationsdaten mutationsdaten = new Mutationsdaten();
		mutationsdaten.setMutationEinkommensverschlechterung(true);
		mutationsdaten.setMutationErwerbspensum(true);
		mutation.setMutationsdaten(mutationsdaten);

		final List<WizardStep> wizardStepList = wizardStepService.createWizardStepList(mutation);

		Assert.assertNotNull(wizardStepList);
		Assert.assertEquals(12, wizardStepList.size());

		wizardStepList.forEach(wizardStep -> {
			// status
			if (WizardStepName.VERFUEGEN.equals(wizardStep.getWizardStepName())) {
				Assert.assertEquals(WizardStepStatus.WARTEN, wizardStep.getWizardStepStatus());
			} else {
				Assert.assertEquals(WizardStepStatus.OK, wizardStep.getWizardStepStatus());
			}

			//verfuegbarkeit
			if (WizardStepName.EINKOMMENSVERSCHLECHTERUNG.equals(wizardStep.getWizardStepName())
				|| WizardStepName.ERWERBSPENSUM.equals(wizardStep.getWizardStepName())
				|| WizardStepName.GESUCH_ERSTELLEN.equals(wizardStep.getWizardStepName())
				|| WizardStepName.DOKUMENTE.equals(wizardStep.getWizardStepName())
				|| WizardStepName.VERFUEGEN.equals(wizardStep.getWizardStepName())) {
				Assert.assertTrue(wizardStep.getVerfuegbar());
			} else {
				Assert.assertFalse(wizardStep.getVerfuegbar());
			}
		});
	}

	@Test
	public void updateWizardStepGesuchErstellen() {
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.GESUCH_ERSTELLEN);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.GESUCH_ERSTELLEN).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepFamiliensituation() {
		updateStatus(familienStep, WizardStepStatus.IN_BEARBEITUNG);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.FAMILIENSITUATION);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.FAMILIENSITUATION).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepFamiliensituationWhenItDoesntExistYet() {
		updateStatus(familienStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatus(gesuchstellerStep, WizardStepStatus.IN_BEARBEITUNG);

		//oldData ist eine leere Familiensituation
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(),
			new Familiensituation(), TestDataUtil.createDefaultFamiliensituation(), WizardStepName.FAMILIENSITUATION);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.FAMILIENSITUATION).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepFamiliensituationFromOneToTwoGS() {
		updateStatus(familienStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatus(gesuchstellerStep, WizardStepStatus.OK);
		updateStatus(finanSitStep, WizardStepStatus.OK);
		updateStatus(einkVerStep, WizardStepStatus.OK);

		Familiensituation oldFamiliensituation = new Familiensituation(gesuch.getFamiliensituation());
		gesuch.getFamiliensituation().setFamilienstatus(EnumFamilienstatus.VERHEIRATET);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), oldFamiliensituation,
			gesuch.getFamiliensituation(), WizardStepName.FAMILIENSITUATION);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.FAMILIENSITUATION).getWizardStepStatus());
		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.GESUCHSTELLER).getWizardStepStatus());
		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.FINANZIELLE_SITUATION).getWizardStepStatus());
		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepGesuchstellerNichtFuerUNBESUCHT() {
		updateStatus(gesuchstellerStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatusVerfuegbar(finanSitStep, WizardStepStatus.UNBESUCHT, false);
		updateStatusVerfuegbar(einkVerStep, WizardStepStatus.UNBESUCHT, false);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.GESUCHSTELLER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.GESUCH_ERSTELLEN).getWizardStepStatus());
		Assert.assertFalse(findStepByName(wizardSteps, WizardStepName.FINANZIELLE_SITUATION).getVerfuegbar());
		Assert.assertFalse(findStepByName(wizardSteps, WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getVerfuegbar());
	}

	@Test
	public void updateWizardStepGesuchsteller() {
		updateStatus(gesuchstellerStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatusVerfuegbar(finanSitStep, WizardStepStatus.IN_BEARBEITUNG, false);
		updateStatusVerfuegbar(einkVerStep, WizardStepStatus.IN_BEARBEITUNG, false);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.GESUCHSTELLER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.GESUCH_ERSTELLEN).getWizardStepStatus());
		Assert.assertTrue(findStepByName(wizardSteps, WizardStepName.FINANZIELLE_SITUATION).getVerfuegbar());
		Assert.assertTrue(findStepByName(wizardSteps, WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getVerfuegbar());
	}

	//TODO: TEST failed, aber warum? Keine Aenderungen in diesen Files....
	@Test
	public void updateWizardStepKinder() {
		updateStatus(kinderStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.KINDER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.KINDER).getWizardStepStatus());
		Assert.assertEquals(WizardStepStatus.PLATZBESTAETIGUNG, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepKinderNOKIfNoKinder() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatus(kinderStep, WizardStepStatus.IN_BEARBEITUNG);

		final Iterator<KindContainer> kinderIterator = gesuch.getKindContainers().iterator();
		KindContainer kind = kinderIterator.next();
		persistence.remove(KindContainer.class, kind.getId());

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.KINDER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.KINDER).getWizardStepStatus());
		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepKinderNOKIfKindNoBedarf() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);
		updateStatus(kinderStep, WizardStepStatus.IN_BEARBEITUNG);

		final Iterator<KindContainer> kinderIterator = gesuch.getKindContainers().iterator();
		KindContainer kind = kinderIterator.next();
		kind.getKindJA().setFamilienErgaenzendeBetreuung(false);
		persistence.merge(kind);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.KINDER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.KINDER).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepBetreuungUnbesucht() {
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.BETREUUNG);
		Assert.assertEquals(10, wizardSteps.size());
		// shouldn't update it because the current status is unbesucht
		Assert.assertEquals(WizardStepStatus.UNBESUCHT, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepBetreuungNOKIfBetreuungAbgewiesen() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);

		final Iterator<Betreuung> betreuungIterator = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator();
		Betreuung betreuung = betreuungIterator.next();
		betreuung = persistence.find(Betreuung.class, betreuung.getId());
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung("Abgelehnt");
		persistence.merge(betreuung);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.BETREUUNG);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepBetreuungPlatzBestaetigung() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);

		final Iterator<Betreuung> betreuungIterator = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator();
		Betreuung betreuung = betreuungIterator.next();
		betreuung = persistence.find(Betreuung.class, betreuung.getId());
		betreuung.setBetreuungsstatus(Betreuungsstatus.WARTEN);
		persistence.merge(betreuung);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.BETREUUNG);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.PLATZBESTAETIGUNG, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepBetreuungNOKIfNoBetreuung() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);

		final Iterator<Betreuung> betreuungIterator = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator();
		Betreuung betreuung1 = betreuungIterator.next();
		persistence.remove(Betreuung.class, betreuung1.getId());
		Betreuung betreuung2 = betreuungIterator.next();
		persistence.remove(Betreuung.class, betreuung2.getId());

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.BETREUUNG);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepBetreuungOK() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.BETREUUNG);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.PLATZBESTAETIGUNG, findStepByName(wizardSteps, WizardStepName.BETREUUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepErwerbspensumOKNotRequired() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);

		final Iterator<Betreuung> betreuungIterator = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator();
		InstitutionStammdaten institutionStammdaten1 = betreuungIterator.next().getInstitutionStammdaten();
		institutionStammdaten1 = persistence.find(InstitutionStammdaten.class, institutionStammdaten1.getId());
		institutionStammdaten1.setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);
		persistence.merge(institutionStammdaten1);

		InstitutionStammdaten institutionStammdaten2 = betreuungIterator.next().getInstitutionStammdaten();
		institutionStammdaten2 = persistence.find(InstitutionStammdaten.class, institutionStammdaten2.getId());
		institutionStammdaten2.setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);
		persistence.merge(institutionStammdaten2);

		ErwerbspensumContainer erwerbspensumContainer = gesuch.getGesuchsteller1().getErwerbspensenContainers().iterator().next();
		persistence.remove(ErwerbspensumContainer.class, erwerbspensumContainer.getId());

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.ERWERBSPENSUM);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.ERWERBSPENSUM).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepErwerbspensumNOKWhenRequired() {
		updateStatus(betreuungStep, WizardStepStatus.IN_BEARBEITUNG);

		ErwerbspensumContainer erwerbspensumContainer = gesuch.getGesuchsteller1().getErwerbspensenContainers().iterator().next();
		persistence.remove(ErwerbspensumContainer.class, erwerbspensumContainer.getId());

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.ERWERBSPENSUM);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.ERWERBSPENSUM).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepErwerbspensum() {
		updateStatus(erwerbStep, WizardStepStatus.IN_BEARBEITUNG);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.ERWERBSPENSUM);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.ERWERBSPENSUM).getWizardStepStatus());
	}

	/**
	 * Nicht sinnvoller Test, da der Status der Finanzieller Situation direkt im Web gesetzt und gespeichert wird.
	 * Daher bringt es nicht zu testen, was nie gebraucht wird
	 */
	@Test
	@Ignore
	public void updateWizardStepFinanzielleSituation() {
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.FINANZIELLE_SITUATION);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.FINANZIELLE_SITUATION).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepEinkommensverschlechterungTrueToFalse() {
		updateWizardStepEinkommensverschlechterung(true);
	}

	@Test
	public void updateWizardStepEinkommensverschlechterungFalseToFalse() {
		updateWizardStepEinkommensverschlechterung(false);
	}

	private void updateWizardStepEinkommensverschlechterung(final boolean oldValue) {
		updateStatus(einkVerStep, WizardStepStatus.IN_BEARBEITUNG);
		EinkommensverschlechterungInfo oldData = new EinkommensverschlechterungInfo();
		oldData.setEinkommensverschlechterung(oldValue);
		EinkommensverschlechterungInfo newData = new EinkommensverschlechterungInfo();
		newData.setEinkommensverschlechterung(false);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), oldData,
			newData, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepEinkommensverschlechterungNOK() {
		updateStatus(einkVerStep, WizardStepStatus.IN_BEARBEITUNG);
		EinkommensverschlechterungInfo oldData = new EinkommensverschlechterungInfo();
		oldData.setEinkommensverschlechterung(false);
		EinkommensverschlechterungInfo newData = new EinkommensverschlechterungInfo();
		newData.setEinkommensverschlechterung(true);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), oldData,
			newData, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
		Assert.assertEquals(10, wizardSteps.size());

		//status is NOK weil die Daten noch nicht eingetragen sind
		Assert.assertEquals(WizardStepStatus.NOK, findStepByName(wizardSteps, WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepDokumente() {
		updateStatus(dokStep, WizardStepStatus.IN_BEARBEITUNG);

		createAndPersistDokumentGrundWithDokument(DokumentGrundTyp.ERWERBSPENSUM, DokumentTyp.NACHWEIS_LANG_ARBEITSWEG);
		createAndPersistDokumentGrundWithDokument(DokumentGrundTyp.FINANZIELLESITUATION, DokumentTyp.STEUERVERANLAGUNG);
		createAndPersistDokumentGrundWithDokument(DokumentGrundTyp.ERWERBSPENSUM, DokumentTyp.NACHWEIS_ERWERBSPENSUM);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.DOKUMENTE);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.DOKUMENTE).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepDokumenteIN_BEARBEITUNG() {
		updateStatus(dokStep, WizardStepStatus.IN_BEARBEITUNG);

		//nicht alle notwendige dokumente
		createAndPersistDokumentGrundWithDokument(DokumentGrundTyp.ERWERBSPENSUM, DokumentTyp.NACHWEIS_LANG_ARBEITSWEG);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.DOKUMENTE);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.IN_BEARBEITUNG, findStepByName(wizardSteps, WizardStepName.DOKUMENTE).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepVerfuegenWARTEN() {
		TestDataUtil.createAndPersistBenutzer(persistence);
		updateStatus(verfStep, WizardStepStatus.WARTEN);
		Iterator<Betreuung> iterator = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator();
		Betreuung betreuung1 = iterator.next();
		betreuung1.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
		persistence.merge(betreuung1);
		Betreuung betreuung2 = iterator.next();
		betreuung2.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
		persistence.merge(betreuung2);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.VERFUEGEN);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.OK, findStepByName(wizardSteps, WizardStepName.VERFUEGEN).getWizardStepStatus());
		final Gesuch persistedGesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertEquals(AntragStatus.VERFUEGT, persistedGesuch.getStatus());
	}

	@Test
	public void updateWizardStepFamiliensitMutiert() {
		updateStatusMutiert(familienStep, WizardStepStatus.OK);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), gesuch.getFamiliensituation(),
			gesuch.getFamiliensituation(), WizardStepName.FAMILIENSITUATION);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.FAMILIENSITUATION).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepGesuchstellerMutiert() {
		updateStatusMutiert(gesuchstellerStep, WizardStepStatus.OK);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null,
			null, WizardStepName.GESUCHSTELLER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.GESUCHSTELLER).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepKinderMutiert() {
		updateStatusMutiert(kinderStep, WizardStepStatus.OK);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null,
			null, WizardStepName.KINDER);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.KINDER).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepErwerbspensumMutiert() {
		updateStatusMutiert(erwerbStep, WizardStepStatus.OK);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null,
			null, WizardStepName.ERWERBSPENSUM);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.ERWERBSPENSUM).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepFinsintMutiert() {
		updateStatusMutiert(finanSitStep, WizardStepStatus.OK);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null,
			null, WizardStepName.FINANZIELLE_SITUATION);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.FINANZIELLE_SITUATION).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepEkvMutiert() {
		updateStatusMutiert(einkVerStep, WizardStepStatus.OK);

		EinkommensverschlechterungInfo newData = new EinkommensverschlechterungInfo();
		newData.setEinkommensverschlechterung(true);

		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(),
			null, newData, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getWizardStepStatus());
	}

	@Test
	public void updateWizardStepDokumenteMutiert() {
		updateStatusMutiert(dokStep, WizardStepStatus.OK);
		final List<WizardStep> wizardSteps = wizardStepService.updateSteps(gesuch.getId(), null,
			null, WizardStepName.DOKUMENTE);
		Assert.assertEquals(10, wizardSteps.size());

		Assert.assertEquals(WizardStepStatus.MUTIERT, findStepByName(wizardSteps, WizardStepName.DOKUMENTE).getWizardStepStatus());
	}
	// HELP METHODS

	private void createAndPersistDokumentGrundWithDokument(DokumentGrundTyp dokGrundTyp, DokumentTyp dokTyp) {
		DokumentGrund dokGrund = new DokumentGrund();
		dokGrund.setDokumentGrundTyp(dokGrundTyp);
		dokGrund.setDokumentTyp(dokTyp);
		dokGrund.setNeeded(true);
		dokGrund.setGesuch(gesuch);
		dokGrund.setFullName("name");
		persistence.persist(dokGrund);
		Dokument dok1 = new Dokument();
		dok1.setFilename("name");
		dok1.setFilepfad("pfad");
		dok1.setFilesize("23");
		dok1.setDokumentGrund(dokGrund);
		persistence.persist(dok1);
	}

	@Nullable
	private WizardStep findStepByName(List<WizardStep> wizardSteps, WizardStepName stepName) {
		for (WizardStep wizardStep : wizardSteps) {
			if (wizardStep.getWizardStepName().equals(stepName)) {
				return wizardStep;
			}
		}
		return null;
	}

	private void updateStatus(WizardStep step, WizardStepStatus status) {
		step.setWizardStepStatus(status);
		wizardStepService.saveWizardStep(step);
	}

	private void updateStatusMutiert(WizardStep step, WizardStepStatus status) {
		step.getGesuch().setTyp(AntragTyp.MUTATION);
		persistence.merge(step.getGesuch());
		step.setWizardStepStatus(status);
		wizardStepService.saveWizardStep(step);
	}

	private void updateStatusVerfuegbar(WizardStep step, WizardStepStatus status, boolean verfuegbar) {
		step.setVerfuegbar(verfuegbar);
		step.setWizardStepStatus(status);
		wizardStepService.saveWizardStep(step);
	}

}
