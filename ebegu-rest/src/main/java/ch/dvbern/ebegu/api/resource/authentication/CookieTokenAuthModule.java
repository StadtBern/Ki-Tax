/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dvbern.ebegu.api.resource.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.util.BasicAuthHelper;
import org.omnifaces.security.jaspic.core.AuthParameters;
import org.omnifaces.security.jaspic.core.HttpMsgContext;
import org.omnifaces.security.jaspic.core.HttpServerAuthModule;
import org.omnifaces.security.jaspic.user.TokenAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.dvbern.ebegu.api.AuthConstants;
import ch.dvbern.ebegu.api.EbeguApplicationV1;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.util.Constants;

import static javax.security.auth.message.AuthStatus.SEND_FAILURE;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.omnifaces.security.cdi.Beans.getReferenceOrNull;
import static org.omnifaces.security.jaspic.Utils.isEmpty;

/**
 * Authentication module / Loginmodule that authenticates based on a token in a cookie the request.
 * <p>
 * <p>
 * Token to username/roles mapping is delegated to an implementation of {@link TokenAuthenticator}, which
 * should be registered as CDI bean.
 * <p>
 * <p>
 * <b>NOTE:</b> This module makes the simplifying assumption that CDI is available in a SAM. Unfortunately
 * this is not true for every implementation. See https://java.net/jira/browse/JASPIC_SPEC-14
 *
 * @author Arjan Tijms
 */
public class CookieTokenAuthModule extends HttpServerAuthModule {

	private static final Logger LOG = LoggerFactory.getLogger(CookieTokenAuthModule.class);
	private static final String LOG_MDC_EBEGUUSER = "ebeguuser";
	private static final String LOG_MDC_AUTHUSERID = "ebeguauthuserid";
	private final String internalApiUser;
	private final String internalApiPassword;
	private final String schulamtApiUser;
	private final String schulamtApiPassword;

	@SuppressWarnings("PMD.UnusedFormalParameter")
	public CookieTokenAuthModule(String loginModuleStackName) {
		//this is unused, just checked if this could be used to declare this module through standalone.xml instead of
		//SamRegistrationListener
		this();
	}

	public CookieTokenAuthModule(@Nullable String internalUser, @Nullable String internalPassword,
	@Nullable String schulamtUser, @Nullable String schulamtPassword) {
		//this is unused, just checked if this could be used to declare this module through standalone.xml instead of
		//SamRegistrationListener
		this.internalApiUser = internalUser;
		this.internalApiPassword = internalPassword;
		if (internalPassword == null || internalUser == null) {
			throw new EbeguRuntimeException("CookieTokenAuthModule initialization", "Internal API User must be set");
		}
		this.schulamtApiUser = schulamtUser;
		this.schulamtApiPassword = schulamtPassword;
	}

	public CookieTokenAuthModule(@Nullable String schulamtUser, @Nullable String schulamtPassword) {
		internalApiUser = null;
		internalApiPassword = null;
		schulamtApiUser = schulamtUser;
		schulamtApiPassword = schulamtPassword;
	}

	public CookieTokenAuthModule() {
		internalApiUser = null;
		internalApiPassword = null;
		schulamtApiUser = null;
		schulamtApiPassword = null;
	}

