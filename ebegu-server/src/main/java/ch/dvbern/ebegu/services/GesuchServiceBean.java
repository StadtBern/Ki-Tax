package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AbstractEntity_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.Gesuchsteller_;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Gesuch
 */
@Stateless
@Local(GesuchService.class)
public class GesuchServiceBean extends AbstractBaseService implements GesuchService {

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public Gesuch createGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		return persistence.persist(gesuch);
	}

	@Nonnull
	@Override
	public Gesuch updateGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		return persistence.merge(gesuch);
	}

	@Nonnull
	@Override
	public Optional<Gesuch> findGesuch(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Gesuch a = persistence.find(Gesuch.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Gesuch> getAllGesuche() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Gesuch.class));
	}

	@Nonnull
	@Override
	public Collection<Gesuch> getAllActiveGesuche() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);

		Predicate predicateGesuch = cb.notEqual(root.get(Gesuch_.status), AntragStatus.VERFUEGT);
		query.where(predicateGesuch);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	@Override
	public void removeGesuch(@Nonnull Gesuch gesuch) {
		Validate.notNull(gesuch);
		Optional<Gesuch> gesuchToRemove = findGesuch(gesuch.getId());
		gesuchToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuch));
		persistence.remove(gesuchToRemove.get());
	}

	public Optional<Gesuch> findGesuchByGSName(String nachname, String vorname) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);

		ParameterExpression<String> nameParam = cb.parameter(String.class, "nachname");
		Predicate namePredicate = cb.equal(root.get(Gesuch_.gesuchsteller1).get(Gesuchsteller_.nachname), nameParam);

		ParameterExpression<String> vornameParam = cb.parameter(String.class, "vorname");
		Predicate vornamePredicate = cb.equal(root.get(Gesuch_.gesuchsteller1).get(Gesuchsteller_.vorname), vornameParam);

		query.where(namePredicate, vornamePredicate);
		TypedQuery<Gesuch> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(nameParam, nachname);
		q.setParameter(vornameParam, vorname);

		return Optional.ofNullable(q.getSingleResult());
	}

	public Optional<Gesuch> findGesuchByFallAndGesuchsperiode(String fallID, String gesuchsperiodeID) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);

		ParameterExpression<String> fallParam = cb.parameter(String.class, "fallID");
		Predicate namePredicate = cb.equal(root.get(Gesuch_.fall).get(AbstractEntity_.id), fallParam);

		ParameterExpression<String> gesuchsperiodeParam = cb.parameter(String.class, "vorname");
		Predicate vornamePredicate = cb.equal(root.get(Gesuch_.gesuchsperiode).get(AbstractEntity_.id), gesuchsperiodeParam);

		query.where(namePredicate, vornamePredicate);
		TypedQuery<Gesuch> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(fallParam, fallID);
		q.setParameter(gesuchsperiodeParam, gesuchsperiodeID);

		return Optional.ofNullable(q.getSingleResult());
	}

}
