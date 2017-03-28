package ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen;

import ch.dvbern.lib.excelmerger.Converter;
import ch.dvbern.lib.excelmerger.MergeField;
import static ch.dvbern.lib.excelmerger.StandardConverters.*;

import javax.annotation.Nonnull;

/**
 * Merger fuer Statistik fuer MitarbeiterInnen
 */
public enum MergeFieldMitarbeiterinnen implements MergeField {

	auswertungVon(DATE_CONVERTER, Type.SIMPLE),
	auswertungBis(DATE_CONVERTER, Type.SIMPLE),

	repeatMitarbeiterinnenRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	name(STRING_CONVERTER, Type.SIMPLE),
	vorname(STRING_CONVERTER, Type.SIMPLE),
	verantwortlicheGesuche(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	verfuegungenAusgestellt(BIGDECIMAL_CONVERTER, Type.SIMPLE);

	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;


	MergeFieldMitarbeiterinnen(@Nonnull Converter converter, @Nonnull Type repeatCol) {
		this.converter = converter;
		this.type = repeatCol;
	}

	@Nonnull
	@Override
	public String getKey() {
		return name();
	}

	@Nonnull
	@Override
	public Type getType() {
		return type;
	}

	@Nonnull
	@Override
	public Converter getConverter() {
		return converter;
	}
}
