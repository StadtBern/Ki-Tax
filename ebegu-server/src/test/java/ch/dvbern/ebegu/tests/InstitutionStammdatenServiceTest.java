package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
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
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse PersonService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class InstitutionStammdatenServiceTest extends AbstractEbeguTest {

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private Persistence<InstitutionStammdaten> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void createPerson() {
		Assert.assertNotNull(institutionStammdatenService);
		InstitutionStammdaten insertedInstitutionStammdaten = getInstitutionStammdaten();

		Collection<InstitutionStammdaten> allInstitutionStammdaten = institutionStammdatenService.getAllInstitutionStammdaten();
		Assert.assertEquals(1, allInstitutionStammdaten.size());
		InstitutionStammdaten nextInstitutionStammdaten = allInstitutionStammdaten.iterator().next();
		Assert.assertEquals(insertedInstitutionStammdaten.getIban(), nextInstitutionStammdaten.getIban());
		Assert.assertEquals(insertedInstitutionStammdaten.getBetreuungsangebotTyp(), nextInstitutionStammdaten.getBetreuungsangebotTyp());
	}

	@Test
	public void updateInstitutionStammdatenTest() {
		Assert.assertNotNull(institutionStammdatenService);
		InstitutionStammdaten insertedInstitutionStammdaten = getInstitutionStammdaten();

		Optional<InstitutionStammdaten> institutionStammdatenOptional = institutionStammdatenService.findInstitutionStammdaten(insertedInstitutionStammdaten.getId());
		Assert.assertTrue(institutionStammdatenOptional.isPresent());
		InstitutionStammdaten persistedInstStammdaten= institutionStammdatenOptional.get();
		Assert.assertEquals(insertedInstitutionStammdaten.getIban(), persistedInstStammdaten.getIban());

		persistedInstStammdaten.setIban("CH987654321");
		InstitutionStammdaten updatedInstitutionStammdaten = institutionStammdatenService.saveInstitutionStammdaten(persistedInstStammdaten);
		Assert.assertEquals(persistedInstStammdaten.getIban(), updatedInstitutionStammdaten.getIban());
	}

	// HELP METHODS

	private InstitutionStammdaten getInstitutionStammdaten() {
		InstitutionStammdaten institutionStammdaten = TestDataUtil.createDefaultInstitutionStammdaten();
		persistence.persist(institutionStammdaten.getInstitution().getMandant());
		persistence.persist(institutionStammdaten.getInstitution().getTraegerschaft());
		persistence.persist(institutionStammdaten.getInstitution());
		return institutionStammdatenService.saveInstitutionStammdaten(institutionStammdaten);
	}

}
