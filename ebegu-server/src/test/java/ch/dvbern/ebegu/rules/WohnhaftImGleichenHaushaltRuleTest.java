package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests fuer die Regel WohnhaftImGleichenHaushaltRule
 */
public class WohnhaftImGleichenHaushaltRuleTest {

	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final WohnhaftImGleichenHaushaltRule wohnhaftImGleichenHaushaltRule = new WohnhaftImGleichenHaushaltRule(defaultGueltigkeit);
	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	/**
	 * Wenn WohnhaftImGleichenHaushalt nicht eingegeben wurde (null), wird der Wert nicht ueberschrieben
	 */
	@Test
	public void testWohnhaftImGleichenHaushaltNullValue() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuung, 100, null);

		List<VerfuegungZeitabschnitt> result = wohnhaftImGleichenHaushaltRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testWohnhaftImGleichenHaushalt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuung, 100, 25);

		List<VerfuegungZeitabschnitt> result = wohnhaftImGleichenHaushaltRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(30, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals("WOHNHAFT_IM_GLEICHEN_HAUSHALT: Das Kind wohnt 25% im gleichen Haushalt", result.get(0).getBemerkungen());
	}

	/**
	 * Der Wert vom AnspruchberechtigtesPensum darf nicht aktualisiert werden, da er bereits kleiner ist als der Wert
	 * von wohnhaftImGleichenHaushalt.
	 */
	@Test
	public void testWohnhaftImGleichenHaushaltHoeherWert() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuung, 12, 26);

		List<VerfuegungZeitabschnitt> result = wohnhaftImGleichenHaushaltRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(10, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}


	private List<VerfuegungZeitabschnitt> prepareData(Betreuung betreuung, final int pensum, final Integer prozentImGleichemHaushalt) {
		Gesuch gesuch = betreuung.extractGesuch();
		betreuung.getKind().getKindJA().setWohnhaftImGleichenHaushalt(prozentImGleichemHaushalt);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, pensum, 0));
		return erwerbspensumRule.calculate(betreuung, new ArrayList<>());
	}
}
