package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxTempDokument;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.File;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.DokumentService;
import ch.dvbern.ebegu.services.DownloadFileService;
import ch.dvbern.ebegu.services.EbeguVorlageService;
import ch.dvbern.ebegu.services.VorlageService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

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


	@Inject
	private DownloadFileService downloadFileService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private DokumentService dokumentService;

	@Inject
	private VorlageService vorlageService;

	@GET
	@Path("blobdata/{accessToken}/{filename}")
	//mimetyp wird in buildDownloadResponse erraten
	public Response downloadByAccessToken(
		@PathParam("accessToken") String blobAccessTokenParam,
		@PathParam("filename") String filename,
		@MatrixParam("attachment") @DefaultValue("false") boolean attachment,
		@Context HttpServletRequest request) {

		String ip = getIP(request);


		DownloadFile downloadFile = downloadFileService.getDownloadFileByAccessToken(blobAccessTokenParam);

		if (downloadFile == null) {
			return Response.status(Response.Status.FORBIDDEN).entity("Ung&uuml;ltige Anfrage f&uuml;r download").build();
		}

		if (!downloadFile.getIp().equals(ip)) {
			return Response.status(Response.Status.FORBIDDEN).entity("Keine Berechtigung f&uuml;r download").build();
		}

		try {
			return RestUtil.buildDownloadResponse(downloadFile, attachment);
		} catch (IOException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Dokument kann nicht gelesen werden").build();
		}

	}

	@Nonnull
	@GET
	@Path("/{dokumentId}/dokument")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getDokumentAccessTokenDokument(
		@Nonnull @Valid @PathParam("dokumentId") JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) throws EbeguEntityNotFoundException {

		String ip = getIP(request);

		Validate.notNull(jaxId.getId());
		String id = converter.toEntityId(jaxId);

		final File dokument = dokumentService.findDokument(id)
			.orElseThrow(() -> new EbeguEntityNotFoundException("getDokumentAccessTokenDokument", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));


		return getFileDownloadResponse(uriInfo, ip, dokument);
	}

	@Nonnull
	@GET
	@Path("/{dokumentId}/vorlage")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getDokumentAccessTokenVorlage(
		@Nonnull @Valid @PathParam("dokumentId") JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) throws EbeguEntityNotFoundException {

		String ip = getIP(request);

		Validate.notNull(jaxId.getId());
		String id = converter.toEntityId(jaxId);

		final File dokument = vorlageService.find(id)
			.orElseThrow(() -> new EbeguEntityNotFoundException("getDokumentAccessTokenVorlage", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));


		return getFileDownloadResponse(uriInfo, ip, dokument);
	}

	private Response getFileDownloadResponse(@Context UriInfo uriInfo, String ip, File file) {
		final DownloadFile downloadFile = downloadFileService.create(file, ip);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(DownloadResource.class)
			.path("/" + downloadFile.getId())
			.build();

		JaxTempDokument jaxTempDokument = converter.tempDokumentToJAX(downloadFile);

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
