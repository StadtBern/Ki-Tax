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

package ch.dvbern.ebegu.dbschema;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

/**
 * Dieses Bean sorgt dafuer, dass beim Startup des Java EE Servers migrate ausgefuehrt wird.
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN) //flyway managed transactions selber
public class MigrateSchema {

	private static final String DATASOURCE_NAME = "jdbc/ebegu";

	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	@PostConstruct
	public void migrateSchema() {
		try {
			// CDI Injection does not work at startup time :-(
			final DataSource dataSource = (DataSource) InitialContext.doLookup(DATASOURCE_NAME);
			final Flyway flyway = new Flyway();
			flyway.setDataSource(dataSource);
			//			flyway.setLocations("/dbscripts", "/ch/dvbern/fzl/kurstool/dbschema"); wir verwenden default
			flyway.setEncoding("UTF-8");
			flyway.migrate();
		} catch (NamingException e) {
			final String msg = ("flyway db migration error (missing datasource '" + DATASOURCE_NAME) + "')";
			throw new RuntimeException(msg, e);
		}
	}
}
