package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.api.resource.*;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.PensumFachstelleService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

/**
 * Testet KindResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class KindResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private KindResource kindResource;
	@Inject
	private GesuchResource gesuchResource;
	@Inject
	private GesuchsperiodeResource gesuchsperiodeResource;
	@Inject
	private FallResource fallResource;
	@Inject
	private FachstelleResource fachstelleResource;
	@Inject
	private PensumFachstelleService pensumFachstelleService;

	@Inject
	private JaxBConverter converter;


	@Test
	public void createKindTest() throws EbeguException {
		UriInfo uri = new ResteasyUriInfo("test", "test", "test");
		JaxGesuch jaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		JaxFall returnedFall = fallResource.saveFall(jaxGesuch.getFall(), uri, null);
		JaxGesuchsperiode returnedGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(jaxGesuch.getGesuchsperiode(), uri, null);
		jaxGesuch.setFall(returnedFall);
		jaxGesuch.setGesuchsperiode(returnedGesuchsperiode);
		JaxGesuch returnedGesuch = (JaxGesuch) gesuchResource.create(jaxGesuch, uri, null).getEntity();

		JaxKindContainer testJaxKindContainer = TestJaxDataUtil.createTestJaxKindContainer();
		JaxPensumFachstelle jaxPensumFachstelle = testJaxKindContainer.getKindGS().getPensumFachstelle();
		jaxPensumFachstelle.setFachstelle(fachstelleResource.saveFachstelle(jaxPensumFachstelle.getFachstelle(), null, null));
		PensumFachstelle returnedPensumFachstelle = pensumFachstelleService.savePensumFachstelle(
			converter.pensumFachstelleToEntity(jaxPensumFachstelle, new PensumFachstelle()));
		JaxPensumFachstelle convertedPensumFachstelle = converter.pensumFachstelleToJax(returnedPensumFachstelle);
		testJaxKindContainer.getKindGS().setPensumFachstelle(convertedPensumFachstelle);
		testJaxKindContainer.getKindJA().setPensumFachstelle(convertedPensumFachstelle);

		JaxKindContainer jaxKindContainer = kindResource.saveKind(converter.toJaxId(returnedGesuch), testJaxKindContainer, null, null);

		Assert.assertNotNull(jaxKindContainer);

		JaxGesuch updatedGesuch = gesuchResource.findGesuch(converter.toJaxId(returnedGesuch));
		Assert.assertEquals(1, updatedGesuch.getKindContainers().size());
		Assert.assertEquals(testJaxKindContainer.getKindGS().getPensumFachstelle().getPensum(), jaxKindContainer.getKindGS().getPensumFachstelle().getPensum());
	}
}
