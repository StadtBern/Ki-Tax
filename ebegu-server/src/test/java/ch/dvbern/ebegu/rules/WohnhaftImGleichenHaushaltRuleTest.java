package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Tests fuer die Regel WohnhaftImGleichenHaushaltRule
 */
public class WohnhaftImGleichenHaushaltRuleTest {

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	/**
	 * Wenn WohnhaftImGleichenHaushalt nicht eingegeben wurde (null), wird der Wert nicht ueberschrieben
	 */
	@Test
	public void testWohnhaftImGleichenHaushaltNullValue() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(100, null, 0));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testWohnhaftImGleichenHaushalt() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(100, 25, 0));

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
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(12, 26, 0));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(10, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	/**
	 * Wenn mehr im gleichen Haushalt als die Fachstelle attestiert, gilt die Fachstelle
	 */
	@Test
	public void testWohnhaftImGleichenHaushaltHoeherAlsFachstelle() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(100, 30, 20));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(20, result.get(0).getAnspruchberechtigtesPensum());
	}

	/**
	 * Wenn weniger im gleichen Haushalt als die Fachstelle attestiert, gilt der gleiche Haushalt
	 */
	@Test
	public void testWohnhaftImGleichenHaushaltTieferAlsFachstelle() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(100, 30, 40));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(30, result.get(0).getAnspruchberechtigtesPensum());
	}


	private Betreuung prepareData(final int pensum, final Integer prozentImGleichemHaushalt, int fachstelle) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		betreuung.getKind().getKindJA().setWohnhaftImGleichenHaushalt(prozentImGleichemHaushalt);
		if (fachstelle > 0) {
			betreuung.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
			betreuung.getKind().getKindJA().getPensumFachstelle().setPensum(fachstelle);
			betreuung.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(new DateRange(START_PERIODE, ENDE_PERIODE));
		}
		betreuung.extractGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, pensum, 0));
		return betreuung;
	}
}
