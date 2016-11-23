package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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
 * Arquillian Tests fuer die Klasse GesuchsperiodeService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;



	@Test
	public void createGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		insertNewEntity(true);

		Collection<Gesuchsperiode> allGesuchsperioden = gesuchsperiodeService.getAllGesuchsperioden();
		Assert.assertEquals(1, allGesuchsperioden.size());
		Gesuchsperiode nextGesuchsperiode = allGesuchsperioden.iterator().next();
		Assert.assertTrue(nextGesuchsperiode.getActive());
	}

	@Test
	public void updateGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		Gesuchsperiode insertedGesuchsperiode = insertNewEntity(true);
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(insertedGesuchsperiode.getId());
		Assert.assertTrue(gesuchsperiode.get().getActive());

		gesuchsperiode.get().setActive(false);
		Gesuchsperiode updateGesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode.get());
		Assert.assertFalse(updateGesuchsperiode.getActive());
		Assert.assertFalse(gesuchsperiodeService.findGesuchsperiode(updateGesuchsperiode.getId()).get().getActive());
	}

	@Test
	public void removeGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		Assert.assertEquals(0, gesuchsperiodeService.getAllGesuchsperioden().size());

		Gesuchsperiode insertedGesuchsperiode = insertNewEntity(true);
		Assert.assertEquals(1, gesuchsperiodeService.getAllGesuchsperioden().size());

		gesuchsperiodeService.removeGesuchsperiode(insertedGesuchsperiode.getId());
		Assert.assertEquals(0, gesuchsperiodeService.getAllGesuchsperioden().size());
	}

	@Test
	public void getAllActiveGesuchsperiodenTest() {
		Gesuchsperiode insertedGesuchsperiode = insertNewEntity(true);
		insertNewEntity(false);

		Collection<Gesuchsperiode> allGesuchsperioden = gesuchsperiodeService.getAllGesuchsperioden();
		Assert.assertEquals(2, allGesuchsperioden.size());

		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		Assert.assertEquals(1, allActiveGesuchsperioden.size());
		Assert.assertEquals(insertedGesuchsperiode, allActiveGesuchsperioden.iterator().next());
	}


	// HELP METHODS

	private Gesuchsperiode insertNewEntity(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiode.setActive(active);
		gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}
}
