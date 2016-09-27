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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Arquillian Tests fuer die Klasse AntragStatusHistoryService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class AntragStatusHistoryServiceTest extends AbstractEbeguTest {


	@Inject
	private AntragStatusHistoryService statusHistoryService;
	@Inject
	private Persistence<Gesuch> persistence;

	private Gesuch gesuch;
	private Benutzer benutzer;


	@Before
	public void setUp() {
		gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		benutzer = TestDataUtil.createAndPersistBenutzer(persistence);
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
		Assert.assertTrue(time.isBefore(createdStatusHistory.getDatum()) || time.isEqual(createdStatusHistory.getDatum()));
	}

	@Test
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
