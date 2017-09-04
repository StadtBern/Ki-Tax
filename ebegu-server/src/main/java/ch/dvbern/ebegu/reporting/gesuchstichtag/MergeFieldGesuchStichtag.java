/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.gesuchstichtag;

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.INTEGER_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;


public enum MergeFieldGesuchStichtag implements MergeFieldProvider {

	repeatGesuchStichtagRow(new RepeatRowMergeField("repeatGesuchStichtagRow")),

	bgNummer(new SimpleMergeField<>("bgNummer", STRING_CONVERTER)),
	gesuchLaufNr(new SimpleMergeField<>("gesuchLaufNr", INTEGER_CONVERTER)),
	institution(new SimpleMergeField<>("institution", STRING_CONVERTER)),
	betreuungsTyp(new SimpleMergeField<>("betreuungsTyp", STRING_CONVERTER)),
	periode(new SimpleMergeField<>("periode", STRING_CONVERTER)),
	nichtFreigegeben(new SimpleMergeField<>("nichtFreigegeben", INTEGER_CONVERTER)),
	mahnungen(new SimpleMergeField<>("mahnungen", INTEGER_CONVERTER)),
	beschwerde(new SimpleMergeField<>("beschwerde", INTEGER_CONVERTER));


	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldGesuchStichtag(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
