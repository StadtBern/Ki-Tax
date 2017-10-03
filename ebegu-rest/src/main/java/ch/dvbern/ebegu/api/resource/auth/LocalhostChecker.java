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

package ch.dvbern.ebegu.api.resource.auth;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heloer to check if a request originates from localhost
 */
@ApplicationScoped
public class LocalhostChecker {


	private final Logger LOG = LoggerFactory.getLogger(LocalhostChecker.class.getSimpleName());

	private Set<String> localAddresses = new HashSet<>();


	@SuppressWarnings({"OverlyBroadCatchBlock", "PMD.UnusedPrivateMethod"})
	@PostConstruct
	private void init(){
		try {
			localAddresses.add(InetAddress.getLocalHost().getHostAddress());
			for (InetAddress inetAddress : InetAddress.getAllByName("localhost")) {
				localAddresses.add(inetAddress.getHostAddress());
			}
		} catch (IOException e) {
			LOG.error("Could not find addresses for localhost ", e);
			throw new EbeguRuntimeException("init localhost checker", "Unable to lookup local addresses", e);
		}
	}

	public boolean isAddressLocalhost(String localhost) {
		return localAddresses.contains(localhost);

	}

}
