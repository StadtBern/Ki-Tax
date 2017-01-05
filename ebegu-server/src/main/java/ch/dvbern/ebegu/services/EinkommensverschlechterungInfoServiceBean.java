package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
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
	private Persistence<EinkommensverschlechterungInfoContainer> persistence;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;
	@Inject
	private EinkommensverschlechterungService einkommensverschlechterungService;


	@Override
	@Nonnull
	public Optional<EinkommensverschlechterungInfoContainer> createEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo) {
		Objects.requireNonNull(einkommensverschlechterungInfo);
		final Gesuch gesuch = einkommensverschlechterungInfo.getGesuch();
		Objects.requireNonNull(gesuch);

		return Optional.ofNullable(gesuchService.updateGesuch(gesuch, false).getEinkommensverschlechterungInfoContainer());
	}

	@Override
	@Nonnull
	public EinkommensverschlechterungInfoContainer updateEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo) {
		Objects.requireNonNull(einkommensverschlechterungInfo);
		return persistence.merge(einkommensverschlechterungInfo);
	}

	@Override
	@Nonnull
	public EinkommensverschlechterungInfoContainer updateEinkommensVerschlechterungInfoAndGesuch(Gesuch gesuch, EinkommensverschlechterungInfoContainer oldEVData,
																								 EinkommensverschlechterungInfoContainer convertedEkvi) {
		convertedEkvi.setGesuch(gesuch);
		gesuch.setEinkommensverschlechterungInfoContainer(convertedEkvi);
		convertedEkvi.setGesuch(gesuchService.updateGesuch(gesuch, false)); // saving gesuch cascades and saves Ekvi too

		//Alle Daten des EV loeschen wenn man kein EV mehr eingeben will
		removeEinkommensverschlechterungFromGesuchsteller(gesuch.getGesuchsteller1(), oldEVData, convertedEkvi);
		removeEinkommensverschlechterungFromGesuchsteller(gesuch.getGesuchsteller2(), oldEVData, convertedEkvi);

		wizardStepService.updateSteps(gesuch.getId(), oldEVData,
			convertedEkvi, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);

		return convertedEkvi;
	}

	@Override
	@Nonnull
	public Optional<EinkommensverschlechterungInfoContainer> findEinkommensverschlechterungInfo(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		EinkommensverschlechterungInfoContainer a = persistence.find(EinkommensverschlechterungInfoContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<EinkommensverschlechterungInfoContainer> getAllEinkommensverschlechterungInfo() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EinkommensverschlechterungInfoContainer.class));
	}

	@Override
	public void removeEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo) {
		Validate.notNull(einkommensverschlechterungInfo);
		einkommensverschlechterungInfo.getGesuch().setEinkommensverschlechterungInfoContainer(null);
		persistence.merge(einkommensverschlechterungInfo.getGesuch());

		Optional<EinkommensverschlechterungInfoContainer> propertyToRemove = findEinkommensverschlechterungInfo(einkommensverschlechterungInfo.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEinkommensverschlechterungInfo", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, einkommensverschlechterungInfo));
		persistence.remove(EinkommensverschlechterungInfoContainer.class, propertyToRemove.get().getId());
	}

	private void removeEinkommensverschlechterungFromGesuchsteller(GesuchstellerContainer gesuchsteller, EinkommensverschlechterungInfoContainer oldData, EinkommensverschlechterungInfoContainer convertedEkvi) {
		if (isNeededToRemoveEinkommensverschlechterung(gesuchsteller, oldData, convertedEkvi)) {
			einkommensverschlechterungService.removeEinkommensverschlechterungContainer(gesuchsteller.getEinkommensverschlechterungContainer());
			gesuchsteller.setEinkommensverschlechterungContainer(null);
		}
	}

	/**
	 * Returns true when the given GS already has an einkommensverschlechtrung and the new EVInfo says that no EV should be present
	 *
	 * @param gesuchsteller
	 * @param oldData
	 * @param newData
	 * @return
	 */
	private boolean isNeededToRemoveEinkommensverschlechterung(GesuchstellerContainer gesuchsteller, EinkommensverschlechterungInfoContainer oldData, EinkommensverschlechterungInfoContainer newData) {
		return oldData != null && newData != null && gesuchsteller != null
			&& oldData.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung() && !newData.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung()
			&& gesuchsteller.getEinkommensverschlechterungContainer() != null;
	}


}
