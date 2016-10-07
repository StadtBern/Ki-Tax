package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.api.dtos.JaxEnversRevision;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.services.HistorizationService;
import io.swagger.annotations.Api;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by imanol on 04.03.16.
 * Resource fuer Historization
 */
@Path("historization")
@Stateless
@Api
public class HistorizationResource {

	@Inject
	private JaxBConverter converter;

	@Inject
	private HistorizationService historizationService;


	@Nonnull
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entity/{entityName}/rev/{revision}")
	public Response getAllByRevision(
		@Nonnull @PathParam("entityName") String entityName,
		@Nonnull @Min(1) @PathParam("revision") Integer revision,
		@Context HttpServletResponse response) {

		List<AbstractEntity> entityList = historizationService.getAllEntitiesByRevision(entityName, revision);
		List<JaxAbstractDTO> resultList = new ArrayList<>();
		if (entityList != null) {
			resultList = entityList.stream().filter(entity -> entity instanceof ApplicationProperty)
				.map(entity -> converter.applicationPropertyToJAX((ApplicationProperty) entity)).collect(Collectors.toList());
		}
		return Response.ok(resultList).build();

	}


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
