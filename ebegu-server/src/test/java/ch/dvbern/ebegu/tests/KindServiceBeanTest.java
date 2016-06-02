package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
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
	private Persistence<Kind> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createAndUpdatekindTest() {
		Assert.assertNotNull(kindService);
		KindContainer persitedKind = persistKind();
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

	}

	@Test
	public void removekindTest() {
		Assert.assertNotNull(kindService);
		KindContainer persitedKind = persistKind();
		Optional<KindContainer> kind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(kind.isPresent());
		kindService.removeKind(kind.get().getId());
		Optional<KindContainer> kindAfterRemove = kindService.findKind(persitedKind.getId());
		Assert.assertFalse(kindAfterRemove.isPresent());
	}


	// HELP METHODS

	@Nonnull
	private KindContainer persistKind() {
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		persistence.persist(gesuch.getGesuchsperiode());
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch);

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
