package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.services.AdresseService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
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
import java.util.Optional;

import static javafx.scene.input.KeyCode.J;

/**
 * Tests fuer die Klasse Convertierung von Personen und ihren Adressen
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PersonAndAdrConverterTest extends AbstractEbeguTest {

	@Inject
	private AdresseService adresseService;

	@Inject
	private Persistence<Person> persistence;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void createAdresseTogetherWithPersonTest() {
		Person pers  = TestDataUtil.createDefaultPerson();
		Person storedPerson = persistence.persist(pers);
		Assert.assertNotNull(storedPerson.getAdressen());
		Assert.assertTrue(storedPerson.getAdressen().stream().findAny().isPresent());

	}

	@Test
	public void convertJaxPerson() {

	}



}
