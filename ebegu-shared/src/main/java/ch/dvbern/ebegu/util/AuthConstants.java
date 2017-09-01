package ch.dvbern.ebegu.util;

/**
 * Constants that are important in connection with Authentication stuff (cookie, headerparams etc)
 */
public interface AuthConstants {
	String COOKIE_PATH = "/";
	String COOKIE_DOMAIN = null;
	String COOKIE_PRINCIPAL = "authId";
	String COOKIE_AUTH_TOKEN = "authToken";
	String PARAM_XSRF_TOKEN = "X-XSRF-TOKEN";
	String COOKIE_XSRF_TOKEN = "XSRF-TOKEN";
}
