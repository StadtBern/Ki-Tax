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

import javax.annotation.Nonnull;

import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import ch.dvbern.oss.lib.excelmerger.mergefields.RepeatRowMergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.SimpleMergeField;

import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BIGDECIMAL_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.BOOLEAN_X_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.DATE_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.INTEGER_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.PERCENT_CONVERTER;
import static ch.dvbern.oss.lib.excelmerger.converters.StandardConverters.STRING_CONVERTER;

public enum MergeFieldGesuchstellerKinderBetreuung implements MergeFieldProvider {

	stichtag(new SimpleMergeField<>("stichtag", DATE_CONVERTER)),
	auswertungVon(new SimpleMergeField<>("auswertungVon", DATE_CONVERTER)),
	auswertungBis(new SimpleMergeField<>("auswertungBis", DATE_CONVERTER)),
	auswertungPeriode(new SimpleMergeField<>("auswertungPeriode", STRING_CONVERTER)),

	repeatRow(new RepeatRowMergeField("repeatRow")),

	bgNummer(new SimpleMergeField<>("bgNummer", STRING_CONVERTER)),
	institution(new SimpleMergeField<>("institution", STRING_CONVERTER)),
	betreuungsTyp(new SimpleMergeField<>("betreuungsTyp", STRING_CONVERTER)),
	periode(new SimpleMergeField<>("periode", STRING_CONVERTER)),
	gesuchStatus(new SimpleMergeField<>("gesuchStatus", STRING_CONVERTER)),

	eingangsdatum(new SimpleMergeField<>("eingangsdatum", DATE_CONVERTER)),
	verfuegungsdatum(new SimpleMergeField<>("verfuegungsdatum", DATE_CONVERTER)),
	fallId(new SimpleMergeField<>("fallId", INTEGER_CONVERTER)),

	gs1Name(new SimpleMergeField<>("gs1Name", STRING_CONVERTER)),
	gs1Vorname(new SimpleMergeField<>("gs1Vorname", STRING_CONVERTER)),
	gs1Strasse(new SimpleMergeField<>("gs1Strasse", STRING_CONVERTER)),
	gs1Hausnummer(new SimpleMergeField<>("gs1Hausnummer", STRING_CONVERTER)),
	gs1Zusatzzeile(new SimpleMergeField<>("gs1Zusatzzeile", STRING_CONVERTER)),
	gs1Plz(new SimpleMergeField<>("gs1Plz", STRING_CONVERTER)),
	gs1Ort(new SimpleMergeField<>("gs1Ort", STRING_CONVERTER)),
	gs1EwkId(new SimpleMergeField<>("gs1EwkId", STRING_CONVERTER)),
	gs1Diplomatenstatus(new SimpleMergeField<>("gs1Diplomatenstatus", BOOLEAN_X_CONVERTER)),
	gs1EwpAngestellt(new SimpleMergeField<>("gs1EwpAngestellt", PERCENT_CONVERTER)),
	gs1EwpAusbildung(new SimpleMergeField<>("gs1EwpAusbildung", PERCENT_CONVERTER)),
	gs1EwpSelbstaendig(new SimpleMergeField<>("gs1EwpSelbstaendig", PERCENT_CONVERTER)),
	gs1EwpRav(new SimpleMergeField<>("gs1EwpRav", PERCENT_CONVERTER)),
	gs1EwpZuschlag(new SimpleMergeField<>("gs1EwpZuschlag", PERCENT_CONVERTER)),
	gs1EwpGesundhtl(new SimpleMergeField<>("gs1EwpGesundhtl", PERCENT_CONVERTER)),

