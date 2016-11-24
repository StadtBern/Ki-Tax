package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.util.crypto.PBKDF2PasswordHash;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

/**
 * Hilfsklasse die die URL fuer SAML Anmeldungen ans IAM genau einmal zusammenstellen soll
 */
@Singleton
public class UsernameRoleChecker {

	private static final Logger LOG = LoggerFactory.getLogger(UsernameRoleChecker.class);
	private static final String ROLE_SEPARATOR = ",";
	private static final String ROLES_MISSING_MESSAGE = "Could not initialaze UsernameRoleChecker because roles could not be loaded: ";
	private static final String USER_MISSING_MESSAGE = "Could not initialaze UsernameRoleChecker because users property file  could not be loaded: ";


	/**
	 * The name of the default properties resource containing user/passwords
	 */
	private String defaultUsersRsrcName = "dummylogin-users.properties";
	/**
	 * The name of the default properties resource containing user/roles
	 */
	private String defaultRolesRsrcName = "dummylogin-roles.properties";
	/**
	 * The name of the properties resource containing user/passwords
	 */
	private String usersRsrcName = null;
	/**
	 * The name of the properties resource containing user/roles
	 */
	private String rolesRsrcName = null;
	/**
	 * The users.properties mappings
	 */
	private Properties users;
	/**
	 * The roles.properties mappings
	 */
	private Properties roles;


	@SuppressWarnings(value = {"PMD.UnusedPrivateMethod", "PreserveStackTrace"})
	@PostConstruct()
	public void init() {
		// Load the properties file that contains the list of users and passwords
		loadUsers();
		loadRoles();
	}


	/**
	 * Loads the roles Properties from the defaultRolesRsrcName and rolesRsrcName
	 * resource settings.
	 *
	 * @throws IOException - thrown on failure to load the properties file.
	 */
	private void loadRoles() {
		String pathToLoad = StringUtils.isEmpty(rolesRsrcName) ? defaultRolesRsrcName : rolesRsrcName;
		URL url = this.getClass().getClassLoader().getResource(pathToLoad);
		if (url == null) {
			throw new EbeguRuntimeException("loadRoles()", ROLES_MISSING_MESSAGE + pathToLoad, pathToLoad);
		}
		try {
			try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(pathToLoad)) {
				roles = new Properties();
				roles.load(is);

			}
		} catch (IOException e) {
			throw new EbeguRuntimeException("loadRoles()", ROLES_MISSING_MESSAGE + pathToLoad, e, pathToLoad);
		}
	}

	/**
	 * Loads the users Properties from the defaultUsersRsrcName and rolesRsrcName
	 * resource settings.
	 *
	 * @throws IOException - thrown on failure to load the properties file.
	 */
	private void loadUsers() {
		String pathToLoad = StringUtils.isEmpty(usersRsrcName) ? defaultUsersRsrcName : usersRsrcName;
		URL url = this.getClass().getClassLoader().getResource(pathToLoad);
		if (url == null) {
			throw new EbeguRuntimeException("loadUsers", USER_MISSING_MESSAGE + pathToLoad, pathToLoad);
		}
		try {
			try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(pathToLoad)) {
				users = new Properties();
				users.load(is);

			}
		} catch (IOException e) {
			throw new EbeguRuntimeException("loadUsers", USER_MISSING_MESSAGE + pathToLoad, e, pathToLoad);
		}
	}

	@Lock(value = LockType.READ)
	protected String getUsersPassword(String username) {
		String password = null;
		if (username != null) {
			password = users.getProperty(username, null);
		}
		return password;
	}

	@Lock(value = LockType.READ)
	public boolean checkLogin(String inputUsername, String inputPassword) {
		// match against property file, null is not an acceptable password
		String hashToMatchAgainst = getUsersPassword(inputUsername);
		if (hashToMatchAgainst == null) {
			LOG.trace("No password passed to validate");
			return false;
		}

		if (this.passwordMatchesHash(inputPassword, hashToMatchAgainst)) {
			return true;
		}
		LOG.trace("Username / Pwassword were invalid " + inputUsername + " / " + inputPassword);
		return false;
	}

	/**
	 * check against stored PBKDF2 hash with integraded salt and interationcount
	 */
	private boolean passwordMatchesHash(String inputPassword, String hashedPassword) {
		try {
			return PBKDF2PasswordHash.validatePassword(inputPassword, hashedPassword);
		} catch (NoSuchAlgorithmException e) {
			throw new EbeguRuntimeException("hashPassword", "Hash algorithm could not be created", e, "NoSuchAlgorithmException");
		} catch (InvalidKeySpecException e) {
			throw new EbeguRuntimeException("hashPassword", "Hash algorithm could not be created", e, "InvalidKeySpecException");
		}
	}


	/**
	 * Create the set of roles the user belongs to by parsing the roles.properties
	 * data for username=role1,role2,... and username.XXX=role1,role2,...
	 * patterns.
	 */
	@Nullable
	@Lock(value = LockType.READ)
	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	protected String[] getRoles(@Nullable String username) {
		if (username == null) {
			return null;
		}
		String roleString = this.roles.getProperty(username, null);
		if (roleString != null) {
			return roleString.split(ROLE_SEPARATOR);
		}
		return null;
	}

	@Lock(value = LockType.READ)
	public String getSingleRole(@Nullable String username) {
		if (username == null) {
			return null;
		}
		String[] foundRoles = getRoles(username);
		if (foundRoles != null) {
			if (foundRoles.length > 1) {
				throw new EbeguRuntimeException("getSingleRole", "Found too many roles for user ", username, foundRoles);
			}
			return foundRoles[0];
		}
		return null;

	}

}

