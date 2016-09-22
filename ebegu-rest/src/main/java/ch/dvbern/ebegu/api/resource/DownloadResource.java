package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDownloadFile;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
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
import java.util.Optional;

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

	@Inject
	private GesuchService gesuchService;

	@Inject
	private PrintVerfuegungPDFService verfuegungsGenerierungPDFService;

	@Inject
	private PrintFinanzielleSituationPDFService printFinanzielleSituationPDFService;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GeneratedDokumentService generatedDokumentService;

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private PrintBegleitschreibenPDFService printBegleitschreibenPDFService;

	@Inject
	private Persistence<Gesuch> persistence;


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

		final File dokument = vorlageService.findVorlage(id)
			.orElseThrow(() -> new EbeguEntityNotFoundException("getDokumentAccessTokenVorlage", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));


		return getFileDownloadResponse(uriInfo, ip, dokument);
	}

	@Nonnull
	@GET
	@Path("/{gesuchid}/{dokumentTyp}/generated")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getDokumentAccessTokenGeneratedDokument(
		@Nonnull @Valid @PathParam("gesuchid") JaxId jaxGesuchId,
		@Nonnull @Valid @PathParam("dokumentTyp") GeneratedDokumentTyp dokumentTyp,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) throws EbeguEntityNotFoundException, MergeDocException {

		Validate.notNull(jaxGesuchId.getId());
		Validate.notNull(dokumentTyp);
		String ip = getIP(request);

		final Optional<Gesuch> gesuch = gesuchService.findGesuch(converter.toEntityId(jaxGesuchId));
		if (gesuch.isPresent()) {
			finanzielleSituationService.calculateFinanzDaten(gesuch.get());

			byte[] data;
			if (GeneratedDokumentTyp.FINANZIELLE_SITUATION.equals(dokumentTyp)) {
				data = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch.get());
			}
			else if (GeneratedDokumentTyp.BEGLEITSCHREIBEN.equals(dokumentTyp)) {
				data = printBegleitschreibenPDFService.printBegleitschreiben(gesuch.get());
			}
			else {
				return null;
			}

			final UploadFileInfo savedDokument = fileSaverService.save(data, "name.pdf", jaxGesuchId.getId());

			final GeneratedDokument persistedDokument = persistGeneratedDokument(dokumentTyp, gesuch.get(), savedDokument);

			persistence.getEntityManager().detach(gesuch.get());

			return getFileDownloadResponse(uriInfo, ip, persistedDokument);
		}
		throw new EbeguEntityNotFoundException("getDokumentAccessTokenGeneratedDokument",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + jaxGesuchId.getId());
	}

	@Nonnull
	@GET
	@Path("/{gesuchid}/{betreuungId}/generatedVerfuegung")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getVerfuegungDokumentAccessTokenGeneratedDokument(
		@Nonnull @Valid @PathParam("gesuchid") JaxId jaxGesuchId,
		@Nonnull @Valid @PathParam("betreuungId") JaxId jaxBetreuungId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) throws EbeguEntityNotFoundException, MergeDocException, DocTemplateException, IOException {

		Validate.notNull(jaxGesuchId.getId());
		Validate.notNull(jaxBetreuungId.getId());
		String ip = getIP(request);

		final Optional<Gesuch> gesuch = gesuchService.findGesuch(converter.toEntityId(jaxGesuchId));
		if (gesuch.isPresent()) {
			finanzielleSituationService.calculateFinanzDaten(gesuch.get());
			final Gesuch gesuchWithVerfuegungen = verfuegungService.calculateVerfuegung(gesuch.get());

			final Betreuung betreuung = getBetreuungFromGesuch(gesuchWithVerfuegungen, jaxBetreuungId.getId());
			if (betreuung != null) {
				final byte[] verfuegungsPDF = verfuegungsGenerierungPDFService.printVerfuegungForBetreuung(betreuung);

				final UploadFileInfo savedDokument = fileSaverService.save(verfuegungsPDF, "name.pdf", jaxGesuchId.getId());

				final GeneratedDokument persistedDokument = persistGeneratedDokument(GeneratedDokumentTyp.VERFUEGUNG_KITA, gesuch.get(), savedDokument);

				persistence.getEntityManager().detach(gesuch.get());

				return getFileDownloadResponse(uriInfo, ip, persistedDokument);
			}
			throw new EbeguEntityNotFoundException("getVerfuegungDokumentAccessTokenGeneratedDokument",
				ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "Betreuung not found: " + jaxBetreuungId.getId());
		}
		throw new EbeguEntityNotFoundException("getVerfuegungDokumentAccessTokenGeneratedDokument",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId not found: " + jaxGesuchId.getId());
	}

	private Betreuung getBetreuungFromGesuch(Gesuch gesuch, String betreuungId) {
		for (KindContainer kind : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kind.getBetreuungen()) {
				if (betreuung.getId().equals(betreuungId)) {
					return betreuung;
				}
			}
		}
		return null;
	}


	private Response getFileDownloadResponse(@Context UriInfo uriInfo, String ip, File file) {
		final DownloadFile downloadFile = downloadFileService.create(file, ip);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(DownloadResource.class)
			.path("/" + downloadFile.getId())
			.build();

		JaxDownloadFile jaxDownloadFile = converter.downloadFileToJAX(downloadFile);

		return Response.created(uri).entity(jaxDownloadFile).build();
	}

	private String getIP(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}

	@Nonnull
	private GeneratedDokument persistGeneratedDokument(@Nonnull GeneratedDokumentTyp dokumentTyp, Gesuch gesuch, UploadFileInfo savedDokument) {
		GeneratedDokument generatedDokument = new GeneratedDokument();
		generatedDokument.setFilename(savedDokument.getFilename());
		generatedDokument.setFilepfad(savedDokument.getPath());
		generatedDokument.setFilesize(savedDokument.getSizeString());
		generatedDokument.setTyp(dokumentTyp);
		generatedDokument.setGesuch(gesuch);
		return generatedDokumentService.saveGeneratedDokument(generatedDokument);
	}
}
