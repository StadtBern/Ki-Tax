package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.client.JaxOpenIdmResponse;
import ch.dvbern.ebegu.api.client.JaxOpenIdmResult;
import ch.dvbern.ebegu.api.client.OpenIdmRestService;
import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxTraegerschaft;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.TraegerschaftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Resource fuer Traegerschaft
 */
@Path("traegerschaften")
@Stateless
@Api
public class TraegerschaftResource {

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private OpenIdmRestService openIdmRestService;

//	private static final Logger LOG = LoggerFactory.getLogger(TraegerschaftResource.class.getSimpleName());

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxTraegerschaft saveTraegerschaft(
		@Nonnull @NotNull @Valid JaxTraegerschaft traegerschaftJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		boolean createMode = true;

		Traegerschaft traegerschaft = new Traegerschaft();
		if (traegerschaftJAXP.getId() != null) {
			Optional<Traegerschaft> optional = traegerschaftService.findTraegerschaft(traegerschaftJAXP.getId());
			traegerschaft = optional.orElse(new Traegerschaft());
			createMode = false;
		}
		Traegerschaft convertedTraegerschaft = converter.traegerschaftToEntity(traegerschaftJAXP, traegerschaft);

		Traegerschaft persistedTraegerschaft = this.traegerschaftService.saveTraegerschaft(convertedTraegerschaft);

		JaxTraegerschaft jaxTraegerschaft = converter.traegerschaftToJAX(persistedTraegerschaft);

		boolean success = createOrUpdateInIDM(createMode, persistedTraegerschaft);
		jaxTraegerschaft.setSynchronizedWithOpenIdm(success);

		return jaxTraegerschaft;
	}

	private boolean createOrUpdateInIDM(boolean createMode, Traegerschaft persistedTraegerschaft) {
		final Optional<JaxOpenIdmResult> openIdmRestClientInstitution;
		if (createMode) {
			openIdmRestClientInstitution = openIdmRestService.createTraegerschaft(persistedTraegerschaft);
		} else {
			openIdmRestClientInstitution = openIdmRestService.updateTraegerschaft(persistedTraegerschaft);
		}
		return openIdmRestClientInstitution.isPresent();
	}

	@Nullable
	@GET
	@Path("/id/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxTraegerschaft findTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId) throws EbeguException {

		Validate.notNull(traegerschaftJAXPId.getId());
		String traegerschaftID = converter.toEntityId(traegerschaftJAXPId);
		Optional<Traegerschaft> optional = traegerschaftService.findTraegerschaft(traegerschaftID);

		return optional.map(traegerschaft -> converter.traegerschaftToJAX(traegerschaft)).orElse(null);
	}

	@Nullable
	@DELETE
	@Path("/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(traegerschaftJAXPId.getId());
		final String traegerschaftId = converter.toEntityId(traegerschaftJAXPId);
		traegerschaftService.setInactive(traegerschaftId);

		openIdmRestService.deleteTraegerschaft(openIdmRestService.convertToOpenIdmTraegerschaftUID(traegerschaftId));

		return Response.ok().build();
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxTraegerschaft> getAllTraegerschaften() {
		return traegerschaftService.getAllTraegerschaften().stream()
			.map(traegerschaft -> converter.traegerschaftToJAX(traegerschaft))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Find and return a list of all active Traegerschaften. An active Traegerschaft is a Traegerschaft where the active flag is true")
	@Nonnull
	@GET
	@Path("/active")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxTraegerschaft> getAllActiveTraegerschaften() {
		return traegerschaftService.getAllActiveTraegerschaften().stream()
			.map(traegerschaft -> converter.traegerschaftToJAX(traegerschaft))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Synchronize DB Traegerschaten with OpenIdm Traegerschaten.")
	@Nullable
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/synchronizeWithOpenIdm")
	public Response synchronizeWithOpenIdm(
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		final StringBuilder stringBuilder = synchronizeTraegerschaft(true);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(InstitutionResource.class)
			.build();

		return Response.created(uri).entity(stringBuilder).build();
	}

	public StringBuilder synchronizeTraegerschaft(boolean deleteOrphan) {
		final Optional<JaxOpenIdmResponse> optAllInstitutions = openIdmRestService.getAll();
		final Collection<Traegerschaft> allActiveTraegerschaften = traegerschaftService.getAllActiveTraegerschaften();
		StringBuilder responseString = new StringBuilder("");

		if (optAllInstitutions.isPresent()) {
			final JaxOpenIdmResponse allInstitutions = optAllInstitutions.get();
			Objects.requireNonNull(allInstitutions);
			Objects.requireNonNull(allActiveTraegerschaften);

			// Create in OpenIDM those Traegerschaften where exist in EBEGU but not in OpenIDM
			allActiveTraegerschaften.forEach(ebeguTraegerschaft -> {
				if (allInstitutions.getResult().stream().noneMatch(jaxOpenIdmResult ->
					openIdmRestService.convertToEBEGUID(jaxOpenIdmResult.get_id()).equals(ebeguTraegerschaft.getId()) && jaxOpenIdmResult.getType().equals(OpenIdmRestService.TRAEGERSCHAFT))) {
					// if none match -> create
					final Optional<JaxOpenIdmResult> traegerschaftCreated = openIdmRestService.createTraegerschaft(ebeguTraegerschaft);
					openIdmRestService.generateResponseString(responseString, ebeguTraegerschaft.getId(), ebeguTraegerschaft.getName(), traegerschaftCreated.isPresent(), "Created");
				}
			});

			if (deleteOrphan) {
				// Delete in OpenIDM those Traegerschaten that exist in OpenIdm but not in EBEGU
				allInstitutions.getResult().forEach(openIdmInstitution -> {
					if (openIdmInstitution.getType().equals(OpenIdmRestService.TRAEGERSCHAFT) && allActiveTraegerschaften.stream().noneMatch(
						ebeguTraegerschaft -> ebeguTraegerschaft.getId().equals(openIdmRestService.convertToEBEGUID(openIdmInstitution.get_id())))) {
						// if none match -> delete
						final boolean sucess = openIdmRestService.deleteTraegerschaft(openIdmInstitution.get_id());
						openIdmRestService.generateResponseString(responseString, openIdmInstitution.get_id(), openIdmInstitution.getName(), sucess, "Deleted");
					}
				});
			}
		} else {
			responseString.append("Error: Can't communicate with OpenIdm server");
		}
		if (responseString.length() == 0) {
			responseString.append("No differences between OpenIdm and Ebegu found. Nothing to do!");
		}
		return responseString;
	}

}
