package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Kind
 */
@Stateless
@Local(EbeguVorlageService.class)
public class EbeguVorlageServiceBean extends AbstractBaseService implements EbeguVorlageService {

	@Inject
	private Persistence<EbeguVorlage> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public EbeguVorlage saveEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage) {
		Objects.requireNonNull(ebeguVorlage);
		return persistence.merge(ebeguVorlage);
	}


/*	@Override
	@Nonnull
	public Optional<EbeguVorlage> getEbeguVorlageByKeyAndDate(@Nonnull EbeguVorlageKey key, @Nonnull LocalDate date, final EntityManager em) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);

		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");
		Predicate intervalPredicate = cb.between(dateParam,
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis));

		ParameterExpression<EbeguVorlageKey> keyParam = cb.parameter(EbeguVorlageKey.class, "key");
		Predicate keyPredicate = cb.equal(root.get(EbeguVorlage_.name), keyParam);

		query.where(intervalPredicate, keyPredicate);
		TypedQuery<EbeguVorlage> q = em.createQuery(query);
		q.setParameter(dateParam, date);
		q.setParameter(keyParam, key);
		List<EbeguVorlage> resultList = q.getResultList();
		EbeguVorlage paramOrNull = null;
		if (!resultList.isEmpty() && resultList.size() == 1) {
			paramOrNull = resultList.get(0);
		} else if (resultList.size() > 1) {
			throw new NonUniqueResultException();
		}
		return Optional.ofNullable(paramOrNull);
	}*/


	@Override
	@Nonnull
	public Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate,LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey) {
		final EntityManager em = persistence.getEntityManager();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);


		ParameterExpression<EbeguVorlageKey> keyParam = cb.parameter(EbeguVorlageKey.class, "key");
		Predicate keyPredicate = cb.equal(root.get(EbeguVorlage_.name), keyParam);

		ParameterExpression<LocalDate> dateAbParam = cb.parameter(LocalDate.class, "dateAb");
		Predicate dateAbPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb), dateAbParam);

		ParameterExpression<LocalDate> dateBisParam = cb.parameter(LocalDate.class, "dateBis");
		Predicate dateBisPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis), dateBisParam);

		query.where(keyPredicate, dateAbPredicate, dateBisPredicate);
		TypedQuery<EbeguVorlage> q = em.createQuery(query);
		q.setParameter(dateAbParam, abDate);
		q.setParameter(dateBisParam, bisDate);
		q.setParameter(keyParam, ebeguVorlageKey);
		List<EbeguVorlage> resultList = q.getResultList();
		EbeguVorlage paramOrNull = null;
		if (!resultList.isEmpty() && resultList.size() == 1) {
			paramOrNull = resultList.get(0);
		} else if (resultList.size() > 1) {
			throw new NonUniqueResultException();
		}
		return Optional.ofNullable(paramOrNull);
	}

	@Override
	@Nonnull
	public List<EbeguVorlage> getALLEbeguVorlageByGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);

		ParameterExpression<LocalDate> dateAbParam = cb.parameter(LocalDate.class, "dateAb");
		Predicate dateAbPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb), dateAbParam);

		ParameterExpression<LocalDate> dateBisParam = cb.parameter(LocalDate.class, "dateBis");
		Predicate dateBisPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis), dateBisParam);

		query.where(dateAbPredicate, dateBisPredicate);
		TypedQuery<EbeguVorlage> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateAbParam, gesuchsperiode.getGueltigkeit().getGueltigAb());
		q.setParameter(dateBisParam, gesuchsperiode.getGueltigkeit().getGueltigBis());

		List<EbeguVorlage> resultList = q.getResultList();
		return resultList;
	}


	@Override
	@Nullable
	public EbeguVorlage updateEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage) {
		Objects.requireNonNull(ebeguVorlage);

//		//Wenn EbeguVorlage keine Dokumente mehr hat und nicht gebraucht wird, wird er entfernt
//		if (!ebeguVorlage.isNeeded() && (ebeguVorlage.getDokumente() == null || ebeguVorlage.getDokumente().isEmpty())) {
//			persistence.remove(ebeguVorlage);
//			return null;
//		}
		return persistence.merge(ebeguVorlage);
	}

	@Override
	public void remove(@Nonnull String id) {
		Validate.notNull(id);
		Optional<EbeguVorlage> ebeguVorlage = findById(id);
		ebeguVorlage.orElseThrow(() -> new EbeguEntityNotFoundException("removeEbeguVorlage", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));
		persistence.remove(ebeguVorlage.get());
	}

	@Nonnull
	@Override
	public Optional<EbeguVorlage> findById(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		EbeguVorlage a =  persistence.find(EbeguVorlage.class, id);
		return Optional.ofNullable(a);
	}



}
