package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(EinkommensverschlechterungContainerService.class)
public class EinkommensverschlechterungContainerServiceBean extends AbstractBaseService implements EinkommensverschlechterungContainerService {

	@Inject
	private Persistence<EinkommensverschlechterungContainer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Override
	@Nonnull
	public EinkommensverschlechterungContainer saveEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		Objects.requireNonNull(einkommensverschlechterungContainer);
		return persistence.merge(einkommensverschlechterungContainer);
	}

	@Override
	@Nonnull
	public Optional<EinkommensverschlechterungContainer> findEinkommensverschlechterungContainer(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		EinkommensverschlechterungContainer a = persistence.find(EinkommensverschlechterungContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<EinkommensverschlechterungContainer> getAllEinkommensverschlechterungContainer() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EinkommensverschlechterungContainer.class));
	}

	@Override
	public void removeEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		Validate.notNull(einkommensverschlechterungContainer);
		einkommensverschlechterungContainer.getGesuchsteller().setEinkommensverschlechterungContainer(null);
		persistence.merge(einkommensverschlechterungContainer.getGesuchsteller());

		Optional<EinkommensverschlechterungContainer> propertyToRemove = findEinkommensverschlechterungContainer(einkommensverschlechterungContainer.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEinkommensverschlechterungContainer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, einkommensverschlechterungContainer));
		persistence.remove(EinkommensverschlechterungContainer.class, propertyToRemove.get().getId());
	}


}
