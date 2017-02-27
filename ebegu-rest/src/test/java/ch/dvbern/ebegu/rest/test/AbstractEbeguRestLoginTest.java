package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import static ch.dvbern.ebegu.tets.util.JBossLoginContextFactory.createLoginContext;

/**
 * Diese Klasse loggt vor jeder testmethode als superadmin ein und danach wieder aus.
 * Zudem wird der superadmin in der dp erstellt
 */
public abstract class AbstractEbeguRestLoginTest extends AbstractEbeguRestTest {


	private static final Logger LOG = LoggerFactory.getLogger(AbstractEbeguRestLoginTest.class);
	private  LoginContext loginContext;

	@Inject
	private Persistence<Gesuch> persistence;
	private Benutzer dummyAdmin;

	@Before
	public  void performLogin() {
		dummyAdmin = TestDataUtil.createDummySuperAdmin(persistence);
		try {
			loginContext = JBossLoginContextFactory.createLoginContext("superadmin", "superadmin");
			loginContext.login();
		} catch (LoginException ex) {
			LOG.error("Konnte dummy login nicht vornehmen fuer ArquillianTests ", ex);
		}
	}

	@After
	public  void performLogout() {
		try {
			if (loginContext != null) {
				loginContext.logout();
			}
		} catch (LoginException e) {
			LOG.error("Konnte dummy loginnicht ausloggen ", e);
		}
	}

	protected Benutzer loginAsSachbearbeiterJA() {
		try {
			createLoginContext("saja", "saja").login();
		} catch (LoginException e) {
			LOG.error("could not login as sachbearbeiter jugendamt saja for tests");
		}

		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer saja = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.persist(saja);
		return saja;
	}

	public Benutzer getDummySuperadmin() {
		return dummyAdmin;
	}
}
