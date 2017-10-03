package ch.dvbern.ebegu.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEbeguVorlage;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxVorlage;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EbeguVorlageService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.UploadFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Resource fuer Dokument-Vorlagen
 */
@Path("ebeguVorlage")
@Stateless
@Api(description = "Resource fuer Dokument-Vorlagen")
public class EbeguVorlageResource {

	private static final String PART_FILE = "file";
	private static final String FILENAME_HEADER = "x-filename";
	private static final String VORLAGE_KEY_HEADER = "x-vorlagekey";
	private static final String GESUCHSPERIODE_HEADER = "x-gesuchsperiode";
	private static final String PROGESUCHSPERIODE_HEADER = "x-progesuchsperiode";

	private static final Logger LOG = LoggerFactory.getLogger(EbeguVorlageResource.class);

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Inject
	private EbeguVorlageService ebeguVorlageService;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@ApiOperation(value = "Gibt alle Vorlagen fuer die Gesuchsperiode mit der uebergebenen Id zurueck",
		responseContainer = "List", response = JaxEbeguVorlage.class)
	@Nullable
	@GET
	@Path("/gesuchsperiode/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxEbeguVorlage> getEbeguVorlagenByGesuchsperiode(
		@Nonnull @NotNull @PathParam("id") JaxId id) {

		Validate.notNull(id.getId());
		String gesuchsperiodeId = converter.toEntityId(id);
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId);
		if (gesuchsperiode.isPresent()) {

			List<EbeguVorlage> persistedEbeguVorlagen = ebeguVorlageService.getALLEbeguVorlageByGesuchsperiode(gesuchsperiode.get());

			if (persistedEbeguVorlagen.isEmpty()) {
				ebeguVorlageService.copyEbeguVorlageListToNewGesuchsperiode(gesuchsperiode.get());
				persistedEbeguVorlagen = ebeguVorlageService.getALLEbeguVorlageByGesuchsperiode(gesuchsperiode.get());
			}

			Collections.sort(persistedEbeguVorlagen);

			return persistedEbeguVorlagen.stream()
				.map(ebeguVorlage -> converter.ebeguVorlageToJax(ebeguVorlage))
				.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	@ApiOperation(value = "Gibt alle Vorlagen zurueck, welche nicht zu einer Gesuchsperiode gehoeren.",
		responseContainer = "List", response = JaxEbeguVorlage.class)
	@Nullable
	@GET
	@Path("/nogesuchsperiode/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxEbeguVorlage> getEbeguVorlagenWithoutGesuchsperiode() {

		List<EbeguVorlage> persistedEbeguVorlagen = new ArrayList<>(ebeguVorlageService
			.getALLEbeguVorlageByDate(LocalDate.now(), false));

		Collections.sort(persistedEbeguVorlagen);

		return persistedEbeguVorlagen.stream()
			.map(ebeguVorlage -> converter.ebeguVorlageToJax(ebeguVorlage))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Speichert eine Vorlage in der Datenbank")
	@POST
	@SuppressWarnings("PMD.NcssMethodCount")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response save(@Context HttpServletRequest request, @Context UriInfo uriInfo, MultipartFormDataInput input)
		throws IOException, ServletException, MimeTypeParseException, SQLException, EbeguException {

		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");

		String filename = request.getHeader(FILENAME_HEADER);

		// check if filename available
		if (filename == null || filename.isEmpty()) {
			final String problemString = "filename must be given";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		EbeguVorlageKey ebeguVorlageKey;
		try {
			ebeguVorlageKey = EbeguVorlageKey.valueOf(request.getHeader(VORLAGE_KEY_HEADER));
		} catch (IllegalArgumentException e) {
			final String problemString = "ebeguVorlageKey must be given";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		String gesuchsperiodeId = request.getHeader(GESUCHSPERIODE_HEADER);
		Boolean proGesuchsperiode = Boolean.valueOf(request.getHeader(PROGESUCHSPERIODE_HEADER));

		// check if it must be linked to a Gesuchsperiode. If not the dateRange will be set by default
		LocalDate gueltigAb = Constants.START_OF_TIME;
		LocalDate gueltigBis = Constants.END_OF_TIME;
		if (proGesuchsperiode) {
			if (gesuchsperiodeId == null || gesuchsperiodeId.isEmpty()) {
				final String problemString = "gesuchsperiodeId must be given";
				LOG.error(problemString);
				return Response.serverError().entity(problemString).build();
			}
			Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId);
			if (!gesuchsperiode.isPresent()) {
				final String problemString = "gesuchsperiode not found on server";
				LOG.error(problemString);
				return Response.serverError().entity(problemString).build();
			}
			gueltigAb = gesuchsperiode.get().getGueltigkeit().getGueltigAb();
			gueltigBis = gesuchsperiode.get().getGueltigkeit().getGueltigBis();
		}

		List<InputPart> inputParts = input.getFormDataMap().get(PART_FILE);
		if (inputParts == null || !inputParts.stream().findAny().isPresent()) {
			return Response.serverError().entity("form-parameter 'file' not found").build();
		}

		UploadFileInfo fileInfo = RestUtil.parseUploadFile(inputParts.stream().findAny().get());

		// evil workaround, (Umlaute werden sonst nicht richtig übertragen!)
		String decodedFilenames = new String(Base64.getDecoder().decode(filename), Charset.forName("UTF-8"));
		fileInfo.setFilename(decodedFilenames);

		try (InputStream file = input.getFormDataPart(PART_FILE, InputStream.class, null)) {
			fileInfo.setBytes(IOUtils.toByteArray(file));
		}

		// safe File to Filesystem
		fileSaverService.save(fileInfo, "vorlagen");

		JaxEbeguVorlage jaxEbeguVorlage = new JaxEbeguVorlage();
		jaxEbeguVorlage.setName(ebeguVorlageKey);
		jaxEbeguVorlage.setGueltigAb(gueltigAb);
		jaxEbeguVorlage.setGueltigBis(gueltigBis);
		jaxEbeguVorlage.setProGesuchsperiode(proGesuchsperiode);
		jaxEbeguVorlage.setVorlage(new JaxVorlage());
		jaxEbeguVorlage.getVorlage().setFilename(fileInfo.getFilename());
		jaxEbeguVorlage.getVorlage().setFilepfad(fileInfo.getPath());
		jaxEbeguVorlage.getVorlage().setFilesize(fileInfo.getSizeString());

		final Optional<EbeguVorlage> ebeguVorlageOptional = ebeguVorlageService.getEbeguVorlageByDatesAndKey(jaxEbeguVorlage.getGueltigAb(),
			jaxEbeguVorlage.getGueltigBis(), jaxEbeguVorlage.getName());
		EbeguVorlage ebeguVorlageToMerge = ebeguVorlageOptional.orElse(new EbeguVorlage());

		EbeguVorlage ebeguVorlageConverted = converter.ebeguVorlageToEntity(jaxEbeguVorlage, ebeguVorlageToMerge);

		// save modified EbeguVorlage to DB
		EbeguVorlage persistedEbeguVorlage = ebeguVorlageService.updateEbeguVorlage(ebeguVorlageConverted);

		final JaxEbeguVorlage jaxEbeguVorlageToReturn = converter.ebeguVorlageToJax(persistedEbeguVorlage);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EbeguVorlageResource.class)
			.path("/" + persistedEbeguVorlage.getId())
			.build();

		return Response.created(uri).entity(jaxEbeguVorlageToReturn).build();
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Loescht die Vorlage mit der uebergebenen Id aus der Datenbank.", response = Void.class)
	@Nullable
	@DELETE
	@Path("/{ebeguVorlageId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeEbeguVorlage(
		@Nonnull @NotNull @PathParam("ebeguVorlageId") JaxId ebeguVorlageId,
		@Context HttpServletResponse response) {

		Validate.notNull(ebeguVorlageId.getId());
		ebeguVorlageService.removeVorlage(converter.toEntityId(ebeguVorlageId));
		return Response.ok().build();
	}
}
