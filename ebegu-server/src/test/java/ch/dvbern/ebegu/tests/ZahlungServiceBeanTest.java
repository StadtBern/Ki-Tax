package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.services.ZahlungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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
import java.time.Month;
import java.util.List;

/**
 * Created by hefr on 06.02.17.
 */
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




	@Before
	public void init() {
		final Gesuchsperiode gesuchsperiode = createGesuchsperiode(true);
		final Mandant mandant = insertInstitutionen();
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

		List<String> gesuchIdsOfAktuellerAntrag = gesuchService.findGesuchIdsOfAktuellerAntrag(verfuegtesGesuch.getGesuchsperiode());
		Assert.assertNotNull(gesuchIdsOfAktuellerAntrag);

		Assert.assertTrue(gesuchIdsOfAktuellerAntrag.contains(verfuegtesGesuch.getId()));
		Assert.assertFalse(gesuchIdsOfAktuellerAntrag.contains(nichtVerfuegtesGesuch.getId()));

		Assert.assertTrue(gesuchIdsOfAktuellerAntrag.contains(erstgesuchMitMutation.getId()));
		Assert.assertFalse(gesuchIdsOfAktuellerAntrag.contains(nichtVerfuegteMutation.getId()));

		Assert.assertFalse(gesuchIdsOfAktuellerAntrag.contains(erstgesuchMitVerfuegterMutation.getId()));
		Assert.assertTrue(gesuchIdsOfAktuellerAntrag.contains(verfuegteMutation.getId()));
	}

	@Test
	public void zahlungsauftragErstellen() throws Exception {

	}

	@Test
	public void zahlungsauftragAusloesen() throws Exception {

	}

	@Test
	public void findZahlungsauftrag() throws Exception {

	}

	@Test
	public void deleteZahlungsauftrag() throws Exception {

	}

	@Test
	public void getAllZahlungsauftraege() throws Exception {

	}

	@Test
	public void createIsoFile() throws Exception {

	}

	@Test
	public void zahlungBestaetigen() throws Exception {

	}

	private Gesuch createGesuch(boolean verfuegen) {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.BeckerNora, verfuegen, verfuegen);
		return gesuch;
	}

	private Gesuch createMutation(Gesuch erstgesuch, boolean verfuegen) {
		final Gesuch mutation = testfaelleService.mutierenHeirat(erstgesuch.getFall().getFallNummer(),
			erstgesuch.getGesuchsperiode().getId(), LocalDate.of(2016, Month.DECEMBER, 15), LocalDate.of(2017, Month.JANUARY, 15), true);
		return mutation;
	}
}
