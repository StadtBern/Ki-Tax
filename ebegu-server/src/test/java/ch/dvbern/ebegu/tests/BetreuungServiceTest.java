package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.UserRole;
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
import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse betreuungService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class BetreuungServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private MitteilungService mitteilungService;
	@Inject
	private Persistence persistence;
	@Inject
	private KindService kindService;

	@Inject
	private InstitutionService institutionService;

	private Mandant mandant;
	private Benutzer empfaengerJA;
	private Benutzer empfaengerINST;
	private Benutzer sender;

	@Test
	public void createAndUpdateBetreuungTest() {
		Assert.assertNotNull(betreuungService);
		Betreuung persitedBetreuung = TestDataUtil.persistBetreuung(betreuungService, persistence);
		Optional<Betreuung> betreuungOpt = betreuungService.findBetreuungWithBetreuungsPensen(persitedBetreuung.getId());
		Assert.assertTrue(betreuungOpt.isPresent());
		Betreuung betreuung = betreuungOpt.get();
		Assert.assertEquals(persitedBetreuung.getBetreuungsstatus(), betreuung.getBetreuungsstatus());

		Assert.assertEquals(GesuchBetreuungenStatus.WARTEN, betreuung.extractGesuch().getGesuchBetreuungenStatus());
		betreuung.setGrundAblehnung("abgewiesen");
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuungService.saveBetreuung(betreuung, false);
		Optional<Betreuung> updatedBetreuung = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertTrue(updatedBetreuung.isPresent());

		Assert.assertEquals(GesuchBetreuungenStatus.ABGEWIESEN, updatedBetreuung.get().extractGesuch()
				.getGesuchBetreuungenStatus());
		Assert.assertEquals(new Integer(1), updatedBetreuung.get().getBetreuungNummer());
		Assert.assertEquals(new Integer(2), kindService.findKind(betreuung.getKind().getId()).get().getNextNumberBetreuung());
	}

	@Test
	public void removeBetreuungTest() {
		Assert.assertNotNull(betreuungService);
		Betreuung persitedBetreuung = TestDataUtil.persistBetreuung(betreuungService, persistence);
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertTrue(betreuung.isPresent());
		final String gesuchId = betreuung.get().extractGesuch().getId();
		betreuungService.removeBetreuung(betreuung.get().getId());

		Optional<Betreuung> betreuungAfterRemove = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertFalse(betreuungAfterRemove.isPresent());
		final Gesuch gesuch = persistence.find(Gesuch.class, gesuchId);
		Assert.assertEquals(GesuchBetreuungenStatus.ALLE_BESTAETIGT, gesuch.getGesuchBetreuungenStatus());
	}

	@Test
	public void removeBetreuungWithMitteilungTest() {
		prepareDependentObjects();
		Gesuch dagmarGesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.now());
		Mitteilung mitteilung = TestDataUtil.createMitteilung(dagmarGesuch.getFall(), empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
				sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		Betreuung betreuungUnderTest = dagmarGesuch.extractAllBetreuungen().get(0);
		mitteilung.setBetreuung(betreuungUnderTest);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);
		Assert.assertEquals(betreuungUnderTest , persistedMitteilung.getBetreuung());

		Optional<Betreuung> betreuung = betreuungService.findBetreuung(betreuungUnderTest.getId());
		Assert.assertTrue(betreuung.isPresent());
		Collection<Mitteilung> mitteilungen = this.mitteilungService.findAllMitteilungenForBetreuung(betreuungUnderTest);
		Assert.assertEquals(1, mitteilungen.size());
		Assert.assertEquals(betreuungUnderTest, mitteilungen.stream().findFirst().get().getBetreuung());
		betreuungService.removeBetreuung(betreuung.get().getId());
		Optional<Betreuung> betreuungAfterRemove = betreuungService.findBetreuung(betreuungUnderTest.getId());
		Assert.assertFalse(betreuungAfterRemove.isPresent());
		Collection<Mitteilung> mitteilungenAfterRemove = this.mitteilungService.findAllMitteilungenForBetreuung(betreuungUnderTest);
		Assert.assertEquals(0, mitteilungenAfterRemove.size());

		//die Mitteilung muss noch existieren
		Optional<Mitteilung> stillExistingMitteilung = this.mitteilungService.findMitteilung(mitteilungen.stream().findFirst().get().getId());
		Assert.assertNotNull(stillExistingMitteilung.get());
		Assert.assertNull(stillExistingMitteilung.get().getBetreuung());

	}

	@Test
	public void removeBetreuungsmitteilungTest() throws LoginException {
		prepareDependentObjects();
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.now());
		final Betreuung betreuungUnderTest = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		loginAsSachbearbeiterInst("sainst", betreuungUnderTest.getInstitutionStammdaten().getInstitution());

		//create a first mitteilung
		final Betreuungsmitteilung betmitteilung = TestDataUtil.createBetreuungmitteilung(gesuch.getFall(), empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
				sender, MitteilungTeilnehmerTyp.INSTITUTION);
		betmitteilung.setBetreuung(betreuungUnderTest);
		final Betreuungsmitteilung persistedFirstMitteilung = mitteilungService.sendBetreuungsmitteilung(betmitteilung);

		loginAsSuperadmin();
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(betreuungUnderTest.getId());
		Assert.assertTrue(betreuung.isPresent());
		Collection<Mitteilung> mitteilungen = this.mitteilungService.findAllMitteilungenForBetreuung(betreuungUnderTest);
		Assert.assertEquals(1, mitteilungen.size());
		Assert.assertEquals(betreuungUnderTest, mitteilungen.stream().findFirst().get().getBetreuung());
		Assert.assertEquals(persistedFirstMitteilung, mitteilungen.stream().findFirst().get());
		betreuungService.removeBetreuung(betreuung.get().getId());
		Optional<Betreuung> betreuungAfterRemove = betreuungService.findBetreuung(betreuungUnderTest.getId());
		Assert.assertFalse(betreuungAfterRemove.isPresent());
		Collection<Mitteilung> mitteilungenAfterRemove = this.mitteilungService.findAllMitteilungenForBetreuung(betreuungUnderTest);
		Assert.assertEquals(0, mitteilungenAfterRemove.size());
		//die Betreuungsmitteilung muss geloescht sein
		Optional<Mitteilung> removedMitteilung = this.mitteilungService.findMitteilung(mitteilungen.stream().findFirst().get().getId());
		Assert.assertFalse(removedMitteilung.isPresent());

	}

	private void prepareDependentObjects() {
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
