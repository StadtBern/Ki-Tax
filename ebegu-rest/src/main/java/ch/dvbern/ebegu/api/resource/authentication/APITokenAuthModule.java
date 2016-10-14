//package ch.dvbern.ebegu.api.resource.authentication;
//
//import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
//import ch.dvbern.ebegu.entities.Benutzer;
//import ch.dvbern.ebegu.services.AuthService;
//import org.omnifaces.security.jaspic.user.TokenAuthenticator;
//
//import javax.enterprise.context.RequestScoped;
//import javax.inject.Inject;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static java.util.Collections.emptyList;
//
//@RequestScoped
//public class APITokenAuthModule implements TokenAuthenticator {
//
////    @Inject
////    private UserService userService;
////
////    @Inject
////    private CacheManager cacheManager;
//
//    private Benutzer user;
//
//	@Inject
//	private AuthService authService;
//
//    @Override
//    public boolean authenticate(String token) {
////        try {
////            Cache<String, User> usersCache = cacheManager.getDefaultCache();
////
////            User cachedUser = usersCache.get(token);
////            if (cachedUser != null) {
////                user = cachedUser;
////            } else {
//			Optional<AuthorisierterBenutzer> authUser = authService.getUserByLoginToken(token);
//			if(!authUser.isPresent()){
//				return false;
//			}
//		 user =  authUser.get().getBenutzer();
//
////			user = userService.getUserByLoginToken(token);
////                usersCache.put(token, user);
////            }
////        } catch (InvalidCredentialsException e) {
////            return false;
////        }
//
//        return true;
//    }
//
//	@Override
//	public String generateLoginToken() {
//		return null;
//	}
//
//	@Override
//	public void removeLoginToken() {
//
//	}
//
//	@Override
//    public String getUserName() {
//        return user == null ? null : user.getUsername();
//    }
//
//    @Override
//    public List<String> getApplicationRoles() {
//		if (user != null) {
//			ArrayList<String> result = new ArrayList<String>();
//			result.add(user.getRole().toString()); //todo match applicaiton roles
//			return result;
//		}
//		return emptyList();
//	}
//
//    // (Two empty methods omitted)
//}
