package ch.dvbern.ebegu.services.authentication;

import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.authentication.AuthLoginElement;
import ch.dvbern.ebegu.authentication.BenutzerCredentials;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer_;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
@Stateless(name = "AuthService")
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
			authorisierterBenutzer.getUsername(),
			authorisierterBenutzer.getAuthToken(),
			UUID.randomUUID().toString(), // XSRF-Token (no need to persist)
			loginElement.getNachname(),
			loginElement.getVorname(),
			loginElement.getEmail(),
			loginElement.getRole()));
	}

	@Override
	@Nonnull
	public Optional<BenutzerCredentials> getCredentialsForAuthorizedToken(@Nonnull String authToken) {
		if (StringUtils.isEmpty(authToken)) {
			return Optional.empty();
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		ParameterExpression<String> authTokenParam = cb.parameter(String.class, "authToken");

		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<AuthorisierterBenutzer> root = query.from(AuthorisierterBenutzer.class);

		Predicate authTokenPredicate = cb.equal(root.get(AuthorisierterBenutzer_.authToken), authTokenParam);
		query.where(authTokenPredicate);
		query.select(root.get(AuthorisierterBenutzer_.username));

		try {
			TypedQuery<String> typedQuery = entityManager.createQuery(query)
				.setParameter(authTokenParam, authToken);
			String username = typedQuery.getSingleResult();
			BenutzerCredentials credentials = new BenutzerCredentials(username,  authToken);
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
	public AuthAccessElement createLoginFromIAM(AuthorisierterBenutzer authorisierterBenutzer) {

		entityManager.persist(authorisierterBenutzer);
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
	public Optional<AuthorisierterBenutzer> validateAndRefreshLoginToken(String token) {

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
				return Optional.of(authUser);

			} catch (NoResultException ignored) {
				LOG.debug("Could not load Authorisierterbenutzer for token '" +
					token + "'");
				return Optional.empty();
			}
	}
}
