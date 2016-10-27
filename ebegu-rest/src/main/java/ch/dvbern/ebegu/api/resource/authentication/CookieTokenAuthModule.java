/*
 * Copyright 2014 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.api.EbeguApplicationV1;
import ch.dvbern.ebegu.api.util.RestUtil;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.security.jaspic.core.AuthParameters;
import org.omnifaces.security.jaspic.core.HttpMsgContext;
import org.omnifaces.security.jaspic.core.HttpServerAuthModule;
import org.omnifaces.security.jaspic.user.TokenAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

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


	@Override
	public AuthStatus validateHttpRequest(HttpServletRequest request, HttpServletResponse response, HttpMsgContext httpMsgContext) throws AuthException {
		prepareLogvars(httpMsgContext);
//		try {
//			request.logout();
//		} catch (ServletException e) {
//			LOG.error("Unexpected exception during Logout", e);
//			return setResponseUnauthorised(request, httpMsgContext);
//		}

		//Exceptional paths that do not require a login
		String apiBasePath = request.getContextPath() + EbeguApplicationV1.API_ROOT_PATH;
		String path = request.getRequestURI();
		AuthDataUtil.getBasePath(request);
		if (path.startsWith(apiBasePath + "/auth/login")
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
		String xsrfTokenHeader = request.getHeader(AuthDataUtil.PARAM_XSRF_TOKEN);


		Cookie xsrfTokenCookie = AuthDataUtil.extractCookie(request.getCookies(), AuthDataUtil.COOKIE_XSRF_TOKEN);
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
			LOG.error("Error when trying to send 401 back because of missing Authorization");
			throw new IllegalStateException(e);
		}
		return SEND_FAILURE;

	}


}
