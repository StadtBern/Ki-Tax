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

import ch.dvbern.lib.excelmerger.Converter;
import ch.dvbern.lib.excelmerger.MergeField;

import javax.annotation.Nonnull;

import static ch.dvbern.lib.excelmerger.StandardConverters.*;

public enum MergeFieldGesuchZeitraum implements MergeField {

	repeatGesuchZeitraumRow(REPEAT_ROW_CONVERTER, Type.REPEAT_ROW),

	bgNummer(STRING_CONVERTER, Type.SIMPLE),
	gesuchLaufNr(INTEGER_CONVERTER, Type.SIMPLE),
	institution(STRING_CONVERTER, Type.SIMPLE),
	betreuungsTyp(STRING_CONVERTER, Type.SIMPLE),
	periode(STRING_CONVERTER, Type.SIMPLE),
	anzahlGesuchOnline(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlGesuchPapier(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationOnline(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationPapier(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationAbwesenheit(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationBetreuung(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationDokumente(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationEV(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationEwerbspensum(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationFamilienSitutation(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationFinanzielleSituation(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationFreigabe(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationGesuchErstellen(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationGesuchsteller(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationKinder(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationUmzug(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMutationVerfuegen(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlMahnungen(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlBeschwerde(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlVerfuegungen(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlVerfuegungenNormal(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlVerfuegungenMaxEinkommen(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlVerfuegungenKeinPensum(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlVerfuegungenZuschlagZumPensum(INTEGER_CONVERTER, Type.SIMPLE),
	anzahlVerfuegungenNichtEintreten(INTEGER_CONVERTER, Type.SIMPLE);


	@Nonnull
	private final Converter converter;

	@Nonnull
	private final Type type;

	MergeFieldGesuchZeitraum(@Nonnull Converter converter, @Nonnull Type repeatCol) {
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
