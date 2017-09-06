package ch.dvbern.ebegu.api.resource.authentication;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.api.connector.clientinfo.ILoginProviderInfoResource;
import ch.dvbern.ebegu.client.ClientRequestLogger;
import ch.dvbern.ebegu.client.ClientResponseLogger;
import ch.dvbern.ebegu.config.EbeguConfiguration;

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
		return getLoginProviderInfoProxClient().getSSOLoginInitURL(relayPath);
	}

	public String pingLoginProvider(){
		return this.getLoginProviderInfoProxClient().getHeartBeat();
	}


	private ILoginProviderInfoResource getLoginProviderInfoProxClient() {
		if (loginProviderInfoRESTService == null) {
			String baseURL = configuration.getLoginProviderAPIUrl();
			LOG.debug("Creating REST Client for URL {}", baseURL);
			ResteasyClient client = buildClient();
			ResteasyWebTarget target = client.target(baseURL);
			this.loginProviderInfoRESTService = target.proxy(ILoginProviderInfoResource.class);
			LOG.info("Creating REST Proxy for Login Provider" );
			final String responseMsg = loginProviderInfoRESTService.getHeartBeat();
			LOG.info("version {}", responseMsg);

		}
		return loginProviderInfoRESTService;
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

