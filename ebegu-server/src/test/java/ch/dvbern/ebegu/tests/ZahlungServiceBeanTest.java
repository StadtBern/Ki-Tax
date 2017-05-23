package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Tests fuer den Zahlungsservice
 */
@SuppressWarnings({"LocalVariableNamingConvention", "InstanceMethodNamingConvention", "InstanceVariableNamingConvention"})
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
	private Persistence<?> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;

	private Gesuchsperiode gesuchsperiode;
	private static final LocalDate DATUM_FAELLIG = LocalDate.now().plusDays(3);

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
	public void findGesuchIdsOfAktuellerAntrag() throws Exception {
		Gesuch verfuegtesGesuch = createGesuch(true);
		Gesuch nichtVerfuegtesGesuch = createGesuch(false);

		Gesuch erstgesuchMitMutation = createGesuch(true);
		Gesuch nichtVerfuegteMutation = createMutation(erstgesuchMitMutation, false);

		Gesuch erstgesuchMitVerfuegterMutation = createGesuch(true);
		Gesuch verfuegteMutation = createMutation(erstgesuchMitVerfuegterMutation, true);

		List<String> gesuchIdsOfAktuellerAntrag = gesuchService.getNeuesteVerfuegteAntraege(verfuegtesGesuch.getGesuchsperiode());
		Assert.assertNotNull(gesuchIdsOfAktuellerAntrag);

		Assert.assertTrue(gesuchIdsOfAktuellerAntrag.contains(verfuegtesGesuch.getId()));
		Assert.assertFalse(gesuchIdsOfAktuellerAntrag.contains(nichtVerfuegtesGesuch.getId()));

		Assert.assertTrue(gesuchIdsOfAktuellerAntrag.contains(erstgesuchMitMutation.getId()));
		Assert.assertFalse(gesuchIdsOfAktuellerAntrag.contains(nichtVerfuegteMutation.getId()));

		Assert.assertFalse(gesuchIdsOfAktuellerAntrag.contains(erstgesuchMitVerfuegterMutation.getId()));
		Assert.assertTrue(gesuchIdsOfAktuellerAntrag.contains(verfuegteMutation.getId()));
	}

	@Test
	public void zahlungsauftragErstellenNormal() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());

		Assert.assertNotNull(zahlungsauftrag);
		Assert.assertNotNull(zahlungsauftrag.getZahlungen());
		Assert.assertFalse(zahlungsauftrag.getZahlungen().isEmpty());
	}

	@Test(expected = EbeguRuntimeException.class)
	public void zahlungsauftragErstellenZweiEntwuerfe() {
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Entwurf 1");
		// Es darf kein zweiter Auftrag erstellt werden, solange der erste nicht freigegeben ist
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Entwurf 2");
	}

	@SuppressWarnings("JUnitTestMethodWithNoAssertions")
	@Test
	public void zahlungsauftragErstellenMitNachzahlung() {
		createGesuch(true);
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), LocalDate.now()) + 1;

		// Die erste Zahlung ueberhaupt wird normal durchgefuehrt
		Zahlungsauftrag zahlungsauftrag1 = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Normaler Auftrag");
		assertZahlungErstgesuch(countMonate, zahlungsauftrag1);

		// Fuer die 2. Zahlung, die eine repetition ist, werden auch neue Gesuche beruecksichtigt, obwohl ihre Abschnitte in der Vergangenheit liegen
		createGesuch(true);
		// Zahlung ausloesen
		Zahlungsauftrag zahlungsauftrag2 = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "nachtraeglicher Auftrag");
		assertZahlungErstgesuch(countMonate, zahlungsauftrag2);
	}

	@SuppressWarnings("ReuseOfLocalVariable")
	@Test
	public void zahlungsauftragErstellenMitKorrektur() throws Exception {
		Gesuch erstgesuch = createGesuch(true);
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), LocalDate.now()) + 1;

		// Zahlung ausloesen
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Normaler Auftrag");
		assertZahlungErstgesuch(countMonate, zahlungsauftrag);

		// Jetzt sollten keine offenen mehr vorhanden sein:
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Normaler Auftrag wiederholt");
		assertZahlungsauftrag(zahlungsauftrag, 0);
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());

		// Eine (verfuegte) Mutation erstellen, welche rueckwirkende Auswirkungen hat auf Vollkosten
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), 50);
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Auftrag mit Mutation der Vollkosten (Betreuungspensum)");
		assertZahlungsauftrag(zahlungsauftrag, 1);
		Zahlung zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate * 2);
		for (int i = 0; i < countMonate; i++) {
			int position = i * 2;
			// Pro Monat gibt es eine Korrekturbuchung und eine Neubuchung
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position), ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, 1074.40);
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position + 1), ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, -1289.30d);
		}
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());

		// Eine weitere (verfuegte) Mutation, welche den Elternbeitrag erhoeht:
		createMutationEinkommen(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb());
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Auftrag mit Mutation des Lohns (Elternbeitrag)");
		assertZahlungsauftrag(zahlungsauftrag, 1);
		zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate * 2);
		for (int i = 0; i < countMonate; i++) {
			int position = i * 2;
			// Pro Monat gibt es eine Korrekturbuchung und eine Neubuchung
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position), ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG, 923.40);
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position + 1), ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG, -1074.40d);
		}
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());

		// Eine (NICHT verfuegte) Mutation erstellen -> Keine Auswirkungen!
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), 50);
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Auftrag ohne neue Mutation");
		assertZahlungsauftrag(zahlungsauftrag, 0);
	}

	@Test
	public void zahlungsauftragErstellenMitKorrekturMultiple() throws Exception {
		Gesuch erstgesuch = createGesuch(true, DATUM_AUGUST.minusDays(1)); // Becker Yasmin, 01.08.2016 - 31.07.2017, EWP 60%

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
		Gesuch erstgesuch = createGesuch(true, DATUM_AUGUST.minusDays(1)); // Becker Yasmin, 01.08.2016 - 31.07.2017, EWP 60%

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
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());

		Assert.assertEquals(ZahlungauftragStatus.ENTWURF, zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).get().getStatus());
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());
		Assert.assertEquals(ZahlungauftragStatus.AUSGELOEST, zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).get().getStatus());
	}

	@Test
	public void findZahlungsauftrag() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());

		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
		Assert.assertFalse(zahlungService.findZahlungsauftrag("ungueltigeId").isPresent());
	}

	@Test
	public void deleteZahlungsauftrag() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());

		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
		zahlungService.deleteZahlungsauftrag(zahlungsauftrag.getId());
		Assert.assertFalse(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
	}

	@Test
	public void getAllZahlungsauftraege() throws Exception {
		Assert.assertTrue(zahlungService.getAllZahlungsauftraege().isEmpty());

		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());
		Assert.assertFalse(zahlungService.getAllZahlungsauftraege().isEmpty());
	}

	@Test
	public void zahlungBestaetigen() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());

		Assert.assertNotNull(zahlungsauftrag);
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), LocalDate.now())+1;
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
		Gesuchsperiode gesuchsperiode = TestDataUtil.createCurrentGesuchsperiode();
		gesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		return gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
	}

	private Gesuch createGesuch(boolean verfuegen, LocalDate verfuegungsdatum) {
		Gesuch gesuch = createGesuch(verfuegen);
		gesuch.setTimestampVerfuegt(verfuegungsdatum.atStartOfDay());
		AntragStatusHistory lastStatusChange = antragStatusHistoryService.findLastStatusChange(gesuch);
		lastStatusChange.setTimestampVon(verfuegungsdatum.atStartOfDay());
		persistence.merge(lastStatusChange);
		return gesuch;
	}

	private Gesuch createGesuch(boolean verfuegen) {
		return testfaelleService.createAndSaveTestfaelle(TestfaelleService.BeckerNora, verfuegen, verfuegen);
	}

	private Gesuch createMutation(Gesuch erstgesuch, boolean verfuegen) {
		return testfaelleService.mutierenHeirat(erstgesuch.getFall().getFallNummer(),
			erstgesuch.getGesuchsperiode().getId(), LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 15), LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 15), verfuegen);
	}

	private Gesuch createMutationBetreuungspensum(Gesuch erstgesuch, LocalDate eingangsdatum, int pensum) {
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			List<Betreuung> betreuungs = mutation.extractAllBetreuungen();
			for (Betreuung betreuung : betreuungs) {
				Set<BetreuungspensumContainer> betreuungspensumContainers = betreuung.getBetreuungspensumContainers();
				for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
					betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
				}
			}
			gesuchService.createGesuch(mutation);
			// es muss mit verfuegungService.verfuegen verfuegt werden, damit der Zahlungsstatus der Zeitabschnitte richtig gesetzt wird. So wird auch dies getestet
			testfaelleService.gesuchVerfuegenUndSpeichern(false, mutation, true);
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
		gesuchService.postGesuchVerfuegen(gesuch);
		gesuch = gesuchService.findGesuch(gesuch.getId()).get();
		gesuch.setTimestampVerfuegt(verfuegungsdatum.atStartOfDay());
		gesuchService.updateGesuch(gesuch, false);
		AntragStatusHistory lastStatusChange = antragStatusHistoryService.findLastStatusChange(gesuch);
		lastStatusChange.setTimestampVon(verfuegungsdatum.atStartOfDay());
		persistence.merge(lastStatusChange);
		return gesuch;
	}

	private Gesuch createMutationEinkommen(Gesuch erstgesuch, LocalDate eingangsdatum) {
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			mutation.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(60000d));
			gesuchService.createGesuch(mutation);
			// es muss mit verfuegungService.verfuegen verfuegt werden, damit der Zahlungsstatus der Zeitabschnitte richtig gesetzt wird. So wird auch dies getestet
			testfaelleService.gesuchVerfuegenUndSpeichern(false, mutation, true);
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
		zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag", LocalDateTime.now());
		Assert.assertFalse(zahlungService.getAllZahlungsauftraege().isEmpty());

		zahlungService.deleteZahlungspositionenOfGesuch(gesuch);

		Assert.assertTrue(zahlungService.getAllZahlungsauftraege().isEmpty());
	}
}
