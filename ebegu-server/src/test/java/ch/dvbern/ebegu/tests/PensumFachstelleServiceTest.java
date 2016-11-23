package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.services.PensumFachstelleService;
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
import java.util.Optional;

/**
 * Tests fuer die Klasse PensumFachstelle
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PensumFachstelleServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private PensumFachstelleService pensumFachstelleService;

	@Inject
	private Persistence<PensumFachstelle> persistence;



	@Test
	public void createPersonInstitutionStammdatenTest() {
		Assert.assertNotNull(pensumFachstelleService);
		PensumFachstelle insertedPensumFachstelle = insertInstitutionStammdaten();

		Optional<PensumFachstelle> returnedPensumFachstelle = pensumFachstelleService.findPensumFachstelle(insertedPensumFachstelle.getId());
		Assert.assertNotNull(returnedPensumFachstelle.get());
		Assert.assertNotNull(returnedPensumFachstelle.get().getId());
		Assert.assertNotNull(returnedPensumFachstelle.get().getTimestampErstellt());
		Assert.assertEquals(insertedPensumFachstelle.getFachstelle(), returnedPensumFachstelle.get().getFachstelle());
		Assert.assertEquals(insertedPensumFachstelle.getGueltigkeit(), returnedPensumFachstelle.get().getGueltigkeit());
		Assert.assertEquals(insertedPensumFachstelle.getPensum(), returnedPensumFachstelle.get().getPensum());
	}

	@Test
	public void updateInstitutionStammdatenTest() {
		Assert.assertNotNull(pensumFachstelleService);
		PensumFachstelle insertedPensumFachstelle = insertInstitutionStammdaten();

		Optional<PensumFachstelle> returnedPensumFachstelle = pensumFachstelleService.findPensumFachstelle(insertedPensumFachstelle.getId());
		Assert.assertTrue(returnedPensumFachstelle.isPresent());
		PensumFachstelle persistedPensFachstelle= returnedPensumFachstelle.get();
		Assert.assertEquals(insertedPensumFachstelle.getPensum(), persistedPensFachstelle.getPensum());

		insertedPensumFachstelle.setPensum(10);
		PensumFachstelle updatedPensumFachstelle = pensumFachstelleService.savePensumFachstelle(insertedPensumFachstelle);
		Assert.assertNotEquals(insertedPensumFachstelle.getPensum(), persistedPensFachstelle.getPensum());
		Assert.assertEquals(insertedPensumFachstelle.getId(), persistedPensFachstelle.getId());
		Assert.assertEquals(insertedPensumFachstelle.getPensum(), updatedPensumFachstelle.getPensum());
	}

	// HELP METHODS

	private PensumFachstelle insertInstitutionStammdaten() {
		PensumFachstelle pensumFachstelle = TestDataUtil.createDefaultPensumFachstelle();
		persistence.persist(pensumFachstelle.getFachstelle());
		return pensumFachstelleService.savePensumFachstelle(pensumFachstelle);
	}

}

