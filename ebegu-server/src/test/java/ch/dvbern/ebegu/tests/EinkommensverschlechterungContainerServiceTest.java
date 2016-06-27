package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.services.EinkommensverschlechterungContainerService;
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
public class EinkommensverschlechterungContainerServiceTest extends AbstractEbeguTest {

	@Inject
	private EinkommensverschlechterungContainerService einkommensverschlechterungContainerService;

	@Inject
	private Persistence<Gesuchsteller> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createEinkommensverschlechterungContainerTest() {
		Assert.assertNotNull(einkommensverschlechterungContainerService);

		EinkommensverschlechterungContainer container = getEinkommensverschlechterungContainer();

		einkommensverschlechterungContainerService.saveEinkommensverschlechterungContainer(container);

		Collection<EinkommensverschlechterungContainer> allEinkommensverschlechterungContainer = einkommensverschlechterungContainerService.getAllEinkommensverschlechterungContainer();
		Assert.assertEquals(1, allEinkommensverschlechterungContainer.size());
		EinkommensverschlechterungContainer einkommensverschlechterungContainer = allEinkommensverschlechterungContainer.iterator().next();
		Assert.assertEquals(0, einkommensverschlechterungContainer.getEkvGSBasisJahrPlus1().getNettolohnJan().compareTo(BigDecimal.ONE));
	}

	private EinkommensverschlechterungContainer getEinkommensverschlechterungContainer() {
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		gesuchsteller = persistence.persist(gesuchsteller);

		final Einkommensverschlechterung einkommensverschlechterung = TestDataUtil.createDefaultEinkommensverschlechterung();

		EinkommensverschlechterungContainer container = TestDataUtil.createDefaultEinkommensverschlechterungsContainer();
		container.setGesuchsteller(gesuchsteller);
		return container;
	}

	/**
	 * 1. Create a container
	 * 2. Store created Container on DB
	 * 3. get Stored Container from DB by calling getAll
	 * 4. Change Stored Container
	 * 5. Update Container on DB
	 * 6. get Stored Container from DB by calling find
	 * <p>
	 * Expected result: new result must be the updated value
	 */
	@Test
	public void updateAndFindEinkommensverschlechterungContainerTest() {
		Assert.assertNotNull(einkommensverschlechterungContainerService);

		EinkommensverschlechterungContainer container = getEinkommensverschlechterungContainer();

		einkommensverschlechterungContainerService.saveEinkommensverschlechterungContainer(container);

		Collection<EinkommensverschlechterungContainer> allEinkommensverschlechterungContainer = einkommensverschlechterungContainerService.getAllEinkommensverschlechterungContainer();
		EinkommensverschlechterungContainer einkommensverschlechterungContainer = allEinkommensverschlechterungContainer.iterator().next();

		einkommensverschlechterungContainer.getEkvGSBasisJahrPlus1().setNettolohnJan(BigDecimal.TEN);

		einkommensverschlechterungContainerService.saveEinkommensverschlechterungContainer(einkommensverschlechterungContainer);

		final Optional<EinkommensverschlechterungContainer> einkommensverschlechterungContainerUpdated = einkommensverschlechterungContainerService.findEinkommensverschlechterungContainer(einkommensverschlechterungContainer.getId());

		final EinkommensverschlechterungContainer container1 = einkommensverschlechterungContainerUpdated.get();
		Assert.assertNotNull(container1);
		Assert.assertEquals(0, container1.getEkvGSBasisJahrPlus1().getNettolohnJan().compareTo(BigDecimal.TEN));
	}

	@Test
	public void removeEinkommensverschlechterungContainerTest() {
		Assert.assertNotNull(einkommensverschlechterungContainerService);
		Assert.assertEquals(0, einkommensverschlechterungContainerService.getAllEinkommensverschlechterungContainer().size());

		EinkommensverschlechterungContainer container = getEinkommensverschlechterungContainer();
		einkommensverschlechterungContainerService.saveEinkommensverschlechterungContainer(container);
		Assert.assertEquals(1, einkommensverschlechterungContainerService.getAllEinkommensverschlechterungContainer().size());

		einkommensverschlechterungContainerService.removeEinkommensverschlechterungContainer(container);
		Assert.assertEquals(0, einkommensverschlechterungContainerService.getAllEinkommensverschlechterungContainer().size());

	}


}
