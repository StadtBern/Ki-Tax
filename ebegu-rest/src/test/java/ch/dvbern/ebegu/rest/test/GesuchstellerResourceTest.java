package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * Testet GesuchstellerResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private GesuchstellerResource gesuchstellerResource;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private JaxBConverter converter;
	private Gesuch testGesuch;
	private JaxId gesuchJAXPId;

	@Before
	public void setUp() {
		testGesuch = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(testGesuch, persistence);
		gesuchJAXPId = new JaxId(testGesuch.getId());
	}

	@Test
	public void createGesuchstellerTest() throws EbeguException {
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testJaxGesuchsteller, null, null);
		Assert.assertNotNull(jaxGesuchsteller);


	}

	@Test
	public void createGesuchstellerWithUmzugTest() throws EbeguException {
		JaxGesuchsteller testGesuchsteller = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testGesuchsteller, null, null);
		Assert.assertNotNull(jaxGesuchsteller);
		Assert.assertNotNull(jaxGesuchsteller.getUmzugAdresse());
		Assert.assertNotNull(jaxGesuchsteller.getAlternativeAdresse());
		Assert.assertNotNull(jaxGesuchsteller.getWohnAdresse());

		JaxGesuchsteller foundGesuchsteller = gesuchstellerResource.findGesuchsteller(converter.toJaxId(jaxGesuchsteller));
		Assert.assertNotNull(foundGesuchsteller);
		Assert.assertEquals(foundGesuchsteller.getId(), converter.toJaxId(jaxGesuchsteller).getId());

	}

	@Test
	public void updateGesuchstellerTest() throws EbeguException {
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testJaxGesuchsteller, null, null);
		JaxAdresse umzugAdr = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		umzugAdr.setGueltigAb(LocalDate.now().plusDays(7));

		jaxGesuchsteller.setUmzugAdresse(umzugAdr);
		JaxGesuchsteller umgezogeneGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, jaxGesuchsteller, null, null);

		Assert.assertNotNull(umgezogeneGesuchsteller.getUmzugAdresse());
		Assert.assertEquals(umgezogeneGesuchsteller.getUmzugAdresse().getStrasse(), umzugAdr.getStrasse());

	}

	@Test
	public void reactivlyAddUmzug() throws EbeguException {
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testJaxGesuchsteller, null, null);
		JaxAdresse pastUmzug = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		pastUmzug.setGueltigAb(LocalDate.now().minusDays(7));

		jaxGesuchsteller.setUmzugAdresse(pastUmzug);
		JaxGesuchsteller umgezogeneGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, jaxGesuchsteller, null, null);
		//Die Frage ist was hier das richtige verhalten ist. Fachlich gilt die Umzugadresse ja in der Gegenwart bereits als
		// Wohnadresse. Die Frage ist ob man trotzdem im GUI die Umzugadr noch anzeigen muesste
		Assert.assertNull("Umzugadresse ist bereits gueltige Wohnadresse", umgezogeneGesuchsteller.getUmzugAdresse());
		Assert.assertEquals(umgezogeneGesuchsteller.getWohnAdresse().getStrasse(), pastUmzug.getStrasse());

	}


	@Test
	public void findGesuchstellerTest() throws EbeguException {
		JaxGesuchsteller testGesuchsteller = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testGesuchsteller, null, null);
		JaxGesuchsteller foundGesuchsteller = gesuchstellerResource.findGesuchsteller(converter.toJaxId(jaxGesuchsteller));
		Assert.assertNotNull(foundGesuchsteller);
		Assert.assertEquals(testGesuchsteller.getNachname(), foundGesuchsteller.getNachname());
		foundGesuchsteller.setNachname("changednachname");

		gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, foundGesuchsteller, null, null);
		JaxGesuchsteller reloadedGesuchsteller = gesuchstellerResource.findGesuchsteller(converter.toJaxId(jaxGesuchsteller));
		Assert.assertEquals(foundGesuchsteller.getNachname(), reloadedGesuchsteller.getNachname());
		Assert.assertEquals("changednachname", reloadedGesuchsteller.getNachname());

	}



	@Test
	public void updateGesuchstellerTest2() throws EbeguException {
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testJaxGesuchsteller, null, null);
		JaxAdresse korrespondenzAdr = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		korrespondenzAdr.setOrganisation("Test");


		jaxGesuchsteller.setAlternativeAdresse(korrespondenzAdr);
		jaxGesuchsteller.getAlternativeAdresse().setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		JaxGesuchsteller umgezogeneGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, jaxGesuchsteller, null, null);

		Assert.assertNotNull(umgezogeneGesuchsteller.getAlternativeAdresse());
		Assert.assertEquals(umgezogeneGesuchsteller.getAlternativeAdresse().getOrganisation(), korrespondenzAdr.getOrganisation());

	}


}
