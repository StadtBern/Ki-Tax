package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

}
