package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.DokumentGrund_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Kind
 */
@Stateless
@Local(DokumentGrundService.class)
@PermitAll
public class DokumentGrundServiceBean extends AbstractBaseService implements DokumentGrundService {

	@Inject
	private Persistence<DokumentGrund> persistence;
	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;
	@Inject
	private Authorizer authorizer;


	@Nonnull
	@Override
	public DokumentGrund saveDokumentGrund(@Nonnull DokumentGrund dokumentGrund) {
		Objects.requireNonNull(dokumentGrund);
		final DokumentGrund mergedDokumentGrund = persistence.merge(dokumentGrund);
		wizardStepService.updateSteps(mergedDokumentGrund.getGesuch().getId(), null, null, WizardStepName.DOKUMENTE);
		return mergedDokumentGrund;
	}

	@Override
	@Nonnull
	public Optional<DokumentGrund> findDokumentGrund(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		DokumentGrund a = persistence.find(DokumentGrund.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<DokumentGrund> findAllDokumentGrundByGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		this.authorizer.checkReadAuthorization(gesuch);
		return criteriaQueryHelper.getEntitiesByAttribute(DokumentGrund.class, gesuch, DokumentGrund_.gesuch);
	}

	@Override
	@Nonnull
	public Collection<DokumentGrund> findAllDokumentGrundByGesuchAndDokumentType(@Nonnull Gesuch gesuch, @Nonnull DokumentGrundTyp dokumentGrundTyp) {
		Objects.requireNonNull(gesuch);

		this.authorizer.checkReadAuthorization(gesuch);
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<DokumentGrund> query = cb.createQuery(DokumentGrund.class);

		Root<DokumentGrund> root = query.from(DokumentGrund.class);

		Predicate predicateGesuch = cb.equal(root.get(DokumentGrund_.gesuch), gesuch);
		Predicate predicateDokumentGrundTyp = cb.equal(root.get(DokumentGrund_.dokumentGrundTyp), dokumentGrundTyp);

		query.where(predicateGesuch, predicateDokumentGrundTyp);
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nullable
	public DokumentGrund updateDokumentGrund(@Nonnull DokumentGrund dokumentGrund) {
		Objects.requireNonNull(dokumentGrund);

		//Wenn DokumentGrund keine Dokumente mehr hat und nicht gebraucht wird, wird er entfernt
		if (!dokumentGrund.isNeeded() && (dokumentGrund.getDokumente() == null || dokumentGrund.getDokumente().isEmpty())) {
			persistence.remove(dokumentGrund);
			return null;
		}
		final DokumentGrund mergedDokument = persistence.merge(dokumentGrund);
		wizardStepService.updateSteps(mergedDokument.getGesuch().getId(), null, null, WizardStepName.DOKUMENTE);
		return mergedDokument;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN,})
	public void removeAllDokumentGrundeFromGesuch(Gesuch gesuch) {
		Collection<DokumentGrund> dokumentsFromGesuch = findAllDokumentGrundByGesuch(gesuch);
		for (DokumentGrund dokument : dokumentsFromGesuch) {
			persistence.remove(DokumentGrund.class, dokument.getId());
		}
	}

}
