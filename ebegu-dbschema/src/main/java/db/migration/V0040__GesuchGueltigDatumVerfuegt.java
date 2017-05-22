package db.migration;

import ch.dvbern.ebegu.dbschema.FlywayMigrationHelper;
import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.services.AntragStatusHistoryService;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;
import java.sql.Connection;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public class V0040__GesuchGueltigDatumVerfuegt implements JdbcMigration {

	private static final Logger LOGGER = LoggerFactory.getLogger(V0040__GesuchGueltigDatumVerfuegt.class);

	@Override
	public void migrate(Connection connection) {
		// Leider funktioniert wegen einem WELD Bug CDI Injection nicht. Wir muessen uns deshalb selbst
		FlywayMigrationHelper helper = CDI.current().select(FlywayMigrationHelper.class).get();
		helper.migrate(this::setGueltigFlagForVerfuegteAntraege);
	}

	private void setGueltigFlagForVerfuegteAntraege(@Nonnull CDI<Object> cdi, @Nonnull EntityManager em) {
		checkNotNull(cdi);
		LOGGER.info("Starting Migration V0040");

		GesuchsperiodeService gesuchsperiodeService = cdi.select(GesuchsperiodeService.class).get();
		FallService fallService = cdi.select(FallService.class).get();
		GesuchService gesuchService = cdi.select(GesuchService.class).get();
		AntragStatusHistoryService antragStatusHistoryService = cdi.select(AntragStatusHistoryService.class).get();

		Collection<Gesuchsperiode> allGesuchsperioden = gesuchsperiodeService.getAllGesuchsperioden();
		List<String> ids = new ArrayList<>();
		for (Gesuchsperiode gesuchsperiode : allGesuchsperioden) {
			Collection<Fall> allFaelle = fallService.getAllFalle(false);
			for (Fall fall : allFaelle) {
				Optional<String> idsFuerGesuch = gesuchService.getNeustesFreigegebenesGesuchIdFuerGesuch(gesuchsperiode, fall);
				idsFuerGesuch.ifPresent(ids::add);
			}
		}
		for (String id : ids) {
			Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(id);
			if (gesuchOptional.isPresent()) {
				Gesuch gesuch = gesuchOptional.get();
				Collection<AntragStatusHistory> allAntragStatusHistoryByGesuch = antragStatusHistoryService.findAllAntragStatusHistoryByGesuch(gesuch);
				Optional<AntragStatusHistory> historyOptional = allAntragStatusHistoryByGesuch.stream()
					.filter(history -> !AntragStatus.FIRST_STATUS_OF_VERFUEGT.contains(history.getStatus()))
					.sorted(Comparator.comparing(AntragStatusHistory::getTimestampVon))
					.findFirst();
				if (historyOptional.isPresent()) {
					gesuch.setDatumVerfuegt(historyOptional.get().getTimestampVon().toLocalDate());
					gesuch.setGueltig(true);
					LOGGER.info("Updating Gesuch: " + gesuch.getFall().getFallNummer() + " / " + gesuch.getGesuchsperiode().getGesuchsperiodeString() + " / " + gesuch.getId());
					gesuchService.updateGesuch(gesuch, false);
				}
			}
		}
		LOGGER.info("Migration V0040 finished");
	}
}
