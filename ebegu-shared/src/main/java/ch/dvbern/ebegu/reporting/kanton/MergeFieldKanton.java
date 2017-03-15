/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.kanton;

import ch.dvbern.ebegu.reporting.lib.Converter;
import ch.dvbern.ebegu.reporting.lib.MergeField;

import javax.annotation.Nonnull;

import static ch.dvbern.ebegu.reporting.lib.StandardConverters.*;

public enum MergeFieldKanton implements MergeField {

	auswertungVon(DATE_CONVERTER, Type.SIMPLE),
	auswertungBis(DATE_CONVERTER, Type.SIMPLE),

	repeatKantonRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	bgNummer(STRING_CONVERTER, Type.SIMPLE),
	gesuchId(STRING_CONVERTER, Type.SIMPLE),
	name(STRING_CONVERTER, Type.SIMPLE),
	vorname(STRING_CONVERTER, Type.SIMPLE),
	geburtsdatum(DATE_CONVERTER, Type.SIMPLE),
	zeitabschnittVon(DATE_CONVERTER, Type.SIMPLE),
	zeitabschnittBis(DATE_CONVERTER, Type.SIMPLE),
	bgPensum(PERCENT_CONVERTER, Type.SIMPLE),
	elternbeitrag(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	verguenstigung(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	institution(STRING_CONVERTER, Type.SIMPLE),
	betreuungsTyp(STRING_CONVERTER, Type.SIMPLE),
	oeffnungstage(BIGDECIMAL_CONVERTER, Type.SIMPLE);


	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;

	MergeFieldKanton(@Nonnull Converter converter, @Nonnull Type repeatCol) {
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
