package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.services.DailyBatch;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests fuer die Klasse DokumentGrundService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class DailyBatchSchedulerTest extends AbstractEbeguLoginTest {

	@Inject
	private DailyBatch dailyBatch;


	@Test
	public void testInjection() {
		Assert.assertNotNull(dailyBatch);

	}

}
