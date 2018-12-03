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

package ch.dvbern.ebegu.enums.reporting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;

/**
 * Enum fuer ReportVorlage
 */
public enum ReportVorlage {

	// Achtung mit Filename, da mehrere Dokumente mit gleichem Namen aber unterschiedlichem Inhalt gespeichert werden.
	// Falls der Name geaendert wuerde, muesste das File wieder geloescht werden.
	VORLAGE_REPORT_GESUCH_STICHTAG("/reporting/GesuchStichtag.xlsx", "GesuchStichtag.xlsx", Constants.DATA,
		MergeFieldGesuchStichtag.class),
	VORLAGE_REPORT_GESUCH_ZEITRAUM("/reporting/GesuchZeitraum.xlsx", "VerfuegteGesucheMutationenNachZeitraum.xlsx", Constants.DATA,
		MergeFieldGesuchZeitraum.class),
	VORLAGE_REPORT_KANTON("/reporting/Kanton.xlsx", "Kanton.xlsx", Constants.DATA,
		MergeFieldKanton.class),
	VORLAGE_REPORT_MITARBEITERINNEN("/reporting/Mitarbeiterinnen.xlsx", "Mitarbeiterinnen.xlsx", Constants.DATA,
		MergeFieldMitarbeiterinnen.class),
	VORLAGE_REPORT_BENUTZER("/reporting/Benutzer.xlsx", "Benutzer.xlsx", Constants.DATA,
		MergeFieldBenutzer.class),
	VORLAGE_REPORT_ZAHLUNG_AUFTRAG("/reporting/ZahlungAuftrag.xlsx", "ZahlungAuftrag.xlsx", Constants.DATA,
		MergeFieldZahlungAuftrag.class),
	VORLAGE_REPORT_ZAHLUNG_AUFTRAG_PERIODE("/reporting/ZahlungAuftragPeriode.xlsx", "ZahlungAuftragPeriode.xlsx", Constants.DATA,
		MergeFieldZahlungAuftragPeriode.class),
	VORLAGE_REPORT_GESUCHSTELLER_KINDER_BETREUUNG("/reporting/GesuchstellerKinderBetreuung.xlsx", "GesuchstellerKinderBetreuung.xlsx", Constants.DATA,
		MergeFieldGesuchstellerKinderBetreuung.class),
	VORLAGE_REPORT_KINDER("/reporting/Kinder.xlsx", "Kinder.xlsx", Constants.DATA,
		MergeFieldGesuchstellerKinderBetreuung.class),
	VORLAGE_REPORT_GESUCHSTELLER("/reporting/Gesuchsteller.xlsx", "Gesuchsteller.xlsx", Constants.DATA,
		MergeFieldGesuchstellerKinderBetreuung.class),
	VORLAGE_REPORT_MASSENVERSAND("/reporting/Massenversand.xlsx", "Massenversand.xlsx", Constants.DATA,
		MergeFieldMassenversand.class);

	@Nonnull
	private final String templatePath;
	@Nonnull
	private final String defaultExportFilename;
	@Nonnull
	private final Class<? extends MergeFieldProvider> mergeFields;
	@Nonnull
	private final String dataSheetName;

	ReportVorlage(@Nonnull String templatePath, @Nonnull String defaultExportFilename,
		@Nonnull String dataSheetName, @Nonnull Class<? extends MergeFieldProvider> mergeFields) {
		this.templatePath = templatePath;
		this.defaultExportFilename = defaultExportFilename;
		this.mergeFields = mergeFields;
		this.dataSheetName = dataSheetName;
	}

	@Nonnull
	public String getTemplatePath() {
		return templatePath;
	}

	@Nonnull
	public String getDefaultExportFilename() {
		return defaultExportFilename;
	}

	@Nonnull
	public MergeFieldProvider[] getMergeFields() {
		return mergeFields.getEnumConstants();
	}

	@Nonnull
	public String getDataSheetName() {
		return dataSheetName;
	}

	public static boolean checkAllowed(@Nullable UserRole role, ReportVorlage vorlage) {
		if (role == null) {
			return false;
		}
		if (UserRole.getInstitutionTraegerschaftRoles().contains(role)) {
			if (vorlage == VORLAGE_REPORT_KINDER || vorlage == VORLAGE_REPORT_KANTON) {
				return true;
			}
			return false;
		}
		if (UserRole.getSchulamtRoles().contains(role)) {
			if (vorlage == VORLAGE_REPORT_GESUCH_STICHTAG || vorlage == VORLAGE_REPORT_GESUCH_ZEITRAUM
				|| vorlage == VORLAGE_REPORT_KINDER || vorlage == VORLAGE_REPORT_GESUCHSTELLER) {
				return true;
			}
			return false;
		}
		if (UserRole.GESUCHSTELLER == role || UserRole.STEUERAMT == role || UserRole.JURIST == role) {
			return false;
		}
		return true;
	}
}
