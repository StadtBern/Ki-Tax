package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Familiensituation;
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

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(oldData, newData));
	}

	@Test
	public void testFromOneGSToTwoGS_From2To2() {
		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.KONKUBINAT);

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(oldData, newData));
	}

	@Test
	public void testFromOneGSToTwoGS_From1To1() {
		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.WENIGER_FUENF_JAHRE);
		oldData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		newData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(oldData, newData));
	}

	@Test
	public void testFromOneGSToTwoGS_From1To2() {
		Familiensituation oldData = new Familiensituation();
		oldData.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		oldData.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		Familiensituation newData = new Familiensituation();
		newData.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);

		Assert.assertTrue(EbeguUtil.fromOneGSToTwoGS(oldData, newData));
	}

	@Test
	public void testFromOneGSToTwoGS_nullFamilienstatus() {
		Familiensituation oldData = new Familiensituation();
		Familiensituation newData = new Familiensituation();

		Assert.assertFalse(EbeguUtil.fromOneGSToTwoGS(oldData, newData));
	}
}
