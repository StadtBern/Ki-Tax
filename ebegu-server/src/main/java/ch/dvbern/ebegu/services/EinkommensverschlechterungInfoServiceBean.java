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

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
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
		removeEKVContainerIfNotNeeded(gesuch.getGesuchsteller1(), oldEVData, convertedEkvi);
		removeEKVContainerIfNotNeeded(gesuch.getGesuchsteller2(), oldEVData, convertedEkvi);
		removeEinkommensverschlechterungFromGesuch(gesuch, convertedEkvi);
		//All needed EKVContainer must be created if they don't exist yet
		addEmptyEKVContainerIfNeeded(gesuch.getGesuchsteller1(), convertedEkvi);
		addEmptyEKVContainerIfNeeded(gesuch.getGesuchsteller2(), convertedEkvi);

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
			convertedEkvi.getEinkommensverschlechterungInfoJA().setStichtagFuerBasisJahrPlus1(null);
			convertedEkvi.getEinkommensverschlechterungInfoJA().setGrundFuerBasisJahrPlus1(null);
		}
		if (!convertedEkvi.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2()) {
			einkommensverschlechterungService.removeAllEKVOfGesuch(gesuch, 2);
			convertedEkvi.getEinkommensverschlechterungInfoJA().setGemeinsameSteuererklaerung_BjP2(null);
			convertedEkvi.getEinkommensverschlechterungInfoJA().setStichtagFuerBasisJahrPlus2(null);
			convertedEkvi.getEinkommensverschlechterungInfoJA().setGrundFuerBasisJahrPlus2(null);
		}
	}

	private void removeEKVContainerIfNotNeeded(GesuchstellerContainer gesuchsteller, EinkommensverschlechterungInfoContainer oldData, EinkommensverschlechterungInfoContainer convertedEkvi) {
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

	/**
	 * This method creates all required EkvContainer and EKV. It uses the information contained in the EKVInfo to
	 * know when these EKVCont and EKV must be created. They will be created using the values by default.
	 */
	private void addEmptyEKVContainerIfNeeded(GesuchstellerContainer gesuchsteller, EinkommensverschlechterungInfoContainer ekvInfo) {
		if (gesuchsteller != null) {
			if (gesuchsteller.getEinkommensverschlechterungContainer() == null
				&& ekvInfo.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung()) {

				EinkommensverschlechterungContainer ekvCont = new EinkommensverschlechterungContainer();
				ekvCont.setGesuchsteller(gesuchsteller);
				gesuchsteller.setEinkommensverschlechterungContainer(ekvCont);
			}
			if (ekvInfo.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus1()
				&& gesuchsteller.getEinkommensverschlechterungContainer() != null
				&& gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() == null) {

				gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus1(createEmptyEKV());
			}
			if (ekvInfo.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2()
				&& gesuchsteller.getEinkommensverschlechterungContainer() != null
				&& gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() == null) {

				gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus2(createEmptyEKV());
			}
		}
	}

	@Nonnull
	private Einkommensverschlechterung createEmptyEKV() {
		Einkommensverschlechterung ekvBasisPlus1 = new Einkommensverschlechterung();
		ekvBasisPlus1.setSteuererklaerungAusgefuellt(false);
		ekvBasisPlus1.setSteuerveranlagungErhalten(false);
		return ekvBasisPlus1;
	}
}
