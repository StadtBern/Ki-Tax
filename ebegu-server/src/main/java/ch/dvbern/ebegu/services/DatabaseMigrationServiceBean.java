package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;


/**
 * Service zum Ausfuehren von manuellen DB-Migrationen
 */
@Stateless
@Local(DatabaseMigrationService.class)
@PermitAll
@SuppressWarnings(value = {"PMD.AvoidDuplicateLiterals", "LocalVariableNamingConvention", "PMD.NcssTypeCount", "InstanceMethodNamingConvention"})
public class DatabaseMigrationServiceBean extends AbstractBaseService implements DatabaseMigrationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrationServiceBean.class.getSimpleName());
	public static final String SEPARATOR = " / ";


	@Inject
	private GesuchService gesuchService;

	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;

	@Inject
	private FallService fallService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private VerfuegungService verfuegungService;


	@Override
	public void processScript(@Nonnull String scriptId) {
		switch (scriptId) {
			case "1105":
				processScript1105_GesuchGueltigDatumVerfuegt();
		}
	}
	@SuppressWarnings({"PMD.NcssMethodCount", "OverlyComplexMethod", "OverlyNestedMethod"})
	private void processScript1105_GesuchGueltigDatumVerfuegt() {
		LOGGER.info("Starting Migration EBEGU-1105");
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
						if (betreuung.isGueltig()) {
							LOGGER.info("... Betreuung wurde schon behandelt");
							continue;
						}
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
		LOGGER.info("Migration EBEGU-1105 finished");
	}

	private String getBetreuungInfo(Betreuung betreuung) {
		return betreuung.getKind().getKindNummer() + SEPARATOR + betreuung.getBetreuungNummer() + SEPARATOR + betreuung.getBetreuungsstatus();
	}
}


