package ch.dvbern.ebegu.tests;

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

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Arquillian Tests fuer die Klasse MahnungService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class MahnungServiceTest extends AbstractEbeguTest {

	@Inject
	private MahnungService mahnungService;

	@Inject
	private Persistence<Gesuch> persistence;


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
	public void dokumenteKomplettErhalten() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch));
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch));

		// Alle Mahnungen sind aktiv
		Collection<Mahnung> mahnungenForGesuch = mahnungService.findMahnungenForGesuch(gesuch);
		Assert.assertNotNull(mahnungenForGesuch);
		for (Mahnung mahnung : mahnungenForGesuch) {
			Assert.assertTrue(mahnung.isActive());
		}

		mahnungService.dokumenteKomplettErhalten(gesuch);

		// Alle Mahnungen sind geschlossen
		mahnungenForGesuch = mahnungService.findMahnungenForGesuch(gesuch);
		Assert.assertNotNull(mahnungenForGesuch);
		for (Mahnung mahnung : mahnungenForGesuch) {
			Assert.assertFalse(mahnung.isActive());
		}
	}

	@Test
	public void fristAblaufTimer() {
		Assert.assertNotNull(mahnungService);
		TestDataUtil.createAndPersistBenutzer(persistence);

		Gesuch gesuchMitMahnung = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchMitMahnung.setStatus(AntragStatus.ERSTE_MAHNUNG);
		persistence.merge(gesuchMitMahnung);
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuchMitMahnung, LocalDate.now().plusWeeks(1)));

		Gesuch gesuchMitAbgelaufenerMahnung = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchMitAbgelaufenerMahnung.setStatus(AntragStatus.ERSTE_MAHNUNG);
		persistence.merge(gesuchMitAbgelaufenerMahnung);
		mahnungService.createMahnung(TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuchMitAbgelaufenerMahnung, LocalDate.now().minusDays(1)));

		mahnungService.fristAblaufTimer();

		Assert.assertEquals(AntragStatus.ERSTE_MAHNUNG, persistence.find(Gesuch.class, gesuchMitMahnung.getId()).getStatus());
		Assert.assertEquals(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN, persistence.find(Gesuch.class, gesuchMitAbgelaufenerMahnung.getId()).getStatus());
	}
}

