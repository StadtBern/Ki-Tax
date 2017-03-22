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

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.excelmerger.ExcelConverter;
import ch.dvbern.lib.excelmerger.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class GesuchstellerKinderBetreuungExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
//		sheet.autoSizeColumn(0); // bgNummer
//		sheet.autoSizeColumn(1); // institution
//		sheet.autoSizeColumn(2); // betreuungsTyp
//		sheet.autoSizeColumn(3); // periode
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<GesuchstellerKinderBetreuungDataRow> data, @Nonnull Locale lang,
										   LocalDate auswertungVon, LocalDate auswertungBis, Gesuchsperiode auswertungPeriode) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();

		sheet.addValue(MergeFieldGesuchstellerKinderBetreuung.auswertungVon, auswertungVon);
		sheet.addValue(MergeFieldGesuchstellerKinderBetreuung.auswertungBis, auswertungBis);
		if (auswertungPeriode != null) {
			sheet.addValue(MergeFieldGesuchstellerKinderBetreuung.auswertungPeriode, auswertungPeriode.getGesuchsperiodeString());
		}

		data.forEach(dataRow -> {
				ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldGesuchstellerKinderBetreuung.repeatRow);
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgNummer, dataRow.getBgNummer());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.institution, dataRow.getInstitution());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.betreuungsTyp, dataRow.getBetreuungsTyp().name());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.periode, dataRow.getPeriode());

				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.eingangsdatum, dataRow.getEingangsdatum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.verfuegungsdatum, dataRow.getVerfuegungsdatum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.fallId, dataRow.getFallId());

				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Name, dataRow.getGs1Name());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Vorname, dataRow.getGs1Vorname());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Strasse, dataRow.getGs1Strasse());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Hausnummer, dataRow.getGs1Hausnummer());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Zusatzzeile, dataRow.getGs1Zusatzzeile());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Plz, dataRow.getGs1Plz());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Ort, dataRow.getGs1Ort());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwkId, dataRow.getGs1EwkId());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1Diplomatenstatus, dataRow.getGs1Diplomatenstatus());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwpAngestellt, MathUtil.GANZZAHL.from(dataRow.getGs1EwpAngestellt()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwpAusbildung, MathUtil.GANZZAHL.from(dataRow.getGs1EwpAusbildung()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwpSelbstaendig, MathUtil.GANZZAHL.from(dataRow.getGs1EwpSelbstaendig()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwpRav, MathUtil.GANZZAHL.from(dataRow.getGs1EwpRav()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwpZuschlag, MathUtil.GANZZAHL.from(dataRow.getGs1EwpZuschlag()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs1EwpGesundhtl, MathUtil.GANZZAHL.from(dataRow.getGs1EwpGesundhtl()));

				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Name, dataRow.getGs2Name());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Vorname, dataRow.getGs2Vorname());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Strasse, dataRow.getGs2Strasse());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Hausnummer, dataRow.getGs2Hausnummer());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Zusatzzeile, dataRow.getGs2Zusatzzeile());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Plz, dataRow.getGs2Plz());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Ort, dataRow.getGs2Ort());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwkId, dataRow.getGs2EwkId());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2Diplomatenstatus, dataRow.getGs2Diplomatenstatus());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwpAngestellt, MathUtil.GANZZAHL.from(dataRow.getGs2EwpAngestellt()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwpAusbildung, MathUtil.GANZZAHL.from(dataRow.getGs2EwpAusbildung()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwpSelbstaendig, MathUtil.GANZZAHL.from(dataRow.getGs2EwpSelbstaendig()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwpRav, MathUtil.GANZZAHL.from(dataRow.getGs2EwpRav()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwpZuschlag, MathUtil.GANZZAHL.from(dataRow.getGs2EwpZuschlag()));
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gs2EwpGesundhtl, MathUtil.GANZZAHL.from(dataRow.getGs2EwpGesundhtl()));

				String familiensituation = dataRow.getFamiliensituation() != null ? dataRow.getFamiliensituation().name() : "";
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.familiensituation, familiensituation);
				String kardinalitaet = dataRow.getKardinalitaet() != null ? dataRow.getKardinalitaet().name() : "";
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kardinalitaet, kardinalitaet);
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.familiengroesse, dataRow.getFamiliengroesse());

				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.massgEinkVorFamilienabzug, dataRow.getMassgEinkVorFamilienabzug());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.familienabzug, dataRow.getFamilienabzug());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.massgEink, dataRow.getMassgEink());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.einkommensjahr, dataRow.getEinkommensjahr());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.ekvVorhanden, dataRow.getEkvVorhanden());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.stvGeprueft, dataRow.getStvGeprueft());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.veranlagt, dataRow.getVeranlagt());

				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kindName, dataRow.getKindName());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kindVorname, dataRow.getKindVorname());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kindGeburtsdatum, dataRow.getKindGeburtsdatum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kindFachstelle, dataRow.getKindFachstelle());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kindErwBeduerfnisse, dataRow.getKindErwBeduerfnisse());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.kindDeutsch, dataRow.getKindDeutsch());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.eingeschult, dataRow.getKindEingeschult());

				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.zeitabschnittVon, dataRow.getZeitabschnittVon());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.zeitabschnittBis, dataRow.getZeitabschnittBis());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.betreuungsPensum, dataRow.getBetreuungsPensum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.anspruchsPensum, dataRow.getAnspruchsPensum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgPensum, dataRow.getBgPensum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgStunden, dataRow.getBgStunden());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.vollkosten, dataRow.getVollkosten());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.elternbeitrag, dataRow.getElternbeitrag());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.verguenstigt, dataRow.getVerguenstigt());
			});

		return sheet;
	}
}


