package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Kind
 */
@Stateless
@Local(KindService.class)
public class KindServiceBean extends AbstractBaseService implements KindService {

	@Inject
	private Persistence<KindContainer> persistence;

	@Nonnull
	@Override
	public KindContainer saveKind(@Nonnull KindContainer kind) {
		Objects.requireNonNull(kind);
		return persistence.merge(kind);
	}

	@Override
	@Nonnull
	public Optional<KindContainer> findKind(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		KindContainer a =  persistence.find(KindContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	public void removeKind(@Nonnull String kindId) {
		Objects.requireNonNull(kindId);
		Optional<KindContainer> kindToRemove = findKind(kindId);
		kindToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, kindId));
		persistence.remove(kindToRemove.get());
	}

}
