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
		empfaengerINST = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "insti", traegerschaft, null, mandant);
		persistence.persist(empfaengerINST);

		sender = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, "gsst", null, null, mandant);
		persistence.persist(sender);
	}

}
