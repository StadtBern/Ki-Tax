package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.BetreuungsmitteilungPensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.MitteilungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse MitteilungService
 */
@SuppressWarnings("ALL")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class MitteilungServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private MitteilungService mitteilungService;

	@Inject
	private InstitutionService instService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Persistence<Gesuch> persistence;

	private Mandant mandant;
	private Fall fall;
	private Benutzer empfaengerJA;
	private Benutzer empfaengerINST;
	private Benutzer sender;


	@Test
	public void testCreateMitteilung() {
		prepareDependentObjects();
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());

		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(mitteilung.getMessage(), foundMitteilung.get().getMessage());
	}

	@Test
	public void testSetMitteilungGelesen() {
		prepareDependentObjects();
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());
		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, foundMitteilung.get().getMitteilungStatus());

		Mitteilung mitteilungNewStatus = mitteilungService.setMitteilungGelesen(foundMitteilung.get().getId());
		Assert.assertNotNull(mitteilungNewStatus);
		Assert.assertEquals(MitteilungStatus.GELESEN, mitteilungNewStatus.getMitteilungStatus());
	}

	@Test
	public void testSetMitteilungErledigt() {
		prepareDependentObjects();
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());
		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, foundMitteilung.get().getMitteilungStatus());
		mitteilungService.setMitteilungGelesen(foundMitteilung.get().getId());

		Mitteilung mitteilungNewStatus = mitteilungService.setMitteilungErledigt(foundMitteilung.get().getId());
		Assert.assertNotNull(mitteilungNewStatus);
		Assert.assertEquals(MitteilungStatus.ERLEDIGT, mitteilungNewStatus.getMitteilungStatus());
	}

	@Test
	public void testGetMitteilungenForCurrentRole() throws LoginException {
		prepareDependentObjects();
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, empfaengerINST, MitteilungTeilnehmerTyp.INSTITUTION,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung2);

		final Collection<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.getMitteilungenForCurrentRolle(mitteilung1.getFall());

		//AS SUPERADMIN
		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(1, mitteilungenForCurrentRolle.size());
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, mitteilungenForCurrentRolle.iterator().next().getEmpfaengerTyp());
	}

	@Test
	public void testGetNewMitteilungenForCurrentRolle() throws LoginException {
		prepareDependentObjects();
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung1.setMessage("Neue Mitteilung");
		persistence.persist(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung2.setMitteilungStatus(MitteilungStatus.GELESEN);
		persistence.persist(mitteilung2);

		final Collection<Mitteilung> newMitteilungenCurrentRolle = mitteilungService.getNewMitteilungenForCurrentRolleAndFall(mitteilung1.getFall());

		//AS SUPERADMIN
		Assert.assertNotNull(newMitteilungenCurrentRolle);
		Assert.assertEquals(1, newMitteilungenCurrentRolle.size());
		final Mitteilung foundMitteilung = newMitteilungenCurrentRolle.iterator().next();
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, foundMitteilung.getEmpfaengerTyp());
		Assert.assertEquals("Neue Mitteilung", foundMitteilung.getMessage());
	}

	@Test
	public void testGetMitteilungenForPosteingang() throws LoginException {
		prepareDependentObjects();
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, null, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);

		mitteilungService.sendMitteilung(mitteilung2);

		final Collection<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.getMitteilungenForPosteingang();

		//AS SUPERADMIN
		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(2, mitteilungenForCurrentRolle.size()); // Wir sehen grundsätzliche alle Nachrichten, die ans JA gehen
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, mitteilungenForCurrentRolle.iterator().next().getEmpfaengerTyp());
	}

	@Test
	public void testSetAllNewMitteilungenOfFallGelesen() {
		prepareDependentObjects();

		Mitteilung entwurf = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedEntwurf = mitteilungService.saveEntwurf(entwurf);

		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1.setMitteilungStatus(MitteilungStatus.NEU);
		final Mitteilung mitFromGSToJA = persistence.persist(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, sender, MitteilungTeilnehmerTyp.GESUCHSTELLER,
			empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT);
		mitteilung2.setMitteilungStatus(MitteilungStatus.NEU);
		final Mitteilung mitFromJAToGS = persistence.persist(mitteilung2);

		Assert.assertEquals(MitteilungStatus.ENTWURF, persistedEntwurf.getMitteilungStatus());
		Assert.assertEquals(MitteilungStatus.NEU, mitFromGSToJA.getMitteilungStatus());
		Assert.assertEquals(MitteilungStatus.NEU, mitFromJAToGS.getMitteilungStatus());

		// Set Gelesen as JA
		mitteilungService.setAllNewMitteilungenOfFallGelesen(fall);

		final Optional<Mitteilung> entwurfUpdated1 = mitteilungService.findMitteilung(persistedEntwurf.getId());
		Assert.assertTrue(entwurfUpdated1.isPresent());
		Assert.assertEquals(MitteilungStatus.ENTWURF, entwurfUpdated1.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromGSToJAUpdated1 = mitteilungService.findMitteilung(mitFromGSToJA.getId());
		Assert.assertTrue(mitFromGSToJAUpdated1.isPresent());
		Assert.assertEquals(MitteilungStatus.GELESEN, mitFromGSToJAUpdated1.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromJAToGSUpdated1 = mitteilungService.findMitteilung(mitFromJAToGS.getId());
		Assert.assertTrue(mitFromJAToGSUpdated1.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, mitFromJAToGSUpdated1.get().getMitteilungStatus());

		// Set Gelesen as GS
		loginAsGesuchsteller("gesuchst");
		mitteilungService.setAllNewMitteilungenOfFallGelesen(fall);

		final Optional<Mitteilung> entwurfUpdated2 = mitteilungService.findMitteilung(persistedEntwurf.getId());
		Assert.assertTrue(entwurfUpdated2.isPresent());
		Assert.assertEquals(MitteilungStatus.ENTWURF, entwurfUpdated2.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromGSToJAUpdated2 = mitteilungService.findMitteilung(mitFromGSToJA.getId());
		Assert.assertTrue(mitFromGSToJAUpdated2.isPresent());
		Assert.assertEquals(MitteilungStatus.GELESEN, mitFromGSToJAUpdated2.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromJAToGSUpdated2 = mitteilungService.findMitteilung(mitFromJAToGS.getId());
		Assert.assertTrue(mitFromJAToGSUpdated2.isPresent());
		Assert.assertEquals(MitteilungStatus.GELESEN, mitFromJAToGSUpdated2.get().getMitteilungStatus());
	}

	@Test
	public void testApplyBetreuungsmitteilungErstgesuch() {
		// Momentan ist es nicht erlaubt, eine Betreuungsmitteilung aus einem Erstgesuch zu machen
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());

		final Betreuungsmitteilung mitteilung = new Betreuungsmitteilung();
		final Betreuung betreuung_1_1 = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		mitteilung.setBetreuung(betreuung_1_1);

		try {
			mitteilungService.applyBetreuungsmitteilung(mitteilung);
			Assert.fail("Keine Betreuungsmittielung darf aus einem Erstgesuch erstellt werden. Es sollte eine Exception werfen");
		}
		catch (EbeguRuntimeException e) {
			//nop
		}
	}

	@Test
	public void testApplyBetreuungsmitteilungMutation() {
		prepareDependentObjects();

		// Wir erstellen ein Erstgesuch und mutieren es
		final Gesuch gesuch1 = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		gesuch1.setStatus(AntragStatus.VERFUEGT);
		gesuch1.setGueltig(true);
		gesuch1.setTimestampVerfuegt(LocalDateTime.now());
		gesuchService.updateGesuch(gesuch1, true, null);
		final Optional<Gesuch> mutationOpt = gesuchService.antragMutieren(gesuch1.getId(), LocalDate.now());
		final Gesuch mutation = gesuchService.createGesuch(mutationOpt.get());
		final Betreuungsmitteilung mitteilung = TestDataUtil.createBetreuungmitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.INSTITUTION);

		final Set<BetreuungsmitteilungPensum> betPensen = new HashSet<>();
		BetreuungsmitteilungPensum betPens = new BetreuungsmitteilungPensum();
		betPens.setBetreuungsmitteilung(mitteilung);
		betPens.setPensum(33);
		final DateRange gueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
		betPens.setGueltigkeit(gueltigkeit);
		betPensen.add(betPens);
		mitteilung.setBetreuungspensen(betPensen);

		final KindContainer kind1 = mutation.getKindContainers().iterator().next();
		final Betreuung betreuung_1_1 = kind1.getBetreuungen().iterator().next();
		mitteilung.setBetreuung(betreuung_1_1);

		mitteilungService.applyBetreuungsmitteilung(mitteilung);

		final Optional<Betreuung> persistedBetreuung = betreuungService.findBetreuungWithBetreuungsPensen(betreuung_1_1.getId());

		Assert.assertTrue(persistedBetreuung.isPresent());
		Assert.assertEquals(1, persistedBetreuung.get().getBetreuungspensumContainers().size());
		final BetreuungspensumContainer nextBetPensum = persistedBetreuung.get().getBetreuungspensumContainers().iterator().next();
		Assert.assertEquals(new Integer(33), nextBetPensum.getBetreuungspensumJA().getPensum());
		Assert.assertEquals(gueltigkeit, nextBetPensum.getBetreuungspensumJA().getGueltigkeit());
	}

	@Test
	public void testFindNewestBetreuungsmitteilung_NoMitteilungExists() {
		prepareDependentObjects();
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		final Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		final Optional<Betreuungsmitteilung> optMitteilung = mitteilungService.findNewestBetreuungsmitteilung(betreuung.getId());

		Assert.assertFalse(optMitteilung.isPresent());
	}

	@Test
	public void testFindNewestBetreuungsmitteilung() {
		prepareDependentObjects();
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		final Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		loginAsSachbearbeiterInst("sainst", betreuung.getInstitutionStammdaten().getInstitution());

		//create a first mitteilung
		final Betreuungsmitteilung oldMitteilung = TestDataUtil.createBetreuungmitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.INSTITUTION);
		oldMitteilung.setBetreuung(betreuung);
		final Betreuungsmitteilung persistedFirstMitteilung = mitteilungService.sendBetreuungsmitteilung(oldMitteilung);
		final LocalDateTime oldSentDatum = persistedFirstMitteilung.getSentDatum();

		//create a second mitteilung, which will be the newest
		final Betreuungsmitteilung newMitteilung = TestDataUtil.createBetreuungmitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.INSTITUTION);
		newMitteilung.setBetreuung(betreuung);
		final Betreuungsmitteilung persistedSecondMitteilung = mitteilungService.sendBetreuungsmitteilung(newMitteilung);
		final LocalDateTime newestSentDatum = persistedSecondMitteilung.getSentDatum();

		final Optional<Betreuungsmitteilung> optMitteilung = mitteilungService.findNewestBetreuungsmitteilung(betreuung.getId());

		Assert.assertTrue(optMitteilung.isPresent());
		Assert.assertEquals(betreuung, optMitteilung.get().getBetreuung());
		Assert.assertEquals(MitteilungTeilnehmerTyp.INSTITUTION, optMitteilung.get().getSenderTyp());
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, optMitteilung.get().getEmpfaengerTyp());
		Assert.assertEquals(newestSentDatum, optMitteilung.get().getSentDatum());
		Assert.assertNotEquals(oldSentDatum, optMitteilung.get().getSentDatum());
	}


	// HELP METHODS

	private void prepareDependentObjects() {
		fall = TestDataUtil.createDefaultFall();
		persistence.persist(fall);
		mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		empfaengerJA = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.persist(empfaengerJA);

		final Traegerschaft traegerschaft = persistence.persist(TestDataUtil.createDefaultTraegerschaft());
		empfaengerINST = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "insti", traegerschaft, null, mandant);
		persistence.persist(empfaengerINST);

		sender = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, "gsst", null, null, mandant);
		persistence.persist(sender);
	}
}
