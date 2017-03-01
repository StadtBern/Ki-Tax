package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.EbeguSchemaOutputResolver;
import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungenExportDTO;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.ExportService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;

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


	@ApiOperation(value = "Exports a json Schema of the ExportDTOs")
	@Path("/meta/jsonschema")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getJsonSchemaString() throws JsonMappingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		// configure mapper, if necessary, then create schema generator
		JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
		JsonSchema schema = schemaGen.generateSchema(VerfuegungenExportDTO.class);
		return Response.ok(schema).build();

	}

	@ApiOperation(value = "Exports an xsd of the ExportDTOs")
	@Path("/meta/xsd")
	@GET
	@Produces({MediaType.APPLICATION_XML})
	public String getXmlSchemaString() throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(VerfuegungenExportDTO.class);
		EbeguSchemaOutputResolver sor = new EbeguSchemaOutputResolver();
		jaxbContext.generateSchema(sor);
		String schema = sor.getSchema();
		return schema;
	}

}
