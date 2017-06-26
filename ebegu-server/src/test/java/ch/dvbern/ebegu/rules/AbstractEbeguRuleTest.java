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
 * Tests für die Hilfsmethoden auf AbstractEbeguRule
 */
public class AbstractEbeguRuleTest {


	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final ErwerbspensumAbschnittRule erwerbspensumRule = new ErwerbspensumAbschnittRule(defaultGueltigkeit);

	private final LocalDate DATUM_1 = LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.APRIL, 1);
	private final LocalDate DATUM_2 = LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.SEPTEMBER, 1);
	private final LocalDate DATUM_3 = LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.OCTOBER, 1);
	private final LocalDate DATUM_4 = LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 1);

	@Test
	public void testErwerbspensenUndBetreuungspensen() throws Exception {

		List<VerfuegungZeitabschnitt> betreuungspensen = new ArrayList<>();
		betreuungspensen.add(createBetreuungspensum(Constants.START_OF_TIME, Constants.END_OF_TIME, 50));
		betreuungspensen.add(createBetreuungspensum(DATUM_2, DATUM_4, 20));
		betreuungspensen = erwerbspensumRule.mergeZeitabschnitte(betreuungspensen);
		// 01.01.1900 - DATUM2-1: 50, DATUM2 - DATUM4: 70, DATUM4+1 - 31.12.9999: 50

		List<VerfuegungZeitabschnitt> erwerbspensen = new ArrayList<>();
		erwerbspensen.add(createErwerbspensum(DATUM_1, DATUM_3, 40));
		erwerbspensen.add(createErwerbspensum(DATUM_2, DATUM_4, 60));
		erwerbspensen = erwerbspensumRule.mergeZeitabschnitte(erwerbspensen);
		// DATUM1 - DATUM2-1: 40, DATUM2 - DATUM3: 100, DATUM3+1 - DATUM 4: 60

		List<VerfuegungZeitabschnitt> alles = new ArrayList<>();
		alles.addAll(betreuungspensen);
		alles.addAll(erwerbspensen);
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(alles);
		// 01.01.1900 - DATUM1-1, DATUM1 - DATUM2-1, DATUM2 - DATUM3, DATUM3+1 - DATUM 4,  DATUM4+1 - 31.12.9999


		Assert.assertNotNull(result);
		Assert.assertEquals(5, result.size());
		VerfuegungZeitabschnitt first = result.get(0);
		VerfuegungZeitabschnitt second = result.get(1);
		VerfuegungZeitabschnitt third = result.get(2);
		VerfuegungZeitabschnitt fourth = result.get(3);
		VerfuegungZeitabschnitt fifth = result.get(4);

		Assert.assertEquals(Constants.START_OF_TIME, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_1.minusDays(1), first.getGueltigkeit().getGueltigBis());

		Assert.assertEquals(DATUM_1, second.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), second.getGueltigkeit().getGueltigBis());

		Assert.assertEquals(DATUM_2, third.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_3, third.getGueltigkeit().getGueltigBis());

		Assert.assertEquals(DATUM_3.plusDays(1), fourth.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, fourth.getGueltigkeit().getGueltigBis());

		Assert.assertEquals(DATUM_4.plusDays(1), fifth.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, fifth.getGueltigkeit().getGueltigBis());

		Assert.assertNull(first.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(40), second.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(100), third.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(60), fourth.getErwerbspensumGS1());
		Assert.assertNull(fifth.getErwerbspensumGS1());

		Assert.assertEquals(50, first.getBetreuungspensum());
		Assert.assertEquals(50, second.getBetreuungspensum());
		Assert.assertEquals(70, third.getBetreuungspensum());
		Assert.assertEquals(70, fourth.getBetreuungspensum());
		Assert.assertEquals(50, fifth.getBetreuungspensum());
	}

	@Test
	public void testNurEinZeitraum() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_3, 40));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(zeitabschnitte);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		VerfuegungZeitabschnitt next = result.iterator().next();
		Assert.assertEquals(DATUM_1, next.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_3, next.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(40), next.getErwerbspensumGS1());
	}

	@Test
	public void testUeberschneidung() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_3, 40));
		zeitabschnitte.add(createErwerbspensum(DATUM_2, DATUM_4, 60));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(zeitabschnitte);

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.size());
		VerfuegungZeitabschnitt first = result.get(0);
		VerfuegungZeitabschnitt second = result.get(1);
		VerfuegungZeitabschnitt third = result.get(2);
		Assert.assertEquals(DATUM_1, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), first.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_2, second.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_3, second.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_3.plusDays(1), third.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, third.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(40), first.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(100), second.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(60), third.getErwerbspensumGS1());
	}

	@Test
	public void testSchnittmenge() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createBetreuungspensum(Constants.START_OF_TIME, Constants.END_OF_TIME, 50));
		zeitabschnitte.add(createBetreuungspensum(DATUM_2, DATUM_4, 20));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(zeitabschnitte);

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.size());
		VerfuegungZeitabschnitt first = result.get(0);
		VerfuegungZeitabschnitt second = result.get(1);
		VerfuegungZeitabschnitt third = result.get(2);
		Assert.assertEquals(Constants.START_OF_TIME, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), first.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_2, second.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, second.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_4.plusDays(1), third.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, third.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(50, first.getBetreuungspensum());
		Assert.assertEquals(70, second.getBetreuungspensum());
		Assert.assertEquals(50, third.getBetreuungspensum());
	}

	@Test
	public void testGleicherStart() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_4, 80));
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_2, 40));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(zeitabschnitte);

		// Es sollte neu zwei geben
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		VerfuegungZeitabschnitt first = result.get(0);
		VerfuegungZeitabschnitt second = result.get(1);

		Assert.assertEquals(DATUM_1, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2, first.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_2.plusDays(1), second.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, second.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(120), first.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(80), second.getErwerbspensumGS1());
	}

	@Test
	public void testGleichesEnde() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_4, 80));
		zeitabschnitte.add(createErwerbspensum(DATUM_2, DATUM_4, 40));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(zeitabschnitte);

		// Es sollte neu zwei geben
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		VerfuegungZeitabschnitt first = result.get(0);
		VerfuegungZeitabschnitt second = result.get(1);

		Assert.assertEquals(DATUM_1, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), first.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_2, second.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, second.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(80), first.getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(120), second.getErwerbspensumGS1());
	}

	@Test
	public void testGleicherStartUndEnde() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_4, 80));
		zeitabschnitte.add(createErwerbspensum(DATUM_1, DATUM_4, 40));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.mergeZeitabschnitte(zeitabschnitte);

		// Es sollte neu zwei geben
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		VerfuegungZeitabschnitt first = result.get(0);

		Assert.assertEquals(DATUM_1, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, first.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(120), first.getErwerbspensumGS1());
	}

	@Test
	public void testBegrenzungAufGesuchsperiode() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(Constants.START_OF_TIME, Constants.END_OF_TIME, 80));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, zeitabschnitte);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), result.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), result.get(0).getGueltigkeit().getGueltigBis());
	}

	@Test
	public void testZusammenlegenVonIdentischenPerioden() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		// 2*20%, direkt gefolgt von 1*40% sollte 1 Abschnitt mit 40% geben
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_2, DATUM_3, 20));
		zeitabschnitte.add(createErwerbspensum(DATUM_2, DATUM_3, 20));
		zeitabschnitte.add(createErwerbspensum(DATUM_3.plusDays(1), DATUM_4, 40));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, zeitabschnitte);

		Assert.assertNotNull(result);
		// Es sind 3 Abschnitte weil es fuer die ganze Periode Zeitabschintte macht, auch fuer die Zeit in der keine
		// Erwerbspensen eingegeben wurden.
		Assert.assertEquals(3, result.size());

		Assert.assertEquals(betreuung.extractGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb(), result.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), result.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(0), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(0).getErwerbspensumGS2());

		Assert.assertEquals(DATUM_2, result.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, result.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(40), result.get(1).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(1).getErwerbspensumGS2());

		Assert.assertEquals(DATUM_4.plusDays(1), result.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(betreuung.extractGesuch().getGesuchsperiode().getGueltigkeit().getGueltigBis(), result.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(0), result.get(2).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(2).getErwerbspensumGS2());
	}

	@Test
	public void testNichtZusammenlegenVonIdentischenPeriodenMitAbstand() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		// 2*20%, direkt gefolgt von 1*40% sollte 1 Abschnitt mit 40% geben
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		zeitabschnitte.add(createErwerbspensum(DATUM_2, DATUM_3, 20));
		zeitabschnitte.add(createErwerbspensum(DATUM_2, DATUM_3, 20));
		zeitabschnitte.add(createErwerbspensum(DATUM_3.plusDays(2), DATUM_4, 40));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, zeitabschnitte);

		Assert.assertNotNull(result);
		// Es sind 5 Abschnitte: 1. kein EP, 2. 20+20, 3. wieder kein EP (02.10.), 4. 40 und 5. kein EP
		// Fuer Zeitabschnitte in denen es kein EP eingegeben wurde, wird auch ein Zeitabschnitte berechnet
		Assert.assertEquals(5, result.size());

		Assert.assertEquals(betreuung.extractGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb(), result.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), result.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(0), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(0).getErwerbspensumGS2());

		Assert.assertEquals(DATUM_2, result.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_3, result.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(40), result.get(1).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(1).getErwerbspensumGS2());

		Assert.assertEquals(DATUM_3.plusDays(1), result.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_3.plusDays(1), result.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(0), result.get(2).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(2).getErwerbspensumGS2());

		Assert.assertEquals(DATUM_3.plusDays(2), result.get(3).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, result.get(3).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(40), result.get(3).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(3).getErwerbspensumGS2());

		Assert.assertEquals(DATUM_4.plusDays(1), result.get(4).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(betreuung.extractGesuch().getGesuchsperiode().getGueltigkeit().getGueltigBis(), result.get(4).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(0), result.get(4).getErwerbspensumGS1());
		Assert.assertEquals(Integer.valueOf(0), result.get(4).getErwerbspensumGS2());
	}

	private VerfuegungZeitabschnitt createErwerbspensum(LocalDate von, LocalDate bis, int pensum) {
		VerfuegungZeitabschnitt zeitabschnitt1 = new VerfuegungZeitabschnitt(new DateRange(von, bis));
		zeitabschnitt1.setErwerbspensumGS1(pensum);
		zeitabschnitt1.setErwerbspensumGS2(0);
		return zeitabschnitt1;
	}

	private VerfuegungZeitabschnitt createBetreuungspensum(LocalDate von, LocalDate bis, int pensum) {
		VerfuegungZeitabschnitt zeitabschnitt1 = new VerfuegungZeitabschnitt(new DateRange(von, bis));
		zeitabschnitt1.setBetreuungspensum(pensum);
		return zeitabschnitt1;
	}
}
