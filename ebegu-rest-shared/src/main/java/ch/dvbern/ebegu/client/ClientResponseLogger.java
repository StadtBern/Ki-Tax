
package ch.dvbern.ebegu.client;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * logger fuer REST responses
 */
public class ClientResponseLogger implements ClientResponseFilter {

	private static final Logger LOG = LoggerFactory.getLogger(ClientResponseLogger.class.getSimpleName());
	private static final char SEPARATOR = ',';

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		LOG.info("ClientResponse Header: ");
		Joiner.MapJoiner mapJoiner = Joiner.on(SEPARATOR).withKeyValueSeparator("=");
		LOG.info(mapJoiner.join(responseContext.getHeaders()));

		LOG.info("ClientResponse Body: ");
		LOG.info("Status: " + responseContext.getStatus() + "; StatusInfo: " + responseContext.getStatusInfo());
		LOG.info("EntityTag: " + responseContext.getEntityTag() + "; length: " + responseContext.getLength());
		if (responseContext.getStatus() >= Response.Status.MOVED_PERMANENTLY.getStatusCode()) {
			LOG.info(requestContext.getEntity().toString());
		}

	}
}
