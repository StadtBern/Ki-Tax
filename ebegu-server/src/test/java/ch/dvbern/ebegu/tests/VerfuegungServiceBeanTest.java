package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ch.dvbern.ebegu.rechner.AbstractBGRechnerTest.checkTestfall01WaeltiDagmar;

/**
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class VerfuegungServiceBeanTest extends AbstractEbeguLoginTest {

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

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private GesuchService gesuchService;


	@Test
	public void saveVerfuegung() {
		Assert.assertNotNull(verfuegungService); //init funktioniert
		Betreuung betreuung = insertBetreuung();
		Assert.assertNull(betreuung.getVerfuegung());
		Verfuegung verfuegung = new Verfuegung();
		verfuegung.setBetreuung(betreuung);
		betreuung.setVerfuegung(verfuegung);
		Verfuegung persistedVerfuegung = verfuegungService.verfuegen(verfuegung, betreuung.getId(), false);
		Betreuung persistedBetreuung = persistence.find(Betreuung.class, betreuung.getId());
		Assert.assertEquals(persistedVerfuegung.getBetreuung(), persistedBetreuung);
		Assert.assertEquals(persistedBetreuung.getVerfuegung(), persistedVerfuegung);
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

		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);
		Assert.assertEquals(20, ebeguParameterService.getAllEbeguParameter().size()); //es muessen min 20 existieren jetzt
		finanzielleSituationService.calculateFinanzDaten(gesuch);
		Gesuch berechnetesGesuch = this.verfuegungService.calculateVerfuegung(gesuch);
		Assert.assertNotNull(berechnetesGesuch);
		Assert.assertNotNull((berechnetesGesuch.getKindContainers().iterator().next()));
		Assert.assertNotNull((berechnetesGesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next()));
		Assert.assertNotNull(berechnetesGesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung());
		checkTestfall01WaeltiDagmar(gesuch);
	}

	@Test
	public void findVorgaengerVerfuegung() {
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(2016, Month.MARCH, 25));
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		Set<KindContainer> kindContainers = gesuch.getKindContainers();
		KindContainer kind = kindContainers.iterator().next();
		Assert.assertEquals(kindContainers.size(), 1);
		Set<Betreuung> betreuungen = kind.getBetreuungen();
		betreuungen.forEach(this::createAndPersistVerfuegteVerfuegung);
		Betreuung betreuung = betreuungen.iterator().next();
		Integer betreuungNummer = betreuung.getBetreuungNummer();
		Verfuegung verfuegung1 = betreuung.getVerfuegung();
		gesuch.setStatus(AntragStatus.VERFUEGT);
		Set<AntragStatusHistory> antragStatusHistories = gesuch.getAntragStatusHistories();
		AntragStatusHistory antragStatusHistory = new AntragStatusHistory();
		antragStatusHistory.setBenutzer(TestDataUtil.createAndPersistBenutzer(persistence));
		antragStatusHistory.setStatus(AntragStatus.VERFUEGT);
		antragStatusHistory.setGesuch(gesuch);
		antragStatusHistory.setTimestampVon(LocalDateTime.of(2016, Month.APRIL, 1, 0, 0));
		antragStatusHistories.add(antragStatusHistory);
		gesuch.getGesuchsteller1().getAdressen().get(0).setGesuchstellerContainer(gesuch.getGesuchsteller1());
		persistence.persist(gesuch.getGesuchsteller1().getAdressen().get(0));
		persistence.persist(antragStatusHistory);
		persistence.merge(gesuch);

		Optional<Gesuch> gesuchOptional = this.gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());
		Assert.assertTrue(gesuchOptional.isPresent());
		Gesuch mutation = persistence.merge(gesuchOptional.get());

		List<Betreuung> allBetreuungenFromGesuch = this.betreuungService.findAllBetreuungenFromGesuch(mutation.getId());
		Optional<Betreuung> optFolgeBetreeung = allBetreuungenFromGesuch.stream().filter(b -> b.getBetreuungNummer().equals(betreuungNummer)).findAny();
		Assert.assertTrue(optFolgeBetreeung.isPresent());
		Optional<Verfuegung> optVorherigeVerfuegungBetreuung = this.verfuegungService.findVorgaengerVerfuegung(optFolgeBetreeung.get());
		Assert.assertTrue(optVorherigeVerfuegungBetreuung.isPresent());
		Assert.assertEquals(optVorherigeVerfuegungBetreuung.get(), verfuegung1);
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
		Betreuung betreuung = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25))
			.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		betreuung.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
		return persistence.merge(betreuung);
	}

	private Verfuegung insertVerfuegung() {
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		return createAndPersistVerfuegteVerfuegung(betreuung);
	}

	private Verfuegung createAndPersistVerfuegteVerfuegung(Betreuung betreuung) {
		betreuung.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
		Assert.assertNull(betreuung.getVerfuegung());
		Verfuegung verfuegung = new Verfuegung();
		verfuegung.setBetreuung(betreuung);
		betreuung.setVerfuegung(verfuegung);
		return persistence.persist(verfuegung);
	}

}

