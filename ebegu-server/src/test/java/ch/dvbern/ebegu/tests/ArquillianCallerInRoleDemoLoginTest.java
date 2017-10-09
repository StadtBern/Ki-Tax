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
package ch.dvbern.ebegu.tests;

import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests demonstrating access to user roles.
 */
@RunWith(Arquillian.class)
public class ArquillianCallerInRoleDemoLoginTest extends AbstractEbeguLoginTest {

	@Inject
	private ISessionContextService sessionContextService;

	@Test
	public void testLoginPrincipal() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory.createLoginContext("saja", "saja");
		loginContext.login();
		Principal callerPrincipal = sessionContextService.getCallerPrincipal();
		Assert.assertNotNull(callerPrincipal);
		Assert.assertNotNull(callerPrincipal.getName(), "saja");
		Assert.assertTrue(sessionContextService.isCallerInRole(UserRoleName.SACHBEARBEITER_JA));
		loginContext.logout();
		Principal anonPrincipal = sessionContextService.getCallerPrincipal();
		Assert.assertNotNull(anonPrincipal);
		Assert.assertNotNull(anonPrincipal.getName(), "anonymous");
	}

	@Test
	public void testDiscoverRoles() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory.createLoginContext("admin", "admin");
		loginContext.login();
		try {
			Set<String> foundRoles = Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Set<String>>() {

				@Override
				public Set<String> run() {
					if (sessionContextService.isCallerInRole("ADMIN")) {
						Set<String> res = new HashSet<>();
						res.add("ADMIN");
						return res;
					}
					return new HashSet<>();
				}

			});
			assertEquals(1, foundRoles.size());
			assertTrue(foundRoles.contains("ADMIN"));

		} finally {
			loginContext.logout();
		}
	}
}