	@Override
	public AuthStatus validateHttpRequest(HttpServletRequest request, HttpServletResponse response, HttpMsgContext httpMsgContext) throws AuthException {
		prepareLogvars(httpMsgContext);
		//maybe we should do a logout first?
		//		try {
		//			request.logout();
		//		} catch (ServletException e) {
		//			LOG.error("Unexpected exception during Logout", e);
		//			return setResponseUnauthorised(request, httpMsgContext);
		//		}

		//Exceptional paths that do not require a login (they must also be added to web.xml security filter exceptions)
		String apiBasePath = request.getContextPath() + EbeguApplicationV1.API_ROOT_PATH;
		String path = request.getRequestURI();
		AuthDataUtil.getBasePath(request);
		if (path.startsWith(apiBasePath + "/auth/login")
			|| path.startsWith(apiBasePath + "/connector/heartbeat")
			|| path.startsWith(apiBasePath + "/schulamt/heartbeat")
			|| path.startsWith(apiBasePath + "/auth/singleSignOn")
			|| path.startsWith(apiBasePath + "/auth/singleLogout")
			|| path.startsWith(apiBasePath + "/swagger.json")
			|| path.startsWith(request.getContextPath() + "/ebeguTestLogin.jsp")
			|| path.startsWith(request.getContextPath() + "/logout.jsp")
			|| path.startsWith(request.getContextPath() + "/samlinfo.jsp")
			|| path.startsWith(request.getContextPath() + "/saml2/jsp/")
			|| path.startsWith(request.getContextPath() + "/fedletapplication")
			|| path.startsWith(request.getContextPath() + "/fedletSloInit")
			|| path.startsWith(request.getContextPath() + "/fedletlogout")
			|| path.startsWith(request.getContextPath() + "/fedletSloRedirect")
			|| "OPTIONS".equals(request.getMethod())) {
			// Beim Login Request gibt es noch nichts abzufangen
			return httpMsgContext.doNothing();
		}

		if (path.startsWith(apiBasePath + "/connector")) {
			return checkAuthorizationForInternalApiAccess(request, httpMsgContext);
		}

		if (path.startsWith(apiBasePath + "/schulamt")) {
			return checkAuthorizationForSchulamtApiAccess(request, httpMsgContext);
		}

		//pages that do not fall under de security-context that was defined in webx.xml
		if (!httpMsgContext.isProtected()) {
			return httpMsgContext.doNothing();
		}

		// Verify that XSRF-Token from HTTP-Header matches Cookie-XSRF-Token
		if (!verifyXSFRHeader(request)) {
			return setResponseUnauthorised(httpMsgContext);
		}
		try {
			// Get AuthId (=loginname, actually not needed) and AuthToken from Cookies.
			String authToken = AuthDataUtil.getAuthTokenFomCookie(request).get();
			String authId = AuthDataUtil.getAuthAccessElement(request).get().getAuthId();
			if (!isEmpty(authToken)) {

				MDC.put(LOG_MDC_EBEGUUSER, authId);
				TokenAuthenticator tokenAuthenticator = getReferenceOrNull(TokenAuthenticator.class);
				if (tokenAuthenticator != null) {

					// In einigen Faellen wollen wir bei einem Request nicht automatisch das Login verlaengern, z.B.
					// wenn ein Request ueber einen Timer ausgeloest war (z.B. Posteingang)
					// Da das Interface authenticate() mit einem Parameter vorgegeben ist, haengen wir in diesem Fall
					// einen Suffix an das Login-Token
					if (path.contains(Constants.PATH_DESIGNATOR_NO_TOKEN_REFRESH)) {
						authToken = authToken + Constants.AUTH_TOKEN_SUFFIX_FOR_NO_TOKEN_REFRESH_REQUESTS;
					}
					if (tokenAuthenticator.authenticate(authToken)) {
						LOG.debug("successfully logged in user: " + tokenAuthenticator.getUserName());
						MDC.put(LOG_MDC_AUTHUSERID, authToken);
						//						httpMsgContext.registerWithContainer(tokenAuthenticator.getUserName(), tokenAuthenticator.getApplicationRoles()); //weis nicht was der untschied zwischen dem und dem andern ist
						return httpMsgContext.notifyContainerAboutLogin(tokenAuthenticator.getUserName(), tokenAuthenticator.getApplicationRoles());
					} else {
						// Token Verification Failed
						LOG.debug("Token verification failed for " + tokenAuthenticator.getUserName());
						return setResponseUnauthorised(httpMsgContext);

					}
				} else {
					LOG.warn("No Authenticator found with CDI:  " + TokenAuthenticator.class.getSimpleName() + " all auth attempts will be refused");

				}
			}

		} catch (NoSuchElementException e) {
			LOG.info("Login with Token failed", e);
			return setResponseUnauthorised(httpMsgContext);
		}

		if (httpMsgContext.isProtected()) {
			LOG.debug("Access to protected path denied");
			return httpMsgContext.responseNotFound();
		}

		return httpMsgContext.doNothing();
	}

