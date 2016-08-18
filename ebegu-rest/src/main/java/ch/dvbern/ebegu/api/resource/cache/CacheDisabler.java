/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.api.resource.cache;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CacheDisabler implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.putSingle(HttpHeaders.CACHE_CONTROL, "no-cache, no-store");

		/*
		 TODO, eine selektives deaktivieren des Caches (auf Resource-Ebene) waere wuenschenswert, ist aber nicht zwingend,
		 da Requests bereits in Frontend selektiv gecached werden.
		 Ein Ansatz mit Annotationen auf den Resource Methoden ist z.B. beschrieben in
		 http://alex.nederlof.com/blog/2013/07/28/caching-using-annotations-with-jersey/
		  */
	}
}
