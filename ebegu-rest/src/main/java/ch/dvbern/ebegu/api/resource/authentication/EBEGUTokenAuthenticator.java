package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.util.Constants;
import org.omnifaces.security.jaspic.user.TokenAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@RequestScoped
public class EBEGUTokenAuthenticator implements TokenAuthenticator {

	private static final Logger LOG = LoggerFactory.getLogger(EBEGUTokenAuthenticator.class);

//    @Inject
//    private UserService userService;
//
//    @Inject
//    private CacheManager cacheManager;

    private AuthorisierterBenutzer user;  //todo homa maybe change to credentials

	@Inject
	private AuthService authService;

    @Override
    public boolean authenticate(String token) {
//        try {
//            Cache<String, User> usersCache = cacheManager.getDefaultCache();
//
//            User cachedUser = usersCache.get(token);
//            if (cachedUser != null) {
//                user = cachedUser;
//            } else {



			Optional<AuthorisierterBenutzer> authUser = readUserFromDatabase(token);
			if(!authUser.isPresent()){
				LOG.debug("Could not load authorisierter_benutzer with  token  " + token );
				return false;
			}
			user = authUser.get();
		boolean stillValid =  verifyTokenStillValid();
		if (!stillValid) {
			return false;
		}



//			user = userService.getUserByLoginToken(token);
//                usersCache.put(token, user);
//            }
//        } catch (InvalidCredentialsException e) {
//            return false;
//        }

        return true;
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

	private Optional<AuthorisierterBenutzer> readUserFromDatabase(String token) {

		return authService.getUserByLoginToken(token);

	}

	@Override
	public String generateLoginToken() {
		return null;
	}

	@Override
	public void removeLoginToken() {

	}

	@Override
    public String getUserName() {
        return user == null ? null : user.getUsername();
    }

    @Override
    public List<String> getApplicationRoles() {
		if (user != null) {
			List<String> result = new ArrayList<String>();
			result.add(user.getRole().toString());
			return result;
		}
		return emptyList();
	}


}
