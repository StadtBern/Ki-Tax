package ch.dvbern.ebegu.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer die Einkommensverschlechterung
 */
@Stateless
@Local(EinkommensverschlechterungInfoService.class)
@RolesAllowed({ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
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
	@RolesAllowed({ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public Optional<EinkommensverschlechterungInfoContainer> createEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo) {
		Objects.requireNonNull(einkommensverschlechterungInfo);
		final Gesuch gesuch = einkommensverschlechterungInfo.getGesuch();
		Objects.requireNonNull(gesuch);

		return Optional.ofNullable(gesuchService.updateGesuch(gesuch, false, null).getEinkommensverschlechterungInfoContainer());
	}

	@Override
	@Nonnull
	@RolesAllowed({ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public EinkommensverschlechterungInfoContainer updateEinkommensVerschlechterungInfoAndGesuch(Gesuch gesuch, EinkommensverschlechterungInfoContainer oldEVData,
																								 EinkommensverschlechterungInfoContainer convertedEkvi) {
		convertedEkvi.setGesuch(gesuch);
		gesuch.setEinkommensverschlechterungInfoContainer(convertedEkvi);

		//Alle Daten des EV loeschen wenn man kein EV mehr eingeben will
		removeEinkommensverschlechterungContFromGesuchsteller(gesuch.getGesuchsteller1(), oldEVData, convertedEkvi);
		removeEinkommensverschlechterungContFromGesuchsteller(gesuch.getGesuchsteller2(), oldEVData, convertedEkvi);
		removeEinkommensverschlechterungFromGesuch(gesuch, convertedEkvi);

		convertedEkvi.setGesuch(gesuchService.updateGesuch(gesuch, false, null)); // saving gesuch cascades and saves Ekvi too

		wizardStepService.updateSteps(gesuch.getId(), oldEVData,
			convertedEkvi, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);

		return convertedEkvi;
	}

	@Override
	@Nonnull
	@PermitAll
	public Optional<EinkommensverschlechterungInfoContainer> findEinkommensverschlechterungInfo(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		EinkommensverschlechterungInfoContainer a = persistence.find(EinkommensverschlechterungInfoContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@RolesAllowed({ADMIN, SUPER_ADMIN})
	public Collection<EinkommensverschlechterungInfoContainer> getAllEinkommensverschlechterungInfo() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EinkommensverschlechterungInfoContainer.class));
	}

	@Override
	@RolesAllowed({ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public void removeEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo) {
		Validate.notNull(einkommensverschlechterungInfo);
		einkommensverschlechterungInfo.getGesuch().setEinkommensverschlechterungInfoContainer(null);
		persistence.merge(einkommensverschlechterungInfo.getGesuch());

		Optional<EinkommensverschlechterungInfoContainer> propertyToRemove = findEinkommensverschlechterungInfo(einkommensverschlechterungInfo.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEinkommensverschlechterungInfo", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, einkommensverschlechterungInfo));
		propertyToRemove.ifPresent(einkommensverschlechterungInfoContainer -> persistence.remove
			(EinkommensverschlechterungInfoContainer.class, einkommensverschlechterungInfoContainer.getId()));
	}

	/**
	 * Removes all EKV of the given Gesuch if the year is not set. The field GemeinsameSteuererklaerung_BjPX will be
	 * also set to null. If the year is set nothing will be done.
	 */
	private void removeEinkommensverschlechterungFromGesuch(Gesuch gesuch, EinkommensverschlechterungInfoContainer
		convertedEkvi) {
		if (!convertedEkvi.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus1()) {
			einkommensverschlechterungService.removeAllEKVOfGesuch(gesuch, 1);
			convertedEkvi.getEinkommensverschlechterungInfoJA().setGemeinsameSteuererklaerung_BjP1(null);
		}
		if (!convertedEkvi.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2()) {
			einkommensverschlechterungService.removeAllEKVOfGesuch(gesuch, 2);
			convertedEkvi.getEinkommensverschlechterungInfoJA().setGemeinsameSteuererklaerung_BjP2(null);
		}
	}

	private void removeEinkommensverschlechterungContFromGesuchsteller(GesuchstellerContainer gesuchsteller, EinkommensverschlechterungInfoContainer oldData, EinkommensverschlechterungInfoContainer convertedEkvi) {
		if (isNeededToRemoveEinkommensverschlechterungCont(gesuchsteller, oldData, convertedEkvi)) {
			//noinspection ConstantConditions
			einkommensverschlechterungService.removeEinkommensverschlechterungContainer(gesuchsteller.getEinkommensverschlechterungContainer());
			gesuchsteller.setEinkommensverschlechterungContainer(null);
		}
	}

	/**
	 * Returns true when the given GS already has an einkommensverschlechtrung and the new EVInfo says that no EV should be present
	 */
	private boolean isNeededToRemoveEinkommensverschlechterungCont(GesuchstellerContainer gesuchsteller, EinkommensverschlechterungInfoContainer oldData, EinkommensverschlechterungInfoContainer newData) {
		return oldData != null && newData != null && gesuchsteller != null
			&& !newData.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung()
			&& gesuchsteller.getEinkommensverschlechterungContainer() != null;
	}
}
