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
 * Authentication module that authenticates based on a token in a cookie the request.
 *
 * <p>
 * Token to username/roles mapping is delegated to an implementation of {@link TokenAuthenticator}, which
 * should be registered as CDI bean.
 *
 * <p>
 * <b>NOTE:</b> This module makes the simplifying assumption that CDI is available in a SAM. Unfortunately
 * this is not true for every implementation. See https://java.net/jira/browse/JASPIC_SPEC-14
 *
 * @author Arjan Tijms
 *
 */
public class CookieTokenAuthModule extends HttpServerAuthModule {

	private static final Logger LOG = LoggerFactory.getLogger(CookieTokenAuthModule.class);
	private static final String LOG_MDC_EBEGUUSER = "ebeguuser";
	private static final String LOG_MDC_AUTHUSERID = "ebeguauthuserid";


	@Override
	public AuthStatus validateHttpRequest(HttpServletRequest request, HttpServletResponse response, HttpMsgContext httpMsgContext) throws AuthException {
		MDC.put(LOG_MDC_EBEGUUSER, "unknown");
		MDC.put(LOG_MDC_AUTHUSERID, "unknown");
//		try {
//			request.logout();
//		} catch (ServletException e) {
//			LOG.error("Unexpected exception during Logout", e);
//			return setResponseUnauthorised(request, httpMsgContext);
//		}


		//todo maybe can to this in web.xml now
		//httpMsgContext.isProtected()

		String apiBasePath =  request.getContextPath() + EbeguApplicationV1.API_ROOT_PATH;
		String path = request.getRequestURI();
		AuthDataUtil.getBasePath(request);
		if (path.startsWith(apiBasePath+ "/auth/login")
			|| path.startsWith(request.getContextPath() + "/mylogin.jsp")
			|| path.startsWith(request.getContextPath() + "/logout.jsp")
			|| path.startsWith(request.getContextPath() + "/index.jsp")
			|| path.startsWith(request.getContextPath() + "/saml2/jsp/")
			|| path.startsWith(request.getContextPath() + "/fedletapplication")
			|| path.startsWith(request.getContextPath() + "/fedletSloInit")
			|| path.startsWith(request.getContextPath() + "/fedletlogout")
			|| path.startsWith(request.getContextPath() + "/fedletSloRedirect")
			||  path.startsWith(apiBasePath+  "/swagger.json")
			|| "OPTIONS".equals(request.getMethod())) {
			// Beim Login Request gibt es noch nichts abzufangen
			return httpMsgContext.doNothing();
		}

		// Verify that XSRF-Token from HTTP-Header matches Cookie-XSRF-Token
		if (!verifyXSFRHeader(request)) {
			return setResponseUnauthorised(request,httpMsgContext);
		}
		try {
			// Get AuthId (=loginname, actually not needed) and AuthToken from Cookies.
			String authToken = AuthDataUtil.getAuthTokenFomCookie(request).get();
			String authId = AuthDataUtil.getAuthAccessElement(request).get().getAuthId();
			if (!isEmpty(authToken)) {

				// If a token is present, authenticate with it whether this is strictly required or not.
				MDC.put(LOG_MDC_EBEGUUSER, authId);
				TokenAuthenticator tokenAuthenticator = getReferenceOrNull(TokenAuthenticator.class);
				if (tokenAuthenticator != null) {

					if (tokenAuthenticator.authenticate(authToken)) {
						LOG.debug("successfully logged in user: " + tokenAuthenticator.getUserName());
						MDC.put(LOG_MDC_AUTHUSERID, authToken);
//						httpMsgContext.registerWithContainer(tokenAuthenticator.getUserName(), tokenAuthenticator.getApplicationRoles()); //fixme why is this nececessary
						return httpMsgContext.notifyContainerAboutLogin(tokenAuthenticator.getUserName(), tokenAuthenticator.getApplicationRoles());
					} else{
						// Token Verification Failed
						LOG.debug("Token verification failed for " + tokenAuthenticator.getUserName());
						return setResponseUnauthorised(request, httpMsgContext);

					}
				} else {
					LOG.warn("No Bean found for Class " + TokenAuthenticator.class.getSimpleName() + " all auth attempts weill be refused");

				}
			}



//			//use token to authorize the request
//			Optional<BenutzerCredentials> loginWithToken = authService.loginWithToken(authId, authToken);
//			if (!loginWithToken.isPresent()) {
//				LOG.debug("Could not load authorisierter_benutzer with username" + authId + " token " + authToken );
//				setResponseUnauthorised(requestContext, httpMsgContext);
//				return;
//			}
//			BenutzerCredentials credentials = loginWithToken.get();
//
//			try {
//				// EJB Container Login todo team evtl mit request.authenticate
//				request.login(credentials.getUsername(), credentials.getPasswordEncrypted());
//
//			} catch (ServletException e) {
//				// Container Login Failed
//				LOG.debug("Container login failed" + credentials.getUsername());
//				setResponseUnauthorised(requestContext, httpMsgContext);
//				return;
//			}
//
//			//check if the token is still valid
//			Optional<String> loginId = authService.verifyToken(credentials);
//			if (loginId.isPresent()) {
//				MDC.put(LOG_MDC_AUTHUSERID, String.valueOf(loginId.get()));
//				LOG.debug("successfully logged in user: " + credentials.getUsername());
//			} else {
//				// Token Verification Failed
//				LOG.debug("Token verification failed for " + credentials.getUsername());
//				setResponseUnauthorised(requestContext, httpMsgContext);
//			}


		} catch (NoSuchElementException e) {
			LOG.info("Login with Token failed", e);
			return setResponseUnauthorised(request, httpMsgContext);
		}

		if (httpMsgContext.isProtected()) {
			return httpMsgContext.responseNotFound();
		}

		return httpMsgContext.doNothing();
	}

	private boolean verifyXSFRHeader(HttpServletRequest request) {
		String xsrfTokenHeader = request.getHeader(AuthDataUtil.PARAM_XSRF_TOKEN);


		Cookie xsrfTokenCookie = AuthDataUtil.extractCookie(request.getCookies(), AuthDataUtil.COOKIE_XSRF_TOKEN);
		boolean isValidFileDownload = StringUtils.isEmpty(xsrfTokenHeader)
			&& xsrfTokenCookie != null
			&& RestUtil.isFileDownloadRequest(request);
		if (!request.getRequestURI().contains("/migration/")) { //migration ist ausgenommen
			if (!isValidFileDownload && !AuthDataUtil.isValidXsrfParam(xsrfTokenHeader, xsrfTokenCookie)) {
				LOG.debug("Could not match XSRF Token from Header and Cookie. Header:" +xsrfTokenHeader +  " cookie " +xsrfTokenCookie );
				return false;
			}
		}
		return true;
	}



	private AuthStatus setResponseUnauthorised(HttpServletRequest requestContext, HttpMsgContext httpMsgContext) {

		try {
			httpMsgContext.getResponse().sendError(SC_UNAUTHORIZED);
		} catch (IOException e) {
			LOG.error("Error when trying to send 401 back because of missing Authorization");
			throw new IllegalStateException(e);
		}

		return SEND_FAILURE;

	}


	}
