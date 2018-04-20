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

package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import org.junit.Assert;
import org.junit.Test;

/**
 * test fuer Ebeguutil
 */
public class EbeguUtilTest {

	@Test
	public void testFromOneGSToTwoGS_From2To1() {

		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		newData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		FamiliensituationContainer fsc = new FamiliensituationContainer();
		fsc.setFamiliensituationErstgesuch(oldData);
		fsc.setFamiliensituationJA(newData);

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(fsc));
	}

	@Test
	public void testFromOneGSToTwoGS_From2To2() {
		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.KONKUBINAT);

		FamiliensituationContainer fsc = new FamiliensituationContainer();
		fsc.setFamiliensituationErstgesuch(oldData);
		fsc.setFamiliensituationJA(newData);

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(fsc));
	}

	@Test
	public void testFromOneGSToTwoGS_From1To1() {
		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.WENIGER_FUENF_JAHRE);
		oldData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		newData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);

		FamiliensituationContainer fsc = new FamiliensituationContainer();
		fsc.setFamiliensituationErstgesuch(oldData);
		fsc.setFamiliensituationJA(newData);

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(fsc));
	}

	@Test
	public void testFromOneGSToTwoGS_From1To2() {
		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		oldData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);

		FamiliensituationContainer fsc = new FamiliensituationContainer();
		fsc.setFamiliensituationErstgesuch(oldData);
		fsc.setFamiliensituationJA(newData);

		Assert.assertTrue(EbeguUtil.fromOneGSToTwoGS(fsc));
	}

	@Test
	public void testFromOneGSToTwoGS_nullFamilienstatus() {
		Familiensituation oldData = new Familiensituation();
		Familiensituation newData = new Familiensituation();

		FamiliensituationContainer fsc = new FamiliensituationContainer();
		fsc.setFamiliensituationErstgesuch(oldData);
		fsc.setFamiliensituationJA(newData);

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(fsc));
	}
}
