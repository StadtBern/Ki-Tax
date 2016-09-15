package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEbeguVorlage;
import ch.dvbern.ebegu.api.dtos.JaxEbeguVorlagen;
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
import ch.dvbern.ebegu.util.UploadFileInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;

/**
 * REST Resource fuer Dokumente
 */
@Path("ebeguVorlage")
@Stateless
@Api
public class EbeguVorlageResource {

	private static final String PART_FILE = "file";
	private static final String PART_EBEGU_VORLAGE = "ebeguVorlage";
	private static final String FILENAME_HEADER = "x-filename";


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


	@Nullable
	@GET
	@Path("/gesuchsperiode/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxEbeguVorlagen getEbeguVorlagenByGesuchsperiode(
		@Nonnull @NotNull @PathParam("id") JaxId id) {

		Validate.notNull(id.getId());
		String gesuchsperiodeId = converter.toEntityId(id);
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId);
		if (gesuchsperiode.isPresent()) {

			final Collection<EbeguVorlage> persistedEbeguVorlagen = ebeguVorlageService.getALLEbeguVorlageByGesuchsperiode(gesuchsperiode.get());

			final Set<EbeguVorlage> emptyVorlagen = getEmptyVorlagen(persistedEbeguVorlagen);

			List<EbeguVorlage> allEbeguVorlageList = new ArrayList<EbeguVorlage>();
			allEbeguVorlageList.addAll(persistedEbeguVorlagen);
			allEbeguVorlageList.addAll(emptyVorlagen);
			Collections.sort(allEbeguVorlageList);

			return converter.ebeguVorlagenToJAX(allEbeguVorlageList);
		}

		return converter.ebeguVorlagenToJAX(Collections.emptyList());
	}

	private Set<EbeguVorlage> getEmptyVorlagen(Collection<EbeguVorlage> persistedEbeguVorlagen) {
		Set<EbeguVorlage> emptyEbeguVorlagen = new HashSet<EbeguVorlage>();
		final EbeguVorlageKey[] ebeguVorlageKeys = EbeguVorlageKey.values();
		for (EbeguVorlageKey ebeguVorlageKey : ebeguVorlageKeys) {
			boolean exist = false;
			for (EbeguVorlage ebeguVorlage : persistedEbeguVorlagen) {
				if (ebeguVorlage.getName().equals(ebeguVorlageKey)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				emptyEbeguVorlagen.add(new EbeguVorlage(ebeguVorlageKey));
			}
		}
		return emptyEbeguVorlagen;
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response save(@Context HttpServletRequest request, @Context UriInfo uriInfo, MultipartFormDataInput input)
		throws IOException, ServletException, MimeTypeParseException, SQLException, SerialException, EbeguException {

		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");

		String filename = request.getHeader(FILENAME_HEADER);

		// check if filename available
		if (filename == null || filename.isEmpty()) {
			final String problemString = "filename must be given";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		// Convert JaxEbeguVorlage from inputStream
		JaxEbeguVorlage jaxEbeguVorlage = null;
		try (InputStream dokGrund = input.getFormDataPart(PART_EBEGU_VORLAGE, InputStream.class, null)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			jaxEbeguVorlage = mapper.readValue(IOUtils.toString(dokGrund, "UTF-8"), JaxEbeguVorlage.class);
		} catch (IOException e) {
			final String problemString = "Can't parse EbeguVorlage from Jax to object";
			LOG.error(problemString, e);
			return Response.serverError().entity(problemString).build();
		}

		if (jaxEbeguVorlage == null) {
			final String problemString = "\"Can't parse DokumentGrund from Jax to object";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();

		}

		List<InputPart> inputParts = input.getFormDataMap().get(PART_FILE);
		if (inputParts == null || !inputParts.stream().findAny().isPresent()) {
			return Response.serverError().entity("form-parameter 'file' not found").build();
		}

		UploadFileInfo fileInfo = RestUtil.parseUploadFile(inputParts.stream().findAny().get());

		// evil workaround, (Umlaute werden sonst nicht richtig übertragen!)
		fileInfo.setFilename(filename);


		try (InputStream file = input.getFormDataPart(filename, InputStream.class, null)) {
			fileInfo.setBytes(IOUtils.toByteArray(file));
		}

		// safe File to Filesystem
		fileSaverService.save(fileInfo, "vorlagen");

		if (jaxEbeguVorlage.getVorlage() == null) {
			jaxEbeguVorlage.setVorlage(new JaxVorlage());
		}
		jaxEbeguVorlage.getVorlage().setDokumentName(fileInfo.getFilename());
		jaxEbeguVorlage.getVorlage().setDokumentPfad(fileInfo.getPath());
		jaxEbeguVorlage.getVorlage().setDokumentSize(fileInfo.getSizeString());


		EbeguVorlage ebeguVorlageToMerge = new EbeguVorlage();
		if (jaxEbeguVorlage.getId() != null) {
			final Optional<EbeguVorlage> ebeguVorlageOptional = ebeguVorlageService.getEbeguVorlageByDatesAndKey(jaxEbeguVorlage.getGueltigAb(),
				jaxEbeguVorlage.getGueltigBis(), jaxEbeguVorlage.getName());
			ebeguVorlageToMerge = ebeguVorlageOptional.orElse(new EbeguVorlage());
		}

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


}
