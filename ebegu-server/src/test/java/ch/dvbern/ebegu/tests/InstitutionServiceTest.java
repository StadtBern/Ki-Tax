package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse InstitutionService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class InstitutionServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence<?> persistence;



	@Test
	public void createInstitution() {
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		Assert.assertEquals("Institution1", institutionOpt.get().getName());
		Assert.assertEquals(institutionOpt.get().getMandant().getId(), institution.getMandant().getId());
		Assert.assertEquals(institutionOpt.get().getTraegerschaft().getId(), institution.getTraegerschaft().getId());
	}

	@Test
	public void deleteInstitution(){
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		institutionService.deleteInstitution(institutionOpt.get().getId());
		Optional<Institution> institutionOpt2 = institutionService.findInstitution(institution.getId());
		Assert.assertFalse(institutionOpt2.isPresent());
	}

	// This test gives a really strange Error java.lang.NoSuchMethodError: ch.dvbern.ebegu.entities.Institution.setActive(Ljava/lang/Boolean;)V
	// but the method in the entity is definitely there!
	@Test
	public void inactiveInstitution(){
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		institutionService.setInstitutionInactive(institutionOpt.get().getId());
		Optional<Institution> institutionOpt2 = institutionService.findInstitution(institution.getId());
		Assert.assertFalse(institutionOpt2.get().getActive());
	}


	@Test
	public void getAllInstitutionenTest(){
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Collection<Institution> allInstitutionen = institutionService.getAllInstitutionen();
		Assert.assertFalse(allInstitutionen.isEmpty());

	}


	// HELP METHODS

	private Institution insertInstitution() {
		Institution institution = TestDataUtil.createDefaultInstitution();

		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		persistence.persist(traegerschaft);
		institution.setTraegerschaft(traegerschaft);

		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		institution.setMandant(mandant);

		institutionService.createInstitution(institution);
		return institution;
	}

}
