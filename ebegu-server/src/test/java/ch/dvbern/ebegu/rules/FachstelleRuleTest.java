package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Tests für Fachstellen-Regel
 */
public class FachstelleRuleTest {

	private final ErwerbspensumAbschnittRule erwerbspensumAbschnittRule = new ErwerbspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private final ErwerbspensumCalcRule erwerbspensumCalcRule = new ErwerbspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private final FachstelleAbschnittRule fachstelleAbschnittRule = new FachstelleAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private final FachstelleCalcRule fachstelleCalcRule = new FachstelleCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private final BetreuungspensumAbschnittRule betreuungspensumAbschnittRule = new BetreuungspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private final BetreuungspensumCalcRule betreuungspensumCalcRule = new BetreuungspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private final RestanspruchCalcRule restanspruchCalcRule = new RestanspruchCalcRule(Constants.DEFAULT_GUELTIGKEIT);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	@Test
	public void testKitaMitFachstelleWenigerAlsPensum() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
		betreuung.getKind().getKindJA().getPensumFachstelle().setPensum(40);
		betreuung.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(new DateRange(START_PERIODE, ENDE_PERIODE));
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(80, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(40, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(40, result.get(0).getBgPensum());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaMitFachstelleUndRestPensum() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
		betreuung.getKind().getKindJA().getPensumFachstelle().setPensum(80);
		betreuung.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(new DateRange(START_PERIODE, ENDE_PERIODE));
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 40, 0));
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(40, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());
	}

	private Betreuung createBetreuungWithPensum(LocalDate von, LocalDate bis, BetreuungsangebotTyp angebot, int pensum) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(angebot);
		betreuung.setBetreuungspensumContainers(new LinkedHashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		DateRange gueltigkeit = new DateRange(von, bis);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum(gueltigkeit));
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		return betreuung;
	}
}
