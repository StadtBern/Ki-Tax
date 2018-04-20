/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nullable;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.tika.Tika;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDokument;
import ch.dvbern.ebegu.api.dtos.JaxDokumentGrund;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.ApplicationPropertyService;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.util.UploadFileInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST Resource zum Upload von Dokumenten
 */
@SuppressWarnings("OverlyBroadCatchBlock")
@Path("upload")
@Stateless
@Api(description = "Resource zum Upload von Dokumenten")
public class UploadResource {

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GesuchService gesuchService;
	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	private static final String PART_FILE = "file";
	private static final String PART_DOKUMENT_GRUND = "dokumentGrund";

	private static final String FILENAME_HEADER = "x-filename";
	private static final String GESUCHID_HEADER = "x-gesuchID";

	private static final Logger LOG = LoggerFactory.getLogger(UploadResource.class);


	@ApiOperation(value = "Speichert ein Dokument in der Datenbank", response = JaxDokumentGrund.class)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response save(@Context HttpServletRequest request, @Context UriInfo uriInfo, MultipartFormDataInput input)
		throws IOException, ServletException, MimeTypeParseException, SQLException, SerialException, EbeguException {

		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");

		String[] encodedFilenames = getFilenamesFromHeader(request);

		// check if filenames available
		if (encodedFilenames == null || encodedFilenames.length == 0) {
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
		JaxDokumentGrund jaxDokumentGrund;
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

		extractFilesFromInput(input, encodedFilenames, gesuchId, jaxDokumentGrund);

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

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EinkommensverschlechterungInfoResource.class)
			.path('/' + persistedDokumentGrund.getId())
			.build();

		return Response.created(uri).entity(jaxDokumentGrundToReturn).build();
	}

	@Nullable
	private String[] getFilenamesFromHeader(@Context HttpServletRequest request) {
		String filenamesJson = request.getHeader(FILENAME_HEADER);
		String[] filenames = null;
		if (!StringUtils.isEmpty(filenamesJson)) {
			filenames = filenamesJson.split(";");
		}
		return filenames;
	}

	private void extractFilesFromInput(MultipartFormDataInput input, String[] encodedFilenames, String gesuchId, JaxDokumentGrund jaxDokumentGrund) throws MimeTypeParseException, IOException {
		int filecounter = 0;
		String partrileName = PART_FILE + '[' + filecounter + ']';

		// do for every file:
		List<InputPart> inputParts = input.getFormDataMap().get(partrileName);
		while (inputParts != null && inputParts.stream().findAny().isPresent()) {
			UploadFileInfo fileInfo = RestUtil.parseUploadFile(inputParts.stream().findAny().get());

			// evil workaround, (Umlaute werden sonst nicht richtig übertragen!)
			if (encodedFilenames[filecounter] != null) {
				String decodedFilenamesJson = new String(Base64.getDecoder().decode(encodedFilenames[filecounter]), Charset.forName("UTF-8"));
				fileInfo.setFilename(decodedFilenamesJson);
			}

			try (InputStream fileInputStream = input.getFormDataPart(partrileName, InputStream.class, null)) {
				fileInfo.setBytes(IOUtils.toByteArray(fileInputStream));
			}

			// safe File to Filesystem, if we just analyze the input stream tika classifies all files as octet streams
			fileSaverService.save(fileInfo, gesuchId);
			checkFiletypeAllowed(fileInfo);

			// add the new file to DokumentGrund object
			addFileToDokumentGrund(jaxDokumentGrund, fileInfo);

			filecounter++;
			partrileName = PART_FILE + '[' + filecounter + ']';
			inputParts = input.getFormDataMap().get(partrileName);
		}
	}

	private void checkFiletypeAllowed(UploadFileInfo fileInfo) {
		//we dont purly trust the filetype set in the header, so we perform our own content-type guessing
		java.nio.file.Path filePath = Paths.get(fileInfo.getPath());
		try {
			Tika tika = new Tika();
			String contentType = tika.detect(filePath); //tika should be more accurate than Files.probeContentType
			if (contentType == null || !contentType.equals(fileInfo.getContentType().toString())) {
				LOG.warn("Content type from Header did not match content type returned from probing. "
					+ "\n\t header:   {} \n\t probing:  {}", fileInfo.getContentType(), contentType);
			}
			if(!applicationPropertyService.readMimeTypeWhitelist().contains(contentType)){
				fileSaverService.remove(fileInfo.getPath());
				LOG.debug("Blocked upload of filetype that is not in whitelist: {}", contentType);
				throw new EbeguRuntimeException("checkFiletypeAllowed", ErrorCodeEnum.ERROR_UPLOAD_INVALID_FILETYPE, contentType);
			}

		} catch (IOException e) {
			LOG.warn("Could not probe file for its content-type, check was omitted", e);
		}
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	private void addFileToDokumentGrund(JaxDokumentGrund jaxDokumentGrund, UploadFileInfo uploadFileInfo) {
		Validate.notNull(jaxDokumentGrund.getDokumente());

		for (JaxDokument jaxDokument : jaxDokumentGrund.getDokumente()) {

			if (null == jaxDokument.getFilename() ||
				jaxDokument.getFilename().isEmpty()) {

				//set to existing
				jaxDokument.setFilename(uploadFileInfo.getFilename());
				jaxDokument.setFilepfad(uploadFileInfo.getPath());
				jaxDokument.setFilesize(uploadFileInfo.getSizeString());
				LOG.info("Replace placeholder on {} by file {}", jaxDokumentGrund.getDokumentTyp(), uploadFileInfo.getFilename());
				return;
			}
		}

		//add new
		JaxDokument dokument = new JaxDokument();
		dokument.setFilename(uploadFileInfo.getFilename());
		dokument.setFilepfad(uploadFileInfo.getPath());
		dokument.setFilesize(uploadFileInfo.getSizeString());
		jaxDokumentGrund.getDokumente().add(dokument);
		LOG.info("Add on {} file {}", jaxDokumentGrund.getDokumentTyp(), uploadFileInfo.getFilename());
	}

}
