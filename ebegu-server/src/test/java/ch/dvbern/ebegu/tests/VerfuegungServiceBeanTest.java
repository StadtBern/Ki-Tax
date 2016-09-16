package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.services.FinanzielleSituationService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.VerfuegungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Optional;

import static ch.dvbern.ebegu.enums.EbeguParameterKey.*;
import static ch.dvbern.ebegu.rechner.AbstractBGRechnerTest.checkTestfall01WaeltiDagmar;

/**
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class VerfuegungServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private InstitutionService instService;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void saveVerfuegung() {
		Assert.assertNotNull(verfuegungService); //init funktioniert
		Betreuung betreuung = insertBetreuung();
		Assert.assertNull(betreuung.getVerfuegung());
		Verfuegung verfuegung = new Verfuegung();
		verfuegung.setBetreuung(betreuung);
		betreuung.setVerfuegung(verfuegung);
		verfuegungService.saveVerfuegung(verfuegung);

	}

	@Test
	public void findVerfuegung() {
		Verfuegung verfuegung = insertVerfuegung();
		Optional<Verfuegung> loadedVerf = this.verfuegungService.findVerfuegung(verfuegung.getId());
		Assert.assertTrue(loadedVerf.isPresent());
		Assert.assertEquals(verfuegung, loadedVerf.get());
	}

	@Test
	public void calculateVerfuegung() {

		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit());
		finanzielleSituationService.calculateFinanzDaten(gesuch);
		Gesuch berechnetesGesuch = this.verfuegungService.calculateVerfuegung(gesuch);
		Assert.assertNotNull(berechnetesGesuch);
		Assert.assertNotNull((berechnetesGesuch.getKindContainers().iterator().next()));
		Assert.assertNotNull((berechnetesGesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next()));
		Assert.assertNotNull(berechnetesGesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung());
		Verfuegung verfuegung = berechnetesGesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung();
		checkTestfall01WaeltiDagmar(gesuch);

	}

	@Test
	public void getAll() {
		Verfuegung verfuegung = insertVerfuegung();
		Verfuegung verfuegung2 = insertVerfuegung();
		Collection<Verfuegung> allVerfuegungen = this.verfuegungService.getAllVerfuegungen();
		Assert.assertEquals(2, allVerfuegungen.size());
		Assert.assertTrue(allVerfuegungen.stream().allMatch(currentVerfuegung -> currentVerfuegung.equals(verfuegung) || currentVerfuegung.equals(verfuegung2)));
	}


	@Test
	public void removeVerfuegung() {
		Verfuegung verfuegung = insertVerfuegung();
		this.verfuegungService.removeVerfuegung(verfuegung);
	}

	//Helpers


	private Betreuung insertBetreuung() {
		return TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence)
			.getKindContainers().iterator().next().getBetreuungen().iterator().next();
	}

	private Verfuegung insertVerfuegung() {
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		Assert.assertNull(betreuung.getVerfuegung());
		Verfuegung verfuegung = new Verfuegung();
		verfuegung.setBetreuung(betreuung);
		betreuung.setVerfuegung(verfuegung);
		return persistence.persist(verfuegung);

	}

	private void prepareParameters(DateRange gueltigkeit) {

		LocalDate year1Start = LocalDate.of(gueltigkeit.getGueltigAb().getYear(), Month.JANUARY, 1);
		LocalDate year1End = LocalDate.of(gueltigkeit.getGueltigAb().getYear(), Month.DECEMBER, 31);
		saveParameter(PARAM_ABGELTUNG_PRO_TAG_KANTON, "107.19", new DateRange(year1Start, year1End));
		saveParameter(PARAM_ABGELTUNG_PRO_TAG_KANTON, "107.19", new DateRange(year1Start.plusYears(1), year1End.plusYears(1)));
		saveParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, "7", gueltigkeit);
		saveParameter(PARAM_ANZAL_TAGE_MAX_KITA, "244", gueltigkeit);
		saveParameter(PARAM_STUNDEN_PRO_TAG_MAX_KITA, "11.5", gueltigkeit);
		saveParameter(PARAM_KOSTEN_PRO_STUNDE_MAX, "11.91", gueltigkeit);
		saveParameter(PARAM_KOSTEN_PRO_STUNDE_MIN, "0.75", gueltigkeit);
		saveParameter(PARAM_MASSGEBENDES_EINKOMMEN_MAX, "158690", gueltigkeit);
		saveParameter(PARAM_MASSGEBENDES_EINKOMMEN_MIN, "42540", gueltigkeit);
		saveParameter(PARAM_ANZAHL_TAGE_KANTON, "240", gueltigkeit);
		saveParameter(PARAM_STUNDEN_PRO_TAG_TAGI, "7", gueltigkeit);
		saveParameter(PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN, "9.16", gueltigkeit);
		saveParameter(PARAM_BABY_ALTER_IN_MONATEN, "12", gueltigkeit);  //waere eigentlich int
		saveParameter(PARAM_BABY_FAKTOR, "1.5", gueltigkeit);
		saveParameter(PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3, "3760", gueltigkeit);
		saveParameter(PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4, "5900", gueltigkeit);
		saveParameter(PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5, "6970", gueltigkeit);
		saveParameter(PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6, "7500", gueltigkeit);

		Assert.assertEquals(18, ebeguParameterService.getAllEbeguParameter().size()); //es muessen min 14 existieren jetzt

	}

	private void saveParameter(EbeguParameterKey key, String value, DateRange gueltigkeit) {
		EbeguParameter ebeguParameter = new EbeguParameter(key, value, gueltigkeit);
		persistence.persist(ebeguParameter);

	}
}

