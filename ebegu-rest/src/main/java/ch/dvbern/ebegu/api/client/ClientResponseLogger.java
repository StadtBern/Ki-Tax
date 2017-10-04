
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

package ch.dvbern.ebegu.api.client;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	}
}
