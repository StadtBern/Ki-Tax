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

import ch.dvbern.ebegu.config.EbeguConfiguration;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * Implementierung des REST Services zum versenden von SMS, erzeugt einen Proxy fuer <link>IOpenIdmRESTProxService</link>
 */
public class OpenIdmService {


	public static final String QUOTA = "quota";
	@Inject
	private EbeguConfiguration configuration;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	Logger logger;

	private IOpenIdmRESTProxService idmRESTProxService;


	public Response login() {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();


		Response response = getSMSGatewayService().login(user, pass);
		String responseString = response.readEntity(String.class);

		return response;

	}

	/**
	 * lazy init den gateway proxy fuer das versenden vons sms
	 */
	private IOpenIdmRESTProxService getSMSGatewayService() {
		if (idmRESTProxService == null) {
			String baseURL = configuration.getOpenIdmURL();
			ResteasyClient client = buildClient();
			ResteasyWebTarget target = client.target(baseURL);
			this.idmRESTProxService = target.proxy(IOpenIdmRESTProxService.class);
		}
		return idmRESTProxService;
	}

	/**
	 * erstellt einen neuen ResteasyClient
	 */
	private ResteasyClient buildClient() {
		ResteasyClientBuilder builder = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS);
		//.trustStore(ks) so koennte man hier auch einen keystore einfuegen
		if (configuration.getIsDevmode() || logger.isDebugEnabled()) { //wenn debug oder dev mode dann loggen wir den request
			builder.register(new ClientRequestLogger());
			builder.register(new ClientResponseLogger());
		}
		return builder.build();
	}


}
