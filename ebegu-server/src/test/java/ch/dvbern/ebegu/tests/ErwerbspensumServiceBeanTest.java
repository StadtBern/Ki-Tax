/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
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

/**
 * Test fuer Erwerbspensum Service
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private ErwerbspensumService erwerbspensumService;
	@Inject
	private InstitutionService instService;

	@Inject
	private Persistence persistence;

	private Gesuch gesuch;

	@Test
	public void createFinanzielleSituation() {
		Assert.assertNotNull(erwerbspensumService);

		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		Erwerbspensum erwerbspensumData = TestDataUtil.createErwerbspensumData();
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
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
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);

		//Creates another Erwerbspensum that won't be loaded since it doesn't belong to the gesuch
		Erwerbspensum erwerbspensumData = TestDataUtil.createErwerbspensumData();
		GesuchstellerContainer gesuchsteller1 = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		gesuch.setGesuchsteller1(gesuchsteller1);
		gesuchsteller1 = persistence.persist(gesuchsteller1);
		persistence.merge(gesuch);

		ErwerbspensumContainer ewpCont = TestDataUtil.createErwerbspensumContainer();
		ewpCont.setErwerbspensumGS(erwerbspensumData);
		ewpCont.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.addErwerbspensumContainer(ewpCont);
		erwerbspensumService.saveErwerbspensum(ewpCont, gesuch);

		Collection<ErwerbspensumContainer> erwerbspensenFromGesuch = erwerbspensumService.findErwerbspensenFromGesuch(gesuch.getId());

		Assert.assertEquals(1, erwerbspensenFromGesuch.size());
		Assert.assertNotNull(gesuch.getGesuchsteller1());
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

	@Test
	public void isErwerbspensumRequired_KITA_Required() {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());

		Assert.assertTrue(erwerbspensumService.isErwerbspensumRequired(gesuch));
	}

	@Test
	public void isErwerbspensumRequired_KITA_TAGESELTERNKLEINKIND_Required() {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		final KindContainer kind = gesuch.getKindContainers().iterator().next();
		final Betreuung betreuung = kind.getBetreuungen().iterator().next();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
		persistence.merge(betreuung.getInstitutionStammdaten());

		Assert.assertTrue(erwerbspensumService.isErwerbspensumRequired(gesuch));
	}

	@Test
	public void isErwerbspensumRequired_TAGI_NotRequired() {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		final KindContainer kind = gesuch.getKindContainers().iterator().next();
		final Betreuung betreuung = kind.getBetreuungen().iterator().next();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGI);
		persistence.merge(betreuung.getInstitutionStammdaten());

		Assert.assertFalse(erwerbspensumService.isErwerbspensumRequired(gesuch));
	}

	@Test
	public void isErwerbspensumRequired_Fachstelle_NotRequired() {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		final KindContainer kind = gesuch.getKindContainers().iterator().next();
		final PensumFachstelle pensumFachstelle = TestDataUtil.createDefaultPensumFachstelle();
		kind.getKindJA().setPensumFachstelle(pensumFachstelle);
		persistence.persist(pensumFachstelle.getFachstelle());
		persistence.persist(pensumFachstelle);

		Assert.assertFalse(erwerbspensumService.isErwerbspensumRequired(gesuch));
	}

	private ErwerbspensumContainer insertNewEntity() {
		gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		ErwerbspensumContainer container = TestDataUtil.createErwerbspensumContainer();
		gesuchsteller.addErwerbspensumContainer(container);
		gesuchsteller = persistence.persist(gesuchsteller);
		return gesuchsteller.getErwerbspensenContainers().iterator().next();
	}

}
