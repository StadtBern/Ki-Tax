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

package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Month;

/**
 * Tests fuer AbwesenheitCalcRule
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class AbwesenheitCalcRuleTest {

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);
	private final DateRange PERIODE = new DateRange(START_PERIODE, ENDE_PERIODE);


	@Test
	public void testSchulamtBetreuungWithAbwesenheit() {
		final AbwesenheitCalcRule rule = new AbwesenheitCalcRule(PERIODE);
		final VerfuegungZeitabschnitt zeitAbschnitt = createZeitabschnitt(true);
		final Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESSCHULE);

		rule.executeRule(betreuung, zeitAbschnitt);

		Assert.assertFalse(zeitAbschnitt.isBezahltVollkosten());
		Assert.assertEquals("", zeitAbschnitt.getBemerkungen());
	}

	@Test
	public void testJABetreuungWithAbwesenheit() {
		final AbwesenheitCalcRule rule = new AbwesenheitCalcRule(PERIODE);
		final VerfuegungZeitabschnitt zeitAbschnitt = createZeitabschnitt(true);
		final Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);

		rule.executeRule(betreuung, zeitAbschnitt);

		Assert.assertTrue(zeitAbschnitt.isBezahltVollkosten());
		Assert.assertEquals("ABWESENHEIT: Ab dem 31. Tag einer Abwesenheit (Krankheit oder Unfall " +
			"des Kinds und bei Mutterschaft ausgeschlossen) entfällt der Gutschein.", zeitAbschnitt.getBemerkungen());

	}

	@Test
	public void testJABetreuungWithoutAbwesenheit() {
		final AbwesenheitCalcRule rule = new AbwesenheitCalcRule(PERIODE);
		final VerfuegungZeitabschnitt zeitAbschnitt = createZeitabschnitt(false);
		final Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);

		rule.executeRule(betreuung, zeitAbschnitt);

		Assert.assertFalse(zeitAbschnitt.isBezahltVollkosten());
		Assert.assertEquals("", zeitAbschnitt.getBemerkungen());
	}


	// HELP METHODS

	@Nonnull
	private VerfuegungZeitabschnitt createZeitabschnitt(boolean abwesend) {
		final VerfuegungZeitabschnitt zeitAbschnitt = new VerfuegungZeitabschnitt();
		zeitAbschnitt.setGueltigkeit(PERIODE);
		zeitAbschnitt.setLongAbwesenheit(abwesend);
		return zeitAbschnitt;
	}
}
