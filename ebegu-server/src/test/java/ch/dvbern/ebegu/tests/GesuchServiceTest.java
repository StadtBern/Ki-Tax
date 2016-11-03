package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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

import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static ch.dvbern.ebegu.tets.TestDataUtil.createAndPersistFeutzYvonneGesuch;

/**
 * Arquillian Tests fuer die Klasse GesuchService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchServiceTest extends AbstractEbeguTest {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private InstitutionService institutionService;

	private int anzahlObjekte = 0;

	@Test
	public void createGesuch() {
		Assert.assertNotNull(gesuchService);
		TestDataUtil.createAndPersistBenutzer(persistence);
		persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		Assert.assertEquals(1, allGesuche.size());
	}

	@Test
	public void updateGesuch() {
		Assert.assertNotNull(gesuchService);
		TestDataUtil.createAndPersistBenutzer(persistence);
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
		TestDataUtil.createAndPersistBenutzer(persistence);
		final Gesuch gesuch = persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);
		Assert.assertEquals(1, gesuchService.getAllGesuche().size());

		final List<WizardStep> wizardStepsFromGesuch = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());
		wizardStepsFromGesuch.forEach(wizardStep -> persistence.remove(WizardStep.class, wizardStep.getId()));
		gesuchService.removeGesuch(gesuch);
		Assert.assertEquals(0, gesuchService.getAllGesuche().size());
	}

	@Test
	public void createEinkommensverschlechterungsGesuch() {
		Assert.assertNotNull(gesuchService);
		TestDataUtil.createAndPersistBenutzer(persistence);
		persistEinkommensverschlechterungEntity();
		final Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		Assert.assertEquals(1, allGesuche.size());
		Gesuch gesuch = allGesuche.iterator().next();
		final EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.getEinkommensverschlechterungInfo();
		Assert.assertNotNull(einkommensverschlechterungInfo);
		Assert.assertTrue(einkommensverschlechterungInfo.getEinkommensverschlechterung());
		Assert.assertTrue(einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus1());
		Assert.assertFalse(einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2());
	}

	@Test
	public void testGetAllActiveGesucheAllActive() {
		TestDataUtil.createAndPersistBenutzer(persistence);
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.IN_BEARBEITUNG_JA);

		final Collection<Gesuch> allActiveGesuche = gesuchService.getAllActiveGesuche();
		Assert.assertEquals(2, allActiveGesuche.size());
	}

	@Test
	public void testGetAllActiveGesucheNotAllActive() {
		TestDataUtil.createAndPersistBenutzer(persistence);
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.VERFUEGT);

		final Collection<Gesuch> allActiveGesuche = gesuchService.getAllActiveGesuche();
		Assert.assertEquals(1, allActiveGesuche.size());
	}

	@Test
	public void testSearchAntraegeOrder() {
		TestDataUtil.createDummyAdminAnonymous(persistence);
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
		TestDataUtil.createDummyAdminAnonymous(persistence);
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
		TestDataUtil.createDummyAdminAnonymous(persistence);

		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuchsperiode periode = TestDataUtil.createGesuchsperiode1617();

		Gesuchsperiode nextPeriode  = TestDataUtil.createGesuchsperiode1617();
		nextPeriode.setGueltigkeit(new DateRange(periode.getGueltigkeit().getGueltigAb().plusYears(1), periode.getGueltigkeit().getGueltigBis().plusYears(1)));
		nextPeriode = persistence.merge(nextPeriode);
		gesuch.setGesuchsperiode(nextPeriode);
		gesuch = persistence.merge(gesuch);
		Assert.assertEquals("gesuch fuer naechste Periode muss vorhanden sein" ,gesuch.getGesuchsperiode(), nextPeriode);
		gesuchService.findGesuch(gesuch.getId());

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Assert.assertEquals("2016/2017", periode.getGesuchsperiodeString());
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString(periode.getGesuchsperiodeString());

		Pair<Long, List<Gesuch>> firstResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), firstResult.getLeft());

		Assert.assertEquals("2017/2018", nextPeriode.getGesuchsperiodeString());
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString(nextPeriode.getGesuchsperiodeString());

		Pair<Long, List<Gesuch>> result = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(1), result.getLeft());
		Assert.assertEquals(gesuch.getId(), result.getRight().get(0).getId());

	}

	@Test
	public void testSearchWithRoleGesuchsteller() {
		Benutzer user = TestDataUtil.createDummyAdminAnonymous(persistence);
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> firstResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), firstResult.getLeft());

		user.setRole(UserRole.GESUCHSTELLER); //Gesuchsteller darf nichts suchen
		persistence.merge(user);
		Pair<Long, List<Gesuch>> secondResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(0), secondResult.getLeft());
		Assert.assertEquals(0, secondResult.getRight().size());
	}

	@Test
	public void testSearchWithRoleSachbearbeiterInst() {
		Benutzer user = TestDataUtil.createDummyAdminAnonymous(persistence);
		Gesuch gesDagmar =  TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesDagmar.setStatus(AntragStatus.IN_BEARBEITUNG_GS);
		gesDagmar = persistence.merge(gesDagmar);
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
		gesuch = persistence.merge(gesuch);

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> firstResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(1), firstResult.getLeft()); //Admin sieht Gesuch im Status IN_BEARBEITUNG_GS nicht, soll anscheinend so sein

		//Die Gesuche sollten im Status IN_BEARBEITUNG_GS sein und zu keinem oder einem Traegerschafts Sachbearbeiter gehoeren, trotzdem sollten wir sie finden
		user.setRole(UserRole.SACHBEARBEITER_INSTITUTION); //Sachbearbeiter findet gesuche die bei seiner institution sind
		//kita aaregg
		user.setTraegerschaft(null);
		user.setInstitution(gesuch.extractAllBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution());
		user = persistence.merge(user);
		Pair<Long, List<Gesuch>> secondResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), secondResult.getLeft());
		Assert.assertEquals(2, secondResult.getRight().size());

		gesuch.getFall().setVerantwortlicher(null);
		persistence.merge(gesuch);
		//traegerschaftbenutzer setzten
		Traegerschaft traegerschaft = user.getInstitution().getTraegerschaft();
		Assert.assertNotNull("Unser testaufbau sieht vor, dass die institution zu einer traegerschaft gehoert",traegerschaft);
		Benutzer verantwortlicherUser = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, traegerschaft, null, TestDataUtil.createDefaultMandant());
		gesDagmar.getFall().setVerantwortlicher(verantwortlicherUser);
		gesDagmar = persistence.merge(gesDagmar);
		//es muessen immer noch beide gefunden werden da die betreuungen immer noch zu inst des users gehoeren
		Pair<Long, List<Gesuch>> thirdResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(2), thirdResult.getLeft());
		Assert.assertEquals(2, thirdResult.getRight().size());

		//aendere user zu einer anderen institution  -> darf nichts mehr finden
		Institution institution = TestDataUtil.createAndPersistDefaultInstitution(persistence);
		user.setInstitution(institution);
		persistence.merge(user);

		Pair<Long, List<Gesuch>> fourthResult =  gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(0), fourthResult.getLeft());
		Assert.assertEquals(0, fourthResult.getRight().size());

	}


	@Test
	public void testAntragMutieren() throws Exception {

		TestDataUtil.createDummyAdminAnonymous(persistence);

		// Voraussetzung: Ich habe einen verfuegten Antrag
		Gesuch gesuchVerfuegt = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuchVerfuegt.setStatus(AntragStatus.VERFUEGT);
		gesuchVerfuegt = gesuchService.updateGesuch(gesuchVerfuegt, true);
		Mutationsdaten mutationsdaten = new Mutationsdaten();
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(gesuchVerfuegt.getId(), mutationsdaten, LocalDate.of(1980, Month.MARCH, 25));

		Assert.assertTrue(gesuchOptional.isPresent());
		Assert.assertEquals(AntragTyp.MUTATION, gesuchOptional.get().getTyp());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, gesuchOptional.get().getStatus());
		Assert.assertEquals(gesuchVerfuegt.getFall(), gesuchOptional.get().getFall());
		Assert.assertTrue(gesuchOptional.get().isNew());

		// Sicherstellen, dass alle Objekte kopiert und nicht referenziert sind.
		// Anzahl erstellte Objekte zaehlen, es muessen im Gesuch und in der Mutation
		// gleich viele sein
		Gesuch mutation = gesuchOptional.get();
		Familiensituation oldFamSit = new Familiensituation(mutation.getFamiliensituationErstgesuch());
		mutation.setFamiliensituationErstgesuch(null);
		mutation = gesuchService.createGesuch(mutation);
		mutation.setFamiliensituationErstgesuch(oldFamSit);
		mutation = gesuchService.updateGesuch(mutation, false);

        anzahlObjekte = 0;
        Set<String> idsErstgesuch = new HashSet<>();
        findAllIdsOfAbstractEntities(gesuchVerfuegt, idsErstgesuch);
        int anzahlObjekteErstgesuch = anzahlObjekte;

        anzahlObjekte = 0;
        Set<String> idsMutation = new HashSet<>();
        findAllIdsOfAbstractEntities(mutation, idsMutation);
        int anzahlObjekteMutation = anzahlObjekte;

		// Die Mutation hat immer 2 Objekte mehr als Erstgesuch, und zwar "Mutationsdaten" und "FamiliensituationErstgesuch.
		// Deswegen muessen wir 2 subtrahieren
        Assert.assertEquals(anzahlObjekteErstgesuch, anzahlObjekteMutation - 1 - 1);

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
			if (entity instanceof Fall|| entity instanceof Mandant || entity instanceof Gesuchsperiode) {
				// Diese Entitaeten wurden korrekterweise nur umgehaengt und nicht kopiert.
				// Aus der Liste entfernen
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
		gesuch.setStatus(status);
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

	private Gesuch persistEinkommensverschlechterungEntity() {
		final Gesuch gesuch = TestDataUtil.createDefaultEinkommensverschlechterungsGesuch();
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

}
