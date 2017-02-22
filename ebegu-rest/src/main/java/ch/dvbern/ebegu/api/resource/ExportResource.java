package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungenExportDTO;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Resource for Exporting data
 */
@Path("export")
@Stateless
@Api("This service provides methods to download verfuegungen in an export format for use by external applications")
public class ExportResource {

	@Inject
	private ExportService exportServiceBean;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Export all existing Verfuegungen of a relevant Antrag",
		response = VerfuegungenExportDTO.class)
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/gesuch/{id}")
	public VerfuegungenExportDTO exportVerfuegungenOfAntrag(
		@Nonnull @NotNull @PathParam("id") JaxId id) throws EbeguException {

		Validate.notNull(id.getId(), "id muss gesetzt sein");
		String antragID = converter.toEntityId(id);
		return this.exportServiceBean.exportAllVerfuegungenOfAntrag(antragID);
	}
}
