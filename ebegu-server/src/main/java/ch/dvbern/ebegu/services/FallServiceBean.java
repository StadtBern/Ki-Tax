package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Fall
 */
public class FallServiceBean extends AbstractBaseService implements FallService {

	@Inject
	private Persistence<Fall> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public Fall createFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall);
		return persistence.persist(fall);
	}

	@Nonnull
	@Override
	public Fall updateFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall);
		return persistence.merge(fall);
	}

	@Nonnull
	@Override
	public Optional<Fall> findFall(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Fall a =  persistence.find(Fall.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Fall> getAllFalle() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Fall.class));
	}

	@Nonnull
	@Override
	public void removeFall(@Nonnull Fall fall) {
		Validate.notNull(fall);
		Optional<Fall> fallToRemove = findFall(fall.getId());
		fallToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fall));
		persistence.remove(fallToRemove.get());
	}
}
