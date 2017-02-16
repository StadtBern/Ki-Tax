package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittStatus;
import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.services.ZahlungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.After;
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
@SuppressWarnings({"LocalVariableNamingConvention", "InstanceMethodNamingConvention"})
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
	private Persistence<?> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	private Gesuchsperiode gesuchsperiode;
	private int JAHR_1, JAHR_2;
	private LocalDateTime DATUM_FAELLIG = LocalDateTime.now().plusDays(3);


	@Before
	public void init() {
		gesuchsperiode = createGesuchsperiode(true);
		Mandant mandant = insertInstitutionen();
		createBenutzer(mandant);
		TestDataUtil.prepareParameters(gesuchsperiode.getGueltigkeit(), persistence);
	}

	@After
	public void tearDown() throws Exception {

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
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag");

		Assert.assertNotNull(zahlungsauftrag);
		Assert.assertNotNull(zahlungsauftrag.getZahlungen());
		Assert.assertFalse(zahlungsauftrag.getZahlungen().isEmpty());
	}

	@Test
	public void zahlungsauftragErstellenMitKorrektur() throws Exception {
		Gesuch erstgesuch = createGesuch(true);
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), LocalDate.now())+1;

		// Zahlung ausloesen
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Normaler Auftrag");
		assertZahlungsauftrag(zahlungsauftrag, 1);
		Zahlung zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate);
		for (int i = 0; i < countMonate; i++) {
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(i), ZahlungspositionStatus.NORMAL, 1289.30);
		}
		// Jetzt sollten keine offenen mehr vorhanden sein:
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Normaler Auftrag wiederholt");
		assertZahlungsauftrag(zahlungsauftrag, 0);

		// Eine (verfuegte) Mutation erstellen, welche rueckwirkende Auswirkungen hat auf Vollkosten
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), true);
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Auftrag mit Mutation der Vollkosten (Betreuungspensum)");
		assertZahlungsauftrag(zahlungsauftrag, 1);
		zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate*2);
		for (int i = 0; i < countMonate; i++) {
			int position = i*2;
			// Pro Monat gibt es eine Korrekturbuchung und eine Neubuchung
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position), ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, 1074.40);
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position+1), ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN, -1289.30d);
		}

		// Eine weitere (verfuegte) Mutation, welche den Elternbeitrag erhoeht:
		createMutationEinkommen(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), true);
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Auftrag mit Mutation des Lohns (Elternbeitrag)");
		assertZahlungsauftrag(zahlungsauftrag, 1);
		zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate*2);
		for (int i = 0; i < countMonate; i++) {
			int position = i*2;
			// Pro Monat gibt es eine Korrekturbuchung und eine Neubuchung
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position), ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG, 923.40);
			assertZahlungsdetail(zahlung.getZahlungspositionen().get(position+1), ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG, -1074.40d);
		}

		// Eine (NICHT verfuegte) Mutation erstellen -> Keine Auswirkungen!
		createMutationBetreuungspensum(erstgesuch, gesuchsperiode.getGueltigkeit().getGueltigAb(), false);
		zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Auftrag ohne neue Mutation");
		assertZahlungsauftrag(zahlungsauftrag, 0);
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
		Assert.assertEquals(VerfuegungsZeitabschnittStatus.VERRECHNET, zahlungsposition.getVerfuegungZeitabschnitt().getStatus());
	}

	@Test
	public void zahlungsauftragAusloesen() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag");

		Assert.assertFalse(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).get().getAusgeloest());
		zahlungService.zahlungsauftragAusloesen(zahlungsauftrag.getId());
		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).get().getAusgeloest());
	}

	@Test
	public void findZahlungsauftrag() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag");

		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
		Assert.assertFalse(zahlungService.findZahlungsauftrag("ungueltigeId").isPresent());
	}

	@Test
	public void deleteZahlungsauftrag() throws Exception {
		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag");

		Assert.assertTrue(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
		zahlungService.deleteZahlungsauftrag(zahlungsauftrag.getId());
		Assert.assertFalse(zahlungService.findZahlungsauftrag(zahlungsauftrag.getId()).isPresent());
	}

	@Test
	public void getAllZahlungsauftraege() throws Exception {
		Assert.assertTrue(zahlungService.getAllZahlungsauftraege().isEmpty());

		createGesuch(true);
		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag");
		Assert.assertFalse(zahlungService.getAllZahlungsauftraege().isEmpty());
	}

	@Test
	public void createIsoFile() throws Exception {
		//TODO (team) Test
	}

	@Test
	public void zahlungBestaetigen() throws Exception {
		// Anzahl Zahlungen: Anzahl Monate seit Periodenbeginn, inkl. dem aktuellen
		long countMonate = ChronoUnit.MONTHS.between(gesuchsperiode.getGueltigkeit().getGueltigAb(), LocalDate.now())+1;
		createGesuch(true);

		Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(DATUM_FAELLIG, "Testauftrag");
		assertZahlungsauftrag(zahlungsauftrag, 1);
		Zahlung zahlung = zahlungsauftrag.getZahlungen().get(0);
		assertZahlung(zahlung, countMonate);
		Assert.assertEquals(ZahlungStatus.AUSGELOEST, zahlung.getStatus());

		zahlung = zahlungService.zahlungBestaetigen(zahlung.getId());
		Assert.assertNotNull(zahlung);
		Assert.assertEquals(ZahlungStatus.BESTAETIGT, zahlung.getStatus());
	}

	protected Gesuchsperiode createGesuchsperiode(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createCurrentGesuchsperiode();
		JAHR_1 = gesuchsperiode.getBasisJahrPlus1();
		JAHR_2 = gesuchsperiode.getBasisJahrPlus2();
		gesuchsperiode.setActive(active);
		return gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
	}

	private Gesuch createGesuch(boolean verfuegen) {
		return testfaelleService.createAndSaveTestfaelle(TestfaelleService.BeckerNora, verfuegen, verfuegen);
	}

	private Gesuch createMutation(Gesuch erstgesuch, boolean verfuegen) {
		return testfaelleService.mutierenHeirat(erstgesuch.getFall().getFallNummer(),
			erstgesuch.getGesuchsperiode().getId(), LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 15), LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 15), verfuegen);
	}

	private Gesuch createMutationBetreuungspensum(Gesuch erstgesuch, LocalDate eingangsdatum, boolean verfuegen) {
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			List<Betreuung> betreuungs = mutation.extractAllBetreuungen();
			for (Betreuung betreuung : betreuungs) {
				Set<BetreuungspensumContainer> betreuungspensumContainers = betreuung.getBetreuungspensumContainers();
				for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
					betreuungspensumContainer.getBetreuungspensumJA().setPensum(50);
				}
			}
			gesuchService.createGesuch(mutation);
			testfaelleService.gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			return mutation;
		}
		return gesuchOptional.orElse(null);
	}

	private Gesuch createMutationEinkommen(Gesuch erstgesuch, LocalDate eingangsdatum, boolean verfuegen) {
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			mutation.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(60000d));
			gesuchService.createGesuch(mutation);
			testfaelleService.gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			return mutation;
		}
		return gesuchOptional.orElse(null);
	}
}
