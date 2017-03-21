package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.KindDubletteDTO;
import ch.dvbern.ebegu.entities.*;
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
import java.util.*;

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

	@Inject
	private GesuchService gesuchService;


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
		Optional<KindContainer> kindToRemoveOpt = findKind(kindId);
		final KindContainer kindToRemove = kindToRemoveOpt.orElseThrow(() -> new EbeguEntityNotFoundException("removeKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, kindId));
		removeKind(kindToRemove);
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

	@Override
	@Nonnull
	public List<KindDubletteDTO> getKindDubletten(@Nonnull String gesuchId) {
		List<KindDubletteDTO> dublettenOfAllKinder = new ArrayList<>();
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchId);
		if (gesuchOptional.isPresent()) {
			Set<KindContainer> kindContainers = gesuchOptional.get().getKindContainers();
			for (KindContainer kindContainer : kindContainers) {
				List<KindDubletteDTO> kindDubletten = getKindDubletten(kindContainer);
				dublettenOfAllKinder.addAll(kindDubletten);
			}
		}
		return dublettenOfAllKinder;
	}

	private List<KindDubletteDTO> getKindDubletten(@Nonnull KindContainer kindContainer) {
		// Wir suchen nach Name, Vorname und Geburtsdatum
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<KindDubletteDTO> query = cb.createQuery(KindDubletteDTO.class);

		Root<KindContainer> root = query.from(KindContainer.class);
		Join<KindContainer, Kind> joinKind = root.join(KindContainer_.kindJA, JoinType.LEFT);
		Join<KindContainer, Gesuch> joinGesuch = root.join(KindContainer_.gesuch, JoinType.LEFT);

		// Identische Merkmale
		Predicate predicateName = cb.equal(joinKind.get(Kind_.nachname), kindContainer.getKindJA().getNachname());
		Predicate predicateVorname = cb.equal(joinKind.get(Kind_.vorname), kindContainer.getKindJA().getVorname());
		Predicate predicateGeburtsdatum = cb.equal(joinKind.get(Kind_.geburtsdatum), kindContainer.getKindJA().getGeburtsdatum());
		// Aber nicht vom selben Fall
		Predicate predicateOtherFall = cb.notEqual(joinGesuch.get(Gesuch_.fall), kindContainer.getGesuch().getFall());

		query.where(predicateName, predicateVorname, predicateGeburtsdatum, predicateOtherFall);

		CriteriaQuery<KindDubletteDTO> distinct = query.multiselect(
			joinGesuch.get(Gesuch_.id),
			joinGesuch.get(Gesuch_.fall).get(Fall_.fallNummer),
			root.get(KindContainer_.kindNummer)
		).distinct(true);
		return persistence.getCriteriaResults(query);
	}
}
