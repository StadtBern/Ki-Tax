package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinConfigurator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

/**
 * Services fuer Rules
 */
@Stateless
@Local(RulesService.class)
public class RulesServiceBean extends AbstractBaseService implements RulesService {

	private final Logger LOG = LoggerFactory.getLogger(RulesServiceBean.class);

	@Inject
	EbeguParameterService ebeguParameterService;

	/**
	 * Diese Methode initialisiert den Calculator mit den richtigen Parametern und benotigten Regeln fuer den Mandanten der
	 * gebraucht wird
	 */
	@Override
	public List<Rule> getRulesForGesuchsperiode(@Nullable Mandant mandant, @Nonnull Gesuchsperiode gesuchsperiode) {
		BetreuungsgutscheinConfigurator ruleConfigurator = new BetreuungsgutscheinConfigurator();
		Set<EbeguParameterKey> keysToLoad = ruleConfigurator.getRequiredParametersForMandant(mandant);
		Map<EbeguParameterKey, EbeguParameter> ebeguParameter = loadRuleParameters(mandant, gesuchsperiode, keysToLoad);
		return ruleConfigurator.configureRulesForMandant(mandant, ebeguParameter);
	}

	/**
	 * Hinewis, hier muss wohl spaeter der Mandant als Parameter mitgehen
	 *
	 * @return
	 */
	private Map<EbeguParameterKey, EbeguParameter> loadRuleParameters(Mandant mandant, Gesuchsperiode gesuchsperiode, Set<EbeguParameterKey> keysToLoad) {
		//Hinweis, Mandant wird noch ignoriert
		if (mandant != null) {
			LOG.warn("Mandant wird noch nicht beruecksichtigt. Codeaenderung noetig");
		}
		LocalDate stichtag = gesuchsperiode.getGueltigkeit().getGueltigAb();
		Map<EbeguParameterKey, EbeguParameter> ebeguRuleParameters = new HashMap<EbeguParameterKey, EbeguParameter>();
		for (EbeguParameterKey currentParamKey : keysToLoad) {
			Optional<EbeguParameter> param = ebeguParameterService.getEbeguParameterByKeyAndDate(currentParamKey, stichtag);
			if (param.isPresent()) {
				ebeguRuleParameters.put(param.get().getName(), param.get());
			} else {
				LOG.error("Required rule parameter '{}' could not be loaded  for the given Mandant '{}', Gesuchsperiode '{}'",
					currentParamKey, mandant, gesuchsperiode);
				throw new EbeguEntityNotFoundException("getRulesForGesuchsperiode", ErrorCodeEnum.ERROR_PARAMETER_NOT_FOUND,
					currentParamKey, Constants.DATE_FORMATTER.format(stichtag));
			}
		}

		return ebeguRuleParameters;
	}

}
