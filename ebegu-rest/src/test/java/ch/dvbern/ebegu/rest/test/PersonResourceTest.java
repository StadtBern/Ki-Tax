package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxPerson;
import ch.dvbern.ebegu.api.resource.PersonResource;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * Testet PersonenResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class PersonResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private PersonResource personResource;

	@Inject
	private JaxBConverter converter;


	@Test
	public void createPersonTest() throws EbeguException {
		JaxPerson testJaxPerson = TestJaxDataUtil.createTestJaxPerson();
		JaxPerson jaxPerson = personResource.create(testJaxPerson, null, null);
		Assert.assertNotNull(jaxPerson);


	}

	@Test
	public void createPersonWithUmzugTest() throws EbeguException {
		JaxPerson testPerson = TestJaxDataUtil.createTestJaxPersonWithUmzug();
		JaxPerson jaxPerson = personResource.create(testPerson, null, null);
		Assert.assertNotNull(jaxPerson);
		Assert.assertNotNull(jaxPerson.getUmzugAdresse());
		Assert.assertNotNull(jaxPerson.getAlternativeAdresse());
		Assert.assertNotNull(jaxPerson.getWohnAdresse());

		JaxPerson foundPerson = personResource.findPerson(converter.toJaxId(jaxPerson));
		Assert.assertNotNull(foundPerson);
		Assert.assertEquals(foundPerson.getId(), jaxPerson.getId());
	}

	@Test
	public void updatePersonTest() throws EbeguException {
		JaxPerson testJaxPerson = TestJaxDataUtil.createTestJaxPerson();
		JaxPerson jaxPerson = personResource.create(testJaxPerson, null, null);
		JaxAdresse umzugAdr = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		umzugAdr.setGueltigAb(LocalDate.now().plusDays(7));

		jaxPerson.setUmzugAdresse(umzugAdr);
		JaxPerson umgezogenePerson = personResource.update(jaxPerson, null, null);

		Assert.assertNotNull(umgezogenePerson.getUmzugAdresse());
		Assert.assertEquals(umgezogenePerson.getUmzugAdresse().getStrasse(), umzugAdr.getStrasse());


	}

	@Test
	public void reactivlyAddUmzug() throws EbeguException {
		JaxPerson testJaxPerson = TestJaxDataUtil.createTestJaxPerson();
		JaxPerson jaxPerson = personResource.create(testJaxPerson, null, null);
		JaxAdresse pastUmzug = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		pastUmzug.setGueltigAb(LocalDate.now().minusDays(7));

		jaxPerson.setUmzugAdresse(pastUmzug);
		JaxPerson umgezogenePerson = personResource.update(jaxPerson, null, null);
		//Die Frage ist was hier das richtige verhalten ist. Fachlich gilt die Umzugadresse ja in der Gegenwart bereits als
		// Wohnadresse. Die Frage ist ob man trotzdem im GUI die Umzugadr noch anzeigen muesste
		Assert.assertNull("Umzugadresse ist bereits gueltige Wohnadresse", umgezogenePerson.getUmzugAdresse());
		Assert.assertEquals(umgezogenePerson.getWohnAdresse().getStrasse(), pastUmzug.getStrasse());

	}


	@Test
	public void findPersonTest() throws EbeguException {
		JaxPerson testPerson = TestJaxDataUtil.createTestJaxPersonWithUmzug();
		JaxPerson jaxPerson = personResource.create(testPerson, null, null);
		JaxPerson foundPers = personResource.findPerson(converter.toJaxId(jaxPerson));
		Assert.assertNotNull(foundPers);
		Assert.assertEquals(testPerson.getNachname(), foundPers.getNachname());
		foundPers.setNachname("changednachname");

		personResource.update(foundPers, null, null);
		JaxPerson reloadedPerson = personResource.findPerson(converter.toJaxId(jaxPerson));
		Assert.assertEquals(foundPers.getNachname(), reloadedPerson.getNachname());
		Assert.assertEquals("changednachname", reloadedPerson.getNachname());

	}


}
