/*
 * Copyright (c)  2013. DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.entities.Workjob_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WorkJobConstants;
import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;

import static ch.dvbern.ebegu.enums.WorkJobConstants.DATE_FROM_PARAM;
import static ch.dvbern.ebegu.enums.WorkJobConstants.DATE_TO_PARAM;
import static ch.dvbern.ebegu.enums.WorkJobConstants.REPORT_VORLAGE_TYPE_PARAM;

/**
 * Data Acess Object Bean zum zugriff auf Workjoben in der DB
 */
@Stateless
@Local(WorkjobService.class)
@PermitAll
public class WorkjobServiceBean extends AbstractBaseService implements WorkjobService {


	private static final Logger LOG = LoggerFactory.getLogger(WorkjobServiceBean.class.getSimpleName());

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private PrincipalBean principalBean;


	public WorkjobServiceBean() {

	}


	public Workjob saveWorkjob(Workjob workjob) {
		return persistence.merge(workjob);
	}

	@Override
	public Workjob findWorkjobByWorkjobID(final String workJobId) {
		if (workJobId == null) {
			return null;
		}
		return persistence.find(Workjob.class, workJobId);

	}

	@Override
	public Workjob findWorkjobByExecutionId(@Nonnull final Long executionId) {

		final Optional<Workjob> optional = criteriaQueryHelper.getEntityByUniqueAttribute(Workjob.class, executionId, Workjob_.executionId);
		return optional.orElse(null);

	}

	@Override
	public Workjob createNewReporting(@Nonnull Workjob workJob, @Nonnull ReportVorlage vorlage, @Nullable LocalDate datumVon, @Nullable LocalDate datumBis, @Nullable String gesuchPeriodIdParam) {
		checkIfJobCreationAllowed(workJob);

		JobOperator jobOperator = BatchRuntime.getJobOperator();
		final Properties jobParameters = new Properties();
		final String datumVonString;
		if (datumVon != null) {
			datumVonString = Constants.SQL_DATE_FORMAT.format(datumVon);
			jobParameters.setProperty(DATE_FROM_PARAM, datumVonString);
		}
		final String datumBisString;
		if (datumBis != null) {
			datumBisString = Constants.SQL_DATE_FORMAT.format(datumBis);
			jobParameters.setProperty(DATE_TO_PARAM, datumBisString);
		}

		jobParameters.setProperty(REPORT_VORLAGE_TYPE_PARAM, ReportVorlage.VORLAGE_REPORT_GESUCH_STICHTAG.name());

		setPropertyIfPresent(jobParameters, WorkJobConstants.GESUCH_PERIODE_ID_PARAM, gesuchPeriodIdParam);
		jobParameters.setProperty(WorkJobConstants.EMAIL_OF_USER, principalBean.getBenutzer().getEmail());
		jobOperator.getJobNames();
		long executionId = jobOperator.start("reportbatch", jobParameters);
		workJob.setExecutionId(executionId);
		workJob.setStatus(BatchJobStatus.REQUESTED);
		workJob = this.saveWorkjob(workJob);
		LOG.debug("Startet GesuchStichttagStatistik with executionId {}", executionId);

		return workJob;
	}

	private void setPropertyIfPresent(Properties jobParameters, String paramName, String paramValue) {

		if (paramValue != null) {
			jobParameters.setProperty(paramName, paramValue);
		}

	}

	private void checkIfJobCreationAllowed(@Nonnull Workjob workJob) {

		Set<BatchJobStatus> statesToSearch = Sets.newHashSet(BatchJobStatus.REQUESTED, BatchJobStatus.RUNNING);

		final List<Workjob> openWorkjobs = this.findOpenWorkjobs(principalBean.getPrincipal().getName(), statesToSearch);
		final boolean alreadyQueued = openWorkjobs.stream().anyMatch(workJob::isSame);
		if (alreadyQueued) {
			LOG.error("An identical Workjob was already queued by this user; {} ", workJob);
			// todo homa check what actually happens if this is triggered
			throw new EbeguRuntimeException("checkIfJobCreationAllowed", ErrorCodeEnum.ERROR_JOB_ALREADY_EXISTS);
		}

	}

	//	@Override
//	public void updateWorkPackage(@Nonnull  final String workJobId, final int workPackageSeqNumber, @Nonnull  final Object[] row) {
//		Workpackage workpackage = findWorkPackage(workJobId, workPackageSeqNumber);
//		Validate.notNull(workpackage, "workpackage fuer workjob " + workJobId + " und sequenznummer "
//				+ workPackageSeqNumber + " konnte nicht gefunden werde, wir erwarten aber, " +
//				"dass das paket beim senden der message erstellt wurde");
//		workpackage.setWorkRowResult(StringUtils.join(row, "$"));
//		getEntityManager().persist(workpackage);
//	}

//	@Override
//	public List<Workpackage> getUnfinishedWorkpackages(@Nonnull  final String workjobIdentifier) {
//		Query q = this.getEntityManager().createNamedQuery(Workpackage.QUERY_FIND_UNFINISHED_PACKAGE);
//		q.setParameter("workJobId", workjobIdentifier);
//		return q.getResultList();
//	}

	@Override
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public void removeOldWorkjobs() {

//		Query q = this.getEntityManager().createNamedQuery(Workjob.QUERY_FIND_OLDER_WORKJOBS);
//		Date dateToDeleteFrom =  DateHelper.addDays(new Date(), -4);
//		q.setParameter("date",dateToDeleteFrom); //find older than 5 days
//		List<Workjob> oldWorkjobs = q.getResultList();
//		try {
//			for (Workjob workjobToRemove  : oldWorkjobs) {
//				this.getEntityManager().remove(workjobToRemove); //workpachages get removed by cascade
//			}
//		} catch (Exception e) {
//			Logger.getLogger(WorkjobServiceBean.class).warn(e.getClass().getSimpleName()+ " while trying to remove old Workjobs, Ignoring exception");
//
//		}

	}

	@Override
	public List<Workjob> findOpenWorkjobs(@Nonnull String startingUserName, @Nonnull Set<BatchJobStatus> statesToSearch) {
		Validate.notNull(startingUserName, "username to search must be set");
		Validate.notNull(statesToSearch, "statesToSearch  must be set");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Workjob> query = cb.createQuery(Workjob.class);
		Root<Workjob> root = query.from(Workjob.class);

		ParameterExpression<String> startingUsernameParam = cb.parameter(String.class, "startingUsernameParam");
		Predicate userPredicate = cb.equal(root.get(Workjob_.startinguser), startingUsernameParam);

		ParameterExpression<Collection> statusParam = cb.parameter(Collection.class, "statusParam");
		Predicate statusPredicate = root.get(Workjob_.status).in(statusParam);

		query.where(userPredicate, statusPredicate);
		TypedQuery<Workjob> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(startingUsernameParam, startingUserName);

		q.setParameter("startingUsernameParam", startingUserName);
		q.setParameter("statusParam", statesToSearch);

		return q.getResultList();
	}

	@Override
	public void changeStateToFinished(long executionId){
		persistence.getEntityManager().createNamedQuery(Workjob.Q_WORK_JOB_STATE_UPDATE)
			.setParameter("exId", executionId)
			.executeUpdate();
	}
}
