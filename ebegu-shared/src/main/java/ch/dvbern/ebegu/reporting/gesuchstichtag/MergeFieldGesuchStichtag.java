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

import ch.dvbern.ebegu.reporting.lib.Converter;
import ch.dvbern.ebegu.reporting.lib.MergeField;

import javax.annotation.Nonnull;

import static ch.dvbern.ebegu.reporting.lib.StandardConverters.*;

public enum MergeFieldGesuchStichtag implements MergeField {

	repeatGesuchStichtagRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	bgNummer(STRING_CONVERTER, Type.SIMPLE),
	gesuchLaufNr(INTEGER_CONVERTER, Type.SIMPLE),
	institution(STRING_CONVERTER, Type.SIMPLE),
	betreuungsTyp(STRING_CONVERTER, Type.SIMPLE),
	periode(STRING_CONVERTER, Type.SIMPLE),
	nichtFreigegeben(INTEGER_CONVERTER, Type.SIMPLE),
	mahnungen(INTEGER_CONVERTER, Type.SIMPLE),
	beschwerde(INTEGER_CONVERTER, Type.SIMPLE);


	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;

	MergeFieldGesuchStichtag(@Nonnull Converter converter, @Nonnull Type repeatCol) {
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
