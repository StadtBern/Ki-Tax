package ch.dvbern.ebegu.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

	@Inject
	private EinkommensverschlechterungInfoService ekvInfoService;

	@Inject
	private EinkommensverschlechterungService ekvService;

	@Inject
	private WizardStepService wizardStepService;


	@Override
	public void processScript(@Nonnull String scriptId) {
		switch (scriptId) {
			case "1105":
				processScript1105_GesuchGueltigDatumVerfuegt();
				break;
			case "1204":
				processScript1204_CreateMissingEKV();
				break;
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
					gesuchService.updateGesuch(gesuch, false, null);
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
						} else if (betreuung.getBetreuungsstatus() == Betreuungsstatus.SCHULAMT) {
							betreuung.setGueltig(true);
							LOGGER.info("... Schulamt");
						} else {
							// Evt. ist die Vorgaenger-Verfuegung die richtige
							LOGGER.info("... nicht gueltig, ermittle Vorgaengerverfuegung");
							Optional<Verfuegung> vorgaengerVerfuegungOptional = verfuegungService.findVorgaengerVerfuegung(betreuung);
							if (vorgaengerVerfuegungOptional.isPresent()) {
								Verfuegung vorgaengerVerfuegung = vorgaengerVerfuegungOptional.get();
								LOGGER.info("Vorgaengerverfuegung: " + getBetreuungInfo(vorgaengerVerfuegung.getBetreuung()));
								if (vorgaengerVerfuegung.getBetreuung().isGueltig()) {
									LOGGER.info("... VorgaengerBetreuung wurde schon behandelt");
									continue;
								}
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

	/**
	 * This script creates all missing EKV of all kinds for existing objects.
	 * This is needed to solve an old problem: we didn't always create the required EKVInfoContainer and EKVContainer
	 * - EKVInfoContainer must be always created even though the question "haben sie EKV?" is answered with NO
	 * - If an EKV for a year was defined in the EKVInfo, the corresponding EKVContainer has to exist, even when it
	 * has no content at all.
	 */
	private void processScript1204_CreateMissingEKV() {
		//todo beim DONE -> Mirar que no se modifica el timestamp_mutiert del Gesuch
		//todo beim DONE -> Hacerlo solo cuando el EKV ya se haya creado, es decir cuando el Step ya se haya completado
		//todo beim -> create button in Client
		//todo beim -> comprobar que los logs funcionan

		LOGGER.info("Starting Migration EBEGU-1204");
		final Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		for (final Gesuch gesuch : allGesuche) {
			if (isEkvStepAlreadyFilledOut(gesuch)) {
				if (gesuch.getEinkommensverschlechterungInfoContainer() == null) {
					LOGGER.info("Found null EKVInfoContainer for Gesuch ", gesuch.getId());
					EinkommensverschlechterungInfoContainer ekvInfoContainer = new EinkommensverschlechterungInfoContainer();

					ekvInfoContainer.setGesuch(gesuch);
					EinkommensverschlechterungInfo ekvInfoJA = new EinkommensverschlechterungInfo();
					ekvInfoJA.setEinkommensverschlechterung(false);
					ekvInfoJA.setEkvFuerBasisJahrPlus1(false);
					ekvInfoJA.setEkvFuerBasisJahrPlus2(false);
					ekvInfoContainer.setEinkommensverschlechterungInfoJA(ekvInfoJA);
					gesuch.setSkipPreUpdate(true); // with this flag we skip the preUpdate and timestampMutiert doesn't get updated
					ekvInfoService.createEinkommensverschlechterungInfo(ekvInfoContainer);
					LOGGER.info("Default EKVInfoContainer created with 'false' for Gesuch ", gesuch.getId());
				} else {
					// if EKVInfo didn't exist it couldn't have it set to true. So we can do this in an else
					final EinkommensverschlechterungInfo ekvInfoJA = gesuch.getEinkommensverschlechterungInfoContainer().getEinkommensverschlechterungInfoJA();
					if (ekvInfoJA.getEinkommensverschlechterung()) {
						if (gesuch.getGesuchsteller1() != null) {
							gesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(
								createNonExistingEKVContainerForGesuchsteller(gesuch.getGesuchsteller1(), ekvInfoJA,
									gesuch.getId()));
						}
						if (gesuch.getGesuchsteller2() != null) {
							gesuch.getGesuchsteller2().setEinkommensverschlechterungContainer
								(createNonExistingEKVContainerForGesuchsteller(gesuch.getGesuchsteller2(), ekvInfoJA,
									gesuch.getId()));
						}
					}
				}
			}
		}
	}

	/**
	 * Checks whether the Step EKV has been already visited and filled out. Only in that case is it needed to create
	 * the missing objects, because if the step hasn't been visited yet, they will be created normally by the user.
	 */
	private boolean isEkvStepAlreadyFilledOut(final Gesuch gesuch) {
		final WizardStep wizardStep = wizardStepService.findWizardStepFromGesuch(gesuch.getId(), WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
		return wizardStep != null && WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus();
	}

	/**
	 * If the ekvInfo says there is an EKV for BasisJahrPlus1 or BasisJahrPlus2 but the EKVContainer doesn't exist,
	 * it will create it for the given Gesuchsteller. After creating the EKVContainer it will check which EKV have to
	 * be created and it creates them too.
	 */
	private EinkommensverschlechterungContainer createNonExistingEKVContainerForGesuchsteller(@NotNull GesuchstellerContainer gesuchsteller, @NotNull EinkommensverschlechterungInfo ekvInfoJA, String gesuchId) {
		if (ekvInfoJA.getEkvFuerBasisJahrPlus1() || ekvInfoJA.getEkvFuerBasisJahrPlus2()) {
			if (gesuchsteller.getEinkommensverschlechterungContainer() == null) {
				LOGGER.info("Found null EKVContainer for Gesuchsteller {0} in Gesuch {1}. Created ", gesuchsteller.getId(), gesuchId);
				final EinkommensverschlechterungContainer ekvContainer = new EinkommensverschlechterungContainer();
				ekvContainer.setGesuchsteller(gesuchsteller);
				gesuchsteller.setEinkommensverschlechterungContainer(ekvContainer);
			}
			//noinspection ConstantConditions
			if (ekvInfoJA.getEkvFuerBasisJahrPlus1() && gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() == null) {
				LOGGER.info("Found null EKVContainerBasisJahrPlus1 for Gesuchsteller {0} in Gesuch {1}. Created ", gesuchsteller.getId(), gesuchId);
				final Einkommensverschlechterung ekvBasisJahrPlus1 = new Einkommensverschlechterung();
				ekvBasisJahrPlus1.setSteuerveranlagungErhalten(false);
				ekvBasisJahrPlus1.setSteuererklaerungAusgefuellt(false);
				gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus1(ekvBasisJahrPlus1);
			}
			if (ekvInfoJA.getEkvFuerBasisJahrPlus2() && gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() == null) {
				LOGGER.info("Found null EKVContainerBasisJahrPlus2 for Gesuchsteller {0} in Gesuch {1}. Created ", gesuchsteller.getId(), gesuchId);
				final Einkommensverschlechterung ekvBasisJahrPlus2 = new Einkommensverschlechterung();
				ekvBasisJahrPlus2.setSteuerveranlagungErhalten(false);
				ekvBasisJahrPlus2.setSteuererklaerungAusgefuellt(false);
				gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus2(ekvBasisJahrPlus2);
			}
			return ekvService.saveEinkommensverschlechterungContainer(gesuchsteller
				.getEinkommensverschlechterungContainer(), null); // no need to update steps
		}
		return null;
	}
}


