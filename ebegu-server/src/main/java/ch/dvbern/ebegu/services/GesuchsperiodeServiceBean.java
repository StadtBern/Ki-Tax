package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AbstractDateRangedEntity_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Gesuchsperiode
 */
@Stateless
@Local(GesuchsperiodeService.class)
public class GesuchsperiodeServiceBean extends AbstractBaseService implements GesuchsperiodeService {

	@Inject
	private Persistence<Gesuchsperiode> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		Objects.requireNonNull(gesuchsperiode);
		return persistence.merge(gesuchsperiode);
	}

	@Nonnull
	@Override
	public Optional<Gesuchsperiode> findGesuchsperiode(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Gesuchsperiode gesuchsperiode =  persistence.find(Gesuchsperiode.class, key);
		return Optional.ofNullable(gesuchsperiode);
	}

	@Nonnull
	@Override
	public Collection<Gesuchsperiode> getAllGesuchsperioden() {
		return criteriaQueryHelper.getAll(Gesuchsperiode.class);
	}

	@Override
	public void removeGesuchsperiode(@Nonnull String gesuchsperiodeId) {
		Objects.requireNonNull(gesuchsperiodeId);
		Optional<Gesuchsperiode> gesuchsperiodeToRemove = findGesuchsperiode(gesuchsperiodeId);
		gesuchsperiodeToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeGesuchsperiode", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsperiodeId));
		persistence.remove(gesuchsperiodeToRemove.get());
	}

	@Override
	@Nonnull
	public Collection<Gesuchsperiode> getAllActiveGesuchsperioden() {
		return criteriaQueryHelper.getEntitiesByAttribute(Gesuchsperiode.class, true, Gesuchsperiode_.active);
	}

	@Override
	@Nonnull
	public Collection<Gesuchsperiode> getAllNichtAbgeschlosseneGesuchsperioden() {
		// Alle Gesuchsperioden, die aktuell am laufen sind oder in der Zukunft liegen, d.h. deren Ende-Datum nicht in der Vergangenheit liegt
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuchsperiode> query = cb.createQuery(Gesuchsperiode.class);
		Root<Gesuchsperiode> root = query.from(Gesuchsperiode.class);
		query.select(root);

		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");
		Predicate predicate = cb.greaterThanOrEqualTo(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis), dateParam);

		query.where(predicate);
		TypedQuery<Gesuchsperiode> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateParam, LocalDate.now());
		return q.getResultList();
	}
}
