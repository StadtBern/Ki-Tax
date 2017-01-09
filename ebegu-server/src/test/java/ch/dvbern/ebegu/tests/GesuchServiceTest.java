package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static ch.dvbern.ebegu.tets.TestDataUtil.createAndPersistFeutzYvonneGesuch;
import static ch.dvbern.ebegu.tets.util.JBossLoginContextFactory.createLoginContext;

/**
 * Arquillian Tests fuer die Klasse GesuchService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchServiceTest extends AbstractEbeguLoginTest {

	private final Logger LOG = LoggerFactory.getLogger(GesuchServiceTest.class.getSimpleName());

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;
	@Inject
	private DokumentGrundService dokumentGrundService;
	@Inject
	private GeneratedDokumentService generatedDokumentService;
	@Inject
	private FallService fallService;
	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private InstitutionService institutionService;

	private int anzahlObjekte = 0;

	@Test
	public void createGesuch() {
		Assert.assertNotNull(gesuchService);
		loginAsSachbearbeiterJA();
		persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Collection<Gesuch> allGesuche = readGesucheAsAdmin();
		Assert.assertEquals(1, allGesuche.size());
	}

	@Test
	public void updateGesuch() {
		Assert.assertNotNull(gesuchService);
		loginAsSachbearbeiterJA();
		final Gesuch insertedGesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Optional<Gesuch> gesuch = gesuchService.findGesuch(insertedGesuch.getId());
		Assert.assertEquals(insertedGesuch.getFall().getId(), gesuch.get().getFall().getId());

		gesuch.get().setFall(persistence.persist(TestDataUtil.createDefaultFall()));
		final Gesuch updated = gesuchService.updateGesuch(gesuch.get(), false);
		Assert.assertEquals(updated.getFall().getId(), gesuch.get().getFall().getId());

	}

	@Test
	public void removeGesuchTest() {
		Assert.assertNotNull(gesuchService);
		final Gesuch gesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);
		final GeneratedDokument generatedDokument = TestDataUtil.createGeneratedDokument(gesuch);
		persistence.persist(generatedDokument);
		final DokumentGrund dokumentGrund = TestDataUtil.createDefaultDokumentGrund();
		dokumentGrund.setGesuch(gesuch);
		persistence.persist(dokumentGrund);

		//check all objects exist
		Assert.assertEquals(1, readGesucheAsAdmin().size());
		final List<WizardStep> wizardSteps = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());
		Assert.assertEquals(13, wizardSteps.size());
		final Collection<AntragStatusHistory> allAntragStatHistory = antragStatusHistoryService.findAllAntragStatusHistoryByGesuch(gesuch);
		Assert.assertEquals(1, allAntragStatHistory.size());
		final Collection<DokumentGrund> allDokGrund = dokumentGrundService.findAllDokumentGrundByGesuch(gesuch);
		Assert.assertEquals(1, allDokGrund.size());
		final Collection<GeneratedDokument> allGenDok = generatedDokumentService.findGeneratedDokumentsFromGesuch(gesuch);
		Assert.assertEquals(1, allGenDok.size());


		gesuchService.removeGesuch(gesuch);


		//check all objects don't exist anymore
		final Collection<Gesuch> gesuche = readGesucheAsAdmin();
		Assert.assertEquals(0, gesuche.size());
		final List<WizardStep> wizardStepsRemoved = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());
		Assert.assertEquals(0, wizardStepsRemoved.size());
		final Collection<AntragStatusHistory> allAntStHistRemoved = antragStatusHistoryService.findAllAntragStatusHistoryByGesuch(gesuch);
		Assert.assertEquals(0, allAntStHistRemoved.size());
		final Collection<DokumentGrund> allDokGrundRemoved = dokumentGrundService.findAllDokumentGrundByGesuch(gesuch);
		Assert.assertEquals(0, allDokGrundRemoved.size());
		final Collection<GeneratedDokument> allGenDokRemoved = generatedDokumentService.findGeneratedDokumentsFromGesuch(gesuch);
		Assert.assertEquals(0, allGenDokRemoved.size());
	}

	@Test
	public void createEinkommensverschlechterungsGesuch() {
		Assert.assertNotNull(gesuchService);
		loginAsSachbearbeiterJA();
		persistEinkommensverschlechterungEntity();
		final Collection<Gesuch> allGesuche = readGesucheAsAdmin();
		Assert.assertEquals(1, allGesuche.size());
		Gesuch gesuch = allGesuche.iterator().next();
		final EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.extractEinkommensverschlechterungInfo();
		Assert.assertNotNull(einkommensverschlechterungInfo);
		Assert.assertTrue(einkommensverschlechterungInfo.getEinkommensverschlechterung());
		Assert.assertTrue(einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus1());
		Assert.assertFalse(einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2());
	}

	@Test
	public void testGetAllActiveGesucheAllActive() {
		loginAsSachbearbeiterJA();
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Collection<Gesuch> allActiveGesuche = gesuchService.getAllActiveGesuche();
		Assert.assertEquals(2, allActiveGesuche.size());
	}

	@Test
	public void testGetAllActiveGesucheNotAllActive() {
		loginAsSachbearbeiterJA();
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.VERFUEGT);

		final Collection<Gesuch> allActiveGesuche = gesuchService.getAllActiveGesuche();
		Assert.assertEquals(1, allActiveGesuche.size());
	}

	@Test
	public void testSearchAntraegeOrder() {
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.VERFUEGT);

		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		final Gesuch gesuch2 = createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		final Gesuch gesuch3 = createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getSort().setPredicate("fallNummer");
		filterDTO.getSort().setReverse(true);     //aufsteigend
		//nach fallnummer geordnete liste
		Pair<Long, List<Gesuch>> resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(5), resultpair.getLeft());
		List<Gesuch> foundGesuche = resultpair.getRight();
		Assert.assertEquals(gesuch.getId(), foundGesuche.get(2).getId());
		Assert.assertEquals(gesuch3.getId(), foundGesuche.get(4).getId());
		//genau anders rum ordnen
		filterDTO.getSort().setReverse(false); //absteigend
		resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(5), resultpair.getLeft());
		List<Gesuch> foundGesucheReversed = resultpair.getRight();
		Assert.assertEquals(gesuch3.getId(), foundGesucheReversed.get(0).getId());

	}

	@Test
	public void testPaginationEdgeCases() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getPagination().setStart(0);
		filterDTO.getPagination().setNumber(10);
		//max 10 resultate davon 3 gefunden
		Pair<Long, List<Gesuch>> resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), resultpair.getLeft());
		Assert.assertEquals(3, resultpair.getRight().size());

		//max 0 resultate -> leere liste
		filterDTO.getPagination().setStart(0);
		filterDTO.getPagination().setNumber(0);
		Pair<Long, List<Gesuch>> noresult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), noresult.getLeft()); //wir erwarten 0 Resultate aber count 3
		Assert.assertEquals(0, noresult.getRight().size());

		filterDTO.getPagination().setStart(0);
		filterDTO.getPagination().setNumber(2);
		Pair<Long, List<Gesuch>> twopages = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), twopages.getLeft());
		Assert.assertEquals(2, twopages.getRight().size());

	}

	@Test
	public void testSearchByGesuchsperiode() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuchsperiode periode = TestDataUtil.createGesuchsperiode1617();

		Gesuchsperiode nextPeriode = TestDataUtil.createGesuchsperiode1617();
		nextPeriode.setGueltigkeit(new DateRange(periode.getGueltigkeit().getGueltigAb().plusYears(1), periode.getGueltigkeit().getGueltigBis().plusYears(1)));
		nextPeriode = persistence.merge(nextPeriode);
		gesuch.setGesuchsperiode(nextPeriode);
		gesuch = persistence.merge(gesuch);
		Assert.assertEquals("gesuch fuer naechste Periode muss vorhanden sein", gesuch.getGesuchsperiode(), nextPeriode);
		gesuchService.findGesuch(gesuch.getId());

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Assert.assertEquals("2016/2017", periode.getGesuchsperiodeString());
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString(periode.getGesuchsperiodeString());

		Pair<Long, List<Gesuch>> firstResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), firstResult.getLeft());

		Assert.assertEquals("2017/2018", nextPeriode.getGesuchsperiodeString());
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString(nextPeriode.getGesuchsperiodeString());

		Pair<Long, List<Gesuch>> result = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(1), result.getLeft());
		Assert.assertEquals(gesuch.getId(), result.getRight().get(0).getId());

		//search nach kurzem string
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString("2017/18");
		Pair<Long, List<Gesuch>> thirdResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(1), thirdResult.getLeft());
		Assert.assertEquals(gesuch.getId(), thirdResult.getRight().get(0).getId());

	}

	@Test
	public void testSearchWithRoleGesuchsteller() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> firstResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), firstResult.getLeft());

		loginAsGesuchsteller("gesuchst");
		Pair<Long, List<Gesuch>> secondResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(0), secondResult.getLeft());
		Assert.assertEquals(0, secondResult.getRight().size());
	}

	@Test
	public void testSearchWithRoleSachbearbeiterInst() {
		loginAsAdmin();
		Gesuch gesDagmar = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesDagmar.setStatus(AntragStatus.IN_BEARBEITUNG_GS);
		gesDagmar = persistence.merge(gesDagmar);
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
		gesuch = persistence.merge(gesuch);

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> firstResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(1), firstResult.getLeft()); //Admin sieht Gesuch im Status IN_BEARBEITUNG_GS nicht, soll anscheinend so sein

		//Die Gesuche sollten im Status IN_BEARBEITUNG_GS sein und zu keinem oder einem Traegerschafts Sachbearbeiter gehoeren, trotzdem sollten wir sie finden
//		Benutzer user = TestDataUtil.createDummySuperAdmin(persistence);
		//kita Weissenstein
		Institution institutionToSet = gesuch.extractAllBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution();
		loginAsSachbearbeiterInst("sainst", institutionToSet);
		Pair<Long, List<Gesuch>> secondResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), secondResult.getLeft());
		Assert.assertEquals(2, secondResult.getRight().size());

		gesuch.getFall().setVerantwortlicher(null);
		persistence.merge(gesuch);
		//traegerschaftbenutzer setzten
		Traegerschaft traegerschaft = institutionToSet.getTraegerschaft();
		Assert.assertNotNull("Unser testaufbau sieht vor, dass die institution zu einer traegerschaft gehoert", traegerschaft);
		Benutzer verantwortlicherUser = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "anonymous", traegerschaft, null, TestDataUtil.createDefaultMandant());
		gesDagmar.getFall().setVerantwortlicher(verantwortlicherUser);
		gesDagmar = persistence.merge(gesDagmar);
		//es muessen immer noch beide gefunden werden da die betreuungen immer noch zu inst des users gehoeren
		Pair<Long, List<Gesuch>> thirdResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), thirdResult.getLeft());
		Assert.assertEquals(2, thirdResult.getRight().size());

		//aendere user zu einer anderen institution  -> darf nichts mehr finden
		Institution otherInst = TestDataUtil.createAndPersistDefaultInstitution(persistence);
		loginAsSachbearbeiterInst("sainst2", otherInst);

		Pair<Long, List<Gesuch>> fourthResult = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(0), fourthResult.getLeft());
		Assert.assertEquals(0, fourthResult.getRight().size());

	}

	@Test
	public void testAntragMutieren() throws Exception {

		// Voraussetzung: Ich habe einen verfuegten Antrag
		Gesuch gesuchVerfuegt = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuchVerfuegt.setStatus(AntragStatus.VERFUEGT);
		gesuchVerfuegt = gesuchService.updateGesuch(gesuchVerfuegt, true);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(gesuchVerfuegt.getId(), LocalDate.of(1980, Month.MARCH, 25));

		Assert.assertTrue(gesuchOptional.isPresent());
		Assert.assertEquals(AntragTyp.MUTATION, gesuchOptional.get().getTyp());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, gesuchOptional.get().getStatus());
		Assert.assertEquals(gesuchVerfuegt.getFall(), gesuchOptional.get().getFall());
		Assert.assertTrue(gesuchOptional.get().isNew());

		// Sicherstellen, dass alle Objekte kopiert und nicht referenziert sind.
		// Anzahl erstellte Objekte zaehlen, es muessen im Gesuch und in der Mutation
		// gleich viele sein
		Gesuch mutation = gesuchOptional.get();

		anzahlObjekte = 0;
		Set<String> idsErstgesuch = new HashSet<>();
		findAllIdsOfAbstractEntities(gesuchVerfuegt, idsErstgesuch);
		int anzahlObjekteErstgesuch = anzahlObjekte;

		anzahlObjekte = 0;
		Set<String> idsMutation = new HashSet<>();
		findAllIdsOfAbstractEntities(mutation, idsMutation);
		int anzahlObjekteMutation = anzahlObjekte;

		// Die Mutation hat immer 1 Objekte mehr als Erstgesuch, und die "FamiliensituationErstgesuch.
		// Deswegen muessen wir 1 subtrahieren
		Assert.assertEquals(anzahlObjekteErstgesuch, anzahlObjekteMutation - 1);

		// Ids, welche in beiden Gesuchen vorkommen ermitteln. Die meisten Objekte muessen kopiert
		// werden, es gibt aber Ausnahmen, wo eine Referenz kopiert wird.
		Set<String> intersection = new HashSet<>(idsErstgesuch);
		intersection.retainAll(idsMutation);
		if (!intersection.isEmpty()) {
			// Die korrekterweise umgehaengten Ids rausnehmen
			findAbstractEntitiesWithIds(gesuchVerfuegt, intersection);
		}
		// Jetzt sollten keine Ids mehr drinn sein.
		Assert.assertTrue(intersection.isEmpty());
	}

	@Test
	public void testAntragEinreichenAndFreigeben() {
		LocalDate now = LocalDate.now();
		final Gesuch gesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_GS);

		final Gesuch eingereichtesGesuch = gesuchService.antragFreigabequittungErstellen(gesuch, AntragStatus.FREIGABEQUITTUNG);

		Assert.assertEquals(AntragStatus.FREIGABEQUITTUNG, eingereichtesGesuch.getStatus());
		Assert.assertFalse(now.isAfter(eingereichtesGesuch.getFreigabeDatum())); // beste Art um Datum zu testen die direkt in der Methode erzeugt werden

		final WizardStep wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(gesuch.getId(), WizardStepName.FREIGABE);

		Assert.assertEquals(WizardStepStatus.OK, wizardStepFromGesuch.getWizardStepStatus());

		Gesuch eingelesenesGesuch = gesuchService.antragFreigeben(eingereichtesGesuch.getId(), null);
		Assert.assertEquals(AntragStatus.FREIGEGEBEN, eingelesenesGesuch.getStatus());
	}

	@Test
	public void testAntragEinreichenAndFreigebenNurSchulamt() {
		LocalDate now = LocalDate.now();
		Gesuch gesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_GS);
		Assert.assertEquals(0, gesuch.getKindContainers().size());
		Assert.assertFalse(gesuch.hasOnlyBetreuungenOfSchulamt());  //alle leer gilt aktuell als only schulamt = false

		Gesuch schulamtGesuch = persistNewNurSchulamtGesuchEntity(AntragStatus.IN_BEARBEITUNG_GS);

		Assert.assertEquals(2, schulamtGesuch.getKindContainers().size());
		Assert.assertTrue(schulamtGesuch.hasOnlyBetreuungenOfSchulamt());
		final Gesuch eingereichtesGesuch = gesuchService.antragFreigabequittungErstellen(schulamtGesuch, AntragStatus.FREIGABEQUITTUNG);

		Assert.assertEquals(AntragStatus.FREIGABEQUITTUNG, eingereichtesGesuch.getStatus());
		Assert.assertFalse(now.isAfter(eingereichtesGesuch.getFreigabeDatum()));
		final WizardStep wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(schulamtGesuch.getId(), WizardStepName.FREIGABE);
		Assert.assertEquals(WizardStepStatus.OK, wizardStepFromGesuch.getWizardStepStatus());
		loginAsSchulamt();
		Gesuch eingelesenesGesuch = gesuchService.antragFreigeben(eingereichtesGesuch.getId(), null);
		Assert.assertEquals(AntragStatus.NUR_SCHULAMT, eingelesenesGesuch.getStatus());

	}

	@Test
	public void testStatusuebergangToInBearbeitungJAIFFreigegeben() {
		//bei Freigegeben soll ein lesen eines ja benutzers dazu fuehren dass das gesuch in bearbeitung ja wechselt
		Gesuch gesuch = persistNewEntity(AntragStatus.FREIGEGEBEN, Eingangsart.ONLINE);
		gesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertEquals(AntragStatus.FREIGEGEBEN, gesuch.getStatus());
		loginAsSachbearbeiterJA();
		//durch findGesuch setzt der {@link UpdateStatusToInBearbeitungJAInterceptor} den Status um
		Optional<Gesuch> foundGesuch = gesuchService.findGesuch(gesuch.getId());
		Assert.assertTrue(foundGesuch.isPresent());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, foundGesuch.get().getStatus());
	}

	@Test
	public void testNoStatusuebergangToInBearbeitungJAIFNurSchulamt() {
		//bei schulamt fuehrt das lesen zu keinem wechsel des Gesuchstatus
		final Gesuch gesuch = persistNewNurSchulamtGesuchEntity(AntragStatus.NUR_SCHULAMT);
		Assert.assertTrue(gesuch.hasOnlyBetreuungenOfSchulamt());
		Assert.assertEquals(AntragStatus.NUR_SCHULAMT, gesuch.getStatus());
		loginAsSchulamt();
		Optional<Gesuch> foundGesuch = gesuchService.findGesuch(gesuch.getId());
		Assert.assertTrue(foundGesuch.isPresent());
		Assert.assertEquals(AntragStatus.NUR_SCHULAMT, foundGesuch.get().getStatus());
	}

	@Test
	public void testJAAntragMutierenWhenOnlineMutationExists() {
		loginAsGesuchsteller("gesuchst");
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);

		gesuch.setStatus(AntragStatus.VERFUEGT);
		gesuch = gesuchService.updateGesuch(gesuch, true);
		final Optional<Gesuch> optMutation = gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());

		gesuchService.createGesuch(optMutation.get());

		loginAsSachbearbeiterJA();
		try {
			gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());
			Assert.fail("Exception should be thrown. There is already an open Mutation");
		} catch (EbeguRuntimeException e) {
			// nop
		}

		optMutation.get().setStatus(AntragStatus.VERFUEGT);
		gesuchService.updateGesuch(optMutation.get(), true);
		gesuchService.antragMutieren(gesuch.getId(), LocalDate.now()); // nach dem die Mutation verfuegt ist, darf man es wieder mutieren
	}

	@Test
	public void testGetAllGesucheForFallAndPeriod() {
		final Fall fall = fallService.saveFall(TestDataUtil.createDefaultFall());

		final Gesuchsperiode gesuchsperiode1516 = TestDataUtil.createCustomGesuchsperiode(2015, 2016);
		final Gesuchsperiode periodeToUpdate = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode1516);

		final Gesuchsperiode otherPeriod = gesuchsperiodeService.saveGesuchsperiode(TestDataUtil.createCustomGesuchsperiode(2014, 2015));

		Gesuch gesuch1516_1 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1516_1);
		Gesuch gesuch1516_2 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1516_2);
		Gesuch gesuch1415_1 = TestDataUtil.createGesuch(fall, otherPeriod, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1415_1);

		final List<Gesuch> allGesuche_1 = gesuchService.getAllGesucheForFallAndPeriod(gesuch1516_1.getFall(), gesuch1516_1.getGesuchsperiode());
		Assert.assertEquals(2, allGesuche_1.size());

		final List<Gesuch> allGesuche_2 = gesuchService.getAllGesucheForFallAndPeriod(gesuch1516_1.getFall(), gesuch1415_1.getGesuchsperiode());
		Assert.assertEquals(1, allGesuche_2.size());
	}

	@Test
	public void testSetBeschwerdeHaengigForPeriode() {
		final Fall fall = fallService.saveFall(TestDataUtil.createDefaultFall());

		final Gesuchsperiode gesuchsperiode1516 = TestDataUtil.createCustomGesuchsperiode(2015, 2016);
		final Gesuchsperiode periodeToUpdate = 	gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode1516);

		final Gesuchsperiode otherPeriod = 	gesuchsperiodeService.saveGesuchsperiode(TestDataUtil.createCustomGesuchsperiode(2014, 2015));

		Gesuch gesuch1516_1 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1516_1);
		Gesuch gesuch1516_2 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1516_2);
		Gesuch gesuch1415_1 = TestDataUtil.createGesuch(fall, otherPeriod, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1415_1);

		gesuchService.setBeschwerdeHaengigForPeriode(gesuch1516_1);

		// Pruefen dass nur die Gesuche der gegebenen Periode den Status BESCHWERDE_HAENGIG haben
		final List<String> allGesuchIDsForFall = gesuchService.getAllGesuchIDsForFall(gesuch1516_1.getFall().getId());
		Assert.assertEquals(3, allGesuchIDsForFall.size());

		allGesuchIDsForFall.forEach(gesuchID -> {
			final Optional<Gesuch> foundGesuch = gesuchService.findGesuch(gesuchID);
			Assert.assertTrue(foundGesuch.isPresent());
			if (foundGesuch.get().getGesuchsperiode().isSame(periodeToUpdate)) {
				if (foundGesuch.get().equals(gesuch1516_1)) {
					Assert.assertEquals(AntragStatus.BESCHWERDE_HAENGIG, foundGesuch.get().getStatus());
				}
				else {
					Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, foundGesuch.get().getStatus()); // vergleicht mit IN_BEARBEITUNG_JA weil es so gesetzt wurde
				}
				Assert.assertTrue(foundGesuch.get().isGesperrtWegenBeschwerde());
			}
			else {
				Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, foundGesuch.get().getStatus()); // vergleicht mit IN_BEARBEITUNG_JA weil es so gesetzt wurde
				Assert.assertFalse(foundGesuch.get().isGesperrtWegenBeschwerde());
			}
		});
	}

	@Test
	public void testRemoveBeschwerdeHaengigForPeriode() {
		final Fall fall = fallService.saveFall(TestDataUtil.createDefaultFall());

		final Gesuchsperiode gesuchsperiode1516 = TestDataUtil.createCustomGesuchsperiode(2015, 2016);
		final Gesuchsperiode periodeToUpdate = 	gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode1516);

		final Gesuchsperiode otherPeriod = 	gesuchsperiodeService.saveGesuchsperiode(TestDataUtil.createCustomGesuchsperiode(2014, 2015));

		Gesuch gesuch1516_1 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1516_1);
		Gesuch gesuch1516_2 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1516_2);
		Gesuch gesuch1415_1 = TestDataUtil.createGesuch(fall, otherPeriod, AntragStatus.IN_BEARBEITUNG_JA);
		persistence.persist(gesuch1415_1);

		gesuchService.setBeschwerdeHaengigForPeriode(gesuch1516_1);
		gesuchService.removeBeschwerdeHaengigForPeriode(gesuch1516_1);

		// Pruefen dass nur die Gesuche der gegebenen Periode den Status BESCHWERDE_HAENGIG haben
		final List<String> allGesuchIDsForFall = gesuchService.getAllGesuchIDsForFall(gesuch1516_1.getFall().getId());
		Assert.assertEquals(3, allGesuchIDsForFall.size());

		allGesuchIDsForFall.forEach(gesuchID -> {
			final Optional<Gesuch> foundGesuch = gesuchService.findGesuch(gesuchID);
			Assert.assertTrue(foundGesuch.isPresent());
			if (foundGesuch.get().getGesuchsperiode().isSame(periodeToUpdate)) {
				if (foundGesuch.get().equals(gesuch1516_1)) {
					Assert.assertEquals(AntragStatus.VERFUEGT, foundGesuch.get().getStatus());
				}
				else {
					Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, foundGesuch.get().getStatus());
				}
			}
			else {
				Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, foundGesuch.get().getStatus());
			}
			Assert.assertFalse(foundGesuch.get().isGesperrtWegenBeschwerde());
		});
	}


	// HELP METHOD


	/**
	 * Schreibt alle Ids von AbstractEntities (rekursiv vom Gesuch) ins Set.
	 */
	private void findAllIdsOfAbstractEntities(AbstractEntity entity, Set<String> ids) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String id = BeanUtils.getProperty(entity, "id");
		if (!ids.contains(id)) {
			anzahlObjekte = anzahlObjekte + 1;
			ids.add(id);
			PropertyUtilsBean bean = new PropertyUtilsBean();
			PropertyDescriptor[] propertyDescriptors = bean.getPropertyDescriptors(entity.getClass());
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				Object property = bean.getProperty(entity, propertyDescriptor.getName());
				if (property instanceof AbstractEntity) {
					findAllIdsOfAbstractEntities((AbstractEntity) property, ids);
				}
			}
		}
	}

	/**
	 * Ermittelt die Entites, deren Ids im Set enthalten sind.
	 */
	private void findAbstractEntitiesWithIds(AbstractEntity entity, Set<String> ids) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if (ids.isEmpty()) {
			return;
		}
		String id = BeanUtils.getProperty(entity, "id");
		if (ids.contains(id)) {
			if (entity instanceof Fall || entity instanceof Mandant || entity instanceof Gesuchsperiode || entity instanceof Familiensituation) {
				// Diese Entitaeten wurden korrekterweise nur umgehaengt und nicht kopiert.
				// Aus der Liste entfernen
				// Familiensituation wird hier ebenfalls aufgefuehrt, da sie bei FamiliensituationErstgescuh nur umgehaengt wird
				// (die "normale" Familiensituation wird aber kopiert, dies wird jetzt nicht mehr getestet)
				ids.remove(id);
			}
		}
		PropertyUtilsBean bean = new PropertyUtilsBean();
		PropertyDescriptor[] propertyDescriptors = bean.getPropertyDescriptors(entity.getClass());
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			Object property = bean.getProperty(entity, propertyDescriptor.getName());
			if (property instanceof AbstractEntity) {
				findAbstractEntitiesWithIds((AbstractEntity) property, ids);
			}
		}
	}


	private Gesuch persistNewEntity(AntragStatus status) {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setEingangsart(Eingangsart.PAPIER);
		gesuch.setStatus(status);
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

	private Gesuch persistNewEntity(AntragStatus status, Eingangsart eingangsart) {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setEingangsart(eingangsart);
		gesuch.setStatus(status);
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

	private Gesuch persistNewNurSchulamtGesuchEntity(AntragStatus status) {
		Gesuch gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuch.setStatus(status);
		gesuch.setEingangsart(Eingangsart.PAPIER);
		wizardStepService.createWizardStepList(gesuch);
		gesuch.getKindContainers().stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.forEach((betreuung)
				-> {
				betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESSCHULE);
				persistence.merge(betreuung.getInstitutionStammdaten());
			});
		return persistence.merge(gesuch);
	}

	private Gesuch persistEinkommensverschlechterungEntity() {
		final Gesuch gesuch = TestDataUtil.createDefaultEinkommensverschlechterungsGesuch();
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

	private void loginAsSachbearbeiterJA() {
		try {
			createLoginContext("saja", "saja").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt saja for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer saja = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.persist(saja);
	}

	private void loginAsAdmin() {
		try {
			createLoginContext("admin", "admin").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt admin for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer admin = TestDataUtil.createBenutzer(UserRole.ADMIN, "admin", null, null, mandant);
		persistence.persist(admin);
	}

	private void loginAsSachbearbeiterInst(String username, Institution institutionToSet) {
		Benutzer user = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_INSTITUTION, username, null, institutionToSet, institutionToSet.getMandant());
		user = persistence.merge(user);
		try {
			createLoginContext(username, username).login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt {} for tests", username);
		}
		//theoretisch sollten wir wohl zuerst ausloggen bevor wir wieder einloggen aber es scheint auch so zu gehen
	}

	private void loginAsGesuchsteller(String username) {
		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer user = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, username, null, null, mandant);
		user = persistence.merge(user);
		try {
			createLoginContext(username, username).login();
		} catch (LoginException e) {
			LOG.error("could not login as gesuchsteller {} for tests", username);
		}
		//theoretisch sollten wir wohl zuerst ausloggen bevor wir wieder einloggen aber es scheint auch so zu gehen
	}

	private void loginAsSchulamt() {
		try {
			createLoginContext("schulamt", "schulamt").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter schulamt for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer schulamt = TestDataUtil.createBenutzer(UserRole.SCHULAMT, "schulamt", null, null, mandant);
		persistence.persist(schulamt);
	}


	/**
	 * da nur der admin getAllGesuche aufrufen darf logen wir uns kurz als admin ein und dann weider aus ...
	 */
	@Nonnull
	private Collection<Gesuch> readGesucheAsAdmin() {

		Collection<Gesuch> foundGesuche = Collections.emptyList();

		LoginContext loginContext = null;
		try {
			loginContext = JBossLoginContextFactory.createLoginContext("admin", "admin");
			loginContext.login();

			foundGesuche = Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Collection<Gesuch>>() {

				@Override
				public Collection<Gesuch> run() {
					Collection<Gesuch> gesuche = gesuchService.getAllGesuche();
					return gesuche;
				}

			});


		} catch (LoginException e) {
			LOG.error("Could not login as admin to read Gesuche");
		} finally {
			if (loginContext != null) {
				try {
					loginContext.logout();
				} catch (LoginException e) {
					LOG.error("could not logout");
				}
			}
		}

		return foundGesuche;
	}

}
