package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Fall_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer Fall
 */
@Stateless
@Local(FallService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT, SCHULAMT})
public class FallServiceBean extends AbstractBaseService implements FallService {

	@Inject
	private Persistence<Fall> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private Authorizer authorizer;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA,  GESUCHSTELLER })
	public Fall saveFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall);
		authorizer.checkWriteAuthorization(fall);
		return persistence.merge(fall);
	}

	@Nonnull
	@Override
	public Optional<Fall> findFall(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Fall a =  persistence.find(Fall.class, key);
		if (a != null) {
			authorizer.checkReadAuthorizationFall(a);
		}
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Optional<Fall> findFallByNumber(@Nonnull Long fallnummer) {
		Objects.requireNonNull(fallnummer, "fallnummer muss gesetzt sein");
		Optional<Fall> fallOptional = criteriaQueryHelper.getEntityByUniqueAttribute(Fall.class, fallnummer, Fall_.fallNummer);
		fallOptional.ifPresent(fall -> authorizer.checkReadAuthorizationFall(fall));
		return fallOptional;
	}

	@Nonnull
	@Override
	public Collection<Fall> getAllFalle() {
		List<Fall> faelle = new ArrayList<>(criteriaQueryHelper.getAll(Fall.class));
		authorizer.checkReadAuthorizationFaelle(faelle);
		return faelle;
	}

	@Override
	public void removeFall(@Nonnull Fall fall) {
		Validate.notNull(fall);
		Optional<Fall> fallToRemove = findFall(fall.getId());
		Fall loadedFall = fallToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fall));
		authorizer.checkWriteAuthorization(loadedFall);
		persistence.remove(loadedFall);
	}
}
