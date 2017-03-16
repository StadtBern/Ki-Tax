/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.gesuchstellerKinderBetreuung;

import ch.dvbern.lib.excelmerger.Converter;
import ch.dvbern.lib.excelmerger.MergeField;

import javax.annotation.Nonnull;

import static ch.dvbern.lib.excelmerger.StandardConverters.*;

public enum MergeFieldGesuchstellerKinderBetreuung implements MergeField {

	auswertungVon(DATE_CONVERTER, Type.SIMPLE),
	auswertungBis(DATE_CONVERTER, Type.SIMPLE),
	auswertungPeriode(STRING_CONVERTER, Type.SIMPLE),

	repeatRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	bgNummer(STRING_CONVERTER, Type.SIMPLE),
	institution(STRING_CONVERTER, Type.SIMPLE),
	betreuungsTyp(STRING_CONVERTER, Type.SIMPLE),
	periode(STRING_CONVERTER, Type.SIMPLE),

	eingangsdatum(DATE_CONVERTER, Type.SIMPLE),
	verfuegungsdatum(DATE_CONVERTER, Type.SIMPLE),
	fallId(INTEGER_CONVERTER, Type.SIMPLE),

	gs1Name(STRING_CONVERTER, Type.SIMPLE),
	gs1Vorname(STRING_CONVERTER, Type.SIMPLE),
	gs1Strasse(STRING_CONVERTER, Type.SIMPLE),
	gs1Hausnummer(STRING_CONVERTER, Type.SIMPLE),
	gs1Zusatzzeile(STRING_CONVERTER, Type.SIMPLE),
	gs1Plz(STRING_CONVERTER, Type.SIMPLE),
	gs1Ort(STRING_CONVERTER, Type.SIMPLE),
	gs1EwkId(STRING_CONVERTER, Type.SIMPLE),
	gs1Diplomatenstatus(BOOLEAN_CONVERTER, Type.SIMPLE),
	gs1EwpAngestellt(INTEGER_CONVERTER, Type.SIMPLE),
	gs1EwpAusbildung(INTEGER_CONVERTER, Type.SIMPLE),
	gs1EwpSelbstaendig(INTEGER_CONVERTER, Type.SIMPLE),
	gs1EwpRav(INTEGER_CONVERTER, Type.SIMPLE),
	gs1EwpZuschlag(INTEGER_CONVERTER, Type.SIMPLE),
	gs1EwpGesundhtl(INTEGER_CONVERTER, Type.SIMPLE),

	gs2Name(STRING_CONVERTER, Type.SIMPLE),
	gs2Vorname(STRING_CONVERTER, Type.SIMPLE),
	gs2Strasse(STRING_CONVERTER, Type.SIMPLE),
	gs2Hausnummer(STRING_CONVERTER, Type.SIMPLE),
	gs2Zusatzzeile(STRING_CONVERTER, Type.SIMPLE),
	gs2Plz(STRING_CONVERTER, Type.SIMPLE),
	gs2Ort(STRING_CONVERTER, Type.SIMPLE),
	gs2EwkId(STRING_CONVERTER, Type.SIMPLE),
	gs2Diplomatenstatus(BOOLEAN_CONVERTER, Type.SIMPLE),
	gs2EwpAngestellt(INTEGER_CONVERTER, Type.SIMPLE),
	gs2EwpAusbildung(INTEGER_CONVERTER, Type.SIMPLE),
	gs2EwpSelbstaendig(INTEGER_CONVERTER, Type.SIMPLE),
	gs2EwpRav(INTEGER_CONVERTER, Type.SIMPLE),
	gs2EwpZuschlag(INTEGER_CONVERTER, Type.SIMPLE),
	gs2EwpGesundhtl(INTEGER_CONVERTER, Type.SIMPLE),

	familiensituation(STRING_CONVERTER, Type.SIMPLE),
	kardinalitaet(STRING_CONVERTER, Type.SIMPLE),
	familiengroesse(BIGDECIMAL_CONVERTER, Type.SIMPLE),

	massgEinkVorFamilienabzug(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	familienabzug(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	massgEink(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	einkommensjahr(INTEGER_CONVERTER, Type.SIMPLE),
	ekvVorhanden(BOOLEAN_CONVERTER, Type.SIMPLE),
	stvGeprueft(BOOLEAN_CONVERTER, Type.SIMPLE),
	veranlagt(BOOLEAN_CONVERTER, Type.SIMPLE),

	kindName(STRING_CONVERTER, Type.SIMPLE),
	kindVorname(STRING_CONVERTER, Type.SIMPLE),
	kindGeburtsdatum(DATE_CONVERTER, Type.SIMPLE),
	kindFachstelle(BOOLEAN_CONVERTER, Type.SIMPLE),
	kindErwBeduerfnisse(BOOLEAN_CONVERTER, Type.SIMPLE),
	kindDeutsch(BOOLEAN_CONVERTER, Type.SIMPLE),
	eingeschult(BOOLEAN_CONVERTER, Type.SIMPLE),

	zeitabschnittVon(DATE_CONVERTER, Type.SIMPLE),
	zeitabschnittBis(DATE_CONVERTER, Type.SIMPLE),
	betreuungsPensum(PERCENT_CONVERTER, Type.SIMPLE),
	anspruchsPensum(PERCENT_CONVERTER, Type.SIMPLE),
	bgPensum(PERCENT_CONVERTER, Type.SIMPLE),
	bgStunden(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	vollkosten(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	elternbeitrag(BIGDECIMAL_CONVERTER, Type.SIMPLE),
	verguenstigt(BIGDECIMAL_CONVERTER, Type.SIMPLE);


	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;

	MergeFieldGesuchstellerKinderBetreuung(@Nonnull Converter converter, @Nonnull Type repeatCol) {
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
