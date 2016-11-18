package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.rechner.BGRechnerParameterDTO;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
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
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private RulesService rulesService;

	@Inject
	private Authorizer authorizer;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	public Verfuegung saveVerfuegung(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId) {
		final Verfuegung persistedVerfuegung = persistVerfuegung(verfuegung, betreuungId);
		wizardStepService.updateSteps(persistedVerfuegung.getBetreuung().extractGesuch().getId(), null, null, WizardStepName.VERFUEGEN);
		return persistedVerfuegung;
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	public Verfuegung persistVerfuegung(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId) {
		Objects.requireNonNull(verfuegung);
		Objects.requireNonNull(betreuungId);

		Betreuung betreuung = persistence.find(Betreuung.class, betreuungId);
		betreuung.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
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
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	public void removeVerfuegung(@Nonnull Verfuegung verfuegung) {
		Validate.notNull(verfuegung);
		Optional<Verfuegung> entityToRemove = this.findVerfuegung(verfuegung.getId());
		Verfuegung loadedVerf = entityToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeVerfuegung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, verfuegung));
		authorizer.checkWriteAuthorization(loadedVerf);
		persistence.remove(loadedVerf);
	}


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT})
	public Gesuch calculateVerfuegung(@Nonnull Gesuch gesuch) {
		this.finanzielleSituationService.calculateFinanzDaten(gesuch);
		Mandant mandant = mandantService.getFirst();   //gesuch get mandant?
		final List<Rule> rules = rulesService.getRulesForGesuchsperiode(mandant, gesuch.getGesuchsperiode());
		Boolean enableDebugOutput = applicationPropertyService.findApplicationPropertyAsBoolean(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, true);
		BetreuungsgutscheinEvaluator bgEvaluator = new BetreuungsgutscheinEvaluator(rules, enableDebugOutput);
		BGRechnerParameterDTO calculatorParameters = loadCalculatorParameters(mandant, gesuch.getGesuchsperiode());
		final Optional<Gesuch> neustesVerfuegtesGesuchFuerGesuch = gesuchService.getNeustesVerfuegtesGesuchFuerGesuch(gesuch);


		// Wir überprüfen of in der Vorgängerverfügung eine Verfügung ist, welche geschlossen wurde ohne neu zu verfügen
		// und somit keine neue Verfügung hat
		if (neustesVerfuegtesGesuchFuerGesuch.isPresent()) {
			final Gesuch nvg = neustesVerfuegtesGesuchFuerGesuch.get();

			for (KindContainer kc : nvg.getKindContainers()) {
				for (Betreuung betreuung : kc.getBetreuungen()) {
					if (betreuung.getBetreuungsstatus().equals(Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG)) {
						// Wenn wir eine solche nicht verfügte Betruung haben, suchen wir die letzte verfügte betreuung
						// und kopieren deren Verfügung um sie später vergleichen und mergen zu können
						Optional<Verfuegung> vorgaengerVerfuegung = findVorgaengerVerfuegung(betreuung);
						if(vorgaengerVerfuegung.isPresent()){
							betreuung.setVorgaengerVerfuegung(vorgaengerVerfuegung.get());
						}else{
							throw new EbeguEntityNotFoundException("calculateVerfuegung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "VorgaengerVerfuegung of Betreuung not found even though state is GESCHLOSSEN_OHNE_VERFUEGUNG. BetreuungID: " + betreuung.getId());
						}
					}
				}
			}
		}

		bgEvaluator.evaluate(gesuch, calculatorParameters, neustesVerfuegtesGesuchFuerGesuch.orElse(null));
		authorizer.checkReadAuthorizationBetreuungen(gesuch.extractAllBetreuungen()); // betreuungen pruefen reicht hier glaub
		return gesuch;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Optional<Verfuegung> findVorgaengerVerfuegung(@Nonnull  Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung darf nicht null sein");
		if(betreuung.getVorgaengerId()==null) {return Optional.empty();}

		Optional<Betreuung> optVorgaengerbetreuung = betreuungService.findBetreuung(betreuung.getVorgaengerId());
		if (optVorgaengerbetreuung.isPresent()) {
			Betreuung vorgaengerbetreuung = optVorgaengerbetreuung.get();
			if (!vorgaengerbetreuung.getBetreuungsstatus().equals(Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG)) {
				authorizer.checkReadAuthorization(vorgaengerbetreuung.getVerfuegung());
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


}
