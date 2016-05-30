package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.resource.GesuchsperiodeResource;
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

import static ch.dvbern.ebegu.rest.test.AbstractEbeguRestTest.createTestArchive;

/**
 * Testet die Gesuchsperiode Resource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeResourceTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private GesuchsperiodeResource gesuchstellerResource;

	@Inject
	private JaxBConverter converter;

	@Test
	public void createGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		JaxGesuchsperiode jaxGesuchsperiode = gesuchstellerResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		Assert.assertNotNull(jaxGesuchsperiode);
		Assert.assertEquals(testJaxGesuchsperiode.getActive(), jaxGesuchsperiode.getActive());

		findExistingObjectAndCompare(jaxGesuchsperiode);
	}

	@Test
	public void removeGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		JaxGesuchsperiode jaxGesuchsperiode = gesuchstellerResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);

		findExistingObjectAndCompare(jaxGesuchsperiode);

		gesuchstellerResource.removeGesuchsperiode(converter.toJaxId(jaxGesuchsperiode), null);

		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchstellerResource.findGesuchsperiode(converter.toJaxId(jaxGesuchsperiode));
		Assert.assertNull(foundJaxGesuchsperiode);
	}


	// HELP METHODS

	private void findExistingObjectAndCompare(JaxGesuchsperiode jaxGesuchsperiode) {
		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchstellerResource.findGesuchsperiode(converter.toJaxId(jaxGesuchsperiode));
		Assert.assertNotNull(foundJaxGesuchsperiode);
		Assert.assertEquals(jaxGesuchsperiode, foundJaxGesuchsperiode);
	}
}
