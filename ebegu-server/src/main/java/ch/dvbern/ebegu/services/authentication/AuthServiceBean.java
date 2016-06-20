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

//		HashedPassword passwordEncrypted;
//		try {
//			passwordEncrypted = encryptPassword(loginElement.getUsername(), loginElement.getPlainTextPassword());
//		} catch (PasswordPreReqsException ignored) {
//			// Wenn die Password-Requirements nicht erfuellt sind, kann's kein gueltiges Passwort sein
//			return Optional.empty();
//		}

		Optional<Benutzer> benutzer = benutzerService.findBenutzer(loginElement.getUsername());
		if (!benutzer.isPresent()) {
			return Optional.empty();
		}

		AuthorisierterBenutzer authorisierterBenutzer = new AuthorisierterBenutzer();
		authorisierterBenutzer.setBenutzer(benutzer.get());
		authorisierterBenutzer.setPassword(loginElement.getPlainTextPassword());
		authorisierterBenutzer.setAuthToken(UUID.randomUUID().toString());
		entityManager.persist(authorisierterBenutzer);
		return Optional.of(new AuthAccessElement(
			authorisierterBenutzer.getAuthId(),
			authorisierterBenutzer.getAuthToken(),
			UUID.randomUUID().toString(), // XSRF-Token (no need to persist)
			authorisierterBenutzer.getAuthId(),
			loginElement.getUsername(),
			loginElement.getNachname(),
			loginElement.getVorname(),
			loginElement.getEmail(),
			loginElement.getRoles()));
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
	public boolean verifyToken(@Nonnull BenutzerCredentials credentials) {
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
				return false;
			}
			authUser.setLastLogin(now);
			entityManager.persist(authUser);
			entityManager.flush();
			return true;

		} catch (NoResultException ignored) {
			return false;
		}
	}

//	@Override
//	@Nonnull
//	public HashedPassword encryptPassword(@Nonnull String username, @Nonnull String plaintextPassword) throws PasswordPreReqsException, RuleViolationException {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//		ParameterExpression<String> paramUserName = cb.parameter(String.class, "username");
//
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Benutzer> root = cq.from(Benutzer.class);
//		cq.select(root.get(Benutzer_.passwordSalt));
//		cq.where(cb.equal(root.get(Benutzer_.username), paramUserName), cb.isNull(root.get(Benutzer_.geloescht)));
//
//		String saltText;
//		try {
//			saltText = entityManager.createQuery(cq)
//				.setParameter(paramUserName, username)
//				.getSingleResult();
//		} catch (NoResultException ignored) {
//			throw new RuleViolationException(ERROR_USERNAME_INVALID); // NOPMD.PreserveStackTrace - hier brauchts keinen Stacktrace
//		}
//
//		return hashPasswordBCrypt(plaintextPassword, HashedPassword.decodeBase64(saltText));
//	}
//
//
//	@Override
//	@Nonnull
//	public HashedPassword createPassword(@Nonnull String plaintextPassword) throws PasswordPreReqsException {
//		long start = System.currentTimeMillis();
//		byte salt[] = new byte[BCRYPT_SALT_SIZE];
//		SECURE_RANDOM.get().nextBytes(salt);
//
//		HashedPassword hashedPassword = hashPasswordBCrypt(plaintextPassword, salt);
//
//		long duration = System.currentTimeMillis() - start;
//		if (duration > 1000) {
//			LOG.error("Hashing password took too long: {}", duration);
//		}
//		return hashedPassword;
//	}
//
//	@Nonnull
//	HashedPassword hashPasswordBCrypt(@Nonnull String plaintextPassword, @Nonnull byte salt[]) throws PasswordPreReqsException {
//		Objects.requireNonNull(plaintextPassword);
//		Objects.requireNonNull(salt);
//		if (plaintextPassword.isEmpty()) {
//			throw new IllegalArgumentException("plaintextPassword may not be empty");
//		}
//
//		// TODO add password security constraints
//
//		byte passwordBytes[] = plaintextPassword.getBytes(StandardCharsets.UTF_8);
//
//		if (passwordBytes.length > BCRYPT_MAX_PASSWORD_BYTES) {
//			throw new PasswordPreReqsException(PreReqFailure.TOO_LONG);
//		}
//
//		byte[] hashedPassword;
//		synchronized (BCrypt.class) {
//			hashedPassword = BCrypt.generate(passwordBytes, salt, BCRYPT_COST_ROUNDS);
//		}
//
//		return new HashedPassword(hashedPassword, salt);
//	}
//
//	@Override
//	public boolean checkPassword(@Nonnull String oldEncryptedPassword, @Nonnull String newEncryptedPassword) throws PasswordPreReqsException {
//		Objects.requireNonNull(oldEncryptedPassword);
//		Objects.requireNonNull(newEncryptedPassword);
//
//		if (!oldEncryptedPassword.equals(newEncryptedPassword)) {
//			throw new PasswordPreReqsException(PreReqFailure.CONFIRMATION_FAILURE);
//		}
//		return true;
//	}
}
