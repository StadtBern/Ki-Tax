package ch.dvbern.ebegu.dbschema;

import org.flywaydb.core.Flyway;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
