package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.services.FinanzielleSituationService;
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
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FinanzielleSituationServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private Persistence<FinanzielleSituation> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createFinanzielleSituation() {
		Assert.assertNotNull(finanzielleSituationService);

		FinanzielleSituation finanzielleSituation = TestDataUtil.createDefaultFinanzielleSituation();
		Person gesuchsteller = TestDataUtil.createDefaultPerson();
		gesuchsteller = persistence.persist(gesuchsteller);

		FinanzielleSituationContainer container = TestDataUtil.createFinanzielleSituationContainer();
		container.setFinanzielleSituationGS(finanzielleSituation);
		container.setGesuchsteller(gesuchsteller);

		finanzielleSituationService.saveFinanzielleSituation(container);
		Collection<FinanzielleSituationContainer> allFinanzielleSituationen = finanzielleSituationService.getAllFinanzielleSituationen();
		Assert.assertEquals(1, allFinanzielleSituationen.size());
		FinanzielleSituationContainer nextFinanzielleSituation = allFinanzielleSituationen.iterator().next();
		Assert.assertEquals(100000L, nextFinanzielleSituation.getFinanzielleSituationGS().getNettolohn().longValue());
	}

	@Test
	public void updateFinanzielleSituationTest() {
		Assert.assertNotNull(finanzielleSituationService);
		FinanzielleSituationContainer insertedFinanzielleSituations = insertNewEntity();
		Optional<FinanzielleSituationContainer> finanzielleSituationOptional = finanzielleSituationService.findFinanzielleSituation(insertedFinanzielleSituations.getId());
		Assert.assertTrue(finanzielleSituationOptional.isPresent());
		FinanzielleSituationContainer finanzielleSituation = finanzielleSituationOptional.get();
		finanzielleSituation.setFinanzielleSituationGS(TestDataUtil.createDefaultFinanzielleSituation());
		FinanzielleSituationContainer updatedCont = finanzielleSituationService.saveFinanzielleSituation(finanzielleSituation);
		Assert.assertEquals(100000L, updatedCont.getFinanzielleSituationGS().getNettolohn().longValue());

		updatedCont.getFinanzielleSituationGS().setNettolohn(new BigDecimal(200000));
		FinanzielleSituationContainer contUpdTwice = finanzielleSituationService.saveFinanzielleSituation(updatedCont);
		Assert.assertEquals(200000L, contUpdTwice.getFinanzielleSituationGS().getNettolohn().longValue());
	}

	@Test
	public void removeFinanzielleSituationTest() {
		Assert.assertNotNull(finanzielleSituationService);
		Assert.assertEquals(0, finanzielleSituationService.getAllFinanzielleSituationen().size());

		FinanzielleSituationContainer insertedFinanzielleSituations = insertNewEntity();
		Assert.assertEquals(1, finanzielleSituationService.getAllFinanzielleSituationen().size());

		finanzielleSituationService.removeFinanzielleSituation(insertedFinanzielleSituations);
		Assert.assertEquals(0, finanzielleSituationService.getAllFinanzielleSituationen().size());
	}

	private FinanzielleSituationContainer insertNewEntity() {
		Person gesuchsteller = TestDataUtil.createDefaultPerson();
		FinanzielleSituationContainer container = TestDataUtil.createFinanzielleSituationContainer();
		gesuchsteller.setFinanzielleSituationContainer(container);
		gesuchsteller = persistence.persist(gesuchsteller);
		return gesuchsteller.getFinanzielleSituationContainer();
	}
}
