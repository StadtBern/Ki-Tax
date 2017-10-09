/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
