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

import ch.dvbern.ebegu.api.dtos.JaxAuthAccessElement;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

/**
 * Util welches aus Requests die cookies extrahiert und aus den Cookies zum Beispiel den Principal
 */
public final class AuthDataUtil {

	private static final Logger LOG = LoggerFactory.getLogger(AuthDataUtil.class);


	public static final String COOKIE_PRINCIPAL = "authId";
	public static final String COOKIE_AUTH_TOKEN = "authToken";
	public static final String PARAM_XSRF_TOKEN = "X-XSRF-TOKEN";
	public static final String COOKIE_XSRF_TOKEN = "XSRF-TOKEN";

	private AuthDataUtil() {
	}

	public static  Optional<JaxAuthAccessElement>  getAuthAccessElement(HttpServletRequest request) {
		javax.servlet.http.Cookie loginInfoCookie = extractCookie(request.getCookies(), AuthDataUtil.COOKIE_PRINCIPAL);
		if (loginInfoCookie == null) {
					return Optional.empty();
				}
		String encodedPrincipalJson = StringUtils.trimToNull(loginInfoCookie.getValue());
			if (StringUtils.isEmpty(encodedPrincipalJson)) {
				return Optional.empty();
			}
		try {
				Gson gson = new Gson();
				return Optional.of(gson.fromJson(
					new String(
						Base64.getDecoder().decode(encodedPrincipalJson), Charset.forName("UTF-8")
					),
					JaxAuthAccessElement.class));
			} catch (JsonSyntaxException | IllegalArgumentException e) {
				LOG.warn("Failed to get the AuthAccessElement from the principal Cookie", e);
				return Optional.empty();
			}
	}

//	/**
//	 * @deprecated
//	 */
//	@Nonnull
//	@Deprecated
//	public static Optional<JaxAuthAccessElement> getAuthAccessElement(@Nonnull ContainerRequestContext requestContext) {
//		if (!requestContext.getCookies().containsKey(AuthDataUtil.COOKIE_PRINCIPAL)) {
//			return Optional.empty();
//		}
//		String encodedPrincipalJson = StringUtils.trimToNull(requestContext.getCookies().get(AuthDataUtil.COOKIE_PRINCIPAL).getValue());
//		if (StringUtils.isEmpty(encodedPrincipalJson)) {
//			return Optional.empty();
//		}
//		//Decode from Json (Json is Base64 encoded)
//		try {
//			Gson gson = new Gson();
//			return Optional.of(gson.fromJson(
//				new String(
//					Base64.getDecoder().decode(encodedPrincipalJson), Charset.forName("UTF-8")
//				),
//				JaxAuthAccessElement.class));
//		} catch (JsonSyntaxException | IllegalArgumentException e) {
//			LOG.warn("Failed to get the AuthAccessElement from the principal Cookie", e);
//			return Optional.empty();
//		}
//	}


	@Nullable
	public static javax.servlet.http.Cookie extractCookie(javax.servlet.http.Cookie[] cookies, String searchedName) {
		if (cookies != null && searchedName != null) {
			for (javax.servlet.http.Cookie cookie : cookies) {
				if (searchedName.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static String getBasePath(HttpServletRequest request){
		String url = request.getRequestURL().toString();
		return url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
	}

	/**
	 * @param requestContext context to extract cookie from
	 * @return Optional containing the authToken if present
	 */
	@Nonnull
	public static  Optional<String> getAuthTokenFomCookie(HttpServletRequest request) {
		javax.servlet.http.Cookie authTokenCookie =  extractCookie(request.getCookies(), AuthDataUtil.COOKIE_AUTH_TOKEN);
			String authToken = authTokenCookie != null ? authTokenCookie.getValue() : null;
			if (StringUtils.isEmpty(authToken)) {
				return Optional.empty();
			}
			return Optional.of(authToken);
	}

	/**
	 * checks that the passed xsrfTokenParam matches the token stored in the cookie
	 *
	 * @param xsrfTokenParam token to check
	 * @param requestContext request to get Cookie from
	 * @return true if the tokens match; false otherweise
	 */
	public static boolean isValidXsrfParam(String xsrfTokenHeader, javax.servlet.http.Cookie xsrfTokenCookie) {
		return !StringUtils.isEmpty(xsrfTokenHeader) && xsrfTokenCookie != null && StringUtils.equals(StringUtils.trimToNull(xsrfTokenCookie.getValue()), StringUtils.trimToNull(xsrfTokenHeader));
	}
}
