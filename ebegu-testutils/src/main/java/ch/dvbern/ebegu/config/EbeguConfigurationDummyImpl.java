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

package ch.dvbern.ebegu.config;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

/**
 * Konfiguration fuer Testing
 */
@Alternative
@Dependent
@Priority(1)
public class EbeguConfigurationDummyImpl extends EbeguConfigurationImpl {

	private static final long serialVersionUID = 7880484074016308515L;


	@Override
	public boolean isSendingOfMailsDisabled() {
		return true;
	}

	@Override
	public String getSenderAddress() {
		return "dummyconfig@example.com";
	}

	@Override
	public String getHostname() {
		return "localhost";
	}

	@Override
	public boolean getIsZahlungenTestMode() {
		return true;
	}
}
