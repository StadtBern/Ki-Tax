package ch.dvbern.ebegu.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.entities.Traegerschaft_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Traegerschaft
 */
@Stateless
@Local(TraegerschaftService.class)
public class TraegerschaftServiceBean extends AbstractBaseService implements TraegerschaftService {

	@Inject
	private Persistence<Traegerschaft> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	@RolesAllowed({ADMIN, SUPER_ADMIN})
	public Traegerschaft saveTraegerschaft(@Nonnull Traegerschaft traegerschaft) {
		Objects.requireNonNull(traegerschaft);
		return persistence.merge(traegerschaft);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<Traegerschaft> findTraegerschaft(@Nonnull final String traegerschaftId) {
		Objects.requireNonNull(traegerschaftId, "id muss gesetzt sein");
		Traegerschaft a = persistence.find(Traegerschaft.class, traegerschaftId);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Traegerschaft> getAllActiveTraegerschaften() {
		return criteriaQueryHelper.getEntitiesByAttribute(Traegerschaft.class, true, Traegerschaft_.active);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Traegerschaft> getAllTraegerschaften() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Traegerschaft.class));
	}

	@Override
	@RolesAllowed({ADMIN, SUPER_ADMIN})
	public void removeTraegerschaft(@Nonnull String traegerschaftId) {
		Validate.notNull(traegerschaftId);
		Optional<Traegerschaft> traegerschaftToRemove = findTraegerschaft(traegerschaftId);
		traegerschaftToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeTraegerschaft", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, traegerschaftId));
		traegerschaftToRemove.ifPresent(traegerschaft -> persistence.remove(traegerschaft));
	}

	@Override
	@RolesAllowed({ADMIN, SUPER_ADMIN})
	public void setInactive(@Nonnull String traegerschaftId) {
		Validate.notNull(traegerschaftId);
		Optional<Traegerschaft> traegerschaftOptional = findTraegerschaft(traegerschaftId);
		Traegerschaft traegerschaft = traegerschaftOptional.orElseThrow(() -> new EbeguEntityNotFoundException("setInactive", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, traegerschaftId));
		traegerschaft.setActive(false);
		persistence.merge(traegerschaft);
	}
}
