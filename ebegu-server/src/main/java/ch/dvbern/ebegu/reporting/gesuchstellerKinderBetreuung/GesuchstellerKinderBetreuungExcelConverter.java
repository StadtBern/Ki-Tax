/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dvbern.ebegu.reporting.gesuchstellerKinderBetreuung;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;

import org.apache.poi.ss.usermodel.Sheet;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.reporting.MergeFieldGesuchstellerKinderBetreuung;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;
import ch.dvbern.oss.lib.excelmerger.ExcelMerger;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import ch.dvbern.oss.lib.excelmerger.RowFiller;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class GesuchstellerKinderBetreuungExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
	}

	@Nonnull
	public Sheet mergeHeaderFields(@Nonnull List<GesuchstellerKinderBetreuungDataRow> data, @Nonnull Sheet sheet,
		@Nonnull LocalDate stichtag) throws ExcelMergeException {

		checkNotNull(data);

		ExcelMergerDTO excelMergerDTO = new ExcelMergerDTO();
		List<MergeField<?>> mergeFields = new ArrayList<>();

		mergeFields.add(MergeFieldGesuchstellerKinderBetreuung.stichtag.getMergeField());
		excelMergerDTO.addValue(MergeFieldGesuchstellerKinderBetreuung.stichtag.getMergeField(), stichtag);

		ExcelMerger.mergeData(sheet, mergeFields, excelMergerDTO);

		return sheet;
	}

	@Nonnull
	public Sheet mergeHeaderFields(@Nonnull List<GesuchstellerKinderBetreuungDataRow> data, @Nonnull Sheet sheet,
		@Nonnull LocalDate auswertungVon, @Nonnull LocalDate auswertungBis, @Nullable Gesuchsperiode auswertungPeriode) throws ExcelMergeException {

		checkNotNull(data);

		ExcelMergerDTO excelMergerDTO = new ExcelMergerDTO();
		List<MergeField<?>> mergeFields = new ArrayList<>();

		mergeFields.add(MergeFieldGesuchstellerKinderBetreuung.auswertungVon.getMergeField());
		excelMergerDTO.addValue(MergeFieldGesuchstellerKinderBetreuung.auswertungVon.getMergeField(), auswertungVon);

		mergeFields.add(MergeFieldGesuchstellerKinderBetreuung.auswertungBis.getMergeField());
		excelMergerDTO.addValue(MergeFieldGesuchstellerKinderBetreuung.auswertungBis.getMergeField(), auswertungBis);

		mergeFields.add(MergeFieldGesuchstellerKinderBetreuung.auswertungPeriode.getMergeField());
		if (auswertungPeriode != null) {
			excelMergerDTO.addValue(MergeFieldGesuchstellerKinderBetreuung.auswertungPeriode.getMergeField(), auswertungPeriode.getGesuchsperiodeString());
		}

		ExcelMerger.mergeData(sheet, mergeFields, excelMergerDTO);

		return sheet;
	}

	public void mergeRows(RowFiller rowFiller, @Nonnull List<GesuchstellerKinderBetreuungDataRow> data) {
		data.forEach(dataRow -> {
			ExcelMergerDTO excelRowGroup = new ExcelMergerDTO();
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgNummer, dataRow.getBgNummer());
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.institution, dataRow.getInstitution());
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.betreuungsTyp, dataRow.getBetreuungsTyp().name());
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.periode, dataRow.getPeriode());
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.gesuchStatus, dataRow.getGesuchStatus());

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
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.betreuungsStatus, dataRow.getBetreuungsStatus());
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.betreuungsPensum, dataRow.getBetreuungsPensum());
			BigDecimal anspruchsPensum = dataRow.getAnspruchsPensum();
			excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.anspruchsPensum, anspruchsPensum);
			if (anspruchsPensum.compareTo(BigDecimal.ZERO) > 0) {
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgPensum, dataRow.getBgPensum());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgStunden, dataRow.getBgStunden());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.vollkosten, dataRow.getVollkosten());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.elternbeitrag, dataRow.getElternbeitrag());
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.verguenstigt, dataRow.getVerguenstigt());
			} else {
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgPensum, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.bgStunden, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.vollkosten, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.elternbeitrag, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldGesuchstellerKinderBetreuung.verguenstigt, BigDecimal.ZERO);
			}

			rowFiller.fillRow(excelRowGroup);
		});
	}
}


