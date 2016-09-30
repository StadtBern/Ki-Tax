package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungInfo;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.resource.EinkommensverschlechterungInfoResource;
import ch.dvbern.ebegu.api.resource.FallResource;
import ch.dvbern.ebegu.api.resource.GesuchResource;
import ch.dvbern.ebegu.api.resource.GesuchsperiodeResource;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

/**
 * Testet die Gesuchsperiode Resource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class EinkommensverschlechterungInfoResourceTest extends AbstractEbeguRestTest{



	@Inject
	private EinkommensverschlechterungInfoResource einkommensverschlechterungInfoResource;

	@Inject
	private GesuchResource gesuchResource;

	@Inject
	private FallResource fallResource;

	@Inject
	private GesuchsperiodeResource gesuchsperiodeResource;

	@Inject
	private JaxBConverter converter;

	@Inject
	private Persistence<Benutzer> persistence;



	@Test
	public void createEinkommensverschlechterungInfoTest() throws EbeguException {

		UriInfo uri = new ResteasyUriInfo("test", "test", "test");
		JaxGesuch returnedGesuch = crateJaxGesuch(uri);

		JaxGesuch gesuch = gesuchResource.findGesuch(converter.toJaxId(returnedGesuch));
		Assert.assertNotNull(gesuch);
		Assert.assertNull(gesuch.getEinkommensverschlechterungInfo());

		final JaxEinkommensverschlechterungInfo testJaxEinkommensverschlechterungInfo = TestJaxDataUtil.createTestJaxEinkommensverschlechterungInfo();

		einkommensverschlechterungInfoResource.saveEinkommensverschlechterungInfo(converter.toJaxId(returnedGesuch), testJaxEinkommensverschlechterungInfo, uri, null);

		gesuch = gesuchResource.findGesuch(converter.toJaxId(returnedGesuch));
		Assert.assertNotNull(gesuch);
		Assert.assertNotNull(gesuch.getEinkommensverschlechterungInfo());
	}

	private JaxGesuch crateJaxGesuch(UriInfo uri) throws EbeguException {
		Benutzer verantwortlicher = TestDataUtil.createDefaultBenutzer();
		persistence.persist(verantwortlicher.getMandant());
		verantwortlicher = persistence.persist(verantwortlicher);

		JaxGesuch testJaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		testJaxGesuch.getFall().setVerantwortlicher(converter.benutzerToAuthLoginElement(verantwortlicher));


		JaxFall returnedFall = fallResource.saveFall(testJaxGesuch.getFall(), uri, null);
		testJaxGesuch.setGesuchsperiode(gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuch.getGesuchsperiode(), uri, null));
		testJaxGesuch.setFall(returnedFall);
		TestDataUtil.createDummyAdminAnonymous(persistence);
		return (JaxGesuch) gesuchResource.create(testJaxGesuch, uri, null).getEntity();
	}


}
