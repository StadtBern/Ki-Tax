package ch.dvbern.ebegu.api.client;


import ch.dvbern.ebegu.api.resource.InstitutionResource;
import ch.dvbern.ebegu.api.resource.TraegerschaftResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class OpenIdmSynchronizer {

	private static final Logger LOG = LoggerFactory.getLogger(OpenIdmSynchronizer.class.getSimpleName());

	@Inject
	private TraegerschaftResource traegerschaftResource;

	@Inject
	private InstitutionResource institutionResource;

	@PostConstruct
	void init()	{

		LOG.info("Synchnoize with Open IDM");
		LOG.info(traegerschaftResource.synchronizeTraegerschaft(true).toString());
		LOG.info(institutionResource.synchronizeInstitutions(true).toString());
	}

}
