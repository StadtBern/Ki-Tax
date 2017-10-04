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

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.Validate;

import ch.dvbern.ebegu.api.client.JaxOpenIdmResponse;
import ch.dvbern.ebegu.api.client.JaxOpenIdmResult;
import ch.dvbern.ebegu.api.client.OpenIdmRestService;
import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxTraegerschaft;
import ch.dvbern.ebegu.api.util.OpenIDMUtil;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.TraegerschaftService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST Resource fuer Traegerschaft
 */
@Path("traegerschaften")
@Stateless
@Api(description = "Resource zur Verwaltung von Trägerschaften (Zusammenschluss von mehreren Institutionen)")
public class TraegerschaftResource {

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private OpenIdmRestService openIdmRestService;

	@ApiOperation(value = "Speichert eine Traegerschaft in der Datenbank", response = JaxTraegerschaft.class)
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

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	private boolean createOrUpdateInIDM(boolean createMode, Traegerschaft persistedTraegerschaft) {
		final Optional<JaxOpenIdmResult> openIdmRestClientInstitution;
		if (createMode) {
			openIdmRestClientInstitution = openIdmRestService.createTraegerschaft(persistedTraegerschaft);
		} else {
			openIdmRestClientInstitution = openIdmRestService.updateTraegerschaft(persistedTraegerschaft);
		}
		return openIdmRestClientInstitution.isPresent();
	}

	@ApiOperation(value = "Gibt die Traegerschaft mit der uebergebenen id zurueck.", response = JaxTraegerschaft.class)
	@Nullable
	@GET
	@Path("/id/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxTraegerschaft findTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId) {

		Validate.notNull(traegerschaftJAXPId.getId());
		String traegerschaftID = converter.toEntityId(traegerschaftJAXPId);
		Optional<Traegerschaft> optional = traegerschaftService.findTraegerschaft(traegerschaftID);

		return optional.map(traegerschaft -> converter.traegerschaftToJAX(traegerschaft)).orElse(null);
	}

	@ApiOperation(value = "Loescht die Traegerschaft mit der uebergebenen id", response = Void.class)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Nullable
	@DELETE
	@Path("/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(traegerschaftJAXPId.getId());
		final String traegerschaftId = converter.toEntityId(traegerschaftJAXPId);
		Collection<Institution> allInstitutionen = institutionService.getAllActiveInstitutionenFromTraegerschaft(traegerschaftId);
		for (Institution institution : allInstitutionen) {
			institutionService.setInstitutionInactive(institution.getId());
			openIdmRestService.deleteInstitution(converter.toJaxId(institution).getId());
		}
		traegerschaftService.setInactive(traegerschaftId);
		openIdmRestService.deleteTraegerschaft(OpenIDMUtil.convertToOpenIdmTraegerschaftUID(traegerschaftId));
		return Response.ok().build();
	}

	@ApiOperation(value = "Gibt alle Traegerschaften zurueck.",
		responseContainer = "List", response = JaxTraegerschaft.class)
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxTraegerschaft> getAllTraegerschaften() {
		return traegerschaftService.getAllTraegerschaften().stream()
			.map(traegerschaft -> converter.traegerschaftToJAX(traegerschaft))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Find and return a list of all active Traegerschaften. An active Traegerschaft is a " +
		"Traegerschaft where the active flag is true",
		responseContainer = "List", response = JaxTraegerschaft.class)
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
					OpenIDMUtil.convertToEBEGUID(jaxOpenIdmResult.get_id()).equals(ebeguTraegerschaft.getId()) && jaxOpenIdmResult.getType().equals(OpenIdmRestService.TRAEGERSCHAFT))) {
					// if none match -> create
					final Optional<JaxOpenIdmResult> traegerschaftCreated = openIdmRestService.createTraegerschaft(ebeguTraegerschaft);
					openIdmRestService.generateResponseString(responseString, ebeguTraegerschaft.getId(), ebeguTraegerschaft.getName(), traegerschaftCreated.isPresent(), "Created");
				}
			});

			if (deleteOrphan) {
				// Delete in OpenIDM those Traegerschaten that exist in OpenIdm but not in EBEGU
				allInstitutions.getResult().forEach(openIdmInstitution -> {
					if (openIdmInstitution.getType().equals(OpenIdmRestService.TRAEGERSCHAFT) && allActiveTraegerschaften.stream().noneMatch(
						ebeguTraegerschaft -> ebeguTraegerschaft.getId().equals(OpenIDMUtil.convertToEBEGUID(openIdmInstitution.get_id())))) {
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
