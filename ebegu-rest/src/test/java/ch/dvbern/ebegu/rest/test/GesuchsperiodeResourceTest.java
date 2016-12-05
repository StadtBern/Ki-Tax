package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.resource.GesuchsperiodeResource;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

/**
 * Testet die Gesuchsperiode Resource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeResourceTest extends AbstractEbeguRestLoginTest {


	@Inject
	private GesuchsperiodeResource gesuchsperiodeResource;

	@Inject
	private JaxBConverter converter;

	@Test
	public void createGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		JaxGesuchsperiode jaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		Assert.assertNotNull(jaxGesuchsperiode);
		Assert.assertEquals(testJaxGesuchsperiode.getActive(), jaxGesuchsperiode.getActive());

		findExistingObjectAndCompare(jaxGesuchsperiode);
	}

	@Test
	public void removeGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		JaxGesuchsperiode jaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);

		findExistingObjectAndCompare(jaxGesuchsperiode);

		gesuchsperiodeResource.removeGesuchsperiode(converter.toJaxId(jaxGesuchsperiode), null);

		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchsperiodeResource.findGesuchsperiode(converter.toJaxId(jaxGesuchsperiode));
		Assert.assertNull(foundJaxGesuchsperiode);
	}

	@Test
	public void getAllGesuchsperiodenTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		testJaxGesuchsperiode.setActive(false);
		gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);

		JaxGesuchsperiode jaxGesuchsperiode2 = gesuchsperiodeResource.saveGesuchsperiode(TestJaxDataUtil.createTestJaxGesuchsperiode(), null, null);

		List<JaxGesuchsperiode> listAll = gesuchsperiodeResource.getAllGesuchsperioden();
		Assert.assertNotNull(listAll);
		Assert.assertEquals(2, listAll.size());

		List<JaxGesuchsperiode> listActive = gesuchsperiodeResource.getAllActiveGesuchsperioden();
		Assert.assertNotNull(listActive);
		Assert.assertEquals(1, listActive.size());
		Assert.assertEquals(listActive.get(0), jaxGesuchsperiode2);
	}


	// HELP METHODS

	private void findExistingObjectAndCompare(JaxGesuchsperiode jaxGesuchsperiode) {
		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchsperiodeResource.findGesuchsperiode(converter.toJaxId(jaxGesuchsperiode));
		Assert.assertNotNull(foundJaxGesuchsperiode);
		Assert.assertEquals(jaxGesuchsperiode, foundJaxGesuchsperiode);
	}
}
