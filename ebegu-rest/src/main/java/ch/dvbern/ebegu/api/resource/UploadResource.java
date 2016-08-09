package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDokument;
import ch.dvbern.ebegu.api.dtos.JaxDokumentGrund;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.api.util.UploadFileInfo;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentTyp;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
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
	private DokumentGrundService dokumentGrundService;

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
		throws IOException, ServletException, MimeTypeParseException, SQLException, SerialException {

		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");


		String filenamesJson = request.getHeader(FILENAME_HEADER);
		String[] filenames = null;
		if (!StringUtils.isEmpty(filenamesJson)) {
			filenames = filenamesJson.split(";");
		}

		if (filenames == null || filenames.length == 0) {
			return Response.serverError().entity("filename must be given").build();
		}


		String gesuchId = request.getHeader(GESUCHID_HEADER);
		if (StringUtils.isEmpty(gesuchId)) {
			return Response.serverError().entity("a valid gesuchID must be given").build();
		}

		List<InputPart> inputPartsDG = input.getFormDataMap().get(PART_DOKUMENT_GRUND);
		if (inputPartsDG == null || !inputPartsDG.stream().findAny().isPresent()) {
			return Response.serverError().entity("form-parameter 'inputPartsDG' not found").build();
		}

		JaxDokumentGrund jaxDokumentGrund = null;
		try (InputStream dokGrund = input.getFormDataPart(PART_DOKUMENT_GRUND, InputStream.class, null)) {
			jaxDokumentGrund = new ObjectMapper().readValue(IOUtils.toString(dokGrund, "UTF-8"), JaxDokumentGrund.class);
		} catch (Exception e) {
			LOG.error("Can't parse DokumentGrund from Jax to object", e);
			return Response.serverError().entity("Can't parse DokumentGrund from Jax to object").build();
		}

		if (jaxDokumentGrund == null) {
			LOG.error("Can't parse DokumentGrund from Jax to object");
			return Response.serverError().entity("Can't parse DokumentGrund from Jax to object").build();
		}

		int filecounter = 0;
		List<UploadFileInfo> fileInfos = new ArrayList<UploadFileInfo>();
		String partrileName = PART_FILE + "[" + filecounter + "]";

		List<InputPart> inputParts = input.getFormDataMap().get(partrileName);
		while (inputParts != null && inputParts.stream().findAny().isPresent()) {
			UploadFileInfo fileInfo = RestUtil.parseUploadFile(inputParts.stream().findAny().get());

			// evil workaround, da sonst das file-encoding (wahrscheinlich beim Parsen vom MultipartFormDataInput kaputt geht :(
			if (filenames[filecounter] != null) {
				fileInfo.setFilename(filenames[filecounter]);
			}

			byte[] bytes;
			try (InputStream file = input.getFormDataPart(partrileName, InputStream.class, null)) {
				fileInfo.setBytes(IOUtils.toByteArray(file));
			}

			fileInfos.add(fileInfo);


			String path = fileSaverService.save(fileInfo.getBytes(), fileInfo.getFilename(), gesuchId);


			addFileToDokumentGrund(jaxDokumentGrund, fileInfo.getFilename(), path);


			filecounter++;
			partrileName = PART_FILE + "[" + filecounter + "]";
			inputParts = input.getFormDataMap().get(partrileName);
		}


		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId);
		if (!gesuch.isPresent()) {
			LOG.error("Can't find Gesuch on DB");
			return Response.serverError().entity("Can't find Gesuch on DB").build();
		}

		DokumentGrund dokumentGrundToMerge = new DokumentGrund();
		if (jaxDokumentGrund.getId() != null) {
			Optional<DokumentGrund> optional = dokumentGrundService.findDokumentGrund(jaxDokumentGrund.getId());
			dokumentGrundToMerge = optional.orElse(new DokumentGrund());
		}
		DokumentGrund convertedDokumentGrund = converter.dokumentGrundToEntity(jaxDokumentGrund, dokumentGrundToMerge);
		convertedDokumentGrund.setGesuch(gesuch.get());

		DokumentGrund persistedDokumentGrund = dokumentGrundService.saveDokumentGrund(convertedDokumentGrund);


		final JaxDokumentGrund jaxDokumentGrundToReturn = converter.dokumentGrundToJax(persistedDokumentGrund);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EinkommensverschlechterungInfoResource.class)
			.path("/" + persistedDokumentGrund.getId())
			.build();

		return Response.created(uri).entity(jaxDokumentGrundToReturn).build();
	}

	private void addFileToDokumentGrund(JaxDokumentGrund jaxDokumentGrund, String filename, String path) {
		Validate.notNull(jaxDokumentGrund.getDokumente());
		DokumentTyp dokumentTyp = null;
		for (JaxDokument jaxDokument : jaxDokumentGrund.getDokumente()) {
			dokumentTyp = jaxDokument.getDokumentTyp();
			if (null == jaxDokument.getDokumentName() || jaxDokument.getDokumentName().isEmpty()) {
				//set to existing
				jaxDokument.setDokumentName(filename);
				jaxDokument.setDokumentPfad(path);
				return;
			}
		}
		//add new
		JaxDokument dokument = new JaxDokument();
		dokument.setDokumentTyp(dokumentTyp);
		dokument.setDokumentName(filename);
		dokument.setDokumentPfad(path);
		jaxDokumentGrund.getDokumente().add(dokument);
	}
}
