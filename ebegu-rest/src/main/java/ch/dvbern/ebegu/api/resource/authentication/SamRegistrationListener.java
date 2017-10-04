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

package ch.dvbern.ebegu.api.resource.authentication;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import org.omnifaces.security.jaspic.core.Jaspic;
import org.omnifaces.security.jaspic.listeners.BaseServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SamRegistrationListener extends BaseServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(SamRegistrationListener.class);

	@Inject
	private EbeguConfiguration configuration;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final CookieTokenAuthModule authModule;
		String intUsr = configuration.getInternalAPIUser();
		String intPW = configuration.getInternalAPIPassword();
		//either construct the module with or without password for external login
		if (intUsr == null && intPW == null) {
			authModule = new CookieTokenAuthModule();
			LOG.debug("No user or password for internal api configured, the api will be inactive");
		} else {
			authModule = new CookieTokenAuthModule(intUsr, intPW);
		}

		Jaspic.registerServerAuthModule(authModule, sce.getServletContext());
	}

}
