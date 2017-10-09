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
package ch.dvbern.ebegu.api.resource.cache;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CacheDisabler implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.putSingle(HttpHeaders.CACHE_CONTROL, "no-cache, no-store");

		/*
		 eine selektives deaktivieren des Caches (auf Resource-Ebene) waere wuenschenswert, ist aber nicht zwingend,
		 da Requests bereits in Frontend selektiv gecached werden.
		 Ein Ansatz mit Annotationen auf den Resource Methoden ist z.B. beschrieben in
		 http://alex.nederlof.com/blog/2013/07/28/caching-using-annotations-with-jersey/
		  */
	}
}
