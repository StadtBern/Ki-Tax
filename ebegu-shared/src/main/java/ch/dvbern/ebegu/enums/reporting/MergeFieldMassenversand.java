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
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatColMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatValMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BOOLEAN_X_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATE_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

public enum MergeFieldMassenversand implements MergeFieldProvider {

	auswertungVon(new SimpleMergeField<>("auswertungVon", DATE_CONVERTER)),
	auswertungBis(new SimpleMergeField<>("auswertungBis", DATE_CONVERTER)),
	auswertungPeriode(new SimpleMergeField<>("auswertungPeriode", STRING_CONVERTER)),
	auswertungInklBgGesuche(new SimpleMergeField<>("auswertungInklBgGesuche", BOOLEAN_X_CONVERTER)),
	auswertungInklMischGesuche(new SimpleMergeField<>("auswertungInklMischGesuche", BOOLEAN_X_CONVERTER)),
	auswertungInklTsGesuche(new SimpleMergeField<>("auswertungInklTsGesuche", BOOLEAN_X_CONVERTER)),
	auswertungOhneFolgegesuch(new SimpleMergeField<>("auswertungOhneFolgegesuch", BOOLEAN_X_CONVERTER)),
	auswertungText(new SimpleMergeField<>("auswertungText", STRING_CONVERTER)),

	gesuchsperiode(new SimpleMergeField<>("gesuchsperiode", STRING_CONVERTER)),
	fall(new SimpleMergeField<>("fall", STRING_CONVERTER)),

	gs1Name(new SimpleMergeField<>("gs1Name", STRING_CONVERTER)),
	gs1Vorname(new SimpleMergeField<>("gs1Vorname", STRING_CONVERTER)),
	gs1PersonId(new SimpleMergeField<>("gs1PersonId", STRING_CONVERTER)),
	gs1Mail(new SimpleMergeField<>("gs1Mail", STRING_CONVERTER)),

	gs2Name(new SimpleMergeField<>("gs2Name", STRING_CONVERTER)),
	gs2Vorname(new SimpleMergeField<>("gs2Vorname", STRING_CONVERTER)),
	gs2PersonId(new SimpleMergeField<>("gs2PersonId", STRING_CONVERTER)),
	gs2Mail(new SimpleMergeField<>("gs2Mail", STRING_CONVERTER)),

	adresse(new SimpleMergeField<>("adresse", STRING_CONVERTER)),

	repeatKind(new RepeatColMergeField<>("repeatKind", STRING_CONVERTER)),

	kindName(new RepeatValMergeField<>("kindName", STRING_CONVERTER)),
	kindVorname(new RepeatValMergeField<>("kindVorname", STRING_CONVERTER)),
	kindGeburtsdatum(new RepeatValMergeField<>("kindGeburtsdatum", DATE_CONVERTER)),
	kindDubletten(new RepeatValMergeField<>("kindDubletten", STRING_CONVERTER)),
	kindInstitutionKita(new RepeatValMergeField<>("kindInstitutionKita", STRING_CONVERTER)),
	kindInstitutionTagi(new RepeatValMergeField<>("kindInstitutionTagi", STRING_CONVERTER)),
	kindInstitutionTeKleinkind(new RepeatValMergeField<>("kindInstitutionTeKleinkind", STRING_CONVERTER)),
	kindInstitutionTeSchulkind(new RepeatValMergeField<>("kindInstitutionTeSchulkind", STRING_CONVERTER)),
	kindInstitutionTagesschule(new RepeatValMergeField<>("kindInstitutionTagesschule", STRING_CONVERTER)),
	kindInstitutionFerieninsel(new RepeatValMergeField<>("kindInstitutionFerieninsel", STRING_CONVERTER)),
	kindInstitutionenWeitere(new RepeatValMergeField<>("kindInstitutionenWeitere", STRING_CONVERTER)),

	einreichungsart(new SimpleMergeField<>("einreichungsart", STRING_CONVERTER)),
	status(new SimpleMergeField<>("status", STRING_CONVERTER)),
	typ(new SimpleMergeField<>("typ", STRING_CONVERTER)),

	repeatRow(new RepeatRowMergeField("repeatRow"));


	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldMassenversand(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
