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

package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.entities.Zahlungsposition;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittZahlungsstatus;
import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.enums.ZahlungauftragStatus;
import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.entities.Zahlungsposition;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittZahlungsstatus;
import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.enums.ZahlungauftragStatus;
import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.AntragStatusHistoryService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.services.VerfuegungService;
import ch.dvbern.ebegu.services.ZahlungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer den Zahlungsservice
 */
@SuppressWarnings({ "LocalVariableNamingConvention", "InstanceMethodNamingConvention", "InstanceVariableNamingConvention" })
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
public class ZahlungServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private ZahlungService zahlungService;

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;

	private Gesuchsperiode gesuchsperiode;

	private static final LocalDateTime DATUM_GENERIERT = LocalDateTime.of(2017, Month.JUNE, 20, 0, 0);
	private static final LocalDate DATUM_FAELLIG = DATUM_GENERIERT.plusDays(3).toLocalDate();

	private static final LocalDate DATUM_AUGUST = LocalDate.of(2016, Month.AUGUST, 20);
	private static final LocalDate DATUM_SEPTEMBER = LocalDate.of(2016, Month.SEPTEMBER, 20);
	private static final LocalDate DATUM_OKTOBER = LocalDate.of(2016, Month.OCTOBER, 20);

	@Before
	public void init() {
		gesuchsperiode = createGesuchsperiode(true);
		insertInstitutionen();
		TestDataUtil.prepareParameters(gesuchsperiode.getGueltigkeit(), persistence);
	}

	@Test
	public void zahlungsauftragErstellenNormal() throws Exception {
		final Gesuch gesuch = createGesuch(true);
		checkZahlungErstgesuch(gesuch, DATUM_GENERIERT);
	}

	@Test(expected = EbeguRuntimeException.class)
	public void zahlungsauftragErstellenZweiEntwuerfe() {
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Entwurf 1");
		// Es darf kein zweiter Auftrag erstellt werden, solange der erste nicht freigegeben ist
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Entwurf 2");
	}

	/**
	 * Ein Gesuch wird erstellt. Eine Zahlung wird gemacht
	 * Die bereits erstellte Verfuegung ohne Aenderung mutieren und verfuegen, Zahlungslauf starten
	 */
	@Test
	public void zahlungsauftragErstellenNormalUndMutationNoChange() throws Exception {
		final Gesuch gesuch = createGesuch(true);
		checkZahlungErstgesuch(gesuch, DATUM_SEPTEMBER.atStartOfDay());
		final Gesuch mutation = createMutationHeirat(gesuch, true);
		Assert.assertNotNull(mutation);

		Zahlungsauftrag zahlungsauftragMutation = zahlungService.zahlungsauftragErstellen(DATUM_SEPTEMBER.plusDays(1), "Testauftrag", DATUM_SEPTEMBER.plusDays(1).atStartOfDay());

		Assert.assertNotNull(zahlungsauftragMutation);
		Assert.assertNotNull(zahlungsauftragMutation.getZahlungen());
		Assert.assertTrue(zahlungsauftragMutation.getZahlungen().isEmpty()); // keine neue Zahlung, da gleiche Daten
	}

	/**
	 * Ein Gesuch wird erstellt. Eine Zahlung wird gemacht
	 * Die bereits erstellte Verfuegung mutieren mit Aenderung. In der Zahlung muessen alle Korrekturen angezeigt werden.
	 */
	@Test
	public void zahlungsauftragErstellenNormalUndMutationChange() throws Exception {
		final Gesuch gesuch = createGesuch(true);
		final Zahlungsauftrag zahlungsauftrag = checkZahlungErstgesuch(gesuch, DATUM_GENERIERT);
		final Gesuch mutation = createMutationFinSit(gesuch, true, DATUM_AUGUST.atStartOfDay(), BigDecimal.valueOf(70000), false);
		Assert.assertNotNull(mutation);

		// gleiche Mutation wie in vorherigem Test aber die Yahlung erfolgt nun am Ende der Periode, daher Aenderungen in der Zahlung
		Zahlungsauftrag zahlungsauftragMutation = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG.plusDays(1), "Testauftrag", DATUM_GENERIERT.plusDays(1));

		final BigDecimal betragAugustMutation = getBetragAugustFromGesuch(mutation);

		Assert.assertNotNull(zahlungsauftragMutation);
		Assert.assertNotNull(zahlungsauftragMutation.getZahlungen());
		Assert.assertEquals(1, zahlungsauftragMutation.getZahlungen().size());

		Assert.assertEquals(11, zahlungsauftrag.getZahlungen().get(0).getZahlungspositionen().size());
		// es hat 22: 11 die substraiert werden von der alten Zahlung und 11 die neu dazukommen
		Assert.assertEquals(22, zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen().size());

		Assert.assertEquals(0, betragAugustMutation.compareTo(
			zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen().get(0).getBetrag()));

		assertZahlungPositionenIgnored(zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen(), false);
		assertAllOldZahlungenAreSubstracted(zahlungsauftrag.getZahlungen().get(0).getZahlungspositionen(),
			zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen());
	}

	/**
	 * Ein Gesuch wird erstellt. Eine Zahlung wird gemacht
	 * Die bereits erstellte Verfuegung mutieren mit Aenderung.
	 * Nochmal eine Mutation machen und die Aenderungen uebernehmen. Erst jetzt eine Zahlung machen
	 * In der Zahlung muessen alle Korrekturen angezeigt werden aber die von erster Mutation nicht.
	 */
	@Test
	public void zahlungsauftragErstellenNormalUndTwoMutationChange() throws Exception {
		final Gesuch gesuch = createGesuch(true);
		final Zahlungsauftrag zahlungsauftrag = checkZahlungErstgesuch(gesuch, DATUM_GENERIERT);
		final Gesuch mutation = createMutationFinSit(gesuch, true, DATUM_AUGUST.atStartOfDay(), BigDecimal.valueOf(70000), false);
		Assert.assertNotNull(mutation);
		final Gesuch zweiteMutation = createMutationFinSit(mutation, true, DATUM_AUGUST.atStartOfDay(), BigDecimal.valueOf(90000), false);
		Assert.assertNotNull(zweiteMutation);

		// gleiche Mutation wie in vorherigem Test aber die Yahlung erfolgt nun am Ende der Periode, daher Aenderungen in der Zahlung
		Zahlungsauftrag zahlungsauftragMutation = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG.plusDays(1), "Testauftrag", DATUM_GENERIERT.plusDays(1));

		Assert.assertNotNull(zahlungsauftragMutation);
		Assert.assertNotNull(zahlungsauftragMutation.getZahlungen());
		Assert.assertEquals(1, zahlungsauftragMutation.getZahlungen().size());

		final BigDecimal betragAugustGesuch = getBetragAugustFromGesuch(gesuch);
		final BigDecimal betragAugustMutation = getBetragAugustFromGesuch(mutation);
		final BigDecimal betragAugustZweiteMutation = getBetragAugustFromGesuch(zweiteMutation);

		Assert.assertEquals(11, zahlungsauftrag.getZahlungen().get(0).getZahlungspositionen().size());
		// es hat 22: 11 die substraiert werden von der alten Zahlung und 11 die neu dazukommen
		Assert.assertEquals(22, zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen().size());

		Assert.assertEquals(0, betragAugustZweiteMutation.compareTo(
			zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen().get(0).getBetrag()));
		Assert.assertEquals(0, betragAugustGesuch.negate().compareTo(
			zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen().get(1).getBetrag()));

		assertZahlungPositionenIgnored(zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen(), false);
		assertErsteMutationIsNeverUsed(zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen(), betragAugustMutation);
		assertAllOldZahlungenAreSubstracted(zahlungsauftrag.getZahlungen().get(0).getZahlungspositionen(),
			zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen());
	}

	/**
	 * Ein Gesuch wird erstellt. Eine Zahlung wird gemacht
	 * Die bereits erstellte Verfuegung mutieren mit Aenderung aber diese ignorieren. Alle Korrekturen werden als ignoriert angezeigt
	 */
	@Test
	public void zahlungsauftragErstellenNormalUndMutationChangeIgnoriert() throws Exception {
		final Gesuch gesuch = createGesuch(true);
		final Zahlungsauftrag zahlungsauftrag = checkZahlungErstgesuch(gesuch, DATUM_GENERIERT);
		final Gesuch mutation = createMutationFinSit(gesuch, true, DATUM_AUGUST.atStartOfDay(), BigDecimal.valueOf(70000), true);
		Assert.assertNotNull(mutation);

		// gleiche Mutation wie in vorherigem Test aber die Yahlung erfolgt nun am Ende der Periode, daher Aenderungen in der Zahlung
		Zahlungsauftrag zahlungsauftragMutation = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG.plusDays(1), "Testauftrag", DATUM_GENERIERT.plusDays(1));

		Assert.assertNotNull(zahlungsauftragMutation);
		Assert.assertNotNull(zahlungsauftragMutation.getZahlungen());
		Assert.assertEquals(1, zahlungsauftragMutation.getZahlungen().size());

		Assert.assertEquals(11, zahlungsauftrag.getZahlungen().get(0).getZahlungspositionen().size());
		// es hat 22: 11 die substraiert werden von der alten Zahlung und 11 die neu dazukommen
		Assert.assertEquals(22, zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen().size());

		// die ZahlungPositionen wurden der Zahlung hinzugefuegt aber sie muessen als ignoriert markiert sein
		assertZahlungPositionenIgnored(zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen(), true);
		assertAllOldZahlungenAreSubstracted(zahlungsauftrag.getZahlungen().get(0).getZahlungspositionen(),
			zahlungsauftragMutation.getZahlungen().get(0).getZahlungspositionen());
	}

	@SuppressWarnings("JUnitTestMethodWithNoAssertions")
	@Test
	public void zahlungsauftragErstellenMitNachzahlung() {
		createGesuch(true);
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), DATUM_GENERIERT
			.minusDays(1)) + 1;

		// Die erste Zahlung ueberhaupt wird normal durchgefuehrt
		Zahlungsauftrag zahlungsauftrag1 = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG,
			"Normaler Auftrag", DATUM_GENERIERT);
		assertZahlungErstgesuch(countMonate, zahlungsauftrag1);

		// Fuer die 2. Zahlung, die eine repetition ist, werden auch neue Gesuche beruecksichtigt, obwohl ihre Abschnitte in der Vergangenheit liegen
		createGesuch(true, DATUM_SEPTEMBER.minusDays(1), null);
		// Zahlung ausloesen
		Zahlungsauftrag zahlungsauftrag2 = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG,
			"nachtraeglicher Auftrag", DATUM_GENERIERT);
		assertZahlungErstgesuch(countMonate, zahlungsauftrag2);
	}

	@Test
	public void zahlungsauftragErstellenMitKorrekturMultiple() throws Exception {
		Gesuch erstgesuch = createGesuch(true, DATUM_AUGUST.minusDays(1), AntragStatus.VERFUEGT); // Becker Yasmin,
		// 01.08.2016 - 31.07.2017, EWP 60%

		// Zahlung August ausloesen:
		// Erwartet:    1 NORMALE Zahlung August
		Zahlungsauftrag auftragAugust = zahlungService.zahlungsauftragErstellen(DATUM_AUGUST, "Zahlung August", DATUM_AUGUST.atStartOfDay());
		Assert.assertEquals(1, auftragAugust.getZahlungen().size());
		Assert.assertEquals(1, auftragAugust.getZahlungen().get(0).getZahlungspositionen().size());
		Assert.assertEquals(ZahlungspositionStatus.NORMAL, auftragAugust.getZahlungen().get(0).getZahlungspositionen().get(0).getStatus());
		zahlungService.zahlungsauftragAusloesen(auftragAugust.getId());

		// Eine (verfuegte) Mutation erstellen, welche rueckwirkende Auswirkungen hat auf Vollkosten
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), 50, DATUM_AUGUST.plusWeeks(1));

		// Zahlung September ausloesen:
		// Erwartet:    1 NORMALE Zahlung September
		//              2 KORREKTUREN August (Minus und Plus)
		Zahlungsauftrag auftragSeptember = zahlungService.zahlungsauftragErstellen(DATUM_SEPTEMBER, "Zahlung September", DATUM_SEPTEMBER.atStartOfDay());
		Assert.assertEquals(1, auftragSeptember.getZahlungen().size());
		Assert.assertEquals(3, auftragSeptember.getZahlungen().get(0).getZahlungspositionen().size());
		Assert.assertEquals(ZahlungspositionStatus.NORMAL, auftragSeptember.getZahlungen().get(0).getZahlungspositionen().get(0).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragSeptember.getZahlungen().get(0).getZahlungspositionen().get(1).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragSeptember.getZahlungen().get(0).getZahlungspositionen().get(2).getStatus());
		zahlungService.zahlungsauftragAusloesen(auftragSeptember.getId());

		// Eine (verfuegte) Mutation erstellen, welche rueckwirkende Auswirkungen hat auf Vollkosten
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), 40, DATUM_SEPTEMBER.plusWeeks(1));

		// Zahlung Oktober ausloesen:
		// Erwartet:    1 NORMALE Zahlung Oktober
		//              2 KORREKTUREN September (Minus und Plus)
		//              2 KORREKTUREN August (Minus und Plus)
		Zahlungsauftrag auftragOktober = zahlungService.zahlungsauftragErstellen(DATUM_OKTOBER, "Zahlung Oktober", DATUM_OKTOBER.atStartOfDay());
		Assert.assertEquals(1, auftragOktober.getZahlungen().size());
		Assert.assertEquals(5, auftragOktober.getZahlungen().get(0).getZahlungspositionen().size());
		Assert.assertEquals(ZahlungspositionStatus.NORMAL, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(0).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(1).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(2).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(3).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(4).getStatus());
		zahlungService.zahlungsauftragAusloesen(auftragOktober.getId());
	}

	@Test
	public void zahlungsauftragErstellenMitKorrekturMonatUeberspringen() throws Exception {
		Gesuch erstgesuch = createGesuch(true, DATUM_AUGUST.minusDays(1), AntragStatus.VERFUEGT); // Becker Yasmin,
		// 01.08.2016 - 31.07.2017, EWP 60%

		// Zahlung August ausloesen
		// Erwartet:    1 NORMALE Zahlung August
		Zahlungsauftrag auftragAugust = zahlungService.zahlungsauftragErstellen(DATUM_AUGUST, "Zahlung August", DATUM_AUGUST.atStartOfDay());
		Assert.assertEquals(1, auftragAugust.getZahlungen().size());
		Assert.assertEquals(1, auftragAugust.getZahlungen().get(0).getZahlungspositionen().size());
		Assert.assertEquals(ZahlungspositionStatus.NORMAL, auftragAugust.getZahlungen().get(0).getZahlungspositionen().get(0).getStatus());
		zahlungService.zahlungsauftragAusloesen(auftragAugust.getId());

		// Eine (verfuegte) Mutation erstellen, welche rueckwirkende Auswirkungen hat auf Vollkosten
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), 50, DATUM_AUGUST.plusWeeks(1));

		// Zahlung September wird nicht ausgeloest

		// Zahlung Oktober ausloesen:
		// Erwartet:    1 NORMALE Zahlung Oktober
		//              1 NORMALE Zahlung September
		//              2 KORREKTUREN August (Minus und Plus)
		Zahlungsauftrag auftragOktober = zahlungService.zahlungsauftragErstellen(DATUM_OKTOBER, "Zahlung Oktober", DATUM_OKTOBER.atStartOfDay());
		Assert.assertEquals(1, auftragOktober.getZahlungen().size());
		Assert.assertEquals(4, auftragOktober.getZahlungen().get(0).getZahlungspositionen().size());
		Assert.assertEquals(ZahlungspositionStatus.NORMAL, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(0).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.NORMAL, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(1).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(2).getStatus());
		Assert.assertEquals(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, auftragOktober.getZahlungen().get(0).getZahlungspositionen().get(3).getStatus());
		zahlungService.zahlungsauftragAusloesen(auftragOktober.getId());
	}

	private void assertZahlungsauftrag(Zahlungsauftrag zahlungsauftrag, int anzahlZahlungen) {
		Assert.assertNotNull(zahlungsauftrag);
		Assert.assertNotNull(zahlungsauftrag.getZahlungen());
		Assert.assertEquals(anzahlZahlungen, zahlungsauftrag.getZahlungen().size());
	}

	private void assertZahlung(Zahlung zahlung, long anzahlZahlungspositionen) {
		Assert.assertNotNull(zahlung);
		Assert.assertNotNull(zahlung.getZahlungspositionen());
		Assert.assertEquals(anzahlZahlungspositionen, zahlung.getZahlungspositionen().size());
	}

	private void assertZahlungsdetail(Zahlungsposition zahlungsposition, ZahlungspositionStatus status, double betrag) {
		Assert.assertNotNull(zahlungsposition);
		Assert.assertEquals(status, zahlungsposition.getStatus());
		Assert.assertEquals(MathUtil.DEFAULT.from(betrag), zahlungsposition.getBetrag());
		Assert.assertEquals(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET, zahlungsposition.getVerfuegungZeitabschnitt().getZahlungsstatus());
	}

	private void assertZahlungErstgesuch(long countMonate, Zahlungsauftrag zahlungsauftrag) {
		assertZahlungsauftrag(zahlungsauftrag, 1);
		Zahlung zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate);
		for (int i = 0; i < countMonate; i++) {
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(i), ZahlungspositionStatus.NORMAL, 1289.30);
		}
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());
	}

	@Test
	public void zahlungsauftragAusloesen() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", DATUM_GENERIERT);

		Assert.assertEquals(ZahlungauftragStatus.ENTWURF, zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).get().getStatus());
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());
		Assert.assertEquals(ZahlungauftragStatus.AUSGELOEST, zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).get().getStatus());
	}

	@Test
	public void findZahlungsauftrag() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", DATUM_GENERIERT);

		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
		Assert.assertFalse(zahlungService.findZahlungsauftrag("ungueltigeId").isPresent());
	}

	@Test
	public void deleteZahlungsauftrag() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", DATUM_GENERIERT);

		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
		zahlungService.deleteAllZahlungsauftraege();
		Assert.assertFalse(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
	}

	@Test
	public void getAllZahlungsauftraege() throws Exception {
		Assert.assertTrue(zahlungService.getAllZahlungsauftraege().isEmpty());

		createGesuch(true);
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", DATUM_GENERIERT);
		Assert.assertFalse(zahlungService.getAllZahlungsauftraege().isEmpty());
	}

	@Test
	public void zahlungBestaetigen() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", DATUM_GENERIERT);

		Assert.assertNotNull(zahlungsauftrag);
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), DATUM_GENERIERT) + 1;
		createGesuch(true);

		assertZahlungsauftrag(zahlungsauftrag, 1);
		Zahlung zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate);
		Assert.assertEquals(ZahlungStatus.ENTWURF, zahlung.getStatus());
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());

		zahlung = zahlungService.zahlungBestaetigen(zahlung.getId());
		Assert.assertNotNull(zahlung);
		Assert.assertEquals(ZahlungStatus.BESTAETIGT, zahlung.getStatus());
	}

	@Override
	protected Gesuchsperiode createGesuchsperiode(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createGesuchsperiode1617();
		gesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		return gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
	}

	private Gesuch createGesuch(boolean verfuegen, LocalDate verfuegungsdatum, @Nullable AntragStatus status) {
		Gesuch gesuch = createGesuch(verfuegen);
		Assert.assertNotNull(gesuch);
		if (status != null) {
			gesuch.setStatus(status);
		}
		gesuch.setTimestampVerfuegt(verfuegungsdatum.atStartOfDay());
		AntragStatusHistory lastStatusChange = antragStatusHistoryService.findLastStatusChange(gesuch);
		Validate.notNull(lastStatusChange);
		lastStatusChange.setTimestampVon(verfuegungsdatum.atStartOfDay());
		persistence.merge(lastStatusChange);
		return persistence.merge(gesuch);
	}

	@Nullable
	private Gesuch createGesuch(boolean verfuegen) {
		return testfaelleService.createAndSaveTestfaelle(TestfaelleService.BECKER_NORA, verfuegen, verfuegen);
	}

	@Nullable
	private Gesuch createMutationHeirat(@Nullable Gesuch gesuch, boolean verfuegen) {
		Assert.assertNotNull(gesuch);
		return testfaelleService.mutierenHeirat(gesuch.getFall().getFallNummer(), gesuch.getGesuchsperiode().getId(),
			LocalDate.of(TestDataUtil.PERIODE_JAHR_0, Month.DECEMBER, 15), LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.JANUARY, 15), verfuegen);
	}

	private Gesuch createMutationFinSit(@Nullable Gesuch gesuch, boolean verfuegen, LocalDateTime timestampVerfuegt, BigDecimal nettoLohn, boolean ignorieren) {
		Assert.assertNotNull(gesuch);
		final Gesuch mutation = testfaelleService.mutierenFinSit(gesuch.getFall().getFallNummer(), gesuch.getGesuchsperiode().getId(),
			LocalDate.of(TestDataUtil.PERIODE_JAHR_0, Month.DECEMBER, 15), LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.JANUARY, 15),
			verfuegen, nettoLohn, ignorieren);// Im Gesuch ist nettolohn nicht definiert, also 0. Hier machen wir es hoeher
		mutation.setTimestampVerfuegt(timestampVerfuegt);
		persistence.merge(mutation);
		return mutation;
	}

	@Nullable
	private Gesuch createMutationBetreuungspensum(Gesuch erstgesuch, LocalDate eingangsdatum, int pensum) {
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			mutation.setStatus(AntragStatus.VERFUEGEN);
			List<Betreuung> betreuungs = mutation.extractAllBetreuungen();
			for (Betreuung betreuung : betreuungs) {
				Set<BetreuungspensumContainer> betreuungspensumContainers = betreuung.getBetreuungspensumContainers();
				for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
					betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
				}
			}
			gesuchService.createGesuch(mutation);
			// es muss mit verfuegungService.verfuegen verfuegt werden, damit der Zahlungsstatus der Zeitabschnitte richtig gesetzt wird. So wird auch dies getestet
			testfaelleService.gesuchVerfuegenUndSpeichern(false, mutation, true, false);
			verfuegungService.calculateVerfuegung(mutation);
			for (Betreuung betreuung : betreuungs) {
				verfuegungService.verfuegen(betreuung.getVerfuegung(), betreuung.getId(), false);
			}
			return mutation;
		}
		return gesuchOptional.orElse(null);
	}

	private Gesuch createMutationBetreuungspensum(Gesuch erstgesuch, LocalDate eingangsdatum, int pensum, LocalDate verfuegungsdatum) {
		Gesuch gesuch = createMutationBetreuungspensum(erstgesuch, eingangsdatum, pensum);
		Assert.assertNotNull(gesuch);
		gesuchService.postGesuchVerfuegen(gesuch);
		gesuch = gesuchService.findGesuch(gesuch.getId()).get();
		gesuch.setTimestampVerfuegt(verfuegungsdatum.atStartOfDay());
		gesuchService.updateGesuch(gesuch, false, null);
		AntragStatusHistory lastStatusChange = antragStatusHistoryService.findLastStatusChange(gesuch);
		Validate.notNull(lastStatusChange);
		lastStatusChange.setTimestampVon(verfuegungsdatum.atStartOfDay());
		persistence.merge(lastStatusChange);
		return gesuch;
	}

	@Nullable
	private Gesuch createMutationEinkommen(Gesuch erstgesuch, LocalDate eingangsdatum) {
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			Validate.notNull(mutation.getGesuchsteller1());
			Validate.notNull(mutation.getGesuchsteller1().getFinanzielleSituationContainer());
			mutation.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(60000d));
			gesuchService.createGesuch(mutation);
			// es muss mit verfuegungService.verfuegen verfuegt werden, damit der Zahlungsstatus der Zeitabschnitte richtig gesetzt wird. So wird auch dies getestet
			testfaelleService.gesuchVerfuegenUndSpeichern(false, mutation, true, false);
			verfuegungService.calculateVerfuegung(mutation);
			List<Betreuung> betreuungs = mutation.extractAllBetreuungen();
			for (Betreuung betreuung : betreuungs) {
				verfuegungService.verfuegen(betreuung.getVerfuegung(), betreuung.getId(), false);
			}
			return mutation;
		}
		return gesuchOptional.orElse(null);
	}

	@Test
	public void testDeleteZahlungspositionenOfGesuch() throws Exception {
		Gesuch gesuch = createGesuch(true);
		Assert.assertNotNull(gesuch);
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", DATUM_GENERIERT);
		Assert.assertFalse(zahlungService.getAllZahlungsauftraege().isEmpty());

		zahlungService.deleteZahlungspositionenOfGesuch(gesuch);

		Assert.assertTrue(zahlungService.getAllZahlungsauftraege().isEmpty());
	}

	@NotNull
	private Zahlungsauftrag checkZahlungErstgesuch(@Nullable Gesuch gesuch, @NotNull LocalDateTime datumGeneriert) {
		Assert.assertNotNull(gesuch);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(datumGeneriert.toLocalDate().plusDays(3), "Zahlung September", datumGeneriert);
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());

		Assert.assertNotNull(zahlungsauftrag);
		Assert.assertNotNull(zahlungsauftrag.getZahlungen());
		Assert.assertFalse(zahlungsauftrag.getZahlungen().isEmpty());
		return zahlungsauftrag;
	}

	private void assertAllOldZahlungenAreSubstracted(List<Zahlungsposition> zahlungspositionen, List<Zahlungsposition> zahlungspositionenMutation) {
		BigDecimal oldTotal = BigDecimal.ZERO;
		for (Zahlungsposition zahlungsposition : zahlungspositionen) {
			oldTotal = oldTotal.add(zahlungsposition.getBetrag());
		}
		BigDecimal allSubstracted = BigDecimal.ZERO;
		for (Zahlungsposition zahlungspositionMutation : zahlungspositionenMutation) {
			if (zahlungspositionMutation.getBetrag().compareTo(BigDecimal.ZERO) < 0) {
				allSubstracted = allSubstracted.add(zahlungspositionMutation.getBetrag());
			}
		}
		Assert.assertEquals(oldTotal, allSubstracted.negate());
	}

	private void assertZahlungPositionenIgnored(List<Zahlungsposition> zahlungspositionen, boolean ignoriert) {
		for (Zahlungsposition zahlungsposition : zahlungspositionen) {
			Assert.assertEquals(ignoriert, zahlungsposition.isIgnoriert());
		}
	}

	private void assertErsteMutationIsNeverUsed(List<Zahlungsposition> zahlungspositionen, BigDecimal betrag) {
		for (Zahlungsposition zahlungsposition : zahlungspositionen) {
			Assert.assertNotEquals(betrag, zahlungsposition.getBetrag());
		}
	}

	/**
	 * Berechnet den gesamten Betrag fuer den Monat August.
	 * Voraussetzungen: Es gibt nur eine Betreuung der Art KITA. Sonst werden alle zusammenaddiert
	 */
	private BigDecimal getBetragAugustFromGesuch(@Nullable Gesuch gesuch) {
		Assert.assertNotNull(gesuch);
		BigDecimal betrag =  gesuch.extractAllBetreuungen().stream()
			.filter(betreuung -> betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp() == BetreuungsangebotTyp.KITA)
			.map(Betreuung::getVerfuegung)
			.map(Verfuegung::getZeitabschnitte)
			.flatMap(Collection::stream)
			.filter(verfuegungZeitabschnitt -> verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb().getMonth() == Month.AUGUST)
			.map(VerfuegungZeitabschnitt::getVerguenstigung)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		return betrag;
	}
}
