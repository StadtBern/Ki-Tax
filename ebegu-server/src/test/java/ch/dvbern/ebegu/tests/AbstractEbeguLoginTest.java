package ch.dvbern.ebegu.tests;

import java.util.Optional;

import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.tets.util.JBossLoginContextFactory.createLoginContext;

/**
 * Diese Klasse loggt vor jeder testmethode als superadmin ein und danach wieder aus.
 * Zudem wird der superadmin in der dp erstellt
 */
public abstract class AbstractEbeguLoginTest extends AbstractEbeguTest {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEbeguLoginTest.class);
	private LoginContext loginContext;

	@Inject
	private Persistence persistence;
	private Benutzer dummyAdmin;

	@Inject
	private BenutzerService benutzerService;

	@Before
	public void performLogin() {
		dummyAdmin = TestDataUtil.createDummySuperAdmin(persistence);
		try {
			loginAsSuperadmin();
		} catch (LoginException ex) {
			LOG.error("Konnte dummy login nicht vornehmen fuer ArquillianTests ", ex);
		}
	}

	protected void loginAsSuperadmin() throws LoginException {
		loginContext = JBossLoginContextFactory.createLoginContext("superadmin", "superadmin");
		loginContext.login();
	}

	@After
	public void performLogout() {
		try {
			if (loginContext != null) {
				loginContext.logout();
			}
		} catch (LoginException e) {
			LOG.error("Konnte dummy loginnicht ausloggen ", e);
		}
	}

	public Benutzer getDummySuperadmin() {
		return dummyAdmin;
	}

	protected Benutzer loginAsGesuchsteller(String username) {
		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer user = createOrFindBenutzer(UserRole.GESUCHSTELLER, username, null, null, mandant);
		user = persistence.merge(user);
		try {
			createLoginContext(username, username).login();
		} catch (LoginException e) {
			LOG.error("could not login as gesuchsteller {} for tests", username);
		}
		return user;
		//theoretisch sollten wir wohl zuerst ausloggen bevor wir wieder einloggen aber es scheint auch so zu gehen
	}

	protected Benutzer loginAsSchulamt() {
		try {
			createLoginContext("schulamt", "schulamt").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter schulamt for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer schulamt = createOrFindBenutzer(UserRole.SCHULAMT, "schulamt", null, null, mandant);
		return persistence.merge(schulamt);
	}

	protected void loginAsSteueramt() {
		try {
			createLoginContext("steueramt", "steueramt").login();
		} catch (LoginException e) {
			LOG.error("could not login as steueramt for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer steueramt = createOrFindBenutzer(UserRole.STEUERAMT, "steueramt", null, null, mandant);
		persistence.merge(steueramt);
	}

	protected Benutzer loginAsSachbearbeiterJA() {
		try {
			createLoginContext("saja", "saja").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt saja for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer saja = createOrFindBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.merge(saja);
		return saja;
	}

	protected void loginAsAdmin() {
		try {
			createLoginContext("admin", "admin").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt admin for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer admin = createOrFindBenutzer(UserRole.ADMIN, "admin", null, null, mandant);
		persistence.merge(admin);
	}

	protected Benutzer loginAsSachbearbeiterInst(String username, Institution institutionToSet) {
		Benutzer user = createOrFindBenutzer(UserRole.SACHBEARBEITER_INSTITUTION, username, null, institutionToSet, institutionToSet.getMandant());
		user = persistence.merge(user);
		try {
			createLoginContext(username, username).login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt {} for tests", username);
		}
		return user;
		//theoretisch sollten wir wohl zuerst ausloggen bevor wir wieder einloggen aber es scheint auch so zu gehen
	}

	private Benutzer createOrFindBenutzer(UserRole role, String userName, Traegerschaft traegerschaft, Institution institution, Mandant mandant) {
		Optional<Benutzer> benutzer = benutzerService.findBenutzer(userName);
		return benutzer.orElseGet(() -> TestDataUtil.createBenutzer(role, userName, traegerschaft, institution, mandant));
	}
}
