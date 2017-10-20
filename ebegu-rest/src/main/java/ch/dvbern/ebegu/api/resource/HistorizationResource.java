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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.api.dtos.JaxEnversRevision;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.services.HistorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;

/**
 * Resource fuer Historization
 */
@Path("historization")
@Stateless
@Api(description = "Resource für (technische) Historisierung")
public class HistorizationResource {

	@Inject
	private JaxBConverter converter;

	@Inject
	private HistorizationService historizationService;

	@ApiOperation(value = "Sucht alle Entities, die zur uebergebenen Envers-Revision gehoeren",
		responseContainer = "List", response = JaxAbstractDTO.class)
	@Nonnull
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entity/{entityName}/rev/{revision}")
	public Response getAllByRevision(
		@Nonnull @PathParam("entityName") String entityName,
		@Nonnull @Min(1) @PathParam("revision") Integer revision,
		@Context HttpServletResponse response) {

		if (entityName.equals(ApplicationProperty.class.getSimpleName())) {
			List<AbstractEntity> entityList = historizationService.getAllEntitiesByRevision(entityName, revision);
			List<JaxAbstractDTO> resultList = new ArrayList<>();
			if (entityList != null) {
				resultList = entityList.stream().filter(entity -> entity instanceof ApplicationProperty)
					.map(entity -> converter.applicationPropertyToJAX((ApplicationProperty) entity)).collect(Collectors.toList());
			}
			return Response.ok(resultList).build();
		}
		throw new NotImplementedException("Diese Methode ist erst fuer ApplicationProperties umgesetzt!");
	}

	@ApiOperation(value = "Sucht alle Entities, die zur uebergebenen Envers-Revision gehoeren",
		responseContainer = "List", response = JaxEnversRevision.class)
	@Nonnull
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entity/{entityName}/id/{id}")
	public Response getHistoryById(
		@Nonnull @PathParam("entityName") String entityName,
		@Nonnull @PathParam("id") String entityId,
		@Context HttpServletResponse response) {

		List<Object[]> entityList = historizationService.getAllRevisionsById(entityName, entityId);
		List<JaxEnversRevision> resultList = new ArrayList<>();
		if (entityList != null) {
			// the result will be a list of three element arrays. The first element will be the changed entity
			// instance. The second will be an entity containing revision data
			// (if no custom entity is used, this will be an instance of DefaultRevisionEntity).
			// The third will be the type of the revision (one of the values of the RevisionType enumeration: ADD, MOD, DEL).
			resultList = entityList.stream().map(entity -> converter.enversRevisionToJAX((DefaultRevisionEntity) entity[1],
				(AbstractEntity) entity[0], (RevisionType) entity[2])).collect(Collectors.toList());
		}
		return Response.ok(resultList).build();
	}
}
