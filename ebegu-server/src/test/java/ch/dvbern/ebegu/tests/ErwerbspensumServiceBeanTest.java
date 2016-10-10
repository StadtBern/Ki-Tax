package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.services.ErwerbspensumService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Optional;

/**
 * Test fuer Erwerbspensum Service
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumServiceBeanTest extends AbstractEbeguTest {



	@Inject
	private ErwerbspensumService erwerbspensumService;
	@Inject
	private InstitutionService instService;

	@Inject
	private Persistence<Gesuch> persistence;


	@Test
	public void createFinanzielleSituation() {
		Assert.assertNotNull(erwerbspensumService);

		Erwerbspensum erwerbspensumData = TestDataUtil.createErwerbspensumData();
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		gesuchsteller = persistence.persist(gesuchsteller);

		ErwerbspensumContainer ewpCont = TestDataUtil.createErwerbspensumContainer();
		ewpCont.setErwerbspensumGS(erwerbspensumData);
		ewpCont.setGesuchsteller(gesuchsteller);

		erwerbspensumService.saveErwerbspensum(ewpCont, TestDataUtil.createDefaultGesuch());
		Collection<ErwerbspensumContainer> allErwerbspensenenContainer = erwerbspensumService.getAllErwerbspensenenContainer();
		Assert.assertEquals(1, allErwerbspensenenContainer.size());
		Optional<ErwerbspensumContainer> storedContainer = erwerbspensumService.findErwerbspensum(ewpCont.getId());
		Assert.assertTrue(storedContainer.isPresent());
		Assert.assertFalse(storedContainer.get().isNew());
		Assert.assertEquals(storedContainer.get(), allErwerbspensenenContainer.iterator().next());
		Assert.assertEquals(erwerbspensumData.getTaetigkeit(), storedContainer.get().getErwerbspensumGS().getTaetigkeit());
	}

	@Test
	public void updateFinanzielleSituationTest() {
		ErwerbspensumContainer insertedEwpCont = insertNewEntity();
		Optional<ErwerbspensumContainer> ewpContOpt = erwerbspensumService.findErwerbspensum(insertedEwpCont.getId());
		Assert.assertTrue(ewpContOpt.isPresent());
		ErwerbspensumContainer erwPenCont = ewpContOpt.get();
		Erwerbspensum changedData = TestDataUtil.createErwerbspensumData();
		changedData.setGueltigkeit(new DateRange(LocalDate.now(), LocalDate.now().plusDays(80)));
		erwPenCont.setErwerbspensumGS(changedData);

		ErwerbspensumContainer updatedCont = erwerbspensumService.saveErwerbspensum(erwPenCont, TestDataUtil.createDefaultGesuch());
		Assert.assertEquals(LocalDate.now(), updatedCont.getErwerbspensumGS().getGueltigkeit().getGueltigAb());
	}

	@Test
	public void findErwerbspensenFromGesuch() {
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		//Creates another Erwerbspensum that won't be loaded since it doesn't belong to the gesuch
		Erwerbspensum erwerbspensumData = TestDataUtil.createErwerbspensumData();
		Gesuchsteller gesuchsteller1 = TestDataUtil.createDefaultGesuchsteller();
		gesuchsteller1 = persistence.persist(gesuchsteller1);
		ErwerbspensumContainer ewpCont = TestDataUtil.createErwerbspensumContainer();
		ewpCont.setErwerbspensumGS(erwerbspensumData);
		ewpCont.setGesuchsteller(gesuchsteller1);
		erwerbspensumService.saveErwerbspensum(ewpCont, TestDataUtil.createDefaultGesuch());

		Collection<ErwerbspensumContainer> erwerbspensenFromGesuch = erwerbspensumService.findErwerbspensenFromGesuch(gesuch.getId());

		Assert.assertEquals(1, erwerbspensenFromGesuch.size());
		Assert.assertEquals(gesuch.getGesuchsteller1().getErwerbspensenContainers().iterator().next(),
			erwerbspensenFromGesuch.iterator().next());
	}

	@Test
	public void removeFinanzielleSituationTest() {
		Assert.assertNotNull(erwerbspensumService);
		Assert.assertEquals(0, erwerbspensumService.getAllErwerbspensenenContainer().size());

		ErwerbspensumContainer insertedEwpCont = insertNewEntity();
		Assert.assertEquals(1, erwerbspensumService.getAllErwerbspensenenContainer().size());

		erwerbspensumService.removeErwerbspensum(insertedEwpCont.getId(), TestDataUtil.createDefaultGesuch());
		Assert.assertEquals(0, erwerbspensumService.getAllErwerbspensenenContainer().size());
	}

	private ErwerbspensumContainer insertNewEntity() {
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		ErwerbspensumContainer container = TestDataUtil.createErwerbspensumContainer();
		gesuchsteller.addErwerbspensumContainer(container);
		gesuchsteller = persistence.persist(gesuchsteller);
		return gesuchsteller.getErwerbspensenContainers().iterator().next();
	}


}
