package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.DownloadResource;
import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.GeneratedDokument_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Collection;

import static org.easymock.EasyMock.*;

/**
 * Testet BetreuungResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class DownloadResourceTest extends AbstractEbeguRestTest {

	@Inject
	private DownloadResource downloadResource;
	@Inject
	private InstitutionService instService;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private CriteriaQueryHelper queryHelper;
	@Inject
	private EbeguParameterService parameterService;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void getVerfuegungDokumentAccessTokenGeneratedDokumentTest() throws MergeDocException, IOException, DocTemplateException, MimeTypeParseException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);
		final String betreuungId = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getId();

		HttpServletRequest request = mockRequest();
		UriInfo uri = new ResteasyUriInfo("uri", "query", "path");

		final Response dokumentResponse = downloadResource
			.getVerfuegungDokumentAccessTokenGeneratedDokument(new JaxId(gesuch.getId()), new JaxId(betreuungId), true, "", request, uri);

		assertResults(gesuch, dokumentResponse.getEntity(), GeneratedDokumentTyp.VERFUEGUNG_KITA);
	}

	@Test
	public void getDokumentAccessTokenGeneratedDokumentBEGLEITSCHREIBENTest() throws MergeDocException, IOException, DocTemplateException, MimeTypeParseException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);

		HttpServletRequest request = mockRequest();
		UriInfo uri = new ResteasyUriInfo("uri", "query", "path");

		final Response dokumentResponse = downloadResource
			.getDokumentAccessTokenGeneratedDokument(new JaxId(gesuch.getId()), GeneratedDokumentTyp.BEGLEITSCHREIBEN, true, request, uri);

		assertResults(gesuch, dokumentResponse.getEntity(), GeneratedDokumentTyp.BEGLEITSCHREIBEN);
	}

	@Test
	public void getDokumentAccessTokenGeneratedDokumentFINANZIELLE_SITUATIONTest() throws MergeDocException, IOException, DocTemplateException, MimeTypeParseException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);

		HttpServletRequest request = mockRequest();
		UriInfo uri = new ResteasyUriInfo("uri", "query", "path");

		final Response dokumentResponse = downloadResource
			.getDokumentAccessTokenGeneratedDokument(new JaxId(gesuch.getId()), GeneratedDokumentTyp.FINANZIELLE_SITUATION, true, request, uri);

		assertResults(gesuch, dokumentResponse.getEntity(), GeneratedDokumentTyp.FINANZIELLE_SITUATION);
	}

	// HELP METHODS

	@Nonnull
	private HttpServletRequest mockRequest() {
		HttpServletRequest request = createMock(HttpServletRequest.class);
		expect(request.getHeader("X-FORWARDED-FOR")).andReturn("1.1.1.1").anyTimes();
		replay(request);
		return request;
	}

	private void assertResults(Gesuch gesuch, Object entity, GeneratedDokumentTyp typ) {
		final Collection<GeneratedDokument> generatedDokumente = queryHelper
			.getEntitiesByAttribute(GeneratedDokument.class, gesuch, GeneratedDokument_.gesuch);
		Assert.assertNotNull(entity);
		Assert.assertNotNull(generatedDokumente);
		Assert.assertEquals(1, generatedDokumente.size());
		Assert.assertEquals(typ, generatedDokumente.iterator().next().getTyp());
	}

}
