package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Arquillian Tests fuer die Klasse GesuchService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchServiceTest extends AbstractEbeguTest {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private InstitutionService institutionService;

	@Test
	public void createGesuch() {
		Assert.assertNotNull(gesuchService);
		persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		Assert.assertEquals(1, allGesuche.size());
	}

	@Test
	public void updateGesuch() {
		Assert.assertNotNull(gesuchService);
		final Gesuch insertedGesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Optional<Gesuch> gesuch = gesuchService.findGesuch(insertedGesuch.getId());
		Assert.assertEquals(insertedGesuch.getFall().getId(), gesuch.get().getFall().getId());

		gesuch.get().setFall(persistence.persist(TestDataUtil.createDefaultFall()));
		final Gesuch updated = gesuchService.updateGesuch(gesuch.get(), false);
		Assert.assertEquals(updated.getFall().getId(), gesuch.get().getFall().getId());

	}

	@Test
	public void removeGesuchTest() {
		Assert.assertNotNull(gesuchService);
		final Gesuch gesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);
		Assert.assertEquals(1, gesuchService.getAllGesuche().size());

		gesuchService.removeGesuch(gesuch);
		Assert.assertEquals(0, gesuchService.getAllGesuche().size());
	}

	@Test
	public void createEinkommensverschlechterungsGesuch() {
		Assert.assertNotNull(gesuchService);
		persistEinkommensverschlechterungEntity();
		final Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		Assert.assertEquals(1, allGesuche.size());
		Gesuch gesuch = allGesuche.iterator().next();
		final EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.getEinkommensverschlechterungInfo();
		Assert.assertNotNull(einkommensverschlechterungInfo);
		Assert.assertTrue(einkommensverschlechterungInfo.getEinkommensverschlechterung());
		Assert.assertTrue(einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus1());
		Assert.assertFalse(einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2());
	}

	@Test
	public void testGetAllActiveGesucheAllActive() {
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Collection<Gesuch> allActiveGesuche = gesuchService.getAllActiveGesuche();
		Assert.assertEquals(2, allActiveGesuche.size());
	}

	@Test
	public void testGetAllActiveGesucheNotAllActive() {
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.VERFUEGT);

		final Collection<Gesuch> allActiveGesuche = gesuchService.getAllActiveGesuche();
		Assert.assertEquals(1, allActiveGesuche.size());
	}

	@Test
	public void testSearchAntraegeOrder() {
		TestDataUtil.createAndPersistBenutzer(persistence);
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.VERFUEGT);
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getSort().setPredicate("fallNummer");
		//nach fallnummer geordnete liste
		Pair<Long, List<Gesuch>> resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), resultpair.getLeft());
		List<Gesuch> foundGesuche = resultpair.getRight();
		Assert.assertEquals(gesuch.getId(), foundGesuche.get(2).getId());
		//genau anders rum ordnen
		filterDTO.getSort().setReverse(true);
		resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), resultpair.getLeft());
		List<Gesuch> foundGesucheReversed = resultpair.getRight();
		Assert.assertEquals(gesuch.getId(), foundGesucheReversed.get(0).getId());

	}


	// HELP METHOD

	private Gesuch persistNewEntity(AntragStatus status) {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setStatus(status);
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

	private Gesuch persistEinkommensverschlechterungEntity() {
		final Gesuch gesuch = TestDataUtil.createDefaultEinkommensverschlechterungsGesuch();
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

}
