/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests demonstrating access to user roles.
 */
@RunWith(Arquillian.class)
public class ArquillianCallerInRoleDemoLoginTest extends AbstractEbeguTest{

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
                    if(sessionContextService.isCallerInRole("ADMIN")){
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
