package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.KindService;
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

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Tests fuer die Klasse KindService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class KindServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private KindService kindService;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private FallService fallService;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createAndUpdatekindTest() {
		Assert.assertNotNull(kindService);
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		KindContainer persitedKind = persistKind(gesuch);
		Optional<KindContainer> kind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(kind.isPresent());
		KindContainer savedKind = kind.get();
		Assert.assertEquals(persitedKind.getKindGS().getNachname(), savedKind.getKindGS().getNachname());
		Assert.assertEquals(persitedKind.getKindJA().getNachname(), savedKind.getKindJA().getNachname());

		Assert.assertNotEquals("Neuer Name", savedKind.getKindGS().getNachname());
		savedKind.getKindGS().setNachname("Neuer Name");
		kindService.saveKind(savedKind);
		Optional<KindContainer> updatedKind= kindService.findKind(persitedKind.getId());
		Assert.assertTrue(updatedKind.isPresent());
		Assert.assertEquals("Neuer Name", updatedKind.get().getKindGS().getNachname());
		Assert.assertEquals(new Integer(1), updatedKind.get().getNextNumberBetreuung());
		Assert.assertEquals(new Integer(1), updatedKind.get().getKindNummer());
		Assert.assertEquals(new Integer(2), fallService.findFall(gesuch.getFall().getId()).get().getNextNumberKind());
	}

	@Test
	public void removekindTest() {
		Assert.assertNotNull(kindService);
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		KindContainer persitedKind = persistKind(gesuch);
		Optional<KindContainer> kind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(kind.isPresent());
		kindService.removeKind(kind.get().getId());
		Optional<KindContainer> kindAfterRemove = kindService.findKind(persitedKind.getId());
		Assert.assertFalse(kindAfterRemove.isPresent());
	}

	@Test
	public void findKinderFromGesuch() {
		Assert.assertNotNull(kindService);
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		final KindContainer persitedKind1 = persistKind(gesuch);
		final KindContainer persitedKind2 = persistKind(gesuch);

		final Gesuch otherGesuch = TestDataUtil.createAndPersistGesuch(persistence);
		final KindContainer persitedKindOtherGesuch = persistKind(otherGesuch);

		final List<KindContainer> allKinderFromGesuch = kindService.findAllKinderFromGesuch(gesuch.getId());

		Assert.assertEquals(2, allKinderFromGesuch.size());
		Assert.assertEquals(persitedKind1, allKinderFromGesuch.get(0));
		Assert.assertEquals(persitedKind2, allKinderFromGesuch.get(1));
	}


	// HELP METHODS

	@Nonnull
	private KindContainer persistKind(Gesuch gesuch) {
		KindContainer kindContainer = TestDataUtil.createDefaultKindContainer();
		kindContainer.setGesuch(gesuch);
		persistence.persist(kindContainer.getKindGS().getPensumFachstelle().getFachstelle());
		persistence.persist(kindContainer.getKindJA().getPensumFachstelle().getFachstelle());
		persistence.persist(kindContainer.getKindGS());
		persistence.persist(kindContainer.getKindJA());

		kindService.saveKind(kindContainer);
		return kindContainer;
	}
}
