package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer AntragStatusHistory
 */
@Stateless
@Local(AntragStatusHistoryService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR,  SCHULAMT,  STEUERAMT, GESUCHSTELLER})
public class AntragStatusHistoryServiceBean extends AbstractBaseService implements AntragStatusHistoryService {

	@Inject
	private Persistence<AntragStatusHistory> persistence;

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public AntragStatusHistory saveStatusChange(@Nonnull Gesuch gesuch, @Nullable Benutzer saveAsUser) {
		Objects.requireNonNull(gesuch);

		Benutzer userToSet = saveAsUser;
		if (userToSet == null) {
			Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
			if (currentBenutzer.isPresent()) {
				userToSet = currentBenutzer.get();
			}
		}
		if (userToSet != null) {
			// Den letzten Eintrag beenden, falls es schon einen gab
			AntragStatusHistory lastStatusChange = findLastStatusChange(gesuch);
			if (lastStatusChange != null) {
				lastStatusChange.setTimestampBis(LocalDateTime.now());
			}
			// Und den neuen speichern
			final AntragStatusHistory newStatusHistory = new AntragStatusHistory();
			newStatusHistory.setStatus(gesuch.getStatus());
			newStatusHistory.setGesuch(gesuch);
			newStatusHistory.setTimestampVon(LocalDateTime.now());
			newStatusHistory.setBenutzer(userToSet);

			return persistence.persist(newStatusHistory);
		}
		throw new EbeguEntityNotFoundException("saveStatusChange", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "No current Benutzer");
	}

	@Override
	@Nullable
	@PermitAll
	public AntragStatusHistory findLastStatusChange(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		try {
			final CriteriaQuery<AntragStatusHistory> query = createQueryAllAntragStatusHistoryProGesuch(gesuch);

			AntragStatusHistory result = persistence.getEntityManager().createQuery(query).setFirstResult(0).setMaxResults(1).getSingleResult();
			authorizer.checkReadAuthorization(result.getGesuch());
			return result;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public void removeAllAntragStatusHistoryFromGesuch(Gesuch gesuch) {
		Collection<AntragStatusHistory> antragStatusHistoryFromGesuch = findAllAntragStatusHistoryByGesuch(gesuch);
		for (AntragStatusHistory antragStatusHistory : antragStatusHistoryFromGesuch) {
			persistence.remove(AntragStatusHistory.class, antragStatusHistory.getId());
		}
	}

	@Override
	@Nonnull
	public Collection<AntragStatusHistory> findAllAntragStatusHistoryByGesuch(@Nonnull Gesuch gesuch) {
		authorizer.checkReadAuthorization(gesuch);
		Objects.requireNonNull(gesuch);
		return criteriaQueryHelper.getEntitiesByAttribute(AntragStatusHistory.class, gesuch, AntragStatusHistory_.gesuch);
	}

	@Override
	@Nonnull
	public Collection<AntragStatusHistory> findAllAntragStatusHistoryByGPFall(@Nonnull Gesuchsperiode gesuchsperiode, Fall fall) {
		Objects.requireNonNull(gesuchsperiode);
		Objects.requireNonNull(fall);
		authorizer.checkReadAuthorizationFall(fall);

		Benutzer user = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("searchAntraege", "No User is logged in"));
		UserRole role = user.getRole();

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<AntragStatusHistory> query = cb.createQuery(AntragStatusHistory.class);
		Set<AntragStatus> antragStatuses = AntragStatus.allowedforRole(role);

		Root<AntragStatusHistory> root = query.from(AntragStatusHistory.class);
		Predicate fallPredicate = cb.equal(root.get(AntragStatusHistory_.gesuch).get(Gesuch_.fall), fall);
		Predicate gesuchsperiodePredicate = cb.equal(root.get(AntragStatusHistory_.gesuch).get(Gesuch_.gesuchsperiode), gesuchsperiode);
		Predicate rolePredicate = root.get(AntragStatusHistory_.gesuch).get(Gesuch_.status).in(antragStatuses);
		query.where(fallPredicate, gesuchsperiodePredicate, rolePredicate);
		query.orderBy(cb.desc(root.get(AntragStatusHistory_.timestampErstellt)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	public AntragStatusHistory findLastStatusChangeBeforeBeschwerde(Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		authorizer.checkReadAuthorization(gesuch);
		final CriteriaQuery<AntragStatusHistory> query = createQueryAllAntragStatusHistoryProGesuch(gesuch);

		final List<AntragStatusHistory> lastTwoChanges = persistence.getEntityManager().createQuery(query).setMaxResults(2).getResultList();
		if (lastTwoChanges.size() < 2 || !AntragStatus.BESCHWERDE_HAENGIG.equals(lastTwoChanges.get(0).getStatus())) {
			throw new EbeguRuntimeException("findLastStatusChangeBeforeBeschwerde", ErrorCodeEnum.ERROR_NOT_FROM_STATUS_BESCHWERDE, gesuch.getId());
		}
		return lastTwoChanges.get(1); // returns the previous status before Beschwerde_Haengig
	}

	/**
	 * Gibt alle AntragStatusHistory des gegebenen Gesuchs zurueck. Sortiert nach timestampVon DESC
	 */
	@Nonnull
	private CriteriaQuery<AntragStatusHistory> createQueryAllAntragStatusHistoryProGesuch(Gesuch gesuch) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<AntragStatusHistory> query = cb.createQuery(AntragStatusHistory.class);
		Root<AntragStatusHistory> root = query.from(AntragStatusHistory.class);

		Predicate predicateInstitution = cb.equal(root.get(AntragStatusHistory_.gesuch).get(Gesuch_.id), gesuch.getId());

		query.where(predicateInstitution);
		query.orderBy(cb.desc(root.get(AntragStatusHistory_.timestampVon)));
		return query;
	}

}
