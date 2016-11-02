package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.rules.initalizer.RestanspruchInitializer;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static ch.dvbern.ebegu.rules.EbeguRuleTestsHelper.calculate;
import static ch.dvbern.ebegu.rules.EbeguRuleTestsHelper.calculateWithRemainingRestanspruch;

/**
 * Tests für Betreuungspensum-Regel
 */
public class BetreuungspensumRuleTest {


	private final RestanspruchInitializer restanspruchInitializer = new RestanspruchInitializer();

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	@Test
	public void testKitaNormalfall() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, result);
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaZuwenigAnspruch() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 80);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, result);
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaMitRestanspruch() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(80), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, result);
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testZweiKitas() {
		Betreuung betreuung1 = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		Betreuung betreuung2 = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 40);
		betreuung1.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung1);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(80), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());  //restanspruch wurde noch nie berechnet
		// Anspruchsrest fuer naechste Betreuung setzten
		List<VerfuegungZeitabschnitt> abschnForNxtBetr = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung1, result);
		//Nach dem Berechnen des Rests ist der Rest im  im Feld AnspruchspensumRest gesetzt, Anspruchsberechtigtes Pensum ist noch 0 da noch nciht berechnet fuer 2.Betr.
		Assert.assertEquals(20, abschnForNxtBetr.get(0).getAnspruchspensumRest());
		Assert.assertEquals(0, abschnForNxtBetr.get(0).getAnspruchberechtigtesPensum());

		// Kita 2: Reicht nicht mehr ganz
		betreuung2.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> resultBetr2 = calculateWithRemainingRestanspruch(betreuung2, 20);


		Assert.assertNotNull(resultBetr2);
		Assert.assertEquals(1, resultBetr2.size());
		Assert.assertEquals(Integer.valueOf(80), resultBetr2.get(0).getErwerbspensumGS1());
		Assert.assertEquals(40, resultBetr2.get(0).getBetreuungspensum());
		Assert.assertEquals(20, resultBetr2.get(0).getAnspruchberechtigtesPensum()); // Nach der Berechnung des Anspruchs kann der Anspruch nicht hoeher sein als der Restanspruch (20)
		Assert.assertEquals(20, resultBetr2.get(0).getBgPensum());
		Assert.assertEquals(20, resultBetr2.get(0).getAnspruchspensumRest()); //Restanspruch wurde noch nicht neu berechnet fuer naechste betreuung
		resultBetr2 = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung2, resultBetr2);
		Assert.assertEquals(0, resultBetr2.get(0).getAnspruchspensumRest()); // Nach dem initialisieren fuer das nachste Betreuungspensum ist der noch verbleibende restanspruch 0
	}

	@Test
	public void testRestanspruchKitaTagiKita() {
		//Teste ob der Restanspruch richtig berechnet wird wenn die erste Betreuung eine Kita ist, gefolgt von einer Tagi fuer Schulkinder, gefolgt von einer Kita
		Betreuung betreuung1 = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		Betreuung betreuung2 = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND, 40);
		Betreuung betreuung3 = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 40);
		betreuung1.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung1);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(80), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());  //restanspruch wurde noch nie berechnet
		// Anspruchsrest fuer naechste Betreuung setzten
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung1, result);
		//Nach dem Berechnen des Rests ist der Rest im  im Feld AnspruchspensumRest gesetzt, Anspruchsberechtigtes Pensum ist noch 0 da noch nciht berechnet fuer 2.Betr.
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());

		//Tagi fuer Schulkinder, Restanspruch bleibt gleich
		betreuung2.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		result = calculateWithRemainingRestanspruch(betreuung2, 20);
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());  //restanspruch ist immer noch 20%
		// Anspruchsrest fuer naechste Betreuung setzten
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung2, result);
		//Nach dem Berechnen des Rests ist dieser immer noch gleich gross da Tagi fuer Schulkinder keinen Einfluss hat
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());

		// Kita 2: Reicht nicht mehr ganz
		betreuung3.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		result = calculateWithRemainingRestanspruch(betreuung3, 20);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(80), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(40, result.get(0).getBetreuungspensum());
		Assert.assertEquals(20, result.get(0).getAnspruchberechtigtesPensum()); // Nach der Berechnung des Anspruchs kann der Anspruch nicht hoeher sein als der Restanspruch (20)
		Assert.assertNotNull(result.get(0).getBemerkungen());
		Assert.assertTrue(result.get(0).getBemerkungen().contains("RESTANSPRUCH"));
		Assert.assertEquals(20, result.get(0).getBgPensum());
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest()); //Restanspruch wurde noch nicht neu berechnet fuer naechste betreuung
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung3, result);
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest()); // Nach dem initialisieren fuer das nachste Betreuungspensum ist der noch verbleibende restanspruch 0
	}


	@Test
	public void testTageselternKleinkinderOhneErwerbspensum() {
		// Tageseltern Kleinkind haben die gleichen Regeln wie Kita
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND, 80);
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(null, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, result);
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testTageselternSchulkinderMitTiefemErwerbspensum() {
		// Tageseltern Schulkinder erhalten immer soviel wie sie wollen, unabhängig von Erwerbspensum
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND, 80);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());     //will 80
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());     // kriegt 80
		Assert.assertEquals(80, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, result);
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());  //Tagi Schulkinder aendern Restanspruch nicht
	}

	@Test
	public void testTageselternKleinkinderMitTiefemErwerbspensum() {
		// Tageseltern Kleinkinder haben die gleichen Regeln wie Kita
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND, 80);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		result = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, result);
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}
}
