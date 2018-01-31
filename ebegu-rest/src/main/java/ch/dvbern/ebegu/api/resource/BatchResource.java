/*
 * Copyright © 2017 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.api.resource;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.BatchJaxBConverter;
import ch.dvbern.ebegu.api.dtos.batch.JaxBatchJob;
import ch.dvbern.ebegu.api.dtos.batch.JaxBatchJobInformation;
import ch.dvbern.ebegu.api.dtos.batch.JaxBatchJobList;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;

@Path("auth/login/admin/batch")
@Stateless
public class BatchResource {
//
//	@Inject
//	private BatchController batchController;
//
	@Inject
	private BatchJaxBConverter converter;
//
//	@Inject
//	private AuthService authService;

	@Nonnull
	private URI buildJobUri(@Nonnull UriInfo uriInfo, long executionId) {
		return uriInfo.getBaseUriBuilder()
			.path(BatchResource.class)
			.path("/jobs/{executionId}")
			.build(String.valueOf(executionId));
	}

	@GET
	@Path("/jobs")
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBatchJobList getBatchJobInformation(
		@Valid @MatrixParam("start") @DefaultValue("0") int start,
		@Valid @MatrixParam("count") @DefaultValue("100") int count) {

		JobOperator operator = BatchRuntime.getJobOperator();
		List<JaxBatchJob> result = operator.getJobNames().stream()
			.map(name -> {
				List<JaxBatchJobInformation> executions = operator.getJobInstances(name, start, count).stream()
					.flatMap(inst -> operator.getJobExecutions(inst).stream())
					.map(converter::batchJobInformationToResource)
					.collect(Collectors.toList());

				return new JaxBatchJob(name, executions);
			})
			.collect(Collectors.toList());

		return new JaxBatchJobList(result);
	}

	@GET
	@Path("/jobs/{executionId}") // Vorsicht: die URL hierher wird in buildJobUri dynamisch zusammengebaut!
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatchJobInformation(@Nonnull @NotNull @Valid @PathParam("executionId") long idParam) {
		try {
			JobExecution information = BatchRuntime.getJobOperator().getJobExecution(idParam);
			return Response.ok(converter.batchJobInformationToResource(information)).build();
		} catch (NoSuchJobExecutionException ex) {
			throw new EbeguEntityNotFoundException("getBatchJobInfo", "could not find batch job", ex);
		}
	}
//
//	@GET
//	@Path("/leistungsrechnung")
//	@Produces(MediaType.TEXT_PLAIN)
//	// Darf von anonymous ausgeführt werden, da via CronJob getriggert (und der hat kein Cookie)
//	public Response startLeistungsrechnung(@Context UriInfo uriInfo) {
//
//		long executionId = authService.runAsSuperAdmin(() -> batchController.startLeistungsrechnung());
//
//		URI batchInfoURI = buildJobUri(uriInfo, executionId);
//		return Response
//			.seeOther(batchInfoURI)
//			.entity("Startet den Batch-Job leistungsrechnung mit executionId : " + executionId + " ; "
//				+ "BatchJobInformation : " + batchInfoURI)
//			.build();
//	}
//
//	@GET
//	@Path("/cleanupschulungsmandanten")
//	@Produces(MediaType.TEXT_PLAIN)
//	public Response startCleanupSchulungsMandanten(@Context UriInfo uriInfo) {
//
//		long executionId = authService.runAsSuperAdmin(() -> batchController.startCleanupSchulungsMandanten());
//
//		URI batchInfoURI = buildJobUri(uriInfo, executionId);
//		return Response
//			.seeOther(batchInfoURI)
//			.entity("Startet den Batch-Job leistungsrechnung mit executionId : " + executionId + " ; "
//				+ "BatchJobInformation : " + batchInfoURI)
//			.build();
//	}
}
