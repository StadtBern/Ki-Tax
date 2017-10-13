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

package ch.dvbern.ebegu.api.resource.schulamt;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.api.dtos.JaxExternalAnmeldung;
import ch.dvbern.ebegu.api.dtos.JaxExternalAnmeldungFerieninsel;
import ch.dvbern.ebegu.api.dtos.JaxExternalAnmeldungTagesschule;
import ch.dvbern.ebegu.api.dtos.JaxExternalFerieninsel;
import ch.dvbern.ebegu.api.dtos.JaxExternalFinanzielleSituation;
import ch.dvbern.ebegu.api.dtos.JaxExternalModul;
import ch.dvbern.ebegu.api.enums.JaxAntragstatus;
import ch.dvbern.ebegu.api.enums.JaxBetreuungsstatus;
import ch.dvbern.ebegu.api.enums.JaxExternalFerienName;
import ch.dvbern.ebegu.api.enums.JaxExternalModulName;
import ch.dvbern.ebegu.api.enums.JaxTarifart;
import ch.dvbern.ebegu.api.util.version.VersionInfoBean;
import ch.dvbern.ebegu.util.DateUtil;
import ch.dvbern.ebegu.util.MathUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Path("/schulamt")
@Api(description = "Resource für die Schnittstelle zu externen Schulamt-Applikationen")
@SuppressWarnings({ "EjbInterceptorInspection", "EjbClassBasicInspection" })
@Stateless
public class SchulamtBackendResource {

	private static final Logger LOG = getLogger(SchulamtBackendResource.class);

	@Inject
	private VersionInfoBean versionInfoBean;


	@ApiOperation(value = "Gibt die Version von Ki-Tax zurück. Kann als Testmethode verwendet werden, da ohne Authentifizierung aufrufbar",
		response = String.class)
	@GET
	@Path("/heartbeat")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public String getHeartBeat() {
		StringBuilder builder = new StringBuilder();
		if (versionInfoBean != null && versionInfoBean.getVersionInfo().isPresent()) {
			builder.append("Version: ");
			builder.append(versionInfoBean.getVersionInfo().get().getVersion());

		} else {
			builder.append("unknown Version");
		}
		return builder.toString();
	}

	@ApiOperation(value = "Gibt eine Anmeldung fuer ein Schulamt-Angebot zurueck (Tagesschule oder Ferieninsel)",
		response = JaxExternalAnmeldung.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/anmeldung/{bgNummer}")
	public Response getAnmeldung(@Nonnull String bgNummer) {
		Validate.notNull(bgNummer);

		JaxExternalAnmeldung anmeldung;
		if (bgNummer.endsWith("1")) {
			anmeldung = (JaxExternalAnmeldung) getAnmeldungFerieninsel(bgNummer).getEntity();
		} else {
			anmeldung = (JaxExternalAnmeldung) getAnmeldungTagesschule(bgNummer).getEntity();
		}
		return Response.ok(anmeldung).build();
	}

	@ApiOperation(value = "Gibt eine Anmeldung fuer eine Tagesschule zurueck.",
		response = JaxExternalAnmeldungTagesschule.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/anmeldungTagesschule/{bgNummer}")
	public Response getAnmeldungTagesschule(@Nonnull String bgNummer) {
		Validate.notNull(bgNummer);

		if (!bgNummer.endsWith("1")) {
			List<JaxExternalModul> anmeldungen = new ArrayList<>();
			anmeldungen.add(new JaxExternalModul(DayOfWeek.MONDAY, JaxExternalModulName.VORMITTAG));
			anmeldungen.add(new JaxExternalModul(DayOfWeek.MONDAY, JaxExternalModulName.NACHMITTAGS_1));
			anmeldungen.add(new JaxExternalModul(DayOfWeek.MONDAY, JaxExternalModulName.NACHMITTAGS_2));
			anmeldungen.add(new JaxExternalModul(DayOfWeek.FRIDAY, JaxExternalModulName.VORMITTAG));
			JaxExternalAnmeldungTagesschule anmeldung = new JaxExternalAnmeldungTagesschule(bgNummer, JaxBetreuungsstatus.BESTAETIGT,
				"TODO_eindeutige_ID_Tagesschule", anmeldungen);
			return Response.ok(anmeldung).build();
		}
		return Response.noContent().build();
	}

	@ApiOperation(value = "Gibt eine Anmeldung fuer eine Ferieninsel zurueck.",
		response = JaxExternalAnmeldungFerieninsel.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/anmeldungFerieninsel/{bgNummer}")
	public Response getAnmeldungFerieninsel(@Nonnull String bgNummer) {
		Validate.notNull(bgNummer);

		if (bgNummer.endsWith("1")) {
			List<LocalDate> datumList = new ArrayList<>();
			datumList.add(LocalDate.now().plusMonths(2));
			datumList.add(LocalDate.now().plusMonths(2).plusDays(1));
			datumList.add(LocalDate.now().plusMonths(2).plusDays(2));
			JaxExternalFerieninsel ferieninsel = new JaxExternalFerieninsel(JaxExternalFerienName.HERBSTFERIEN, datumList);
			JaxExternalAnmeldungFerieninsel anmeldung = new JaxExternalAnmeldungFerieninsel(bgNummer, JaxBetreuungsstatus.VERFUEGT,
				"TODO_eindeutige_ID_Institution", ferieninsel);
			return Response.ok(anmeldung).build();
		}
		return Response.noContent().build();
	}

	@ApiOperation(value = "Gibt eine Anmeldung fuer ein Schulamt-Angebot zurueck (Tagesschule oder Ferieninsel)", responseContainer = "List",
		response = JaxExternalFinanzielleSituation.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/finanziellesituation")
	public Response getFinanzielleSituation(@Nonnull String stichtagParam, @Nonnull String csFaelleParam) {
		Validate.notNull(stichtagParam);
		Validate.notNull(csFaelleParam);

		String[] fallNummern = StringUtils.split(csFaelleParam, ',');
		if (fallNummern.length <= 0) {
			return Response.noContent().build();
		}
		LocalDate stichtag = DateUtil.parseStringToDateOrReturnNow(stichtagParam);

		List<JaxExternalFinanzielleSituation> result = new ArrayList<>();
		for (String fallNr : fallNummern) {
			@SuppressWarnings("ConstantConditions")
			JaxExternalFinanzielleSituation dto = new JaxExternalFinanzielleSituation(
				Long.valueOf(fallNr),
				stichtag,
				MathUtil.DEFAULT.from(65000),
				JaxAntragstatus.VERFUEGT,
				JaxTarifart.DETAILBERECHNUNG);
			result.add(dto);
		}
		return Response.ok(result).build();
	}
}