	gs2Name(new SimpleMergeField<>("gs2Name", STRING_CONVERTER)),
	gs2Vorname(new SimpleMergeField<>("gs2Vorname", STRING_CONVERTER)),
	gs2Strasse(new SimpleMergeField<>("gs2Strasse", STRING_CONVERTER)),
	gs2Hausnummer(new SimpleMergeField<>("gs2Hausnummer", STRING_CONVERTER)),
	gs2Zusatzzeile(new SimpleMergeField<>("gs2Zusatzzeile", STRING_CONVERTER)),
	gs2Plz(new SimpleMergeField<>("gs2Plz", STRING_CONVERTER)),
	gs2Ort(new SimpleMergeField<>("gs2Ort", STRING_CONVERTER)),
	gs2EwkId(new SimpleMergeField<>("gs2EwkId", STRING_CONVERTER)),
	gs2Diplomatenstatus(new SimpleMergeField<>("gs2Diplomatenstatus", BOOLEAN_X_CONVERTER)),
	gs2EwpAngestellt(new SimpleMergeField<>("gs2EwpAngestellt", PERCENT_CONVERTER)),
	gs2EwpAusbildung(new SimpleMergeField<>("gs2EwpAusbildung", PERCENT_CONVERTER)),
	gs2EwpSelbstaendig(new SimpleMergeField<>("gs2EwpSelbstaendig", PERCENT_CONVERTER)),
	gs2EwpRav(new SimpleMergeField<>("gs2EwpRav", PERCENT_CONVERTER)),
	gs2EwpZuschlag(new SimpleMergeField<>("gs2EwpZuschlag", PERCENT_CONVERTER)),
	gs2EwpGesundhtl(new SimpleMergeField<>("gs2EwpGesundhtl", PERCENT_CONVERTER)),

	familiensituation(new SimpleMergeField<>("familiensituation", STRING_CONVERTER)),
	kardinalitaet(new SimpleMergeField<>("kardinalitaet", STRING_CONVERTER)),
	familiengroesse(new SimpleMergeField<>("familiengroesse", BIGDECIMAL_CONVERTER)),

	massgEinkVorFamilienabzug(new SimpleMergeField<>("massgEinkVorFamilienabzug", BIGDECIMAL_CONVERTER)),
	familienabzug(new SimpleMergeField<>("familienabzug", BIGDECIMAL_CONVERTER)),
	massgEink(new SimpleMergeField<>("massgEink", BIGDECIMAL_CONVERTER)),
	einkommensjahr(new SimpleMergeField<>("einkommensjahr", INTEGER_CONVERTER)),
	ekvVorhanden(new SimpleMergeField<>("ekvVorhanden", BOOLEAN_X_CONVERTER)),
	stvGeprueft(new SimpleMergeField<>("stvGeprueft", BOOLEAN_X_CONVERTER)),
	veranlagt(new SimpleMergeField<>("veranlagt", BOOLEAN_X_CONVERTER)),

	kindName(new SimpleMergeField<>("kindName", STRING_CONVERTER)),
	kindVorname(new SimpleMergeField<>("kindVorname", STRING_CONVERTER)),
	kindGeburtsdatum(new SimpleMergeField<>("kindGeburtsdatum", DATE_CONVERTER)),
	kindFachstelle(new SimpleMergeField<>("kindFachstelle", STRING_CONVERTER)),
	kindErwBeduerfnisse(new SimpleMergeField<>("kindErwBeduerfnisse", BOOLEAN_X_CONVERTER)),
	kindDeutsch(new SimpleMergeField<>("kindDeutsch", BOOLEAN_X_CONVERTER)),
	eingeschult(new SimpleMergeField<>("eingeschult", BOOLEAN_X_CONVERTER)),

	zeitabschnittVon(new SimpleMergeField<>("zeitabschnittVon", DATE_CONVERTER)),
	zeitabschnittBis(new SimpleMergeField<>("zeitabschnittBis", DATE_CONVERTER)),
	betreuungsStatus(new SimpleMergeField<>("betreuungsStatus", STRING_CONVERTER)),
	betreuungsPensum(new SimpleMergeField<>("betreuungsPensum", PERCENT_CONVERTER)),
	anspruchsPensum(new SimpleMergeField<>("anspruchsPensum", PERCENT_CONVERTER)),
	bgPensum(new SimpleMergeField<>("bgPensum", PERCENT_CONVERTER)),
	bgStunden(new SimpleMergeField<>("bgStunden", BIGDECIMAL_CONVERTER)),
	vollkosten(new SimpleMergeField<>("vollkosten", BIGDECIMAL_CONVERTER)),
	elternbeitrag(new SimpleMergeField<>("elternbeitrag", BIGDECIMAL_CONVERTER)),
	verguenstigt(new SimpleMergeField<>("verguenstigt", BIGDECIMAL_CONVERTER));

	@Nonnull
	private final MergeField<?> mergeField;

	<V> MergeFieldGesuchstellerKinderBetreuung(@Nonnull MergeField<V> mergeField) {
		this.mergeField = mergeField;
	}

	@Override
	@Nonnull
	public <V> MergeField<V> getMergeField() {
		//noinspection unchecked
		return (MergeField<V>) mergeField;
	}
}
