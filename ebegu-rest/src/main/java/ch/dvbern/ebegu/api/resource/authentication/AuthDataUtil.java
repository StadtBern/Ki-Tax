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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import ch.dvbern.ebegu.api.AuthConstants;
import ch.dvbern.ebegu.api.dtos.JaxAuthAccessElementCookieData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util welches aus Requests die cookies extrahiert und aus den Cookies zum Beispiel den Principal
 */
public final class AuthDataUtil {

	private static final Logger LOG = LoggerFactory.getLogger(AuthDataUtil.class);

	private AuthDataUtil() {
	}

	public static Optional<JaxAuthAccessElementCookieData> getAuthAccessElement(HttpServletRequest request) {
		Cookie loginInfoCookie = extractCookie(request.getCookies(), AuthConstants.COOKIE_PRINCIPAL);
		if (loginInfoCookie == null) {
			return Optional.empty();
		}
		String encodedPrincipalJson = StringUtils.trimToNull(loginInfoCookie.getValue());
		if (StringUtils.isEmpty(encodedPrincipalJson)) {
			return Optional.empty();
		}
		try {
			Gson gson = new Gson();
			encodedPrincipalJson =  URLDecoder.decode(encodedPrincipalJson, "UTF-8");
			return Optional.of(gson.fromJson(
				new String(
					Base64.getDecoder().decode(encodedPrincipalJson), Charset.forName("UTF-8")
				),
				JaxAuthAccessElementCookieData.class));
		} catch (JsonSyntaxException | IllegalArgumentException | UnsupportedEncodingException e) {
			LOG.warn("Failed to get the AuthAccessElement from the principal Cookie", e);
			return Optional.empty();
		}
	}

	@Nullable
	public static Cookie extractCookie(Cookie[] cookies, String searchedName) {
		if (cookies != null && searchedName != null) {
			for (Cookie cookie : cookies) {
				if (searchedName.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static String getBasePath(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
	}

	/**
	 * @param requestContext context to extract cookie from
	 * @return Optional containing the authToken if present
	 */
	@Nonnull
	public static Optional<String> getAuthTokenFomCookie(HttpServletRequest request) {
		Cookie authTokenCookie = extractCookie(request.getCookies(), AuthConstants.COOKIE_AUTH_TOKEN);
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
	public static boolean isValidXsrfParam(String xsrfTokenHeader, Cookie xsrfTokenCookie) {
		return !StringUtils.isEmpty(xsrfTokenHeader) && xsrfTokenCookie != null && StringUtils.equals(StringUtils.trimToNull(xsrfTokenCookie.getValue()), StringUtils.trimToNull(xsrfTokenHeader));
	}
}
