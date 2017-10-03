package ch.dvbern.ebegu.api.client;

import javax.annotation.security.RunAs;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.inject.Inject;

import ch.dvbern.ebegu.api.resource.InstitutionResource;
import ch.dvbern.ebegu.api.resource.TraegerschaftResource;
import ch.dvbern.ebegu.enums.UserRoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class synchronizes traegerschaften and institution with iam on server startup
 */
@Startup
@Singleton
@RunAs(value = UserRoleName.SUPER_ADMIN)
public class OpenIdmSynchronizer {

	private static final Logger LOG = LoggerFactory.getLogger(OpenIdmSynchronizer.class.getSimpleName());

	@Inject
	private TraegerschaftResource traegerschaftResource;

	@Inject
	private InstitutionResource institutionResource;

	@Timeout
	public void startSync() {
		try {
			LOG.info("Synchnoize with Open IDM");
			StringBuilder resultStringTraegerschaft = traegerschaftResource.synchronizeTraegerschaft(true);
			LOG.info(resultStringTraegerschaft.toString());
			StringBuilder resultStringInstitution = institutionResource.synchronizeInstitutions(true);
			LOG.info(resultStringInstitution.toString());
			LOG.info("Synchronization with Open IDM finished!");
		} catch (Exception e) {
			LOG.error("Could not start institution sync", e);
		}
	}
}
