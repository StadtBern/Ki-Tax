package ch.dvbern.ebegu;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.services.HistorizationService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

/**
 * Tests fuer Services von Historization. Es wird vor jedem Test die Datenbank mit dem leeren Dataset
 * initialisiert.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/applicationPropertyAudited.xml")
@Transactional(TransactionMode.DISABLED)
public class HistorizationServiceTest extends AbstractEbeguTest {

	@Inject
	private HistorizationService historizationService;

	@Inject
	private Persistence<AbstractEntity> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return AbstractEbeguTest.createTestArchive(new Class[] {
			HistorizationServiceTest.class
		});
	}


	@Test
	public void getAllEntitiesByRevisionTest() {
		Assert.assertNotNull(historizationService);
		assertValuesAtRevision(1, "code1", "entry1");
		assertValuesAtRevision(2, "code2", "entry2");
	}

	@Test
	public void getAllRevisionsByIdTest() {
		List<Object[]> revisions = historizationService
			.getAllRevisionsById(ApplicationProperty.class.getSimpleName(), "67219837-0e12-4d97-b245-649b90d9d2a5");

		Assert.assertNotNull(revisions);
		Assert.assertEquals(2, revisions.size());
		checkRevisionsValues(revisions.get(0), "code1", "entry1", 1, RevisionType.ADD);
		checkRevisionsValues(revisions.get(1), "code2", "entry2", 2, RevisionType.MOD);

	}


	// Help Methods

	private void checkRevisionsValues(Object[] revisionObject, String name, String value, int rev, RevisionType revisionType) {
		Assert.assertTrue(revisionObject[0] instanceof AbstractEntity);
		Assert.assertTrue(revisionObject[1] instanceof DefaultRevisionEntity);
		Assert.assertTrue(revisionObject[2] instanceof RevisionType);

		Assert.assertEquals(name, ((ApplicationProperty) revisionObject[0]).getName());
		Assert.assertEquals(value, ((ApplicationProperty) revisionObject[0]).getValue());
		Assert.assertEquals(rev, ((DefaultRevisionEntity) revisionObject[1]).getId());
		Assert.assertEquals(revisionType, revisionObject[2]);
	}

	private void assertValuesAtRevision(Integer revision, String name, String value) {
		List<AbstractEntity> entities = historizationService
			.getAllEntitiesByRevision(ApplicationProperty.class.getSimpleName(), revision);

		Assert.assertNotNull(entities);
		Assert.assertEquals(1, entities.size());
		Assert.assertTrue(entities.get(0) instanceof ApplicationProperty);
		Assert.assertEquals(name, ((ApplicationProperty) entities.get(0)).getName());
		Assert.assertEquals(value, ((ApplicationProperty) entities.get(0)).getValue());
	}


}
