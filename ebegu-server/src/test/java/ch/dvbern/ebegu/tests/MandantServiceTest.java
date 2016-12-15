package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.services.MandantService;
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
 * Tests fuer die Klasse MandantService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class MandantServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private MandantService mandantService;

	@Inject
	private Persistence<Mandant> persistence;




	@Test
	public void findMandantTest() {
		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		Optional<Mandant> mandantOpt = mandantService.findMandant(mandant.getId());
		Assert.assertTrue(mandantOpt.isPresent());
		Assert.assertEquals("Mandant1", mandantOpt.get().getName());
	}

	@Test
	public void firstMandantTest() {
		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		Mandant loadedMandant = mandantService.getFirst();
		Assert.assertNotNull(loadedMandant);
		Assert.assertEquals("Mandant1", loadedMandant.getName());
	}
}
