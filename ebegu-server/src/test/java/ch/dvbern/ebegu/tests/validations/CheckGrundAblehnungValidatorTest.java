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

package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckGrundAblehnungValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests fuer CheckGrundAblehnungValidator
 */
public class CheckGrundAblehnungValidatorTest {

	private CheckGrundAblehnungValidator validator;
	private Betreuung betreuung;

	@Before
	public void setUp() throws Exception {
		validator = new CheckGrundAblehnungValidator();
		betreuung = TestDataUtil.createDefaultBetreuung();
	}

	@Test
	public void testNichtAbgewiesenEmptyGrund() {
		betreuung.setGrundAblehnung("");
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

	@Test
	public void testNichtAbgewiesenNullGrund() {
		betreuung.setGrundAblehnung(null);
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

	@Test
	public void testNichtAbgewiesenWithGrund() {
		betreuung.setGrundAblehnung("mein Grund");
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

	@Test
	public void testAbgewiesenEmptyGrund() {
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung("");
		Assert.assertFalse(validator.isValid(betreuung, null));
	}

	@Test
	public void testAbgewiesenNullGrund() {
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung(null);
		Assert.assertFalse(validator.isValid(betreuung, null));
	}

	@Test
	public void testAbgewiesenWithGrund() {
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung("mein Grund");
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

}
