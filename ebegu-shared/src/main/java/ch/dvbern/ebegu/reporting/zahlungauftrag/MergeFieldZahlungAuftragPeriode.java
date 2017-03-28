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

import ch.dvbern.lib.excelmerger.Converter;
import ch.dvbern.lib.excelmerger.MergeField;

import javax.annotation.Nonnull;

import static ch.dvbern.lib.excelmerger.StandardConverters.*;

public enum MergeFieldZahlungAuftragPeriode implements MergeField {

	repeatZahlungAuftragRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	periode(STRING_CONVERTER, Type.SIMPLE),
	institution(STRING_CONVERTER, Type.SIMPLE),
	bezahltAm(DATE_CONVERTER, Type.SIMPLE),
	betragCHF(BIGDECIMAL_CONVERTER, Type.SIMPLE);


	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;

	MergeFieldZahlungAuftragPeriode(@Nonnull Converter converter, @Nonnull Type repeatCol) {
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
