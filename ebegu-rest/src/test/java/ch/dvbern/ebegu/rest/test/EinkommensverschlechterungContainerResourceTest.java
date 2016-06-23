package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.api.resource.EinkommensverschlechterungContainerResource;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
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
 * Testet GesuchstellerResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class EinkommensverschlechterungContainerResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private GesuchstellerResource gesuchstellerResource;

	@Inject
	private EinkommensverschlechterungContainerResource einkommensverschlechterungContainerResource;

	@Inject
	private JaxBConverter converter;

	private UriInfo uri = new ResteasyUriInfo("test", "test", "test");

	@Test
	public void createAndFindEinkommensverschlechterungsContainerTest() throws EbeguException {
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.createGesuchsteller(testJaxGesuchsteller, uri, null);
		Assert.assertNotNull(jaxGesuchsteller);

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainer = TestJaxDataUtil.createTestJaxEinkommensverschlechterungContianer();

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainerReturned =
			(JaxEinkommensverschlechterungContainer) einkommensverschlechterungContainerResource.
				saveEinkommensverschlechterungContainer(converter.toJaxId(jaxGesuchsteller), jaxEinkommensverschlechterungContainer, uri, null).getEntity();

		Assert.assertNotNull(jaxEinkommensverschlechterungContainerReturned);

		final JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerFound =
			einkommensverschlechterungContainerResource.findEinkommensverschlechterungContainer(
				converter.toJaxId(jaxEinkommensverschlechterungContainerReturned));
		Assert.assertNotNull(einkommensverschlechterungContainerFound);

	}

}
