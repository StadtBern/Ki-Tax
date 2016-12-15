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
