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
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.tests.util.ValidationTestHelper;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlapping;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Test fuer {@link ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlappingValidator}
 */

public class CheckBetreuungspensumDatesOverlappingValidatorTest {


	private ValidatorFactory customFactory;

	@Before
	public void setUp() throws Exception {
		// see https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/chapter-bootstrapping.html#_constraintvalidatorfactory
		Configuration<?> config = Validation.byDefaultProvider().configure();
		//wir verwenden dummy service daher geben wir hier null als em mit
		config.constraintValidatorFactory(new ValidationTestConstraintValidatorFactory(null));
		this.customFactory = config.buildValidatorFactory();
	}

	@Test
	public void testCheckBetreuungspensumDatesOverlapping() {
		Betreuung betreuung = createBetreuungWithOverlappedDates(true); //overlapping
		ValidationTestHelper.assertViolated(CheckBetreuungspensumDatesOverlapping.class, betreuung, customFactory ,"" );
	}

	@Test
	public void testCheckBetreuungspensumDatesNotOverlapping() {
		Betreuung betreuung = createBetreuungWithOverlappedDates(false); // not overlapping
		ValidationTestHelper.assertNotViolated(CheckBetreuungspensumDatesOverlapping.class, betreuung, customFactory, "");
	}

	@Nonnull
	private Betreuung createBetreuungWithOverlappedDates(boolean overlapping) {
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1718());
		Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		betreuung.getKind().setGesuch(gesuch); // Aktuell nur in 1 Richtung verknuepft
		Set<BetreuungspensumContainer> containerSet = new HashSet<>();

		BetreuungspensumContainer betPensContainer = TestDataUtil.createBetPensContainer(betreuung);
		betPensContainer.setBetreuungspensumGS(null); //wir wollen nur JA container testen
		betPensContainer.getBetreuungspensumJA().getGueltigkeit().setGueltigAb(LocalDate.of(2000, 10, 10));
		betPensContainer.getBetreuungspensumJA().getGueltigkeit().setGueltigBis(LocalDate.of(2005, 10, 10));
		containerSet.add(betPensContainer);
		BetreuungspensumContainer betPensContainer2 = TestDataUtil.createBetPensContainer(betreuung);
		betPensContainer.setBetreuungspensumGS(null);
		betPensContainer2.getBetreuungspensumJA().getGueltigkeit().setGueltigAb(overlapping ? LocalDate.of(2003, 10, 10) : LocalDate.of(2006, 10, 10));
		betPensContainer2.getBetreuungspensumJA().getGueltigkeit().setGueltigBis(LocalDate.of(2008, 10, 10));
		containerSet.add(betPensContainer2);
		betreuung.setBetreuungspensumContainers(containerSet);
		return betreuung;
	}
}
