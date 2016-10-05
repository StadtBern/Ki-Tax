package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer {@link ErwerbspensumContainer} diese beinhalten einzelne Objekte mit den Daten von GS und JA
 */
@Stateless
@Local(ErwerbspensumService.class)
public class ErwerbspensumServiceBean extends AbstractBaseService implements ErwerbspensumService {

	@Inject
	private Persistence<ErwerbspensumContainer> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private WizardStepService wizardStepService;

	@Nonnull
	@Override
	public ErwerbspensumContainer saveErwerbspensum(@Valid @Nonnull ErwerbspensumContainer erwerbspensumContainer, Gesuch gesuch) {
		Objects.requireNonNull(erwerbspensumContainer);
		final ErwerbspensumContainer mergedErwerbspensum = persistence.merge(erwerbspensumContainer);
		wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.ERWERBSPENSUM);
		return mergedErwerbspensum;
	}

	@Nonnull
	@Override
	public Optional<ErwerbspensumContainer> findErwerbspensum(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		ErwerbspensumContainer ewpCnt =  persistence.find(ErwerbspensumContainer.class, key);
		return Optional.ofNullable(ewpCnt);
	}

	@Override
	public Collection<ErwerbspensumContainer> findErwerbspensenForGesuchsteller(@Nonnull Gesuchsteller gesuchsteller) {
		return criteriaQueryHelper.getEntitiesByAttribute(ErwerbspensumContainer.class, gesuchsteller, ErwerbspensumContainer_.gesuchsteller);
	}


	@Override
	@Nonnull
	public Collection<ErwerbspensumContainer> findErwerbspensenFromGesuch(@Nonnull String gesuchId) {
		Collection<ErwerbspensumContainer> result = new ArrayList<>();
		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId);
		if (gesuch.isPresent()) {
			if (gesuch.get().getGesuchsteller1() != null) {
				result.addAll(findErwerbspensenForGesuchsteller(gesuch.get().getGesuchsteller1()));
			}
			if (gesuch.get().getGesuchsteller2() != null) {
				result.addAll(findErwerbspensenForGesuchsteller(gesuch.get().getGesuchsteller2()));
			}
		}
		return result;
	}

	@Nonnull
	@Override
	public Collection<ErwerbspensumContainer> getAllErwerbspensenenContainer() {
		return criteriaQueryHelper.getAll(ErwerbspensumContainer.class);
	}

	@Override
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
