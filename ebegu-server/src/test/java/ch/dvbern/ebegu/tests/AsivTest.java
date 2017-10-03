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
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.services.TraegerschaftService;
import ch.dvbern.ebegu.services.VerfuegungService;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_01;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_02;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_03;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_04;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_05;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_06;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_07;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_08;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_09;
import ch.dvbern.ebegu.testfaelle.Testfall_ASIV_10;
import ch.dvbern.ebegu.tets.TestDataUtil;
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
 * Tests fuer die ASIV Revision
 * 01: Wechsel von 1 auf 2. Keine EKV
 * 02: Wechsel von 2 auf 1. Keine EKV
 * 03: EKV, 1 Gesuchsteller
 * 04: EKV, 2 Gesuchsteller
 * 05: Wechsel von 1 auf 2. Mit vorheriger EKV, stattgegeben auch nach Heirat
 * 06: Wechsel von 1 auf 2. Mit vorheriger EKV, nach Heirat nicht mehr stattgegeben
 * 07: Wechsel von 2 auf 1. Mit vorheriger EKV, stattgegeben auch nach Trennung
 * 08: Wechsel von 2 auf 1. Mit vorheriger EKV, nach Trennung nicht mehr stattgegeben
 * 09: Wechsel von 1 auf 2. Mit nachheriger EKV, stattgegeben
 * 10: Wechsel von 2 auf 1. Mit nachheriger EKV, nach der Trennung (GS2 nicht mehr relevant)
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class AsivTest extends AbstractEbeguLoginTest {

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private Persistence persistence;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private TestfaelleService testfaelleService;

	private Gesuchsperiode gesuchsperiode;
	private List<InstitutionStammdaten> institutionStammdatenList;


	@Before
	public void init() {
		gesuchsperiode = createGesuchsperiode(true);
		final Mandant mandant = insertInstitutionen();
		createBenutzer(mandant);
		TestDataUtil.prepareParameters(gesuchsperiode.getGueltigkeit(), persistence);
	}

	@Test
	public void testfall_ASIV_01() {
		// Erstgesuch erstellen
		Testfall_ASIV_01 testfall = new Testfall_ASIV_01(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_01(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_02() {
		// Erstgesuch erstellen
		Testfall_ASIV_02 testfall = new Testfall_ASIV_02(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_02(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_03() {
		// Erstgesuch erstellen
		Testfall_ASIV_03 testfall = new Testfall_ASIV_03(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_03(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_04() {
		// Erstgesuch erstellen
		Testfall_ASIV_04 testfall = new Testfall_ASIV_04(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_04(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_05() {
		// Erstgesuch erstellen
		Testfall_ASIV_05 testfall = new Testfall_ASIV_05(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_05(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_06() {
		// Erstgesuch erstellen
		Testfall_ASIV_06 testfall = new Testfall_ASIV_06(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_06(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_07() {
		// Erstgesuch erstellen
		Testfall_ASIV_07 testfall = new Testfall_ASIV_07(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_07(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_08() {
		// Erstgesuch erstellen
		Testfall_ASIV_08 testfall = new Testfall_ASIV_08(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_08(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_09() {
		// Erstgesuch erstellen
		Testfall_ASIV_09 testfall = new Testfall_ASIV_09(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_09(mutationCalculated);
		}
	}

	@Test
	public void testfall_ASIV_10() {
		// Erstgesuch erstellen
		Testfall_ASIV_10 testfall = new Testfall_ASIV_10(gesuchsperiode, institutionStammdatenList, true);
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, true, null);
		TestDataUtil.calculateFinanzDaten(gesuch);
		Gesuch erstgesuch = verfuegungService.calculateVerfuegung(gesuch);

		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(1980, Month.MARCH, 25));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = testfall.createMutation(gesuchOptional.get());
			TestDataUtil.calculateFinanzDaten(mutation);
			mutation.setGesuchsperiode(gesuchsperiode);
			Gesuch mutationCalculated = verfuegungService.calculateVerfuegung(mutation);
			AbstractBGRechnerTest.checkTestfall_ASIV_10(mutationCalculated);
		}
	}

	/**
	 * Helper für init. Speichert Gesuchsperiode in DB
	 */
	protected Gesuchsperiode createGesuchsperiode(boolean active) {
		gesuchsperiode = TestDataUtil.createCustomGesuchsperiode(2016, 2017);
		gesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		gesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}

	/**
	 * Helper für init. Speichert Traegerschaften, Mandant und Institution in DB
	 */
	protected Mandant insertInstitutionen() {
		final InstitutionStammdaten institutionStammdatenKitaBruennen = TestDataUtil.createInstitutionStammdatenKitaBruennen();
		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		traegerschaftService.saveTraegerschaft(traegerschaft);
		institutionStammdatenKitaBruennen.getInstitution().setTraegerschaft(traegerschaft);

		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		institutionStammdatenKitaBruennen.getInstitution().setMandant(mandant);

		institutionService.createInstitution(institutionStammdatenKitaBruennen.getInstitution());
		InstitutionStammdaten institutionStammdaten = institutionStammdatenService.saveInstitutionStammdaten(institutionStammdatenKitaBruennen);

		Assert.assertNotNull(institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_BRUENNEN_KITA));

		institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(institutionStammdaten);
		return mandant;
	}
}

