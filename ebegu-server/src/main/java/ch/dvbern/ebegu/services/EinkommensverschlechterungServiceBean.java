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

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Einkommensverschlechterung
 */
@Stateless
@Local(EinkommensverschlechterungService.class)
public class EinkommensverschlechterungServiceBean extends AbstractBaseService implements EinkommensverschlechterungService {

	@Inject
	private Persistence<EinkommensverschlechterungContainer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private FinanzielleSituationRechner finSitRechner;

	@Inject
	private WizardStepService wizardStepService;


	@Override
	@Nonnull
	@RolesAllowed({ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public EinkommensverschlechterungContainer saveEinkommensverschlechterungContainer(
		@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer, String gesuchId) {
		Objects.requireNonNull(einkommensverschlechterungContainer);
		final EinkommensverschlechterungContainer persistedEKV = persistence.merge(einkommensverschlechterungContainer);
		if(gesuchId != null) {
			wizardStepService.updateSteps(gesuchId, null, einkommensverschlechterungContainer, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
		}
		return persistedEKV;
	}

	@Override
	@Nonnull
	@PermitAll
	public Optional<EinkommensverschlechterungContainer> findEinkommensverschlechterungContainer(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		EinkommensverschlechterungContainer a = persistence.find(EinkommensverschlechterungContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<EinkommensverschlechterungContainer> getAllEinkommensverschlechterungContainer() {
		return new ArrayList<>(criteriaQueryHelper.getAll(EinkommensverschlechterungContainer.class));
	}

	@Override
	@PermitAll
	public void removeEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		Validate.notNull(einkommensverschlechterungContainer);
		einkommensverschlechterungContainer.getGesuchsteller().setEinkommensverschlechterungContainer(null);
		persistence.merge(einkommensverschlechterungContainer.getGesuchsteller());

		Optional<EinkommensverschlechterungContainer> entityToRemove = findEinkommensverschlechterungContainer(einkommensverschlechterungContainer.getId());
		entityToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEinkommensverschlechterungContainer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, einkommensverschlechterungContainer));
		entityToRemove.ifPresent(einkommensverschlechterungContainer1 -> persistence.remove
			(EinkommensverschlechterungContainer.class, einkommensverschlechterungContainer1.getId()));
	}

	@Override
	@Nonnull
	@PermitAll
	public FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch, int basisJahrPlus) {
		return finSitRechner.calculateResultateEinkommensverschlechterung(gesuch, basisJahrPlus, true);
	}
}
