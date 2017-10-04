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

package ch.dvbern.ebegu.api.filter;


import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import ch.dvbern.ebegu.config.EbeguConfiguration;

/**
 * Dieser Filter erlaubt cross origin requests. Dies ist natuerlich ein Sicherheitsrisiko und sollte in Produktion
 * entsprechend eingeschraenkt werden
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Inject
	private EbeguConfiguration configuration;

	@Override
	public void filter(final ContainerRequestContext requestContext,
					   final ContainerResponseContext cres) throws IOException {
		if (configuration.getIsDevmode()) {
			cres.getHeaders().add("Access-Control-Allow-Origin", "*");
			cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, X-Requested-With,authorization");
			cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
			cres.getHeaders().add("Access-Control-Expose-Headers", "Location, Content-Disposition");
			cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
			cres.getHeaders().add("Access-Control-Max-Age", "1209600");

		}

	}

}
