package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * Testet den Tageseltern-Rechner
 */
public class TageselternRechnerTest extends AbstractBGRechnerTest {

	private BGRechnerParameterDTO parameterDTO = getParameter();
	private TageselternRechner tageselternRechner = new TageselternRechner();

	@Test
	public void testHalberMonatHohesEinkommenAnspruch15() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 21),
			15, new BigDecimal("234567"));

		VerfuegungZeitabschnitt calculate = tageselternRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("15.30"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("3.35"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("11.95"), calculate.getVerguenstigung());
	}

	@Test
	public void testHalberMonatMittleresEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("87654"));

		VerfuegungZeitabschnitt calculate = tageselternRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("713.95"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("313.05"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("400.90"), calculate.getVerguenstigung());
	}

	@Test
	public void testGanzerMonatZuWenigEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 1), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("27750"));

		VerfuegungZeitabschnitt calculate = tageselternRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("2141.90"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("175.40"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("1966.50"), calculate.getVerguenstigung());
	}
}
