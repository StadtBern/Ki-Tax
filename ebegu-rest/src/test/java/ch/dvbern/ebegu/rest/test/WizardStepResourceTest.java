package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxWizardStep;
import ch.dvbern.ebegu.api.resource.wizard.WizardStepResource;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

/**
 * Tests fuer WizardStepResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class WizardStepResourceTest extends AbstractEbeguRestTest {

	@Inject
	private WizardStepResource wizardStepResource;
	private Gesuch gesuch;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence<Gesuch> persistence;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void testCreateWizardStepList() throws EbeguException {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);

		final List<JaxWizardStep> wizardStepList = wizardStepResource.createWizardStepList(new JaxId(gesuch.getId()));

		Assert.assertEquals(10, wizardStepList.size());
		assertWizardStep(wizardStepList.get(0), WizardStepName.GESUCH_ERSTELLEN);
		assertWizardStep(wizardStepList.get(1), WizardStepName.FAMILIENSITUATION);
		assertWizardStep(wizardStepList.get(2), WizardStepName.GESUCHSTELLER);
		assertWizardStep(wizardStepList.get(3), WizardStepName.KINDER);
		assertWizardStep(wizardStepList.get(4), WizardStepName.BETREUUNG);
		assertWizardStep(wizardStepList.get(5), WizardStepName.ERWERBSPENSUM);
		assertWizardStep(wizardStepList.get(6), WizardStepName.FINANZIELLE_SITUATION);
		assertWizardStep(wizardStepList.get(7), WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
		assertWizardStep(wizardStepList.get(8), WizardStepName.DOKUMENTE);
		assertWizardStep(wizardStepList.get(9), WizardStepName.VERFUEGEN);
	}

	private void assertWizardStep(JaxWizardStep wizardStep, WizardStepName wizardStepName) {
		Assert.assertEquals(gesuch.getId(), wizardStep.getGesuchId());
		Assert.assertEquals(wizardStepName, wizardStep.getWizardStepName());
		Assert.assertEquals(WizardStepStatus.UNBESUCHT, wizardStep.getWizardStepStatus());
		Assert.assertNull(wizardStep.getBemerkungen());
	}
}
