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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AdresseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer Adressen
 */
@Path("adressen")
@Stateless
@Api(description = "Resource zum Speichern von Adressen")
public class AdresseResource {

	@Inject
	private AdresseService adresseService;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Erstellt eine neue Adresse in der Datenbank.", response = JaxAdresse.class)
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxAdresse adresseJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Adresse convertedAdresse = converter.adresseToEntity(adresseJAXP, new Adresse());
		Adresse persistedAdresse = this.adresseService.createAdresse(convertedAdresse);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(AdresseResource.class)
			.path("/" + persistedAdresse.getId())
			.build();

		return Response.created(uri).entity(converter.adresseToJAX(persistedAdresse)).build();
	}

	@ApiOperation(value = "Aktualisiert eine Adresse in der Datenbank.", response = JaxAdresse.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxAdresse update(
		@Nonnull @NotNull JaxAdresse adresseJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(adresseJAXP.getId());
		Adresse adrFromDB = adresseService.findAdresse(adresseJAXP.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, adresseJAXP.getId()));
		Adresse adrToMerge = converter.adresseToEntity(adresseJAXP, adrFromDB);
		Adresse modifiedAdresse = this.adresseService.updateAdresse(adrToMerge);

		return converter.adresseToJAX(modifiedAdresse);
	}
}
