package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthAccessElement;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.authentication.AuthLoginElement;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.util.Constants;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Stateless
@Path("auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

	public static final String COOKIE_PATH = "/";
	public static final String COOKIE_DOMAIN = null;

	private static final Logger LOG = LoggerFactory.getLogger(AuthResource.class);

	@EJB
	private AuthService authService;

	@Context
	private HttpServletRequest request;
	@Inject
	private BenutzerService benutzerService;

	@Inject
	private JaxBConverter converter;

	private boolean containerLogin(@Nonnull JaxAuthLoginElement loginElement) {
		try {
			// zuallererst einloggen, damit die SQL-Statements in den Services auch den richtigen Mandant haben...
			request.login(loginElement.getUsername(), loginElement.getPassword());
			return true;
		} catch (ServletException ignored) {
			return false;
		}
	}

	/**
	 * {@link AuthSecurityInterceptor}
	 * @param loginElement Benutzer Identifikation (Benutzername/Passwort)
	 * @return im Erfolgsfall eine HTTP Response mit Cookies
	 */
	@Nullable
	@POST
	@Path("/login")
	@PermitAll
	public Response login(@Nonnull JaxAuthLoginElement loginElement) {

		Optional<AuthAccessElement> accessElement;
		// zuerst im Container einloggen, sonst schlaegt in den Entities die Mandanten-Validierung fehl
		if (!containerLogin(loginElement)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		AuthLoginElement login = new AuthLoginElement(loginElement.getUsername(), loginElement.getPassword(),
			loginElement.getNachname(), loginElement.getVorname(), loginElement.getEmail(), loginElement.getRole());

		// Der Benutzer wird gesucht. Wenn er noch nicht existiert wird er erstellt und wenn ja dann aktualisiert
		Benutzer benutzer = new Benutzer();
		Optional<Benutzer> optBenutzer = benutzerService.findBenutzer(loginElement.getUsername());
		if (optBenutzer.isPresent()) {
			benutzer = optBenutzer.get();
		}
		benutzerService.saveBenutzer(converter.authLoginElementToBenutzer(loginElement, benutzer));

		accessElement = authService.login(login);
		if (!accessElement.isPresent()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		AuthAccessElement access = accessElement.get();
		JaxAuthAccessElement element = converter.authAccessElementToResource(access);

		boolean cookieSecure = isCookieSecure();

		// HTTP-Only Cookie --> Protection from XSS
		NewCookie authCookie = new NewCookie(AuthDataUtil.COOKIE_AUTH_TOKEN, access.getAuthToken(),
			COOKIE_PATH, COOKIE_DOMAIN, "authentication", Constants.COOKIE_TIMEOUT_SECONDS, cookieSecure, true);
		// Readable Cookie for XSRF Protection (the Cookie can only be read from our Domain)
		NewCookie xsrfCookie = new NewCookie(AuthDataUtil.COOKIE_XSRF_TOKEN, access.getXsrfToken(),
			COOKIE_PATH, COOKIE_DOMAIN, "XSRF", Constants.COOKIE_TIMEOUT_SECONDS, cookieSecure, false);
		// Readable Cookie storing user data
		NewCookie principalCookie = new NewCookie(AuthDataUtil.COOKIE_PRINCIPAL, encodeAuthAccessElement(element),
			COOKIE_PATH, COOKIE_DOMAIN, "principal", Constants.COOKIE_TIMEOUT_SECONDS, cookieSecure, false);

		return Response.ok().cookie(authCookie, xsrfCookie, principalCookie).build();
	}

	/**
	 *   TODO testen, bisher konnte dies nicht getestet werden
	 */
	private boolean isCookieSecure() {
		return request.isSecure();
	}

	@POST
	@Path("/logout")
	@PermitAll
	public Response logout(@CookieParam(AuthDataUtil.COOKIE_AUTH_TOKEN) Cookie authTokenCookie) {
		try {
			String authToken = Objects.requireNonNull(authTokenCookie.getValue());
			boolean cookieSecure = isCookieSecure();

			if (authService.logout(authToken)) {
				// Respond with expired cookies
				NewCookie authCookie = expireCookie(AuthDataUtil.COOKIE_AUTH_TOKEN, cookieSecure, true);
				NewCookie xsrfCookie = expireCookie(AuthDataUtil.COOKIE_XSRF_TOKEN, cookieSecure, false);
				NewCookie principalCookie = expireCookie(AuthDataUtil.COOKIE_PRINCIPAL, cookieSecure, false);
				return Response.ok().cookie(authCookie, xsrfCookie, principalCookie).build();
			}
			return Response.ok().build(); // TODO team Maybe notify the client? Seems not important for a logout request though.
		} catch (NoSuchElementException e) {
			LOG.info("Token Decoding from Cookies failed", e);
			return Response.ok().build(); // TODO team Maybe notify the client? Seems not important for a logout request though.
		}
	}

	@Nonnull
	private NewCookie expireCookie(@Nonnull String name, boolean secure, boolean httpOnly) {
		return new NewCookie(name, "", COOKIE_PATH, COOKIE_DOMAIN, "", 0, secure, httpOnly);
	}

	/**
	 * @param element zu codirendes AuthAccessElement
	 * @return Base64 encoded JSON representation
	 */
	private String encodeAuthAccessElement(JaxAuthAccessElement element) {
		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(element).getBytes(Charset.defaultCharset()));
	}
}
