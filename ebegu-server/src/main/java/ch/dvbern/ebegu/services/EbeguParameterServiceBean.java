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

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.entities.AbstractDateRangedEntity_;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.EbeguParameter_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.JURIST;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.STEUERAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer E-BEGU-Parameter
 */
@Stateless
@Local(EbeguParameterService.class)
@RolesAllowed({ADMIN, SUPER_ADMIN})
public class EbeguParameterServiceBean extends AbstractBaseService implements EbeguParameterService {

	@Inject
	private Persistence<AbstractEntity> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Override
	@Nonnull
	@RolesAllowed({ADMIN, SUPER_ADMIN})
	public EbeguParameter saveEbeguParameter(@Nonnull EbeguParameter ebeguParameter) {
		Objects.requireNonNull(ebeguParameter);
		return persistence.merge(ebeguParameter);
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Optional<EbeguParameter> findEbeguParameter(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		EbeguParameter a = persistence.find(EbeguParameter.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@RolesAllowed({ADMIN, SUPER_ADMIN})
	public void removeEbeguParameter(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Optional<EbeguParameter> parameterToRemove = findEbeguParameter(id);
		EbeguParameter param = parameterToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEbeguParameter", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));
		persistence.remove(param);
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Collection<EbeguParameter> getAllEbeguParameter() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EbeguParameter.class));
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Collection<EbeguParameter> getAllEbeguParameterByDate(@Nonnull LocalDate date) {
		return new ArrayList<>(criteriaQueryHelper.getAllInInterval(EbeguParameter.class, date));
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Collection<EbeguParameter> getEbeguParameterByGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		Collection<EbeguParameter> ebeguParameters = getAllEbeguParameterByDate(gesuchsperiode.getGueltigkeit().getGueltigAb());
		List<EbeguParameter> collect = ebeguParameters.stream().filter(ebeguParameter -> ebeguParameter.getName().isProGesuchsperiode()).collect(Collectors.toCollection(ArrayList::new));
		if (collect.isEmpty()) {
			copyEbeguParameterListToNewGesuchsperiode(gesuchsperiode);
			ebeguParameters = getAllEbeguParameterByDate(gesuchsperiode.getGueltigkeit().getGueltigAb());
			collect = ebeguParameters.stream().filter(ebeguParameter -> ebeguParameter.getName().isProGesuchsperiode()).collect(Collectors.toCollection(ArrayList::new));
		}
		collect.sort(Comparator.comparing(EbeguParameter::getName));
		return collect;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Collection<EbeguParameter> getEbeguParametersByJahr(@Nonnull Integer jahr) {
		Collection<EbeguParameter> ebeguParameters = getAllEbeguParameterByDate(LocalDate.of(jahr, Month.JANUARY, 1));
		List<EbeguParameter> collect = ebeguParameters.stream().filter(ebeguParameter -> !ebeguParameter.getName().isProGesuchsperiode()).collect(Collectors.toCollection(ArrayList::new));
		if (collect.isEmpty()) {
			createEbeguParameterListForJahr(jahr);
			ebeguParameters = getAllEbeguParameterByDate(LocalDate.of(jahr, Month.JANUARY, 1));
			collect = ebeguParameters.stream().filter(ebeguParameter -> !ebeguParameter.getName().isProGesuchsperiode()).collect(Collectors.toCollection(ArrayList::new));
		}
		collect.sort(Comparator.comparing(EbeguParameter::getName));
		return collect;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR})
	public Collection<EbeguParameter> getJahresabhParameter() {
		List<EbeguParameterKey> jahresabhParamsKey = Arrays.stream(EbeguParameterKey.values()).filter(ebeguParameterKey -> !ebeguParameterKey.isProGesuchsperiode()).collect(Collectors.toList());
		return getEbeguParameterByKey(jahresabhParamsKey);

	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date) {
		return getEbeguParameterByKeyAndDate(key, date, persistence.getEntityManager());
	}

	/**
	 * Methode zum laden von EEGU Parametern
	 * @param key Key des property das geladen werden soll
	 * @param date stichtag zu dem der Wert des property gelesen werden soll
	 * @param em wir geben hier einen entity manager mit weil wir diese Methode aus dem validator aufrufen
	 *           im Validator darf man nicht einfach direkt den entity manager injecten weil dieser nicht in
	 *           der gleiche sein darf wie in den services (sonst gibt es eine concurrentModificationException in hibernate)
	 *           http://stackoverflow.com/questions/18267269/correct-way-to-do-an-entitymanager-query-during-hibernate-validation
	 * @return EbeguParameter
	 */
	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date, final EntityManager em) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
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
		TypedQuery<EbeguParameter> q = em.createQuery(query);
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

	@Nonnull
	private Collection<EbeguParameter> getEbeguParameterByKey(@Nonnull Collection<EbeguParameterKey> keys) {
		if (!keys.isEmpty()) {
			final CriteriaBuilder cb = persistence.getCriteriaBuilder();
			final CriteriaQuery<EbeguParameter> query = cb.createQuery(EbeguParameter.class);
			Root<EbeguParameter> root = query.from(EbeguParameter.class);
			query.select(root);

			Predicate keyPredicate = root.get(EbeguParameter_.name).in(keys);

			query.where(keyPredicate);
			query.orderBy(cb.asc(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb)));
			return persistence.getCriteriaResults(query);
		}
		return Collections.emptyList();

	}

	private void copyEbeguParameterListToNewGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		// Die Parameter des letzten Jahres suchen (datumAb -1 Tag)
		Collection<EbeguParameter> paramsOfGesuchsperiode = getAllEbeguParameterByDate(gesuchsperiode.getGueltigkeit().getGueltigAb().minusDays(1));
		paramsOfGesuchsperiode.stream().filter(lastYearParameter -> lastYearParameter.getName().isProGesuchsperiode()).forEach(lastYearParameter -> {
			EbeguParameter newParameter = lastYearParameter.copy(gesuchsperiode.getGueltigkeit());
			saveEbeguParameter(newParameter);
		});
	}

	/**
	 * searches all parameters that were valid at the first of january of the jahr-1. Then go through those parameters and if
	 * the parameter is set "per Gesuchsperiode" then copy it from the previous year and set the daterange for the current year
	 */
	private void createEbeguParameterListForJahr(@Nonnull Integer jahr) {
		Collection<EbeguParameter> paramsOfYear = getAllEbeguParameterByDate(LocalDate.of(jahr-1, Month.JANUARY, 1));
		paramsOfYear.stream().filter(lastYearParameter -> !lastYearParameter.getName().isProGesuchsperiode()).forEach(lastYearParameter -> {
			EbeguParameter newParameter = lastYearParameter.copy(new DateRange(jahr));
			saveEbeguParameter(newParameter);
		});
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT,  STEUERAMT})
	public Map<EbeguParameterKey, EbeguParameter> getEbeguParameterByGesuchsperiodeAsMap(@Nonnull Gesuchsperiode gesuchsperiode) {
		Map<EbeguParameterKey, EbeguParameter> result = new HashMap<>();
		Collection<EbeguParameter> paramsForPeriode = getEbeguParameterByGesuchsperiode(gesuchsperiode);
		for (EbeguParameter ebeguParameter : paramsForPeriode) {
			result.put(ebeguParameter.getName(), ebeguParameter);
		}
		return result;
	}
}
