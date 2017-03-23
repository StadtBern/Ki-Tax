package ch.dvbern.ebegu.enums;

import ch.dvbern.ebegu.reporting.gesuchstellerKinderBetreuung.MergeFieldGesuchstellerKinderBetreuung;
import ch.dvbern.ebegu.reporting.gesuchstichtag.MergeFieldGesuchStichtag;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.MergeFieldGesuchZeitraum;
import ch.dvbern.ebegu.reporting.kanton.MergeFieldKanton;
import ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen.MergeFieldMitarbeiterinnen;
import ch.dvbern.ebegu.reporting.zahlungauftrag.MergeFieldZahlungAuftrag;
import ch.dvbern.ebegu.reporting.zahlungauftrag.MergeFieldZahlungAuftragPeriode;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.excelmerger.MergeField;

import javax.annotation.Nonnull;

/**
 * Enum fuer ReportVorlage
 */
public enum ReportVorlage {

	// TODO Achtung mit Filename, da mehrere Dokumente mit gleichem Namen aber unterschiedlichem Inhalt gespeichert werden. Falls der Name geaendert wuerde, muesste das File wieder geloescht werden.
	VORLAGE_REPORT_GESUCH_STICHTAG("/reporting/GesuchStichtag.xlsx", "GesuchStichtag.xlsx", Constants.DATA,
		MergeFieldGesuchStichtag.class),
	VORLAGE_REPORT_GESUCH_ZEITRAUM("/reporting/GesuchZeitraum.xlsx", "GesuchZeitraum.xlsx", Constants.DATA,
		MergeFieldGesuchZeitraum.class),
	VORLAGE_REPORT_KANTON("/reporting/Kanton.xlsx", "Kanton.xlsx", Constants.DATA,
		MergeFieldKanton.class),
	VORLAGE_REPORT_MITARBEITERINNEN("/reporting/Mitarbeiterinnen.xlsx", "Mitarbeiterinnen.xlsx", Constants.DATA,
		MergeFieldMitarbeiterinnen.class),
	VORLAGE_REPORT_ZAHLUNG_AUFTRAG("/reporting/ZahlungAuftrag.xlsx", "ZahlungAuftrag.xlsx", Constants.DATA,
		MergeFieldZahlungAuftrag.class),
	VORLAGE_REPORT_ZAHLUNG_AUFTRAG_PERIODE("/reporting/ZahlungAuftragPeriode.xlsx", "ZahlungAuftragPeriode.xlsx", Constants.DATA,
		MergeFieldZahlungAuftragPeriode.class),
	VORLAGE_REPORT_GESUCHSTELLER_KINDER_BETREUUNG("/reporting/GesuchstellerKinderBetreuung.xlsx", "GesuchstellerKinderBetreuung.xlsx", Constants.DATA,
		MergeFieldGesuchstellerKinderBetreuung.class),
	VORLAGE_REPORT_KINDER("/reporting/Kinder.xlsx", "Kinder.xlsx", Constants.DATA,
		MergeFieldGesuchstellerKinderBetreuung.class);

	@Nonnull
	private final String templatePath;
	@Nonnull
	private final String defaultExportFilename;
	@Nonnull
	private final Class<? extends MergeField> mergeFields;
	@Nonnull
	private final String dataSheetName;

	ReportVorlage(@Nonnull String templatePath, @Nonnull String defaultExportFilename,
				   @Nonnull String dataSheetName, @Nonnull Class<? extends MergeField> mergeFields) {
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
	public MergeField[] getMergeFields() {
		return mergeFields.getEnumConstants();
	}

	@Nonnull
	public String getDataSheetName() {
			return dataSheetName;
		}
}
