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

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.MahnungService;
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
 * Arquillian Tests fuer die Klasse MahnungService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class MahnungServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private MahnungService mahnungService;

	@Inject
	private Persistence persistence;


	@Test
	public void createErsteMahnung() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		Mahnung mahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch));
		Assert.assertNotNull(mahnung);
		Assert.assertEquals(MahnungTyp.ERSTE_MAHNUNG, mahnung.getMahnungTyp());
	}

	@Test
	public void createZweiteMahnung() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		Mahnung ersteMahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch));
		Assert.assertNotNull(ersteMahnung);
		Mahnung zweiteMahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch));
		Assert.assertNotNull(zweiteMahnung);
	}

	@Test (expected = EbeguRuntimeException.class)
	public void createZweiteMahnungOhneErsteMahnung() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch));
	}

	@Test
	public void findMahnung() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		Mahnung mahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch));
		Assert.assertNotNull(mahnung);

		Optional<Mahnung> readMahnungOptional = mahnungService.findMahnung(mahnung.getId());
		Assert.assertTrue(readMahnungOptional.isPresent());
		Assert.assertEquals(mahnung, readMahnungOptional.get());
	}

	@Test
	public void findMahnungenForGesuch() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		Mahnung ersteMahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch));
		Assert.assertNotNull(ersteMahnung);

		Collection<Mahnung> mahnungenForGesuch = mahnungService.findMahnungenForGesuch(gesuch);
		Assert.assertNotNull(mahnungenForGesuch);
		Assert.assertFalse(mahnungenForGesuch.isEmpty());
		Assert.assertEquals(1, mahnungenForGesuch.size());

		Mahnung zweiteMahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch));
		Assert.assertNotNull(zweiteMahnung);

		mahnungenForGesuch = mahnungService.findMahnungenForGesuch(gesuch);
		Assert.assertNotNull(mahnungenForGesuch);
		Assert.assertFalse(mahnungenForGesuch.isEmpty());
		Assert.assertEquals(2, mahnungenForGesuch.size());
	}

	@Test
	public void mahnlaufBeenden() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch));
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch));

		// Alle Mahnungen sind aktiv
		Collection<Mahnung> mahnungenForGesuch = mahnungService.findMahnungenForGesuch(gesuch);
		Assert.assertNotNull(mahnungenForGesuch);
		for (Mahnung mahnung : mahnungenForGesuch) {
			Assert.assertNull(mahnung.getTimestampAbgeschlossen());
		}

		mahnungService.mahnlaufBeenden(gesuch);

		// Alle Mahnungen sind geschlossen
		mahnungenForGesuch = mahnungService.findMahnungenForGesuch(gesuch);
		Assert.assertNotNull(mahnungenForGesuch);
		for (Mahnung mahnung : mahnungenForGesuch) {
			Assert.assertNotNull(mahnung.getTimestampAbgeschlossen());
		}
	}

	@Test
	public void fristAblaufTimer() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuchMitMahnung = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchMitMahnung.setStatus(AntragStatus.ERSTE_MAHNUNG);
		persistence.merge(gesuchMitMahnung);
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuchMitMahnung, LocalDate.now().plusWeeks(1), 3));

		Gesuch gesuchMitAbgelaufenerMahnung = createGesuchWithAbgelaufenerMahnung();

		mahnungService.fristAblaufTimer();

		Assert.assertEquals(AntragStatus.ERSTE_MAHNUNG, persistence.find(Gesuch.class, gesuchMitMahnung.getId()).getStatus());
		Assert.assertEquals(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN, persistence.find(Gesuch.class, gesuchMitAbgelaufenerMahnung.getId()).getStatus());
	}

	@Test
	public void fristAblaufTimerZweiteMahnungInFuture() {
		TestDataUtil.createAndPersistBenutzer(persistence);
		Gesuch gesuch = createGesuchWithAbgelaufenerMahnung();

		mahnungService.fristAblaufTimer();
		gesuch = persistence.find(Gesuch.class, gesuch.getId()); // needed because the method fristAblaufTimer has persisted it

		Assert.assertEquals(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN, persistence.find(Gesuch.class, gesuch.getId()).getStatus());

		gesuch.setStatus(AntragStatus.ZWEITE_MAHNUNG);
		gesuch = persistence.merge(gesuch);
		Mahnung secondMahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch,
			LocalDate.now().plusWeeks(1), 3));

		mahnungService.fristAblaufTimer();
		gesuch = persistence.find(Gesuch.class, gesuch.getId()); // needed because the method fristAblaufTimer has persisted it
		secondMahnung = persistence.find(Mahnung.class, secondMahnung.getId());

		Assert.assertEquals(AntragStatus.ZWEITE_MAHNUNG, persistence.find(Gesuch.class, gesuch.getId()).getStatus());
		Assert.assertFalse(secondMahnung.getAbgelaufen());
	}

	@Test
	public void fristAblaufTimerZweiteMahnungInPast() {
		TestDataUtil.createAndPersistBenutzer(persistence);
		Gesuch gesuch = createGesuchWithAbgelaufenerMahnung();

		mahnungService.fristAblaufTimer();
		gesuch = persistence.find(Gesuch.class, gesuch.getId()); // needed because the method fristAblaufTimer has persisted it

		Assert.assertEquals(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN, persistence.find(Gesuch.class, gesuch.getId()).getStatus());

		gesuch.setStatus(AntragStatus.ZWEITE_MAHNUNG);
		gesuch = persistence.merge(gesuch);
		Mahnung secondMahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch,
			LocalDate.now().minusDays(1), 3));

		mahnungService.fristAblaufTimer();
		gesuch = persistence.find(Gesuch.class, gesuch.getId()); // needed because the method fristAblaufTimer has persisted it
		secondMahnung = persistence.find(Mahnung.class, secondMahnung.getId());

		Assert.assertEquals(AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN, persistence.find(Gesuch.class, gesuch.getId()).getStatus());
		Assert.assertTrue(secondMahnung.getAbgelaufen());
	}

	@Nonnull
	private Gesuch createGesuchWithAbgelaufenerMahnung() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuch.setStatus(AntragStatus.ERSTE_MAHNUNG);
		gesuch = persistence.merge(gesuch);
		final Mahnung mahnung = mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch,
			LocalDate.now().minusDays(1), 3));
		return mahnung.getGesuch();
	}
}

