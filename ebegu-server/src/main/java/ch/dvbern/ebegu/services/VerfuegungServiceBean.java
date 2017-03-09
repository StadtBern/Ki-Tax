package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.rechner.BGRechnerParameterDTO;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.VerfuegungUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_FIXBETRAG_STADT_PRO_TAG_KITA;
import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(VerfuegungService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT})
public class VerfuegungServiceBean extends AbstractBaseService implements VerfuegungService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Inject
	private Persistence<Verfuegung> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private MandantService mandantService;

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private RulesService rulesService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private ZahlungService zahlungService;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Verfuegung verfuegen(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId, boolean ignorieren) {
		setZahlungsstatus(verfuegung, ignorieren);
		final Verfuegung persistedVerfuegung = persistVerfuegung(verfuegung, betreuungId, Betreuungsstatus.VERFUEGT);
		wizardStepService.updateSteps(persistedVerfuegung.getBetreuung().extractGesuch().getId(), null, null, WizardStepName.VERFUEGEN);
		return persistedVerfuegung;
	}

	private void setZahlungsstatus(Verfuegung verfuegung, boolean ignorieren) {
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : verfuegung.getZeitabschnitte()) {
			List<VerfuegungZeitabschnitt> zeitabschnitteOnVorgaengerVerfuegung = findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(verfuegungZeitabschnitt,
				verfuegungZeitabschnitt.getVerfuegung().getBetreuung());
			// if zeitabschnitteOnVorgaengerVerfuegung is empty the status of the new Zeitabschnitt must still be NEU

			final BigDecimal totalVerrechneteVerguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitteOnVorgaengerVerfuegung, verfuegungZeitabschnitt.getGueltigkeit());
//			final BigDecimal newVerrechneteVerguenstigung = ;
			if (verfuegungZeitabschnitt.getVerguenstigung().compareTo(totalVerrechneteVerguenstigung) != 0) {
				if (ignorieren) {
					verfuegungZeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT);
				} else {
					verfuegungZeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.NEU);
				}
			}
		}

//		LocalDate dateLastZahlungsauftrag = Constants.START_OF_TIME;
//		final Optional<Zahlungsauftrag> lastZahlungsauftrag = zahlungService.findLastZahlungsauftrag();
//		if (lastZahlungsauftrag.isPresent()) {
//			dateLastZahlungsauftrag = LocalDate.from(lastZahlungsauftrag.get().getDatumGeneriert().with(TemporalAdjusters.lastDayOfMonth()));
//		}
//		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : verfuegung.getZeitabschnitte()) {
//			if (!verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb().isAfter(dateLastZahlungsauftrag)) {
//				// dies wurde schon ausbezahlt. Mann muss es dementsprechend ignorieren oder als neu setzen
//				if (ignorieren) {
//					verfuegungZeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT);
//				} else {
//					if (haveZeitabschnittValuesChanged(verfuegung, verfuegungZeitabschnitt)) {
//						verfuegungZeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.NEU);
//					} else {
//						verfuegungZeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET);
//					}
//				}
//			}
//			else {
//				// es wurde noch nicht bezahlt. Muss noch bezahlt werden
//				verfuegungZeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.NEU);
//			}
//		}
	}

