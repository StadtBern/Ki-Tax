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

import java.util.Collection;
import java.util.List;
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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxBelegungFerieninselTag;
import ch.dvbern.ebegu.api.dtos.JaxFerieninselStammdaten;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.BelegungFerieninselTag;
import ch.dvbern.ebegu.entities.FerieninselStammdaten;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.Ferienname;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.FerieninselStammdatenService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer FerieninselStammdaten
 */
@Path("ferieninselStammdaten")
@Stateless
@Api(description = "Resource fuer die Verwaltung von FerieninselStammdaten")
public class FerieninselStammdatenResource {


	@Inject
	private FerieninselStammdatenService ferieninselStammdatenService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Create a new FerieninselStammdaten in the database", response = JaxFerieninselStammdaten.class)
	@Nonnull
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFerieninselStammdaten saveFerieninselStammdaten(
		@Nonnull @NotNull @Valid JaxFerieninselStammdaten jaxFerieninselStammdaten,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		FerieninselStammdaten ferieninselStammdaten = new FerieninselStammdaten();
		if (jaxFerieninselStammdaten.getId() != null) {
			Optional<FerieninselStammdaten> optional = ferieninselStammdatenService.findFerieninselStammdaten(jaxFerieninselStammdaten.getId());
			ferieninselStammdaten = optional.orElse(new FerieninselStammdaten());
		}
		FerieninselStammdaten convertedFerieninselStammdaten = converter.ferieninselStammdatenToEntity(jaxFerieninselStammdaten, ferieninselStammdaten);

		FerieninselStammdaten persistedFachstelle = this.ferieninselStammdatenService.saveFerieninselStammdaten(convertedFerieninselStammdaten);
		return converter.ferieninselStammdatenToJAX(persistedFachstelle);
	}

	@ApiOperation(value = "Returns the FerieninselStammdaten with the specified ID", response = JaxFerieninselStammdaten.class)
	@Nullable
	@GET
	@Path("/id/{ferieninselStammdatenId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFerieninselStammdaten findFerieninselStammdaten(
		@Nonnull @NotNull @PathParam("ferieninselStammdatenId") JaxId ferieninselStammdatenId) throws EbeguRuntimeException {

		Validate.notNull(ferieninselStammdatenId.getId());
		String entityID = converter.toEntityId(ferieninselStammdatenId);
		FerieninselStammdaten ferieninselStammdaten = ferieninselStammdatenService.findFerieninselStammdaten(entityID).orElseThrow(()
			-> new EbeguRuntimeException("findFerieninselStammdaten", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, entityID));

		return converter.ferieninselStammdatenToJAX(ferieninselStammdaten);
	}

	@ApiOperation(value = "Returns all the FerieninselStammdaten for the Gesuchsperiode with the specified ID",
		responseContainer = "Collection", response = JaxFerieninselStammdaten.class)
	@Nullable
	@GET
	@Path("/gesuchsperiode/{gesuchsperiodeId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<JaxFerieninselStammdaten> findFerieninselStammdatenForGesuchsperiode(
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeId) throws EbeguEntityNotFoundException {

		Validate.notNull(gesuchsperiodeId.getId());
		String gpEntityID = converter.toEntityId(gesuchsperiodeId);

		Gesuchsperiode gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gpEntityID).orElseThrow(()
			-> new EbeguRuntimeException("findFerieninselStammdatenForGesuchsperiode", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gpEntityID));

		Collection<FerieninselStammdaten> ferieninselStammdatenList = ferieninselStammdatenService.findFerieninselStammdatenForGesuchsperiode(gesuchsperiode.getId());
		return ferieninselStammdatenList.stream()
			.map(fiStammdaten -> converter.ferieninselStammdatenToJAX(fiStammdaten))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Returns the FerieninselStammdaten for the Gesuchsperiode with the specified ID for the given Ferien",
		response = JaxFerieninselStammdaten.class)
	@Nullable
	@GET
	@Path("/gesuchsperiode/{gesuchsperiodeId}/{ferienname}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFerieninselStammdaten findFerieninselStammdatenForGesuchsperiodeAndFerienname(
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeId,
		@Nonnull @NotNull @PathParam("ferienname") String feriennameParam) throws EbeguEntityNotFoundException {

		Validate.notNull(gesuchsperiodeId.getId());
		String gpEntityID = converter.toEntityId(gesuchsperiodeId);
		Ferienname ferienname = Ferienname.valueOf(feriennameParam);

		Gesuchsperiode gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gpEntityID).orElseThrow(()
			-> new EbeguRuntimeException("findFerieninselStammdatenForGesuchsperiode", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gpEntityID));

		Optional<FerieninselStammdaten> stammdatenOptional = ferieninselStammdatenService.findFerieninselStammdatenForGesuchsperiodeAndFerienname
			(gesuchsperiode.getId(), ferienname);

		if (stammdatenOptional.isPresent()) {
			FerieninselStammdaten stammdaten = stammdatenOptional.get();
			JaxFerieninselStammdaten ferieninselStammdatenJAX = converter.ferieninselStammdatenToJAX(stammdaten);
			// Zur gefundenen Ferieninsel die tatsaechlich verfuegbaren Tage fuer die Belegung ermitteln (nur Wochentage, ohne Feiertage)
			List<BelegungFerieninselTag> possibleFerieninselTage = ferieninselStammdatenService.getPossibleFerieninselTage(stammdaten);
			List<JaxBelegungFerieninselTag> possibleFerieninselTageJAX = converter.belegungFerieninselTageListToJAX(possibleFerieninselTage);
			ferieninselStammdatenJAX.setPotenzielleFerieninselTageFuerBelegung(possibleFerieninselTageJAX);
			return ferieninselStammdatenJAX;
		}
		return null;
	}
}
