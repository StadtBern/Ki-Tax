package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

	@Nonnull
	@Override
	public void removeGesuchsperiode(@Nonnull String gesuchsperiodeId) {
		Objects.requireNonNull(gesuchsperiodeId);
		Optional<Gesuchsperiode> gesuchsperiodeToRemove = findGesuchsperiode(gesuchsperiodeId);
		gesuchsperiodeToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeGesuchsperiode", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsperiodeId));
		persistence.remove(gesuchsperiodeToRemove.get());
	}
}
