package ch.dvbern.ebegu.api.resource.auth;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;

/**
 * Heloer to check if a request originates from localhost
 */
@ApplicationScoped
public class LocalhostChecker {


	private final Logger LOG = LoggerFactory.getLogger(LocalhostChecker.class.getSimpleName());

	private Set<String> localAddresses = new HashSet<>();


	@SuppressWarnings("OverlyBroadCatchBlock")
	@PostConstruct
	private void init(){
		try {
			localAddresses.add(InetAddress.getLocalHost().getHostAddress());
			for (InetAddress inetAddress : InetAddress.getAllByName("localhost")) {
				localAddresses.add(inetAddress.getHostAddress());
			}
		} catch (IOException e) {
			LOG.error("Could not find addresses for localhost ", e);
			throw new EbeguRuntimeException("init localhost checker", "Unable to lookup local addresses");
		}
	}

	public boolean isAddressLocalhost(String localhost) {
		return localAddresses.contains(localhost);

	}

}
