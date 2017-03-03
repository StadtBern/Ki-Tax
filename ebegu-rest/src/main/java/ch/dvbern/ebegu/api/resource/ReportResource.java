package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.lib.ExcelMergeException;
import ch.dvbern.ebegu.services.ReportService;
import ch.dvbern.ebegu.util.DateUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import io.swagger.annotations.Api;
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
import java.time.LocalDateTime;

/**
 * REST Resource fuer Reports
 */
@Path("reporting")
@Stateless
@Api
public class ReportResource {

	@Inject
	private ReportService reportService;

	@Inject
	private DownloadResource downloadResource;

	@Inject
	private JaxBConverter converter;

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
		LocalDateTime dateTime = DateUtil.parseStringToDateTimeOrReturnNow(dateTimeStichtag);

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchStichtag(dateTime,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);
		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

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
		LocalDateTime dateTimeFrom = DateUtil.parseStringToDateTimeOrReturnNow(dateTimeFromParam);
		LocalDateTime dateTimeTo = DateUtil.parseStringToDateTimeOrReturnNow(dateTimeToParam);

		if (!dateTimeTo.isAfter(dateTimeFrom)) {
			throw new EbeguRuntimeException("getGesuchZeitraumReportExcel", "Fehler beim erstellen Report Gesuch Zeitraum"
				, "Das von-Datum muss vor dem bis-Datum sein.");
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchZeitraum(dateTimeFrom,
			dateTimeTo,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@Nonnull
	@GET
	@Path("/excel/zahlungsauftrag")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZahlungsauftragReportExcel(
		@QueryParam("zahlungsauftragID") @Nullable @Valid JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);
		String id = null;
		if(jaxId!= null) {
			id = converter.toEntityId(jaxId);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportZahlungAuftrag(id, null);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}

	@Nonnull
	@GET
	@Path("/excel/zahlung")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZahlungReportExcel(
		@QueryParam("zahlungID") @Nullable @Valid JaxId jaxId,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException, EbeguRuntimeException {

		String ip = downloadResource.getIP(request);
		String id = null;
		if(jaxId!= null) {
			id = converter.toEntityId(jaxId);
		}

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportZahlung(id);

		DownloadFile downloadFileInfo = new DownloadFile(uploadFileInfo, ip);

		return downloadResource.getFileDownloadResponse(uriInfo, ip, downloadFileInfo);
	}
}
