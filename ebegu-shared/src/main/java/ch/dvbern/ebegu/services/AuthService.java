package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.authentication.AuthLoginElement;
import ch.dvbern.ebegu.authentication.BenutzerCredentials;

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
	 * @param username  Benutzername Identifikation
	 * @param authToken Authentifizierungs Token Identifikation
	 * @return BenutzerCredentials mit der angegebenen Identifikation
	 */
	@Nonnull
	Optional<BenutzerCredentials> loginWithToken(@Nonnull final String username,  @Nonnull final String authToken);

	/**
	 * @param authToken Authentifizierungs Token Identifikation
	 * @return TRUE falls das Logout erfolgreich war, sonst FALSE
	 */
	boolean logout(@Nonnull final String authToken);

	/**
	 * @param credentials Token spezifische Credentials
	 * @return {@link ch.dvbern.ebegu.entities.AuthorisierterBenutzer#getId()} wenn der Login noch gut ist
	 */
	Optional<String> verifyToken(@Nonnull BenutzerCredentials credentials);

}
