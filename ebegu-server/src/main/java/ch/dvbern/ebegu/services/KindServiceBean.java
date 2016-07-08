package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
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
	private FallService fallService;
	@Inject
	private Persistence<KindContainer> persistence;

	@Nonnull
	@Override
	public KindContainer saveKind(@Nonnull KindContainer kind) {
		Objects.requireNonNull(kind);
		if (kind.getTimestampErstellt() == null) {
			// nur wenn das Kind erstellt wird, setzen wir die KindNummer und aktualisieren nextNumberKind in Fall
			Optional<Fall> optFall = fallService.findFall(kind.getGesuch().getFall().getId());
			if (optFall.isPresent()) {
				Fall fall = optFall.get();
				kind.setKindNummer(fall.getNextNumberKind());
				fall.setNextNumberKind(fall.getNextNumberKind() + 1);
			}
		}
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
