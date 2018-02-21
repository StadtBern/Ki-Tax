/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.services.TestdataCreationService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.testdata.TestdataSetupConfig;
import org.junit.Before;

/**
 * Superklasse fuer Tests, welche den TestdataCreationService verwenden.
 * Initialisiert diesen mit Mandant, Gesuchsperiode und Institutionen
 */
public class AbstractTestdataCreationTest extends AbstractEbeguLoginTest {

	@Inject
	private TestdataCreationService testdataCreationService;

	protected Gesuchsperiode gesuchsperiode;

	@Before
	public void init() {
		// Tests initialisieren
		gesuchsperiode = createGesuchsperiode(true);
		final InstitutionStammdaten kitaAaregg = TestDataUtil.createInstitutionStammdatenKitaWeissenstein();
		final InstitutionStammdaten kitaBruennen = TestDataUtil.createInstitutionStammdatenKitaBruennen();
		final InstitutionStammdaten tagiAaregg = TestDataUtil.createInstitutionStammdatenTagiWeissenstein();
		// Die Institution Brünnen erhaelt auch Tagesschule und Ferieninsel-Stammdaten
		InstitutionStammdaten tagesschule = TestDataUtil.createInstitutionStammdatenTagesschuleForInstitution(kitaBruennen.getInstitution());
		InstitutionStammdaten ferieninsel = TestDataUtil.createInstitutionStammdatenFerieninselForInstitution(kitaBruennen.getInstitution());
		Mandant mandant = TestDataUtil.createDefaultMandant();
		TestdataSetupConfig setupConfig = new TestdataSetupConfig(mandant, kitaBruennen, kitaAaregg, tagiAaregg, tagesschule, ferieninsel, gesuchsperiode);
		testdataCreationService.setupTestdata(setupConfig);
	}
}
