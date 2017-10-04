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

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.authentication.AuthLoginElement;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service fuer die Authentifizierung eines Benutzers
 */
public interface AuthService {

	/**
	 * Verifiziert die User Credentials, Wenn erfolgreich wird das login als authorisierter_benutzer persisstiert
	 *
	 * @param loginElement beinhaltet die User Credentials
	 * @return Authentication Response, falls das Login erfolgriech war, sonst NULL
	 */
	@Nonnull
	Optional<AuthAccessElement> login(@Nonnull AuthLoginElement loginElement);

	/**
	 * @param authToken Authentifizierungs Token Identifikation
	 * @return TRUE falls das Logout erfolgreich war, sonst FALSE
	 */
	boolean logout(@Nonnull final String authToken);


	@Nonnull
	AuthAccessElement createLoginFromIAM(AuthorisierterBenutzer authorisierterBenutzer);

	/**
	 * gets the logged in user based on the login token
	 * if doRefresh is true, the login cookie gets refreshed.
	 */
	Optional<AuthorisierterBenutzer> validateAndRefreshLoginToken(String token, boolean doRefreshToken);
}
