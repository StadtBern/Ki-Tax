package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse die die URL fuer SAML Anmeldungen ans IAM genau einmal zusammenstellen soll
 */
@Singleton
public class FedletURLInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(FedletURLInitializer.class);


	private String spEntityID;
	private String spMetaAlias;
	private String idpEntityID;

	private static final String BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private URI sSOInitURI;
	private static final String SINGLE_SIGN_ON_INIT_URL = "/saml2/jsp/fedletSSOInit.jsp";
	private static final String SINGLE_LOGOUT_INIT_URL = "/fedletSloInit";

	//single logout params


	@PostConstruct()
	private void init() {
		SAML2MetaManager manager = null;
		try {
			manager = new SAML2MetaManager();
			initSPEntityID(manager);
			initSPMetaAlias(manager);
			initIDPEntities(manager);

			URIBuilder uriBuilder = new URIBuilder(SINGLE_SIGN_ON_INIT_URL);
			uriBuilder.addParameter("metaAlias", spMetaAlias);
			uriBuilder.addParameter("idpEntityID", idpEntityID);
			uriBuilder.addParameter("binding", BINDING);
			this.sSOInitURI = uriBuilder.build();


		} catch (SAML2MetaException | URISyntaxException e) {
			LOG.error("Could not initialize Fedlet with URLS for SAML Login", e);
			throw new EbeguRuntimeException("init", "Could not initialize Fedlet correctly", spMetaAlias, idpEntityID);
		}
	}


	private void initSPMetaAlias(SAML2MetaManager manager) throws SAML2MetaException {
		List spMetaAliases =
			manager.getAllHostedServiceProviderMetaAliases("/");
		if ((spMetaAliases != null) && !spMetaAliases.isEmpty()) {
			// get first one
			spMetaAlias = (String) spMetaAliases.get(0);
		}
	}

	private void initSPEntityID(SAML2MetaManager manager) throws SAML2MetaException {
		List<String> spEntities =
			manager.getAllHostedServiceProviderEntities("/");
		if ((spEntities != null) && !spEntities.isEmpty()) {
			// get first one
			spEntityID = (String) spEntities.get(0);
		}
	}


	private void initIDPEntities(SAML2MetaManager manager) throws SAML2MetaException {
		List<String> trustedIDPs = new ArrayList<>();
		// find out all trusted IDPs
		List<String> idpEntities = manager.getAllRemoteIdentityProviderEntities("/");
		if ((idpEntities != null) && !idpEntities.isEmpty()) {
			int numOfIDP = idpEntities.size();
			for (int j = 0; j < numOfIDP; j++) {
				String idpID = idpEntities.get(j);
				if (manager.isTrustedProvider("/",
					spEntityID, idpID)) {
					trustedIDPs.add(idpID);
				}
			}
		}

		if (trustedIDPs.size() > 1) {
			// multiple trusted IDPs
			LOG.warn("Multiple Identity Providers are configured, currently ebegu only supports one");
			for (String trustedIDP : trustedIDPs) {
				LOG.warn(trustedIDP);

			}

		} else if (!trustedIDPs.isEmpty()) {
			// get the single IDP entity ID
			idpEntityID = (String) trustedIDPs.get(0);
		} else {
			LOG.warn("NO idpEntityID configured");

		}

	}

	/**
	 * To initialize the single sign on url we need at least the spMetaAlias and the idpEntityID
	 *
	 * @return
	 * @throws URISyntaxException
	 */
	@Lock(LockType.READ)
	public URI getSSOInitURI() {
		return this.sSOInitURI;
	}


	public String getSpMetaAlias() {
		return spMetaAlias;
	}


	public String getSpEntityID() {
		return spEntityID;
	}

	public void setSpEntityID(String spEntityID) {
		this.spEntityID = spEntityID;
	}

	public String getIdpEntityID() {
		return idpEntityID;
	}

	public void setIdpEntityID(String idpEntityID) {
		this.idpEntityID = idpEntityID;
	}


	public URI createLogoutURI(String nameID, String sessionID) {

		try {
			URIBuilder uriBuilder = new URIBuilder(SINGLE_LOGOUT_INIT_URL);
			uriBuilder.addParameter("spEntityID", spEntityID);
			uriBuilder.addParameter("idpEntityID", idpEntityID);
			uriBuilder.addParameter("binding", BINDING);
			uriBuilder.addParameter("NameIDValue", nameID);
			uriBuilder.addParameter("SessionIndex", sessionID);
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			LOG.error("Could not initialize Fedlet with URLS for SAML Logout", e);
			throw new EbeguRuntimeException("init", "Could not initialize Fedlet correctly", spEntityID, idpEntityID, nameID);
		}
	}
}
