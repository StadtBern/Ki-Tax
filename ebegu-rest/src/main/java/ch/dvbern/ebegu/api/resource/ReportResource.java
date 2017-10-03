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
import ch.dvbern.ebegu.api.dtos.JaxDownloadFile;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.ReportService;
import ch.dvbern.ebegu.util.DateUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

/**
 * REST Resource fuer Reports
 */
@Path("reporting")
@Stateless
@Api(description = "Resource für Statistiken und Reports")
public class ReportResource {

	public static final String DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN = "Das von-Datum muss vor dem bis-Datum sein.";

	@Inject
	private ReportService reportService;

	@Inject
	private DownloadResource downloadResource;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuch-Stichtag'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchStichtag")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGesuchStichtagReportExcel(
		@QueryParam("dateTimeStichtag") @Nonnull String dateTimeStichtag,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
	    throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(dateTimeStichtag);
		LocalDate date = DateUtil.parseStringToDateOrReturnNow(dateTimeStichtag);

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchStichtag(date,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);
		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuch-Zeitraum'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchZeitraum")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGesuchZeitraumReportExcel(
		@QueryParam("dateTimeFrom") @Nonnull String dateTimeFromParam,
		@QueryParam("dateTimeTo") @Nonnull String dateTimeToParam,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(dateTimeFromParam);
		Validate.notNull(dateTimeToParam);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(dateTimeFromParam);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(dateTimeToParam);

		if (!dateTo.isAfter(dateFrom)) {
			throw new EbeguRuntimeException("getGesuchZeitraumReportExcel", "Fehler beim erstellen Report Gesuch Zeitraum"
				, DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchZeitraum(dateFrom,
			dateTo,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Kanton'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/kanton")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKantonReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateAuswertungVon = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateAuswertungBis = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateAuswertungBis.isAfter(dateAuswertungVon)) {
			throw new EbeguRuntimeException("getKantonReportExcel", "Fehler beim erstellen Report Kanton", DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportKanton(dateAuswertungVon, dateAuswertungBis);
		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);
		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'MitarbeiterInnen'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/mitarbeiterinnen")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMitarbeiterinnenReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateAuswertungVon = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateAuswertungBis = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateAuswertungBis.isAfter(dateAuswertungVon)) {
			throw new EbeguRuntimeException("getMitarbeiterinnenReportExcel", "Fehler beim erstellen Report Mitarbeiterinnen", DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportMitarbeiterinnen(dateAuswertungVon, dateAuswertungBis);
		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);
		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Zahlungsauftrag'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/zahlungsauftrag")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZahlungsauftragReportExcel(
		@QueryParam("zahlungsauftragID") @Nonnull @Valid JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		Validate.notNull(jaxId);
		String ip = downloadResource.getIP(request);
		String id = converter.toEntityId(jaxId);

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportZahlungAuftrag(id);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Zahlung'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/zahlung")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZahlungReportExcel(
		@QueryParam("zahlungID") @Nonnull @Valid JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		Validate.notNull(jaxId);
		String ip = downloadResource.getIP(request);
		String id = converter.toEntityId(jaxId);

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportZahlung(id);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Zahlungen pro Periode'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/zahlungperiode")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZahlungPeridoReportExcel(
		@QueryParam("gesuchsperiodeID") @Nonnull @Valid JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		Validate.notNull(jaxId);
		String ip = downloadResource.getIP(request);
		String id = converter.toEntityId(jaxId);

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportZahlungPeriode(id);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuchsteller-Kinder-Betreuung'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchstellerkinderbetreuung")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGesuchstellerKinderBetreuungReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateTo.isAfter(dateFrom)) {
			throw new EbeguRuntimeException("getGesuchstellerKinderBetreuungReportExcel", "Fehler beim erstellen Report Gesuchsteller-Kinder-Betreuung"
				, DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchstellerKinderBetreuung(dateFrom, dateTo,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Kinder'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/kinder")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKinderReportExcel(
		@QueryParam("auswertungVon") @Nonnull String auswertungVon,
		@QueryParam("auswertungBis") @Nonnull String auswertungBis,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(auswertungVon);
		Validate.notNull(auswertungBis);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(auswertungVon);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(auswertungBis);

		if (!dateTo.isAfter(dateFrom)) {
			throw new EbeguRuntimeException("getKinderReportExcel", "Fehler beim erstellen Report Kinder"
				, DAS_VON_DATUM_MUSS_VOR_DEM_BIS_DATUM_SEIN);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportKinder(dateFrom, dateTo,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@ApiOperation(value = "Erstellt ein Excel mit der Statistik 'Gesuchsteller'", response = JaxDownloadFile.class)
	@Nonnull
	@GET
	@Path("/excel/gesuchsteller")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGesuchstellerReportExcel(
		@QueryParam("stichtag") @Nonnull String stichtag,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		String ip = downloadResource.getIP(request);

		Validate.notNull(stichtag);
		LocalDate date = DateUtil.parseStringToDateOrReturnNow(stichtag);

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchsteller(date);
		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}
}
