package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GeuschStichtagExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchstichtag.MergeFieldGesuchStichtag;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GeuschZeitraumExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.MergeFieldGesuchZeitraum;
import ch.dvbern.ebegu.reporting.kanton.KantonDataRow;
import ch.dvbern.ebegu.reporting.kanton.KantonExcelConverter;
import ch.dvbern.ebegu.reporting.kanton.MergeFieldKanton;
import ch.dvbern.ebegu.reporting.lib.*;
import ch.dvbern.ebegu.reporting.zahlungauftrag.MergeFieldZahlungAuftrag;
import ch.dvbern.ebegu.reporting.zahlungauftrag.ZahlungAuftragExcelConverter;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.*;
import static ch.dvbern.ebegu.services.ReportServiceBean.ReportResource.*;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 31/01/2017.
 */
@Stateless
@Local(ReportService.class)
public class ReportServiceBean extends AbstractReportServiceBean implements ReportService {

	public static final String DATA = "Data";

	@Inject
	private GeuschStichtagExcelConverter geuschStichtagExcelConverter;

	@Inject
	private GeuschZeitraumExcelConverter geuschZeitraumExcelConverter;

	@Inject
	private KantonExcelConverter kantonExcelConverter;

	@Inject
	private ZahlungAuftragExcelConverter zahlungAuftragExcelConverter;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private ZahlungService zahlungService;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	private static final String MIME_TYPE_EXCEL = "application/vnd.ms-excel";
	private static final String TEMP_REPORT_FOLDERNAME = "tempReports";

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public List<GesuchStichtagDataRow> getReportDataGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID) {

		Objects.requireNonNull(datetime, "Das Argument 'date' darf nicht leer sein");

		EntityManager em = persistence.getEntityManager();

		List<GesuchStichtagDataRow> results = null;

		if (em != null) {

			Query gesuchStichtagQuery = em.createNamedQuery("GesuchStichtagNativeSQLQuery");
			// Wir rechnen zum Stichtag einen Tag dazu, damit es bis 24.00 des Vorabends gilt.
			gesuchStichtagQuery.setParameter("stichTagDate", DateUtil.SQL_DATETIME_FORMAT.format(datetime.plusDays(1)));
			gesuchStichtagQuery.setParameter("gesuchPeriodeID", gesuchPeriodeID);
			gesuchStichtagQuery.setParameter("onlySchulamt", principalBean.isCallerInRole(SCHULAMT) ? 1 : 0);
			results = gesuchStichtagQuery.getResultList();
		}
		return results;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public UploadFileInfo generateExcelReportGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		Objects.requireNonNull(datetime, "Das Argument 'date' darf nicht leer sein");

		final ReportResource reportResource = VORLAGE_REPORT_GESUCH_STICHTAG;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Objects.requireNonNull(is, "Vorlage '" + reportResource.getTemplatePath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		List<GesuchStichtagDataRow> reportData = getReportDataGesuchStichtag(datetime, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschStichtagExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		geuschStichtagExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportResource.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public List<GesuchZeitraumDataRow> getReportDataGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws IOException, URISyntaxException {

		Objects.requireNonNull(datetimeVon, "Das Argument 'datetimeVon' darf nicht leer sein");
		Objects.requireNonNull(datetimeBis, "Das Argument 'datetimeBis' darf nicht leer sein");

		EntityManager em = persistence.getEntityManager();

		List<GesuchZeitraumDataRow> results = null;

		if (em != null) {
			Query gesuchPeriodeQuery = em.createNamedQuery("GesuchZeitraumNativeSQLQuery");
			gesuchPeriodeQuery.setParameter("fromDateTime", DateUtil.SQL_DATETIME_FORMAT.format(datetimeVon));
			gesuchPeriodeQuery.setParameter("fromDate", DateUtil.SQL_DATE_FORMAT.format(datetimeVon));
			gesuchPeriodeQuery.setParameter("toDateTime", DateUtil.SQL_DATETIME_FORMAT.format(datetimeBis));
			gesuchPeriodeQuery.setParameter("toDate", DateUtil.SQL_DATE_FORMAT.format(datetimeBis));
			gesuchPeriodeQuery.setParameter("gesuchPeriodeID", gesuchPeriodeID);
			gesuchPeriodeQuery.setParameter("onlySchulamt", principalBean.isCallerInRole(SCHULAMT) ? 1 : 0);
			results = gesuchPeriodeQuery.getResultList();
		}
		return results;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public UploadFileInfo generateExcelReportGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		Objects.requireNonNull(datetimeVon, "Das Argument 'datetimeVon' darf nicht leer sein");
		Objects.requireNonNull(datetimeBis, "Das Argument 'datetimeBis' darf nicht leer sein");

		final ReportResource reportResource = VORLAGE_REPORT_GESUCH_ZEITRAUM;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Objects.requireNonNull(is, "Vorlage '" + reportResource.getTemplatePath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		List<GesuchZeitraumDataRow> reportData = getReportDataGesuchZeitraum(datetimeVon, datetimeBis, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschZeitraumExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		geuschZeitraumExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportResource.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public List<KantonDataRow> getReportDataKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) throws IOException, URISyntaxException {
		Validate.notNull(datumVon, "Das Argument 'datumVon' darf nicht leer sein");
		Validate.notNull(datumBis, "Das Argument 'datumBis' darf nicht leer sein");

		// Alle Verfuegungszeitabschnitte zwischen datumVon und datumBis. Aber pro Fall immer nur das zuletzt verfuegte.
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = builder.createQuery(VerfuegungZeitabschnitt.class);
		query.distinct(true);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		// startAbschnitt <= datumBis && endeAbschnitt >= datumVon
		Predicate predicateStart = builder.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), datumBis);
		predicatesToUse.add(predicateStart);
		Predicate predicateEnd = builder.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), datumVon);
		predicatesToUse.add(predicateEnd);

		// nur das neuest verfuegte Gesuch
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.getGesuchsperiodeAm(datumVon);
		if (gesuchsperiode.isPresent()) {
			List<String> neuesteVerfuegteAntraege = gesuchService.getNeuesteVerfuegteAntraege(gesuchsperiode.get());
			if (!neuesteVerfuegteAntraege.isEmpty()) {
				Predicate predicateAktuellesGesuch = root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(neuesteVerfuegteAntraege);
				predicatesToUse.add(predicateAktuellesGesuch);
			} else {
				return Collections.emptyList();
			}
		} else {
			return Collections.emptyList();
		}

		// Sichtbarkeit nach eingeloggtem Benutzer
		boolean isInstitutionsbenutzer = principalBean.isCallerInAnyOfRole(UserRole.SACHBEARBEITER_INSTITUTION, UserRole.SACHBEARBEITER_TRAEGERSCHAFT);
		if (isInstitutionsbenutzer) {
			Collection<Institution> allowedInstitutionen = institutionService.getAllowedInstitutionenForCurrentBenutzer();
			Predicate predicateAllowedInstitutionen = root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(allowedInstitutionen);
			predicatesToUse.add(predicateAllowedInstitutionen);
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(builder, predicatesToUse));
		List<VerfuegungZeitabschnitt> criteriaResults = persistence.getCriteriaResults(query);

		List<KantonDataRow> kantonDataRowList = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : criteriaResults) {
			KantonDataRow row = new KantonDataRow();
			row.setBgNummer(zeitabschnitt.getVerfuegung().getBetreuung().getBGNummer());
			row.setGesuchId(zeitabschnitt.getVerfuegung().getBetreuung().extractGesuch().getId());
			row.setName(zeitabschnitt.getVerfuegung().getBetreuung().getKind().getKindJA().getNachname());
			row.setVorname(zeitabschnitt.getVerfuegung().getBetreuung().getKind().getKindJA().getVorname());
			row.setGeburtsdatum(zeitabschnitt.getVerfuegung().getBetreuung().getKind().getKindJA().getGeburtsdatum());
			row.setZeitabschnittVon(zeitabschnitt.getGueltigkeit().getGueltigAb());
			row.setZeitabschnittBis(zeitabschnitt.getGueltigkeit().getGueltigBis());
			row.setBgPensum(MathUtil.DEFAULT.from(zeitabschnitt.getBgPensum()));
			row.setElternbeitrag(zeitabschnitt.getElternbeitrag());
			row.setVerguenstigung(zeitabschnitt.getVerguenstigung());
			row.setInstitution(zeitabschnitt.getVerfuegung().getBetreuung().getInstitutionStammdaten().getInstitution().getName());
			row.setBetreuungsTyp(zeitabschnitt.getVerfuegung().getBetreuung().getBetreuungsangebotTyp().name());
			row.setOeffnungstage(zeitabschnitt.getVerfuegung().getBetreuung().getInstitutionStammdaten().getOeffnungstage());
			kantonDataRowList.add(row);
		}
		return kantonDataRowList;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public UploadFileInfo generateExcelReportKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {
		Validate.notNull(datumVon, "Das Argument 'datumVon' darf nicht leer sein");
		Validate.notNull(datumBis, "Das Argument 'datumBis' darf nicht leer sein");

		final ReportResource reportResource = VORLAGE_REPORT_KANTON;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Validate.notNull(is, "Vorlage '" + reportResource.getTemplatePath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		List<KantonDataRow> reportData = getReportDataKanton(datumVon, datumBis);
		ExcelMergerDTO excelMergerDTO = kantonExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), datumVon, datumBis);

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		kantonExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportResource.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	@Nonnull
	private MimeType getContentTypeForExport() {
		try {
			return new MimeType(MIME_TYPE_EXCEL);
		} catch (MimeTypeParseException e) {
			throw new EbeguRuntimeException("getContentTypeForExport", "could not parse mime type", e, MIME_TYPE_EXCEL);

		}
	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public UploadFileInfo generateExcelReportZahlungAuftrag(String auftragId) throws ExcelMergeException {

		Zahlungsauftrag zahlungsauftrag = zahlungService.findZahlungsauftrag(auftragId)
			.orElseThrow(() -> new EbeguEntityNotFoundException("generateExcelReportZahlungAuftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, auftragId));

		return getUploadFileInfoZahlung(zahlungsauftrag.getZahlungen(), zahlungsauftrag.getBeschrieb() + ".xlsx");
	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public UploadFileInfo generateExcelReportZahlung(String zahlungId) throws ExcelMergeException {

		List<Zahlung> reportData = new ArrayList<>();

		Zahlung zahlung = zahlungService.findZahlung(zahlungId)
			.orElseThrow(() -> new EbeguEntityNotFoundException("generateExcelReportZahlungAuftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, zahlungId));

		reportData.add(zahlung);

		return getUploadFileInfoZahlung(reportData, "Zahlungen_" + zahlung.getInstitutionStammdaten().getInstitution().getName() + ".xlsx");
	}

	private UploadFileInfo getUploadFileInfoZahlung(List<Zahlung> reportData, String excelFileName) throws ExcelMergeException {
		final ReportResource reportResource = VORLAGE_REPORT_ZAHLUNG_AUFTRAG;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Objects.requireNonNull(is, "Vorlage '" + reportResource.getTemplatePath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		Collection<Institution> allowedInst = institutionService.getAllowedInstitutionenForCurrentBenutzer();

		ExcelMergerDTO excelMergerDTO = zahlungAuftragExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), principalBean.discoverMostPrivilegedRole(), allowedInst);

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		zahlungAuftragExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			excelFileName,
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	public enum ReportResource {

		VORLAGE_REPORT_GESUCH_STICHTAG("/reporting/GesuchStichtag.xlsx", "GesuchStichtag.xlsx", DATA,
			MergeFieldGesuchStichtag.class),
		VORLAGE_REPORT_GESUCH_ZEITRAUM("/reporting/GesuchZeitraum.xlsx", "GesuchZeitraum.xlsx", DATA,
			MergeFieldGesuchZeitraum.class),
		VORLAGE_REPORT_KANTON("/reporting/Kanton.xlsx", "Kanton.xlsx", DATA,
			MergeFieldKanton.class),

		//TODO: Achtung mit Filename, da mehrere Dokumente mit gleichem Namen aber unterschiedlichem Inhalt gespeichert werden
		VORLAGE_REPORT_ZAHLUNG_AUFTRAG("/reporting/ZahlungAuftrag.xlsx", "ZahlungAuftrag.xlsx", DATA,
			MergeFieldZahlungAuftrag.class);

		@Nonnull
		private final String templatePath;
		@Nonnull
		private final String defaultExportFilename;
		@Nonnull
		private final Class<? extends MergeField> mergeFields;
		@Nonnull
		private final String dataSheetName;

		ReportResource(@Nonnull String templatePath, @Nonnull String defaultExportFilename,
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
}
