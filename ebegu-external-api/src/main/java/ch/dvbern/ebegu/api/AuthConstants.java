package ch.dvbern.ebegu.api;

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
	int COOKIE_TIMEOUT_SECONDS = 60 * 60 * 12; //aktuell 12h
	String OPENIDM_INST_PREFIX = "I-";
	String OENIDM_TRAEGERSCHAFT_PREFIX = "T-";
	/**
	 * Path to locallogin page (relative to base path) that will be used if no login connector api is specified
	 */
	String LOCALLOGIN_PATH = "/#/locallogin";
}
