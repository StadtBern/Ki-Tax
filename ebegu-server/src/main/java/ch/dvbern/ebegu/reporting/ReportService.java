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

package ch.dvbern.ebegu.reporting;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.kanton.KantonDataRow;
import ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen.MitarbeiterinnenDataRow;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;

public interface ReportService {

	// Gesuch Stichtag
	@Nonnull
	List<GesuchStichtagDataRow> getReportDataGesuchStichtag(@Nonnull LocalDate date, @Nullable String gesuchPeriodeID)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportGesuchStichtag(@Nonnull LocalDate date, @Nullable String gesuchPeriodeID)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// Gesuch Zeitraum

	@Nonnull
	List<GesuchZeitraumDataRow> getReportDataGesuchZeitraum(@Nonnull LocalDate dateVon, @Nonnull LocalDate dateBis, @Nullable String gesuchPeriodeID)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportGesuchZeitraum(@Nonnull LocalDate dateVon, @Nonnull LocalDate dateBis, @Nullable String gesuchPeriodeID)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// Kanton
	@Nonnull
	List<KantonDataRow> getReportDataKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// MitarbeterInnen
	@Nonnull
	List<MitarbeiterinnenDataRow> getReportMitarbeiterinnen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportMitarbeiterinnen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// Zahlungen

	@Nullable
	UploadFileInfo generateExcelReportZahlungAuftrag(@Nonnull String auftragId) throws ExcelMergeException;

	@Nullable
	UploadFileInfo generateExcelReportZahlung(@Nonnull String zahlungId) throws ExcelMergeException;

	@Nullable
	UploadFileInfo generateExcelReportZahlungPeriode(@Nonnull String gesuchsperiodeId) throws ExcelMergeException;

	// Gesuchsteller / Kinder / Betreuung
	@Nullable
	UploadFileInfo generateExcelReportGesuchstellerKinderBetreuung(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable String gesuchPeriodeId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportKinder(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable String gesuchPeriodeId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportGesuchsteller(@Nonnull LocalDate stichtag) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;
}
