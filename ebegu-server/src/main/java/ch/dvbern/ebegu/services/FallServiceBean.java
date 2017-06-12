package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
	private GesuchService gesuchService;

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
	public Optional<Fall> findFallByBesitzer(@Nullable Benutzer benutzer) {
		Optional<Fall> fallOptional = criteriaQueryHelper.getEntityByUniqueAttribute(Fall.class, benutzer, Fall_.besitzer);
		fallOptional.ifPresent(fall -> authorizer.checkReadAuthorizationFall(fall));
		return fallOptional;
	}

	@Nonnull
	@Override
	public Collection<Fall> getAllFalle(boolean doAuthCheck) {
		List<Fall> faelle = new ArrayList<>(criteriaQueryHelper.getAll(Fall.class));
		if (doAuthCheck) {
			authorizer.checkReadAuthorizationFaelle(faelle);
		}
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
		// Alle Gesuche des Falls ebenfalls loeschen
		final List<String> allGesucheForFall = gesuchService.getAllGesuchIDsForFall(loadedFall.getId());
		allGesucheForFall
			.forEach(gesuchId -> gesuchService.findGesuch(gesuchId)
				.ifPresent((gesuch) -> {
					gesuchService.removeGesuch(gesuch.getId());
				}));
		//Finally remove the Fall when all other objects are really removed
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


	@Override
	public Optional<String> getCurrentEmailAddress(String fallID){
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();

		final CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Gesuch> root = query.from(Gesuch.class);
		ParameterExpression<String> fallIdParam = cb.parameter(String.class, "fallId");
		Join<Gesuch, Fall> fallJoin = root.join(Gesuch_.fall, JoinType.LEFT);
		Join<Gesuch, GesuchstellerContainer> gesuchstellerJoin = root.join(Gesuch_.gesuchsteller1, JoinType.LEFT);
		Join<GesuchstellerContainer, Gesuchsteller> gesDataJoin = gesuchstellerJoin.join(GesuchstellerContainer_.gesuchstellerJA, JoinType.LEFT);
		Predicate gesuchOfFall = cb.equal(fallJoin.get(Fall_.id), fallIdParam);
		Path<String> gsEmail = gesDataJoin.get(Gesuchsteller_.mail);
		query.select(gsEmail);
		query.where(gesuchOfFall);
		query.orderBy(cb.desc(root.get(Gesuch_.timestampErstellt))); // Das mit dem neuesten Verfuegungsdatum
		TypedQuery<String> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setParameter(fallIdParam, fallID);
		typedQuery.setMaxResults(1);

		List<String> criteriaResults = typedQuery.getResultList();

		String emailToReturn = null;
		if(!criteriaResults.isEmpty()){
			if (criteriaResults.size() != 1) {
				throw new EbeguRuntimeException("getEmailAddressForFall", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, criteriaResults.size());
			} else{
				String gesuchstellerEmail = criteriaResults.get(0);
				emailToReturn = gesuchstellerEmail;
			}
		}
		if (emailToReturn == null) {
			emailToReturn = readBesitzerEmailForFall(fallID);

		}
		return Optional.ofNullable(emailToReturn);

	}

	private String readBesitzerEmailForFall(String fallID) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Fall> root = query.from(Fall.class);
		Join<Fall, Benutzer> benutzerJoin = root.join(Fall_.besitzer, JoinType.LEFT);
		ParameterExpression<String> fallIdParam = cb.parameter(String.class, "fallId");
		Predicate gesuchOfFall = cb.equal(root.get(Fall_.id), fallIdParam);
		Path<String> benutzerEmail = benutzerJoin.get(Benutzer_.email);
		query.select(benutzerEmail);
		query.where(gesuchOfFall);
		TypedQuery<String> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setParameter(fallIdParam, fallID);

		return typedQuery.getSingleResult();
	}
}
