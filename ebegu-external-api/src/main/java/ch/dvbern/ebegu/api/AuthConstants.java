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

package ch.dvbern.ebegu.api;

/**
 * Constants that are important in connection with Authentication stuff (cookie, headerparams etc)
 */
public interface AuthConstants {
	String COOKIE_PATH = "/";
	String COOKIE_DOMAIN = null;
	String COOKIE_PRINCIPAL = "authId";
	String COOKIE_AUTH_TOKEN = "authToken";
	String PARAM_XSRF_TOKEN = "X-XSRF-TOKEN";
	String COOKIE_XSRF_TOKEN = "XSRF-TOKEN";
	int COOKIE_TIMEOUT_SECONDS = 60 * 60 * 12; //aktuell 12h
	String OPENIDM_INST_PREFIX = "I-";
	String OENIDM_TRAEGERSCHAFT_PREFIX = "T-";
	/**
	 * Path to locallogin page (relative to base path) that will be used if no login connector api is specified
	 */
	String LOCALLOGIN_PATH = "/#/locallogin";
}