	private AuthStatus checkAuthorizationForInternalApiAccess(HttpServletRequest request, HttpMsgContext httpMsgContext) {
		if (!isInternalApiActive()) {
			LOG.error("Call to connector API even though the properties for username and password were not defined"
				+ "in ebegu. Please check that the system properties for username/password for the internal api are"
				+ " set");
			return setResponseUnauthorised(httpMsgContext);
		} else {

			String header = request.getHeader("Authorization");
			final String[] strings = BasicAuthHelper.parseHeader(header);
			if (strings != null && strings.length == 2) {
				final String username = strings[0];
				final String password = strings[1];
				boolean validLogin = username.equals(this.internalApiUser) && password.equals(this.internalApiPassword);
				return getAuthStatus(httpMsgContext, validLogin);
			} else {
				LOG.error("Call to connector api without BasicAuth header credentials");
				return setResponseUnauthorised(httpMsgContext);
			}
		}
	}

	private AuthStatus checkAuthorizationForSchulamtApiAccess(HttpServletRequest request, HttpMsgContext httpMsgContext) {
		if (!isSchulamtApiActive()) {
			LOG.error("Call to connector API even though the properties for username and password were not defined"
				+ "in ebegu. Please check that the system properties for username/password for the schulamt api are set");
			return setResponseUnauthorised(httpMsgContext);
		} else {

			String header = request.getHeader("Authorization");
			final String[] strings = BasicAuthHelper.parseHeader(header);
			if (strings != null && strings.length == 2) {
				final String username = strings[0];
				final String password = strings[1];
				boolean validLogin = username.equals(this.schulamtApiUser) && password.equals(this.schulamtApiPassword);
				return getAuthStatus(httpMsgContext, validLogin);
			} else {
				LOG.error("Call to connector api without BasicAuth header credentials");
				return setResponseUnauthorised(httpMsgContext);
			}
		}
	}

	private AuthStatus getAuthStatus(HttpMsgContext httpMsgContext, boolean validLogin) {
		if (validLogin) {
			//note: no actual container login is performed currently
			List<String> roles = new ArrayList<>();
			roles.add(UserRoleName.SUPER_ADMIN);
			return httpMsgContext.notifyContainerAboutLogin("LoginConnector", roles);
		} else {
			LOG.error("Call to connector api with invalid BasicAuth header credentials");
			return setResponseUnauthorised(httpMsgContext);
		}
	}

	private void prepareLogvars(HttpMsgContext msgContext) {
		MDC.put(LOG_MDC_EBEGUUSER, "unknown");
		MDC.put(LOG_MDC_AUTHUSERID, "unknown");

		if (LOG.isDebugEnabled()) {
			AuthParameters authParameters = msgContext.getAuthParameters();
			if (authParameters != null && Boolean.FALSE.equals(authParameters.getNoPassword())) {
				LOG.debug("Username " + authParameters.getUsername());
				LOG.debug("Password was passed in request");
			}
		}
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	private boolean verifyXSFRHeader(HttpServletRequest request) {
		String xsrfTokenHeader = request.getHeader(AuthConstants.PARAM_XSRF_TOKEN);

		Cookie xsrfTokenCookie = AuthDataUtil.extractCookie(request.getCookies(), AuthConstants.COOKIE_XSRF_TOKEN);
		boolean isValidFileDownload = StringUtils.isEmpty(xsrfTokenHeader)
			&& xsrfTokenCookie != null
			&& RestUtil.isFileDownloadRequest(request);
		if (!request.getRequestURI().contains("/migration/")) { //migration ist ausgenommen
			if (!isValidFileDownload && !AuthDataUtil.isValidXsrfParam(xsrfTokenHeader, xsrfTokenCookie)) {
				LOG.debug("Could not match XSRF Token from Header and Cookie. Header:" + xsrfTokenHeader + " cookie " + xsrfTokenCookie);
				return false;
			}
		}
		return true;
	}

	private AuthStatus setResponseUnauthorised(HttpMsgContext httpMsgContext) {
		try {
			httpMsgContext.getResponse().sendError(SC_UNAUTHORIZED);
		} catch (IOException e) {
			String message = "Error when trying to send 401 back because of missing Authorization";
			throw new IllegalStateException(message, e);
		}
		return SEND_FAILURE;
	}

	private boolean isInternalApiActive() {
		return internalApiPassword != null && internalApiUser != null;
	}

	private boolean isSchulamtApiActive() {
		return schulamtApiPassword != null && schulamtApiUser != null;
	}
}
