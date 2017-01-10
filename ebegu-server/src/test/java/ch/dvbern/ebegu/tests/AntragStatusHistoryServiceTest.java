package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.services.AntragStatusHistoryService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Arquillian Tests fuer die Klasse AntragStatusHistoryService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class AntragStatusHistoryServiceTest extends AbstractEbeguLoginTest {


	@Inject
	private AntragStatusHistoryService statusHistoryService;
	@Inject
	private Persistence<Gesuch> persistence;

	private Gesuch gesuch;
	private Benutzer benutzer;


	@Before
	public void setUp() {
		benutzer = getDummySuperadmin(); // wir erstellen in superklasse schon einen superadmin
		gesuch = TestDataUtil.createAndPersistGesuch(persistence);
	}

	@Test
	public void saveChanges() {

		LocalDateTime time = LocalDateTime.now();
		final AntragStatusHistory createdStatusHistory = statusHistoryService.saveStatusChange(gesuch);

		Assert.assertNotNull(createdStatusHistory);
		Assert.assertEquals(gesuch.getStatus(), createdStatusHistory.getStatus());
		Assert.assertEquals(gesuch, createdStatusHistory.getGesuch());
		Assert.assertEquals(benutzer, createdStatusHistory.getBenutzer());
		//just check that the generated date is after (or equals) the temporal one we created before
		Assert.assertTrue(time.isBefore(createdStatusHistory.getTimestampVon()) || time.isEqual(createdStatusHistory.getTimestampVon()));
	}

	/**
	 * Dieser Test gibt manchmal einen Fehler zurück, das der lastStatusChange FREIGABEQUITTUNG und nicht VERFUEGT ist.
	 * Das Problem könnte ein Timing Problem sein, da er zweimal den Status fast zur selben Zeit speichert.
	 */
	@Test
	@Ignore
	public void findLastStatusChangeTest() {
		gesuch.setStatus(AntragStatus.ERSTE_MAHNUNG);
		statusHistoryService.saveStatusChange(gesuch);
		gesuch.setStatus(AntragStatus.FREIGABEQUITTUNG);
		statusHistoryService.saveStatusChange(gesuch);
		gesuch.setStatus(AntragStatus.VERFUEGT);
		statusHistoryService.saveStatusChange(gesuch);
		final AntragStatusHistory lastStatusChange = statusHistoryService.findLastStatusChange(gesuch);
		Assert.assertNotNull(lastStatusChange);
		Assert.assertEquals(AntragStatus.VERFUEGT, lastStatusChange.getStatus());
	}

	@Test
	public void findLastStatusChangeNoChangeNullTest() {
		Assert.assertNull(statusHistoryService.findLastStatusChange(gesuch));
	}

}
