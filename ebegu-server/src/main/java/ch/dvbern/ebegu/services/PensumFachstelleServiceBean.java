package ch.dvbern.ebegu.services;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

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
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER })
	public PensumFachstelle savePensumFachstelle(@Nonnull PensumFachstelle pensumFachstelle) {
		Objects.requireNonNull(pensumFachstelle);
		return persistence.merge(pensumFachstelle);
	}

	@Override
	@Nonnull
	@PermitAll
	public Optional<PensumFachstelle> findPensumFachstelle(@Nonnull String pensumFachstelleId) {
		Objects.requireNonNull(pensumFachstelleId, "id muss gesetzt sein");
		PensumFachstelle a =  persistence.find(PensumFachstelle.class, pensumFachstelleId);
		return Optional.ofNullable(a);
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER })
	public void removePensumFachstelle(@Nonnull String pensumFachstelleId) {
		Objects.requireNonNull(pensumFachstelleId);
		Optional<PensumFachstelle> pensumFachstelleToRemove = findPensumFachstelle(pensumFachstelleId);
		pensumFachstelleToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removePensumFachstelle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, pensumFachstelleId));
		pensumFachstelleToRemove.ifPresent(pensumFachstelle -> persistence.remove(pensumFachstelle));
	}
}
