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
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementierung des REST Services zum synchronisieren mit OpenIdm, erzeugt einen Proxy fuer <link>IOpenIdmRESTProxService</link>
 */
@Stateless
public class OpenIdmRestClient {

	private static final Logger LOG = LoggerFactory.getLogger(OpenIdmRestClient.class.getSimpleName());

	public static final String INSTITUTION = "institution";
	public static final String TRAEGERSCHAFT = "sponsor";

	@Inject
	private EbeguConfiguration configuration;

	private IOpenIdmRESTProxService idmRESTProxService;


	public Response login() {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		Response response = getIdmRESTProxService().login(user, pass, false);
		checkSucess(response, "login");

		response.readEntity(String.class);

		return response;
	}

	public Optional<JaxOpenIdmResponse> getAll() {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		Response response = getIdmRESTProxService().getAllInstitutions(user, pass, false, true);
		if (checkSucess(response, "getAll")) {
			JaxOpenIdmResponse jaxOpenIdmResponse = response.readEntity(JaxOpenIdmResponse.class);
			return Optional.of(jaxOpenIdmResponse);
		}

		response.readEntity(String.class);
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> getInstitutionByUid(String uid) {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		Response response = getIdmRESTProxService().getInstitutionbyUid(user, pass, false, uid);
		if (checkSucess(response, "getInstitutionByUid")) {
			JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
			return Optional.of(jaxOpenIdmResult);
		}
		response.readEntity(String.class);
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> createInstitution(Institution institution) {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		JaxInstitutionOpenIdm jaxInstitutionOpenIdm = new JaxInstitutionOpenIdm();
		jaxInstitutionOpenIdm.setName(institution.getName());
		jaxInstitutionOpenIdm.setType(INSTITUTION);
		jaxInstitutionOpenIdm.setMail("");

		Response response = getIdmRESTProxService().create(user, pass, institution.getId(), jaxInstitutionOpenIdm);
		if (checkSucess(response, "createInstitution")) {
			JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
			return Optional.of(jaxOpenIdmResult);
		}
		response.readEntity(String.class);
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> createTraegerschaft(Traegerschaft traegerschaft) {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		JaxInstitutionOpenIdm jaxInstitutionOpenIdm = new JaxInstitutionOpenIdm();
		jaxInstitutionOpenIdm.setName(traegerschaft.getName());
		jaxInstitutionOpenIdm.setType(TRAEGERSCHAFT);
		jaxInstitutionOpenIdm.setMail("");

		Response response = getIdmRESTProxService().create(user, pass, traegerschaft.getId(), jaxInstitutionOpenIdm);

		if (checkSucess(response, "createTraegerschaft")) {
			JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
			return Optional.of(jaxOpenIdmResult);
		}
		response.readEntity(String.class);
		return Optional.empty();
	}


	public Optional<JaxOpenIdmResult> updateInstitution(Institution institution) {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		JaxUpdateOpenIdm jaxUpdateOpenIdm = new JaxUpdateOpenIdm();
		jaxUpdateOpenIdm.setOperation("replace");
		jaxUpdateOpenIdm.setField("name");
		jaxUpdateOpenIdm.setValue(institution.getName());

		Response response = getIdmRESTProxService().update(user, pass, institution.getId(), jaxUpdateOpenIdm);

		if (checkSucess(response, "updateInstitution")) {
			JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
			return Optional.of(jaxOpenIdmResult);
		}
		response.readEntity(String.class);
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> updateTraegerschaft(Traegerschaft traegerschaft) {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		JaxUpdateOpenIdm jaxUpdateOpenIdm = new JaxUpdateOpenIdm();
		jaxUpdateOpenIdm.setOperation("replace");
		jaxUpdateOpenIdm.setField("name");
		jaxUpdateOpenIdm.setValue(traegerschaft.getName());

		Response response = getIdmRESTProxService().update(user, pass, traegerschaft.getId(), jaxUpdateOpenIdm);

		if (checkSucess(response, "updateTraegerschaft")) {
			JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
			return Optional.of(jaxOpenIdmResult);
		}
		response.readEntity(String.class);
		return Optional.empty();
	}

	public boolean delete(String id) {
		String user = configuration.getOpenIdmUser();
		String pass = configuration.getOpenIdmPassword();

		Response response = getIdmRESTProxService().delete(user, pass, id);
		response.readEntity(String.class);
		return checkSucess(response, "delete");
	}

	private boolean checkSucess(Response response, String functionName) {
		if (response.getStatus() != Response.Status.OK.getStatusCode() &&
			response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			LOG.error("There was an error during " + functionName + ": HttpStatus " + response.getStatus() + " " + response.getStatusInfo());
			return false;
		}
		return true;
	}

	/**
	 * lazy init den gateway proxy fuer das versenden vons sms
	 */
	private IOpenIdmRESTProxService getIdmRESTProxService() {
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
		if (configuration.getIsDevmode()) { //wenn debug oder dev mode dann loggen wir den request
			builder.register(new ClientRequestLogger());
			builder.register(new ClientResponseLogger());
		}
		return builder.build();
	}

	public void generateResponseString(StringBuilder responseString, String id, String name, boolean present, final String msg) {
		responseString.append(msg + " on OpenIdm Id = ");
		responseString.append(id);
		responseString.append(", Name = ");
		responseString.append(name);
		if (present) {
			responseString.append(" -> OK");
		} else {
			responseString.append(" -> FAILED");
		}
		responseString.append(System.lineSeparator());
	}


}
