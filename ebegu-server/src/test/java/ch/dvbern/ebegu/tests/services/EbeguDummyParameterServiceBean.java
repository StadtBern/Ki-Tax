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

package ch.dvbern.ebegu.tests.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.AbstractBaseService;
import ch.dvbern.ebegu.services.EbeguParameterService;

import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM;
import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_PENSUM_KITA_MIN;
import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_PENSUM_TAGESELTERN_MIN;
import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_PENSUM_TAGESSCHULE_MIN;
import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_PENSUM_TAGI_MIN;

/**
 * Dummyservice fuer Ebegu Parameters
 */
@Stateless
@Alternative
@Local(EbeguParameterService.class)
public class EbeguDummyParameterServiceBean extends AbstractBaseService implements EbeguParameterService {

	private final Map<EbeguParameterKey, EbeguParameter> dummyObjects;

	public EbeguDummyParameterServiceBean() {
		this.dummyObjects = new EnumMap<>(EbeguParameterKey.class);

		dummyObjects.put(PARAM_PENSUM_TAGI_MIN, new EbeguParameter(PARAM_PENSUM_TAGI_MIN, "60"));
		dummyObjects.put(PARAM_PENSUM_KITA_MIN, new EbeguParameter(PARAM_PENSUM_KITA_MIN, "10"));
		dummyObjects.put(PARAM_PENSUM_TAGESELTERN_MIN, new EbeguParameter(PARAM_PENSUM_TAGESELTERN_MIN, "20"));
		dummyObjects.put(PARAM_PENSUM_TAGESSCHULE_MIN, new EbeguParameter(PARAM_PENSUM_TAGESSCHULE_MIN, "0"));
		dummyObjects.put(PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM, new EbeguParameter(PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM, "20"));
	}

	@Override
	@Nonnull
	public EbeguParameter saveEbeguParameter(@Nonnull EbeguParameter ebeguParameter) {
		Objects.requireNonNull(ebeguParameter);
		this.dummyObjects.put(ebeguParameter.getName(), ebeguParameter);
		return ebeguParameter;
	}

	@Override
	@Nonnull
	public Optional<EbeguParameter> findEbeguParameter(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		return this.dummyObjects.values().stream().filter(ebeguParameter -> ebeguParameter.getId().equals(id)).findFirst();
	}

	@Override
	public void removeEbeguParameter(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		this.dummyObjects.values().stream().filter(ebeguParameter -> ebeguParameter.getId().equals(id)).findFirst();
		Optional<EbeguParameter> parameterToRemove = findEbeguParameter(id);
		EbeguParameter param = parameterToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeEbeguParameter", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));
		this.dummyObjects.remove(param.getName());
	}

	@Override
	@Nonnull
	public Collection<EbeguParameter> getAllEbeguParameter() {
		return dummyObjects.values();
	}

	@Nonnull
	@Override
	public Collection<EbeguParameter> getAllEbeguParameterByDate(@Nonnull LocalDate date) {
		return dummyObjects.values();
	}

	@Override
	@Nonnull
	public Collection<EbeguParameter> getEbeguParameterByGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		return dummyObjects.values();
	}

	@Override
	@Nonnull
	public Collection<EbeguParameter> getEbeguParametersByJahr(@Nonnull Integer jahr) {
		return dummyObjects.values();
	}

	@Nonnull
	@Override
	public Collection<EbeguParameter> getJahresabhParameter() {
		return dummyObjects.values().stream().filter(ebeguParameter -> ebeguParameter.getName().isProGesuchsperiode()).collect(Collectors.toList());
	}

	@Override
	@Nonnull
	public Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date) {
		return getEbeguParameterByKeyAndDate(key, date, null);
	}

	//wird von validator gebraucht
	@Override
	@Nonnull
	public Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date, final EntityManager em) {
		EbeguParameter mockParameter = this.dummyObjects.get(key);
		if (mockParameter != null) {
			return Optional.of(mockParameter);
		}
		return Optional.empty();
	}

	@Override
	public void copyEbeguParameterListToNewGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		// nop
	}

	@Override
	public void createEbeguParameterListForJahr(@Nonnull Integer jahr) {
		// nop§
	}

	@Override
	public Map<EbeguParameterKey, EbeguParameter> getEbeguParameterByGesuchsperiodeAsMap(@Nonnull Gesuchsperiode gesuchsperiode) {
		Map<EbeguParameterKey, EbeguParameter> result = new HashMap<>();
		Collection<EbeguParameter> paramsForGesuchsperiode = getEbeguParameterByGesuchsperiode(gesuchsperiode);
		paramsForGesuchsperiode.stream().map(ebeguParameter -> result.put(ebeguParameter.getName(), ebeguParameter));
		return result;
	}
}
