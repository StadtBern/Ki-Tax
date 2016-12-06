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


	public Optional<JaxOpenIdmResponse> getAll() {
		return getAll(false);
	}

	public Optional<JaxOpenIdmResponse> getAll(boolean force) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			Response response = getIdmRESTProxService().getAllInstitutions(user, pass, true, true);
			if (checkSucess(response, "getAll")) {
				JaxOpenIdmResponse jaxOpenIdmResponse = response.readEntity(JaxOpenIdmResponse.class);
				return Optional.of(jaxOpenIdmResponse);
			}

			response.readEntity(String.class);
		}
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> getInstitutionByUid(String uid) {
		return getInstitutionByUid(uid, false);
	}

	public Optional<JaxOpenIdmResult> getInstitutionByUid(String uid, boolean force) {
		return getById(force, getOpenIdmInstitutionsUID(uid));
	}

	public Optional<JaxOpenIdmResult> getTraegerschaftByUid(String uid) {
		return getTraegerschaftByUid(uid, false);
	}

	public Optional<JaxOpenIdmResult> getTraegerschaftByUid(String uid, boolean force) {
		return getById(force, getOpenIdmTraegerschaftUID(uid));
	}

	private Optional<JaxOpenIdmResult> getById(boolean force, String openIdmTraegerschaftUID) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			Response response = getIdmRESTProxService().getInstitutionbyUid(user, pass, false, openIdmTraegerschaftUID);
			if (checkSucess(response, "getByUid")) {
				JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
				return Optional.of(jaxOpenIdmResult);
			}
			response.readEntity(String.class);
		}
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> createInstitution(Institution institution) {
		return createInstitution(institution, false);
	}

	public Optional<JaxOpenIdmResult> createInstitution(Institution institution, boolean force) {
		return create(force, institution.getName(), INSTITUTION, getOpenIdmInstitutionsUID(institution.getId()));
	}

	private String getOpenIdmInstitutionsUID(String institutionId) {
		return "I-" + institutionId;
	}

	private String getOpenIdmTraegerschaftUID(String traegerschaftId) {
		return "T-" + traegerschaftId;
	}

	public String getEbeguId(String openIdmUid) {
		return openIdmUid.substring(2);
	}

	public Optional<JaxOpenIdmResult> createTraegerschaft(Traegerschaft traegerschaft) {
		return createTraegerschaft(traegerschaft, false);
	}

	public Optional<JaxOpenIdmResult> createTraegerschaft(Traegerschaft traegerschaft, boolean force) {
		return create(force, traegerschaft.getName(), TRAEGERSCHAFT, getOpenIdmTraegerschaftUID(traegerschaft.getId()));
	}

	private Optional<JaxOpenIdmResult> create(boolean force, String name, String type, String openIdmUID) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			JaxInstitutionOpenIdm jaxInstitutionOpenIdm = new JaxInstitutionOpenIdm();
			jaxInstitutionOpenIdm.setName(name);
			jaxInstitutionOpenIdm.setType(type);
			jaxInstitutionOpenIdm.setMail("");

			Response response = getIdmRESTProxService().create(user, pass, openIdmUID, jaxInstitutionOpenIdm);

			if (checkSucess(response, "Create")) {
				JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
				return Optional.of(jaxOpenIdmResult);
			}
			response.readEntity(String.class);
		}
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> updateInstitution(Institution institution) {
		return updateInstitution(institution, false);
	}

	public Optional<JaxOpenIdmResult> updateInstitution(Institution institution, boolean force) {
		if (configuration.getOpenIdmEnabled() || force) {
			final boolean deleted = deleteInstitution(institution.getId(), force);
			if (deleted) {
				return createInstitution(institution, force);
			}
		}
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> updateTraegerschaft(Traegerschaft traegerschaft) {
		return updateTraegerschaft(traegerschaft, false);
	}

	public Optional<JaxOpenIdmResult> updateTraegerschaft(Traegerschaft traegerschaft, boolean force) {
		if (configuration.getOpenIdmEnabled() || force) {
			final boolean deleted = deleteTraegerschaft(traegerschaft.getId(), force);
			if (deleted) {
				return createTraegerschaft(traegerschaft, force);
			}
		}
		return Optional.empty();
	}

	public boolean deleteInstitution(String id) {
		return deleteByOpenIdmUid(id, false);
	}

	public boolean deleteInstitution(String institutionId, boolean force) {
		return deleteByOpenIdmUid(getOpenIdmInstitutionsUID(institutionId), force);
	}

	public boolean deleteTraegerschaft(String traegerschaftId) {
		return deleteByOpenIdmUid(getOpenIdmTraegerschaftUID(traegerschaftId), false);
	}

	public boolean deleteTraegerschaft(String traegerschaftId, boolean force) {
		return deleteByOpenIdmUid(getOpenIdmTraegerschaftUID(traegerschaftId), force);
	}

	public boolean deleteByOpenIdmUid(String uid, boolean force) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			Response response = getIdmRESTProxService().delete(user, pass, uid);
			response.readEntity(String.class);

			return checkSucess(response, "delete");
		}
		return false;
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

		if (configuration.getIsDevmode()) { //wenn debug oder dev mode dann loggen wir den request
			builder.register(new ClientRequestLogger());
			builder.register(new ClientResponseLogger());
		}
		return builder.build();
	}

	public void generateResponseString(StringBuilder responseString, String id, String name, boolean present, final String msg) {
		responseString.append(msg).append(" on OpenIdm Id = ").append(id).append(", Name = ").append(name);
		if (present) {
			responseString.append(" -> OK");
		} else {
			responseString.append(" -> FAILED");
		}
		responseString.append(System.lineSeparator());
	}


}
