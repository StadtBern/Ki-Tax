package ch.dvbern.ebegu.api.client;


import ch.dvbern.ebegu.api.resource.InstitutionResource;
import ch.dvbern.ebegu.api.resource.TraegerschaftResource;
import ch.dvbern.ebegu.enums.UserRoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.security.RunAs;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.inject.Inject;

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


//	@Resource
//	private TimerService timerService;

	@PostConstruct
	public void startControlBeans() {
//  Habe das noch kurz mit Fraenzi besprochen und wir sehen den Nutzen des automatischen austauschs nicht ganz
//  Lasse die Klase aber mal hier falls wir es spaeter doch brauchen
// 	workaround for bug that prevens runAs to work on @Singleton or @Startup
// @see https://issues.jboss.org/browse/WFLY-981 and  https://developer.jboss.org/thread/175108
//		timerService.createTimer(5 * 1000, "");
	}


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
