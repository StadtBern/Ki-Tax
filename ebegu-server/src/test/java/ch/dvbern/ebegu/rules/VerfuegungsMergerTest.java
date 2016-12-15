package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;


/**
 * Tests fuer Verfügungsmuster
 */
public class VerfuegungsMergerTest {

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	private final VerfuegungsMerger verfuegungsMerger = new VerfuegungsMerger();
	private final MonatsRule monatsRule = new MonatsRule(Constants.DEFAULT_GUELTIGKEIT);


	@Test
	public void test_Reduktion_Rechtzeitig_aenderungUndEingangsdatumGleich() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(6);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 80);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		verfuegungErstgesuch.setZeitabschnitte(monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch));
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);


		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(12, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, START_PERIODE.plusMonths(6), 100);
		checkAllAfter(zeitabschnitte, START_PERIODE.plusMonths(6), 80);

	}

	@Test
	public void test_Reduktion_Rechtzeitig_aenderungNachEingangsdatum() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(7).plusDays(1);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 40);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		verfuegungErstgesuch.setZeitabschnitte(monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch));
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);

		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(12, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, aenderungsDatumPensum, 100);
		checkAllAfter(zeitabschnitte, aenderungsDatumPensum, 40);

	}

	@Test
	public void test_Reduktion_Rückwirkend_aenderungVorEingangsdatum() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(5).minusDays(1);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 40);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		verfuegungErstgesuch.setZeitabschnitte(monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch));
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);

		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(12, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, aenderungsDatumPensum, 100);
		checkAllAfter(zeitabschnitte, aenderungsDatumPensum, 40);

	}

	@Test
	public void test_Erhoehung_Rechtzeitig_aenderungUndEingangsdatumGleich() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6).minusDays(1);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(6);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, START_PERIODE, 80);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 100);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteErstgesuch = monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteErstgesuch, START_PERIODE, 80);
		verfuegungErstgesuch.setZeitabschnitte(verfuegungsZeitabschnitteErstgesuch);
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);


		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(12, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, aenderungsDatumPensum, 80);
		checkAllAfter(zeitabschnitte, aenderungsDatumPensum, 100);

	}

	@Test
	public void test_Erhoehung_Rechtzeitig_aenderungNachEingangsdatum() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(7).plusDays(1);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, START_PERIODE, 80);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 100);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteErstgesuch = monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteErstgesuch, START_PERIODE, 80);
		verfuegungErstgesuch.setZeitabschnitte(verfuegungsZeitabschnitteErstgesuch);
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);


		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(12, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, aenderungsDatumPensum, 80);
		checkAllAfter(zeitabschnitte, aenderungsDatumPensum, 100);

	}

	@Test
	public void test_Erhoehung_Nicht_Rechtzeitig_aenderungVorEingangsdatum() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6).minusDays(1);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(5).minusDays(1);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, START_PERIODE, 80);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 100);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteErstgesuch = monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteErstgesuch, START_PERIODE, 80);
		verfuegungErstgesuch.setZeitabschnitte(verfuegungsZeitabschnitteErstgesuch);
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);

		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(12, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, eingangsdatumMuation, 80);
		checkAllAfter(zeitabschnitte, eingangsdatumMuation, 100);
	}

	@Test
	public void test_Erhoehung_Rechtzeitig_aenderungNachEingangsdatum_nichtAnMonatsgrenze() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(7).plusDays(15);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, START_PERIODE, 80);
		verfuegungsZeitabschnitteMutiert = splitUpAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 100);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum.withDayOfMonth(aenderungsDatumPensum.lengthOfMonth()).plusDays(1), 100);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteErstgesuch = monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteErstgesuch, START_PERIODE, 80);
		verfuegungErstgesuch.setZeitabschnitte(verfuegungsZeitabschnitteErstgesuch);
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);

		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(13, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, aenderungsDatumPensum, 80);
		checkAllAfter(zeitabschnitte, aenderungsDatumPensum, 100);

	}

	@Test
	public void test_Erhoehung_nicht_Rechtzeitig_aenderungVorEingangsdatum_nichtAnMonatsgrenze() {

		final LocalDate eingangsdatumMuation = START_PERIODE.plusMonths(6).minusDays(1);
		final LocalDate aenderungsDatumPensum = START_PERIODE.plusMonths(5).plusDays(15);

		// Mutiertes Gesuch vorbereiten
		Betreuung mutierteBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrMutiert = EbeguRuleTestsHelper.calculate(mutierteBetreuung);
		mutierteBetreuung.extractGesuch().setEingangsdatum(eingangsdatumMuation);
		List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert = monatsRule.createVerfuegungsZeitabschnitte(mutierteBetreuung, zabetrMutiert);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, START_PERIODE, 80);
		verfuegungsZeitabschnitteMutiert = splitUpAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum, 100);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteMutiert, aenderungsDatumPensum.withDayOfMonth(aenderungsDatumPensum.lengthOfMonth()).plusDays(1), 100);

		// Erstgesuch Gesuch vorbereiten
		Betreuung erstgesuchBetreuung = prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100);
		List<VerfuegungZeitabschnitt> zabetrErtgesuch = EbeguRuleTestsHelper.calculate(erstgesuchBetreuung);
		Verfuegung verfuegungErstgesuch = new Verfuegung();
		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteErstgesuch = monatsRule.createVerfuegungsZeitabschnitte(erstgesuchBetreuung, zabetrErtgesuch);
		setzeAnsprechberechtigtesPensumAbDatum(verfuegungsZeitabschnitteErstgesuch, START_PERIODE, 80);
		verfuegungErstgesuch.setZeitabschnitte(verfuegungsZeitabschnitteErstgesuch);
		erstgesuchBetreuung.setVerfuegung(verfuegungErstgesuch);
		final Gesuch erstgesuch = erstgesuchBetreuung.extractGesuch();

		// mergen
		List<VerfuegungZeitabschnitt> zeitabschnitte = verfuegungsMerger.createVerfuegungsZeitabschnitte(mutierteBetreuung, verfuegungsZeitabschnitteMutiert, erstgesuch);

		//ueberprüfen
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(13, zeitabschnitte.size());
		checkAllBefore(zeitabschnitte, eingangsdatumMuation, 80);
		checkAllAfter(zeitabschnitte, eingangsdatumMuation, 100);

	}

	private List<VerfuegungZeitabschnitt> splitUpAnsprechberechtigtesPensumAbDatum(List<VerfuegungZeitabschnitt> zeitabschnitte, LocalDate aenderungsDatumPensum, int ansprechberechtigtesPensum) {

		List<VerfuegungZeitabschnitt> zeitabschnitteSplitted = new ArrayList<>();
		zeitabschnitte.stream().
			filter(za -> za.getGueltigkeit().endsBefore(aenderungsDatumPensum)).
			forEach(zeitabschnitteSplitted::add);

		VerfuegungZeitabschnitt zeitabschnitToSplit = zeitabschnitte.stream().
			filter(za -> za.getGueltigkeit().contains(aenderungsDatumPensum)).findFirst().get();
		VerfuegungZeitabschnitt zeitabschnitSplit1 = new VerfuegungZeitabschnitt(zeitabschnitToSplit);
		zeitabschnitSplit1.getGueltigkeit().setGueltigBis(aenderungsDatumPensum.minusDays(1));
		zeitabschnitteSplitted.add(zeitabschnitSplit1);

		VerfuegungZeitabschnitt zeitabschnitSplit2 = new VerfuegungZeitabschnitt(zeitabschnitToSplit);
		zeitabschnitSplit2.getGueltigkeit().setGueltigAb(aenderungsDatumPensum);
		zeitabschnitSplit2.setAnspruchberechtigtesPensum(ansprechberechtigtesPensum);
		zeitabschnitteSplitted.add(zeitabschnitSplit2);

		zeitabschnitte.stream().
			filter(za -> za.getGueltigkeit().startsAfter(aenderungsDatumPensum)).
			forEach(zeitabschnitteSplitted::add);

		return zeitabschnitteSplitted;
	}


	private void checkAllBefore(List<VerfuegungZeitabschnitt> zeitabschnitte, LocalDate endsBeforeOrAt, int anspruchberechtigtesPensum) {

		zeitabschnitte.stream().
			filter(za -> za.getGueltigkeit().endsBefore(endsBeforeOrAt) || za.getGueltigkeit().endsSameDay(endsBeforeOrAt)).
			forEach(za ->
				Assert.assertEquals("Falsches anspruchberechtiges Pensum in Zeitabschnitt " + za.toString(), anspruchberechtigtesPensum, za.getAnspruchberechtigtesPensum())
			);
	}

	private void checkAllAfter(List<VerfuegungZeitabschnitt> zeitabschnitte, LocalDate startAfterOrAt, int anspruchberechtigtesPensum) {
		zeitabschnitte.stream().
			filter(za -> za.getGueltigkeit().startsAfter(startAfterOrAt) || za.getGueltigkeit().startsSameDay(startAfterOrAt)).
			forEach(za ->
				Assert.assertEquals("Falsches anspruchberechtiges Pensum in Zeitabschnitt " + za.toString(), anspruchberechtigtesPensum, za.getAnspruchberechtigtesPensum())
			);
	}

	private void setzeAnsprechberechtigtesPensumAbDatum(List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitteMutiert, LocalDate datumAb, int anspruchberechtigtesPensum) {
		verfuegungsZeitabschnitteMutiert.stream()
			.filter(v -> v.getGueltigkeit().startsSameDay(datumAb) || v.getGueltigkeit().startsAfter(datumAb))
			.forEach(v -> v.setAnspruchberechtigtesPensum(anspruchberechtigtesPensum));
	}

	private Betreuung prepareData(BigDecimal massgebendesEinkommen, BetreuungsangebotTyp angebot, int pensum) {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, angebot, pensum);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		Set<KindContainer> kindContainers = new LinkedHashSet<>();
		final KindContainer kindContainer = betreuung.getKind();
		Set<Betreuung> betreuungen = new TreeSet<>();
		betreuungen.add(betreuung);
		kindContainer.setBetreuungen(betreuungen);
		kindContainers.add(betreuung.getKind());
		gesuch.setKindContainers(kindContainers);

		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.getFinanzDatenDTO().setMassgebendesEinkBjVorAbzFamGr(massgebendesEinkommen);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		return betreuung;
	}


}
