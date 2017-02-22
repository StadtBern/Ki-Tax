package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.lib.ExcelMergeException;
import ch.dvbern.ebegu.services.ReportService;
import ch.dvbern.ebegu.util.DateUtil;
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

	private static final String MIME_TYPE_EXCEL = "application/vnd.ms-excel";

	@Inject
	private ReportService reportService;

	@Nonnull
	@GET
	@Path("/excel/gesuchStichtag")
	@Consumes(MediaType.WILDCARD)
	@Produces(MIME_TYPE_EXCEL)
	public Response getGesuchStichtagReportExcel(
		@QueryParam("dateTimeStichtag") @Nonnull String dateTimeStichtag,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
	    throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		Validate.notNull(dateTimeStichtag);
		LocalDateTime dateTime = DateUtil.parseStringToDateTimeOrReturnNow(dateTimeStichtag);

		byte[] reportBytes = reportService.generateExcelReportGesuchStichtag(dateTime,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		return Response.ok(reportBytes)
			.header("Content-Disposition", "attachment; filename=GesuchStichtagReport.xlsx")
			.header("Content-Length", reportBytes.length)
			.type(MediaType.valueOf(MIME_TYPE_EXCEL))
			.build();
	}

	@Nonnull
	@GET
	@Path("/excel/gesuchZeitraum")
	@Consumes(MediaType.WILDCARD)
	@Produces(MIME_TYPE_EXCEL)
	public Response getGesuchZeitraumReportExcel(
		@QueryParam("dateTimeFrom") @Nonnull String dateTimeFromParam,
		@QueryParam("dateTimeTo") @Nonnull String dateTimeToParam,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		Validate.notNull(dateTimeFromParam);
		Validate.notNull(dateTimeToParam);
		LocalDateTime dateTimeFrom = DateUtil.parseStringToDateTimeOrReturnNow(dateTimeFromParam);
		LocalDateTime dateTimeTo = DateUtil.parseStringToDateTimeOrReturnNow(dateTimeToParam);

		if (!dateTimeTo.isAfter(dateTimeFrom)) {
			throw new EbeguRuntimeException("getGesuchZeitraumReportExcel", "Das von-Datum muss vor dem bis-Datum sein.");
		}

		byte[] reportBytes = reportService.generateExcelReportGesuchZeitraum(dateTimeFrom,
			dateTimeTo,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		return Response.ok(reportBytes)
			.header("Content-Disposition", "attachment; filename=GesuchPeriodeReport.xlsx")
			.header("Content-Length", reportBytes.length)
			.type(MediaType.valueOf(MIME_TYPE_EXCEL))
			.build();
	}
}
