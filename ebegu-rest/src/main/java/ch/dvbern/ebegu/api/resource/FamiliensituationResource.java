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

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFamiliensituationContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.services.GesuchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

/**
 * Resource fuer Familiensituation
 */
@Path("familiensituation")
@Stateless
@Api(description = "Resource für die Familiensituation")
public class FamiliensituationResource {

	@Inject
	private FamiliensituationService familiensituationService;
	@Inject
	private GesuchService gesuchService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private ResourceHelper resourceHelper;


	@ApiOperation(value = "Speichert eine Familiensituation in der Datenbank", response = JaxFamiliensituationContainer.class)
	@Nullable
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamiliensituationContainer saveFamiliensituation(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull JaxFamiliensituationContainer familiensituationContainerJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Gesuch gesuch = gesuchService.findGesuch(gesuchJAXPId.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("saveFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXPId.getId()));

		// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
		resourceHelper.assertGesuchStatusForBenutzerRole(gesuch);

		FamiliensituationContainer familiensituationContainerToMerge = new FamiliensituationContainer();
		//wenn es sich um ein update handelt
		Familiensituation oldFamiliensituation = null;
		if (familiensituationContainerJAXP.getId() != null) {
			Optional<FamiliensituationContainer> loadedFamiliensituation = this.familiensituationService.findFamiliensituation(familiensituationContainerJAXP.getId());
			if (loadedFamiliensituation.isPresent()) {
				familiensituationContainerToMerge = loadedFamiliensituation.get();
				oldFamiliensituation = new Familiensituation(familiensituationContainerToMerge.extractFamiliensituation());
			} else {
				familiensituationContainerToMerge = new FamiliensituationContainer();
			}
		}

		FamiliensituationContainer convertedFamiliensituation = converter.familiensituationContainerToEntity(familiensituationContainerJAXP, familiensituationContainerToMerge);
		FamiliensituationContainer persistedFamiliensituation = this.familiensituationService.saveFamiliensituation(gesuch, convertedFamiliensituation, oldFamiliensituation);

		return converter.familiensituationContainerToJAX(persistedFamiliensituation);
	}
}
