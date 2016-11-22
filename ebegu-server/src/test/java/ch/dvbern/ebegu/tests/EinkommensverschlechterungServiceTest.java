package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.services.EinkommensverschlechterungService;
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
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class EinkommensverschlechterungServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private EinkommensverschlechterungService einkommensverschlechterungService;

	@Inject
	private Persistence<Gesuchsteller> persistence;



	@Test
	public void createEinkommensverschlechterungContainerTest() {
		Assert.assertNotNull(einkommensverschlechterungService);

		EinkommensverschlechterungContainer container = getEinkommensverschlechterungContainer();

		einkommensverschlechterungService.saveEinkommensverschlechterungContainer(container);

		Collection<EinkommensverschlechterungContainer> allEinkommensverschlechterungContainer = einkommensverschlechterungService.getAllEinkommensverschlechterungContainer();
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
		Assert.assertNotNull(einkommensverschlechterungService);

		EinkommensverschlechterungContainer container = getEinkommensverschlechterungContainer();

		einkommensverschlechterungService.saveEinkommensverschlechterungContainer(container);

		Collection<EinkommensverschlechterungContainer> allEinkommensverschlechterungContainer = einkommensverschlechterungService.getAllEinkommensverschlechterungContainer();
		EinkommensverschlechterungContainer einkommensverschlechterungContainer = allEinkommensverschlechterungContainer.iterator().next();

		einkommensverschlechterungContainer.getEkvGSBasisJahrPlus1().setNettolohnJan(BigDecimal.TEN);

		einkommensverschlechterungService.saveEinkommensverschlechterungContainer(einkommensverschlechterungContainer);

		final Optional<EinkommensverschlechterungContainer> einkommensverschlechterungContainerUpdated = einkommensverschlechterungService.findEinkommensverschlechterungContainer(einkommensverschlechterungContainer.getId());

		final EinkommensverschlechterungContainer container1 = einkommensverschlechterungContainerUpdated.get();
		Assert.assertNotNull(container1);
		Assert.assertEquals(0, container1.getEkvGSBasisJahrPlus1().getNettolohnJan().compareTo(BigDecimal.TEN));
	}

	@Test
	public void removeEinkommensverschlechterungContainerTest() {
		Assert.assertNotNull(einkommensverschlechterungService);
		Assert.assertEquals(0, einkommensverschlechterungService.getAllEinkommensverschlechterungContainer().size());

		EinkommensverschlechterungContainer container = getEinkommensverschlechterungContainer();
		einkommensverschlechterungService.saveEinkommensverschlechterungContainer(container);
		Assert.assertEquals(1, einkommensverschlechterungService.getAllEinkommensverschlechterungContainer().size());

		einkommensverschlechterungService.removeEinkommensverschlechterungContainer(container);
		Assert.assertEquals(0, einkommensverschlechterungService.getAllEinkommensverschlechterungContainer().size());

	}


}
