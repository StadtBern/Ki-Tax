package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * Testet den Kita-Rechner
 */
public class KitaRechnerTest extends AbstractBGRechnerTest {

	private BGRechnerParameterDTO parameterDTO = getParameter();
	private KitaRechner kitaRechner = new KitaRechner();

	@Test
	public void testHalberMonatHohesEinkommenKitaLangeOffenAnspruch15() {
		Verfuegung verfuegung = prepareVerfuegungKita(LocalDate.of(2014, Month.AUGUST, 1),
			new BigDecimal("260"), new BigDecimal("13"),
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 21),
			15, new BigDecimal("234567"));

		VerfuegungZeitabschnitt calculate = kitaRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("16.60"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("15.55"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("1.05"), calculate.getVerguenstigung());
	}

	@Test
	public void testHalberMonatHohesEinkommenKitaLangeOffenBabyAnspruch80() {
		Verfuegung verfuegung = prepareVerfuegungKita(LocalDate.of(2015, Month.AUGUST, 1),
			new BigDecimal("260"), new BigDecimal("13"),
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 27),
			80, new BigDecimal("234567"));

		VerfuegungZeitabschnitt calculate = kitaRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("663.40"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("415.15"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("248.25"), calculate.getVerguenstigung());
	}

	@Test
	public void testHalberMonatMittleresEinkommenBaby() {
		Verfuegung verfuegung = prepareVerfuegungKita(LocalDate.of(2015, Month.AUGUST, 1),
			new BigDecimal("200"), new BigDecimal("9"),
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("87654"));

		VerfuegungZeitabschnitt calculate = kitaRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("744.70"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("198.95"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("545.75"), calculate.getVerguenstigung());
	}

	@Test
	public void testGanzerMonatZuWenigEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungKita(LocalDate.of(2014, Month.AUGUST, 1),
			new BigDecimal("244"), new BigDecimal("11.5"),
			LocalDate.of(2016, Month.JANUARY, 1), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("27750"));

		VerfuegungZeitabschnitt calculate = kitaRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("2321.85"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("137.25"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("2184.60"), calculate.getVerguenstigung());
	}
}
