package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxPerson;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests fuer die Klasse AdresseService
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class PersonAndAdresseConverterTest extends AbstractEbeguRestTest {


	@Inject
	private Persistence<Person> persistence;

	@Inject
	private JaxBConverter converter;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	/**
	 * transformiert eine gespeicherte person nach jax und wieder zurueck. wir erwarten das daten gelich beliben
	 */
	@Test
	public void convertPersistedTestEntityToJax(){
		Person person = insertNewEntity();
		JaxPerson jaxPerson = this.converter.personToJAX(person);
		Person transformedEntity = this.converter.personToEntity(jaxPerson, new Person());
		Assert.assertEquals(person.getNachname(), transformedEntity.getNachname());
		Assert.assertEquals(person.getVorname(), transformedEntity.getVorname());
		Assert.assertEquals(person.getGeburtsdatum(), transformedEntity.getGeburtsdatum());
		Assert.assertEquals(person.getGeschlecht(), transformedEntity.getGeschlecht());
		Assert.assertEquals(person.getMail(), transformedEntity.getMail());
		Assert.assertEquals(person.getTelefon(), transformedEntity.getTelefon());
		Assert.assertEquals(person.getTelefonAusland(), transformedEntity.getTelefonAusland());
		Assert.assertEquals(person.getAdressen().size(), transformedEntity.getAdressen().size());
		boolean allAdrAreSame  = person.getAdressen().stream().allMatch(
			adresse -> transformedEntity.getAdressen().stream().anyMatch(adresse::isSame));
		Assert.assertTrue(allAdrAreSame);

	}

	/**
	 * Testet das Umzugadresse konvertiert wird
	 */
	@Test
	public void convertJaxPersonWithUmzgTest(){
		JaxPerson personWith3Adr = TestJaxDataUtil.createTestJaxPersonWithUmzug();
		Person person = converter.personToEntity(personWith3Adr, new Person());
		Assert.assertEquals(personWith3Adr.getGeburtsdatum(), person.getGeburtsdatum());
		Assert.assertEquals(personWith3Adr.getVorname(), person.getVorname());
		Assert.assertEquals(personWith3Adr.getNachname(), person.getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(personWith3Adr.getId());
		Assert.assertNotNull(person.getId());
		Assert.assertEquals(3, person.getAdressen().size());
		ImmutableListMultimap<AdresseTyp, Adresse> adrByTyp = Multimaps.index(person.getAdressen(), Adresse::getAdresseTyp);
		Adresse altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
		Assert.assertTrue(altAdr.isSame(converter.adresseToEntity(personWith3Adr.getAlternativeAdresse(), new Adresse())));

	}

	@Test
	public void datesRangeAddedOnEntityTest() {
		JaxAdresse adr = TestJaxDataUtil.createTestJaxAdr(null);
		adr.setGueltigAb(null);
		adr.setGueltigBis(null);
		Adresse adrEntity = converter.adresseToEntity(adr, new Adresse());
		Assert.assertEquals(Constants.START_OF_TIME, adrEntity.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME,adrEntity.getGueltigkeit().getGueltigBis());
	}


	private Person insertNewEntity() {
		Person person = TestDataUtil.createDefaultPerson();
		persistence.persist(person);
		return person;
	}

}
