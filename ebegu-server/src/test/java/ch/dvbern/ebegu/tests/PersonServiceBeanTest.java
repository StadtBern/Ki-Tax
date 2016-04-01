package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.services.PersonService;
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
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse PersonService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PersonServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private PersonService personService;

	@Inject
	private Persistence<Person> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createPerson() {
		Assert.assertNotNull(personService);
		Person person = TestDataUtil.createDefaultPerson();

		personService.createPerson(person);
		Collection<Person> allPersonen = personService.getAllPersonen();
		Assert.assertEquals(1, allPersonen.size());
		Person nextPerson = allPersonen.iterator().next();
		Assert.assertEquals("Tester", nextPerson.getNachname());
		Assert.assertEquals("tim.tester@example.com", nextPerson.getMail());
	}

	@Test
	public void updatePersonTest() {
		Assert.assertNotNull(personService);
		Person insertedPersons = insertNewEntity();
		Optional<Person> personOptional = personService.findPerson(insertedPersons.getId());
		Assert.assertTrue(personOptional.isPresent());
		Person person = personOptional.get();
		Assert.assertEquals("tim.tester@example.com", person.getMail());

		person.setMail("fritz.mueller@example.com");
		Person updatedPerson = personService.updatePerson(person);
		Assert.assertEquals("fritz.mueller@example.com", updatedPerson.getMail());
	}

	@Test
	public void removePersonTest() {
		Assert.assertNotNull(personService);
		Person insertedPersons = insertNewEntity();
		Assert.assertEquals(1, personService.getAllPersonen().size());

		personService.removePerson(insertedPersons);
		Assert.assertEquals(0, personService.getAllPersonen().size());
	}



	// Helper Methods

	private Person insertNewEntity() {
		Person person = TestDataUtil.createDefaultPerson();
		persistence.persist(person);
		return person;
	}



}
