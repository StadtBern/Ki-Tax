package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.api.resource.*;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.PensumFachstelleService;
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
 * Testet KindResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class BetreuungResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private BetreuungResource betreuungResource;
	@Inject
	private KindResource kindResource;
	@Inject
	private GesuchResource gesuchResource;
	@Inject
	private FallResource fallResource;
	@Inject
	private FachstelleResource fachstelleResource;
	@Inject
	private PensumFachstelleService pensumFachstelleService;
	@Inject
	private MandantResource mandantResource;
	@Inject
	private JaxBConverter converter;
	@Inject
	private Persistence<?> persistence;

	@Test
	public void createKindTest() throws EbeguException {
		UriInfo uri = new ResteasyUriInfo("test", "test", "test");
		KindContainer returnedKind = persistKindAndDependingObjects(uri);

		Betreuung testBetreuung = TestDataUtil.createDefaultBetreuung();
		JaxBetreuung testJaxBetreuung = converter.betreuungToJAX(testBetreuung);

		persistence.persist(testBetreuung.getInstitutionStammdaten().getInstitution().getMandant());
		persistence.persist(testBetreuung.getInstitutionStammdaten().getInstitution().getTraegerschaft());
		persistence.persist(testBetreuung.getInstitutionStammdaten().getInstitution());
		persistence.persist(testBetreuung.getInstitutionStammdaten());

		JaxBetreuung jaxBetreuung = betreuungResource.saveBetreuung(converter.toJaxId(returnedKind), testJaxBetreuung, uri, null);
		Assert.assertNotNull(jaxBetreuung);
	}


	// HELP

	private KindContainer persistKindAndDependingObjects(UriInfo uri) throws EbeguException {
		JaxGesuch jaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		JaxFall returnedFall = (JaxFall) fallResource.create(jaxGesuch.getFall(), uri, null).getEntity();
		jaxGesuch.setFall(returnedFall);
		JaxGesuch returnedGesuch = (JaxGesuch) gesuchResource.create(jaxGesuch, uri, null).getEntity();

		KindContainer returnedKind = TestDataUtil.createDefaultKindContainer();
		JaxKindContainer jaxKind = converter.kindContainerToJAX(returnedKind);
		JaxPensumFachstelle jaxPensumFachstelle = jaxKind.getKindGS().getPensumFachstelle();
		jaxPensumFachstelle.setFachstelle(fachstelleResource.saveFachstelle(jaxPensumFachstelle.getFachstelle(), null, null));
		PensumFachstelle returnedPensumFachstelle = pensumFachstelleService.savePensumFachstelle(
			converter.pensumFachstelleToEntity(jaxPensumFachstelle, new PensumFachstelle()));
		JaxPensumFachstelle convertedPensumFachstelle = converter.pensumFachstelleToJax(returnedPensumFachstelle);
		jaxKind.getKindGS().setPensumFachstelle(convertedPensumFachstelle);
		jaxKind.getKindJA().setPensumFachstelle(convertedPensumFachstelle);
		kindResource.saveKind(converter.toJaxId(returnedGesuch), jaxKind, uri, null);
		return returnedKind;
	}
}
