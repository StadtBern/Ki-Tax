package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
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

	@Inject
	private ReportService reportService;

	@Inject
	private JaxBConverter converter;

	@Nonnull
	@GET
	@Path("/gesuchStichtag/excel/")
	@Consumes(MediaType.WILDCARD)
	@Produces("application/vnd.ms-excel")
	public Response getGesuchStichtagReportExcel(
		@QueryParam("stichtagDateTime") @Nonnull String stringDateTimeParam,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
	throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		Validate.notNull(stringDateTimeParam);
		LocalDateTime dateTime = DateUtil.parseStringToDateTimeOrReturnNow(stringDateTimeParam);

		byte[] reportBytes = reportService.generateExcelReportGesuchStichtag(dateTime,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		Response.ResponseBuilder response = Response.ok(reportBytes);
		response.header("Content-Disposition",
			"attachment; filename=GesuchStichtagReport.xls");
		return response.build();

	}

	@Nonnull
	@GET
	@Path("/gesuchStichtag/excel/")
	@Consumes(MediaType.WILDCARD)
	@Produces("application/vnd.ms-excel")
	public Response getGesuchPeriodeReportExcel(
		@QueryParam("dateTimeFrom") @Nonnull String stringDateTimeFromParam,
		@QueryParam("dateTimeTo") @Nonnull String stringDateTimeToParam,
		@QueryParam("gesuchPeriodeID") @Nullable @Valid JaxId gesuchPeriodIdParam,
		@Context HttpServletRequest request, @Context UriInfo uriInfo)
		throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		Validate.notNull(stringDateTimeFromParam);
		Validate.notNull(stringDateTimeToParam);
		LocalDateTime dateTimeFrom = DateUtil.parseStringToDateTimeOrReturnNow(stringDateTimeFromParam);
		LocalDateTime dateTimeTo = DateUtil.parseStringToDateTimeOrReturnNow(stringDateTimeFromParam);

		byte[] reportBytes = reportService.generateExcelReportGesuchPeriode(dateTimeFrom,
			dateTimeTo,
			gesuchPeriodIdParam != null ? gesuchPeriodIdParam.getId() : null);

		Response.ResponseBuilder response = Response.ok(reportBytes);
		response.header("Content-Disposition",
			"attachment; filename=GesuchPeriodeReport.xls");
		return response.build();

	}

}
