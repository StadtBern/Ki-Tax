package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
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
import java.util.Optional;

/**
 * Tests fuer die Klasse InstitutionService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class InstitutionServiceTest extends AbstractEbeguTest {

	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence<?> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createInstitution() {
		Assert.assertNotNull(institutionService);
		Institution institution = TestDataUtil.createDefaultInstitution();

		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		persistence.persist(traegerschaft);
		institution.setTraegerschaft(traegerschaft);

		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		institution.setMandant(mandant);

		institutionService.createInstitution(institution);

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		Assert.assertEquals("Institution1", institutionOpt.get().getName());
		Assert.assertEquals(institutionOpt.get().getMandant().getId(), institution.getMandant().getId());
		Assert.assertEquals(institutionOpt.get().getTraegerschaft().getId(), institution.getTraegerschaft().getId());
	}

}
