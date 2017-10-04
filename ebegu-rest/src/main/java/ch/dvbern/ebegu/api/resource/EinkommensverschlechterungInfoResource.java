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
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EinkommensverschlechterungInfoService;
import ch.dvbern.ebegu.services.GesuchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST Resource fuer Einkommensverschlechterung
 */
@Path("einkommensverschlechterungInfo")
@Stateless
@Api(description = "Resource für EinkommensverschlechterungInfo (pro Familie)")
public class EinkommensverschlechterungInfoResource {

	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;
	@Inject
	private GesuchService gesuchService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Inject
	private ResourceHelper resourceHelper;

	@ApiOperation(value = "Create a new EinkommensverschlechterungInfoContainer in the database.",
		response = JaxEinkommensverschlechterungInfoContainer.class)
	@Nullable
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEinkommensverschlechterungInfo(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId,
		@Nonnull @NotNull @Valid JaxEinkommensverschlechterungInfoContainer jaxEkvInfoContainer,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Gesuch gesuch = gesuchService.findGesuch(gesuchId.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("saveEinkommensverschlechterungInfo", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId()));

		// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
		resourceHelper.assertGesuchStatusForBenutzerRole(gesuch);

		EinkommensverschlechterungInfoContainer oldEVData = null;
		EinkommensverschlechterungInfoContainer ekviToMerge = new EinkommensverschlechterungInfoContainer();

		if (jaxEkvInfoContainer.getId() != null) {
			Optional<EinkommensverschlechterungInfoContainer> optional = einkommensverschlechterungInfoService.
				findEinkommensverschlechterungInfo(jaxEkvInfoContainer.getId());
			ekviToMerge = optional.orElse(new EinkommensverschlechterungInfoContainer());
			oldEVData = new EinkommensverschlechterungInfoContainer(ekviToMerge); //wir muessen uns merken wie die Daten vorher waren damit wir nachher vergleichen koennen
		}
		EinkommensverschlechterungInfoContainer convertedEkvi = converter
			.einkommensverschlechterungInfoContainerToEntity(jaxEkvInfoContainer, ekviToMerge);

		EinkommensverschlechterungInfoContainer persistedEkvi = einkommensverschlechterungInfoService
			.updateEinkommensVerschlechterungInfoAndGesuch(gesuch, oldEVData, convertedEkvi);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EinkommensverschlechterungInfoResource.class)
			.path('/' + convertedEkvi.getId())
			.build();

		JaxEinkommensverschlechterungInfoContainer jaxEkvInfoContainerReturn = converter.einkommensverschlechterungInfoContainerToJAX(persistedEkvi);
		return Response.created(uri).entity(jaxEkvInfoContainerReturn).build();
	}
}
