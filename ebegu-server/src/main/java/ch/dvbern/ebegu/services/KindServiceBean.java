package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.KindContainer_;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Kind
 */
@Stateless
@Local(KindService.class)
public class KindServiceBean extends AbstractBaseService implements KindService {

	@Inject
	private Persistence<KindContainer> persistence;
	@Inject
	private WizardStepService wizardStepService;


	@Nonnull
	@Override
	public KindContainer saveKind(@Nonnull KindContainer kind) {
		Objects.requireNonNull(kind);
		final KindContainer mergedKind = persistence.merge(kind);
		wizardStepService.updateSteps(kind.getGesuch().getId(), null, null, WizardStepName.KINDER);
		return mergedKind;
	}

	@Override
	@Nonnull
	public Optional<KindContainer> findKind(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		KindContainer a =  persistence.find(KindContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public List<KindContainer> findAllKinderFromGesuch(@Nonnull String gesuchId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<KindContainer> query = cb.createQuery(KindContainer.class);
		Root<KindContainer> root = query.from(KindContainer.class);
		// Kinder from Gesuch
		Predicate predicateInstitution = root.get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchId);

		query.where(predicateInstitution);
		return persistence.getCriteriaResults(query);
	}

	@Override
	public void removeKind(@Nonnull String kindId) {
		Objects.requireNonNull(kindId);
		Optional<KindContainer> kindToRemove = findKind(kindId);
		kindToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, kindId));
		removeKind(kindToRemove.get());
	}

	@Override
	public void removeKind(@Nonnull KindContainer kind) {
		final String gesuchId = kind.getGesuch().getId();
		persistence.remove(kind);
		wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.KINDER);
	}

	@Override
	@Nonnull
	public List<KindContainer> getAllKinderWithMissingStatistics() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<KindContainer> query = cb.createQuery(KindContainer.class);

		Root<KindContainer> root = query.from(KindContainer.class);
		Join<KindContainer, Gesuch> joinGesuch = root.join(KindContainer_.gesuch, JoinType.LEFT);


		Predicate predicateMutation = cb.equal(joinGesuch.get(Gesuch_.typ), AntragTyp.MUTATION);
		Predicate predicateFlag = cb.isNull(root.get(KindContainer_.kindMutiert));
		Predicate predicateStatus = joinGesuch.get(Gesuch_.status).in(AntragStatus.getAllVerfuegtStates());

		query.where(predicateMutation, predicateFlag, predicateStatus);
		query.orderBy(cb.desc(joinGesuch.get(Gesuch_.laufnummer)));
		return persistence.getCriteriaResults(query);
	}
}
