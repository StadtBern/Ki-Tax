package db.migration;

import ch.dvbern.ebegu.dbschema.FlywayMigrationHelper;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.services.*;
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
public class V0041__GesuchGueltigDatumVerfuegt implements JdbcMigration {

	private static final Logger LOGGER = LoggerFactory.getLogger(V0041__GesuchGueltigDatumVerfuegt.class);
	public static final String SEPARATOR = " / ";

	@Override
	public void migrate(Connection connection) {
		// Leider funktioniert wegen einem WELD Bug CDI Injection nicht. Wir muessen uns deshalb selbst
		FlywayMigrationHelper helper = CDI.current().select(FlywayMigrationHelper.class).get();
		helper.migrate(this::setGueltigFlagForVerfuegteAntraege);
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private void setGueltigFlagForVerfuegteAntraege(@Nonnull CDI<Object> cdi, @Nonnull EntityManager em) {
		checkNotNull(cdi);
		LOGGER.info("Starting Migration V0040");

		GesuchsperiodeService gesuchsperiodeService = cdi.select(GesuchsperiodeService.class).get();
		FallService fallService = cdi.select(FallService.class).get();
		GesuchService gesuchService = cdi.select(GesuchService.class).get();
		AntragStatusHistoryService antragStatusHistoryService = cdi.select(AntragStatusHistoryService.class).get();
		VerfuegungService verfuegungService = cdi.select(VerfuegungService.class).get();

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
				String gesuchInfo = gesuch.getFall().getFallNummer() + SEPARATOR + gesuch.getGesuchsperiode().getGesuchsperiodeString() + SEPARATOR + gesuch.getId();
				Collection<AntragStatusHistory> allAntragStatusHistoryByGesuch = antragStatusHistoryService.findAllAntragStatusHistoryByGesuch(gesuch);
				Optional<AntragStatusHistory> historyOptional = allAntragStatusHistoryByGesuch.stream()
					.filter(history -> !AntragStatus.FIRST_STATUS_OF_VERFUEGT.contains(history.getStatus()))
					.sorted(Comparator.comparing(AntragStatusHistory::getTimestampVon))
					.findFirst();
				if (historyOptional.isPresent()) {
					// Das Gesuch ist verfuegt
					gesuch.setTimestampVerfuegt(historyOptional.get().getTimestampVon());
					gesuch.setGueltig(true);
					LOGGER.info("Updating Gesuch: " + gesuchInfo);
					gesuchService.updateGesuch(gesuch, false);
					// Die gueltige Verfuegung ermitteln
					for (Betreuung betreuung : gesuch.extractAllBetreuungen()) {
						String betreuungInfo = getBetreuungInfo(betreuung);
						LOGGER.info("Evaluiere Betreuung: " + betreuungInfo);
						if (betreuung.getVerfuegung() != null && betreuung.getBetreuungsstatus().isAnyStatusOfVerfuegt()) {
							betreuung.setGueltig(true);
							LOGGER.info("... gueltig");
						} else if (betreuung.getBetreuungsstatus().equals(Betreuungsstatus.SCHULAMT)) {
							betreuung.setGueltig(true);
							LOGGER.info("... Schulamt");
						} else {
							// Evt. ist die Vorgaenger-Verfuegung die richtige
							LOGGER.info("... nicht gueltig, ermittle Vorgaengerverfuegung");
							Optional<Verfuegung> vorgaengerVerfuegungOptional = verfuegungService.findVorgaengerVerfuegung(betreuung);
							if (vorgaengerVerfuegungOptional.isPresent()) {
								Verfuegung vorgaengerVerfuegung = vorgaengerVerfuegungOptional.get();
								LOGGER.info("Vorgaengerverfuegung: " + getBetreuungInfo(vorgaengerVerfuegung.getBetreuung()));
								if (vorgaengerVerfuegung.getBetreuung().getBetreuungsstatus().isAnyStatusOfVerfuegt()) {
									vorgaengerVerfuegung.getBetreuung().setGueltig(true);
									LOGGER.info("... gueltig");
								} else {
									LOGGER.warn("Keine gueltige VorgaengerVerfuegung gefunden fuer Betreuung: " + betreuungInfo);
								}
							} else {
								LOGGER.warn("Keine gueltige Verfuegung gefunden fuer Betreuung: " + betreuungInfo);
							}
						}
					}
				} else {
					LOGGER.warn("Verfuegtes Gesuch ohne AntragStatusHistory gefunden: " + gesuchInfo);
				}
			}
		}
		LOGGER.info("Migration V0040 finished");
	}

	private String getBetreuungInfo(Betreuung betreuung) {
		return betreuung.getKind().getKindNummer() + SEPARATOR + betreuung.getBetreuungNummer() + SEPARATOR + betreuung.getBetreuungsstatus();
	}
}
