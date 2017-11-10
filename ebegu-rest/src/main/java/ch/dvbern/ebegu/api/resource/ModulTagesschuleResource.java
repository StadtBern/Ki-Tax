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

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxModulTagesschule;
import ch.dvbern.ebegu.services.ModulTagesschuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer ModulTagesschule
 */
@Path("moudultagesschule")
@Stateless
@Api(description = "Resource für ModulTagesschule")
public class ModulTagesschuleResource {

	@Inject
	private ModulTagesschuleService modulTagesschuleService;

	@Inject
	private JaxBConverter converter;

	/**
	 * Sucht in der DB alle TagesschulModule, die zu der mitgegebenen Tagesschule gehoeren
	 *
	 * @param institutionStammdatenJAXPId ID der Tagesschule
	 * @return Liste mit allen Montagsmodulen der Tagesschule
	 */
	@ApiOperation(value = "Gibt alle TagesschulModule des Montags der uebergebenen Tagesschule zurueck.",
		responseContainer = "List", response = JaxModulTagesschule.class)
	@Nonnull
	@GET
	@Path("/{institutionStammdatenId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxModulTagesschule> findMondayModuleTagesschuleByInstitutionStammdaten(
		@Nonnull @NotNull @PathParam("institutionStammdatenId") JaxId institutionStammdatenJAXPId) {
	//TODO wijo wird das ueberhaupt gebraucht?

		Validate.notNull(institutionStammdatenJAXPId.getId());
		String institutionStammdatenID = converter.toEntityId(institutionStammdatenJAXPId);
		return modulTagesschuleService.findMondayModuleTagesschuleByInstitutionStammdaten(institutionStammdatenID).stream()
			.map(modul -> converter.modulTagesschuleToJAX(modul))
			.collect(Collectors.toList());
	}

}
