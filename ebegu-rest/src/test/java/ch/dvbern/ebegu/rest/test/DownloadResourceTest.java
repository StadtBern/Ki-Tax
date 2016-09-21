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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
	public void getDokumentAccessTokenGeneratedDokumentTest() throws MergeDocException, MalformedURLException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);

		HttpServletRequest request = createMock(HttpServletRequest.class);
		expect(request.getHeader("X-FORWARDED-FOR")).andReturn("1.1.1.1").anyTimes();
		replay(request);

		UriInfo uri = new ResteasyUriInfo("uri", "query", "path");

		final Response dokumentResponse = downloadResource.getDokumentAccessTokenGeneratedDokument(new JaxId(gesuch.getId()),
			GeneratedDokumentTyp.VERFUEGUNG_KITA, request, uri);

		final Collection<GeneratedDokument> generatedDokumente = queryHelper
			.getEntitiesByAttribute(GeneratedDokument.class, gesuch, GeneratedDokument_.gesuch);

		Assert.assertNotNull(dokumentResponse.getEntity());
		Assert.assertNotNull(generatedDokumente);
		Assert.assertEquals(1, generatedDokumente.size());
		Assert.assertEquals(GeneratedDokumentTyp.VERFUEGUNG_KITA, generatedDokumente.iterator().next().getTyp());
	}
}
