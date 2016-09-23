package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.api.resource.*;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.PensumFachstelleService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
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
	private BenutzerService benutzerService;

	@Inject
	private JaxBConverter converter;
	@Inject
	private Persistence<?> persistence;


	@Test
	public void createKindTest() throws EbeguException {
		UriInfo uri = new ResteasyUriInfo("test", "test", "test");
		JaxGesuch jaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		TestDataUtil.createDummyAdminAnonymous(persistence);
		Mandant persistedMandant = persistence.persist(converter.mandantToEntity(TestJaxDataUtil.createTestMandant(), new Mandant()));
		jaxGesuch.getFall().getVerantwortlicher().setMandant(converter.mandantToJAX(persistedMandant));
		benutzerService.saveBenutzer(converter.authLoginElementToBenutzer(jaxGesuch.getFall().getVerantwortlicher(), new Benutzer()));
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
		Assert.assertEquals(Integer.valueOf(1), jaxKindContainer.getKindNummer());
		Assert.assertEquals(Integer.valueOf(1), jaxKindContainer.getNextNumberBetreuung());

		JaxGesuch updatedGesuch = gesuchResource.findGesuch(converter.toJaxId(returnedGesuch));
		Assert.assertEquals(Integer.valueOf(2), updatedGesuch.getFall().getNextNumberKind());
		Assert.assertEquals(1, updatedGesuch.getKindContainers().size());
		Assert.assertEquals(testJaxKindContainer.getKindGS().getPensumFachstelle().getPensum(), jaxKindContainer.getKindGS().getPensumFachstelle().getPensum());
	}
}
