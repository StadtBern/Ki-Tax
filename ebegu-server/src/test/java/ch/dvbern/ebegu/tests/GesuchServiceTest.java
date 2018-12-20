
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.ejb.EJBAccessException;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchDeletionLog;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.entities.Zahlungsposition;
import ch.dvbern.ebegu.enums.AnmeldungMutationZustand;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.enums.FinSitStatus;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.enums.GesuchDeletionCause;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.AntragStatusHistoryService;
import ch.dvbern.ebegu.services.ApplicationPropertyService;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GeneratedDokumentService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.MitteilungService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.services.ZahlungService;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Arquillian Tests fuer die Klasse GesuchService
 */
@SuppressWarnings("LocalVariableNamingConvention")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchServiceTest extends AbstractEbeguLoginTest {

	private final Logger LOG = LoggerFactory.getLogger(GesuchServiceTest.class.getSimpleName());

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Persistence persistence;
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
	private MitteilungService mitteilungService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private ZahlungService zahlungService;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private BetreuungService betreuungService;

	public static final int ANZAHL_TAGE_BIS_WARNUNG_FREIGABE = 60;
	public static final int ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG = 15;
	public static final int ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE = 60;
	public static final int ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG = 90;

	@Test
	public void createGesuch() {
		Assert.assertNotNull(gesuchService);
		loginAsSachbearbeiterJA();
		TestDataUtil.persistNewGesuchInStatus(AntragStatus.IN_BEARBEITUNG_JA, persistence, gesuchService);

		final Collection<Gesuch> allGesuche = readGesucheAsAdmin();
		Assert.assertEquals(1, allGesuche.size());
	}

	@Test
	public void updateGesuch() {
		Assert.assertNotNull(gesuchService);
		loginAsSachbearbeiterJA();
		final Gesuch insertedGesuch = TestDataUtil.persistNewGesuchInStatus(AntragStatus.IN_BEARBEITUNG_JA, persistence, gesuchService);

		final Optional<Gesuch> gesuch = gesuchService.findGesuch(insertedGesuch.getId());
		Assert.assertTrue(gesuch.isPresent());
		Assert.assertEquals(insertedGesuch.getFall().getId(), gesuch.get().getFall().getId());

		gesuch.get().setFall(persistence.persist(TestDataUtil.createDefaultFall()));
		final Gesuch updated = gesuchService.updateGesuch(gesuch.get(), false, null);
		Assert.assertEquals(updated.getFall().getId(), gesuch.get().getFall().getId());

	}

	@Test
	public void removeGesuchTest() {
		Assert.assertNotNull(gesuchService);
		final Gesuch gesuch = TestDataUtil.persistNewGesuchInStatus(AntragStatus.IN_BEARBEITUNG_JA, persistence, gesuchService);
		insertInstitutionen();
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);
		Collection<InstitutionStammdaten> stammdaten = criteriaQueryHelper.getAll(InstitutionStammdaten.class);
		Gesuch gesuch2 = testfaelleService.createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuch.getGesuchsperiode(), stammdaten), true, null);
		final GeneratedDokument generatedDokument = TestDataUtil.createGeneratedDokument(gesuch);
		persistence.persist(generatedDokument);
		final DokumentGrund dokumentGrund = TestDataUtil.createDefaultDokumentGrund();
		dokumentGrund.setGesuch(gesuch);
		persistence.persist(dokumentGrund);
		zahlungService.zahlungsauftragErstellen(LocalDate.now(), "Testauftrag", gesuch2.getGesuchsperiode().getGueltigkeit().getGueltigAb().plusMonths(1).atTime(0, 0, 0));

		//check all objects exist
		Assert.assertEquals(2, readGesucheAsAdmin().size());
		final List<WizardStep> wizardSteps = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());
		Assert.assertEquals(13, wizardSteps.size());
		final Collection<AntragStatusHistory> allAntragStatHistory = antragStatusHistoryService.findAllAntragStatusHistoryByGesuch(gesuch);
		Assert.assertEquals(1, allAntragStatHistory.size());
		final Collection<DokumentGrund> allDokGrund = dokumentGrundService.findAllDokumentGrundByGesuch(gesuch);
		Assert.assertEquals(1, allDokGrund.size());
		final Collection<GeneratedDokument> allGenDok = generatedDokumentService.findGeneratedDokumentsFromGesuch(gesuch);
		Assert.assertEquals(1, allGenDok.size());
		Collection<Zahlungsposition> zahlungspositionen = criteriaQueryHelper.getAll(Zahlungsposition.class);
		Assert.assertEquals(2, zahlungspositionen.size());

		gesuchService.removeGesuch(gesuch.getId(), GesuchDeletionCause.UNBEKANNT);
		gesuchService.removeGesuch(gesuch2.getId(), GesuchDeletionCause.UNBEKANNT);

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
		Collection<Zahlungsposition> zahlungspositionenRemoved = criteriaQueryHelper.getAll(Zahlungsposition.class);
		Assert.assertEquals(0, zahlungspositionenRemoved.size());

		// Check Logeintrag
		Collection<GesuchDeletionLog> logEintraege = criteriaQueryHelper.getAll(GesuchDeletionLog.class);
		Assert.assertNotNull(logEintraege);
		Assert.assertEquals(2, logEintraege.size());
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
	public void testAntragMutieren() throws Exception {

		// Voraussetzung: Ich habe einen verfuegten Antrag
		Gesuch gesuchVerfuegt = createSimpleVerfuegtesGesuch();

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

		Map<String, AbstractEntity> entitiesErstgesuch = new TreeMap<>();
		findAllIdsOfAbstractEntities(gesuchVerfuegt, entitiesErstgesuch, 0);
		int anzahlObjekteErstgesuch = entitiesErstgesuch.size();

		Map<String, AbstractEntity> entitiesMutation = new TreeMap<>();
		findAllIdsOfAbstractEntities(mutation, entitiesMutation, 0);
		int anzahlObjekteMutation = entitiesMutation.size();

		// Die Mutation hat immer 1 Objekte mehr als Erstgesuch, und die "FamiliensituationErstgesuch.
		// Deswegen muessen wir 1 subtrahieren
		Assert.assertEquals(anzahlObjekteErstgesuch, anzahlObjekteMutation - 1);

		Set<String> intersection = new HashSet<>(entitiesErstgesuch.keySet());
		intersection.retainAll(entitiesMutation.keySet());
		Assert.assertEquals(0, intersection.size());
	}

	@Test
	public void testAntragErneuern() throws Exception {

		// Voraussetzung: Ich habe einen Antrag, er muss nicht verfuegt sein
		Gesuch erstgesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuchsperiode gpFolgegesuch = new Gesuchsperiode();
		gpFolgegesuch.getGueltigkeit().setGueltigAb(erstgesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb().plusYears(1));
		gpFolgegesuch.getGueltigkeit().setGueltigBis(erstgesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis().plusYears(1));
		gpFolgegesuch = persistence.persist(gpFolgegesuch);
		Optional<Gesuch> gesuchOptional = gesuchService.antragErneuern(erstgesuch.getId(), gpFolgegesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));

		Assert.assertTrue(gesuchOptional.isPresent());
		Gesuch folgegesuch = gesuchOptional.get();

		Assert.assertEquals(AntragTyp.ERNEUERUNGSGESUCH, folgegesuch.getTyp());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, folgegesuch.getStatus());
		Assert.assertEquals(erstgesuch.getFall(), folgegesuch.getFall());
		Assert.assertTrue(folgegesuch.isNew());

		// Sicherstellen, dass alle Objekte kopiert und nicht referenziert sind.
		// Anzahl erstellte Objekte zaehlen, es muessen im Gesuch und in der Mutation
		// gleich viele sein
		Map<String, AbstractEntity> entitiesErstgesuch = new TreeMap<>();
		findAllIdsOfAbstractEntities(erstgesuch, entitiesErstgesuch, 0);

		Map<String, AbstractEntity> entitiesFolgegesuch = new TreeMap<>();
		findAllIdsOfAbstractEntities(folgegesuch, entitiesFolgegesuch, 0);
		int anzahlObjekteFolgegesuch = entitiesFolgegesuch.size();
		Assert.assertEquals(11, anzahlObjekteFolgegesuch);

		Set<String> intersection = new HashSet<>(entitiesErstgesuch.keySet());
		intersection.retainAll(entitiesFolgegesuch.keySet());
		Assert.assertEquals(0, intersection.size());
	}

	@Test
	public void testAntragEinreichenAndFreigeben() {
		LocalDate now = LocalDate.now();
		final Gesuch gesuch = TestDataUtil.persistNewGesuchInStatus(AntragStatus.IN_BEARBEITUNG_GS, persistence, gesuchService);

		final Gesuch eingereichtesGesuch = gesuchService.antragFreigabequittungErstellen(gesuch, AntragStatus.FREIGABEQUITTUNG);

		Assert.assertEquals(AntragStatus.FREIGABEQUITTUNG, eingereichtesGesuch.getStatus());
		Assert.assertNotNull(eingereichtesGesuch.getFreigabeDatum());
		Assert.assertFalse(now.isAfter(eingereichtesGesuch.getFreigabeDatum())); // beste Art um Datum zu testen die direkt in der Methode erzeugt werden

		final WizardStep wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(gesuch.getId(), WizardStepName.FREIGABE);

		Assert.assertEquals(WizardStepStatus.OK, wizardStepFromGesuch.getWizardStepStatus());

		Benutzer sachbearbeiterJA = loginAsSachbearbeiterJA();
		Gesuch eingelesenesGesuch = gesuchService.antragFreigeben(eingereichtesGesuch.getId(), sachbearbeiterJA.getUsername(), null);
		Assert.assertEquals(AntragStatus.FREIGEGEBEN, eingelesenesGesuch.getStatus());
	}

	@Test
	public void testExceptionOnInvalidFreigabe() {
		LocalDate now = LocalDate.now();
		final Gesuch gesuch = TestDataUtil.persistNewGesuchInStatus(AntragStatus.IN_BEARBEITUNG_GS, persistence, gesuchService);

		final Gesuch eingereichtesGesuch = gesuchService.antragFreigabequittungErstellen(gesuch, AntragStatus.FREIGABEQUITTUNG);

		Assert.assertEquals(AntragStatus.FREIGABEQUITTUNG, eingereichtesGesuch.getStatus());
		Assert.assertNotNull(eingereichtesGesuch.getFreigabeDatum());
		Assert.assertFalse(now.isAfter(eingereichtesGesuch.getFreigabeDatum())); // beste Art um Datum zu testen die direkt in der Methode erzeugt werden

		final WizardStep wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(gesuch.getId(), WizardStepName.FREIGABE);

		Assert.assertEquals(WizardStepStatus.OK, wizardStepFromGesuch.getWizardStepStatus());

		Benutzer gesuchsteller = loginAsGesuchsteller("gesuchst");
		try {
			gesuchService.antragFreigeben(eingereichtesGesuch.getId(), null, null);
			Assert.fail("No Besitzer is present. must fail for Role Gesuchsteller");
		} catch (EJBAccessException e) {
			//noop
		}

		gesuch.getFall().setBesitzer(gesuchsteller);
		persistence.merge(gesuch.getFall());
		Gesuch eingelesenesGesuch = gesuchService.antragFreigeben(eingereichtesGesuch.getId(), null, null);
		Assert.assertEquals(AntragStatus.FREIGEGEBEN, eingelesenesGesuch.getStatus());
		try {
			gesuchService.antragFreigeben(eingereichtesGesuch.getId(), null, null);
			Assert.fail("Gesuch is already freigegeben. Wrong state should be detected");
		} catch (EbeguRuntimeException e) {
			Assert.assertEquals("Das Gesuch wurde bereits freigegeben", e.getCustomMessage());
		}
	}

	@Test
	public void testAntragEinreichenAndFreigebenNurSchulamt() {
		LocalDate now = LocalDate.now();
		Gesuch gesuch = TestDataUtil.persistNewGesuchInStatus(AntragStatus.IN_BEARBEITUNG_GS, persistence, gesuchService);
		Assert.assertEquals(0, gesuch.getKindContainers().size());
		Assert.assertFalse(gesuch.hasOnlyBetreuungenOfSchulamt());  //alle leer gilt aktuell als only schulamt = false

		Gesuch schulamtGesuch = persistNewNurSchulamtGesuchEntity(AntragStatus.IN_BEARBEITUNG_GS);

		Assert.assertEquals(2, schulamtGesuch.getKindContainers().size());
		Assert.assertTrue(schulamtGesuch.hasOnlyBetreuungenOfSchulamt());
		final Gesuch eingereichtesGesuch = gesuchService.antragFreigabequittungErstellen(schulamtGesuch, AntragStatus.FREIGABEQUITTUNG);

		Assert.assertEquals(AntragStatus.FREIGABEQUITTUNG, eingereichtesGesuch.getStatus());
		Assert.assertNotNull(eingereichtesGesuch.getFreigabeDatum());
		Assert.assertFalse(now.isAfter(eingereichtesGesuch.getFreigabeDatum()));
		final WizardStep wizardStepFromGesuch = wizardStepService.findWizardStepFromGesuch(schulamtGesuch.getId(), WizardStepName.FREIGABE);
		Assert.assertEquals(WizardStepStatus.OK, wizardStepFromGesuch.getWizardStepStatus());

		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);
		Benutzer schulamt = loginAsSchulamt();
		Gesuch eingelesenesGesuch = gesuchService.antragFreigeben(eingereichtesGesuch.getId(), schulamt.getUsername(), null);
		Assert.assertEquals(AntragStatus.FREIGEGEBEN, eingelesenesGesuch.getStatus());

	}

	@Test
	public void testStatusuebergangToInBearbeitungSTV_VERFUEGT() {
		//wenn das Gesuch nicht im Status PRUEFUNG_STV ist, wird nichts gemacht
		Gesuch gesuch = persistNewEntity(AntragStatus.VERFUEGT, Eingangsart.ONLINE);
		gesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertEquals(AntragStatus.VERFUEGT, gesuch.getStatus());
		loginAsSteueramt();
		//durch findGesuch setzt der {@link UpdateStatusInterceptor} den Status um
		try {
			gesuchService.findGesuch(gesuch.getId());
			Assert.fail("It should crash because STV has no rights to see VERFUEGT");
		} catch (EJBAccessException e) {
			// nop
		}
	}

	@Test
	public void testStatusuebergangToInBearbeitungSTV_PRUEFUNGSTV() {
		//Wenn das Gesuch im Status PRUEFUNG_STV ist, wechselt der Status beim Ablesen auf IN_BEARBEITUNG_STV
		Gesuch gesuch = persistNewEntity(AntragStatus.PRUEFUNG_STV, Eingangsart.ONLINE);
		gesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertEquals(AntragStatus.PRUEFUNG_STV, gesuch.getStatus());
		loginAsSteueramt();
		//durch findGesuch setzt der {@link UpdateStatusInterceptor} den Status um
		Optional<Gesuch> foundGesuch = gesuchService.findGesuch(gesuch.getId());
		Assert.assertTrue(foundGesuch.isPresent());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_STV, foundGesuch.get().getStatus());
	}

	@Test
	public void testStatusuebergangToInBearbeitungJAIFFreigegeben() {
		//bei Freigegeben soll ein lesen eines ja benutzers dazu fuehren dass das gesuch in bearbeitung ja wechselt
		Gesuch gesuch = persistNewEntity(AntragStatus.FREIGEGEBEN, Eingangsart.ONLINE);
		gesuch = persistence.find(Gesuch.class, gesuch.getId());
		Assert.assertEquals(AntragStatus.FREIGEGEBEN, gesuch.getStatus());
		loginAsSachbearbeiterJA();
		//durch findGesuch setzt der {@link UpdateStatusInterceptor} den Status um
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
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence, AntragStatus.VERFUEGT);
		loginAsSachbearbeiterJA();
		gesuch.setGueltig(true);
		gesuch.setTimestampVerfuegt(LocalDateTime.now());
		gesuch = gesuchService.updateGesuch(gesuch, true, null);
		loginAsGesuchsteller("gesuchst");
		final Optional<Gesuch> optMutation = gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());

		Assert.assertTrue(optMutation.isPresent());
		gesuchService.createGesuch(optMutation.get());

		loginAsSachbearbeiterJA();
		try {
			gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());
			Assert.fail("Exception should be thrown. There is already an open Mutation");
		} catch (EbeguRuntimeException e) {
			// nop
		}
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
		final Gesuchsperiode periodeToUpdate = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode1516);

		final Gesuchsperiode otherPeriod = gesuchsperiodeService.saveGesuchsperiode(TestDataUtil.createCustomGesuchsperiode(2014, 2015));

		Gesuch gesuch1516_1 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1516_1);
		Gesuch gesuch1516_2 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1516_2);
		Gesuch gesuch1415_1 = TestDataUtil.createGesuch(fall, otherPeriod, AntragStatus.VERFUEGT);
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
				} else {
					Assert.assertEquals(AntragStatus.VERFUEGT, foundGesuch.get().getStatus()); // vergleicht mit IN_BEARBEITUNG_JA weil es so gesetzt wurde
				}
				Assert.assertTrue(foundGesuch.get().isGesperrtWegenBeschwerde());
			} else {
				Assert.assertEquals(AntragStatus.VERFUEGT, foundGesuch.get().getStatus()); // vergleicht mit IN_BEARBEITUNG_JA weil es so gesetzt wurde
				Assert.assertFalse(foundGesuch.get().isGesperrtWegenBeschwerde());
			}
		});
	}

	@Test
	public void testRemoveBeschwerdeHaengigForPeriode() {
		final Fall fall = fallService.saveFall(TestDataUtil.createDefaultFall());

		final Gesuchsperiode gesuchsperiode1516 = TestDataUtil.createCustomGesuchsperiode(2015, 2016);
		final Gesuchsperiode periodeToUpdate = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode1516);

		final Gesuchsperiode otherPeriod = gesuchsperiodeService.saveGesuchsperiode(TestDataUtil.createCustomGesuchsperiode(2014, 2015));

		Gesuch gesuch1516_1 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1516_1);
		Gesuch gesuch1516_2 = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1516_2);
		Gesuch gesuch1415_1 = TestDataUtil.createGesuch(fall, otherPeriod, AntragStatus.VERFUEGT);
		persistence.persist(gesuch1415_1);

		gesuch1516_1.setStatus(AntragStatus.VERFUEGT);
		final Gesuch gesuch1516_1_verfuegt = gesuchService.updateGesuch(gesuch1516_1, true, null);

		gesuchService.setBeschwerdeHaengigForPeriode(gesuch1516_1_verfuegt);
		gesuchService.removeBeschwerdeHaengigForPeriode(gesuch1516_1_verfuegt);

		// Pruefen dass nur die Gesuche der gegebenen Periode den Status BESCHWERDE_HAENGIG haben
		final List<String> allGesuchIDsForFall = gesuchService.getAllGesuchIDsForFall(gesuch1516_1.getFall().getId());
		Assert.assertEquals(3, allGesuchIDsForFall.size());

		allGesuchIDsForFall.forEach(gesuchID -> {
			final Optional<Gesuch> foundGesuch = gesuchService.findGesuch(gesuchID);
			Assert.assertTrue(foundGesuch.isPresent());
			Assert.assertEquals(AntragStatus.VERFUEGT, foundGesuch.get().getStatus());
			Assert.assertFalse(foundGesuch.get().isGesperrtWegenBeschwerde());
		});
	}

	@Test
	public void testWarnungFehlendeQuittung() throws Exception {
		insertApplicationProperties();
		Gesuch gesuch1 = createGesuchFreigabequittung(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG).minusDays(1));
		Gesuch gesuch2 = createGesuchFreigabequittung(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG));
		Gesuch gesuch3 = createGesuchFreigabequittung(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG).plusDays(1));

		Assert.assertEquals(2, gesuchService.warnFreigabequittungFehlt());
		final Optional<Gesuch> resultGesuch1 = gesuchService.findGesuch(gesuch1.getId());
		Assert.assertTrue(resultGesuch1.isPresent());
		Assert.assertNotNull(resultGesuch1.get().getDatumGewarntFehlendeQuittung());
		final Optional<Gesuch> resultGesuch2 = gesuchService.findGesuch(gesuch2.getId());
		Assert.assertTrue(resultGesuch2.isPresent());
		Assert.assertNotNull(resultGesuch2.get().getDatumGewarntFehlendeQuittung());
		final Optional<Gesuch> resultGesuch3 = gesuchService.findGesuch(gesuch3.getId());
		Assert.assertTrue(resultGesuch3.isPresent());
		Assert.assertNull(resultGesuch3.get().getDatumGewarntFehlendeQuittung());
	}

	@Test
	public void testWarnungNichtFreigegeben() throws Exception {
		insertApplicationProperties();
		Gesuch gesuch1 = createGesuchInBearbeitungGS(LocalDateTime.now().minusDays(ANZAHL_TAGE_BIS_WARNUNG_FREIGABE).minusDays(1));
		Gesuch gesuch2 = createGesuchInBearbeitungGS(LocalDateTime.now().minusDays(ANZAHL_TAGE_BIS_WARNUNG_FREIGABE));
		Gesuch gesuch3 = createGesuchInBearbeitungGS(LocalDateTime.now().minusDays(ANZAHL_TAGE_BIS_WARNUNG_FREIGABE).plusDays(1));

		Assert.assertEquals(2, gesuchService.warnGesuchNichtFreigegeben());
		final Optional<Gesuch> resultGesuch1 = gesuchService.findGesuch(gesuch1.getId());
		Assert.assertTrue(resultGesuch1.isPresent());
		Assert.assertNotNull(resultGesuch1.get().getDatumGewarntNichtFreigegeben());
		final Optional<Gesuch> resultGesuch2 = gesuchService.findGesuch(gesuch2.getId());
		Assert.assertTrue(resultGesuch2.isPresent());
		Assert.assertNotNull(resultGesuch2.get().getDatumGewarntNichtFreigegeben());
		final Optional<Gesuch> resultGesuch3 = gesuchService.findGesuch(gesuch3.getId());
		Assert.assertTrue(resultGesuch3.isPresent());
		Assert.assertNull(resultGesuch3.get().getDatumGewarntNichtFreigegeben());
	}

	@Test
	public void testDeleteGesucheOhneFreigabeOderQuittung() throws Exception {
		insertApplicationProperties();
		Gesuch gesuch1 = createGesuchInBearbeitungGS(LocalDateTime.now().minusMonths(4).minusDays(1));
		Gesuch gesuch2 = createGesuchInBearbeitungGS(LocalDateTime.now().minusMonths(4));
		Gesuch gesuch3 = createGesuchInBearbeitungGS(LocalDateTime.now().minusMonths(4).plusDays(1));
		Gesuch gesuch4 = createGesuchFreigabequittung(LocalDate.now().minusMonths(4).minusDays(1));
		Gesuch gesuch5 = createGesuchFreigabequittung(LocalDate.now().minusMonths(4));
		Gesuch gesuch6 = createGesuchFreigabequittung(LocalDate.now().minusMonths(4).plusDays(1));

		gesuch1.setDatumGewarntNichtFreigegeben(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE).minusDays(1));
		gesuch2.setDatumGewarntNichtFreigegeben(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE));
		gesuch3.setDatumGewarntNichtFreigegeben(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE).plusDays(1));
		gesuch4.setDatumGewarntFehlendeQuittung(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG).minusDays(1));
		gesuch5.setDatumGewarntFehlendeQuittung(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG));
		gesuch6.setDatumGewarntFehlendeQuittung(LocalDate.now().minusDays(ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG).plusDays(1));
		persistence.merge(gesuch1);
		persistence.merge(gesuch2);
		persistence.merge(gesuch3);
		persistence.merge(gesuch4);
		persistence.merge(gesuch5);
		persistence.merge(gesuch6);

		Assert.assertEquals(6, gesuchService.getAllGesuche().size());
		Assert.assertEquals(4, gesuchService.deleteGesucheOhneFreigabeOderQuittung());
		Assert.assertEquals(2, gesuchService.getAllGesuche().size());
	}

	@Test
	public void testRemoveOnlineMutation() {
		final Benutzer userGS = loginAsGesuchsteller("gesuchst");
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, null, AntragStatus.VERFUEGT);
		Benutzer sachbearbeiterJA = loginAsSachbearbeiterJA();
		gesuch.setGueltig(true);
		gesuch.setTimestampVerfuegt(LocalDateTime.now());
		gesuch.setStatus(AntragStatus.VERFUEGT);
		gesuch = gesuchService.updateGesuch(gesuch, true, sachbearbeiterJA);
		final Betreuung betreuungErstGesuch = gesuch.extractAllBetreuungen().get(0);

		final Optional<Gesuch> optMutation = gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());
		Assert.assertTrue(optMutation.isPresent());
		final Gesuch mutation = gesuchService.createGesuch(optMutation.get());
		final String mutationID = mutation.getId();

		Institution institutionToSet = gesuch.extractAllBetreuungen().get(0).getInstitutionStammdaten().getInstitution();
		final Benutzer saInst = loginAsSachbearbeiterInst("sainst", institutionToSet);

		Betreuungsmitteilung mitteilung = TestDataUtil.createBetreuungmitteilung(mutation.getFall(),
			userGS, MitteilungTeilnehmerTyp.JUGENDAMT, saInst, MitteilungTeilnehmerTyp.INSTITUTION);
		final Betreuung betreuungMutation = mutation.extractAllBetreuungen().get(0);
		mitteilung.setBetreuung(betreuungMutation);
		mitteilungService.sendBetreuungsmitteilung(mitteilung);

		// mitteilung belongs to the Mutation and not to the Erstgesuch
		loginAsAdmin();
		Assert.assertEquals(0, mitteilungService.findAllBetreuungsmitteilungenForBetreuung(betreuungErstGesuch).size());
		Assert.assertEquals(1, mitteilungService.findAllBetreuungsmitteilungenForBetreuung(betreuungMutation).size());

		gesuchService.removeOnlineMutation(mutation.getFall(), mutation.getGesuchsperiode());

		final Optional<Gesuch> removedMutation = gesuchService.findGesuch(mutationID);
		Assert.assertFalse(removedMutation.isPresent());

		// The Mitteilung belongs now to the Betreuung Erstgesuch
		final Collection<Betreuungsmitteilung> mitteilungenErstgesuch = mitteilungService.findAllBetreuungsmitteilungenForBetreuung(betreuungErstGesuch);
		Assert.assertEquals(1, mitteilungenErstgesuch.size());
	}

	@Test
	public void testupdateBetreuungenStatusAllWarten() {
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, null, AntragStatus.VERFUEGT);
		Assert.assertEquals(GesuchBetreuungenStatus.ALLE_BESTAETIGT, gesuch.getGesuchBetreuungenStatus()); // by default

		// 1st Betreuung=WARTEN, 2nd Betreuung=WARTEN
		gesuchService.updateBetreuungenStatus(gesuch);
		Assert.assertEquals(GesuchBetreuungenStatus.WARTEN, gesuch.getGesuchBetreuungenStatus());
	}

	@Test
	public void testupdateBetreuungenStatusBestaetigtWarten() {
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, null, AntragStatus.VERFUEGT);
		Assert.assertEquals(GesuchBetreuungenStatus.ALLE_BESTAETIGT, gesuch.getGesuchBetreuungenStatus()); // by default

		// 1st Betreuung=BESTAETIGT, 2nd Betreuung=WARTEN
		Objects.requireNonNull(gesuch.getKindContainers().iterator().next().getBetreuungen()).iterator().next().setBetreuungsstatus
			(Betreuungsstatus.BESTAETIGT);
		gesuchService.updateBetreuungenStatus(gesuch);
		Assert.assertEquals(GesuchBetreuungenStatus.WARTEN, gesuch.getGesuchBetreuungenStatus());
	}

	@Test
	public void testupdateBetreuungenStatusAlleBestaetigt() {
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, null, AntragStatus.VERFUEGT);
		Assert.assertEquals(GesuchBetreuungenStatus.ALLE_BESTAETIGT, gesuch.getGesuchBetreuungenStatus()); // by default

		// 1st Betreuung=BESTAETIGT, 2nd Betreuung=BESTAETIGT
		gesuch.extractAllBetreuungen().forEach(betreuung -> betreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT));
		gesuchService.updateBetreuungenStatus(gesuch);
		Assert.assertEquals(GesuchBetreuungenStatus.ALLE_BESTAETIGT, gesuch.getGesuchBetreuungenStatus());
	}

	@Test
	public void testupdateBetreuungenStatusAbgewiesenWarten() {
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, null, AntragStatus.VERFUEGT);
		Assert.assertEquals(GesuchBetreuungenStatus.ALLE_BESTAETIGT, gesuch.getGesuchBetreuungenStatus()); // by default

		// 1st Betreuung=ABGEWIESEN, 2nd Betreuung=WARTEN
		final Betreuung betreuung = Objects.requireNonNull(gesuch.getKindContainers().iterator().next().getBetreuungen()).iterator().next();
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung("grund");
		gesuchService.updateBetreuungenStatus(gesuch);
		Assert.assertEquals(GesuchBetreuungenStatus.ABGEWIESEN, gesuch.getGesuchBetreuungenStatus());
	}

	@Test
	public void testChangeFinSitStatusAbgelehnt() {
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, null, AntragStatus.VERFUEGT);
		Assert.assertTrue(gesuch.isHasFSDokument());
		Assert.assertNull(gesuch.getFinSitStatus());

		gesuchService.changeFinSitStatus(gesuch.getId(), FinSitStatus.ABGELEHNT);
		final Optional<Gesuch> updatedGesuch = gesuchService.findGesuch(gesuch.getId());
		Assert.assertTrue(updatedGesuch.isPresent());
		Assert.assertFalse(updatedGesuch.get().isHasFSDokument());
		Assert.assertEquals(FinSitStatus.ABGELEHNT, updatedGesuch.get().getFinSitStatus());
	}

	@Test
	public void testRemoveGesuchResetAnmeldungen() {
		Gesuch erstgesuch = createSimpleVerfuegtesGesuch();

		//add Anmeldungen
		Betreuung betreuung = TestDataUtil.createAnmeldungTagesschule(erstgesuch.getKindContainers().iterator().next());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getMandant());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getTraegerschaft());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution());
		persistence.persist(betreuung.getInstitutionStammdaten());
		betreuungService.saveBetreuung(betreuung, false);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		Assert.assertTrue(gesuchOptional.isPresent());
		final Gesuch mutation = gesuchService.createGesuch(gesuchOptional.get());

		final List<Betreuung> allBetreuungenFromErstgesuch = betreuungService.findAllBetreuungenFromGesuch(erstgesuch.getId());
		allBetreuungenFromErstgesuch.stream().filter(Betreuung::isAngebotSchulamt)
			.forEach(bet -> Assert.assertEquals(AnmeldungMutationZustand.MUTIERT, bet.getAnmeldungMutationZustand()));
		mutation.extractAllBetreuungen().stream().filter(Betreuung::isAngebotSchulamt)
			.forEach(bet -> Assert.assertEquals(AnmeldungMutationZustand.AKTUELLE_ANMELDUNG, bet.getAnmeldungMutationZustand()));

		gesuchService.removeGesuch(mutation.getId(), GesuchDeletionCause.UNBEKANNT);

		final Optional<Gesuch> removedGesuchOpt = gesuchService.findGesuch(mutation.getId());
		Assert.assertFalse(removedGesuchOpt.isPresent());

		final List<Betreuung> allAktuelleBetreuungenFromErstgesuch = betreuungService.findAllBetreuungenFromGesuch(erstgesuch.getId());
		allAktuelleBetreuungenFromErstgesuch.stream().filter(Betreuung::isAngebotSchulamt)
			.forEach(bet -> Assert.assertEquals(AnmeldungMutationZustand.AKTUELLE_ANMELDUNG, bet.getAnmeldungMutationZustand()));

	}

	/**
	 * tests a Papiergesuch that has been geprueft within the given period
	 * The Gesuch should be returned by the function
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiode() {
		Gesuch gesuchFeutz = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence,
			LocalDate.of(1980, Month.MARCH, 25));
		final Gesuch verfuegtesGesuch = TestDataUtil.gesuchVerfuegen(gesuchFeutz, gesuchService);

		final List<Gesuch> gesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			Constants.START_OF_TIME,
			Constants.END_OF_TIME,
			verfuegtesGesuch.getGesuchsperiode().getId()
		);

		Assert.assertEquals(1, gesuche.size());
		Assert.assertEquals(verfuegtesGesuch, gesuche.get(0));
	}

	/**
	 * tests a Papiergesuch that hasn't been geprueft yet
	 * The Gesuch shouldn't be returned by the function
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeNotGeprueft() {
		Gesuch gesuchFeutz = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence,
			LocalDate.of(1980, Month.MARCH, 25));

		final List<Gesuch> gesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			Constants.START_OF_TIME,
			Constants.END_OF_TIME,
			gesuchFeutz.getGesuchsperiode().getId()
		);

		Assert.assertTrue(gesuche.isEmpty());
	}

	/**
	 * tests a Papiergesuch that has been geprueft within the given period but belongs to a different Gesuchsperiode.
	 * The Gesuch shouldn't be returned by the function
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeOtherGesuchsperiode() {
		Gesuch gesuchFeutz = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence,
			LocalDate.of(1980, Month.MARCH, 25));
		TestDataUtil.gesuchVerfuegen(gesuchFeutz, gesuchService);

		final Gesuchsperiode otherGesuchsperiode = TestDataUtil.createGesuchsperiode1617();
		persistence.persist(otherGesuchsperiode);

		final List<Gesuch> gesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			Constants.START_OF_TIME,
			Constants.END_OF_TIME,
			otherGesuchsperiode.getId()
		);

		Assert.assertTrue(gesuche.isEmpty());
	}

	/**
	 * tests an Onlinegesuch that hasn't been freigegeben yet
	 * The Gesuch shouldn't be returned by the function
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeNotFreigegeben() {
		Gesuch gesuchFeutz = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, null);

		// the status is not really important in this test because we only check the transition to FREIGEGEBEN which doesn't exist
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_GS, gesuchFeutz.getStatus());
		Assert.assertEquals(Eingangsart.PAPIER, gesuchFeutz.getEingangsart());
		gesuchFeutz.setEingangsart(Eingangsart.ONLINE);
		final Gesuch mergedGesuch = persistence.merge(gesuchFeutz);

		final List<Gesuch> gesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			Constants.START_OF_TIME,
			Constants.END_OF_TIME,
			mergedGesuch.getGesuchsperiode().getId()
		);

		Assert.assertTrue(gesuche.isEmpty());
	}

	/**
	 * tests an Onlinegesuch that has already been freigegeben
	 * The Gesuch should be returned by the function
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeFreigegeben() {
		Gesuch onlineGesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, null);
		onlineGesuch.setEingangsart(Eingangsart.ONLINE);
		final Gesuch mergedOnlineGesuch = persistence.merge(onlineGesuch);
		final WizardStep wizardStepObject = TestDataUtil
			.createWizardStepObject(mergedOnlineGesuch, WizardStepName.FREIGABE, WizardStepStatus.UNBESUCHT);
		persistence.persist(wizardStepObject);

		final Gesuch freigegebenesGesuch = gesuchService.antragFreigeben(mergedOnlineGesuch.getId(), null, null);

		final List<Gesuch> gesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			Constants.START_OF_TIME,
			Constants.END_OF_TIME,
			freigegebenesGesuch.getGesuchsperiode().getId()
		);

		Assert.assertEquals(1, gesuche.size());
		Assert.assertEquals(freigegebenesGesuch, gesuche.get(0));
	}

	/**
	 * tests an Onlinegesuch that has already been freigegeben but outside the given dates
	 * The Gesuch shouldn't be returned by the function
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeFreigegebenOutOfDates() {
		Gesuch onlineGesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, null);
		onlineGesuch.setEingangsart(Eingangsart.ONLINE);
		final Gesuch mergedOnlineGesuch = persistence.merge(onlineGesuch);
		final WizardStep wizardStepObject = TestDataUtil
			.createWizardStepObject(mergedOnlineGesuch, WizardStepName.FREIGABE, WizardStepStatus.UNBESUCHT);
		persistence.persist(wizardStepObject);

		final Gesuch freigegebenesGesuch = gesuchService.antragFreigeben(mergedOnlineGesuch.getId(), null, null);

		final List<Gesuch> gesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			Constants.END_OF_TIME.minusMonths(2),
			Constants.END_OF_TIME,
			freigegebenesGesuch.getGesuchsperiode().getId()
		);

		Assert.assertTrue(gesuche.isEmpty());
	}

	@Test
	public void testHasFolgegesuchWithoutFolgegesuch() {
		Gesuch gesuch = TestDataUtil.createAndPersistASIV12(institutionService, persistence,
						LocalDate.of(1980, Month.MARCH, 25), AntragStatus.GEPRUEFT);

		Assert.assertFalse(gesuchService.hasFolgegesuchForAmt(gesuch.getId()));
	}

	@Test
	public void testHasFolgegesuchWithFolgegesuch() {
		Gesuch gesuch = TestDataUtil.createAndPersistASIV12(institutionService, persistence,
						LocalDate.of(1980, Month.MARCH, 25), AntragStatus.GEPRUEFT);

		final Gesuchsperiode gesuchsperiode1819 = TestDataUtil.createCustomGesuchsperiode(2018, 2019);
		final Gesuchsperiode savedGesuchsperiode1819 = persistence.persist(gesuchsperiode1819);

		final Optional<Gesuch> erneurtesGesuch = gesuchService.antragErneuern(gesuch.getId(), savedGesuchsperiode1819.getId(), null);
		Assert.assertTrue(erneurtesGesuch.isPresent());
		Gesuch folgegesuch = gesuchService.createGesuch(erneurtesGesuch.get());

		Assert.assertTrue(gesuchService.hasFolgegesuchForAmt(gesuch.getId()));
		Assert.assertFalse(gesuchService.hasFolgegesuchForAmt(folgegesuch.getId()));
	}


	// HELP METHODS

	private void insertApplicationProperties() {
		applicationPropertyService.saveOrUpdateApplicationProperty(ApplicationPropertyKey.ANZAHL_TAGE_BIS_WARNUNG_FREIGABE, "" + ANZAHL_TAGE_BIS_WARNUNG_FREIGABE);
		applicationPropertyService.saveOrUpdateApplicationProperty(ApplicationPropertyKey.ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG, "" + ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG);
		applicationPropertyService.saveOrUpdateApplicationProperty(ApplicationPropertyKey.ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE, "" + ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE);
		applicationPropertyService.saveOrUpdateApplicationProperty(ApplicationPropertyKey.ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG, "" + ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG);
	}

	private Gesuch createGesuchInBearbeitungGS(LocalDateTime timestampErstellt) {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence, AntragStatus.IN_BEARBEITUNG_GS);
		gesuch.setTimestampErstellt(timestampErstellt);
		gesuch.setEingangsart(Eingangsart.ONLINE);
		gesuch.getFall().setBesitzer(TestDataUtil.createAndPersistTraegerschaftBenutzer(persistence));
		persistence.merge(gesuch.getFall());
		gesuch.setGesuchsteller1(TestDataUtil.createDefaultGesuchstellerContainer(gesuch));
		Assert.assertNotNull(gesuch.getGesuchsteller1());
		gesuch.getGesuchsteller1().getGesuchstellerJA().setMail("fanny.huber@example.com");
		return persistence.merge(gesuch);
	}

	private Gesuch createGesuchFreigabequittung(LocalDate datumFreigabe) {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence, AntragStatus.FREIGABEQUITTUNG);
		gesuch.setFreigabeDatum(datumFreigabe);
		gesuch.setEingangsart(Eingangsart.ONLINE);
		gesuch.getFall().setBesitzer(TestDataUtil.createAndPersistTraegerschaftBenutzer(persistence));
		persistence.merge(gesuch.getFall());
		gesuch.setGesuchsteller1(TestDataUtil.createDefaultGesuchstellerContainer(gesuch));
		Assert.assertNotNull(gesuch.getGesuchsteller1());
		gesuch.getGesuchsteller1().getGesuchstellerJA().setMail("fanny.huber@example.com");
		return persistence.merge(gesuch);
	}

	/**
	 * Schreibt alle Ids von AbstractEntities (rekursiv vom Gesuch) ins Set.
	 */
	private void findAllIdsOfAbstractEntities(AbstractEntity entity, Map<String, AbstractEntity> entities, int depth) throws IllegalAccessException,
		NoSuchMethodException, InvocationTargetException {
		String id = BeanUtils.getProperty(entity, "id");
		if (!entities.containsKey(id)) {
			entities.put(id, entity);
			StringBuilder indent = new StringBuilder();
			for (int i = 0; i < depth; i++) {
				indent.append(' ');
			}
			LOG.info(indent + " - " + entity.getClass().getSimpleName());
			PropertyUtilsBean bean = new PropertyUtilsBean();
			PropertyDescriptor[] propertyDescriptors = bean.getPropertyDescriptors(entity.getClass());
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				Object property = bean.getProperty(entity, propertyDescriptor.getName());
				if (property instanceof AbstractEntity) {
					if (!isCorrectlyIgnored((AbstractEntity) property)) {
						findAllIdsOfAbstractEntities((AbstractEntity) property, entities, depth + 1);
					}
				} else if (property instanceof Collection) {
					Collection properties = (Collection) property;
					for (Object o : properties) {
						if (o instanceof AbstractEntity) {
							findAllIdsOfAbstractEntities((AbstractEntity) o, entities, depth+1);
						}
					}
				}
			}
		}
	}

	private boolean isCorrectlyIgnored(AbstractEntity property) {
		// Diese Entitaeten wurden korrekterweise nur umgehaengt und nicht kopiert.
		if (property instanceof Fall || property instanceof Mandant || property instanceof Gesuchsperiode
			|| property instanceof Institution || property instanceof InstitutionStammdaten || property instanceof Benutzer
			|| property instanceof Traegerschaft) {
			return true;
		}
		return false;
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
		Gesuch gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25), status);
		gesuch.setStatus(status);
		gesuch.setEingangsart(Eingangsart.PAPIER);
		wizardStepService.createWizardStepList(gesuch);
		gesuch.getKindContainers().stream()
			.flatMap(kindContainer -> {
				assert kindContainer.getBetreuungen() != null;
				return kindContainer.getBetreuungen().stream();
			})
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

			foundGesuche = Subject.doAs(loginContext.getSubject(), (PrivilegedAction<Collection<Gesuch>>) () -> {
				Collection<Gesuch> gesuche = gesuchService.getAllGesuche();
				return gesuche;
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

	@Nonnull
	private Gesuch createSimpleVerfuegtesGesuch() {
		Gesuch gesuchVerfuegt = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25), AntragStatus.VERFUEGT);
		gesuchVerfuegt.setGueltig(true);
		gesuchVerfuegt.setTimestampVerfuegt(LocalDateTime.now());
		gesuchVerfuegt = gesuchService.updateGesuch(gesuchVerfuegt, true, null);
		return gesuchVerfuegt;
	}
}
