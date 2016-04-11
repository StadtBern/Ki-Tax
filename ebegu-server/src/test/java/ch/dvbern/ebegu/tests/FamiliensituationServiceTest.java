package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.services.FamiliensituationService;
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
 * Arquillian Tests fuer die Klasse FamiliensituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FamiliensituationServiceTest extends AbstractEbeguTest {

	@Inject
	private FamiliensituationService familiensituationService;

	@Inject
	private Persistence<Familiensituation> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createFamiliensituation() {
		Assert.assertNotNull(familiensituationService);
		insertNewEntity();

		Collection<Familiensituation> allFamiliensituation = familiensituationService.getAllFamiliensituatione();
		Assert.assertEquals(1, allFamiliensituation.size());
		Familiensituation nextFamsit = allFamiliensituation.iterator().next();
		Assert.assertEquals(EnumFamilienstatus.ALLEINERZIEHEND, nextFamsit.getFamilienstatus());
		Assert.assertEquals(EnumGesuchstellerKardinalitaet.ALLEINE, nextFamsit.getGesuchstellerKardinalitaet());
		Assert.assertEquals("DVBern", nextFamsit.getBemerkungen());
	}

	@Test
	public void updateFamiliensituationTest() {
		Assert.assertNotNull(familiensituationService);
		Familiensituation insertedFamiliensituation = insertNewEntity();
		Optional<Familiensituation> familiensituation = familiensituationService.findFamiliensituation(insertedFamiliensituation.getId());
		Assert.assertEquals(EnumFamilienstatus.ALLEINERZIEHEND, familiensituation.get().getFamilienstatus());

		familiensituation.get().setFamilienstatus(EnumFamilienstatus.KONKUBINAT);
		Familiensituation updatedFamsit = familiensituationService.updateFamiliensituation(familiensituation.get());
		Assert.assertEquals(EnumFamilienstatus.KONKUBINAT, updatedFamsit.getFamilienstatus());
		Assert.assertEquals(EnumFamilienstatus.KONKUBINAT,
			familiensituationService.findFamiliensituation(updatedFamsit.getId()).get().getFamilienstatus());
	}

	@Test
	public void removeFamiliensituationTest() {
		Assert.assertNotNull(familiensituationService);
		Familiensituation insertedFamiliensituation = insertNewEntity();
		Assert.assertEquals(1, familiensituationService.getAllFamiliensituatione().size());

		familiensituationService.removeFamiliensituation(insertedFamiliensituation);
		Assert.assertEquals(0, familiensituationService.getAllFamiliensituatione().size());
	}


	// HELP METHODS

	@Nonnull
	private Familiensituation insertNewEntity() {
		Familiensituation familiensituation = TestDataUtil.createDefaultFamiliensituation();
		familiensituation.getGesuch().setFall(persistence.persist(familiensituation.getGesuch().getFall()));
		familiensituation.setGesuch(persistence.persist(familiensituation.getGesuch()));
		familiensituationService.createFamiliensituation(familiensituation);
		return familiensituation;
	}

}
