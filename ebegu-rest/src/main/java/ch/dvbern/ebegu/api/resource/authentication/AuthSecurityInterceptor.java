/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.api.resource.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.NoSuchElementException;

@Provider
@PreMatching
public class AuthSecurityInterceptor implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthSecurityInterceptor.class);

	@Context
	private HttpServletRequest request;

//	@EJB
//	private AuthService authService;

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	public void filter(ContainerRequestContext requestContext) {
		try {
			// nur zur Sicherheit...
			request.logout();
		} catch (ServletException e) {
			LOG.error("Unexpected", e);
			setResponseUnauthorised(requestContext);
			return;
		}

		String path = requestContext.getUriInfo().getPath();
		if (path.startsWith("/auth/login") || path.startsWith("/application.wadl") || "OPTIONS".equals(requestContext.getMethod())) {
			// Beim Login Request gibt es noch nichts abzufangen
			return;
		}

//		// Verify that XSRF-Token from HTTP-Header matches Cookie-XSRF-Token
//		String xsrfTokenHeader = requestContext.getHeaderString(AuthDataUtil.PARAM_XSRF_TOKEN);
//		Cookie xsrfTokenCookie = requestContext.getCookies().get(AuthDataUtil.COOKIE_XSRF_TOKEN);
//		boolean isValidFileDownload = StringUtils.isEmpty(xsrfTokenHeader)
//			&& xsrfTokenCookie != null
//			&& RestUtil.isFileDownloadRequest(requestContext);
//		if (!request.getRequestURI().contains("/migration/")) {
//			if (!isValidFileDownload && !AuthDataUtil.isValidXsrfParam(xsrfTokenHeader, requestContext)) {
//				setResponseUnauthorised(requestContext);
//				return;
//			}
//		}

		try {
			// Get AuthId and AuthToken from Cookies.
//			String authId = AuthDataUtil.getAuthAccessElement(requestContext).get().getAuthId();
//			String authToken = AuthDataUtil.getAuthToken(requestContext).get();
//
//			Optional<BenutzerCredentials> loginWithToken = authService.loginWithToken(authId, authToken);
//			if (!loginWithToken.isPresent()) {
//				setResponseUnauthorised(requestContext);
//				return;
//			}
//			BenutzerCredentials credentials = loginWithToken.get();

			try {
				// EJB Container Login
				request.login("baku", "password4");
//				request.login(credentials.getUsername(), credentials.getPasswordEncrypted());
			} catch (ServletException e) {
				// Login Failed
				setResponseUnauthorised(requestContext);
				return;
			}

//			if (!authService.verifyToken(credentials)) {
//				// Token Verification Failed
//				setResponseUnauthorised(requestContext);
//			}
		} catch (NoSuchElementException e) {
			LOG.info("Login with Token failed", e);
			setResponseUnauthorised(requestContext);
		}
	}

	private void setResponseUnauthorised(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	}
}
