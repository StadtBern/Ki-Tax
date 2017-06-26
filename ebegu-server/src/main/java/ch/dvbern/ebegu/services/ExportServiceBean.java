package ch.dvbern.ebegu.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import ch.dvbern.ebegu.dto.dataexport.v1.ExportConverter;
import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungenExportDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.util.UploadFileInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.JURIST;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;


@Stateless
@Local(ExportService.class)
public class ExportServiceBean implements ExportService {


	@Inject
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private FileSaverService fileSaverService;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public VerfuegungenExportDTO exportAllVerfuegungenOfAntrag(@Nonnull String antragId) {
		Objects.requireNonNull(antragId, "gesuchId muss gesetzt sein");
		Gesuch gesuch = gesuchService.findGesuch(antragId)
			.orElseThrow(() -> new EbeguEntityNotFoundException("exportVerfuegungOfAntrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragId));

		authorizer.checkReadAuthorization(gesuch);
		ExportConverter expConverter = new ExportConverter();

		List<Verfuegung> verfToExport = gesuch.extractAllBetreuungen().stream()
			.filter(betreuung -> betreuung.getVerfuegung() != null)
			.map(Betreuung::getVerfuegung)
			.collect(Collectors.toList());
		return expConverter.createVerfuegungenExportDTO(verfToExport);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public VerfuegungenExportDTO exportVerfuegungOfBetreuung(String betreuungID) {
		Betreuung betreuung = readBetreuung(betreuungID);
		return convertBetreuungToExport(betreuung);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT, JURIST, REVISOR})
	public UploadFileInfo exportVerfuegungOfBetreuungAsFile(String betreuungID) {
		Betreuung betreuung = readBetreuung(betreuungID);
		VerfuegungenExportDTO verfuegungenExportDTO = convertBetreuungToExport(betreuung);
		String json = convertToJson(verfuegungenExportDTO);
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		String filename = "export_" + betreuung.getBGNummer() + ".json";
		return this.fileSaverService.save(bytes, filename, betreuung.extractGesuch().getId(), getContentTypeForExport());
	}


	private Betreuung readBetreuung(String betreuungID) {
		Objects.requireNonNull(betreuungID, "betreuungID muss gesetzt sein");
		Betreuung betreuung = betreuungService.findBetreuung(betreuungID)
			.orElseThrow(() -> new EbeguEntityNotFoundException("exportVerfuegungOfBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungID));
		authorizer.checkReadAuthorization(betreuung);
		return betreuung;
	}

	private VerfuegungenExportDTO convertBetreuungToExport(Betreuung betreuung) {
		ExportConverter expConverter = new ExportConverter();
		List<Verfuegung> verfuegungToExport = new ArrayList<>();
		if (betreuung.getVerfuegung() != null) {
			//single element in list to export
			verfuegungToExport.add(betreuung.getVerfuegung());
		}
		return expConverter.createVerfuegungenExportDTO(verfuegungToExport);
	}

	@Nonnull
	private MimeType getContentTypeForExport() {
		try {
			return new MimeType(MediaType.TEXT_PLAIN);
		} catch (MimeTypeParseException e) {
			throw new EbeguRuntimeException("getContentTypeForExport", "could not parse mime type", e, MediaType.TEXT_PLAIN);
		}
	}

	/**
	 * convert the dto as json
	 */
	private String convertToJson(VerfuegungenExportDTO verfuegungenExportDTO) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		try {
			return mapper.writeValueAsString(verfuegungenExportDTO);
		} catch (JsonProcessingException e) {
			throw new EbeguRuntimeException("convertToJson", "Objekt kann nicht JSON konvertiert werden", e, "Objekt kann nicht JSON konvertiert werden");
		}
	}
}
