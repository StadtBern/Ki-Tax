package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.api.dtos.JaxEnversRevision;
import ch.dvbern.ebegu.api.util.JaxBConverter;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.services.HistorizationService;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by imanol on 04.03.16.
 * Resource fuer Historization
 */
@Path("historization")
@Stateless
public class HistorizationResource {

	@Inject
	private JaxBConverter converter;

	@Inject
	private HistorizationService historizationService;


	@Nullable
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entity/{entityName}/rev/{revision}")
	public Response getAllByRevision(
		@Nonnull @PathParam("entityName") String entityName,
		@Nonnull @Min(1) @PathParam("revision") Integer revision,
		@Context HttpServletResponse response) {

		List<AbstractEntity> entityList = historizationService.getAllEntitiesByRevision(entityName, revision);
		List<JaxAbstractDTO> resultList = entityList.stream().filter(entity -> entity instanceof ApplicationProperty)
			.map(entity -> converter.applicationPropertieToJAX((ApplicationProperty) entity)).collect(Collectors.toList());
		return Response.ok(resultList).build();

	}


	@Nullable
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/entity/{entityName}/id/{id}")
	public Response getHistoryById(
		@Nonnull @PathParam("entityName") String entityName,
		@Nonnull @PathParam("id") String entityId,
		@Context HttpServletResponse response) {

		List<Object[]> entityList = historizationService.getAllRevisionsById(entityName, entityId);
		List<JaxEnversRevision> resultList = entityList.stream().map(entity -> converter.enversRevisionToJAX((DefaultRevisionEntity) entity[1],
			(AbstractEntity) entity[0], (RevisionType) entity[2])).collect(Collectors.toList());
		return Response.ok(resultList).build();
	}

}
