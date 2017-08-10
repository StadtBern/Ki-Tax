package ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen;

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BIGDECIMAL_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATE_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

/**
 * Merger fuer Statistik fuer MitarbeiterInnen
 */
public enum MergeFieldMitarbeiterinnen implements MergeFieldProvider {

	auswertungVon(new SimpleMergeField<>("auswertungVon", DATE_CONVERTER)),
	auswertungBis(new SimpleMergeField<>("auswertungBis", DATE_CONVERTER)),

	repeatMitarbeiterinnenRow(new RepeatRowMergeField("repeatMitarbeiterinnenRow")),

	name(new SimpleMergeField<>("name", STRING_CONVERTER)),
	vorname(new SimpleMergeField<>("vorname", STRING_CONVERTER)),
	verantwortlicheGesuche(new SimpleMergeField<>("verantwortlicheGesuche", BIGDECIMAL_CONVERTER)),
	verfuegungenAusgestellt(new SimpleMergeField<>("verfuegungenAusgestellt", BIGDECIMAL_CONVERTER));

	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldMitarbeiterinnen(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
