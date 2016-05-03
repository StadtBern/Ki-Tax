package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Kind;
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
import java.util.Collection;
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
		Kind persitedKind = persistKind();
		Optional<Kind> kind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(kind.isPresent());
		Kind savedKind = kind.get();
		Assert.assertEquals(persitedKind.getNachname(), savedKind.getNachname());

		Assert.assertNotEquals("Neuer Name", savedKind.getNachname());
		savedKind.setNachname("Neuer Name");
		kindService.saveKind(savedKind);
		Optional<Kind> updatedKind= kindService.findKind(persitedKind.getId());
		Assert.assertTrue(updatedKind.isPresent());
		Assert.assertEquals("Neuer Name", updatedKind.get().getNachname());

	}

	@Test
	public void removekindTest() {
		Assert.assertNotNull(kindService);
		Kind persitedKind = persistKind();
		Optional<Kind> kind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(kind.isPresent());
		kindService.removeKind(kind.get().getId());
		Optional<Kind> kindAfterRemove = kindService.findKind(persitedKind.getId());
		Assert.assertFalse(kindAfterRemove.isPresent());
	}

	@Test
	public void getAllKinderFromGesuchTest() {
		Assert.assertNotNull(kindService);
		Kind kind = persistKind();
		Collection<Kind> allKinderFromGesuch = kindService.getAllKinderFromGesuch(kind.getGesuch().getId());
		Assert.assertEquals(1, allKinderFromGesuch.size());
		Kind nextKind = allKinderFromGesuch.iterator().next();
		Assert.assertEquals("Kind_Mustermann", nextKind.getNachname());

		allKinderFromGesuch = kindService.getAllKinderFromGesuch("andereGesuchID");
		Assert.assertEquals(0, allKinderFromGesuch.size());
	}

	// HELP METHODS

	@Nonnull
	private Kind persistKind() {
		Kind kind = TestDataUtil.createDefaultKind();
		persistence.persist(kind.getFachstelle());
		persistence.persist(kind.getGesuch().getFall());
		persistence.persist(kind.getGesuch());

		kindService.saveKind(kind);
		return kind;
	}
}
