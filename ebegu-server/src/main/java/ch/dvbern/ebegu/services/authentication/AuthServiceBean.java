package ch.dvbern.ebegu.services.authentication;

import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.authentication.AuthLoginElement;
import ch.dvbern.ebegu.authentication.BenutzerCredentials;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer_;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Stateless(name = "AuthService")
@PermitAll
public class AuthServiceBean implements AuthService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthServiceBean.class);

	@PersistenceContext(unitName = "ebeguPersistenceUnit")
	private EntityManager entityManager;
	@Inject
	private BenutzerService benutzerService;


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
		authorisierterBenutzer.setPassword(loginElement.getPlainTextPassword());
		authorisierterBenutzer.setAuthToken(UUID.randomUUID().toString());  //auth token generieren
		entityManager.persist(authorisierterBenutzer);
		return Optional.of(new AuthAccessElement(
			authorisierterBenutzer.getAuthId(),
			authorisierterBenutzer.getAuthToken(),
			UUID.randomUUID().toString(), // XSRF-Token (no need to persist)
			loginElement.getUsername(),
			loginElement.getNachname(),
			loginElement.getVorname(),
			loginElement.getEmail(),
			loginElement.getRole()));
	}

	@Override
	@Nonnull
	public Optional<BenutzerCredentials> loginWithToken(@Nonnull String username, @Nonnull String authToken) {
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(authToken)) {
			return Optional.empty();
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		ParameterExpression<String> usernameParam = cb.parameter(String.class, "username");
		ParameterExpression<String> authTokenParam = cb.parameter(String.class, "authToken");

		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<AuthorisierterBenutzer> root = query.from(AuthorisierterBenutzer.class);
		Join<AuthorisierterBenutzer, Benutzer> join = root.join(AuthorisierterBenutzer_.benutzer, JoinType.INNER);
		Predicate usernamePredicate = cb.equal(join.get(Benutzer_.username), usernameParam);
		Predicate authTokenPredicate = cb.equal(root.get(AuthorisierterBenutzer_.authToken), authTokenParam);
		query.where(usernamePredicate, authTokenPredicate);
		//todo team hier muessen wir einen anderen Weg finden, um das Password zu holen. Dieses gilt nur jetzt bei dummy users und ist gar nicht sicher
		query.select(root.get(AuthorisierterBenutzer_.password));

		try {
			TypedQuery<String> typedQuery = entityManager.createQuery(query)
				.setParameter(usernameParam, username)
				.setParameter(authTokenParam, authToken);
			String passwordEncrypted = typedQuery.getSingleResult();
			BenutzerCredentials credentials = new BenutzerCredentials(username, passwordEncrypted, authToken);
			return Optional.of(credentials);
		} catch (NoResultException ignored) {
			return Optional.empty();
		}
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

		try {
			entityManager.createQuery(delete).executeUpdate();
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	@Override
	public  Optional<String> verifyToken(@Nonnull BenutzerCredentials credentials) {
		Objects.requireNonNull(credentials);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		ParameterExpression<String> usernameParam = cb.parameter(String.class, "username");
		ParameterExpression<String> authTokenParam = cb.parameter(String.class, "authToken");

		CriteriaQuery<AuthorisierterBenutzer> query = cb.createQuery(AuthorisierterBenutzer.class);
		Root<AuthorisierterBenutzer> root = query.from(AuthorisierterBenutzer.class);
		Join<AuthorisierterBenutzer, Benutzer> join = root.join(AuthorisierterBenutzer_.benutzer, JoinType.INNER);
		Predicate usernamePredicate = cb.equal(join.get(Benutzer_.username), usernameParam);
		Predicate authTokenPredicate = cb.equal(root.get(AuthorisierterBenutzer_.authToken), authTokenParam);
		query.where(cb.and(usernamePredicate, authTokenPredicate));

		try {
			TypedQuery<AuthorisierterBenutzer> tq = entityManager.createQuery(query)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.setParameter(usernameParam, credentials.getUsername())
				.setParameter(authTokenParam, credentials.getAuthToken());

			AuthorisierterBenutzer authUser = tq.getSingleResult();

			LocalDateTime now = LocalDateTime.now();
			LocalDateTime maxDateFromNow = now.minus(Constants.LOGIN_TIMEOUT_SECONDS, ChronoUnit.SECONDS);
			if (authUser.getLastLogin().isBefore(maxDateFromNow)) {
				LOG.debug("Token is no longer valid: " +credentials.getAuthToken());
				return Optional.empty();
			}
			authUser.setLastLogin(now);
			entityManager.persist(authUser);
			entityManager.flush();
			LOG.trace("Valid auth Token was refreshed ");
			return Optional.of(authUser.getId());

		} catch (NoResultException ignored) {
			LOG.debug("Could not load Authorisierterbenutzer for username '" + credentials.getUsername() + "' and token '" +
			credentials.getAuthToken() +"'");
			return Optional.empty();
		}
	}

}
