package ch.dvbern.ebegu.services.authentication;

import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.authentication.AuthLoginElement;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer_;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

//Berechtigungen: PermitAll weggenommen weil wir das ohne user aus dem loginmodul aufrufen muessen, sonst wird anonymous genommen und man hat 2 principals nach dem loginmodul
@SuppressWarnings("OverlyBroadCatchBlock")
@Stateless(name = "AuthService")
public class AuthServiceBean implements AuthService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthServiceBean.class);

	@PersistenceContext(unitName = "ebeguPersistenceUnit")
	private EntityManager entityManager;
	@Inject
	private BenutzerService benutzerService;

	@Resource(lookup = "java:jboss/infinispan/container/ebeguCache")
	private CacheContainer cacheContainer;


	@Nonnull
	@Override
	public Optional<AuthAccessElement> login(@Nonnull AuthLoginElement loginElement) {
		Objects.requireNonNull(loginElement);

		if (StringUtils.isEmpty(loginElement.getUsername()) || StringUtils.isEmpty(loginElement.getPlainTextPassword())) {
			return Optional.empty();
		}

		//Benutzer muss in jedem Fall bekannt sein (wird bei erfolgreichem container login angelegt)
		Optional<Benutzer> benutzer = benutzerService.findBenutzer(loginElement.getUsername());
		if (!benutzer.isPresent()) {
			return Optional.empty();
		}

		AuthorisierterBenutzer authorisierterBenutzer = new AuthorisierterBenutzer();
		authorisierterBenutzer.setBenutzer(benutzer.get());

		authorisierterBenutzer.setAuthToken(UUID.randomUUID().toString());  //auth token generieren
		authorisierterBenutzer.setUsername(loginElement.getUsername());
		authorisierterBenutzer.setRole(loginElement.getRole()); // hier kommt rolle aus property file
		entityManager.persist(authorisierterBenutzer);
		return Optional.of(new AuthAccessElement(
			authorisierterBenutzer.getUsername(),
			authorisierterBenutzer.getAuthToken(),
			UUID.randomUUID().toString(), // XSRF-Token (no need to persist)
			loginElement.getNachname(),
			loginElement.getVorname(),
			loginElement.getEmail(),
			loginElement.getRole()));
	}

	@Override
	public boolean logout(@Nonnull String authToken) {
		if (StringUtils.isEmpty(authToken)) {
			return false;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<AuthorisierterBenutzer> delete = criteriaBuilder.createCriteriaDelete(AuthorisierterBenutzer.class);
		Root<AuthorisierterBenutzer> root = delete.from(AuthorisierterBenutzer.class);
		Predicate authTokenPredicate = criteriaBuilder.equal(root.get(AuthorisierterBenutzer_.authToken), authToken);
		delete.where(criteriaBuilder.and(authTokenPredicate));
		cacheContainer.getCache().remove(authToken);
		try {
			entityManager.createQuery(delete).executeUpdate();
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}



	@Override
	public AuthAccessElement createLoginFromIAM(AuthorisierterBenutzer authorisierterBenutzer) {
		try {
			entityManager.persist(authorisierterBenutzer);
		} catch (RuntimeException ex) {
			LOG.error("Could not create Login from IAM for user " + authorisierterBenutzer);
			throw ex;
		}
		Benutzer existingUser = authorisierterBenutzer.getBenutzer();
		return new AuthAccessElement(
			authorisierterBenutzer.getUsername(),
			authorisierterBenutzer.getAuthToken(),
			UUID.randomUUID().toString(), // XSRF-Token (no need to persist)
			existingUser.getNachname(),
			existingUser.getVorname(),
			existingUser.getEmail(),
			existingUser.getRole());
	}

	@Override
	public Optional<AuthorisierterBenutzer> validateAndRefreshLoginToken(String token, boolean doRefreshToken) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			ParameterExpression<String> authTokenParam = cb.parameter(String.class, "authToken");

			CriteriaQuery<AuthorisierterBenutzer> query = cb.createQuery(AuthorisierterBenutzer.class);
			Root<AuthorisierterBenutzer> root = query.from(AuthorisierterBenutzer.class);
			Predicate authTokenPredicate = cb.equal(root.get(AuthorisierterBenutzer_.authToken), authTokenParam);
			query.where(authTokenPredicate);

			try {
				TypedQuery<AuthorisierterBenutzer> tq = entityManager.createQuery(query)
					.setLockMode(LockModeType.PESSIMISTIC_WRITE)
					.setParameter(authTokenParam, token);

				AuthorisierterBenutzer authUser = tq.getSingleResult();

				// Das Login verlaengern, falls es sich nicht um einen Timer handelt
				if (doRefreshToken) {
					LocalDateTime now = LocalDateTime.now();
					LocalDateTime maxDateFromNow = now.minus(Constants.LOGIN_TIMEOUT_SECONDS, ChronoUnit.SECONDS);
					if (authUser.getLastLogin().isBefore(maxDateFromNow)) {
						LOG.debug("Token is no longer valid: " + token);
						return Optional.empty();
					}
					authUser.setLastLogin(now);
					entityManager.persist(authUser);
					entityManager.flush();
					LOG.trace("Valid auth Token was refreshed ");
				}
				return Optional.of(authUser);

			} catch (NoResultException ignored) {
				LOG.debug("Could not load Authorisierterbenutzer for token '" +
					token + "'");
				return Optional.empty();
			}
	}
}
