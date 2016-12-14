package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.services.EinkommensverschlechterungInfoService;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
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
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class FamiliensituationServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private FamiliensituationService familiensituationService;

	@Inject
	private EinkommensverschlechterungInfoService evInfoService;

	@Inject
	private Persistence<Gesuch> persistence;



	@Test
	public void testCreateFamiliensituation() {
		Assert.assertNotNull(familiensituationService);
		insertNewFamiliensituationContainer();

		Collection<FamiliensituationContainer> allFamiliensituation = familiensituationService.getAllFamiliensituatione();
		Assert.assertEquals(1, allFamiliensituation.size());
		FamiliensituationContainer nextFamsit = allFamiliensituation.iterator().next();
		Assert.assertEquals(EnumFamilienstatus.ALLEINERZIEHEND, nextFamsit.getFamiliensituationJA().getFamilienstatus());
		Assert.assertEquals(EnumGesuchstellerKardinalitaet.ALLEINE, nextFamsit.getFamiliensituationJA().getGesuchstellerKardinalitaet());
	}

	@Test
	public void testUpdateFamiliensituationTest() {
		Optional<FamiliensituationContainer> familiensituation = createFamiliensituationContainer();

		familiensituation.get().extractFamiliensituation().setFamilienstatus(EnumFamilienstatus.KONKUBINAT);
		FamiliensituationContainer updatedFamsit = familiensituationService.saveFamiliensituation(TestDataUtil.createDefaultGesuch(),
			familiensituation.get());
		Assert.assertEquals(EnumFamilienstatus.KONKUBINAT, updatedFamsit.extractFamiliensituation().getFamilienstatus());
		Assert.assertEquals(EnumFamilienstatus.KONKUBINAT,
			familiensituationService.findFamiliensituation(updatedFamsit.getId()).get().extractFamiliensituation().getFamilienstatus());
	}

	@Test
	public void testRemoveFamiliensituationTest() {
		Assert.assertNotNull(familiensituationService);
		FamiliensituationContainer insertedFamiliensituation = insertNewFamiliensituationContainer();
		Assert.assertEquals(1, familiensituationService.getAllFamiliensituatione().size());

		familiensituationService.removeFamiliensituation(insertedFamiliensituation);
		Assert.assertEquals(0, familiensituationService.getAllFamiliensituatione().size());
	}

	@Test
	public void testSaveFamiliensituationMutation() {
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuch.setTyp(AntragTyp.MUTATION);


		final EinkommensverschlechterungInfoContainer evInfo = TestDataUtil.createDefaultEinkommensverschlechterungsInfoContainer(gesuch);
		final Optional<EinkommensverschlechterungInfoContainer> einkommensverschlechterungInfo = evInfoService.createEinkommensverschlechterungInfo(evInfo);
		gesuch.setEinkommensverschlechterungInfoContainer(einkommensverschlechterungInfo.get());

		Optional<FamiliensituationContainer> familiensituation = createFamiliensituationContainer();
		final FamiliensituationContainer newFamiliensituation = familiensituation.get().copyForMutation(new FamiliensituationContainer(),false);
		newFamiliensituation.extractFamiliensituation().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		newFamiliensituation.extractFamiliensituation().setGemeinsameSteuererklaerung(null);

		final FamiliensituationContainer persistedFamiliensituation = familiensituationService.saveFamiliensituation(gesuch,
			newFamiliensituation);

		Assert.assertFalse(persistedFamiliensituation.extractFamiliensituation().getGemeinsameSteuererklaerung());
		Assert.assertFalse(gesuch.extractEinkommensverschlechterungInfo().getGemeinsameSteuererklaerung_BjP1());
		Assert.assertFalse(gesuch.extractEinkommensverschlechterungInfo().getGemeinsameSteuererklaerung_BjP2());
	}


	// HELP METHODS

	@Nonnull
	private FamiliensituationContainer insertNewFamiliensituationContainer() {
		FamiliensituationContainer familiensituationContainer = TestDataUtil.createDefaultFamiliensituationContainer();
		familiensituationService.saveFamiliensituation(TestDataUtil.createDefaultGesuch(), familiensituationContainer);
		return familiensituationContainer;
	}

	@Nonnull
	private Optional<FamiliensituationContainer> createFamiliensituationContainer() {
		Assert.assertNotNull(familiensituationService);
		FamiliensituationContainer insertedFamiliensituationContainer = insertNewFamiliensituationContainer();
		Optional<FamiliensituationContainer> familiensituation = familiensituationService.findFamiliensituation(insertedFamiliensituationContainer.getId());
		Assert.assertEquals(EnumFamilienstatus.ALLEINERZIEHEND, familiensituation.get().extractFamiliensituation().getFamilienstatus());
		return familiensituation;
	}

}
