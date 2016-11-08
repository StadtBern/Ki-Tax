package db.migration;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.services.EbeguParameterService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import java.sql.Connection;
import java.util.Collection;

/**
 * User: homa
 * Date: 08.11.16
 * comments homa
 */
@SuppressWarnings("ProhibitedExceptionDeclared")
public class V0052__UpdateMutationsNummer implements JdbcMigration {
	private static final Logger LOG = LoggerFactory.getLogger(V0052__UpdateMutationsNummer.class);

	@Override
	public void migrate(Connection connection) {
		try {
			//todo your service here
			EbeguParameterService service = CDI.current().select(EbeguParameterService.class).get();
			Collection<EbeguParameter> allEbeguParameter = service.getAllEbeguParameter();
			if (allEbeguParameter.isEmpty()) {

				throw new RuntimeException("loading not workin");
			}

		} catch (RuntimeException ex) {
			LOG.error("Could not perform programmatic migration from flyway");
			throw ex;
		}

	}
}
