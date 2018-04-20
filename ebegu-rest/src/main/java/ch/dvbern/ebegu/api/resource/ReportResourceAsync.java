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

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.dtos.JaxDownloadFile;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.enums.WorkJobType;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.WorkjobService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * REST Resource fuer Reports
 */
@Path("reporting/async")
@Stateless
@Api(description = "Resource für Statistiken und Reports")
@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT, SACHBEARBEITER_INSTITUTION,
	SACHBEARBEITER_TRAEGERSCHAFT, REVISOR })
public class ReportResourceAsync {

	public static final String DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN = "Das von-Datum muss vor dem bis-Datum sein.";
	public static final String URL_PART_EXCEL = "excel/";

	@Inject
	private DownloadResource downloadResource;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private WorkjobService workjobService;


	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuch-Stichtag'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchStichtag")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getGesuchStichtagReportExcel(
		@QueryParam("dateTimeStichtag") @Nonnull String dateTimeStichtag,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) {

		String ip = downloadResource.getIP(request);
		Validate.notNull(dateTimeStichtag);
		LocalDate datumVon = DateUtil.parseStringToDateOrReturnNow(dateTimeStichtag);

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());

		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		String periodeId = gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null;
		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_GESUCH_STICHTAG, datumVon, null, periodeId);

		return Response.ok(workJob.getId()).build();
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuch-Zeitraum'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchZeitraum")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getGesuchZeitraumReportExcel(
		@QueryParam("dateTimeFrom") @Nonnull String dateTimeFromParam,
		@QueryParam("dateTimeTo") @Nonnull String dateTimeToParam,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(dateTimeFromParam);
		Validate.notNull(dateTimeToParam);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(dateTimeFromParam);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(dateTimeToParam);

		if (!dateTo.isAfter(dateFrom)) {
			throw new EbeguRuntimeException("getGesuchZeitraumReportExcel", "Fehler beim erstellen Report Gesuch Zeitraum"
				, DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		String periodeId = gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null;
		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_GESUCH_ZEITRAUM, dateFrom, dateTo, periodeId);

		return Response.ok(workJob.getId()).build();
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Kanton'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/kanton")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getKantonReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateAuswertungVon = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateAuswertungBis = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateAuswertungBis.isAfter(dateAuswertungVon)) {
			throw new EbeguRuntimeException("getKantonReportExcel", "Fehler beim erstellen Report Kanton", DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_KANTON, dateAuswertungVon, dateAuswertungBis, null);

		return Response.ok(workJob.getId()).build();
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'MitarbeiterInnen'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/mitarbeiterinnen")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMitarbeiterinnenReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateAuswertungVon = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateAuswertungBis = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateAuswertungBis.isAfter(dateAuswertungVon)) {
			throw new EbeguRuntimeException("getMitarbeiterinnenReportExcel", "Fehler beim erstellen Report Mitarbeiterinnen", DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_MITARBEITERINNEN, dateAuswertungVon, dateAuswertungBis, null);

		return Response.ok(workJob.getId()).build();
	}

//	not implemented aync getZahlungsauftragReportExcel
//	not implemented aync  getZahlungReportExcel(

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Zahlungen pro Periode'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/zahlungperiode")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getZahlungPeridoReportExcel(
		@QueryParam("gesuchsperiodeID") @Nonnull @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws EbeguRuntimeException {

		Validate.notNull(gesuchPeriodIdParam);
		String ip = downloadResource.getIP(request);

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		String periodeId = gesuchPeriodIdParam.getId();
		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_ZAHLUNG_AUFTRAG_PERIODE, null, null, periodeId);

		return Response.ok(workJob.getId()).build();
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuchsteller-Kinder-Betreuung'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchstellerkinderbetreuung")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getGesuchstellerKinderBetreuungReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateTo.isAfter(dateFrom)) {
			throw new EbeguRuntimeException("getGesuchstellerKinderBetreuungReportExcel", "Fehler beim erstellen Report Gesuchsteller-Kinder-Betreuung"
				, DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		String periodeId = gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null;
		workJob =  workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_GESUCHSTELLER_KINDER_BETREUUNG, dateFrom, dateTo, periodeId);

		return Response.ok(workJob.getId()).build();
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Kinder'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/kinder")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getKinderReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(auswertungBis);
		String periodeId = gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null;

		if (!dateTo.isAfter(dateFrom)) {
			throw new EbeguRuntimeException("getKinderReportExcel", "Fehler beim erstellen Report Kinder"
				, DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}
		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_KINDER, dateFrom, dateTo, periodeId);

		return Response.ok(workJob.getId()).build();
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuchsteller'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchsteller")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getGesuchstellerReportExcel(
		@QueryParam("stichtag") @Nonnull String stichtag,
		@Context HttpServletRequest request, @Context UriInfo uriInfo) {

		String ip = downloadResource.getIP(request);

		Validate.notNull(stichtag);
		LocalDate date = DateUtil.parseStringToDateOrReturnNow(stichtag);

		Workjob workJob = new Workjob();
		workJob.setWorkJobType(WorkJobType.REPORT_GENERATION);
		workJob.setStartinguser(principalBean.getPrincipal().getName());
		workJob.setTriggeringIp(ip);
		workJob.setRequestURI(uriInfo.getRequestUri().toString());
		String param = StringUtils.substringAfterLast(request.getRequestURI(), URL_PART_EXCEL);
		workJob.setParams(param);

		workJob = workjobService.createNewReporting(workJob, ReportVorlage.VORLAGE_REPORT_GESUCHSTELLER, date, null, null);

		return Response.ok(workJob.getId()).build();
	}
}