//	private boolean haveZeitabschnittValuesChanged(Verfuegung verfuegung, VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
//		if (verfuegung.hasVorgaenger()) { // man kann nicht nach dem Vorgaenger des Zeitabschnitts fragen, da es nicht richtig gesetzt wird
//			final Optional<Verfuegung> previousVerfuegung = findVorgaengerVerfuegung(verfuegung.getBetreuung());
//			if (!previousVerfuegung.isPresent()) {
//				throw new EbeguEntityNotFoundException("haveZeitabschnittValuesChanged", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, verfuegung.getVorgaengerId());
//			}
//			final BigDecimal oldVerguenstigungForInterval = VerfuegungUtil.getVerguenstigungZeitInterval(previousVerfuegung.get().getZeitabschnitte(), verfuegungZeitabschnitt.getGueltigkeit());
//			// unterschiedliche Verguenstigungen fuehren zu unterschiedlichen Bezahlungen
//			return verfuegungZeitabschnitt.getVerguenstigung().compareTo(oldVerguenstigungForInterval) != 0;
//		}
//		return true;
//	}

	private void setVerfuegungsKategorien(Verfuegung verfuegung) {
		if (!verfuegung.isKategorieNichtEintreten()) {
			for (VerfuegungZeitabschnitt zeitabschnitt : verfuegung.getZeitabschnitte()) {
				if (zeitabschnitt.isKategorieKeinPensum()) {
					verfuegung.setKategorieKeinPensum(true);
				}
				if (zeitabschnitt.isKategorieMaxEinkommen()) {
					verfuegung.setKategorieMaxEinkommen(true);
				}
				if (zeitabschnitt.isKategorieZuschlagZumErwerbspensum()) {
					verfuegung.setKategorieZuschlagZumErwerbspensum(true);
				}
			}
			// Wenn es keines der anderen ist, ist es "normal"
			if (!verfuegung.isKategorieKeinPensum() &&
				!verfuegung.isKategorieMaxEinkommen() &&
				!verfuegung.isKategorieZuschlagZumErwerbspensum() &&
				!verfuegung.isKategorieNichtEintreten()) {
				verfuegung.setKategorieNormal(true);
			}
		}
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Verfuegung nichtEintreten(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId) {
		// Bei Nich-Eintreten muss der Anspruch auf der Verfuegung auf 0 gesetzt werden, da diese u.U. bei Mutationen
		// als Vergleichswert hinzugezogen werden
		for (VerfuegungZeitabschnitt zeitabschnitt : verfuegung.getZeitabschnitte()) {
			zeitabschnitt.setAnspruchberechtigtesPensum(0);
		}
		verfuegung.setKategorieNichtEintreten(true);
		final Verfuegung persistedVerfuegung = persistVerfuegung(verfuegung, betreuungId, Betreuungsstatus.NICHT_EINGETRETEN);
		wizardStepService.updateSteps(persistedVerfuegung.getBetreuung().extractGesuch().getId(), null, null, WizardStepName.VERFUEGEN);
		return persistedVerfuegung;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Verfuegung persistVerfuegung(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId, @Nonnull Betreuungsstatus betreuungsstatus) {
		Objects.requireNonNull(verfuegung);
		Objects.requireNonNull(betreuungId);

		setVerfuegungsKategorien(verfuegung);
		Betreuung betreuung = persistence.find(Betreuung.class, betreuungId);
		betreuung.setBetreuungsstatus(betreuungsstatus);
		// setting all depending objects
		verfuegung.setBetreuung(betreuung);
		betreuung.setVerfuegung(verfuegung);
		verfuegung.getZeitabschnitte().forEach(verfZeitabsch -> verfZeitabsch.setVerfuegung(verfuegung));
		authorizer.checkWriteAuthorization(verfuegung);
		return persistence.persist(verfuegung);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Optional<Verfuegung> findVerfuegung(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Verfuegung a = persistence.find(Verfuegung.class, id);
		authorizer.checkReadAuthorization(a);
		return Optional.ofNullable(a);
	}


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Collection<Verfuegung> getAllVerfuegungen() {
		Collection<Verfuegung> verfuegungen = criteriaQueryHelper.getAll(Verfuegung.class);
		authorizer.checkReadAuthorizationVerfuegungen(verfuegungen);
		return verfuegungen;
	}


	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public void removeVerfuegung(@Nonnull Verfuegung verfuegung) {
		Validate.notNull(verfuegung);
		Optional<Verfuegung> entityToRemove = this.findVerfuegung(verfuegung.getId());
		Verfuegung loadedVerf = entityToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeVerfuegung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, verfuegung));
		authorizer.checkWriteAuthorization(loadedVerf);
		persistence.remove(loadedVerf);
	}


	@SuppressWarnings("OptionalIsPresent")
	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT, SCHULAMT})
	public Gesuch calculateVerfuegung(@Nonnull Gesuch gesuch) {
		this.finanzielleSituationService.calculateFinanzDaten(gesuch);
		Mandant mandant = mandantService.getFirst();   //gesuch get mandant?
		final List<Rule> rules = rulesService.getRulesForGesuchsperiode(mandant, gesuch.getGesuchsperiode());
		Boolean enableDebugOutput = applicationPropertyService.findApplicationPropertyAsBoolean(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, true);
		BetreuungsgutscheinEvaluator bgEvaluator = new BetreuungsgutscheinEvaluator(rules, enableDebugOutput);
		BGRechnerParameterDTO calculatorParameters = loadCalculatorParameters(mandant, gesuch.getGesuchsperiode());

		// Finde und setze die letzte Verfuegung für die Betreuung für den Merger und Vergleicher.
		// Bei GESCHLOSSEN_OHNE_VERFUEGUNG wird solange ein Vorgänger gesucht, bis  dieser gefunden wird. (Rekursiv)
		gesuch.getKindContainers()
			.stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.forEach(betreuung -> {
					Optional<Verfuegung> vorgaengerVerfuegung = findVorgaengerVerfuegung(betreuung);
					betreuung.setVorgaengerVerfuegung(vorgaengerVerfuegung.orElse(null));
				}
			);


		bgEvaluator.evaluate(gesuch, calculatorParameters);
		authorizer.checkReadAuthorizationForAnyBetreuungen(gesuch.extractAllBetreuungen()); // betreuungen pruefen reicht hier glaub
		return gesuch;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, SCHULAMT})
	public Optional<Verfuegung> findVorgaengerVerfuegung(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung darf nicht null sein");
		if (betreuung.getVorgaengerId() == null) {
			return Optional.empty();
		}

		// Achtung, hier wird persistence.find() verwendet, da ich fuer das Vorgaengergesuch evt. nicht
		// Leseberechtigt bin, fuer die Mutation aber schon!
		Betreuung vorgaengerbetreuung = persistence.find(Betreuung.class, betreuung.getVorgaengerId());
		if (vorgaengerbetreuung != null) {
			if (!vorgaengerbetreuung.getBetreuungsstatus().equals(Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG)) {
				// Hier kann aus demselben Grund die Berechtigung fuer die Vorgaengerverfuegung nicht geprueft werden
				return Optional.ofNullable(vorgaengerbetreuung.getVerfuegung());
			} else {
				return findVorgaengerVerfuegung(vorgaengerbetreuung);
			}
		}
		return Optional.empty();
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Optional<LocalDate> findVorgaengerVerfuegungDate(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung darf nicht null sein");
		Optional<Verfuegung> vorgaengerVerfuegung = findVorgaengerVerfuegung(betreuung);
		LocalDate letztesVerfDatum = null;
		if (vorgaengerVerfuegung.isPresent()) {
			authorizer.checkReadAuthorization(vorgaengerVerfuegung.get());
			letztesVerfDatum = vorgaengerVerfuegung.get().getTimestampErstellt().toLocalDate();
		}
		return Optional.ofNullable(letztesVerfDatum);
	}

	private BGRechnerParameterDTO loadCalculatorParameters(Mandant mandant, @Nonnull Gesuchsperiode gesuchsperiode) {
		Map<EbeguParameterKey, EbeguParameter> paramMap = ebeguParameterService.getEbeguParameterByGesuchsperiodeAsMap(gesuchsperiode);
		BGRechnerParameterDTO parameterDTO = new BGRechnerParameterDTO(paramMap, gesuchsperiode, mandant);

		//Es gibt aktuell einen Parameter der sich aendert am Jahreswechsel
		int startjahr = gesuchsperiode.getGueltigkeit().getGueltigAb().getYear();
		int endjahr = gesuchsperiode.getGueltigkeit().getGueltigBis().getYear();
		Validate.isTrue(endjahr == startjahr + 1, "Startjahr " + startjahr + " muss ein Jahr vor Endjahr" + endjahr + " sein ");
		BigDecimal abgeltungJahr1 = loadYearlyParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, startjahr);
		BigDecimal abgeltungJahr2 = loadYearlyParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, endjahr);
		parameterDTO.setBeitragStadtProTagJahr1((abgeltungJahr1));
		parameterDTO.setBeitragStadtProTagJahr2((abgeltungJahr2));
		return parameterDTO;
	}

	@Nonnull
	private BigDecimal loadYearlyParameter(EbeguParameterKey key, int jahr) {
		Optional<EbeguParameter> result = ebeguParameterService.getEbeguParameterByKeyAndDate(key, LocalDate.of(jahr, 1, 1));
		if (!result.isPresent()) {
			LOG.error("Required yearly calculator parameter '{}' could not be loaded for year {}'", key, jahr);
			throw new EbeguEntityNotFoundException("loadCalculatorParameters", ErrorCodeEnum.ERROR_PARAMETER_NOT_FOUND, key);
		}
		return result.get().getValueAsBigDecimal();
	}

	@Nonnull
	public List<VerfuegungZeitabschnitt> findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(@Nonnull VerfuegungZeitabschnitt zeitabschnittNeu, @Nonnull Betreuung betreuungNeu) {
		Optional<Verfuegung> vorgaengerVerfuegung = findVorgaengerVerfuegung(betreuungNeu);
		if (vorgaengerVerfuegung.isPresent()) {
			List<VerfuegungZeitabschnitt> zeitabschnitteOnVorgaengerVerfuegung = findZeitabschnitteOnVorgaengerVerfuegung(zeitabschnittNeu.getGueltigkeit(), vorgaengerVerfuegung.get());
			Betreuung vorgaengerBetreuung = null;
			for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitteOnVorgaengerVerfuegung) {
				vorgaengerBetreuung = zeitabschnitt.getVerfuegung().getBetreuung();
				if (zeitabschnitt.getZahlungsstatus().isVerrechnet()) {
					return zeitabschnitteOnVorgaengerVerfuegung;
				}
			}
			// Es gab keine bereits Verrechneten Zeitabschnitte auf dieser Verfuegung -> eins weiter zurueckgehen
			return findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(zeitabschnittNeu, vorgaengerBetreuung);
		}
		return Collections.emptyList();
	}

	/**
	 * Findet das anspruchberechtigtes Pensum zum Zeitpunkt des neuen Zeitabschnitt-Start
	 */
	@Nonnull
	private List<VerfuegungZeitabschnitt> findZeitabschnitteOnVorgaengerVerfuegung(@Nonnull DateRange newVerfuegungGueltigkeit, @Nonnull Verfuegung lastVerfuegung) {
		List<VerfuegungZeitabschnitt> lastVerfuegungsZeitabschnitte = new ArrayList<>();
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : lastVerfuegung.getZeitabschnitte()) {
			final DateRange gueltigkeit = verfuegungZeitabschnitt.getGueltigkeit();
			if (gueltigkeit.contains(newVerfuegungGueltigkeit.getGueltigAb()) || gueltigkeit.contains(newVerfuegungGueltigkeit.getGueltigBis())) {
				lastVerfuegungsZeitabschnitte.add(verfuegungZeitabschnitt);
			}
		}
		return lastVerfuegungsZeitabschnitte;
	}

}
