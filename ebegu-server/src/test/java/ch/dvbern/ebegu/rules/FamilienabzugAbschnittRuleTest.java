/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.rules;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests fuer FamilienabzugAbschnittRule
 */
public class FamilienabzugAbschnittRuleTest {

	private final BigDecimal pauschalabzugProPersonFamiliengroesse3 = BigDecimal.valueOf(3800);
	private final BigDecimal pauschalabzugProPersonFamiliengroesse4 = BigDecimal.valueOf(5960);
	private final BigDecimal pauschalabzugProPersonFamiliengroesse5 = BigDecimal.valueOf(7040);
	private final BigDecimal pauschalabzugProPersonFamiliengroesse6 = BigDecimal.valueOf(7580);

	private static final double DELTA = 1e-15;
	public static final LocalDate DATE_2005 = LocalDate.of(2005, 12, 31);

	private final FamilienabzugAbschnittRule famabAbschnittRule =
		new FamilienabzugAbschnittRule(Constants.DEFAULT_GUELTIGKEIT, pauschalabzugProPersonFamiliengroesse3,
			pauschalabzugProPersonFamiliengroesse4, pauschalabzugProPersonFamiliengroesse5, pauschalabzugProPersonFamiliengroesse6);

	@Test
	public void test2PKeinAbzug() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<>());
		final KindContainer defaultKindContainer = TestDataUtil.createDefaultKindContainer();
		gesuch.getKindContainers().add(defaultKindContainer);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt = zeitabschnitte.iterator().next();
		Assert.assertEquals(0, verfuegungZeitabschnitt.getAbzugFamGroesse().compareTo(BigDecimal.ZERO));
	}

	@Test
	public void test3P_Abzug() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt = zeitabschnitte.iterator().next();
		Assert.assertEquals(0, verfuegungZeitabschnitt.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse3.multiply(BigDecimal.valueOf(3))));
	}

	@Test
	public void test4P_Abzug() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt = zeitabschnitte.iterator().next();
		Assert.assertEquals(0, verfuegungZeitabschnitt.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse4.multiply(BigDecimal.valueOf(4))));
	}

	@Test
	public void test3P_Abzug_Kind_waehrendPeriode() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();
		final LocalDate geburtsdatum = LocalDate.of(TestDataUtil.PERIODE_JAHR_2, 1, 10);
		defaultKindContainer2.getKindJA().setGeburtsdatum(geburtsdatum);

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());

		final Iterator<VerfuegungZeitabschnitt> iterator = zeitabschnitte.iterator();
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt1 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse3.multiply(BigDecimal.valueOf(3))));
		final LocalDate withDayOfMonth = geburtsdatum.plusMonths(1).withDayOfMonth(1);
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getGueltigkeit().getGueltigBis().compareTo(
			withDayOfMonth.minusDays(1)));

		final VerfuegungZeitabschnitt verfuegungZeitabschnitt2 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse4.multiply(BigDecimal.valueOf(4))));
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getGueltigkeit().getGueltigAb().compareTo(withDayOfMonth));
	}

	@Test
	public void test3P_Abzug_Zwiling_waehrendPeriode() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();
		final LocalDate geburtsdatum = LocalDate.of(TestDataUtil.PERIODE_JAHR_2, 1, 10);
		defaultKindContainer2.getKindJA().setGeburtsdatum(geburtsdatum);

		final KindContainer defaultKindContainer3 = TestDataUtil.createDefaultKindContainer();
		defaultKindContainer3.getKindJA().setGeburtsdatum(geburtsdatum);

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);
		gesuch.getKindContainers().add(defaultKindContainer3);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());

		final Iterator<VerfuegungZeitabschnitt> iterator = zeitabschnitte.iterator();
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt1 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse3.multiply(BigDecimal.valueOf(3))));
		final LocalDate withDayOfMonth = geburtsdatum.plusMonths(1).withDayOfMonth(1);
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getGueltigkeit().getGueltigBis().compareTo(
			withDayOfMonth.minusDays(1)));

		final VerfuegungZeitabschnitt verfuegungZeitabschnitt2 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse5.multiply(BigDecimal.valueOf(5))));
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getGueltigkeit().getGueltigAb().compareTo(withDayOfMonth));
	}

	@Test
	public void testCalculateFamiliengroesseNullGesuch() {
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(null, null).getKey();
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNullDate() {
		Gesuch gesuch = new Gesuch();
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, null).getKey();
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNoGesuchsteller() {
		Gesuch gesuch = new Gesuch();
		gesuch.setFall(TestDataUtil.createDefaultFall());
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1718());
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005).getKey();
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseOneGesuchsteller() {
		Gesuch gesuch = createGesuchWithOneGS();
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005).getKey();
		Assert.assertEquals(1, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseOneGesuchstellerErstges() {
		Gesuch gesuch = createGesuchWithOneGS();
		//aktuell alleinerziehend
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005).getKey();
		Assert.assertEquals(1, familiengroesse, DELTA);
		// jetzt wechseln auf verheiratet
		Familiensituation erstFamiliensituation = new Familiensituation();
		erstFamiliensituation.setAenderungPer(null); //im erstgesuch immer null
		erstFamiliensituation.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		erstFamiliensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(erstFamiliensituation);
		double newFamGr = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005).getKey();
		Assert.assertEquals(2, newFamGr, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseTwoGesuchsteller() {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005).getKey();
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithGanzerAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG, null, LocalDate.of(2006, 5, 25));

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now()).getKey();
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithHalberAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.HALBER_ABZUG, null, LocalDate.of(2006, 5, 25));

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now()).getKey();
		Assert.assertEquals(2.5, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeinAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEIN_ABZUG, null, LocalDate.of(2006, 5, 25));

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now()).getKey();
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithWrongGeburtsdatum() {
		//das Kind war noch nicht geboren, innerhalb der Gesuchsperiode. So it cannot be counted for the abschnitt
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG, null, LocalDate.of(2017, 10, 25));

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.of(2017, 9, 25))
			.getKey();
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithGeburtsdatumAusserhalbPeriode() {
		// The Kind was born before the period start but after the date given as parameter. In this case the actual date
		// is not important because within the period the Kind already exists and the familiy has already changed, so
		// the Kind must be counted as well and the familiengroesse must be 3
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG, null, LocalDate.of(2015, 10, 25));

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.of(2014, 9, 25))
			.getKey();
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithCorrectGeburtsdatum() {
		//das Kind war schon geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG, null, LocalDate.of(2006, 5, 25));

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now()).getKey();
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithTwoKinder() {
		//das Kind war schon geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG, Kinderabzug.HALBER_ABZUG, LocalDate.of(2006, 5, 25));

		final Entry<Double, Integer> famGroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		double familiengroesse = famGroesse.getKey();
		double familienMitglieder = famGroesse.getValue();
		Assert.assertEquals(3.5, familiengroesse, DELTA);
		Assert.assertEquals(4, familienMitglieder, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithTwoKinderKeinAbzug() {
		//das Kind war schon geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.HALBER_ABZUG, Kinderabzug.KEIN_ABZUG, LocalDate.of(2006, 5, 25));

		final Entry<Double, Integer> famGroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		double familiengroesse = famGroesse.getKey();
		double familienMitglieder = famGroesse.getValue();
		Assert.assertEquals(2.5, familiengroesse, DELTA);
		Assert.assertEquals(3, familienMitglieder, DELTA);
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseZero() {
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(0, 0).intValue());
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(1, 1).intValue());
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(1.5, 2).intValue());
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesse_EBEGU_1185_NR32_Familiengroesse_Berechnung() {

		/* Beispiel Nr. 1:
		 * 1 Erwachsene Person (Alleinerziehend) und 1 Kind zu 50% in den Steuern abzugsberechtigt. Die Anzahl der Personen,
		 * die im Haushalt wohnen, beträgt zwei, die anrechenbare Familiengrösse ist 1,5. Es ist damit kein Abzug möglich,
		 * da 2-Personenhaushalt. Daher Fr. 0.00 in der Berechnung.
		 */
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(1.5, 2).intValue());

		/* Beispiel Nr. 2:
		 * 1 Erwachsene Person (Alleinerziehend) und 2 Kindern zu je 50% Abzugsmöglichkeit in den Steuern. Die Anzahl der Personen,
		 * die im gleichen Haushalt wohnen, beträgt somit 3 Personen und es wird nun der Ansatz 3-Personenhaushalt von
		 * Fr. 3'800.00 angenommen. Die anrechenbare Familiengrösse ist 2 und dieser Wert wird mit dem Ansatz von 3-Personenhaushalt
		 * von Fr. 3'800.00 multipliziert; Ergebnis Fr. 7'600.00
		 */
		Assert.assertEquals(7600, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(2, 3).intValue());

		/* Beispiel Nr. 3:
		 * 1 Erwachsene Person (Alleinerziehend) mit 3 Kindern, für die Kinder ist je 50% Kinderabzug möglich. Es sind insgesamt
		 * 4 Personen im gleichen Haushalt wohnhaft, somit wird die Pauschale einer 4-Personenhaushalt von Fr. 5'960.00 genommen.
		 * Die anrechenbare Familiengrösse beträgt 2,5 und dieser Wert wird mit der Pauschale 4-Personenhaushalt von
		 * Fr. 5'960.00 multipliziert; Ergebnis Fr. 14'900.00.
		*/
		Assert.assertEquals(14900, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(2.5, 4).intValue());

		/*
		 * Beispiel Nr. 4:
		 * 1 Erwachsene Person (Alleinerziehend) mit 4 Kindern, für das erste Kind ist kein Abzug in der Steuererklärung möglich,
		 * für das zweite Kind ist 100% möglich und für das dritte Kind 50%. Insgesamt sind 3 Personen im gleichen Haushalt
		 * wohnhaft. Deshalb wird die Pauschale 3-Personenhaushalt genommen
		 * (das erste Kind hat unter der Frage Kinderabzug "nein" stehen und zählt damit nicht dazu).
		 * Die anrechenbare Familiengrösse beträgt 2,5 und diese Familiengrösse wird mit der
		 * Pauschale 3-Personenhaushalt von Fr. 3'800.00 multipliziert; Ergebnis Fr. 9'500.00.
		 */
		Assert.assertEquals(9500, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(2.5, 3).intValue());

		/*
		 * Beispiel Nr. 5:
		 * 2 Erwachsene Personen (Konkubinat) und 4 Kindern, zwei eigene Kinder sind je 100% abzugsberechtigt in der
		 * Steuererklärung und für zwei Kindern sind zu je 50% Abzug möglich. Insgesamt leben 6 Personen im gleichen Haushalt,
		 * es wird nun der Ansatz von 6 Personenhaushalt von Fr. 7'580.00 genommen. Die anrechenbare Familiengrösse von 4,5
		 * wird mit der Pauschale 6-Personenhaushalt von Fr. 7'580.00 multipliziert; Ergebnis Fr. 34'110.00.
		 */
		Assert.assertEquals(34110, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(4.5, 6).intValue());

		/*
		 * Beispiel Nr. 6:
		 * 2 Erwachsene Personen (verheiratet) und 2 Kindern mit je 100% Abzugsmöglichkeit in den Steuern. Somit beträgt
		 * die Anzahl der Personen im gleichen Haushalt 4. Damit wird der Pauschalabzug von 4-Personenhaushalt angewendet.
		 * Die anrechenbare Familiengrösse 4 wird mit 4-Personhaushalt von Fr. 5'960.00 multipliziert; Ergebnis Fr. 23'840.00.
		 */
		Assert.assertEquals(23840, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(4.0, 4).intValue());
	}

	@Test
	public void testCalculateFamiliengroesseWithMutation1GSTo2GS() {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		final LocalDate date = LocalDate.of(1980, Month.MARCH, 25);
		gesuch.extractFamiliensituation().setAenderungPer(date);

		Familiensituation famSitErstgesuch = new Familiensituation();
		famSitErstgesuch.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(famSitErstgesuch);

		Assert.assertEquals(1, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.minusMonths(1)).getKey(), DELTA);
		Assert.assertEquals(1, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.withDayOfMonth(31)).getKey(), DELTA);
		Assert.assertEquals(2, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.plusMonths(1).withDayOfMonth(1)).getKey(), DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithMutation2GSTo1GS() {
		Gesuch gesuch = createGesuchWithOneGS();
		final LocalDate date = LocalDate.of(1980, Month.MARCH, 25);
		gesuch.extractFamiliensituation().setAenderungPer(date);

		Familiensituation famSitErstgesuch = new Familiensituation();
		famSitErstgesuch.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(famSitErstgesuch);

		Assert.assertEquals(2, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.minusMonths(1)).getKey(), DELTA);
		Assert.assertEquals(2, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.withDayOfMonth(31)).getKey(), DELTA);
		Assert.assertEquals(1, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.plusMonths(1).withDayOfMonth(1)).getKey(), DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithMutation2GSTo2GS() {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		final LocalDate date = LocalDate.of(1980, Month.MARCH, 25);
		gesuch.extractFamiliensituation().setAenderungPer(date);

		Familiensituation famSitErstgesuch = new Familiensituation();
		famSitErstgesuch.setFamilienstatus(EnumFamilienstatus.KONKUBINAT);
		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(famSitErstgesuch);

		Assert.assertEquals(2, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.minusMonths(1)).getKey(), DELTA);
		Assert.assertEquals(2, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.withDayOfMonth(31)).getKey(), DELTA);
		Assert.assertEquals(2, famabAbschnittRule.calculateFamiliengroesse(gesuch, date.plusMonths(1).withDayOfMonth(1)).getKey(), DELTA);
	}

	@Test
	public void testFamiliensituationMutiert1GSTo2GS() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		final LocalDate date = LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 25); // gesuchsperiode ist 2017/2018
		gesuch.extractFamiliensituation().setAenderungPer(date);

		Familiensituation famSitErstgesuch = new Familiensituation();
		famSitErstgesuch.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		famSitErstgesuch.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(famSitErstgesuch);

		gesuch.setKindContainers(new HashSet<>());
		final KindContainer defaultKindContainer = TestDataUtil.createDefaultKindContainer();
		gesuch.getKindContainers().add(defaultKindContainer);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());

		final VerfuegungZeitabschnitt zeitabschnitt0 = zeitabschnitte.get(0);
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), zeitabschnitt0.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(date.withDayOfMonth(31), zeitabschnitt0.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(BigDecimal.ZERO, zeitabschnitt0.getAbzugFamGroesse());
		Assert.assertEquals(BigDecimal.valueOf(2.0), zeitabschnitt0.getFamGroesse());

		final VerfuegungZeitabschnitt zeitabschnitt1 = zeitabschnitte.get(1);
		Assert.assertEquals(date.plusMonths(1).withDayOfMonth(1), zeitabschnitt1.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), zeitabschnitt1.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(BigDecimal.valueOf(11400), zeitabschnitt1.getAbzugFamGroesse());
		Assert.assertEquals(BigDecimal.valueOf(3.0), zeitabschnitt1.getFamGroesse());
	}

	@Nonnull
	private Gesuch createGesuchWithOneGS() {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1718());
		GesuchstellerContainer gesuchsteller = new GesuchstellerContainer();
		gesuchsteller.setGesuchstellerJA(new Gesuchsteller());
		gesuch.setGesuchsteller1(gesuchsteller);
		Familiensituation famSit = new Familiensituation();
		famSit.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		gesuch.setFamiliensituationContainer(new FamiliensituationContainer());
		gesuch.getFamiliensituationContainer().setFamiliensituationJA(famSit);
		return gesuch;
	}

	@Nonnull
	private Gesuch createGesuchWithTwoGesuchsteller() {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1718());
		GesuchstellerContainer gesuchsteller = new GesuchstellerContainer();
		gesuchsteller.setGesuchstellerJA(new Gesuchsteller());
		gesuch.setGesuchsteller1(gesuchsteller);
		gesuch.setGesuchsteller2(gesuchsteller);
		Familiensituation famSit = new Familiensituation();
		famSit.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		gesuch.setFamiliensituationContainer(new FamiliensituationContainer());
		gesuch.getFamiliensituationContainer().setFamiliensituationJA(famSit);
		return gesuch;
	}

	@Nonnull
	private Gesuch createGesuchWithKind(Kinderabzug abzug1, @Nullable Kinderabzug abzug2, LocalDate kindGeburtsdatum) {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		Set<KindContainer> kindContainers = new LinkedHashSet<>();
		kindContainers.add(createKindContainer(abzug1, kindGeburtsdatum));
		if (abzug2 != null) {
			kindContainers.add(createKindContainer(abzug2, kindGeburtsdatum));
		}
		gesuch.setKindContainers(kindContainers);
		return gesuch;
	}

	@Nonnull
	private KindContainer createKindContainer(Kinderabzug abzug, LocalDate kindGeburtsdatum) {
		KindContainer kindContainer = new KindContainer();
		Kind kindJA = new Kind();
		kindJA.setKinderabzug(abzug);
		kindJA.setGeburtsdatum(kindGeburtsdatum);
		kindContainer.setKindJA(kindJA);
		return kindContainer;
	}

}
