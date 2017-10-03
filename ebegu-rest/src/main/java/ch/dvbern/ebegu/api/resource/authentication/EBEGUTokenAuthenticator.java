package ch.dvbern.ebegu.api.resource.authentication;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MonitoringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.NotImplementedException;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.omnifaces.security.jaspic.user.TokenAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;

/**
 * This Authenticator will check if an Token Passed bz the {@link CookieTokenAuthModule} is actually valid.
 * To be valid a token must be stored in the Cache already or exist in the authorisierte_benutyer table
 */
@RequestScoped
public class EBEGUTokenAuthenticator implements TokenAuthenticator {

	private static final Logger LOG = LoggerFactory.getLogger(EBEGUTokenAuthenticator.class);
	private static final long serialVersionUID = 55599436567329056L;

	@Resource(lookup = "java:jboss/infinispan/container/ebeguCache")
	private CacheContainer cacheContainer;


	private AuthorisierterBenutzer user;

	@Inject
	private AuthService authService;

	private Cache<String, AuthorisierterBenutzer> cache;
	private long expirationLifespan;

	@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH", justification = "should be injected")
	@PostConstruct
	void init() {
		if (cacheContainer == null) {
			LOG.warn("ACHTUNG: Cache konnte nicht initialisiert werden. " +
				"Ist die Infinispan Cache konfiguration im Standalone.xml korrekt und ist der Dependencies Eintrag im MANIFEST.MF gesetzt?");
		}
		this.cache = cacheContainer.getCache();
		expirationLifespan = cache.getCacheConfiguration().expiration().lifespan() == -1 ?
			Long.MAX_VALUE : cache.getCacheConfiguration().expiration().lifespan();
		logCacheInfo();
	}

	private void logCacheInfo() {
		LOG.debug("Cache settings for EBEGUTokenAuthenticator:");
		LOG.debug("\t lifespan: {}", cache.getCacheConfiguration().expiration().lifespan());
		LOG.debug("\t wakeup Interval: {}", cache.getCacheConfiguration().expiration().wakeUpInterval());
		LOG.debug("\t attributes: {}", cache.getCacheConfiguration().expiration().attributes());
		LOG.debug("\t using expiration lifespan of " + expirationLifespan);

	}

	@Override
	public boolean authenticate(final String token) {
		return MonitoringUtil.monitor(EBEGUTokenAuthenticator.class, "auth", () -> {
			boolean doRefreshToken = true;
			// Wir muessen entscheiden, ob wir das Login verlaengern sollen. Dies wollen wir nur, wenn der ensprechende
			// Suffix nicht am Token haengt.
			String effectiveToken = token;
			if (token.endsWith(Constants.AUTH_TOKEN_SUFFIX_FOR_NO_TOKEN_REFRESH_REQUESTS)) {
				effectiveToken = token.replaceAll(Constants.AUTH_TOKEN_SUFFIX_FOR_NO_TOKEN_REFRESH_REQUESTS, "");
				doRefreshToken = false;
			}

			AuthorisierterBenutzer cachedUser = cache.get(effectiveToken);
			if (cachedUser != null
				&& cachedUser.getLastLogin().plus(expirationLifespan, ChronoUnit.MILLIS).isAfter(LocalDateTime.now())) {
				//cache token gelten immer als valid (kein expired check)
				LOG.debug("Cache HIT, found token in cache, no further checks neceasary" + effectiveToken);
				user = cachedUser;

			} else {
				LOG.debug("Cache MISS, could not find active cache entry for " + effectiveToken);

				Optional<AuthorisierterBenutzer> authUser = readUserFromDatabase(effectiveToken, doRefreshToken);
				if (!authUser.isPresent()) {
					LOG.debug("Could not load authorisierter_benutzer with  token  " + effectiveToken);
					return false;
				}
				user = authUser.get();

				boolean stillValid = verifyTokenStillValid();
				LocalDateTime expiresAt = user.getLastLogin().plus(Constants.LOGIN_TIMEOUT_SECONDS, ChronoUnit.SECONDS);
				LOG.debug("Found AuthorisierterBenutzer entry.  valid: {}; expires at {}", stillValid, expiresAt);
				if (!stillValid) {
					LOG.debug("Token {} was no longer valid in Database -> unauthorized ", effectiveToken);
					return false;
				} else {
					cache.putForExternalRead(effectiveToken, user);
					LOG.debug("Token {} is still valid in Database (and was probably renewed), cache was refreshed", effectiveToken);
				}
			}

			return true;
		});
	}

	private boolean verifyTokenStillValid() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime maxDateFromNow = now.minus(Constants.LOGIN_TIMEOUT_SECONDS, ChronoUnit.SECONDS);

		if (user.getLastLogin().isBefore(maxDateFromNow)) {
			LOG.debug("Token is no longer valid: " + user.getAuthToken());
			return false;
		}
		return true;
	}

	/**
	 * reads user from database and optionally refreshes the login token in the database
	 */
	private Optional<AuthorisierterBenutzer> readUserFromDatabase(String token, boolean doRefreshToken) {
		return authService.validateAndRefreshLoginToken(token, doRefreshToken);
	}

	@Override
	public String generateLoginToken() {
		throw new NotImplementedException("Token is not generated here. Please see in FedletSamlServlet for generation");
	}

	@Override
	public void removeLoginToken() {
		//this method is never actually called from our CookieTokenAuthModule
		authService.logout(user.getAuthToken());
	}

	@SuppressFBWarnings("NM_CONFUSING")
	@Override
	public String getUserName() {
		return user == null ? null : user.getUsername();
	}

	@Override
	public List<String> getApplicationRoles() {
		if (user != null) {
			List<String> result = new ArrayList<>();
			result.add(user.getRole().toString());
			return result;
		}
		return emptyList();
	}


}
