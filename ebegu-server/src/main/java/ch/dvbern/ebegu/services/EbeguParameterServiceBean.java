/*
 * Copyright (c) 2014 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Service fuer E-BEGU-Parameter
 */
@Stateless
@Local(EbeguParameterService.class)
public class EbeguParameterServiceBean extends AbstractBaseService implements EbeguParameterService {


	@Inject
	private Persistence<AbstractEntity> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Override
	public void createEbeguParameterListForGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		// Die Parameter des letzten Jahres suchen
		Collection<EbeguParameter> lastYearParameterList = getAllEbeguParameterByDate(gesuchsperiode.getGueltigkeit().getGueltigAb().minusDays(1));
		lastYearParameterList.stream().filter(lastYearParameter -> lastYearParameter.getName().isProGesuchsperiode()).forEach(lastYearParameter -> {
			EbeguParameter newParameter = lastYearParameter.copy(gesuchsperiode.getGueltigkeit());
			saveEbeguParameter(newParameter);
		});
	}

	@Override
	@Nonnull
	public EbeguParameter saveEbeguParameter(@Nonnull EbeguParameter ebeguParameter) {
		Objects.requireNonNull(ebeguParameter);
		return persistence.merge(ebeguParameter);
	}

	@Override
	@Nonnull
	public Optional<EbeguParameter> findEbeguParameter(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		EbeguParameter a =  persistence.find(EbeguParameter.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	public void removeEbeguParameter(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Optional<EbeguParameter> parameterToRemove = findEbeguParameter(id);
		parameterToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEbeguParameter", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));
		//noinspection OptionalGetWithoutIsPresent
		persistence.remove(parameterToRemove.get());
	}

	@Override
	@Nonnull
	public Collection<EbeguParameter> getAllEbeguParameter() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EbeguParameter.class));
	}

	@Nonnull
	@Override
	public Collection<EbeguParameter> getAllEbeguParameterByDate(LocalDate date) {
		return new ArrayList<>(criteriaQueryHelper.getAllInInterval(EbeguParameter.class, date));
	}

	@Override
	public Optional<EbeguParameter> getEbeguParameterByKeyAndDate(EbeguParameterKey key, LocalDate date) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<EbeguParameter> query = cb.createQuery(EbeguParameter.class);
		Root<EbeguParameter> root = query.from(EbeguParameter.class);
		query.select(root);

		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");
		Predicate intervalPredicate = cb.between(dateParam,
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis));

		ParameterExpression<EbeguParameterKey> keyParam = cb.parameter(EbeguParameterKey.class, "key");
		Predicate keyPredicate = cb.equal(root.get(EbeguParameter_.name), keyParam);

		query.where(intervalPredicate, keyPredicate);
		TypedQuery<EbeguParameter> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateParam, date);
		q.setParameter(keyParam, key);
		List<EbeguParameter> resultList = q.getResultList();
		EbeguParameter paramOrNull = null;
		if (!resultList.isEmpty() && resultList.size() == 1) {
			paramOrNull = resultList.get(0);
		} else if (resultList.size() > 1) {
			throw new NonUniqueResultException();
		}
		return Optional.ofNullable(paramOrNull);
	}
}
