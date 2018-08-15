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

package ch.dvbern.ebegu.enums.reporting;

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BOOLEAN_X_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATE_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

/**
 * Merger fuer Statistik fuer Benutzer
 */
public enum MergeFieldBenutzer implements MergeFieldProvider {

	repeatBenutzerRow(new RepeatRowMergeField("repeatBenutzerRow")),

	stichtag(new SimpleMergeField<>("stichtag", DATE_CONVERTER)),

	username(new SimpleMergeField<>("username", STRING_CONVERTER)),
	vorname(new SimpleMergeField<>("vorname", STRING_CONVERTER)),
	nachname(new SimpleMergeField<>("nachname", STRING_CONVERTER)),
	email(new SimpleMergeField<>("email", STRING_CONVERTER)),
	role(new SimpleMergeField<>("role", STRING_CONVERTER)),
	roleGueltigAb(new SimpleMergeField<>("roleGueltigAb", DATE_CONVERTER)),
	roleGueltigBis(new SimpleMergeField<>("roleGueltigBis", DATE_CONVERTER)),
	institution(new SimpleMergeField<>("institution", STRING_CONVERTER)),
	traegerschaft(new SimpleMergeField<>("traegerschaft", STRING_CONVERTER)),
	gesperrt(new SimpleMergeField<>("gesperrt", BOOLEAN_X_CONVERTER)),
	isKita(new SimpleMergeField<>("isKita", BOOLEAN_X_CONVERTER)),
	isTageselternKleinkind(new SimpleMergeField<>("isTageselternKleinkind", BOOLEAN_X_CONVERTER)),
	isTageselternSchulkind(new SimpleMergeField<>("isTageselternSchulkind", BOOLEAN_X_CONVERTER)),
	isTagi(new SimpleMergeField<>("isTagi", BOOLEAN_X_CONVERTER)),
	isTagesschule(new SimpleMergeField<>("isTagesschule", BOOLEAN_X_CONVERTER)),
	isFerieninsel(new SimpleMergeField<>("isFerieninsel", BOOLEAN_X_CONVERTER)),
	isJugendamt(new SimpleMergeField<>("isJugendamt", BOOLEAN_X_CONVERTER)),
	isSchulamt(new SimpleMergeField<>("isSchulamt", BOOLEAN_X_CONVERTER));

	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldBenutzer(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
