package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer AntragStatusHistory
 */
@Stateless
@Local(AntragStatusHistoryService.class)
public class AntragStatusHistoryServiceBean extends AbstractBaseService implements AntragStatusHistoryService {

	@Inject
	private Persistence<AntragStatusHistory> persistence;
	@Inject
	private BenutzerService benutzerService;


	@Nonnull
	@Override
	public AntragStatusHistory saveStatusChange(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);

		Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
		if (currentBenutzer.isPresent()) {
			final AntragStatusHistory newStatusHistory = new AntragStatusHistory();
			newStatusHistory.setStatus(gesuch.getStatus());
			newStatusHistory.setGesuch(gesuch);
			newStatusHistory.setDatum(LocalDateTime.now());
			newStatusHistory.setBenutzer(currentBenutzer.get());

			return persistence.persist(newStatusHistory);
		}
		throw new EbeguEntityNotFoundException("saveStatusChange", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "No current Benutzer");
	}

	@Override
	@Nullable
	public AntragStatusHistory findLastStatusChange(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		try {
			final CriteriaBuilder cb = persistence.getCriteriaBuilder();
			final CriteriaQuery<AntragStatusHistory> query = cb.createQuery(AntragStatusHistory.class);
			Root<AntragStatusHistory> root = query.from(AntragStatusHistory.class);

			Predicate predicateInstitution = cb.equal(root.get(AntragStatusHistory_.gesuch).get(Gesuch_.id), gesuch.getId());

			query.where(predicateInstitution);
			query.orderBy(cb.desc(root.get(AntragStatusHistory_.datum)));

			return persistence.getEntityManager().createQuery(query).setFirstResult(0).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
