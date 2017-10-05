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
package ch.dvbern.ebegu.reporting.gesuchzeitraum;

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.INTEGER_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

public enum MergeFieldGesuchZeitraum implements MergeFieldProvider {

	repeatGesuchZeitraumRow(new RepeatRowMergeField("repeatGesuchZeitraumRow")),

	bgNummer(new SimpleMergeField<>("bgNummer", STRING_CONVERTER)),
	gesuchLaufNr(new SimpleMergeField<>("gesuchLaufNr", INTEGER_CONVERTER)),
	institution(new SimpleMergeField<>("institution", STRING_CONVERTER)),
	betreuungsTyp(new SimpleMergeField<>("betreuungsTyp", STRING_CONVERTER)),
	periode(new SimpleMergeField<>("periode", STRING_CONVERTER)),
	anzahlGesuchOnline(new SimpleMergeField<>("anzahlGesuchOnline", INTEGER_CONVERTER)),
	anzahlGesuchPapier(new SimpleMergeField<>("anzahlGesuchPapier", INTEGER_CONVERTER)),
	anzahlMutationOnline(new SimpleMergeField<>("anzahlMutationOnline", INTEGER_CONVERTER)),
	anzahlMutationPapier(new SimpleMergeField<>("anzahlMutationPapier", INTEGER_CONVERTER)),
	anzahlMutationAbwesenheit(new SimpleMergeField<>("anzahlMutationAbwesenheit", INTEGER_CONVERTER)),
	anzahlMutationBetreuung(new SimpleMergeField<>("anzahlMutationBetreuung", INTEGER_CONVERTER)),
	anzahlMutationEV(new SimpleMergeField<>("anzahlMutationEV", INTEGER_CONVERTER)),
	anzahlMutationEwerbspensum(new SimpleMergeField<>("anzahlMutationEwerbspensum", INTEGER_CONVERTER)),
	anzahlMutationFamilienSitutation(new SimpleMergeField<>("anzahlMutationFamilienSitutation", INTEGER_CONVERTER)),
	anzahlMutationFinanzielleSituation(new SimpleMergeField<>("anzahlMutationFinanzielleSituation", INTEGER_CONVERTER)),
	anzahlMutationGesuchsteller(new SimpleMergeField<>("anzahlMutationGesuchsteller", INTEGER_CONVERTER)),
	anzahlMutationKinder(new SimpleMergeField<>("anzahlMutationKinder", INTEGER_CONVERTER)),
	anzahlMutationUmzug(new SimpleMergeField<>("anzahlMutationUmzug", INTEGER_CONVERTER)),
	anzahlMahnungen(new SimpleMergeField<>("anzahlMahnungen", INTEGER_CONVERTER)),
	anzahlSteueramtAusgeloest(new SimpleMergeField<>("anzahlSteueramtAusgeloest", INTEGER_CONVERTER)),
	anzahlSteueramtGeprueft(new SimpleMergeField<>("anzahlSteueramtGeprueft", INTEGER_CONVERTER)),
	anzahlBeschwerde(new SimpleMergeField<>("anzahlBeschwerde", INTEGER_CONVERTER)),
	anzahlVerfuegungen(new SimpleMergeField<>("anzahlVerfuegungen", INTEGER_CONVERTER)),
	anzahlVerfuegungenNormal(new SimpleMergeField<>("anzahlVerfuegungenNormal", INTEGER_CONVERTER)),
	anzahlVerfuegungenMaxEinkommen(new SimpleMergeField<>("anzahlVerfuegungenMaxEinkommen", INTEGER_CONVERTER)),
	anzahlVerfuegungenKeinPensum(new SimpleMergeField<>("anzahlVerfuegungenKeinPensum", INTEGER_CONVERTER)),
	anzahlVerfuegungenZuschlagZumPensum(new SimpleMergeField<>("anzahlVerfuegungenZuschlagZumPensum", INTEGER_CONVERTER)),
	anzahlVerfuegungenNichtEintreten(new SimpleMergeField<>("anzahlVerfuegungenNichtEintreten", INTEGER_CONVERTER));

	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldGesuchZeitraum(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
