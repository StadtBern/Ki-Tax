/*
 * Copyright (c) 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.api.client;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

public class ClientResponseLogger implements ClientResponseFilter {


	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		logger.info("ClientResponse Header: ");
		Joiner.MapJoiner mapJoiner = Joiner.on(',').withKeyValueSeparator("=");
		logger.info(mapJoiner.join(responseContext.getHeaders()));

		logger.info("ClientResponse Body: ");
		logger.info("Status: " + responseContext.getStatus() + "; StatusInfo: " + responseContext.getStatusInfo());
		logger.info("EntityTag: " + responseContext.getEntityTag() + "; length: " +responseContext.getLength());

	}
}
