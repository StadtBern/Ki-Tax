package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.services.DatabaseMigrationService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse DatabaseMigrationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class DatabaseMigrationServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private DatabaseMigrationService dbMigrationService;
	@Inject
	private InstitutionService instService;
	@Inject
	private WizardStepService wizardStepService;

	private Gesuch gesuch;
	private LocalDateTime timestampMutiert;


	@Test
	public void testScript1204EKVInfoExists_NoEkvToAdd() throws ExecutionException, InterruptedException {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		createWizardStepEKV();
		final EinkommensverschlechterungInfoContainer ekvInfo = TestDataUtil.createDefaultEinkommensverschlechterungsInfoContainer(gesuch);
		ekvInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus1(false);
		gesuch = persistence.merge(gesuch);
		timestampMutiert = gesuch.getTimestampMutiert();

		final Future<Boolean> future = dbMigrationService.processScript("1204");
		future.get();

		final Gesuch updatedGesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertNotNull(updatedGesuch.getGesuchsteller1());
		Assert.assertNull(updatedGesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(updatedGesuch.getGesuchsteller2());

		checkTimestampMutiertHasntChange(updatedGesuch);
	}

	@Test
	public void testScript1204_AllRequiredEkvExists() throws ExecutionException, InterruptedException {
		gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(instService, persistence, LocalDate.now());
		createWizardStepEKV();
		final EinkommensverschlechterungInfoContainer ekvInfo = TestDataUtil.createDefaultEinkommensverschlechterungsInfoContainer(gesuch);
		ekvInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus1(true);
		Assert.assertNotNull(gesuch.getGesuchsteller1());
		Assert.assertNotNull(gesuch.getGesuchsteller2());
		gesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(TestDataUtil.createDefaultEinkommensverschlechterungsContainer());
		gesuch.getGesuchsteller2().setEinkommensverschlechterungContainer(TestDataUtil.createDefaultEinkommensverschlechterungsContainer());
		ekvInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus2(true);
		gesuch = persistence.merge(gesuch);
		timestampMutiert = gesuch.getTimestampMutiert();

		// asserts previous values
		Assert.assertNotNull(gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNotNull(gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer());

		final Future<Boolean> future = dbMigrationService.processScript("1204");
		future.get();

		final Gesuch updatedGesuch = persistence.find(Gesuch.class, gesuch.getId());

		//both EKVContainer must still exist for GS1 though the script didn't created them
		checkEKVContainerForGesuchsteller(updatedGesuch.getGesuchsteller1(), false);
		//both EKVContainer must still exist for GS2 though the script didn't created them
		checkEKVContainerForGesuchsteller(updatedGesuch.getGesuchsteller2(), false);

		checkTimestampMutiertHasntChange(updatedGesuch);
	}

	@Test
	public void testScript1204EKVInfoExists_AddingEkv() throws ExecutionException, InterruptedException {
		gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(instService, persistence, LocalDate.now());
		createWizardStepEKV();
		final EinkommensverschlechterungInfoContainer ekvInfo = TestDataUtil.createDefaultEinkommensverschlechterungsInfoContainer(gesuch);
		ekvInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus1(true);
		ekvInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus2(true);
		gesuch = persistence.merge(gesuch);
		timestampMutiert = gesuch.getTimestampMutiert();

		// asserts previous values
		Assert.assertNotNull(gesuch.getGesuchsteller1());
		Assert.assertNull(gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNotNull(gesuch.getGesuchsteller2());
		Assert.assertNull(gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer());

		final Future<Boolean> future = dbMigrationService.processScript("1204");
		future.get();

		final Gesuch updatedGesuch = persistence.find(Gesuch.class, gesuch.getId());

		//both EKVContainer must exist for GS1
		checkEKVContainerForGesuchsteller(updatedGesuch.getGesuchsteller1(), true);
		//both EKVContainer must exist for GS2
		checkEKVContainerForGesuchsteller(updatedGesuch.getGesuchsteller2(), true);

		checkTimestampMutiertHasntChange(updatedGesuch);
	}

	@Test
	public void testScript1204EKVInfoDoesntExist_SetItFalse() throws ExecutionException, InterruptedException {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		createWizardStepEKV();
		gesuch.setEinkommensverschlechterungInfoContainer(null);
		gesuch = persistence.merge(gesuch);
		timestampMutiert = gesuch.getTimestampMutiert();
		//assert previous ekvInfo is null
		Assert.assertNull(gesuch.getEinkommensverschlechterungInfoContainer());


		final Future<Boolean> future = dbMigrationService.processScript("1204");
		future.get();

		final Gesuch updatedGesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertNotNull(updatedGesuch.getEinkommensverschlechterungInfoContainer());
		Assert.assertNull(updatedGesuch.getEinkommensverschlechterungInfoContainer().getEinkommensverschlechterungInfoGS());
		Assert.assertNotNull(updatedGesuch.getEinkommensverschlechterungInfoContainer().getEinkommensverschlechterungInfoJA());
		Assert.assertFalse(updatedGesuch.getEinkommensverschlechterungInfoContainer()
			.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung());
		Assert.assertNotNull(updatedGesuch.getGesuchsteller1());
		Assert.assertNull(updatedGesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(updatedGesuch.getGesuchsteller2());

		checkTimestampMutiertHasntChange(updatedGesuch);
	}

	@Test
	public void testScript1204_Status_Unbesucht() throws ExecutionException, InterruptedException {
		// in this case it shouldn't do anything because it is not needed. The user will do it when needed
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		wizardStepService.createWizardStepList(gesuch); // step is created with UNBESUCHT
		gesuch.setEinkommensverschlechterungInfoContainer(null);
		gesuch = persistence.merge(gesuch);
		timestampMutiert = gesuch.getTimestampMutiert();
		//assert previous ekvInfo is null
		Assert.assertNull(gesuch.getEinkommensverschlechterungInfoContainer());

		final Future<Boolean> future = dbMigrationService.processScript("1204");
		future.get();

		// it must still be null
		Assert.assertNull(gesuch.getEinkommensverschlechterungInfoContainer());
	}



	private void checkEKVContainerForGesuchsteller(GesuchstellerContainer gesuchsteller, boolean checkFields) {
		Assert.assertNotNull(gesuchsteller);
		Assert.assertNotNull(gesuchsteller.getEinkommensverschlechterungContainer());
		Assert.assertNotNull(gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1());
		if (checkFields) {
			Assert.assertFalse(gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1().getSteuererklaerungAusgefuellt());
			Assert.assertFalse(gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1().getSteuerveranlagungErhalten());
			Assert.assertFalse(gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2().getSteuererklaerungAusgefuellt());
			Assert.assertFalse(gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2().getSteuerveranlagungErhalten());
		}
		Assert.assertNotNull(gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2());
	}

	/**
	 * Executing this script cannot change the timestampMutiert of the Gesuch, because the user is not aware of the
	 * execution of this script and for this reason shouldn't see any change in the content.
	 */
	private void checkTimestampMutiertHasntChange(Gesuch gesuch) {
		Assert.assertEquals(timestampMutiert, gesuch.getTimestampMutiert());
	}

	private void createWizardStepEKV() {
		wizardStepService.createWizardStepList(gesuch); // create all Steps
		wizardStepService.setWizardStepOkay(gesuch.getId(), WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
	}
}
