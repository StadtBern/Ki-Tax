package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.ErwerbspensumResource;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
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

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Testet die Erwerbspensum Resource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumResourceTest extends AbstractEbeguRestTest{


	@Inject
	private GesuchstellerResource gesuchstellerResource;
	@Inject
	private ErwerbspensumResource erwerbspensumResource;
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
	public void createGesuchstelelrWithErwerbspensumTest() throws EbeguException {
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchstellerWithErwerbsbensum();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, testJaxGesuchsteller, null, null);
		Assert.assertNotNull(jaxGesuchsteller);

	}

	@Test
	public void createErwerbspensumTest() throws EbeguException {
		JaxGesuchsteller jaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller gesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, jaxGesuchsteller, null, null);
		Response response = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(gesuchsteller), TestJaxDataUtil.createTestJaxErwerbspensumContainer(), null, null);
		JaxErwerbspensumContainer jaxErwerbspensum = (JaxErwerbspensumContainer) response.getEntity();
		Assert.assertNotNull(jaxErwerbspensum);

	}

	@Test
	public void updateGesuchstellerTest() throws EbeguException {
		JaxGesuchsteller jaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller storedGS = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, jaxGesuchsteller, null, null);
		Response response = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(storedGS), TestJaxDataUtil.createTestJaxErwerbspensumContainer(), null, null);
		JaxErwerbspensumContainer jaxErwerbspensum = (JaxErwerbspensumContainer) response.getEntity();
		JaxErwerbspensumContainer loadedEwp = erwerbspensumResource.findErwerbspensum(converter.toJaxId(jaxErwerbspensum));
		Assert.assertNotNull(loadedEwp);
		Assert.assertNotEquals(Integer.valueOf(20), loadedEwp.getErwerbspensumGS().getZuschlagsprozent());

		jaxErwerbspensum.getErwerbspensumGS().setZuschlagsprozent(18);
		Response result = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(storedGS), jaxErwerbspensum, null, null);
		JaxErwerbspensumContainer updatedContainer = (JaxErwerbspensumContainer) result.getEntity();
		Assert.assertEquals(Integer.valueOf(18), updatedContainer.getErwerbspensumGS().getZuschlagsprozent());

	}

	@Test
	public void invalidPercentErwerbspensumTest() throws EbeguException {
		JaxGesuchsteller jaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller storedGS = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, jaxGesuchsteller, null, null);
		Response response = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(storedGS), TestJaxDataUtil.createTestJaxErwerbspensumContainer(), null, null);
		JaxErwerbspensumContainer jaxErwerbspensum = (JaxErwerbspensumContainer) response.getEntity();
		JaxErwerbspensumContainer loadedEwp = erwerbspensumResource.findErwerbspensum(converter.toJaxId(jaxErwerbspensum));
		Assert.assertNotNull(loadedEwp);
		loadedEwp.getErwerbspensumGS().setZuschlagsprozent(50);
		try{
			erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(storedGS),loadedEwp,null,null);
			Assert.fail("50% is invalid");
		} catch (EJBException e){
			Assert.assertNotNull(e);
		}

	}

}
