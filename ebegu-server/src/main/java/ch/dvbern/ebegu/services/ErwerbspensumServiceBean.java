package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer {@link ErwerbspensumContainer} diese beinhalten einzelne Objekte mit den Daten von GS und JA
 */
@Stateless
@Local(ErwerbspensumService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA,  GESUCHSTELLER})
public class ErwerbspensumServiceBean extends AbstractBaseService implements ErwerbspensumService {

	@Inject
	private Persistence<ErwerbspensumContainer> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private Authorizer authorizer;

	@Nonnull
	@Override
	public ErwerbspensumContainer saveErwerbspensum(@Valid @Nonnull ErwerbspensumContainer erwerbspensumContainer, Gesuch gesuch) {
		Objects.requireNonNull(erwerbspensumContainer);
		final ErwerbspensumContainer mergedErwerbspensum = persistence.merge(erwerbspensumContainer);
		wizardStepService.updateSteps(gesuch.getId(), null, mergedErwerbspensum.getErwerbspensumJA(), WizardStepName.ERWERBSPENSUM);
		return mergedErwerbspensum;
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, JURIST,  GESUCHSTELLER, SCHULAMT})
	public Optional<ErwerbspensumContainer> findErwerbspensum(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		ErwerbspensumContainer ewpCnt =  persistence.find(ErwerbspensumContainer.class, key);
		authorizer.checkReadAuthorization(ewpCnt);
		return Optional.ofNullable(ewpCnt);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, JURIST,  GESUCHSTELLER})
	public Collection<ErwerbspensumContainer> findErwerbspensenForGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller) {
		return criteriaQueryHelper.getEntitiesByAttribute(ErwerbspensumContainer.class, gesuchsteller, ErwerbspensumContainer_.gesuchstellerContainer);
	}


	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, JURIST,  GESUCHSTELLER})
	public Collection<ErwerbspensumContainer> findErwerbspensenFromGesuch(@Nonnull String gesuchId) {
		Collection<ErwerbspensumContainer> result = new ArrayList<>();
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchId);
		if (gesuchOptional.isPresent()) {
			authorizer.checkReadAuthorization(gesuchOptional.get());
			if (gesuchOptional.get().getGesuchsteller1() != null) {
				result.addAll(findErwerbspensenForGesuchsteller(gesuchOptional.get().getGesuchsteller1()));
			}
			if (gesuchOptional.get().getGesuchsteller2() != null) {
				result.addAll(findErwerbspensenForGesuchsteller(gesuchOptional.get().getGesuchsteller2()));
			}
		}
		return result;
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA,  REVISOR, JURIST, })
	public Collection<ErwerbspensumContainer> getAllErwerbspensenenContainer() {
		Collection<ErwerbspensumContainer> ewpContainers = criteriaQueryHelper.getAll(ErwerbspensumContainer.class);
		ewpContainers.forEach(ewpContainer -> authorizer.checkReadAuthorization(ewpContainer));
		return ewpContainers;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, JURIST,  GESUCHSTELLER})
	public void removeErwerbspensum(@Nonnull String erwerbspensumContainerID, Gesuch gesuch) {
		Objects.requireNonNull(erwerbspensumContainerID);
		Optional<ErwerbspensumContainer> ewpCont = this.findErwerbspensum(erwerbspensumContainerID);
		persistence.remove(ewpCont
			.orElseThrow(
				() -> new EbeguEntityNotFoundException("removeErwerbspensum", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, erwerbspensumContainerID)
			)
		);
		wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.ERWERBSPENSUM);
	}
}
