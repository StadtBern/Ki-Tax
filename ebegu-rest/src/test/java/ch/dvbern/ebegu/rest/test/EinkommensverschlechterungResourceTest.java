package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.EinkommensverschlechterungResource;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
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
public class EinkommensverschlechterungResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private GesuchstellerResource gesuchstellerResource;

	@Inject
	private EinkommensverschlechterungResource einkommensverschlechterungResource;

	@Inject
	private JaxBConverter converter;
	@Inject
	private Persistence<Gesuch> persistence;

	private UriInfo uri = new ResteasyUriInfo("test", "test", "test");

	@Test
	public void createAndFindEinkommensverschlechterungsContainerTest() throws EbeguException {
		Gesuch testGesuch = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(testGesuch, persistence);
		JaxGesuchsteller testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchsteller jaxGesuchsteller = gesuchstellerResource.createGesuchsteller(new JaxId(testGesuch.getId()), 1, testJaxGesuchsteller, uri, null);
		Assert.assertNotNull(jaxGesuchsteller);

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainer = TestJaxDataUtil.createTestJaxEinkommensverschlechterungContianer();

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainerReturned =
			(JaxEinkommensverschlechterungContainer) einkommensverschlechterungResource.
				saveEinkommensverschlechterungContainer(new JaxId(testGesuch.getId()), converter.toJaxId(jaxGesuchsteller),
					jaxEinkommensverschlechterungContainer, uri, null).getEntity();

		Assert.assertNotNull(jaxEinkommensverschlechterungContainerReturned);

		final JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerFound =
			einkommensverschlechterungResource.findEinkommensverschlechterungContainer(
				converter.toJaxId(jaxEinkommensverschlechterungContainerReturned));
		Assert.assertNotNull(einkommensverschlechterungContainerFound);

	}

}
