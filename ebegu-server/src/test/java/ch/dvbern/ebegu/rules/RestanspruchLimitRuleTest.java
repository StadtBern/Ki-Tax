package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.apache.commons.lang3.Validate;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static ch.dvbern.ebegu.rules.EbeguRuleTestsHelper.calculate;
import static ch.dvbern.ebegu.rules.EbeguRuleTestsHelper.calculateWithRemainingRestanspruch;

/**
 * Tests für {@link RestanspruchLimitCalcRule}
 */
public class RestanspruchLimitRuleTest {


	@Test
	public void testRestanspruchInitForKita() {

		List<VerfuegungZeitabschnitt> restansprchZeitabschnittList = initZeitabschnitteForSecondBetreuung(100, -1, 30, BetreuungsangebotTyp.KITA);

		Assert.assertNotNull(restansprchZeitabschnittList);
		Assert.assertEquals(1, restansprchZeitabschnittList.size());
		VerfuegungZeitabschnitt nextInitialabschnitt = restansprchZeitabschnittList.get(0);
		//hat anspruch 100, braucht 30, fuer die naechste betreuung bleibt 70
		Assert.assertEquals(70, nextInitialabschnitt.getAnspruchspensumRest());

	}

	@Test
	public void testRestanspruchZeroForKita() {
		List<VerfuegungZeitabschnitt> restansprchZeitabschnittList = initZeitabschnitteForSecondBetreuung(50, -1, 80, BetreuungsangebotTyp.KITA);
		Assert.assertNotNull(restansprchZeitabschnittList);
		Assert.assertEquals(1, restansprchZeitabschnittList.size());
		VerfuegungZeitabschnitt nextInitialabschnitt = restansprchZeitabschnittList.get(0);
		Assert.assertEquals(0, nextInitialabschnitt.getAnspruchspensumRest());

	}

	@Test
	public void testExistingRestanspruchConsideredForKita() {
		List<VerfuegungZeitabschnitt> restansprchZeitabschnittList = initZeitabschnitteForSecondBetreuung(100, 50, 30, BetreuungsangebotTyp.KITA);
		Assert.assertNotNull(restansprchZeitabschnittList);
		Assert.assertEquals(1, restansprchZeitabschnittList.size());
		VerfuegungZeitabschnitt nextInitialabschnitt = restansprchZeitabschnittList.get(0);
		// hat von seinen 100% anspruch 50 verbraucht, nun hat er 30 prozent betreuung in kita, der Restanspruch fuer die nachste Betreuung ist also 20
		Assert.assertEquals(20, nextInitialabschnitt.getAnspruchspensumRest());

	}


	@Test
	public void testRestanspruchNoChangeForTagi() {

		List<VerfuegungZeitabschnitt> restansprchZeitabschnittList = initZeitabschnitteForSecondBetreuung(100, -1, 30, BetreuungsangebotTyp.TAGI);

		Assert.assertNotNull(restansprchZeitabschnittList);
		Assert.assertEquals(1, restansprchZeitabschnittList.size());
		VerfuegungZeitabschnitt nextInitialabschnitt = restansprchZeitabschnittList.get(0);
		Assert.assertEquals(-1, nextInitialabschnitt.getAnspruchspensumRest());

	}

	@Test
	public void testRestanspruchNoChangeForTagiIfAlreadyPresent() {

		List<VerfuegungZeitabschnitt> restansprchZeitabschnittList = initZeitabschnitteForSecondBetreuung(100, 50, 30, BetreuungsangebotTyp.TAGI);

		Assert.assertNotNull(restansprchZeitabschnittList);
		Assert.assertEquals(1, restansprchZeitabschnittList.size());
		VerfuegungZeitabschnitt nextInitialabschnitt = restansprchZeitabschnittList.get(0);
		//hat in der "1." betreuung von seinen 100 prozent anspruch 50 verbraucht, die 30% tagibetrauung aendern den restanspruch aber nicht
		Assert.assertEquals(50, nextInitialabschnitt.getAnspruchspensumRest());

	}

	@Test
	public void testRestansprucAlreadyZero() {

		List<VerfuegungZeitabschnitt> restansprchZeitabschnittList = initZeitabschnitteForSecondBetreuung(70, 0, 30, BetreuungsangebotTyp.KITA);

		Assert.assertNotNull(restansprchZeitabschnittList);
		Assert.assertEquals(1, restansprchZeitabschnittList.size());
		VerfuegungZeitabschnitt nextInitialabschnitt = restansprchZeitabschnittList.get(0);
		//hat in der "1." betreuung von seinen 70 prozent anspruch 70 verbraucht, nun macht er eine neue Betreuung Kita fuer 30% -> Restanspruch bleibt 0
		Assert.assertEquals(0, nextInitialabschnitt.getAnspruchspensumRest());

	}

	private List<VerfuegungZeitabschnitt> initZeitabschnitteForSecondBetreuung(int arbeitspensum, int remainingRestanspruch, int betreuungspensum, BetreuungsangebotTyp type) {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, type, betreuungspensum);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, arbeitspensum, 0));
		List<VerfuegungZeitabschnitt> calculatedAbschnitte;
		if (remainingRestanspruch != -1) {
			//simulates the already existing another betreuung and we have just remainingRestanspruch left for this calculation,
			int availableRestanspruch = Math.max(0, arbeitspensum - betreuungspensum);
			Validate.isTrue(availableRestanspruch >= remainingRestanspruch, "Invalid Testdata: Can not define a restanspruch usage bigger than max available restanspruch");
			calculatedAbschnitte = calculateWithRemainingRestanspruch(betreuung, remainingRestanspruch);
		} else {
			calculatedAbschnitte = calculate(betreuung);
		}
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, calculatedAbschnitte);
		return zeitabschnittList;
	}
}
