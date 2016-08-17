package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxTempDokument;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.TempDokument;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.DokumentService;
import ch.dvbern.ebegu.services.TempDokumentService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;

/**
 * REST Resource fuer Institution
 */
@Path("blobs/temp")
@Stateless
@Api
public class DownloadResource {


	private static final Logger LOG = LoggerFactory.getLogger(DownloadResource.class);

	@Inject
	private TempDokumentService tempDokumentService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private DokumentService dokumentService;


	@GET
	@Path("blobdata/{accessToken}/{filename}")
	public Response downloadByAccessToken(
		@PathParam("accessToken") String blobAccessTokenParam,
		@PathParam("filename") String filename,
		@MatrixParam("attachment") @DefaultValue("false") boolean attachment,
		@Context HttpServletRequest request) {

		String ip = getIP(request);


		TempDokument tempDokument = tempDokumentService.getTempDownloadByAccessToken(blobAccessTokenParam);

		if (tempDokument == null) {
			return Response.status(Response.Status.FORBIDDEN).entity("Ung&uuml;ltige Anfrage f&uuml;r download").build();
		}

		if (!tempDokument.getIp().equals(ip)) {
			return Response.status(Response.Status.FORBIDDEN).entity("Keine Berechtigung f&uuml;r download").build();
		}

		try {
			return RestUtil.buildDownloadResponse(tempDokument.getDokument(), attachment);
		} catch (IOException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Dokument kann nicht gelesen werden").build();
		}

	}

	@Nonnull
	@GET
	@Path("/{dokumentId}/download")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getDokumentAccessToken(
		@Nonnull @Valid @PathParam("dokumentId") JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) throws EbeguEntityNotFoundException {

		String ip = getIP(request);

		Validate.notNull(jaxId.getId());
		String id = converter.toEntityId(jaxId);

		final Dokument dokument = dokumentService.findDokument(id)
			.orElseThrow(() -> new EbeguEntityNotFoundException("updateFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));


		final TempDokument tempDokument = tempDokumentService.create(dokument, ip);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(DownloadResource.class)
			.path("/" + tempDokument.getId())
			.build();

		JaxTempDokument jaxTempDokument = converter.tempDokumentToJAX(tempDokument);

		return Response.created(uri).entity(jaxTempDokument).build();
	}

	private String getIP(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}
