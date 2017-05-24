package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.reporting.gesuchstellerKinderBetreuung.GesuchstellerKinderBetreuungDataRow;
import ch.dvbern.ebegu.reporting.gesuchstellerKinderBetreuung.GesuchstellerKinderBetreuungExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GeuschStichtagExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GeuschZeitraumExcelConverter;
import ch.dvbern.ebegu.reporting.kanton.KantonDataRow;
import ch.dvbern.ebegu.reporting.kanton.KantonExcelConverter;
import ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen.MitarbeiterinnenDataRow;
import ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen.MitarbeiterinnenExcelConverter;
import ch.dvbern.ebegu.reporting.zahlungauftrag.ZahlungAuftragExcelConverter;
import ch.dvbern.ebegu.reporting.zahlungauftrag.ZahlungAuftragPeriodeExcelConverter;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import ch.dvbern.lib.excelmerger.ExcelMergeException;
import ch.dvbern.lib.excelmerger.ExcelMerger;
import ch.dvbern.lib.excelmerger.ExcelMergerDTO;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceBean.class);

	public static final String NICHT_GEFUNDEN = "' nicht gefunden";
	public static final String VORLAGE = "Vorlage '";
	private static final String VALIDIERUNG_STICHTAG = "Das Argument 'stichtag' darf nicht leer sein";
	private static final String VALIDIERUNG_DATUM_VON = "Das Argument 'datumVon' darf nicht leer sein";
	private static final String VALIDIERUNG_DATUM_BIS = "Das Argument 'datumBis' darf nicht leer sein";

	@Inject
	private GeuschStichtagExcelConverter geuschStichtagExcelConverter;

	@Inject
	private GeuschZeitraumExcelConverter geuschZeitraumExcelConverter;

	@Inject
	private KantonExcelConverter kantonExcelConverter;

	@Inject
	private MitarbeiterinnenExcelConverter mitarbeiterinnenExcelConverter;

	@Inject
	private ZahlungAuftragExcelConverter zahlungAuftragExcelConverter;

	@Inject
	private ZahlungAuftragPeriodeExcelConverter zahlungAuftragPeriodeExcelConverter;

	@Inject
	private GesuchstellerKinderBetreuungExcelConverter gesuchstellerKinderBetreuungExcelConverter;

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
	private BetreuungService betreuungService;

	@Inject
	private KindService kindService;

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
			gesuchStichtagQuery.setParameter("stichTagDate", Constants.SQL_DATETIME_FORMAT.format(datetime.plusDays(1)));
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

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_GESUCH_STICHTAG;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Objects.requireNonNull(is, VORLAGE+ reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		List<GesuchStichtagDataRow> reportData = getReportDataGesuchStichtag(datetime, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschStichtagExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		geuschStichtagExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public List<GesuchZeitraumDataRow> getReportDataGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws IOException, URISyntaxException {

		validateDateParams(datetimeVon, datetimeBis);

		// Bevor wir die Statistik starten, muessen gewissen Werte nachgefuehrt werden
		runStatisticsBetreuung();
		runStatisticsAbwesenheiten();
		runStatisticsKinder();

		EntityManager em = persistence.getEntityManager();

		List<GesuchZeitraumDataRow> results = null;

		if (em != null) {
			Query gesuchPeriodeQuery = em.createNamedQuery("GesuchZeitraumNativeSQLQuery");
			gesuchPeriodeQuery.setParameter("fromDateTime", Constants.SQL_DATETIME_FORMAT.format(datetimeVon));
			gesuchPeriodeQuery.setParameter("fromDate", Constants.SQL_DATE_FORMAT.format(datetimeVon));
			gesuchPeriodeQuery.setParameter("toDateTime", Constants.SQL_DATETIME_FORMAT.format(datetimeBis));
			gesuchPeriodeQuery.setParameter("toDate", Constants.SQL_DATE_FORMAT.format(datetimeBis));
			gesuchPeriodeQuery.setParameter("gesuchPeriodeID", gesuchPeriodeID);
			gesuchPeriodeQuery.setParameter("onlySchulamt", principalBean.isCallerInRole(SCHULAMT) ? 1 : 0);
			results = gesuchPeriodeQuery.getResultList();
		}
		return results;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public UploadFileInfo generateExcelReportGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		validateDateParams(datetimeVon, datetimeBis);

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_GESUCH_ZEITRAUM;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Objects.requireNonNull(is, VORLAGE + reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		List<GesuchZeitraumDataRow> reportData = getReportDataGesuchZeitraum(datetimeVon, datetimeBis, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschZeitraumExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		geuschZeitraumExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	@SuppressWarnings("PMD.NcssMethodCount, PMD.AvoidDuplicateLiterals")
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public List<KantonDataRow> getReportDataKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) throws IOException, URISyntaxException {
		validateDateParams(datumVon, datumBis);

		Collection<Gesuchsperiode> relevanteGesuchsperioden = gesuchsperiodeService.getGesuchsperiodenBetween(datumVon, datumBis);
		if (relevanteGesuchsperioden.isEmpty()) {
			return Collections.emptyList();
		}
		// Alle Verfuegungszeitabschnitte zwischen datumVon und datumBis. Aber pro Fall immer nur das zuletzt verfuegte.
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = builder.createQuery(VerfuegungZeitabschnitt.class);
		query.distinct(true);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<VerfuegungZeitabschnitt, Verfuegung> joinVerfuegung = root.join(VerfuegungZeitabschnitt_.verfuegung);
		Join<Verfuegung, Betreuung> joinBetreuung = joinVerfuegung.join(Verfuegung_.betreuung);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		// startAbschnitt <= datumBis && endeAbschnitt >= datumVon
		Predicate predicateStart = builder.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), datumBis);
		predicatesToUse.add(predicateStart);
		Predicate predicateEnd = builder.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), datumVon);
		predicatesToUse.add(predicateEnd);
		// Nur neueste Verfuegung jedes Falls beachten
		Predicate predicateGueltig = builder.equal(joinBetreuung.get(Betreuung_.gueltig), Boolean.TRUE);
		predicatesToUse.add(predicateGueltig);

		// Sichtbarkeit nach eingeloggtem Benutzer
		boolean isInstitutionsbenutzer = principalBean.isCallerInAnyOfRole(UserRole.SACHBEARBEITER_INSTITUTION, UserRole.SACHBEARBEITER_TRAEGERSCHAFT);
		if (isInstitutionsbenutzer) {
			Collection<Institution> allowedInstitutionen = institutionService.getAllowedInstitutionenForCurrentBenutzer();
			Predicate predicateAllowedInstitutionen = root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(allowedInstitutionen);
			predicatesToUse.add(predicateAllowedInstitutionen);
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(builder, predicatesToUse));
		List<VerfuegungZeitabschnitt> zeitabschnittList = persistence.getCriteriaResults(query);
		List<KantonDataRow> kantonDataRowList = convertToKantonDataRow(zeitabschnittList);
		kantonDataRowList.sort(Comparator.comparing(KantonDataRow::getBgNummer).thenComparing(KantonDataRow::getZeitabschnittVon));
		return kantonDataRowList;
	}

	private void validateDateParams(Object datumVon, Object datumBis) {
		Validate.notNull(datumVon, "Das Argument 'datumVon' darf nicht leer sein");
		Validate.notNull(datumBis, "Das Argument 'datumBis' darf nicht leer sein");
	}

	private List<KantonDataRow> convertToKantonDataRow(List<VerfuegungZeitabschnitt> zeitabschnittList) {
		List<KantonDataRow> kantonDataRowList = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnittList) {
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
		validateDateParams(datumVon, datumBis);

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_KANTON;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		List<KantonDataRow> reportData = getReportDataKanton(datumVon, datumBis);
		ExcelMergerDTO excelMergerDTO = kantonExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), datumVon, datumBis);

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		kantonExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	// MitarbeterInnen
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR})
	public List<MitarbeiterinnenDataRow> getReportMitarbeiterinnen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) throws IOException, URISyntaxException {
		validateDateParams(datumVon, datumBis);

		List<Tuple> numberVerantwortlicheGesuche = getAllVerantwortlicheGesuche();
		List<Tuple> numberVerfuegteGesuche = getAllVerfuegteGesuche(datumVon, datumBis);

		return convertToMitarbeiterinnenDataRow(numberVerantwortlicheGesuche, numberVerfuegteGesuche);
	}

	/**
	 * Gibt eine tuple zurueck mit dem ID, dem Nachnamen und Vornamen des Benutzers und die Anzahl Gesuche
	 * bei denen er verantwortlich ist. Group by Verantwortlicher und oder by Verantwortlicher-nachname
	 */
	private List<Tuple> getAllVerantwortlicheGesuche() {
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<Tuple> query = builder.createTupleQuery();
		query.distinct(true);

		Root<Gesuch> root = query.from(Gesuch.class);

		final Join<Gesuch, Fall> fallJoin = root.join(Gesuch_.fall, JoinType.INNER);
		final Join<Fall, Benutzer> verantwortlicherJoin = fallJoin.join(Fall_.verantwortlicher, JoinType.LEFT);

		query.multiselect(verantwortlicherJoin.get(Benutzer_.id).alias(Benutzer_.id.getName()),
			verantwortlicherJoin.get(Benutzer_.nachname).alias(Benutzer_.nachname.getName()),
			verantwortlicherJoin.get(Benutzer_.vorname).alias(Benutzer_.vorname.getName()),
			builder.count(root).alias("allVerantwortlicheGesuche"));

		query.groupBy(verantwortlicherJoin.get(Benutzer_.id), verantwortlicherJoin.get(Benutzer_.nachname), verantwortlicherJoin.get(Benutzer_.vorname));
		query.orderBy(builder.asc(verantwortlicherJoin.get(Benutzer_.nachname)));

		Predicate isAdmin = builder.equal(verantwortlicherJoin.get(Benutzer_.role), UserRole.ADMIN);
		Predicate isSachbearbeiterJA = builder.equal(verantwortlicherJoin.get(Benutzer_.role), UserRole.SACHBEARBEITER_JA);
		Predicate orRoles = builder.or(isAdmin, isSachbearbeiterJA);

		query.where(orRoles);

		return persistence.getCriteriaResults(query);
	}

	/**
	 * Gibt eine tuple zurueck mit dem ID, dem Nachnamen und Vornamen des Benutzers und die Anzahl Gesuche
	 * die er im gegebenen Zeitraum verfuegt hat. Group by Verantwortlicher und oder by Verantwortlicher-nachname
	 */
	private List<Tuple> getAllVerfuegteGesuche(LocalDate datumVon, LocalDate datumBis) {
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<Tuple> query = builder.createTupleQuery();
		query.distinct(true);

		Root<AntragStatusHistory> root = query.from(AntragStatusHistory.class);
		final Join<AntragStatusHistory, Benutzer> benutzerJoin = root.join(AntragStatusHistory_.benutzer, JoinType.INNER);

		query.multiselect(
			benutzerJoin.get(Benutzer_.id).alias(Benutzer_.id.getName()),
			benutzerJoin.get(Benutzer_.nachname).alias(Benutzer_.nachname.getName()),
			benutzerJoin.get(Benutzer_.vorname).alias(Benutzer_.vorname.getName()),
			builder.count(root).alias("allVerfuegteGesuche"));

		// Status ist verfuegt
		Predicate predicateStatus = root.get(AntragStatusHistory_.status).in(AntragStatus.getAllVerfuegtStates());
		// Datum der Verfuegung muss nach (oder gleich) dem Anfang des Abfragezeitraums sein
		final Predicate predicateDatumVon = builder.greaterThanOrEqualTo(root.get(AntragStatusHistory_.timestampVon), datumVon.atStartOfDay());
		// Datum der Verfuegung muss vor (oder gleich) dem Ende des Abfragezeitraums sein
		final Predicate predicateDatumBis = builder.lessThanOrEqualTo(root.get(AntragStatusHistory_.timestampVon), datumBis.atStartOfDay());

		Predicate isAdmin = builder.equal(benutzerJoin.get(Benutzer_.role), UserRole.ADMIN);
		Predicate isSachbearbeiterJA = builder.equal(benutzerJoin.get(Benutzer_.role), UserRole.SACHBEARBEITER_JA);
		Predicate predOrRoles = builder.or(isAdmin, isSachbearbeiterJA);

		query.where(predicateStatus, predicateDatumVon, predicateDatumBis, predOrRoles);

		query.groupBy(benutzerJoin.get(Benutzer_.id), benutzerJoin.get(Benutzer_.nachname), benutzerJoin.get(Benutzer_.vorname));
		query.orderBy(builder.asc(benutzerJoin.get(Benutzer_.nachname)));

		return persistence.getCriteriaResults(query);
	}

	private List<MitarbeiterinnenDataRow> convertToMitarbeiterinnenDataRow(List<Tuple> numberVerantwortlicheGesuche, List<Tuple> numberVerfuegteGesuche) {
		final Map<String, MitarbeiterinnenDataRow> result = new HashMap<>();
		for (Tuple tupleVerant : numberVerantwortlicheGesuche) {
			MitarbeiterinnenDataRow row = createMitarbeiterinnenDataRow(tupleVerant,
				new BigDecimal((Long) tupleVerant.get("allVerantwortlicheGesuche")), BigDecimal.ZERO);
			result.put((String) tupleVerant.get(Benutzer_.id.getName()), row);
		}
		for (Tuple tupleVerfuegte : numberVerfuegteGesuche) {
			final BigDecimal numberVerfuegte = new BigDecimal((Long) tupleVerfuegte.get("allVerfuegteGesuche"));
			final MitarbeiterinnenDataRow existingRow = result.get((String) tupleVerfuegte.get(Benutzer_.id.getName()));
			if (existingRow != null) {
				existingRow.setVerfuegungenAusgestellt(numberVerfuegte);
			} else {
				MitarbeiterinnenDataRow row = createMitarbeiterinnenDataRow(tupleVerfuegte, BigDecimal.ZERO, numberVerfuegte);
				result.put((String) tupleVerfuegte.get(Benutzer_.id.getName()), row);
			}
		}
		return new ArrayList<>(result.values());
	}

	@Nonnull
	private MitarbeiterinnenDataRow createMitarbeiterinnenDataRow(Tuple tuple, BigDecimal numberVerant, BigDecimal numberVerfuegte) {
		MitarbeiterinnenDataRow row = new MitarbeiterinnenDataRow();
		row.setName((String) tuple.get(Benutzer_.nachname.getName()));
		row.setVorname((String) tuple.get(Benutzer_.vorname.getName()));
		row.setVerantwortlicheGesuche(numberVerant);
		row.setVerfuegungenAusgestellt(numberVerfuegte);
		return row;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR})
	public UploadFileInfo generateExcelReportMitarbeiterinnen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {
		validateDateParams(datumVon, datumBis);

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_MITARBEITERINNEN;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		List<MitarbeiterinnenDataRow> reportData = getReportMitarbeiterinnen(datumVon, datumBis);
		ExcelMergerDTO excelMergerDTO = mitarbeiterinnenExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), datumVon, datumBis);

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		mitarbeiterinnenExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
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
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR})
	public UploadFileInfo generateExcelReportZahlungAuftrag(String auftragId) throws ExcelMergeException {

		Zahlungsauftrag zahlungsauftrag = zahlungService.findZahlungsauftrag(auftragId)
			.orElseThrow(() -> new EbeguEntityNotFoundException("generateExcelReportZahlungAuftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, auftragId));

		return getUploadFileInfoZahlung(zahlungsauftrag.getZahlungen(), zahlungsauftrag.getFilename(), zahlungsauftrag.getBeschrieb(),
			zahlungsauftrag.getDatumGeneriert(), zahlungsauftrag.getDatumFaellig());
	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR})
	public UploadFileInfo generateExcelReportZahlung(String zahlungId) throws ExcelMergeException {

		List<Zahlung> reportData = new ArrayList<>();

		Zahlung zahlung = zahlungService.findZahlung(zahlungId)
			.orElseThrow(() -> new EbeguEntityNotFoundException("generateExcelReportZahlungAuftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, zahlungId));

		reportData.add(zahlung);

		return getUploadFileInfoZahlung(reportData, zahlung.getZahlungsauftrag().getFilename() + "_" + zahlung.getInstitutionStammdaten().getInstitution().getName(),
			zahlung.getZahlungsauftrag().getBeschrieb(), zahlung.getZahlungsauftrag().getDatumGeneriert(), zahlung.getZahlungsauftrag().getDatumFaellig());
	}

	private UploadFileInfo getUploadFileInfoZahlung(List<Zahlung> reportData, String excelFileName, String bezeichnung, LocalDateTime datumGeneriert, LocalDate datumFaellig) throws ExcelMergeException {
		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_ZAHLUNG_AUFTRAG;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Objects.requireNonNull(is, VORLAGE + reportVorlage.getTemplatePath() +NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		Collection<Institution> allowedInst = institutionService.getAllowedInstitutionenForCurrentBenutzer();

		ExcelMergerDTO excelMergerDTO = zahlungAuftragExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(),
			principalBean.discoverMostPrivilegedRole(), allowedInst, "Detailpositionen der Zahlung " + bezeichnung,
			datumGeneriert, datumFaellig);

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		zahlungAuftragExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			excelFileName + ".xlsx",
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, REVISOR})
	public UploadFileInfo generateExcelReportZahlungPeriode(@Nonnull String gesuchsperiodeId) throws ExcelMergeException {

		Gesuchsperiode gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId)
			.orElseThrow(() -> new EbeguEntityNotFoundException("generateExcelReportZahlungPeriode", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsperiodeId));

		final Collection<Zahlungsauftrag> zahlungsauftraegeInPeriode = zahlungService.getZahlungsauftraegeInPeriode(gesuchsperiode.getGueltigkeit().getGueltigAb(), gesuchsperiode.getGueltigkeit().getGueltigBis());

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_ZAHLUNG_AUFTRAG_PERIODE;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Objects.requireNonNull(is, VORLAGE + reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		final List<Zahlung> allZahlungen = zahlungsauftraegeInPeriode.stream()
			.flatMap(zahlungsauftrag -> zahlungsauftrag.getZahlungen().stream())
			.collect(Collectors.toList());

		ExcelMergerDTO excelMergerDTO = zahlungAuftragPeriodeExcelConverter.toExcelMergerDTO(allZahlungen, gesuchsperiode.getGesuchsperiodeString(), Locale.getDefault());

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		zahlungAuftragPeriodeExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	private List<GesuchstellerKinderBetreuungDataRow> getReportDataGesuchstellerKinderBetreuung(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable Gesuchsperiode gesuchsperiode) throws IOException, URISyntaxException {
		List<VerfuegungZeitabschnitt> zeitabschnittList = getReportDataBetreuungen(datumVon, datumBis, gesuchsperiode);
		List<GesuchstellerKinderBetreuungDataRow> dataRows = convertToGesuchstellerKinderBetreuungDataRow(zeitabschnittList);
		dataRows.sort(Comparator.comparing(GesuchstellerKinderBetreuungDataRow::getBgNummer).thenComparing(GesuchstellerKinderBetreuungDataRow::getZeitabschnittVon));
		return dataRows;
	}

	private List<GesuchstellerKinderBetreuungDataRow> getReportDataKinder(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable Gesuchsperiode gesuchsperiode) throws IOException, URISyntaxException {
		List<VerfuegungZeitabschnitt> zeitabschnittList = getReportDataBetreuungen(datumVon, datumBis, gesuchsperiode);
		List<GesuchstellerKinderBetreuungDataRow> dataRows = convertToKinderDataRow(zeitabschnittList);
		dataRows.sort(Comparator.comparing(GesuchstellerKinderBetreuungDataRow::getBgNummer).thenComparing(GesuchstellerKinderBetreuungDataRow::getZeitabschnittVon));
		return dataRows;
	}

	private List<GesuchstellerKinderBetreuungDataRow> getReportDataGesuchsteller(@Nonnull LocalDate stichtag) throws IOException, URISyntaxException {
		List<VerfuegungZeitabschnitt> zeitabschnittList = getReportDataBetreuungen(stichtag);
		List<GesuchstellerKinderBetreuungDataRow> dataRows = convertToGesuchstellerKinderBetreuungDataRow(zeitabschnittList);
		dataRows.sort(Comparator.comparing(GesuchstellerKinderBetreuungDataRow::getBgNummer).thenComparing(GesuchstellerKinderBetreuungDataRow::getZeitabschnittVon));
		return dataRows;
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private List<VerfuegungZeitabschnitt> getReportDataBetreuungen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable Gesuchsperiode gesuchsperiode) throws IOException, URISyntaxException {
		validateDateParams(datumVon, datumBis);

		// Alle Verfuegungszeitabschnitte zwischen datumVon und datumBis. Aber pro Fall immer nur das zuletzt verfuegte.
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = builder.createQuery(VerfuegungZeitabschnitt.class);
		query.distinct(true);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<VerfuegungZeitabschnitt, Verfuegung> joinVerfuegung = root.join(VerfuegungZeitabschnitt_.verfuegung);
		Join<Verfuegung, Betreuung> joinBetreuung = joinVerfuegung.join(Verfuegung_.betreuung);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		// startAbschnitt <= datumBis && endeAbschnitt >= datumVon
		Predicate predicateStart = builder.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), datumBis);
		predicatesToUse.add(predicateStart);
		Predicate predicateEnd = builder.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), datumVon);
		predicatesToUse.add(predicateEnd);
		// Gesuchsperiode
		if (gesuchsperiode != null) {
			Predicate predicateGesuchsperiode = builder.equal(root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.kind)
				.get(KindContainer_.gesuch).get(Gesuch_.gesuchsperiode), gesuchsperiode);
			predicatesToUse.add(predicateGesuchsperiode);
		}
		// Nur neueste Verfuegung jedes Falls beachten
		Predicate predicateGueltig = builder.equal(joinBetreuung.get(Betreuung_.gueltig), Boolean.TRUE);
		predicatesToUse.add(predicateGueltig);

		// Sichtbarkeit nach eingeloggtem Benutzer
		boolean isInstitutionsbenutzer = principalBean.isCallerInAnyOfRole(UserRole.SACHBEARBEITER_INSTITUTION, UserRole.SACHBEARBEITER_TRAEGERSCHAFT);
		if (isInstitutionsbenutzer) {
			Collection<Institution> allowedInstitutionen = institutionService.getAllowedInstitutionenForCurrentBenutzer();
			Predicate predicateAllowedInstitutionen = root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(allowedInstitutionen);
			predicatesToUse.add(predicateAllowedInstitutionen);
		}
		boolean isSchulamtBenutzer = principalBean.isCallerInAnyOfRole(UserRole.SCHULAMT);
		if (isSchulamtBenutzer) {
			Predicate predicateSchulamt = builder.equal(root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp) , BetreuungsangebotTyp.TAGESSCHULE);
			predicatesToUse.add(predicateSchulamt);
		} else {
			Predicate predicateNotSchulamt = builder.notEqual(root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp) , BetreuungsangebotTyp.TAGESSCHULE);
			predicatesToUse.add(predicateNotSchulamt);
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(builder, predicatesToUse));
		List<VerfuegungZeitabschnitt> zeitabschnittList = persistence.getCriteriaResults(query);
		return zeitabschnittList;
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private List<VerfuegungZeitabschnitt> getReportDataBetreuungen(@Nonnull LocalDate stichtag) throws IOException, URISyntaxException {
		Validate.notNull(stichtag, VALIDIERUNG_STICHTAG);

		// Alle Verfuegungszeitabschnitte zwischen datumVon und datumBis. Aber pro Fall immer nur das zuletzt verfuegte.
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = builder.createQuery(VerfuegungZeitabschnitt.class);
		query.distinct(true);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<VerfuegungZeitabschnitt, Verfuegung> joinVerfuegung = root.join(VerfuegungZeitabschnitt_.verfuegung);
		Join<Verfuegung, Betreuung> joinBetreuung = joinVerfuegung.join(Verfuegung_.betreuung);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		// Stichtag
		Predicate intervalPredicate = builder.between(builder.literal(stichtag),
			root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis));
		predicatesToUse.add(intervalPredicate);
		// Nur neueste Verfuegung jedes Falls beachten
		Predicate predicateGueltig = builder.equal(joinBetreuung.get(Betreuung_.gueltig), Boolean.TRUE);
		predicatesToUse.add(predicateGueltig);

		// Sichtbarkeit nach eingeloggtem Benutzer
		boolean isInstitutionsbenutzer = principalBean.isCallerInAnyOfRole(UserRole.SACHBEARBEITER_INSTITUTION, UserRole.SACHBEARBEITER_TRAEGERSCHAFT);
		if (isInstitutionsbenutzer) {
			Collection<Institution> allowedInstitutionen = institutionService.getAllowedInstitutionenForCurrentBenutzer();
			Predicate predicateAllowedInstitutionen = root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(allowedInstitutionen);
			predicatesToUse.add(predicateAllowedInstitutionen);
		}
		boolean isSchulamtBenutzer = principalBean.isCallerInAnyOfRole(UserRole.SCHULAMT);
		if (isSchulamtBenutzer) {
			Predicate predicateSchulamt = builder.equal(root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp) , BetreuungsangebotTyp.TAGESSCHULE);
			predicatesToUse.add(predicateSchulamt);
		} else {
			Predicate predicateNotSchulamt = builder.notEqual(root.get(VerfuegungZeitabschnitt_.verfuegung).get(Verfuegung_.betreuung).get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp) , BetreuungsangebotTyp.TAGESSCHULE);
			predicatesToUse.add(predicateNotSchulamt);
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(builder, predicatesToUse));
		return persistence.getCriteriaResults(query);
	}

	private void addStammdaten(GesuchstellerKinderBetreuungDataRow row, VerfuegungZeitabschnitt zeitabschnitt) {
		Gesuch gesuch = zeitabschnitt.getVerfuegung().getBetreuung().extractGesuch();
		row.setInstitution(zeitabschnitt.getVerfuegung().getBetreuung().getInstitutionStammdaten().getInstitution().getName());
		row.setBetreuungsTyp(zeitabschnitt.getVerfuegung().getBetreuung().getBetreuungsangebotTyp());
		row.setPeriode(gesuch.getGesuchsperiode().getGesuchsperiodeString());
		row.setEingangsdatum(gesuch.getEingangsdatum());
		for (AntragStatusHistory antragStatusHistory : gesuch.getAntragStatusHistories()) {
			if (AntragStatus.getAllVerfuegtStates().contains(antragStatusHistory.getStatus())) {
				row.setVerfuegungsdatum(antragStatusHistory.getTimestampVon().toLocalDate());
			}
		}
		row.setFallId(Integer.parseInt(""+gesuch.getFall().getFallNummer()));
		row.setBgNummer(zeitabschnitt.getVerfuegung().getBetreuung().getBGNummer());
	}

	private void addGesuchsteller1ToGesuchstellerKinderBetreuungDataRow(GesuchstellerKinderBetreuungDataRow row, GesuchstellerContainer containerGS1) {
		Gesuchsteller gs1 = containerGS1.getGesuchstellerJA();
		row.setGs1Name(gs1.getNachname());
		row.setGs1Vorname(gs1.getVorname());
		GesuchstellerAdresse gs1Adresse = containerGS1.getWohnadresseAm(row.getZeitabschnittVon());
		if (gs1Adresse != null) {
			row.setGs1Strasse(gs1Adresse.getStrasse());
			row.setGs1Hausnummer(gs1Adresse.getHausnummer());
			row.setGs1Zusatzzeile(gs1Adresse.getZusatzzeile());
			row.setGs1Plz(gs1Adresse.getPlz());
			row.setGs1Ort(gs1Adresse.getOrt());
		}
		row.setGs1EwkId(gs1.getEwkPersonId());
		row.setGs1Diplomatenstatus(gs1.isDiplomatenstatus());
		// EWP Gesuchsteller 1

		List<Erwerbspensum> erwerbspensenGS1 = containerGS1.getErwerbspensenAm(row.getZeitabschnittVon());
		for (Erwerbspensum erwerbspensumJA : erwerbspensenGS1) {
			if (Taetigkeit.ANGESTELLT.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs1EwpAngestellt(row.getGs1EwpAngestellt() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.AUSBILDUNG.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs1EwpAusbildung(row.getGs1EwpAusbildung() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.SELBSTAENDIG.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs1EwpSelbstaendig(row.getGs1EwpSelbstaendig() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.RAV.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs1EwpRav(row.getGs1EwpRav() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.GESUNDHEITLICHE_EINSCHRAENKUNGEN.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs1EwpGesundhtl(row.getGs1EwpGesundhtl() + erwerbspensumJA.getPensum());
			}
			if (erwerbspensumJA.getZuschlagZuErwerbspensum()) {
				row.setGs1EwpZuschlag(row.getGs1EwpZuschlag() + erwerbspensumJA.getZuschlagsprozent());
			}
		}
	}

	private void addGesuchsteller2ToGesuchstellerKinderBetreuungDataRow(GesuchstellerKinderBetreuungDataRow row, GesuchstellerContainer containerGS2) {
		Gesuchsteller gs2 = containerGS2.getGesuchstellerJA();
		row.setGs2Name(gs2.getNachname());
		row.setGs2Vorname(gs2.getVorname());
		GesuchstellerAdresse gs2Adresse = containerGS2.getWohnadresseAm(row.getZeitabschnittVon());
		if (gs2Adresse != null) {
			row.setGs2Strasse(gs2Adresse.getStrasse());
			row.setGs2Hausnummer(gs2Adresse.getHausnummer());
			row.setGs2Zusatzzeile(gs2Adresse.getZusatzzeile());
			row.setGs2Plz(gs2Adresse.getPlz());
			row.setGs2Ort(gs2Adresse.getOrt());
		}
		row.setGs2EwkId(gs2.getEwkPersonId());
		row.setGs2Diplomatenstatus(gs2.isDiplomatenstatus());
		// EWP Gesuchsteller 2
		List<Erwerbspensum> erwerbspensenGS2 = containerGS2.getErwerbspensenAm(row.getZeitabschnittVon());
		for (Erwerbspensum erwerbspensumJA : erwerbspensenGS2) {
			if (Taetigkeit.ANGESTELLT.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs2EwpAngestellt(row.getGs2EwpAngestellt() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.AUSBILDUNG.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs2EwpAusbildung(row.getGs2EwpAusbildung() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.SELBSTAENDIG.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs2EwpSelbstaendig(row.getGs2EwpSelbstaendig() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.RAV.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs2EwpRav(row.getGs2EwpRav() + erwerbspensumJA.getPensum());
			}
			if (Taetigkeit.GESUNDHEITLICHE_EINSCHRAENKUNGEN.equals(erwerbspensumJA.getTaetigkeit())) {
				row.setGs2EwpGesundhtl(row.getGs2EwpGesundhtl() + erwerbspensumJA.getPensum());
			}
			if (erwerbspensumJA.getZuschlagZuErwerbspensum()) {
				row.setGs2EwpZuschlag(row.getGs2EwpZuschlag() + erwerbspensumJA.getZuschlagsprozent());
			}
		}
	}

	private void addKindToGesuchstellerKinderBetreuungDataRow(GesuchstellerKinderBetreuungDataRow row, VerfuegungZeitabschnitt zeitabschnitt) {
		Kind kind = zeitabschnitt.getVerfuegung().getBetreuung().getKind().getKindJA();
		row.setKindName(kind.getNachname());
		row.setKindVorname(kind.getVorname());
		row.setKindGeburtsdatum(kind.getGeburtsdatum());
		row.setKindFachstelle(kind.getPensumFachstelle() != null);
		row.setKindErwBeduerfnisse(zeitabschnitt.getVerfuegung().getBetreuung().getErweiterteBeduerfnisse());
		row.setKindDeutsch(kind.getMutterspracheDeutsch());
		row.setKindEingeschult(kind.getEinschulung());
	}

	private void addBetreuungToGesuchstellerKinderBetreuungDataRow(GesuchstellerKinderBetreuungDataRow row, VerfuegungZeitabschnitt zeitabschnitt) {
		row.setZeitabschnittVon(zeitabschnitt.getGueltigkeit().getGueltigAb());
		row.setZeitabschnittBis(zeitabschnitt.getGueltigkeit().getGueltigBis());
		row.setBetreuungsPensum(MathUtil.DEFAULT.from(zeitabschnitt.getBetreuungspensum()));
		row.setAnspruchsPensum(MathUtil.DEFAULT.from(zeitabschnitt.getAnspruchberechtigtesPensum()));
		row.setBgPensum(MathUtil.DEFAULT.from(zeitabschnitt.getBgPensum()));
		row.setBgStunden(zeitabschnitt.getBetreuungsstunden());
		row.setVollkosten(zeitabschnitt.getVollkosten());
		row.setElternbeitrag(zeitabschnitt.getElternbeitrag());
		row.setVerguenstigt(zeitabschnitt.getVerguenstigung());
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR})
	public UploadFileInfo generateExcelReportGesuchstellerKinderBetreuung(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable String gesuchPeriodeId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {
		Validate.notNull(datumVon, VALIDIERUNG_DATUM_VON);
		Validate.notNull(datumBis, VALIDIERUNG_DATUM_BIS);

		final ReportVorlage reportResource = ReportVorlage.VORLAGE_REPORT_GESUCHSTELLER_KINDER_BETREUUNG;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportResource.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		Gesuchsperiode gesuchsperiode = null;
		if (gesuchPeriodeId != null) {
			Optional<Gesuchsperiode> gesuchsperiodeOptional = gesuchsperiodeService.findGesuchsperiode(gesuchPeriodeId);
			if (gesuchsperiodeOptional.isPresent()) {
				gesuchsperiode = gesuchsperiodeOptional.get();
			}
		}

		List<GesuchstellerKinderBetreuungDataRow> reportData = getReportDataGesuchstellerKinderBetreuung(datumVon, datumBis, gesuchsperiode);
		ExcelMergerDTO excelMergerDTO = gesuchstellerKinderBetreuungExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), datumVon, datumBis, gesuchsperiode);

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		gesuchstellerKinderBetreuungExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportResource.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	private List<GesuchstellerKinderBetreuungDataRow> convertToGesuchstellerKinderBetreuungDataRow(List<VerfuegungZeitabschnitt> zeitabschnittList) {
		List<GesuchstellerKinderBetreuungDataRow> dataRowList = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnittList) {
			GesuchstellerKinderBetreuungDataRow row = createRowForGesuchstellerKinderBetreuungReport(zeitabschnitt);
			dataRowList.add(row);
		}
		return dataRowList;
	}

	private GesuchstellerKinderBetreuungDataRow createRowForGesuchstellerKinderBetreuungReport(VerfuegungZeitabschnitt zeitabschnitt) {
		Gesuch gesuch = zeitabschnitt.getVerfuegung().getBetreuung().extractGesuch();

		GesuchstellerKinderBetreuungDataRow row = new GesuchstellerKinderBetreuungDataRow();
		// Betreuung
		addBetreuungToGesuchstellerKinderBetreuungDataRow(row, zeitabschnitt);
		// Stammdaten
		addStammdaten(row, zeitabschnitt);

		// Gesuchsteller 1: Prozent-Felder initialisieren, damit im Excel das Total sicher berechnet werden kann
		row.setGs1EwpAngestellt(0);
		row.setGs1EwpAusbildung(0);
		row.setGs1EwpSelbstaendig(0);
		row.setGs1EwpRav(0);
		row.setGs1EwpGesundhtl(0);
		row.setGs1EwpZuschlag(0);
		addGesuchsteller1ToGesuchstellerKinderBetreuungDataRow(row, gesuch.getGesuchsteller1());
		// Gesuchsteller 2: Prozent-Felder initialisieren, damit im Excel das Total sicher berechnet werden kann
		row.setGs2EwpAngestellt(0);
		row.setGs2EwpAusbildung(0);
		row.setGs2EwpSelbstaendig(0);
		row.setGs2EwpRav(0);
		row.setGs2EwpGesundhtl(0);
		row.setGs2EwpZuschlag(0);
		if (gesuch.getGesuchsteller2() != null) {
			addGesuchsteller2ToGesuchstellerKinderBetreuungDataRow(row, gesuch.getGesuchsteller2());
		}
		// Familiensituation / Einkommen
		Familiensituation familiensituation = gesuch.getFamiliensituationContainer().getFamiliensituationAm(row.getZeitabschnittVon());
		row.setFamiliensituation(familiensituation.getFamilienstatus());
		if (familiensituation.hasSecondGesuchsteller()) {
			row.setKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		} else {
			row.setKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		}
		row.setFamiliengroesse(zeitabschnitt.getFamGroesse());
		row.setMassgEinkVorFamilienabzug(zeitabschnitt.getMassgebendesEinkommenVorAbzFamgr());
		row.setFamilienabzug(zeitabschnitt.getAbzugFamGroesse());
		row.setMassgEink(zeitabschnitt.getMassgebendesEinkommen());
		row.setEinkommensjahr(zeitabschnitt.getEinkommensjahr());
		if (gesuch.getEinkommensverschlechterungInfoContainer() != null) {
			row.setEkvVorhanden(gesuch.getEinkommensverschlechterungInfoContainer().getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung());
		}
		row.setStvGeprueft(gesuch.isGeprueftSTV());
		row.setVeranlagt(gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().getSteuerveranlagungErhalten());
		// Kind
		addKindToGesuchstellerKinderBetreuungDataRow(row, zeitabschnitt);
		return row;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT})
	public UploadFileInfo generateExcelReportKinder(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable String gesuchPeriodeId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {
		Validate.notNull(datumVon, VALIDIERUNG_DATUM_VON);
		Validate.notNull(datumBis, VALIDIERUNG_DATUM_BIS);

		final ReportVorlage reportResource = ReportVorlage.VORLAGE_REPORT_KINDER;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportResource.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		Gesuchsperiode gesuchsperiode = null;
		if (gesuchPeriodeId != null) {
			Optional<Gesuchsperiode> gesuchsperiodeOptional = gesuchsperiodeService.findGesuchsperiode(gesuchPeriodeId);
			if (gesuchsperiodeOptional.isPresent()) {
				gesuchsperiode = gesuchsperiodeOptional.get();
			}
		}

		List<GesuchstellerKinderBetreuungDataRow> reportData = getReportDataKinder(datumVon, datumBis, gesuchsperiode);
		ExcelMergerDTO excelMergerDTO = gesuchstellerKinderBetreuungExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), datumVon, datumBis, gesuchsperiode);

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		gesuchstellerKinderBetreuungExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportResource.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	private List<GesuchstellerKinderBetreuungDataRow> convertToKinderDataRow(List<VerfuegungZeitabschnitt> zeitabschnittList) {
		List<GesuchstellerKinderBetreuungDataRow> dataRowList = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnittList) {
			GesuchstellerKinderBetreuungDataRow row = createRowForKinderReport(zeitabschnitt);
			dataRowList.add(row);
		}
		return dataRowList;
	}

	private GesuchstellerKinderBetreuungDataRow createRowForKinderReport(VerfuegungZeitabschnitt zeitabschnitt) {
		Gesuch gesuch = zeitabschnitt.getVerfuegung().getBetreuung().extractGesuch();

		GesuchstellerKinderBetreuungDataRow row = new GesuchstellerKinderBetreuungDataRow();
		addStammdaten(row, zeitabschnitt);

		// Gesuchsteller 1
		Gesuchsteller gs1 = gesuch.getGesuchsteller1().getGesuchstellerJA();
		row.setGs1Name(gs1.getNachname());
		row.setGs1Vorname(gs1.getVorname());
		// Gesuchsteller 2
		if (gesuch.getGesuchsteller2() != null) {
			Gesuchsteller gs2 = gesuch.getGesuchsteller2().getGesuchstellerJA();
			row.setGs2Name(gs2.getNachname());
			row.setGs2Vorname(gs2.getVorname());
		}
		// Kind
		addKindToGesuchstellerKinderBetreuungDataRow(row, zeitabschnitt);
		// Betreuung
		addBetreuungToGesuchstellerKinderBetreuungDataRow(row, zeitabschnitt);
		return row;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, REVISOR, SCHULAMT})
	public UploadFileInfo generateExcelReportGesuchsteller(@Nonnull LocalDate stichtag) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {
		Validate.notNull(stichtag, VALIDIERUNG_STICHTAG);

		final ReportVorlage reportResource = ReportVorlage.VORLAGE_REPORT_GESUCHSTELLER;

		InputStream is = ReportServiceBean.class.getResourceAsStream(reportResource.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportResource.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportResource.getDataSheetName());

		List<GesuchstellerKinderBetreuungDataRow> reportData = getReportDataGesuchsteller(stichtag);
		ExcelMergerDTO excelMergerDTO = gesuchstellerKinderBetreuungExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault(), stichtag);

		mergeData(sheet, excelMergerDTO, reportResource.getMergeFields());
		gesuchstellerKinderBetreuungExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportResource.getDefaultExportFilename(),
			TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	private void runStatisticsBetreuung() {
		List<Betreuung> allBetreuungen = betreuungService.getAllBetreuungenWithMissingStatistics();
		for (Betreuung betreuung : allBetreuungen) {
			if (betreuung.hasVorgaenger()) {
				Betreuung vorgaengerBetreuung = persistence.find(Betreuung.class, betreuung.getVorgaengerId());
				if (!betreuung.isSame(vorgaengerBetreuung, false, false)) {
					betreuung.setBetreuungMutiert(Boolean.TRUE);
					LOGGER.info("Betreuung hat geändert: " + betreuung.getId());
				} else {
					betreuung.setBetreuungMutiert(Boolean.FALSE);
					LOGGER.info("Betreuung hat nicht geändert: " + betreuung.getId());
				}
			} else {
				// Betreuung war auf dieser Mutation neu
				LOGGER.info("Betreuung ist neu: " + betreuung.getId());
				betreuung.setBetreuungMutiert(Boolean.TRUE);
			}
		}
	}

	private void runStatisticsAbwesenheiten() {
		List<Abwesenheit> allAbwesenheiten = betreuungService.getAllAbwesenheitenWithMissingStatistics();
		for (Abwesenheit abwesenheit : allAbwesenheiten) {
			Betreuung betreuung = abwesenheit.getAbwesenheitContainer().getBetreuung();
			if (abwesenheit.hasVorgaenger()) {
				Abwesenheit vorgaengerAbwesenheit = persistence.find(Abwesenheit.class, abwesenheit.getVorgaengerId());
				if (!abwesenheit.isSame(vorgaengerAbwesenheit)) {
					betreuung.setAbwesenheitMutiert(Boolean.TRUE);
					LOGGER.info("Abwesenheit hat geändert: " + abwesenheit.getId());
				} else {
					betreuung.setAbwesenheitMutiert(Boolean.FALSE);
					LOGGER.info("Abwesenheit hat nicht geändert: " + abwesenheit.getId());
				}
			} else {
				// Abwesenheit war auf dieser Mutation neu
				LOGGER.info("Abwesenheit ist neu: " + abwesenheit.getId());
				betreuung.setAbwesenheitMutiert(Boolean.TRUE);
			}
		}
	}

	private void runStatisticsKinder() {
		List<KindContainer> allKindContainer = kindService.getAllKinderWithMissingStatistics();
		for (KindContainer kindContainer : allKindContainer) {
			Kind kind = kindContainer.getKindJA();
			if (kind.hasVorgaenger()) {
				Kind vorgaengerKind = persistence.find(Kind.class, kind.getVorgaengerId());
				if (!kind.isSame(vorgaengerKind)) {
					kindContainer.setKindMutiert(Boolean.TRUE);
					LOGGER.info("Kind hat geändert: " + kindContainer.getId());
				} else {
					kindContainer.setKindMutiert(Boolean.FALSE);
					LOGGER.info("Kind hat nicht geändert: " + kindContainer.getId());
				}
			} else {
				// Kind war auf dieser Mutation neu
				LOGGER.info("Kind ist neu: " + kindContainer.getId());
				kindContainer.setKindMutiert(Boolean.TRUE);
			}
		}
	}
}
