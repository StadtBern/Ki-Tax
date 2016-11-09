package db.migration;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import java.sql.Connection;
import java.util.Collection;

@SuppressWarnings("ProhibitedExceptionDeclared")
public class V0052__UpdateLaufnummern implements JdbcMigration {
	private static final Logger LOG = LoggerFactory.getLogger(V0052__UpdateLaufnummern.class);

	@Override
	public void migrate(Connection connection) {
		int counter = 0;
		try {
			GesuchService gesuchService = CDI.current().select(GesuchService.class).get();
			FallService fallService = CDI.current().select(FallService.class).get();

			Collection<Fall> faelle = fallService.getAllFalle();
			if(faelle.isEmpty()){
				LOG.info("keine Faelle vorhanden");
			}else {
				for (Fall fall : faelle) {
					gesuchService.updateLaufnummerOfAllGesucheOfFall(fall.getId());
					counter++;
				}
			}
		} catch (RuntimeException ex) {
			LOG.error("Could not perform programmatic migration from flyway");
			throw ex;
		} finally {
			LOG.info("Laufnummern aller Gesuche von  "  + counter + " faellen aktualisiert");

		}

	}
}
