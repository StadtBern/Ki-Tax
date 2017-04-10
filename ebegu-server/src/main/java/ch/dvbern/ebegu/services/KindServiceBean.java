package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.KindDubletteDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer Kind
 */
@Stateless
@Local(KindService.class)
@PermitAll
public class KindServiceBean extends AbstractBaseService implements KindService {

	@Inject
	private Persistence<KindContainer> persistence;
	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private GesuchService gesuchService;


	@Nonnull
	@Override
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public KindContainer saveKind(@Nonnull KindContainer kind) {
		Objects.requireNonNull(kind);
		final KindContainer mergedKind = persistence.merge(kind);
		wizardStepService.updateSteps(kind.getGesuch().getId(), null, null, WizardStepName.KINDER);
		return mergedKind;
	}

	@Override
	@Nonnull
	@PermitAll
	public Optional<KindContainer> findKind(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		KindContainer a =  persistence.find(KindContainer.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@PermitAll
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
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public void removeKind(@Nonnull String kindId) {
		Objects.requireNonNull(kindId);
		Optional<KindContainer> kindToRemoveOpt = findKind(kindId);
		final KindContainer kindToRemove = kindToRemoveOpt.orElseThrow(() -> new EbeguEntityNotFoundException("removeKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, kindId));
		removeKind(kindToRemove);
	}

	@Override
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER})
	public void removeKind(@Nonnull KindContainer kind) {
		final String gesuchId = kind.getGesuch().getId();
		persistence.remove(kind);
		wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.KINDER);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, REVISOR})
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
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR})
	public List<KindDubletteDTO> getKindDubletten(@Nonnull String gesuchId) {
		List<KindDubletteDTO> dublettenOfAllKinder = new ArrayList<>();
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchId);
		if (gesuchOptional.isPresent()) {
			// Nur das zuletzt gueltige Gesuch
			List<String> idsOfLetztFreigegebeneAntraege = gesuchService.getNeuesteFreigegebeneAntraege(gesuchOptional.get().getGesuchsperiode());
			Set<KindContainer> kindContainers = gesuchOptional.get().getKindContainers();
			for (KindContainer kindContainer : kindContainers) {
				List<KindDubletteDTO> kindDubletten = getKindDubletten(kindContainer, idsOfLetztFreigegebeneAntraege);
				dublettenOfAllKinder.addAll(kindDubletten);
			}
		}
		else {
			throw new EbeguEntityNotFoundException("getKindDubletten", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchId);
		}
		return dublettenOfAllKinder;
	}

	@Nonnull
	private List<KindDubletteDTO> getKindDubletten(@Nonnull KindContainer kindContainer, List<String> idsOfLetztFreigegebeneAntraege) {
		// Wir suchen nach Name, Vorname und Geburtsdatum
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<KindDubletteDTO> query = cb.createQuery(KindDubletteDTO.class);

		Root<KindContainer> root = query.from(KindContainer.class);
		Join<KindContainer, Kind> joinKind = root.join(KindContainer_.kindJA, JoinType.LEFT);
		Join<KindContainer, Gesuch> joinGesuch = root.join(KindContainer_.gesuch, JoinType.LEFT);

		query.multiselect(
			joinGesuch.get(Gesuch_.id),
			joinGesuch.get(Gesuch_.fall).get(Fall_.fallNummer),
			cb.literal(kindContainer.getKindNummer()),
			root.get(KindContainer_.kindNummer)
		).distinct(true);

		// Identische Merkmale
		Predicate predicateName = cb.equal(joinKind.get(Kind_.nachname), kindContainer.getKindJA().getNachname());
		Predicate predicateVorname = cb.equal(joinKind.get(Kind_.vorname), kindContainer.getKindJA().getVorname());
		Predicate predicateGeburtsdatum = cb.equal(joinKind.get(Kind_.geburtsdatum), kindContainer.getKindJA().getGeburtsdatum());
		// Aber nicht vom selben Fall
		Predicate predicateOtherFall = cb.notEqual(joinGesuch.get(Gesuch_.fall), kindContainer.getGesuch().getFall());
		// Nur das zuletzt gueltige Gesuch
		Predicate predicateAktuellesGesuch = joinGesuch.get(Gesuch_.id).in(idsOfLetztFreigegebeneAntraege);

		query.where(predicateName, predicateVorname, predicateGeburtsdatum, predicateOtherFall, predicateAktuellesGesuch);

		return persistence.getCriteriaResults(query);
	}
}
