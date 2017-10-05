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
package ch.dvbern.ebegu.reporting.kanton;

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BIGDECIMAL_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATE_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.PERCENT_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

public enum MergeFieldKanton implements MergeFieldProvider {

	auswertungVon(new SimpleMergeField<>("auswertungVon", DATE_CONVERTER)),
	auswertungBis(new SimpleMergeField<>("auswertungBis", DATE_CONVERTER)),

	repeatKantonRow(new RepeatRowMergeField("repeatKantonRow")),

	bgNummer(new SimpleMergeField<>("bgNummer", STRING_CONVERTER)),
	gesuchId(new SimpleMergeField<>("gesuchId", STRING_CONVERTER)),
	name(new SimpleMergeField<>("name", STRING_CONVERTER)),
	vorname(new SimpleMergeField<>("vorname", STRING_CONVERTER)),
	geburtsdatum(new SimpleMergeField<>("geburtsdatum", DATE_CONVERTER)),
	zeitabschnittVon(new SimpleMergeField<>("zeitabschnittVon", DATE_CONVERTER)),
	zeitabschnittBis(new SimpleMergeField<>("zeitabschnittBis", DATE_CONVERTER)),
	bgPensum(new SimpleMergeField<>("bgPensum", PERCENT_CONVERTER)),
	elternbeitrag(new SimpleMergeField<>("elternbeitrag", BIGDECIMAL_CONVERTER)),
	verguenstigung(new SimpleMergeField<>("verguenstigung", BIGDECIMAL_CONVERTER)),
	institution(new SimpleMergeField<>("institution", STRING_CONVERTER)),
	betreuungsTyp(new SimpleMergeField<>("betreuungsTyp", STRING_CONVERTER)),
	oeffnungstage(new SimpleMergeField<>("oeffnungstage", BIGDECIMAL_CONVERTER));

	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldKanton(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
