package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer pensumFachstelle
 */
@Stateless
@Local(PensumFachstelleService.class)
public class PensumFachstelleServiceBean extends AbstractBaseService implements PensumFachstelleService {

	@Inject
	private Persistence<PensumFachstelle> persistence;

	@Override
	@Nonnull
	public PensumFachstelle savePensumFachstelle(@Nonnull PensumFachstelle pensumFachstelle) {
		Objects.requireNonNull(pensumFachstelle);
		return persistence.merge(pensumFachstelle);
	}

	@Override
	@Nonnull
	public Optional<PensumFachstelle> findPensumFachstelle(@Nonnull String pensumFachstelleId) {
		Objects.requireNonNull(pensumFachstelleId, "id muss gesetzt sein");
		PensumFachstelle a =  persistence.find(PensumFachstelle.class, pensumFachstelleId);
		return Optional.ofNullable(a);
	}

	@Override
	public void removePensumFachstelle(@Nonnull String pensumFachstelleId) {
		Objects.requireNonNull(pensumFachstelleId);
		Optional<PensumFachstelle> pensumFachstelleToRemove = findPensumFachstelle(pensumFachstelleId);
		pensumFachstelleToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removePensumFachstelle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, pensumFachstelleId));
		persistence.remove(pensumFachstelleToRemove.get());
	}
}
