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

package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxWizardStep;
import ch.dvbern.ebegu.api.resource.WizardStepResource;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionService;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Tests fuer WizardStepResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class WizardStepResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private WizardStepResource wizardStepResource;
	private Gesuch gesuch;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence persistence;


	@Test
	public void testCreateWizardStepList() throws EbeguException {
		gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		final List<JaxWizardStep> wizardStepList = wizardStepResource.createWizardStepList(new JaxId(gesuch.getId()));

		Assert.assertEquals(13, wizardStepList.size());
		assertWizardStep(wizardStepList.get(0), WizardStepName.GESUCH_ERSTELLEN, WizardStepStatus.OK);
		assertWizardStep(wizardStepList.get(1), WizardStepName.FAMILIENSITUATION, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(2), WizardStepName.GESUCHSTELLER, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(3), WizardStepName.UMZUG, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(4), WizardStepName.KINDER, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(5), WizardStepName.BETREUUNG, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(6), WizardStepName.ABWESENHEIT, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(7), WizardStepName.ERWERBSPENSUM, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(8), WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(9), WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(10), WizardStepName.DOKUMENTE, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(11), WizardStepName.FREIGABE, WizardStepStatus.UNBESUCHT);
		assertWizardStep(wizardStepList.get(12), WizardStepName.VERFUEGEN, WizardStepStatus.UNBESUCHT);
	}

	private void assertWizardStep(JaxWizardStep wizardStep, WizardStepName wizardStepName, WizardStepStatus status) {
		Assert.assertEquals(gesuch.getId(), wizardStep.getGesuchId());
		Assert.assertEquals(wizardStepName, wizardStep.getWizardStepName());
		Assert.assertEquals(status, wizardStep.getWizardStepStatus());
		Assert.assertNull(wizardStep.getBemerkungen());
	}
}
