
package ch.dvbern.ebegu.api.client;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Form;
import java.io.IOException;

/**
 * Klasse um Resteasy client requests zu loggen
 */
public class ClientRequestLogger implements ClientRequestFilter {


	private static final Logger LOG = LoggerFactory.getLogger(ClientRequestLogger.class.getSimpleName());

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOG.info("ClientRequest Header for call to : " + requestContext.getUri());

		Joiner.MapJoiner mapJoiner = Joiner.on(',').withKeyValueSeparator("=");
		LOG.info(mapJoiner.join(requestContext.getStringHeaders()));

		LOG.info("ClientReqeust Body: ");
		//bisschen hacky mit dem form aber wir haben atm nur einen service mit form format
		if (requestContext.getEntity() instanceof Form) {
			LOG.info(mapJoiner.join(((Form) requestContext.getEntity()).asMap()));
		} else if (requestContext.getEntity() != null){
			LOG.info(requestContext.getEntity().toString());
		}else {
			LOG.info("no body");
		}
	}
}
