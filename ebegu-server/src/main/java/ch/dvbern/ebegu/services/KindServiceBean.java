package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.Kind_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Kind
 */
@Stateless
@Local(KindService.class)
public class KindServiceBean extends AbstractBaseService implements KindService {

	@Inject
	private Persistence<Kind> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public Kind saveKind(@Nonnull Kind kind) {
		Objects.requireNonNull(kind);
		return persistence.merge(kind);
	}

	@Override
	@Nonnull
	public Optional<Kind> findKind(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Kind a =  persistence.find(Kind.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	public void removeKind(@Nonnull String kindId) {
		Objects.requireNonNull(kindId);
		Optional<Kind> kindToRemove = findKind(kindId);
		kindToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, kindId));
		persistence.remove(kindToRemove.get());
	}

	@Override
	@Nonnull
	public Collection<Kind> getAllKinderFromGesuch(@Nonnull String gesuchId) {
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<Kind> query = builder.createQuery(Kind.class);
		final Root<Kind> root = query.from(Kind.class);

		final Join<Kind, Gesuch> join = root.join(Kind_.gesuch, JoinType.INNER);
		final Predicate predicate = builder.equal(join.get(Gesuch_.id), gesuchId);
		query.where(predicate);
		return persistence.getCriteriaResults(query);
	}

}
