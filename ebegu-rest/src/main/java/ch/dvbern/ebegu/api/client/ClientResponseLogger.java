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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
