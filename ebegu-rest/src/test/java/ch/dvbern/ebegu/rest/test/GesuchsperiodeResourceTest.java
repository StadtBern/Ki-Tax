package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.resource.GesuchsperiodeResource;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
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
		// Gesuchsperiode muss zuerst als ENTWURF gespeichert werden
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.ENTWURF);
		JaxGesuchsperiode jaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		Assert.assertNotNull(jaxGesuchsperiode);
		Assert.assertEquals(testJaxGesuchsperiode.getStatus(), jaxGesuchsperiode.getStatus());

		findExistingObjectAndCompare(jaxGesuchsperiode);
	}

	@Test (expected = EbeguRuntimeException.class)
	public void createGesuchsperiodeAsAktivTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		// Gesuchsperiode muss zuerst als ENTWURF gespeichert werden
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
	}

	@Test
	public void removeGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		// Gesuchsperiode muss zuerst als ENTWURF gespeichert werden
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.ENTWURF);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.INAKTIV);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.GESCHLOSSEN);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);

		findExistingObjectAndCompare(testJaxGesuchsperiode);

		gesuchsperiodeResource.removeGesuchsperiode(converter.toJaxId(testJaxGesuchsperiode), null);

		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchsperiodeResource.findGesuchsperiode(converter.toJaxId(testJaxGesuchsperiode));
		Assert.assertNull(foundJaxGesuchsperiode);
	}

	@Test
	public void getAllGesuchsperiodenTest() {
		saveGesuchsperiodeInStatusEntwurf(TestJaxDataUtil.createTestJaxGesuchsperiode());
		saveGesuchsperiodeInStatusAktiv(TestJaxDataUtil.createTestJaxGesuchsperiode());
		saveGesuchsperiodeInStatusInaktiv(TestJaxDataUtil.createTestJaxGesuchsperiode());
		saveGesuchsperiodeInStatusGesperrt(TestJaxDataUtil.createTestJaxGesuchsperiode());

		List<JaxGesuchsperiode> listAll = gesuchsperiodeResource.getAllGesuchsperioden();
		Assert.assertNotNull(listAll);
		Assert.assertEquals(4, listAll.size());

		List<JaxGesuchsperiode> listActive = gesuchsperiodeResource.getAllActiveGesuchsperioden();
		Assert.assertNotNull(listActive);
		Assert.assertEquals(1, listActive.size());

		List<JaxGesuchsperiode> listActiveAndInaktiv = gesuchsperiodeResource.getAllNichtAbgeschlosseneGesuchsperioden();
		Assert.assertNotNull(listActiveAndInaktiv);
		Assert.assertEquals(2, listActiveAndInaktiv.size());
	}


	// HELP METHODS

	private void findExistingObjectAndCompare(JaxGesuchsperiode jaxGesuchsperiode) {
		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchsperiodeResource.findGesuchsperiode(converter.toJaxId(jaxGesuchsperiode));
		Assert.assertNotNull(foundJaxGesuchsperiode);
		Assert.assertEquals(jaxGesuchsperiode, foundJaxGesuchsperiode);
	}
}
