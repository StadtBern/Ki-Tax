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

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.EbeguSchemaOutputResolver;
import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungenExportDTO;
import ch.dvbern.ebegu.services.ExportService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * Resource for Exporting data
 */
@Path("export")
@Stateless
@Api(description = "This service provides methods to download verfuegungen in an export format for use by external applications")
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
		@Nonnull @NotNull @PathParam("id") JaxId id) {

		Validate.notNull(id.getId(), "id muss gesetzt sein");
		String antragID = converter.toEntityId(id);
		return this.exportServiceBean.exportAllVerfuegungenOfAntrag(antragID);
	}

	@ApiOperation("Exports a json Schema of the ExportDTOs")
	@Path("/meta/jsonschema")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJsonSchemaString() throws JsonMappingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		// configure mapper, if necessary, then create schema generator
		JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
		JsonSchema schema = schemaGen.generateSchema(VerfuegungenExportDTO.class);
		return Response.ok(schema).build();

	}

	@ApiOperation(value = "Exports an xsd of the ExportDTOs", response = String.class)
	@Path("/meta/xsd")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getXmlSchemaString() throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(VerfuegungenExportDTO.class);
		EbeguSchemaOutputResolver sor = new EbeguSchemaOutputResolver();
		jaxbContext.generateSchema(sor);
		String schema = sor.getSchema();
		return Response.ok(schema).build();
	}
}
