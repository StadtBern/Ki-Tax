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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.KindService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse KindService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class KindServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private KindService kindService;

	@Inject
	private Persistence persistence;

	@Inject
	private FallService fallService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private WizardStepService wizardStepService;

	@Test
	public void createAndUpdatekindTest() {
		Assert.assertNotNull(kindService);
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		KindContainer persitedKind = persistKind(gesuch);
		Optional<KindContainer> kind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(kind.isPresent());
		KindContainer savedKind = kind.get();
		Assert.assertEquals(persitedKind.getKindGS().getNachname(), savedKind.getKindGS().getNachname());
		Assert.assertEquals(persitedKind.getKindJA().getNachname(), savedKind.getKindJA().getNachname());

		Assert.assertNotEquals("Neuer Name", savedKind.getKindGS().getNachname());
		savedKind.getKindGS().setNachname("Neuer Name");
		kindService.saveKind(savedKind);
		Optional<KindContainer> updatedKind = kindService.findKind(persitedKind.getId());
		Assert.assertTrue(updatedKind.isPresent());
		Assert.assertEquals("Neuer Name", updatedKind.get().getKindGS().getNachname());
		Assert.assertEquals(new Integer(1), updatedKind.get().getNextNumberBetreuung());
		Assert.assertEquals(new Integer(1), updatedKind.get().getKindNummer());
		Assert.assertEquals(new Integer(2), fallService.findFall(gesuch.getFall().getId()).get().getNextNumberKind());
	}

	@Test
	public void addKindInMutationTest() {
		Gesuch erstgesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25), AntragStatus.VERFUEGT);
		erstgesuch.setGueltig(true);
		erstgesuch.setTimestampVerfuegt(LocalDateTime.now());
		erstgesuch = gesuchService.updateGesuch(erstgesuch, true, null);
		Assert.assertEquals(2, erstgesuch.getKindContainers().size());

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = gesuchOptional.get();

			mutation = gesuchService.createGesuch(mutation);
			Assert.assertEquals(2, mutation.getKindContainers().size());

			WizardStep wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(mutation.getId(), WizardStepName.KINDER);
			Assert.assertEquals(WizardStepStatus.OK, wizardStepFromGesuch.getWizardStepStatus());

			KindContainer neuesKindInMutation = TestDataUtil.createKindContainerWithoutFachstelle();

			neuesKindInMutation.setGesuch(mutation);
			kindService.saveKind(neuesKindInMutation);
			Assert.assertEquals(3, kindService.findAllKinderFromGesuch(mutation.getId()).size());

			wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(mutation.getId(), WizardStepName.KINDER);
			Assert.assertEquals(WizardStepStatus.MUTIERT, wizardStepFromGesuch.getWizardStepStatus());
		}
	}

	@Test
	public void findKinderFromGesuch() {
		Assert.assertNotNull(kindService);
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		final KindContainer persitedKind1 = persistKind(gesuch);
		final KindContainer persitedKind2 = persistKind(gesuch);

		final Gesuch otherGesuch = TestDataUtil.createAndPersistGesuch(persistence);
		final KindContainer persitedKindOtherGesuch = persistKind(otherGesuch);

		final List<KindContainer> allKinderFromGesuch = kindService.findAllKinderFromGesuch(gesuch.getId());

		Assert.assertEquals(2, allKinderFromGesuch.size());
		Assert.assertTrue(allKinderFromGesuch.contains(persitedKind1));
		Assert.assertTrue(allKinderFromGesuch.contains(persitedKind2));

	}

	// HELP METHODS

	@Nonnull
	private KindContainer persistKind(Gesuch gesuch) {
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
