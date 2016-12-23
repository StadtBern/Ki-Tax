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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Implementierung des REST Services zum synchronisieren mit OpenIdm, erzeugt einen Proxy fuer <link>IOpenIdmRESTProxService</link>
 */
@Stateless
@RolesAllowed(value ={ADMIN, SUPER_ADMIN})
public class OpenIdmRestService {

	private static final Logger LOG = LoggerFactory.getLogger(OpenIdmRestService.class.getSimpleName());

	public static final String INSTITUTION = "institution";
	public static final String TRAEGERSCHAFT = "sponsor";

	@Inject
	private EbeguConfiguration configuration;

	private IOpenIdmRESTProxClient openIdmRESTProxClient;


	public Optional<JaxOpenIdmResponse> getAll() {
		return getAll(false);
	}

	public Optional<JaxOpenIdmResponse> getAll(boolean force) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			Response response = getOpenIdmRESTProxClient().getAllInstitutions(user, pass, true, true);
			if (checkSucess(response, "getAll")) {
				JaxOpenIdmResponse jaxOpenIdmResponse = response.readEntity(JaxOpenIdmResponse.class);
				return Optional.of(jaxOpenIdmResponse);
			} else {
				//im error fall muss trotzdem die response ausgelesen werden sonst gibt es spaeter exceptions
				String errorContent = response.readEntity(String.class);
				LOG.error("ErrorContent: " +errorContent);
			}
		}
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> getInstitutionByUid(String uid) {
		return getInstitutionByUid(uid, false);
	}

	public Optional<JaxOpenIdmResult> getInstitutionByUid(String uid, boolean force) {
		return getById(force, convertToOpenIdmInstitutionsUID(uid));
	}

	public Optional<JaxOpenIdmResult> getTraegerschaftByUid(String uid) {
		return getTraegerschaftByUid(uid, false);
	}

	public Optional<JaxOpenIdmResult> getTraegerschaftByUid(String uid, boolean force) {
		return getById(force, convertToOpenIdmTraegerschaftUID(uid));
	}

	private Optional<JaxOpenIdmResult> getById(boolean force, String openIdmTraegerschaftUID) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			Response response = getOpenIdmRESTProxClient().getInstitutionbyUid(user, pass, false, openIdmTraegerschaftUID);
			if (checkSucess(response, "getByUid")) {
				JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
				return Optional.of(jaxOpenIdmResult);
			} else {
				//im error fall muss trotzdem die response ausgelesen werden sonst gibt es spaeter exceptions
				String errorContent = response.readEntity(String.class);
				LOG.error("ErrorContent: " +errorContent);
			}
		}
		return Optional.empty();
	}

	public Optional<JaxOpenIdmResult> createInstitution(Institution institution) {
		return createInstitution(institution, false);
	}

	public Optional<JaxOpenIdmResult> createInstitution(Institution institution, boolean force) {
		return create(force, institution.getName(), INSTITUTION, convertToOpenIdmInstitutionsUID(institution.getId()), institution.getMail());
	}

	@Nonnull
	public String convertToOpenIdmInstitutionsUID(@Nonnull String institutionId) {
		return "I-" + institutionId;
	}

	@Nonnull
	public String convertToOpenIdmTraegerschaftUID(@Nonnull String traegerschaftId) {
		return "T-" + traegerschaftId;
	}

	@Nonnull
	public final String convertToEBEGUID(@Nullable String openIdmUid) {
		if (openIdmUid != null && openIdmUid.length() > 2) {
			return openIdmUid.substring(2);
		} else {
			return "";
		}
	}

	public Optional<JaxOpenIdmResult> createTraegerschaft(Traegerschaft traegerschaft) {
		return createTraegerschaft(traegerschaft, false);
	}

	public Optional<JaxOpenIdmResult> createTraegerschaft(Traegerschaft traegerschaft, boolean force) {
		return create(force, traegerschaft.getName(), TRAEGERSCHAFT, convertToOpenIdmTraegerschaftUID(traegerschaft.getId()), traegerschaft.getMail());
	}

	/**
	 * creates the passed element in openIDM and returns the result entity or nothing if there is no openidm configured
	 */
	private Optional<JaxOpenIdmResult> create(boolean force, String name, String type, String openIdmUID, String mail) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			JaxInstitutionOpenIdm jaxInstitutionOpenIdm = new JaxInstitutionOpenIdm();
			jaxInstitutionOpenIdm.setName(name);
			jaxInstitutionOpenIdm.setType(type);
			jaxInstitutionOpenIdm.setMail(mail);

			Response response = getOpenIdmRESTProxClient().create(user, pass, openIdmUID, jaxInstitutionOpenIdm);

			if (checkSucess(response, "Create")) {
				JaxOpenIdmResult jaxOpenIdmResult = response.readEntity(JaxOpenIdmResult.class);
				return Optional.of(jaxOpenIdmResult);
			} else{
				//im error fall muss trotzdem die response ausgelesen werden sonst gibt es spaeter exceptions
				String errorContent = response.readEntity(String.class);
				LOG.error("ErrorContent: " +errorContent);
			}
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
		return deleteInstitution(id, false);
	}

	public boolean deleteInstitution(String institutionId, boolean force) {
		return deleteByOpenIdmUid(convertToOpenIdmInstitutionsUID(institutionId), force);
	}

	public boolean deleteTraegerschaft(String openIDMTraegerschaftUID) {
		return deleteTraegerschaft(openIDMTraegerschaftUID, false);
	}

	public boolean deleteTraegerschaft(String openIDMTraegerschaftUID, boolean force) {
		return deleteByOpenIdmUid(openIDMTraegerschaftUID, force);
	}

	public boolean deleteByOpenIdmUid(String uid, boolean force) {
		if (configuration.getOpenIdmEnabled() || force) {
			String user = configuration.getOpenIdmUser();
			String pass = configuration.getOpenIdmPassword();

			Response response = getOpenIdmRESTProxClient().delete(user, pass, uid);
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
	 * lazy init den REST proxy fuer die Kommunikation mit OpenIDM
	 */
	private IOpenIdmRESTProxClient getOpenIdmRESTProxClient() {
		if (openIdmRESTProxClient == null) {
			String baseURL = configuration.getOpenIdmURL();
			ResteasyClient client = buildClient();
			ResteasyWebTarget target = client.target(baseURL);
			this.openIdmRESTProxClient = target.proxy(IOpenIdmRESTProxClient.class);
		}
		return openIdmRESTProxClient;
	}

	/**
	 * erstellt einen neuen ResteasyClient
	 */
	private ResteasyClient buildClient() {
		ResteasyClientBuilder builder = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS);

		if (configuration.getIsDevmode() || LOG.isDebugEnabled() ) { //wenn debug oder dev mode dann loggen wir den request
			builder.register(new ClientRequestLogger());
			builder.register(new ClientResponseLogger());
		}
		return builder.build();
	}

	public void generateResponseString(StringBuilder responseString, String id, String name, boolean present, final String msg) {
		responseString.append(msg).append(" in OpenIdm Id = ").append(id).append(", Name = ").append(name);
		if (present) {
			responseString.append(" -> OK");
		} else {
			responseString.append(" -> FAILED");
		}
		responseString.append(System.lineSeparator());
	}

}
