/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

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

/**
 * Services fuer Rules
 */
@Stateless
@Local(RulesService.class)
public class RulesServiceBean extends AbstractBaseService implements RulesService {

	private static final Logger LOG = LoggerFactory.getLogger(RulesServiceBean.class);

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
	 */
	private Map<EbeguParameterKey, EbeguParameter> loadRuleParameters(Mandant mandant, Gesuchsperiode gesuchsperiode, Set<EbeguParameterKey> keysToLoad) {
		//Hinweis, Mandant wird noch ignoriert
		if (mandant != null) {
			LOG.warn("Mandant wird noch nicht beruecksichtigt. Codeaenderung noetig");
		}
		LocalDate stichtag = gesuchsperiode.getGueltigkeit().getGueltigAb();
		Map<EbeguParameterKey, EbeguParameter> ebeguRuleParameters = new HashMap<>();
		for (EbeguParameterKey currentParamKey : keysToLoad) {
			Optional<EbeguParameter> param = ebeguParameterService.getEbeguParameterByKeyAndDate(currentParamKey, stichtag);
			if (param.isPresent()) {
				ebeguRuleParameters.put(param.get().getName(), param.get());
			} else {
				String message = String.format("Required rule parameter '%s' could not be loaded  for the given Mandant '%s', Gesuchsperiode '%s'",
					currentParamKey, mandant, gesuchsperiode);
				throw new EbeguEntityNotFoundException("getRulesForGesuchsperiode", message, ErrorCodeEnum.ERROR_PARAMETER_NOT_FOUND,
					currentParamKey, Constants.DATE_FORMATTER.format(stichtag));
			}
		}

		return ebeguRuleParameters;
	}

}
