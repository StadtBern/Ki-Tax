/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.zahlungauftrag;

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BIGDECIMAL_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BOOLEAN_X_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATETIME_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATE_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

public enum MergeFieldZahlungAuftrag implements MergeFieldProvider {

	repeatZahlungAuftragRow(new RepeatRowMergeField("repeatZahlungAuftragRow")),

	beschrieb(new SimpleMergeField<>("beschrieb", STRING_CONVERTER)),
	generiertAm(new SimpleMergeField<>("generiertAm", DATETIME_CONVERTER)),
	faelligAm(new SimpleMergeField<>("faelligAm", DATE_CONVERTER)),

	institution(new SimpleMergeField<>("institution", STRING_CONVERTER)),
	name(new SimpleMergeField<>("name", STRING_CONVERTER)),
	vorname(new SimpleMergeField<>("vorname", STRING_CONVERTER)),
	gebDatum(new SimpleMergeField<>("gebDatum", DATE_CONVERTER)),
	verfuegung(new SimpleMergeField<>("verfuegung", STRING_CONVERTER)),
	vonDatum(new SimpleMergeField<>("vonDatum", DATE_CONVERTER)),
	bisDatum(new SimpleMergeField<>("bisDatum", DATE_CONVERTER)),
	bgPensum(new SimpleMergeField<>("bgPensum", BIGDECIMAL_CONVERTER)),
	betragCHF(new SimpleMergeField<>("betragCHF", BIGDECIMAL_CONVERTER)),
	isKorrektur(new SimpleMergeField<>("isKorrektur", BOOLEAN_X_CONVERTER)),
	isIgnoriert(new SimpleMergeField<>("isIgnoriert", BOOLEAN_X_CONVERTER));

	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldZahlungAuftrag(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
