/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
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
