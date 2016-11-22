package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(FinanzielleSituationService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR,  GESUCHSTELLER, STEUERAMT})
public class FinanzielleSituationServiceBean extends AbstractBaseService implements FinanzielleSituationService {

	@Inject
	private Persistence<FinanzielleSituationContainer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private FinanzielleSituationRechner finSitRechner;

	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private Authorizer authorizer;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public FinanzielleSituationContainer saveFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation, String gesuchId) {
		Objects.requireNonNull(finanzielleSituation);
		authorizer.checkCreateAuthorizationFinSit(finanzielleSituation);
		FinanzielleSituationContainer finanzielleSituationPersisted = persistence.merge(finanzielleSituation);
		if(gesuchId != null) {
			wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.FINANZIELLE_SITUATION);
		}
		return finanzielleSituationPersisted;
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR,  GESUCHSTELLER, STEUERAMT})
	public Optional<FinanzielleSituationContainer> findFinanzielleSituation(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		FinanzielleSituationContainer finanzielleSituation = persistence.find(FinanzielleSituationContainer.class, id);
		authorizer.checkReadAuthorization(finanzielleSituation);
		return Optional.ofNullable(finanzielleSituation);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR,  GESUCHSTELLER, STEUERAMT})
	public Collection<FinanzielleSituationContainer> getAllFinanzielleSituationen() {
		Collection<FinanzielleSituationContainer> finanzielleSituationen = criteriaQueryHelper.getAll(FinanzielleSituationContainer.class);
		authorizer.checkReadAuthorization(finanzielleSituationen);
		return new ArrayList<>(finanzielleSituationen);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public void removeFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation) {
		Validate.notNull(finanzielleSituation);
		finanzielleSituation.getGesuchsteller().setFinanzielleSituationContainer(null);
		persistence.merge(finanzielleSituation.getGesuchsteller());
		authorizer.checkWriteAuthorization(finanzielleSituation);
		Optional<FinanzielleSituationContainer> propertyToRemove = findFinanzielleSituation(finanzielleSituation.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFinanzielleSituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, finanzielleSituation));
		persistence.remove(FinanzielleSituationContainer.class, propertyToRemove.get().getId());
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR,  GESUCHSTELLER, STEUERAMT})
	public FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch) {
		authorizer.checkReadAuthorizationFinSit(gesuch);
		return finSitRechner.calculateResultateFinanzielleSituation(gesuch);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR,  GESUCHSTELLER, STEUERAMT})
	public void calculateFinanzDaten(@Nonnull Gesuch gesuch) {
		authorizer.checkReadAuthorizationFinSit(gesuch);
		finSitRechner.calculateFinanzDaten(gesuch);
	}
}
