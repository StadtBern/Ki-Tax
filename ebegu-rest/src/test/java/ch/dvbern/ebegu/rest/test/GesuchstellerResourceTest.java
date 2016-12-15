package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresseContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
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
public class GesuchstellerResourceTest extends AbstractEbeguRestLoginTest {



	@Inject
	private GesuchstellerResource gesuchstellerResource;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private JaxBConverter converter;
	private JaxId gesuchJAXPId;

	@Before
	public void setUp() {
		final Gesuch testGesuch = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(testGesuch, persistence);
		gesuchJAXPId = new JaxId(testGesuch.getId());
	}

	@Test
	public void createGesuchstellerTest() throws EbeguException {
		JaxGesuchstellerContainer testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testJaxGesuchsteller, null, null);
		Assert.assertNotNull(jaxGesuchsteller);


	}

	@Test
	public void createGesuchstellerWithUmzugTest() throws EbeguException {
		JaxGesuchstellerContainer testGesuchsteller = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testGesuchsteller, null, null);
		Assert.assertNotNull(jaxGesuchsteller);
		Assert.assertNotNull(jaxGesuchsteller.getAlternativeAdresse());
		Assert.assertNotNull(jaxGesuchsteller.getAdressen());

		JaxGesuchstellerContainer foundGesuchsteller = gesuchstellerResource.findGesuchsteller(converter.toJaxId(jaxGesuchsteller));
		Assert.assertNotNull(foundGesuchsteller);
		Assert.assertEquals(foundGesuchsteller.getId(), converter.toJaxId(jaxGesuchsteller).getId());

	}

	@Test
	public void updateGesuchstellerTest() throws EbeguException {
		JaxGesuchstellerContainer testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		final JaxAdresseContainer oldAdresse = testJaxGesuchsteller.getAdressen().get(0);
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testJaxGesuchsteller, null, null);
		JaxAdresseContainer umzugAdr = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		umzugAdr.getAdresseJA().setGueltigAb(LocalDate.now().plusDays(7));

		jaxGesuchsteller.addAdresse(umzugAdr);
		JaxGesuchstellerContainer umgezogeneGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, jaxGesuchsteller, null, null);

		Assert.assertNotNull(umgezogeneGesuchsteller.getAdressen());
		Assert.assertEquals(2, umgezogeneGesuchsteller.getAdressen().size());
		Assert.assertEquals(umgezogeneGesuchsteller.getAdressen().get(0).getAdresseJA().getStrasse(), oldAdresse.getAdresseJA().getStrasse());
		Assert.assertEquals(umgezogeneGesuchsteller.getAdressen().get(1).getAdresseJA().getStrasse(), umzugAdr.getAdresseJA().getStrasse());

	}

	@Test
	public void removeKorrespondenzaddr() throws EbeguException {
		JaxGesuchstellerContainer testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testJaxGesuchsteller, null, null);
		JaxAdresseContainer korrArr = TestJaxDataUtil.createTestJaxAdr("korradr");
		korrArr.getAdresseJA().setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);

		jaxGesuchsteller.setAlternativeAdresse(korrArr);
		JaxGesuchstellerContainer gesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, jaxGesuchsteller, null, null);
		Assert.assertNotNull(gesuchsteller.getAlternativeAdresse());

		gesuchsteller.setAlternativeAdresse(null);
		gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, jaxGesuchsteller, null, null);
		//Nun wollen wir testen was passiert wenn man die Korrespondenzadr wieder entfernt
		Assert.assertNull("Korrespondenzaddr muss geloscht sein", gesuchsteller.getAlternativeAdresse());

	}

	@Test
	public void findGesuchstellerTest() throws EbeguException {
		JaxGesuchstellerContainer testGesuchsteller = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testGesuchsteller, null, null);
		JaxGesuchstellerContainer foundGesuchsteller = gesuchstellerResource.findGesuchsteller(converter.toJaxId(jaxGesuchsteller));
		Assert.assertNotNull(foundGesuchsteller);
		Assert.assertEquals(testGesuchsteller.getGesuchstellerJA().getNachname(), foundGesuchsteller.getGesuchstellerJA().getNachname());
		foundGesuchsteller.getGesuchstellerJA().setNachname("changednachname");

		gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, foundGesuchsteller, null, null);
		JaxGesuchstellerContainer reloadedGesuchsteller = gesuchstellerResource.findGesuchsteller(converter.toJaxId(jaxGesuchsteller));
		Assert.assertEquals(foundGesuchsteller.getGesuchstellerJA().getNachname(), reloadedGesuchsteller.getGesuchstellerJA().getNachname());
		Assert.assertEquals("changednachname", reloadedGesuchsteller.getGesuchstellerJA().getNachname());

	}



	@Test
	public void updateGesuchstellerTest2() throws EbeguException {
		JaxGesuchstellerContainer testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testJaxGesuchsteller, null, null);
		JaxAdresseContainer korrespondenzAdr = TestJaxDataUtil.createTestJaxAdr("umzugadr");
		korrespondenzAdr.getAdresseJA().setOrganisation("Test");


		jaxGesuchsteller.setAlternativeAdresse(korrespondenzAdr);
		jaxGesuchsteller.getAlternativeAdresse().getAdresseJA().setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		JaxGesuchstellerContainer umgezogeneGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, jaxGesuchsteller, null, null);

		Assert.assertNotNull(umgezogeneGesuchsteller.getAlternativeAdresse());
		Assert.assertEquals(umgezogeneGesuchsteller.getAlternativeAdresse().getAdresseJA().getOrganisation(),
			korrespondenzAdr.getAdresseJA().getOrganisation());

	}


}
