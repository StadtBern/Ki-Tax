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

	String ERROR_USERNAME_INVALID = "G\u00fcltige E-Mail-Adresse eingeben.";

	/**
	 * Verifiziert die User Credentials
	 *
	 * @param loginElement beinhaltet die User Credentials
	 * @return Authentication Response, falls das Login erfolgriech war, sonst NULL
	 */
	@Nonnull
//	Optional<AuthAccessElement> login(@Nonnull AuthLoginElement loginElement) throws RuleViolationException;
	Optional<AuthAccessElement> login(@Nonnull AuthLoginElement loginElement);

	/**
	 * @param username  Benutzername Identifikation
	 * @param authToken Authentifizierungs Token Identifikation
	 * @return BenutzerCredentials mit der angegebenen Identifikation
	 */
	@Nonnull
	Optional<BenutzerCredentials> loginWithToken(@Nonnull final String username, @Nonnull final String authToken);

	/**
	 * @param authToken Authentifizierungs Token Identifikation
	 * @return TRUE falls das Logout erfolgreich war, sonst FALSE
	 */
	boolean logout(@Nonnull final String authToken);

	/**
	 * @param credentials Token spezifische Credentials
	 * @return TRUE falls der Token gültig ist, sonst FALSE
	 */
	boolean verifyToken(@Nonnull BenutzerCredentials credentials);

//	/**
//	 * Holt das Salt vom User und erzeugt damit ein gehashtes Passwort.
//	 * @throws RuleViolationException Wenn der Username nicht gefunden wurde
//	 * @throws PasswordPreReqsException Wenn die Voraussetzungen fuer die Passwort-Verschluesselung nicht gegeben sind (z.B. Passwort zu lange)
//	 */
//	@Nonnull
//	HashedPassword encryptPassword(@Nonnull String username, @Nonnull String plaintextPassword) throws PasswordPreReqsException, RuleViolationException;
//
//	/**
//	 * Erzeugt ein neues gehashtes Passwort
//	 * @throws PasswordPreReqsException Wenn die Voraussetzungen fuer die Passwort-Verschluesselung nicht gegeben sind (z.B. Passwort zu lange)
//	 */
//	@Nonnull
//	HashedPassword createPassword(@Nonnull String plaintextPassword) throws PasswordPreReqsException;
//
//	boolean checkPassword(@Nonnull String oldEncryptedPassword, @Nonnull String newEncryptedPassword) throws PasswordPreReqsException;
}
