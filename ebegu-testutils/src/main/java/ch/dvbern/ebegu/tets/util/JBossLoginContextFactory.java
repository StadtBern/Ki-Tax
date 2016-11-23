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
package ch.dvbern.ebegu.tets.util;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a {@link LoginContext} for use by unit tests. It is driven by users.properties and roles.properties files as
 * described in <a href="https://community.jboss.org/wiki/UsersRolesLoginModule">UsersRolesLoginModule</a>
 */
public class JBossLoginContextFactory {

    static class NamePasswordCallbackHandler implements CallbackHandler {
        private final String username;
        private final String password;

        private NamePasswordCallbackHandler(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback current : callbacks) {
                if (current instanceof NameCallback) {
                    ((NameCallback) current).setName(username);
                } else if (current instanceof PasswordCallback) {
                    ((PasswordCallback) current).setPassword(password.toCharArray());
                } else {
                    throw new UnsupportedCallbackException(current);
                }
            }
        }
    }

    static class JBossJaasConfiguration extends Configuration {
        private final String configurationName;

        JBossJaasConfiguration(String configurationName) {
            this.configurationName = configurationName;
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            if (!configurationName.equals(name)) {
                throw new IllegalArgumentException("Unexpected configuration name '" + name + "'");
            }

            return new AppConfigurationEntry[] {

            createUsersRolesLoginModuleConfigEntry(),

            createClientLoginModuleConfigEntry(),

            };
        }

        /**
         * The {@link org.jboss.security.auth.spi.UsersRolesLoginModule} creates the association between users and
         * roles.
         *
         * @return
         */
        private AppConfigurationEntry createUsersRolesLoginModuleConfigEntry() {
            Map<String, String> options = new HashMap<String, String>();
            return new AppConfigurationEntry("org.jboss.security.auth.spi.UsersRolesLoginModule",
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
        }

        /**
         * The {@link org.jboss.security.ClientLoginModule} associates the user credentials with the
         * {@link org.jboss.security.SecurityContext} where the JBoss security runtime can find it.
         *
         * @return
         */
        private AppConfigurationEntry createClientLoginModuleConfigEntry() {
            Map<String, String> options = new HashMap<String, String>();
            options.put("multi-threaded", "true");
            options.put("restore-login-identity", "true");

            return new AppConfigurationEntry("org.jboss.security.ClientLoginModule",
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
        }
    }

    /**
     * Obtain a LoginContext configured for use with the ClientLoginModule.
     *
     * @return the configured LoginContext.
     */
    public static LoginContext createLoginContext(final String username, final String password) throws LoginException {
        final String configurationName = "Arquillian Testing";

        CallbackHandler cbh = new JBossLoginContextFactory.NamePasswordCallbackHandler(username, password);
        Configuration config = new JBossJaasConfiguration(configurationName);

        return new LoginContext(configurationName, new Subject(), cbh, config);
    }

}
