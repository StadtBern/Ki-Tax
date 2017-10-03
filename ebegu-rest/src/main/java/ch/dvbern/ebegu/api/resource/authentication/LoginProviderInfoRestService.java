package ch.dvbern.ebegu.api.resource.authentication;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.api.AuthConstants;
import ch.dvbern.ebegu.api.client.ClientRequestLogger;
import ch.dvbern.ebegu.api.client.ClientResponseLogger;
import ch.dvbern.ebegu.api.connector.clientinfo.ILoginProviderInfoResource;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.config.EbeguConfigurationImpl.EBEGU_LOGIN_PROVIDER_API_URL;

/**
 * Service managing a REST Client {@link ILoginProviderInfoResource} that can grab infos from the LoginProvider that is used
 */
@Stateless
public class LoginProviderInfoRestService {

	private final Logger LOG = LoggerFactory.getLogger(LoginProviderInfoRestService.class.getSimpleName());
	public static final int CONNECTION_TIMEOUT = 10;

	@Inject
	private EbeguConfiguration configuration;

	private ILoginProviderInfoResource loginProviderInfoRESTService;

	public String getSingleLogoutURL(
		@Nullable String relayPath,
		@Nullable String nameID,
		@Nullable String sessionID) {
		return getLoginProviderInfoProxClient().getSingleLogoutURL(relayPath, nameID, sessionID);
	}

	public String getSSOLoginInitURL(@Nullable String relayPath) {
		if (!this.isConnectorEndpointSpecified()) {
			LOG.debug("No external Login connector specified, redirecting to locallogin");
			return AuthConstants.LOCALLOGIN_PATH;
		}
		return getLoginProviderInfoProxClient().getSSOLoginInitURL(relayPath);
	}

	public String pingLoginProvider() {
		return this.getLoginProviderInfoProxClient().getHeartBeat();
	}

	private ILoginProviderInfoResource getLoginProviderInfoProxClient() {
		if (loginProviderInfoRESTService == null) {
			String baseURL = configuration.getLoginProviderAPIUrl();
			if (baseURL == null) {
				final String errMsg = "Can not construct LoginConnectorService because API-URI of connector is not specified via property";
				LOG.error(errMsg);
				LOG.error("The required URI must be specified using the property " + EBEGU_LOGIN_PROVIDER_API_URL);
				throw new IllegalStateException(errMsg);
			}
			LOG.debug("Creating REST Client for URL {}", baseURL);
			ResteasyClient client = buildClient();
			ResteasyWebTarget target = client.target(baseURL);
			this.loginProviderInfoRESTService = target.proxy(ILoginProviderInfoResource.class);
			LOG.debug("Creating REST Proxy for Login Provider");
			final String responseMsg = loginProviderInfoRESTService.getHeartBeat();
			LOG.debug("version {}", responseMsg);

		}
		return loginProviderInfoRESTService;
	}

	private boolean isConnectorEndpointSpecified() {
		return !StringUtils.isEmpty(configuration.getLoginProviderAPIUrl());
	}

	/**
	 * erstellt einen neuen ResteasyClient
	 */
	private ResteasyClient buildClient() {
		ResteasyClientBuilder builder = new ResteasyClientBuilder().establishConnectionTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);

		if (configuration.getIsDevmode() || LOG.isDebugEnabled()) { //wenn debug oder dev mode dann loggen wir den request
			builder.register(new ClientRequestLogger());
			builder.register(new ClientResponseLogger());
		}
		return builder.build();
	}
}

