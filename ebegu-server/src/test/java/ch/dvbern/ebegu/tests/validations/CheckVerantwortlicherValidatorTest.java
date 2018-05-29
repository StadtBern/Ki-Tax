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

package ch.dvbern.ebegu.tests.validations;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.tests.util.ValidationTestHelper;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validationgroups.ChangeVerantwortlicherJAValidationGroup;
import ch.dvbern.ebegu.validationgroups.ChangeVerantwortlicherSCHValidationGroup;
import ch.dvbern.ebegu.validators.CheckVerantwortlicherJA;
import ch.dvbern.ebegu.validators.CheckVerantwortlicherSCH;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class CheckVerantwortlicherValidatorTest {

	private ValidatorFactory customFactory = null;
	private Benutzer schUser = null;
	private Benutzer jaUser = null;
	private Benutzer jaAdmin = null;
	private Benutzer schAdmin = null;

	@Before
	public void setUp() {
		// see https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/chapter-bootstrapping.html#_constraintvalidatorfactory
		Configuration<?> config = Validation.byDefaultProvider().configure();
		//wir verwenden dummy service daher geben wir hier null als em mit
		config.constraintValidatorFactory(new ValidationTestConstraintValidatorFactory(null));
		this.customFactory = config.buildValidatorFactory();
		schUser = TestDataUtil.createBenutzer(UserRole.SCHULAMT, "userSCH", null, null, null);
		jaUser = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "userJA", null, null, null);
		jaAdmin = TestDataUtil.createBenutzer(UserRole.ADMIN, "adminJA", null, null, null);
		schAdmin = TestDataUtil.createBenutzer(UserRole.ADMINISTRATOR_SCHULAMT, "adminSCH", null, null, null);
	}

	@Test
	public void testCheckVerantwortlicherNormalUsers() {
		final Fall fall = new Fall();
		fall.setVerantwortlicher(jaUser);
		fall.setVerantwortlicherSCH(schUser);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherJA.class, fall, customFactory, ChangeVerantwortlicherJAValidationGroup.class);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherSCH.class, fall, customFactory, ChangeVerantwortlicherSCHValidationGroup.class);
	}

	@Test
	public void testCheckVerantwortlicherAdminUsers() {
		final Fall fall = new Fall();
		fall.setVerantwortlicher(jaAdmin);
		fall.setVerantwortlicherSCH(schAdmin);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherJA.class, fall, customFactory, ChangeVerantwortlicherJAValidationGroup.class);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherSCH.class, fall, customFactory, ChangeVerantwortlicherSCHValidationGroup.class);
	}

	@Test
	public void testCheckVerantwortlicherNullUsers() {
		final Fall fall = new Fall();
		fall.setVerantwortlicher(null);
		fall.setVerantwortlicherSCH(null);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherJA.class, fall, customFactory, ChangeVerantwortlicherJAValidationGroup.class);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherSCH.class, fall, customFactory, ChangeVerantwortlicherSCHValidationGroup.class);
	}

	@Test
	public void testCheckVerantwortlicherWrongSCHUser() {
		final Fall fall = new Fall();
		fall.setVerantwortlicher(schAdmin);
		fall.setVerantwortlicherSCH(schAdmin);
		ValidationTestHelper.assertViolated(CheckVerantwortlicherJA.class, fall, customFactory, ChangeVerantwortlicherJAValidationGroup.class);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherSCH.class, fall, customFactory, ChangeVerantwortlicherSCHValidationGroup.class);
	}

	@Test
	public void testCheckVerantwortlicherWrongJAUser() {
		final Fall fall = new Fall();
		fall.setVerantwortlicher(jaAdmin);
		fall.setVerantwortlicherSCH(jaAdmin);
		ValidationTestHelper.assertNotViolated(CheckVerantwortlicherJA.class, fall, customFactory, ChangeVerantwortlicherJAValidationGroup.class);
		ValidationTestHelper.assertViolated(CheckVerantwortlicherSCH.class, fall, customFactory, ChangeVerantwortlicherSCHValidationGroup.class);
	}

}
