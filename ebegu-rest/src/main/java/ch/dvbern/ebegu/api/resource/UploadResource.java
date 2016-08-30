package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDokument;
import ch.dvbern.ebegu.api.dtos.JaxDokumentGrund;
import ch.dvbern.ebegu.api.dtos.JaxDokumente;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.util.UploadFileInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * REST Resource fuer Institution
 */
@Path("upload")
@Stateless
@Api
public class UploadResource {

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GesuchService gesuchService;
	@Inject
	private DokumenteResource dokumenteResource;
	@Inject
	private DokumentGrundService dokumentGrundService;
	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private JaxBConverter converter;

	private static final String PART_FILE = "file";
	private static final String PART_DOKUMENT_GRUND = "dokumentGrund";

	private static final String FILENAME_HEADER = "x-filename";
	private static final String GESUCHID_HEADER = "x-gesuchID";

	private static final Logger LOG = LoggerFactory.getLogger(UploadResource.class);


	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response save(@Context HttpServletRequest request, @Context UriInfo uriInfo, MultipartFormDataInput input)
		throws IOException, ServletException, MimeTypeParseException, SQLException, SerialException, EbeguException {

		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");

		// get filenames from Header
		String filenamesJson = request.getHeader(FILENAME_HEADER);
		String[] filenames = null;
		if (!StringUtils.isEmpty(filenamesJson)) {
			filenames = filenamesJson.split(";");
		}

		// check if filenames available
		if (filenames == null || filenames.length == 0) {
			final String problemString = "filename must be given";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		// Get GesuchId from header
		String gesuchId = request.getHeader(GESUCHID_HEADER);
		if (StringUtils.isEmpty(gesuchId)) {
			final String problemString = "a valid gesuchID must be given";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		// Get DokumentGrund Object from form-paramter
		List<InputPart> inputPartsDG = input.getFormDataMap().get(PART_DOKUMENT_GRUND);
		if (inputPartsDG == null || !inputPartsDG.stream().findAny().isPresent()) {
			final String problemString = "form-parameter 'inputPartsDG' not found";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		// Convert DokumentGrund from inputStream
		JaxDokumentGrund jaxDokumentGrund = null;
		try (InputStream dokGrund = input.getFormDataPart(PART_DOKUMENT_GRUND, InputStream.class, null)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			jaxDokumentGrund = mapper.readValue(IOUtils.toString(dokGrund, "UTF-8"), JaxDokumentGrund.class);
		} catch (IOException e) {
			final String problemString = "Can't parse DokumentGrund from Jax to object";
			LOG.error(problemString, e);
			return Response.serverError().entity(problemString).build();
		}

		if (jaxDokumentGrund == null) {
			final String problemString = "\"Can't parse DokumentGrund from Jax to object";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();

		}

		extractFilesFromInput(input, filenames, gesuchId, jaxDokumentGrund);

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId);
		if (!gesuch.isPresent()) {
			final String problemString = "Can't find Gesuch on DB";
			LOG.error(problemString);
			return Response.serverError().entity(problemString).build();
		}

		DokumentGrund dokumentGrundToMerge = new DokumentGrund();
		if (jaxDokumentGrund.getId() != null) {
			Optional<DokumentGrund> optional = dokumentGrundService.findDokumentGrund(jaxDokumentGrund.getId());
			dokumentGrundToMerge = optional.orElse(new DokumentGrund());
		}
		DokumentGrund convertedDokumentGrund = converter.dokumentGrundToEntity(jaxDokumentGrund, dokumentGrundToMerge);
		convertedDokumentGrund.setGesuch(gesuch.get());

		// save modified Dokument to DB
		DokumentGrund persistedDokumentGrund = dokumentGrundService.saveDokumentGrund(convertedDokumentGrund);

		final JaxDokumentGrund jaxDokumentGrundToReturn = converter.dokumentGrundToJax(persistedDokumentGrund);

		final JaxDokumente dokumente = dokumenteResource.getDokumente(new JaxId(gesuchId));
		boolean allNeededDokumenteUploaded = true;
		for (JaxDokumentGrund dokumentGrund : dokumente.getDokumentGruende()) {
			if (dokumentGrund.isNeeded() && dokumentGrund.isEmpty()) {
				allNeededDokumenteUploaded = false;
				break;
			}
		}
		if (allNeededDokumenteUploaded) { //only set status to OK if all required documents have been uploaded
			wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.DOKUMENTE);
		}

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EinkommensverschlechterungInfoResource.class)
			.path("/" + persistedDokumentGrund.getId())
			.build();

		return Response.created(uri).entity(jaxDokumentGrundToReturn).build();
	}

	private void extractFilesFromInput(MultipartFormDataInput input, String[] filenames, String gesuchId, JaxDokumentGrund jaxDokumentGrund) throws MimeTypeParseException, IOException {
		int filecounter = 0;
		String partrileName = PART_FILE + "[" + filecounter + "]";

		// do for every file:
		List<InputPart> inputParts = input.getFormDataMap().get(partrileName);
		while (inputParts != null && inputParts.stream().findAny().isPresent()) {
			UploadFileInfo fileInfo = RestUtil.parseUploadFile(inputParts.stream().findAny().get());

			// evil workaround, (Umlaute werden sonst nicht richtig übertragen!)
			if (filenames[filecounter] != null) {
				fileInfo.setFilename(filenames[filecounter]);
			}

			try (InputStream file = input.getFormDataPart(partrileName, InputStream.class, null)) {
				fileInfo.setBytes(IOUtils.toByteArray(file));
			}

			// safe File to Filesystem
			fileSaverService.save(fileInfo, gesuchId);

			// add the new file to DokumentGrund object
			addFileToDokumentGrund(jaxDokumentGrund, fileInfo);

			filecounter++;
			partrileName = PART_FILE + "[" + filecounter + "]";
			inputParts = input.getFormDataMap().get(partrileName);
		}
	}

	private void addFileToDokumentGrund(JaxDokumentGrund jaxDokumentGrund, UploadFileInfo uploadFileInfo) {
		Validate.notNull(jaxDokumentGrund.getDokumente());

		for (JaxDokument jaxDokument : jaxDokumentGrund.getDokumente()) {

			if (null == jaxDokument.getDokumentName() ||
				jaxDokument.getDokumentName().isEmpty() ) {

				//set to existing
				jaxDokument.setDokumentName(uploadFileInfo.getFilename());
				jaxDokument.setDokumentPfad(uploadFileInfo.getPath());
				jaxDokument.setDokumentSize(uploadFileInfo.getSizeString());
				LOG.info("Replace placeholder on "+jaxDokumentGrund.getDokumentTyp()+" by file "+ uploadFileInfo.getFilename());
				return;
			}
		}

		//add new
		JaxDokument dokument = new JaxDokument();
		dokument.setDokumentName(uploadFileInfo.getFilename());
		dokument.setDokumentPfad(uploadFileInfo.getPath());
		dokument.setDokumentSize(uploadFileInfo.getSizeString());
		jaxDokumentGrund.getDokumente().add(dokument);
		LOG.info("Add on "+jaxDokumentGrund.getDokumentTyp()+" file "+ uploadFileInfo.getFilename());
	}
}
