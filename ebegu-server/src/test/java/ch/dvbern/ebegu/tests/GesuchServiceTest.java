package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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
import java.util.*;

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
		TestDataUtil.createAndPersistBenutzer(persistence);
		persistNewEntity(AntragStatus.ERSTE_MAHNUNG);
		persistNewEntity(AntragStatus.VERFUEGT);

		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getSort().setPredicate("fallNummer");
		//nach fallnummer geordnete liste
		Pair<Long, List<Gesuch>> resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), resultpair.getLeft());
		List<Gesuch> foundGesuche = resultpair.getRight();
		Assert.assertEquals(gesuch.getId(), foundGesuche.get(2).getId());
		//genau anders rum ordnen
		filterDTO.getSort().setReverse(true);
		resultpair = gesuchService.searchAntraege(filterDTO);
		Assert.assertEquals(new Long(3), resultpair.getLeft());
		List<Gesuch> foundGesucheReversed = resultpair.getRight();
		Assert.assertEquals(gesuch.getId(), foundGesucheReversed.get(0).getId());

	}

	@Test
	public void testAntragMutieren() throws Exception {

		TestDataUtil.createAndPersistBenutzer(persistence);
		// Voraussetzung: Ich habe einen verfuegten Antrag
		Gesuch gesuchVerfuegt = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);
		gesuchVerfuegt.setStatus(AntragStatus.VERFUEGT);
		gesuchVerfuegt = gesuchService.updateGesuch(gesuchVerfuegt, true);
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(gesuchVerfuegt.getId());

		Assert.assertTrue(gesuchOptional.isPresent());
		Assert.assertEquals(AntragTyp.MUTATION, gesuchOptional.get().getTyp());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, gesuchOptional.get().getStatus());
		Assert.assertEquals(gesuchVerfuegt.getFall(), gesuchOptional.get().getFall());
		Assert.assertTrue(gesuchOptional.get().isNew());

		// Sicherstellen, dass alle Objekte kopiert und nicht referenziert sind.
		// Anzahl erstellte Objekte zaehlen, es muessen im Gesuch und in der Mutation
		// gleich viele sein
		Gesuch mutation = gesuchOptional.get();
		mutation = gesuchService.createGesuch(mutation);

        anzahlObjekte = 0;
        Set<String> idsErstgesuch = new HashSet<>();
        findAllIdsOfAbstractEntities(gesuchVerfuegt, idsErstgesuch);
        int anzahlObjekteErstgesuch = anzahlObjekte;

        anzahlObjekte = 0;
        Set<String> idsMutation = new HashSet<>();
        findAllIdsOfAbstractEntities(mutation, idsMutation);
        int anzahlObjekteMutation = anzahlObjekte;

        Assert.assertEquals(anzahlObjekteErstgesuch, anzahlObjekteMutation);

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
