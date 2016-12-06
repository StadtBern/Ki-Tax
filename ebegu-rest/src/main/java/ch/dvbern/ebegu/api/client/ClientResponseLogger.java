
package ch.dvbern.ebegu.api.client;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

public class ClientResponseLogger implements ClientResponseFilter {

	private static final Logger LOG = LoggerFactory.getLogger(ClientResponseLogger.class.getSimpleName());

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		LOG.info("ClientResponse Header: ");
		Joiner.MapJoiner mapJoiner = Joiner.on(',').withKeyValueSeparator("=");
		LOG.info(mapJoiner.join(responseContext.getHeaders()));

		LOG.info("ClientResponse Body: ");
		LOG.info("Status: " + responseContext.getStatus() + "; StatusInfo: " + responseContext.getStatusInfo());
		LOG.info("EntityTag: " + responseContext.getEntityTag() + "; length: " + responseContext.getLength());

	}
}
