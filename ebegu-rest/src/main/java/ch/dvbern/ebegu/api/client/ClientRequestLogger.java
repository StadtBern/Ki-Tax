
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
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

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
