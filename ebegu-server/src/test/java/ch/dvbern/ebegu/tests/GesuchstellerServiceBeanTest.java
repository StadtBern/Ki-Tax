package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.services.GesuchstellerService;
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
 * Tests fuer die Klasse GesuchstellerService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private GesuchstellerService gesuchstellerService;

	@Inject
	private Persistence<Gesuchsteller> persistence;


	@Test
	public void createGesuchsteller() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();

		gesuchstellerService.saveGesuchsteller(gesuchsteller, TestDataUtil.createDefaultGesuch(), 1, false);
		Collection<Gesuchsteller> allGesuchsteller = gesuchstellerService.getAllGesuchsteller();
		Assert.assertEquals(1, allGesuchsteller.size());
		Gesuchsteller nextGesuchsteller = allGesuchsteller.iterator().next();
		Assert.assertEquals("Tester", nextGesuchsteller.getNachname());
		Assert.assertEquals("tim.tester@example.com", nextGesuchsteller.getMail());
	}

	@Test
	public void updateGesuchstellerTest() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller insertedGesuchsteller = insertNewEntity();
		Optional<Gesuchsteller> gesuchstellerOptional = gesuchstellerService.findGesuchsteller(insertedGesuchsteller.getId());
		Assert.assertTrue(gesuchstellerOptional.isPresent());
		Gesuchsteller gesuchsteller = gesuchstellerOptional.get();
		Assert.assertEquals("tim.tester@example.com", gesuchsteller.getMail());

		gesuchsteller.setMail("fritz.mueller@example.com");
		Gesuchsteller updatedGesuchsteller = gesuchstellerService.saveGesuchsteller(gesuchsteller, TestDataUtil.createDefaultGesuch(), 1, false);
		Assert.assertEquals("fritz.mueller@example.com", updatedGesuchsteller.getMail());
	}

	@Test
	public void removeGesuchstellerTest() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller insertedGesuchsteller = insertNewEntity();
		Assert.assertEquals(1, gesuchstellerService.getAllGesuchsteller().size());

		gesuchstellerService.removeGesuchsteller(insertedGesuchsteller);
		Assert.assertEquals(0, gesuchstellerService.getAllGesuchsteller().size());
	}

	@Test
	public void createGesuchstellerWithEinkommensverschlechterung() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchstellerWithEinkommensverschlechterung();

		gesuchstellerService.saveGesuchsteller(gesuchsteller, TestDataUtil.createDefaultGesuch(), 1, false);
		Collection<Gesuchsteller> allGesuchsteller = gesuchstellerService.getAllGesuchsteller();
		Assert.assertEquals(1, allGesuchsteller.size());

		Gesuchsteller nextGesuchsteller = allGesuchsteller.iterator().next();
		final EinkommensverschlechterungContainer einkommensverschlechterungContainer = nextGesuchsteller.getEinkommensverschlechterungContainer();
		Assert.assertNotNull(einkommensverschlechterungContainer);

		final Einkommensverschlechterung ekvGSBasisJahrPlus1 = einkommensverschlechterungContainer.getEkvGSBasisJahrPlus1();
		Assert.assertNotNull(ekvGSBasisJahrPlus1);
		Assert.assertEquals(0, ekvGSBasisJahrPlus1.getNettolohnJan().compareTo(BigDecimal.ONE));

		final Einkommensverschlechterung ekvGSBasisJahrPlus2 = einkommensverschlechterungContainer.getEkvGSBasisJahrPlus2();
		Assert.assertNotNull(ekvGSBasisJahrPlus2);
		Assert.assertEquals(0, ekvGSBasisJahrPlus2.getNettolohnJan().compareTo(BigDecimal.valueOf(2)));

		final Einkommensverschlechterung ekvJABasisJahrPlus1 = einkommensverschlechterungContainer.getEkvJABasisJahrPlus1();
		Assert.assertNotNull(ekvJABasisJahrPlus1);
		Assert.assertEquals(0, ekvJABasisJahrPlus1.getNettolohnJan().compareTo(BigDecimal.valueOf(3)));

		final Einkommensverschlechterung ekvJABasisJahrPlus2 = einkommensverschlechterungContainer.getEkvJABasisJahrPlus2();
		Assert.assertNotNull(ekvJABasisJahrPlus2);
		Assert.assertEquals(0, ekvJABasisJahrPlus2.getNettolohnJan().compareTo(BigDecimal.valueOf(4)));

	}

	@Test
	public void testSaveGesuchsteller2Mutation() {
		Gesuchsteller gesuchsteller = insertNewEntity();
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setTyp(AntragTyp.MUTATION);

		gesuch.setGesuchsteller1(TestDataUtil.createDefaultGesuchsteller());
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(TestDataUtil.createFinanzielleSituationContainer());
		gesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(TestDataUtil.createDefaultEinkommensverschlechterungsContainer());

		final Gesuchsteller savedGesuchsteller = gesuchstellerService.saveGesuchsteller(gesuchsteller, gesuch, 2, false);

		Assert.assertNotNull(savedGesuchsteller.getFinanzielleSituationContainer());
		Assert.assertNotNull(savedGesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationJA());
		Assert.assertFalse(savedGesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationJA().getSteuererklaerungAusgefuellt());
		Assert.assertFalse(savedGesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationJA().getSteuerveranlagungErhalten());
		Assert.assertEquals(gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getJahr(),
			savedGesuchsteller.getFinanzielleSituationContainer().getJahr());

		Assert.assertNotNull(savedGesuchsteller.getEinkommensverschlechterungContainer());
		Assert.assertNotNull(savedGesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1());
		Assert.assertFalse(savedGesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1().getSteuererklaerungAusgefuellt());
		Assert.assertFalse(savedGesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1().getSteuerveranlagungErhalten());
	}


	// Helper Methods

	private Gesuchsteller insertNewEntity() {
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		persistence.persist(gesuchsteller);
		return gesuchsteller;
	}


}
