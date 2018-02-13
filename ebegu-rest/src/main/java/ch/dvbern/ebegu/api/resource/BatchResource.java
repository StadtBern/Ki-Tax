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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

import ch.dvbern.ebegu.api.converter.BatchJaxBConverter;
import ch.dvbern.ebegu.api.dtos.batch.JaxBatchJobList;
import ch.dvbern.ebegu.api.dtos.batch.JaxWorkJob;
import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.WorkjobService;

@Path("admin/batch")
@Stateless
@RolesAllowed(UserRoleName.SUPER_ADMIN)
public class BatchResource {

	@Inject
	private BatchJaxBConverter converter;

	@Inject
	private WorkjobService workjobService;

	@Inject
	private PrincipalBean principalBean;


	@GET
	@Path("/jobs")
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBatchJobList getAllJobs(
		@Valid @MatrixParam("start") @DefaultValue("0") int start,
		@Valid @MatrixParam("count") @DefaultValue("100") int count) {

		JobOperator operator = BatchRuntime.getJobOperator();
		final List<JaxWorkJob> resultlist = operator.getJobNames().stream()
			.flatMap(name -> operator.getJobInstances(name, start, count).stream()
				.flatMap(inst -> operator.getJobExecutions(inst).stream())
				.map(converter::toBatchJobInformation)
				.map((ele) -> new JaxWorkJob(ele.getJobName(), ele)))
			.collect(Collectors.toList());

		return new JaxBatchJobList(resultlist);
	}

	@GET
	@Path("/jobs/{executionId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatchJobInformation(@NotNull @Valid @PathParam("executionId") long idParam) {
		try {
			JobExecution information = BatchRuntime.getJobOperator().getJobExecution(idParam);
			return Response.ok(converter.toBatchJobInformation(information)).build();
		} catch (NoSuchJobExecutionException ex) {
			throw new EbeguEntityNotFoundException("getBatchJobInfo", "could not find batch job", ex);
		}
	}

	@GET
	@Path("/userjobs/notokenrefresh") //wir pollen diesen endpunkt daher notokenrefresh
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response getBatchJobsOfUser() {

		Set<BatchJobStatus> all = Arrays.stream(BatchJobStatus.values()).collect(Collectors.toSet());
		final List<Workjob> jobs = workjobService.findWorkjobs(principalBean.getPrincipal().getName(), all);

		final JobOperator jobOperator = BatchRuntime.getJobOperator();

		final List<JaxWorkJob> jobList = jobs.stream()
			.map(job -> converter.toBatchJobInformation(job))
			.peek((jaxWorkJob) -> {
				JobExecution jobExecution = null;
				try {
					jobExecution = jobOperator.getJobExecution(jaxWorkJob.getExecutionId());
					jaxWorkJob.setExecution(converter.toBatchJobInformation(jobExecution));
				} catch (NoSuchJobExecutionException ex) {
					//ignroe, not a problem
				}
			})
			.collect(Collectors.toList());

		return Response.ok(new JaxBatchJobList(jobList)).build();
	}
}
