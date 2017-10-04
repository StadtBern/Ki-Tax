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

package ch.dvbern.ebegu.util;

import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.EntityManager;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.services.EbeguParameterService;
import org.slf4j.LoggerFactory;

/**
 * Allgemeine Utils fuer Betreuung
 */
public class BetreuungUtil {

	/**
	 * Returns the corresponding minimum value for the given betreuungsangebotTyp.
	 *
	 * @param betreuungsangebotTyp betreuungsangebotTyp
	 * @param stichtag defines which parameter to load. We only look for params that are valid on this day
	 * @return The minimum value for the betreuungsangebotTyp. Default value is -1: This means if the given betreuungsangebotTyp doesn't match any
	 * recorded type, the min value will be 0 and any positive value will be then accepted
	 */
	public static int getMinValueFromBetreuungsangebotTyp(LocalDate stichtag, BetreuungsangebotTyp betreuungsangebotTyp,
		EbeguParameterService ebeguParameterService, final EntityManager em) {
		EbeguParameterKey key = null;
		if (betreuungsangebotTyp == BetreuungsangebotTyp.KITA) {
			key = EbeguParameterKey.PARAM_PENSUM_KITA_MIN;
		} else if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGI) {
			key = EbeguParameterKey.PARAM_PENSUM_TAGI_MIN;
		} else if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESSCHULE) {
			key = EbeguParameterKey.PARAM_PENSUM_TAGESSCHULE_MIN;
		} else if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESELTERN_KLEINKIND ||
			betreuungsangebotTyp == BetreuungsangebotTyp.TAGESELTERN_SCHULKIND) {
			key = EbeguParameterKey.PARAM_PENSUM_TAGESELTERN_MIN;
		}
		if (key != null) {
			Optional<EbeguParameter> parameter = ebeguParameterService.getEbeguParameterByKeyAndDate(key, stichtag, em);
			if (parameter.isPresent()) {
				return parameter.get().getValueAsInteger();
			} else {
				LoggerFactory.getLogger(BetreuungUtil.class).warn("No Value available for Validation of key " + key);
			}
		}
		return 0;
	}
}
