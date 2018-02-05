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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ejb.EJBAccessException;
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
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
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
import org.junit.Before;
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
	private Persistence persistence;

	private Traegerschaft traegerschaft;
	private Mandant mandant;
	private Fall fall;
	private Benutzer empfaengerJA;
	private Benutzer empfaengerSCH;
	private Benutzer empfaengerINST;
	private Benutzer sender;

	@Before
	public void init() {
		mandant = getDummySuperadmin().getMandant();
		empfaengerJA = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.persist(empfaengerJA);
		empfaengerSCH = TestDataUtil.createBenutzer(UserRole.SCHULAMT, "scju", null, null, mandant);
		persistence.persist(empfaengerSCH);

		fall = TestDataUtil.createDefaultFall();
		fall.setMandant(mandant);
		fall.setVerantwortlicher(empfaengerJA);
		fall.setVerantwortlicherSCH(empfaengerSCH);
		fall = persistence.persist(fall);

		traegerschaft = persistence.persist(TestDataUtil.createDefaultTraegerschaft());
		empfaengerINST = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "insti", traegerschaft, null, mandant);
		persistence.persist(empfaengerINST);

		// Default-Verantwortliche setzen, damit beim Senden der Message automatisch der Empfaenger ermittelt werden kann
		TestDataUtil.saveParameter(ApplicationPropertyKey.DEFAULT_VERANTWORTLICHER, "saja", persistence);
		TestDataUtil.saveParameter(ApplicationPropertyKey.DEFAULT_VERANTWORTLICHER_SCH, "scju", persistence);
	}

	@Test
	public void testCreateMitteilung() {
		prepareDependentObjects("gesuchst");
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());

		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(mitteilung.getMessage(), foundMitteilung.get().getMessage());
	}

	/**
	 * This must fail with an exception because the user hasn't got the rights to send the Mitteilung of the other user
	 */
	@Test(expected = EJBAccessException.class)
	public void testSendMitteilungAsDifferentGS() {
		prepareDependentObjects("gesuchst2");
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		loginAsGesuchsteller("gesuchst"); // send as GS a different user gesuchst. (The GS used for the mitteilung is "gsst")
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);
	}

	@Test
	public void testSetMitteilungGelesen() {
		prepareDependentObjects("gesuchst");
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		// from now on as JA
		loginAsSachbearbeiterJA();

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());
		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, foundMitteilung.get().getMitteilungStatus());

		Mitteilung mitteilungNewStatus = mitteilungService.setMitteilungGelesen(foundMitteilung.get().getId());
		Assert.assertNotNull(mitteilungNewStatus);
		Assert.assertEquals(MitteilungStatus.GELESEN, mitteilungNewStatus.getMitteilungStatus());
	}

	@Test
	public void testSetMitteilungErledigt() {
		prepareDependentObjects("gesuchst");
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		// from now on as JA
		loginAsSachbearbeiterJA();

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
		prepareDependentObjects("gesuchst");
		loginAsGesuchsteller("gesuchst");
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung1);

		loginAsSachbearbeiterTraegerschaft("satraeg", traegerschaft);
		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, empfaengerINST, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.INSTITUTION);
		mitteilungService.sendMitteilung(mitteilung2);

		//AS Traegerschaft
		final Collection<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.getMitteilungenForCurrentRolle(mitteilung1.getFall());

		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(1, mitteilungenForCurrentRolle.size());
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, mitteilungenForCurrentRolle.iterator().next().getEmpfaengerTyp());
	}

	@Test
	public void testGetNewMitteilungenForCurrentRolle() throws LoginException {
		prepareDependentObjects("gesuchst");
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
		prepareDependentObjects("gesuchst");
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1 = mitteilungService.sendMitteilung(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, null, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung2 = mitteilungService.sendMitteilung(mitteilung2);

		// from now on as JA
		loginAsSachbearbeiterJA();

		List<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.searchMitteilungen(TestDataUtil.createMitteilungTableFilterDTO(), false).getRight();

		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(2, mitteilungenForCurrentRolle.size()); // Wir sehen grundsätzliche alle Nachrichten, die ans JA gehen
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, mitteilungenForCurrentRolle.iterator().next().getEmpfaengerTyp());
	}

	@Test
	public void testSetAllNewMitteilungenOfFallGelesen() {
		prepareDependentObjects("gesuchst");
		fall.setBesitzer(sender);
		fall = persistence.merge(fall);

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
		loginAsSachbearbeiterJA();
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
		} catch (EbeguRuntimeException e) {
			//nop
		}
	}

	@Test
	public void testApplyBetreuungsmitteilungMutation() {
		prepareDependentObjects("gesuchst");

		// Wir erstellen ein Erstgesuch und mutieren es
		final Gesuch gesuch1 = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now(), AntragStatus.VERFUEGT);
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
		prepareDependentObjects("gesuchst");
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.now());
		final Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		final Optional<Betreuungsmitteilung> optMitteilung = mitteilungService.findNewestBetreuungsmitteilung(betreuung.getId());

		Assert.assertFalse(optMitteilung.isPresent());
	}

	@Test
	public void testFindNewestBetreuungsmitteilung() {
		prepareDependentObjects("gesuchst");
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

	@Test
	public void testMitteilungUebergebenAnSchulamt() {
		// Als GS einloggen und eine Meldung schreiben
		prepareDependentObjects("gesuchst");
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1 = mitteilungService.sendMitteilung(mitteilung1);
		Benutzer empfaengerUrspruenglich = mitteilung1.getEmpfaenger();

		// Als JA einloggen: Die Meldung ist jetzt im Posteingang des JA
		loginAsSachbearbeiterJA();
		Mitteilung mitteilung = readFirstAndOnlyMitteilung();
		Assert.assertEquals(empfaengerUrspruenglich, mitteilung.getEmpfaenger());
		// Diese Meldung an SCH uebergeben: Es wird ein neuer Empfaenger gesetzt
		mitteilung = mitteilungService.mitteilungUebergebenAnSchulamt(mitteilung.getId());
		Assert.assertNotEquals(empfaengerUrspruenglich, mitteilung.getEmpfaenger());
	}

	@Test (expected = EJBAccessException.class)
	public void testMitteilungUebergebenAnSchulamtFalscheRolle() {
		// Als GS einloggen und eine Meldung schreiben
		prepareDependentObjects("gesuchst");
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1 = mitteilungService.sendMitteilung(mitteilung1);

		// Als SCH einloggen: Da ich schon SCH bin, kann ich nicht an SCH uebergeben
		loginAsSchulamt();
		Mitteilung mitteilung = readFirstAndOnlyMitteilung();
		mitteilungService.mitteilungUebergebenAnSchulamt(mitteilung.getId());
	}

	@Test
	public void testMitteilungUebergebenAnJugendamt() {
		// Als GS einloggen und eine Meldung schreiben
		prepareDependentObjects("gesuchst");
		// Den Fall auf NUR-SCHULAMT setzen, damit die Meldung ans Schulamt geht
		fall.setVerantwortlicher(null);
		persistence.merge(fall);
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerSCH, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1 = mitteilungService.sendMitteilung(mitteilung1);
		Benutzer empfaengerUrspruenglich = mitteilung1.getEmpfaenger();

		// Als SCH einloggen: Die Meldung ist jetzt im Posteingang des SCH
		loginAsSchulamt();
		Mitteilung mitteilung = readFirstAndOnlyMitteilung();
		Assert.assertEquals(empfaengerUrspruenglich, mitteilung.getEmpfaenger());
		// Diese Meldung an JA uebergeben: Es wird ein neuer Empfaenger gesetzt
		mitteilung = mitteilungService.mitteilungUebergebenAnJugendamt(mitteilung.getId());
		Assert.assertNotEquals(empfaengerUrspruenglich, mitteilung.getEmpfaenger());
	}

	@Test (expected = EJBAccessException.class)
	public void testMitteilungUebergebenAnJugendamtFalscheRolle() {
		// Als GS einloggen und eine Meldung schreiben
		prepareDependentObjects("gesuchst");
		loginAsGesuchsteller("gesuchst"); // send as GS to preserve the defined senderTyp empfaengerTyp
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerSCH, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1 = mitteilungService.sendMitteilung(mitteilung1);

		// Als JA einloggen: Da ich schon JA bin, kann ich nicht an JA uebergeben
		loginAsSachbearbeiterJA();
		Mitteilung mitteilung = readFirstAndOnlyMitteilung();
		mitteilungService.mitteilungUebergebenAnJugendamt(mitteilung.getId());
	}

	private Mitteilung readFirstAndOnlyMitteilung() {
		List<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.searchMitteilungen(TestDataUtil.createMitteilungTableFilterDTO(), false).getRight();
		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(1, mitteilungenForCurrentRolle.size());
		Mitteilung mitteilung = mitteilungenForCurrentRolle.iterator().next();
		return mitteilung;
	}

	// HELP METHODS

	private void prepareDependentObjects(String gesuchstellerUserName) {
		sender = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, gesuchstellerUserName, null, null, mandant);
		persistence.persist(sender);
	}
}
