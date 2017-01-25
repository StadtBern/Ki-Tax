package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Fall_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
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

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private MitteilungService mitteilungService;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA,  GESUCHSTELLER })
	public Fall saveFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall);
		// Den "Besitzer" auf dem Fall ablegen
		if (principalBean.isCallerInRole(UserRole.GESUCHSTELLER)) {
			Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
			currentBenutzer.ifPresent(fall::setBesitzer);
		}
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

	@Override
	@Nonnull
	public Optional<Fall> findFallByCurrentBenutzerAsBesitzer() {
		Optional<Benutzer> currentBenutzerOptional = benutzerService.getCurrentBenutzer();
		if (currentBenutzerOptional.isPresent()) {
			return findFallByBesitzer(currentBenutzerOptional.get());
		}
		return Optional.empty();
	}

	@Override
	@Nonnull
	public Optional<Fall> findFallByBesitzer(Benutzer benutzer) {
		Optional<Benutzer> currentBenutzerOptional = benutzerService.getCurrentBenutzer();
		if (currentBenutzerOptional.isPresent()) {
			Optional<Fall> fallOptional = criteriaQueryHelper.getEntityByUniqueAttribute(Fall.class, benutzer, Fall_.besitzer);
			fallOptional.ifPresent(fall -> authorizer.checkReadAuthorizationFall(fall));
			return fallOptional;
		}
		return Optional.empty();
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
		// Remove all depending objects
		mitteilungService.removeAllMitteilungenForFall(loadedFall);
		//TODO (team) muessten die Gesuche hier auch geloescht werden?
		//Finally remove the Gesuch when all other objects are really removed
		persistence.remove(loadedFall);
	}

	@Override
	public Optional<Fall> createFallForCurrentGesuchstellerAsBesitzer() {
		Optional<Benutzer> currentBenutzerOptional = benutzerService.getCurrentBenutzer();
		if (currentBenutzerOptional.isPresent() && UserRole.GESUCHSTELLER.equals(currentBenutzerOptional.get().getRole())) {
			final Optional<Fall> existingFall = findFallByCurrentBenutzerAsBesitzer();
			if (!existingFall.isPresent()) {
				return Optional.of(saveFall(new Fall()));
			}
		}
		return Optional.empty();
	}
}
