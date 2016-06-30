package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.Gesuch;
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
@Local(EinkommensverschlechterungInfoService.class)
public class EinkommensverschlechterungInfoServiceBean extends AbstractBaseService implements EinkommensverschlechterungInfoService {

	@Inject
	private Persistence<EinkommensverschlechterungInfo> persistence;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Override
	@Nonnull
	public Optional<EinkommensverschlechterungInfo> createEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		Objects.requireNonNull(einkommensverschlechterungInfo);
		final Gesuch gesuch = einkommensverschlechterungInfo.getGesuch();
		Objects.requireNonNull(gesuch);

		return Optional.ofNullable(gesuchService.updateGesuch(gesuch).getEinkommensverschlechterungInfo());
	}

	@Override
	@Nonnull
	public EinkommensverschlechterungInfo updateEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		Objects.requireNonNull(einkommensverschlechterungInfo);
		return persistence.merge(einkommensverschlechterungInfo);
	}

	@Override
	@Nonnull
	public Optional<EinkommensverschlechterungInfo> findEinkommensverschlechterungInfo(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		EinkommensverschlechterungInfo a = persistence.find(EinkommensverschlechterungInfo.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<EinkommensverschlechterungInfo> getAllEinkommensverschlechterungInfo() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EinkommensverschlechterungInfo.class));
	}

	@Override
	public void removeEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		Validate.notNull(einkommensverschlechterungInfo);
		einkommensverschlechterungInfo.getGesuch().setEinkommensverschlechterungInfo(null);
		persistence.merge(einkommensverschlechterungInfo.getGesuch());

		Optional<EinkommensverschlechterungInfo> propertyToRemove = findEinkommensverschlechterungInfo(einkommensverschlechterungInfo.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEinkommensverschlechterungInfo", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, einkommensverschlechterungInfo));
		persistence.remove(EinkommensverschlechterungInfo.class, propertyToRemove.get().getId());
	}


}
