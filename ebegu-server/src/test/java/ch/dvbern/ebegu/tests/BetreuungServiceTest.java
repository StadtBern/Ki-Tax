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
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import ch.dvbern.ebegu.entities.BelegungFerieninsel;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.KindService;
import ch.dvbern.ebegu.services.MitteilungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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

	private Mandant mandant = null;
	private Benutzer empfaengerJA = null;
	private Benutzer sender = null;

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
		Assert.assertEquals(Integer.valueOf(1), updatedBetreuung.get().getBetreuungNummer());
		final Optional<KindContainer> kind = kindService.findKind(betreuung.getKind().getId());
		Assert.assertTrue(kind.isPresent());
		Assert.assertEquals(Integer.valueOf(2), kind.get().getNextNumberBetreuung());
	}

	@Test
	public void removeBetreuungTest() {
		Assert.assertNotNull(betreuungService);
		Betreuung persitedBetreuung = TestDataUtil.persistBetreuung(betreuungService, persistence);
		Optional<Betreuung> betreuungOptional = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertTrue(betreuungOptional.isPresent());
		Betreuung betreuung = betreuungOptional.get();

		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setGesuchsteller1(TestDataUtil.createDefaultGesuchstellerContainer(gesuch));
		persistence.merge(gesuch);

		final String gesuchId = betreuung.extractGesuch().getId();
		betreuungService.removeBetreuung(betreuung.getId());

		Optional<Betreuung> betreuungAfterRemove = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertFalse(betreuungAfterRemove.isPresent());
		gesuch = persistence.find(Gesuch.class, gesuchId);
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
		Assert.assertEquals(betreuungUnderTest, persistedMitteilung.getBetreuung());

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

	@Test
	public void betreuungMitBelegungFerieninsel() {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.now());
		final Betreuung betreuungUnderTest = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		BelegungFerieninsel belegungFerieninsel = TestDataUtil.createDefaultBelegungFerieninsel();
		betreuungUnderTest.setBelegungFerieninsel(belegungFerieninsel);
		Betreuung persistedBetreuung = betreuungService.saveBetreuung(betreuungUnderTest, false);

		Assert.assertNotNull(persistedBetreuung);
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel());
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel().getFerienname());
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel().getTage());
		Assert.assertFalse(persistedBetreuung.getBelegungFerieninsel().getTage().isEmpty());
		Assert.assertEquals(1, persistedBetreuung.getBelegungFerieninsel().getTage().size());

		// Einen Tag hinzufügen
		persistedBetreuung.getBelegungFerieninsel().getTage().add(TestDataUtil.createBelegungFerieninselTag(LocalDate.now().plusMonths(4)));
		persistedBetreuung = betreuungService.saveBetreuung(persistedBetreuung, false);
		Assert.assertNotNull(persistedBetreuung);
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel());
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel().getFerienname());
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel().getTage());
		Assert.assertFalse(persistedBetreuung.getBelegungFerieninsel().getTage().isEmpty());
		Assert.assertEquals(2, persistedBetreuung.getBelegungFerieninsel().getTage().size());

		// Einen wieder loeschen
		persistedBetreuung.getBelegungFerieninsel().getTage().remove(1);
		persistedBetreuung = betreuungService.saveBetreuung(persistedBetreuung, false);
		Assert.assertNotNull(persistedBetreuung);
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel());
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel().getFerienname());
		Assert.assertNotNull(persistedBetreuung.getBelegungFerieninsel().getTage());
		Assert.assertFalse(persistedBetreuung.getBelegungFerieninsel().getTage().isEmpty());
		Assert.assertEquals(1, persistedBetreuung.getBelegungFerieninsel().getTage().size());
	}

	private void prepareDependentObjects() {
		mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		empfaengerJA = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.persist(empfaengerJA);

		final Traegerschaft traegerschaft = persistence.persist(TestDataUtil.createDefaultTraegerschaft());
		Benutzer empfaengerINST = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "insti", traegerschaft, null, mandant);
		persistence.persist(empfaengerINST);

		sender = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, "gsst", null, null, mandant);
		persistence.persist(sender);
	}

	@Test
	public void getFallnummerFromBetreuungsIdTest() {
		Assert.assertEquals(108L, betreuungService.getFallnummerFromBGNummer("18.000108.1.2").longValue());
		Assert.assertEquals(123456L, betreuungService.getFallnummerFromBGNummer("18.123456.1.2").longValue());
	}

	@Test
	public void getYearFromBetreuungsIdTest() {
		Assert.assertEquals(2018, betreuungService.getYearFromBGNummer("18.000108.1.2"));
	}

	@Test
	public void getKindNummerFromBetreuungsIdTest() {
		Assert.assertEquals(1, betreuungService.getKindNummerFromBGNummer("18.000108.1.2"));
		Assert.assertEquals(2, betreuungService.getKindNummerFromBGNummer("18.000108.2.2"));
		Assert.assertEquals(88, betreuungService.getKindNummerFromBGNummer("18.000108.88.2"));
	}

	@Test
	public void getBetreuungNummerFromBetreuungsId() {
		Assert.assertEquals(2, betreuungService.getBetreuungNummerFromBGNummer("18.000108.1.2"));
		Assert.assertEquals(1, betreuungService.getBetreuungNummerFromBGNummer("18.000108.2.1"));
		Assert.assertEquals(99, betreuungService.getBetreuungNummerFromBGNummer("18.000108.88.99"));
	}

	@Test
	public void validateBGNummer() {
		Assert.assertTrue("18.000108.1.2", betreuungService.validateBGNummer("18.000108.1.2"));
		Assert.assertTrue("88.999999.77.66", betreuungService.validateBGNummer("88.999999.77.66"));
		Assert.assertTrue("88.999999.7.66", betreuungService.validateBGNummer("88.999999.7.66"));
		Assert.assertTrue("88.999999.77.6", betreuungService.validateBGNummer("88.999999.77.6"));
		Assert.assertFalse("1.000108.1.2", betreuungService.validateBGNummer("1.000108.1.2"));
		Assert.assertFalse("88.99999.77.66", betreuungService.validateBGNummer("88.99999.77.66"));
		Assert.assertFalse("88.999999.66", betreuungService.validateBGNummer("88.999999.66"));
		Assert.assertFalse("88.999999.66", betreuungService.validateBGNummer("88.999999.66"));
	}

	/**
	 * Kita-Zeitraum = Gesuchsperiode (mindestens)
	 */
	@Test
	public void validateBetreuungszeitraumInnerhalbInstitutionsGueltigkeit() {
		prepareDependentObjects();
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.now());
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		// *** Kita-Zeitraum = Gesuchsperiode (mindestens)
		LocalDate kitaFrom = betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigAb();
		LocalDate kitaUntil = betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis();
		InstitutionStammdaten institutionStammdaten = TestDataUtil.createDefaultInstitutionStammdaten();
		institutionStammdaten.getInstitution().setMandant(mandant);
		institutionStammdaten.getInstitution().setTraegerschaft(null);
		institutionStammdaten.getGueltigkeit().setGueltigAb(kitaFrom);
		institutionStammdaten.getGueltigkeit().setGueltigBis(kitaUntil);
		persistence.persist(institutionStammdaten.getInstitution());
		institutionStammdaten = persistence.persist(institutionStammdaten);
		betreuung.setInstitutionStammdaten(institutionStammdaten);

		// (1) Pensum exakt gleich wie Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom);
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil);
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (2) Pensum innerhalb Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.plusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil.minusDays(1));
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (3) Pensum ausserhalb Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.minusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil.plusDays(1));
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (4) Pensum innerhalb Kita-Zeitraum mit bis=END_OF_TIME
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.plusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (5) Pensum ausserhalb Kita-Zeitraum mit bis=END_OF_TIME
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.minusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);
	}

	/**
	 * Kita hat innerhalb GP neu geöffnet
	 */
	@Test
	public void validateBetreuungszeitraumInstitutionsGueltigkeitInGesuchsperiodeOpen() {
		prepareDependentObjects();
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.now());
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		// *** Kita hat innerhalb GP neu geöffnet
		LocalDate kitaFrom = betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigAb().plusWeeks(1);
		LocalDate kitaUntil = betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis();
		prepareInstitutionsstammdaten(betreuung, kitaFrom, kitaUntil);

		// (1) Pensum exakt gleich wie Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom);
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil);
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (2) Pensum innerhalb Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.plusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil.minusDays(1));
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (3) Pensum ausserhalb Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.minusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil.plusDays(1));
		try {
			betreuungService.betreuungPlatzBestaetigen(betreuung);
			Assert.fail("Exception expected");
		} catch (Exception e) {
			// Expected
		}

		// (4) Pensum innerhalb Kita-Zeitraum mit bis=END_OF_TIME
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.plusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (5) Pensum ausserhalb Kita-Zeitraum mit bis=END_OF_TIME
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.minusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		try {
			betreuungService.betreuungPlatzBestaetigen(betreuung);
			Assert.fail("Exception expected");
		} catch (Exception e) {
			// Expected
		}
	}

	/**
	 * Kita hat innerhalb GP geschlossen
	 */
	@Test
	public void validateBetreuungszeitraumInstitutionsGueltigkeitInGesuchsperiodeClosed() {
		prepareDependentObjects();
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.now());
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();

		// *** Kita wird innerhalb GP geschlossen
		LocalDate kitaFrom = betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigAb();
		LocalDate kitaUntil = betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis().minusWeeks(1);
		prepareInstitutionsstammdaten(betreuung, kitaFrom, kitaUntil);

		// (1) Pensum exakt gleich wie Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom);
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil);
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (2) Pensum innerhalb Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.plusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil.minusDays(1));
		betreuung = betreuungService.betreuungPlatzBestaetigen(betreuung);
		Assert.assertNotNull(betreuung);

		// (3) Pensum ausserhalb Kita-Zeitraum
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.minusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(kitaUntil.plusDays(1));
		try {
			betreuungService.betreuungPlatzBestaetigen(betreuung);
			Assert.fail("Exception expected");
		} catch (Exception e) {
			// Expected
		}

		// (4) Pensum innerhalb Kita-Zeitraum mit bis=END_OF_TIME
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.plusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		try {
			betreuungService.betreuungPlatzBestaetigen(betreuung);
			Assert.fail("Exception expected");
		} catch (Exception e) {
			// Expected
		}

		// (5) Pensum ausserhalb Kita-Zeitraum mit bis=END_OF_TIME
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigAb(kitaFrom.minusDays(1));
		betreuung.getBetreuungspensumContainers().iterator().next().getBetreuungspensumJA().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		try {
			betreuungService.betreuungPlatzBestaetigen(betreuung);
			Assert.fail("Exception expected");
		} catch (Exception e) {
			// Expected
		}
	}

	private void prepareInstitutionsstammdaten(Betreuung betreuung, LocalDate kitaFrom, LocalDate kitaUntil) {
		InstitutionStammdaten institutionStammdaten = TestDataUtil.createDefaultInstitutionStammdaten();
		institutionStammdaten.getGueltigkeit().setGueltigAb(kitaFrom);
		institutionStammdaten.getGueltigkeit().setGueltigBis(kitaUntil);
		persistence.merge(institutionStammdaten.getInstitution().getMandant());
		persistence.merge(institutionStammdaten.getInstitution().getTraegerschaft());
		persistence.merge(institutionStammdaten.getInstitution());
		institutionStammdaten = persistence.merge(institutionStammdaten);
		betreuung.setInstitutionStammdaten(institutionStammdaten);
	}
}
