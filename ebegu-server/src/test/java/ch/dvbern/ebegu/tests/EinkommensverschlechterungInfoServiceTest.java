package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.EinkommensverschlechterungInfoService;
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
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class EinkommensverschlechterungInfoServiceTest extends AbstractEbeguLoginTest {

	public static final String TEST_123 = "test123";

	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;

	@Inject
	private Persistence<Gesuch> persistence;




	@Test
	public void createEinkommensverschlechterungInfoTest() {
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setFall(persistence.persist(TestDataUtil.createDefaultFall()));
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch = persistence.persist(gesuch);

		TestDataUtil.createDefaultEinkommensverschlechterungsInfoContainer(gesuch);

		einkommensverschlechterungInfoService.createEinkommensverschlechterungInfo(gesuch.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuch);
		Assert.assertNotNull(gesuch.extractEinkommensverschlechterungInfo());

		Collection<EinkommensverschlechterungInfoContainer> allEinkommensverschlechterungInfo = einkommensverschlechterungInfoService.getAllEinkommensverschlechterungInfo();
		EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo = allEinkommensverschlechterungInfo.iterator().next();
		Assert.assertNotNull(einkommensverschlechterungInfo);
	}

	private EinkommensverschlechterungInfoContainer persistAndGetEinkommensverschlechterungInfoOnGesuch() {
		Gesuch gesuch = TestDataUtil.createDefaultEinkommensverschlechterungsGesuch();
		gesuch.setFall(persistence.persist(TestDataUtil.createDefaultFall()));
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch = persistence.persist(gesuch);

		return gesuch.getEinkommensverschlechterungInfoContainer();
	}

	/**
	 * 1. Create a info
	 * 2. Store created Info on DB
	 * 3. get Stored Info from DB by calling getAll
	 * 4. Change Stored Info
	 * 5. Update Info on DB
	 * 6. get Stored Info from DB by calling find
	 * <p>
	 * Expected result: new result must be the updated value
	 */
	@Test
	public void updateAndFindEinkommensverschlechterungInfoTest() {
		Assert.assertNotNull(einkommensverschlechterungInfoService);

		persistAndGetEinkommensverschlechterungInfoOnGesuch();

		Collection<EinkommensverschlechterungInfoContainer> allEinkommensverschlechterungInfo = einkommensverschlechterungInfoService.getAllEinkommensverschlechterungInfo();
		EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo = allEinkommensverschlechterungInfo.iterator().next();
		Assert.assertFalse(einkommensverschlechterungInfo.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2());
		Assert.assertNull(einkommensverschlechterungInfo.getEinkommensverschlechterungInfoJA().getGrundFuerBasisJahrPlus2());

		//Add EKV_BasisJahr2
		einkommensverschlechterungInfo.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus2(true);
		einkommensverschlechterungInfo.getEinkommensverschlechterungInfoJA().setGrundFuerBasisJahrPlus2(TEST_123);
		einkommensverschlechterungInfoService.updateEinkommensVerschlechterungInfoAndGesuch(einkommensverschlechterungInfo
			.getGesuch(), null, einkommensverschlechterungInfo);

		final Optional<EinkommensverschlechterungInfoContainer> ekvInfoUpdated = einkommensverschlechterungInfoService.findEinkommensverschlechterungInfo(einkommensverschlechterungInfo.getId());

		final EinkommensverschlechterungInfoContainer info1 = ekvInfoUpdated.get();
		Assert.assertNotNull(info1);
		Assert.assertTrue(info1.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2());
		Assert.assertEquals(TEST_123, info1.getEinkommensverschlechterungInfoJA().getGrundFuerBasisJahrPlus2());

		//Remove EKV_BasisJahr2
		info1.getEinkommensverschlechterungInfoJA().setEkvFuerBasisJahrPlus2(false);
		einkommensverschlechterungInfoService.updateEinkommensVerschlechterungInfoAndGesuch(info1.getGesuch(), null, info1);

		final Optional<EinkommensverschlechterungInfoContainer> ekvInfoUpdated2 = einkommensverschlechterungInfoService.findEinkommensverschlechterungInfo(info1.getId());

		final EinkommensverschlechterungInfoContainer info2 = ekvInfoUpdated2.get();
		Assert.assertNotNull(info2);
		Assert.assertFalse(info2.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2());
		//even though GrundFuerBasisJahrPlus2 was not set to null it changes to null after saving
		Assert.assertNull(info2.getEinkommensverschlechterungInfoJA().getGrundFuerBasisJahrPlus2());
	}

	@Test
	public void removeEinkommensverschlechterungInfoTest() {
		Assert.assertNotNull(einkommensverschlechterungInfoService);
		Assert.assertEquals(0, einkommensverschlechterungInfoService.getAllEinkommensverschlechterungInfo().size());

		EinkommensverschlechterungInfoContainer info = persistAndGetEinkommensverschlechterungInfoOnGesuch();
		Assert.assertEquals(1, einkommensverschlechterungInfoService.getAllEinkommensverschlechterungInfo().size());

		einkommensverschlechterungInfoService.removeEinkommensverschlechterungInfo(info);
		Assert.assertEquals(0, einkommensverschlechterungInfoService.getAllEinkommensverschlechterungInfo().size());

	}


}
