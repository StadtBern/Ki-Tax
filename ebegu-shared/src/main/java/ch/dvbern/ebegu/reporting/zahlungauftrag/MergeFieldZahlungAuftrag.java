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

import ch.dvbern.ebegu.reporting.lib.Converter;
import ch.dvbern.ebegu.reporting.lib.MergeField;

import javax.annotation.Nonnull;

import static ch.dvbern.ebegu.reporting.lib.StandardConverters.*;

public enum MergeFieldZahlungAuftrag implements MergeField {

	repeatZahlungAuftragRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	institution(STRING_CONVERTER, Type.SIMPLE),
	name(STRING_CONVERTER, Type.SIMPLE),
	vorname(STRING_CONVERTER, Type.SIMPLE),
	gebDatum(DATE_CONVERTER, Type.SIMPLE),
	verfuegung(STRING_CONVERTER, Type.SIMPLE),
	vonDatum(DATE_CONVERTER, Type.SIMPLE),
	bisDatum(DATE_CONVERTER, Type.SIMPLE),
	bgPensum(STRING_CONVERTER, Type.SIMPLE),
	betragCHF(BIGDECIMAL_CONVERTER, Type.SIMPLE);


	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;

	MergeFieldZahlungAuftrag(@Nonnull Converter converter, @Nonnull Type repeatCol) {
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
