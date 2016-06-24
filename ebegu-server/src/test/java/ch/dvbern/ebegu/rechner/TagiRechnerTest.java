package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * Testet den Tagi-Rechner
 */
public class TagiRechnerTest extends AbstractBGRechnerTest {

	private BGRechnerParameterDTO parameterDTO = getParameter();
	private TagiRechner tagiRechner = new TagiRechner();

	@Test
	public void testHalberMonatHohesEinkommenAnspruch15() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 21),
			15, new BigDecimal("234567"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("11.90"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("2.40"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("9.50"), calculate.getVerguenstigung());
	}

	@Test
	public void testHalberMonatMittleresEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("87654"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("555.80"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("237.30"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("318.50"), calculate.getVerguenstigung());
	}

	@Test
	public void testGanzerMonatZuWenigEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 1), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("27750"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("1667.40"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("105.00"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("1562.40"), calculate.getVerguenstigung());
	}
}
