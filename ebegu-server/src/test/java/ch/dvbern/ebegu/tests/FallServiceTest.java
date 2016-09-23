package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.services.FallService;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Arquillian Tests fuer die Klasse FallService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DEFAULT)
public class FallServiceTest extends AbstractEbeguTest {

	@Inject
	private FallService fallService;

	@Inject
	private Persistence<Fall> persistence;




	@Test
	public void createFall() {
		persistence.getEntityManager().createNativeQuery("ALTER TABLE fall ALTER COLUMN fallNummer RESTART WITH 1").executeUpdate();

		Assert.assertNotNull(fallService);
		Fall fall = TestDataUtil.createDefaultFall();
		fallService.saveFall(fall);

		Collection<Fall> allFalle = fallService.getAllFalle();
		Assert.assertEquals(1, allFalle.size());
		Assert.assertEquals(1, allFalle.iterator().next().getFallNummer());

		Assert.assertNotNull(fallService);
		Fall secondFall = TestDataUtil.createDefaultFall();
		fallService.saveFall(secondFall);

		//Wir erwarten das die Fallnummern 1 und 2 (bzw in PSQL 0 und 1 ) vergeben wurden
		List<Fall> moreFaelle = new ArrayList<>(fallService.getAllFalle().stream()
			.sorted((o1, o2) -> Integer.valueOf(o1.getFallNummer()).compareTo(Integer.valueOf(o2.getFallNummer())))
			.collect(Collectors.toList()));
		Assert.assertEquals(2, moreFaelle.size());
		for (int i = 0; i < moreFaelle.size(); i++) {
			int expectedFallNr = (i + 1); //H2 DB faengt anscheinend im Gegensatz zu PSQL bei 1 an wenn auto increment
			Assert.assertEquals(expectedFallNr, moreFaelle.get(i).getFallNummer());
		}
	}

	@Test
	public void removeFallTest() {
		Assert.assertNotNull(fallService);
		Fall fall = TestDataUtil.createDefaultFall();
		fallService.saveFall(fall);
		Assert.assertEquals(1, fallService.getAllFalle().size());

		fallService.removeFall(fall);
		Assert.assertEquals(0, fallService.getAllFalle().size());
	}

}
