package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import javax.persistence.metamodel.EntityType;

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

/**
 * Created by imanol on 04.03.16.
 * Resource fuer Historization
 */
@Path("historization")
@Stateless
public class HistorizationResource {

	@Inject
	private Persistence<AbstractEntity> persistence;


	@Nullable
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{entity}/{revision}")
	public Response getByKey(
		@Nonnull @PathParam("entity") String entity,
		@Nonnull @Min(1) @PathParam("revision") Integer revParam,
		@Context HttpServletResponse response) {

		for (EntityType<?> entityType : persistence.getEntityManager().getMetamodel().getEntities()) {
			if (entityType.getName().equalsIgnoreCase(entity)) {
				AuditQuery query = AuditReaderFactory.get(persistence.getEntityManager())
					.createQuery()
					.forEntitiesAtRevision(entityType.getJavaType(), revParam);
				return Response.ok(query.getResultList()).build();
			}
		}

		//todo sollen wir hier eine Exception werfen?
		return Response.ok(null).build();

	}

}
