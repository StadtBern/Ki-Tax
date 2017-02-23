package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GeuschStichtagExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchstichtag.MergeFieldGesuchStichtag;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GeuschZeitraumExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.MergeFieldGesuchZeitraum;
import ch.dvbern.ebegu.reporting.lib.DateUtil;
import ch.dvbern.ebegu.reporting.lib.ExcelMergeException;
import ch.dvbern.ebegu.reporting.lib.ExcelMerger;
import ch.dvbern.ebegu.reporting.lib.ExcelMergerDTO;
import ch.dvbern.ebegu.reporting.zahlungauftrag.MergeFieldZahlungAuftrag;
import ch.dvbern.ebegu.reporting.zahlungauftrag.ZahlungAuftragExcelConverter;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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

	@Inject
	private GeuschStichtagExcelConverter geuschStichtagExcelConverter;

	@Inject
	private GeuschZeitraumExcelConverter geuschZeitraumExcelConverter;

	@Inject
	private ZahlungAuftragExcelConverter zahlungAuftragExcelConverter;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private ZahlungService zahlungService;

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public List<GesuchStichtagDataRow> getReportDataGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID) {

		Objects.requireNonNull(datetime, "Das Argument 'date' darf nicht leer sein");

		EntityManager em = persistence.getEntityManager();

		List<GesuchStichtagDataRow> results = null;

		if (em != null) {

			Query  gesuchStichtagQuery = em.createNamedQuery("GesuchStichtagNativeSQLQuery");
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
	public byte[] generateExcelReportGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		Objects.requireNonNull(datetime, "Das Argument 'date' darf nicht leer sein");

		InputStream is = ReportServiceBean.class.getResourceAsStream(VORLAGE_REPORT_GESUCH_STICHTAG.getPath());
		Objects.requireNonNull(is, "Vorlage '" + VORLAGE_REPORT_GESUCH_STICHTAG.getPath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet("Data");

		List<GesuchStichtagDataRow> reportData = getReportDataGesuchStichtag(datetime, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschStichtagExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, MergeFieldGesuchStichtag.values());
		geuschStichtagExcelConverter.applyAutoSize(sheet);

		return createWorkbook(workbook);
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
	public byte[] generateExcelReportGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		Objects.requireNonNull(datetimeVon, "Das Argument 'datetimeVon' darf nicht leer sein");
		Objects.requireNonNull(datetimeBis, "Das Argument 'datetimeBis' darf nicht leer sein");

		InputStream is = ReportServiceBean.class.getResourceAsStream(VORLAGE_REPORT_GESUCH_ZEITRAUM.getPath());
		Objects.requireNonNull(is, "Vorlage '" + VORLAGE_REPORT_GESUCH_ZEITRAUM.getPath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet("Data");

		List<GesuchZeitraumDataRow> reportData = getReportDataGesuchZeitraum(datetimeVon, datetimeBis, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschZeitraumExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, MergeFieldGesuchZeitraum.values());
		geuschZeitraumExcelConverter.applyAutoSize(sheet);

		return createWorkbook(workbook);
	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public byte[] generateExcelReportZahlungAuftrag(String auftragId, Optional<InstitutionStammdaten> institution) throws ExcelMergeException {

		List<Zahlung> reportData;
		if (institution.isPresent()) {
			//TODO: get data filtered by Institution, zahlungService method to do this?
			reportData = new ArrayList<>();
		} else {
			Zahlungsauftrag zahlungsauftrag = zahlungService.findZahlungsauftrag(auftragId)
				.orElseThrow(() -> new EbeguEntityNotFoundException("generateExcelReportZahlungAuftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, auftragId));
			reportData = zahlungsauftrag.getZahlungen();
		}

		InputStream is = ReportServiceBean.class.getResourceAsStream(VORLAGE_REPORT_ZAHLUNG_AUFTRAG.getPath());
		Objects.requireNonNull(is, "Vorlage '" + VORLAGE_REPORT_ZAHLUNG_AUFTRAG.getPath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet("Data");

		ExcelMergerDTO excelMergerDTO = zahlungAuftragExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, MergeFieldZahlungAuftrag.values());
		zahlungAuftragExcelConverter.applyAutoSize(sheet);

		return createWorkbook(workbook);
	}

	public enum ReportResource {

		VORLAGE_REPORT_GESUCH_STICHTAG("/reporting/GesuchStichtag.xlsx"),
		VORLAGE_REPORT_GESUCH_ZEITRAUM("/reporting/GesuchZeitraum.xlsx"),
		VORLAGE_REPORT_ZAHLUNG_AUFTRAG("/reporting/ZahlungAuftrag.xlsx");

		private String path;

		ReportResource(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}
}
