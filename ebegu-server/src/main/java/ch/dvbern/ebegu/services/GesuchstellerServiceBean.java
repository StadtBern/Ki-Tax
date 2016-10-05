package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
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
 * Service fuer Gesuchsteller
 */
@Stateless
@Local(GesuchstellerService.class)
public class GesuchstellerServiceBean extends AbstractBaseService implements GesuchstellerService {

	@Inject
	private Persistence<Gesuchsteller> persistence;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public Gesuchsteller saveGesuchsteller(@Nonnull Gesuchsteller gesuchsteller, final Gesuch gesuch, Integer gsNumber) {
		Objects.requireNonNull(gesuchsteller);

		final Gesuchsteller mergedGesuchsteller = persistence.merge(gesuchsteller);

		if ((gesuch.getFamiliensituation().hasSecondGesuchsteller() && gsNumber == 2)
			|| (!gesuch.getFamiliensituation().hasSecondGesuchsteller() && gsNumber == 1)) {
			wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.GESUCHSTELLER);
		}

		return mergedGesuchsteller;
	}

	@Nonnull
	@Override
	public Optional<Gesuchsteller> findGesuchsteller(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Gesuchsteller a =  persistence.find(Gesuchsteller.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<Gesuchsteller> getAllGesuchsteller() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Gesuchsteller.class));
	}

	@Override
	public void removeGesuchsteller(@Nonnull Gesuchsteller gesuchsteller) {
		Validate.notNull(gesuchsteller);
		Optional<Gesuchsteller> gesuchstellerToRemove = findGesuchsteller(gesuchsteller.getId());
		gesuchstellerToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeGesuchsteller", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsteller));
		persistence.remove(gesuchstellerToRemove.get());
	}
}
