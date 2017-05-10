package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.rechner.BGRechnerParameterDTO;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import ch.dvbern.lib.iso20022.AuszahlungDTO;
import ch.dvbern.lib.iso20022.Pain001DTO;
import ch.dvbern.lib.iso20022.Pain001Service;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer GeneratedDokument
 */
@Stateless
@Local(GeneratedDokumentService.class)
@PermitAll
public class GeneratedDokumentServiceBean extends AbstractBaseService implements GeneratedDokumentService {


	private static final Logger LOG = LoggerFactory.getLogger(GeneratedDokumentServiceBean.class.getSimpleName());

	@Inject
	private Persistence<GeneratedDokument> persistence;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private EbeguConfiguration ebeguConfiguration;

	@Inject
	private PDFService pdfService;

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private MandantService mandantService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private MahnungService mahnungService;

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private RulesService rulesService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private Pain001Service pain001Service;

	private static final String DEF_DEBTOR_NAME = "Direktion fuer Bildung, Soziales und Sport der Stadt Bern";
	private static final String DEF_DEBTOR_BIC = "POFICHBEXXX";
	private static final String DEF_DEBTOR_IBAN = "CH3309000000300008233";


	@Override
	@Nonnull
	public WriteProtectedDokument saveDokument(@Nonnull WriteProtectedDokument dokument) {
		Objects.requireNonNull(dokument);
		return persistence.merge(dokument);
	}

	@Override
	@Nullable
	public GeneratedDokument findGeneratedDokument(String gesuchId, String filename, String path) {

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<GeneratedDokument> query = cb.createQuery(GeneratedDokument.class);
		Root<GeneratedDokument> root = query.from(GeneratedDokument.class);

		path = path.replace("\\", "\\\\"); //dirty fix fuer windows pfad mit backslash

		Predicate predGesuch = cb.equal(root.get(GeneratedDokument_.gesuch).get(Gesuch_.id), gesuchId);
		Predicate predFileName = cb.equal(root.get(GeneratedDokument_.filename), filename);
		Predicate predPath = cb.like(root.get(GeneratedDokument_.filepfad), path + "%");

		query.where(predGesuch, predFileName, predPath);
		return persistence.getCriteriaSingleResult(query);
	}

	@Override
	@Nullable
	public Pain001Dokument findPain001Dokument(String zahlungsauftragId, String filename, String path) {

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Pain001Dokument> query = cb.createQuery(Pain001Dokument.class);
		Root<Pain001Dokument> root = query.from(Pain001Dokument.class);

		path = path.replace("\\", "\\\\"); //dirty fix fuer windows pfad mit backslash

		Predicate predZahlungsauftrag = cb.equal(root.get(Pain001Dokument_.zahlungsauftrag).get(Zahlungsauftrag_.id), zahlungsauftragId);
		Predicate predFileName = cb.equal(root.get(Pain001Dokument_.filename), filename);
		Predicate predPath = cb.like(root.get(Pain001Dokument_.filepfad), path + "%");

		query.where(predZahlungsauftrag, predFileName, predPath);
		return persistence.getCriteriaSingleResult(query);
	}

	/**
	 * Sucht ein WriteProtectedDokument mit demselben Namen und Pfad und vom selben Gesuch. Wen das Dokument existiert, wird dieses gelöscht
	 * und mit dem Neuen ersetzt. Wenn es nicht existiert, ein neues wird erstellt.
	 */
	@Nonnull
	@Override
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST")
	public WriteProtectedDokument saveGeneratedDokumentInDB(byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, AbstractEntity entity, String fileName, boolean writeProtected) throws MimeTypeParseException {

		WriteProtectedDokument writeProtectedDokument;
		String filePathToRemove = null;
		if (entity instanceof Gesuch) {
			writeProtectedDokument = this.findGeneratedDokument(entity.getId(),
				fileName, ebeguConfiguration.getDocumentFilePath() + "/" + entity.getId());
			if (writeProtectedDokument == null) {
				writeProtectedDokument = new GeneratedDokument();
			} else {
				//Die Datei wird am Ende geloscht, um unvollstaenige Daten zu vermeiden falls was kaputt geht
				filePathToRemove = writeProtectedDokument.getFilepfad();
			}
			((GeneratedDokument) writeProtectedDokument).setGesuch((Gesuch) entity);
		} else { // case of pain001
			writeProtectedDokument = this.findPain001Dokument(entity.getId(),
				fileName, ebeguConfiguration.getDocumentFilePath() + "/" + entity.getId());
			if (writeProtectedDokument == null) {
				writeProtectedDokument = new Pain001Dokument();
			} else {
				//Die Datei wird am Ende geloscht, um unvollstaenige Daten zu vermeiden falls was kaputt geht
				filePathToRemove = writeProtectedDokument.getFilepfad();
			}
			Pain001Dokument.class.cast(writeProtectedDokument).setZahlungsauftrag((Zahlungsauftrag) entity);
		}

		final UploadFileInfo savedDokument = fileSaverService.save(data,
			fileName, entity.getId());

		writeProtectedDokument.setFilename(savedDokument.getFilename());
		writeProtectedDokument.setFilepfad(savedDokument.getPath());
		writeProtectedDokument.setFilesize(savedDokument.getSizeString());
		writeProtectedDokument.setTyp(dokumentTyp);
		writeProtectedDokument.setWriteProtected(writeProtected);

		WriteProtectedDokument returnDocument = this.saveDokument(writeProtectedDokument);

		if (filePathToRemove != null) {
			fileSaverService.remove(filePathToRemove);
		}

		return returnDocument;
	}


	@Override
	public GeneratedDokument getFinSitDokumentAccessTokenGeneratedDokument(final Gesuch gesuch,
																		   Boolean forceCreation) throws MimeTypeParseException, MergeDocException {

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.FINANZIELLE_SITUATION, gesuch.getJahrAndFallnummer());
		GeneratedDokument persistedDokument = null;
		if (!forceCreation && gesuch.getStatus().isAnyStatusOfVerfuegtOrVefuegen()) {
			persistedDokument = (GeneratedDokument) getWriteProtectedDokument(gesuch.getId(), GeneratedDokumentTyp.FINANZIELLE_SITUATION, fileNameForGeneratedDokumentTyp);
		}
		if (!gesuch.getStatus().isAnyStatusOfVerfuegtOrVefuegen() || persistedDokument == null) {
			//  persistedDokument == null:  Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen
			authorizer.checkReadAuthorizationFinSit(gesuch);
			finanzielleSituationService.calculateFinanzDaten(gesuch);

			final BetreuungsgutscheinEvaluator evaluator = initEvaluator(gesuch);
			final Verfuegung famGroessenVerfuegung = evaluator.evaluateFamiliensituation(gesuch);
			boolean writeProtectPDF = gesuch.getStatus().isAnyStatusOfVerfuegt() || gesuch.getStatus().equals(AntragStatus.VERFUEGEN);
			byte[] data = pdfService.generateFinanzielleSituation(gesuch, famGroessenVerfuegung, writeProtectPDF);
			// FINANZIELLE_SITUATION in einem Zustand isAnyStatusOfVerfuegt oder Verfügen, soll das Dokument schreibgeschützt sein!
			persistedDokument = (GeneratedDokument) saveGeneratedDokumentInDB(data, GeneratedDokumentTyp.FINANZIELLE_SITUATION, gesuch,
				fileNameForGeneratedDokumentTyp,
				writeProtectPDF);


		}
		return persistedDokument;
	}


	@Override
	public GeneratedDokument getBegleitschreibenDokument(final Gesuch gesuch,
														 Boolean forceCreation) throws MimeTypeParseException, MergeDocException {
		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.BEGLEITSCHREIBEN, gesuch.getJahrAndFallnummer());
		GeneratedDokument persistedDokument = null;
		if (!forceCreation && gesuch.getStatus().isAnyStatusOfVerfuegtOrVefuegen()) {
			persistedDokument = (GeneratedDokument) getWriteProtectedDokument(gesuch.getId(), GeneratedDokumentTyp.BEGLEITSCHREIBEN, fileNameForGeneratedDokumentTyp);
		}
		if (!gesuch.getStatus().isAnyStatusOfVerfuegtOrVefuegen() || persistedDokument == null) {
			//  persistedDokument == null:  Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen

			boolean writeProtectPDF = gesuch.getStatus().isAnyStatusOfVerfuegt();
			byte[] data = pdfService.generateBegleitschreiben(gesuch, writeProtectPDF);
			// BEGLEITSCHREIBEN in einem Zustand isAnyStatusOfVerfuegt oder Verfügen, soll das Dokument schreibgeschützt sein!
			persistedDokument = (GeneratedDokument) saveGeneratedDokumentInDB(data, GeneratedDokumentTyp.BEGLEITSCHREIBEN, gesuch,
				fileNameForGeneratedDokumentTyp, writeProtectPDF);

		}
		return persistedDokument;
	}


	@Override
	public GeneratedDokument getFreigabequittungAccessTokenGeneratedDokument(final Gesuch gesuch,
																			 Boolean forceCreation, Zustelladresse zustelladresse) throws MimeTypeParseException, MergeDocException {

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.FREIGABEQUITTUNG, gesuch.getJahrAndFallnummer());

		GeneratedDokument persistedDokument = null;

		if (!forceCreation) {
			persistedDokument = (GeneratedDokument) getWriteProtectedDokument(gesuch.getId(), GeneratedDokumentTyp.FREIGABEQUITTUNG, fileNameForGeneratedDokumentTyp);
		}

		if (persistedDokument == null || forceCreation) {

			authorizer.checkReadAuthorizationFinSit(gesuch);

			if (!gesuch.getStatus().inBearbeitung() && !forceCreation) {
				LOG.error(GeneratedDokumentTyp.FREIGABEQUITTUNG.name() + " für Gesuch " + gesuch.getJahrAndFallnummer() + " nicht gefunden.");
			}

			gesuchService.antragFreigabequittungErstellen(gesuch, AntragStatus.FREIGABEQUITTUNG);
			byte[] data = pdfService.generateFreigabequittung(gesuch, zustelladresse, true);

			// Freigabequittung soll wird nur einmal produziert und soll deswegen immer schreibgeschützt sein!
			persistedDokument = (GeneratedDokument) saveGeneratedDokumentInDB(data, GeneratedDokumentTyp.FREIGABEQUITTUNG, gesuch,
				fileNameForGeneratedDokumentTyp, true);
		}

		return persistedDokument;
	}

	@Nullable
	private WriteProtectedDokument getWriteProtectedDokument(String id, GeneratedDokumentTyp dokumentTyp, String fileNameForGeneratedDokumentTyp) {

		String expectedFilepath = ebeguConfiguration.getDocumentFilePath() + "/" + id;

		final WriteProtectedDokument persistedDokument;
		if (!dokumentTyp.equals(GeneratedDokumentTyp.PAIN001)) {
			persistedDokument = findGeneratedDokument(id, fileNameForGeneratedDokumentTyp, expectedFilepath);
		} else {
			persistedDokument = findPain001Dokument(id, fileNameForGeneratedDokumentTyp, expectedFilepath);
		}

		if (persistedDokument == null) {
			LOG.error("Das Dokument vom Typ: {} fuer Antragnummer {} konnte unter dem Pfad {} " +
				"nicht gefunden  werden obwohl es existieren muesste. Wird neu generiert!", dokumentTyp, expectedFilepath);
		}

		if (persistedDokument != null && !Files.exists(Paths.get(persistedDokument.getFilepfad()))) {
			LOG.error("Die Datei {} könnte nicht gefunden werdern!", persistedDokument.getFilepfad());
			return null;
		}

		return persistedDokument;
	}

	@Nonnull
	public BetreuungsgutscheinEvaluator initEvaluator(@Nonnull Gesuch gesuch) {
		Mandant mandant = mandantService.getFirst();   //gesuch get mandant?
		List<Rule> rules = rulesService.getRulesForGesuchsperiode(mandant, gesuch.getGesuchsperiode());
		Boolean enableDebugOutput = applicationPropertyService.findApplicationPropertyAsBoolean(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, true);
		BetreuungsgutscheinEvaluator bgEvaluator = new BetreuungsgutscheinEvaluator(rules, enableDebugOutput);
		loadCalculatorParameters(mandant, gesuch.getGesuchsperiode());
		return bgEvaluator;
	}


	private BGRechnerParameterDTO loadCalculatorParameters(Mandant mandant, @Nonnull Gesuchsperiode gesuchsperiode) {
		Map<EbeguParameterKey, EbeguParameter> paramMap = ebeguParameterService.getEbeguParameterByGesuchsperiodeAsMap(gesuchsperiode);
		BGRechnerParameterDTO parameterDTO = new BGRechnerParameterDTO(paramMap, gesuchsperiode, mandant);

		//Es gibt aktuell einen Parameter der sich aendert am Jahreswechsel
//		int startjahr = gesuchsperiode.getGueltigkeit().getGueltigAb().getYear();
//		int endjahr = gesuchsperiode.getGueltigkeit().getGueltigBis().getYear();
//		Validate.isTrue(endjahr == startjahr +1, "Startjahr " + startjahr + " muss ein Jahr vor Endjahr"+ endjahr +" sein ");
//		BigDecimal abgeltungJahr1 = loadYearlyParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, startjahr);
//		BigDecimal abgeltungJahr2 = loadYearlyParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, endjahr);
//		parameterDTO.setBeitragStadtProTagJahr1((abgeltungJahr1));
//		parameterDTO.setBeitragStadtProTagJahr2((abgeltungJahr2));
		return parameterDTO;
	}

	@Override
	public GeneratedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(final Gesuch gesuch, Betreuung betreuung, String manuelleBemerkungen,
																			   Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException {

		GeneratedDokument persistedDokument = null;

		if (!forceCreation && Betreuungsstatus.VERFUEGT.equals(betreuung.getBetreuungsstatus())) {

			String expectedFilepath = ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId();
			String bgNummer = betreuung.getBGNummer();
			persistedDokument = findGeneratedDokument(gesuch.getId(),
				DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.VERFUEGUNG,
					bgNummer), expectedFilepath);
			if (persistedDokument == null) {
				LOG.warn("Das Dokument vom Typ: {} fuer Betreuungsnummer {} konnte unter dem Pfad {} " +
					"nicht gefunden  werden obwohl es existieren muesste. Wird neu generiert!", GeneratedDokumentTyp.VERFUEGUNG.name(), bgNummer, expectedFilepath);
			}
		}
		// Wenn die Betreuung nicht verfuegt ist oder das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es erstellen
		// (Der Status wird auf Verfuegt gesetzt, BEVOR das Dokument erstellt wird!)
		if (!Betreuungsstatus.VERFUEGT.equals(betreuung.getBetreuungsstatus()) || persistedDokument == null) {
			finanzielleSituationService.calculateFinanzDaten(gesuch);
			final Gesuch gesuchWithVerfuegung = verfuegungService.calculateVerfuegung(gesuch);

			Betreuung matchedBetreuung = gesuchWithVerfuegung.extractBetreuungById(betreuung.getId());
			if (matchedBetreuung != null) {
				if (!manuelleBemerkungen.isEmpty()) {
					matchedBetreuung.getVerfuegung().setManuelleBemerkungen(manuelleBemerkungen);
				}

				final byte[] verfuegungsPDF;
				Optional<LocalDate> optVorherigeVerfuegungDate = verfuegungService.findVorgaengerVerfuegungDate(betreuung);
				LocalDate letztesVerfDatum = optVorherigeVerfuegungDate.orElse(null);
				boolean writeProtectPDF = Betreuungsstatus.VERFUEGT.equals(betreuung.getBetreuungsstatus());
				verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(matchedBetreuung, letztesVerfDatum, writeProtectPDF);


				final String fileNameForDocTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.VERFUEGUNG,
					matchedBetreuung.getBGNummer());

				// Wenn die Betreuung im Zustand Verfügt ist, soll das Dokument als schreibgeschützt gespeichert werden.
				persistedDokument = (GeneratedDokument) saveGeneratedDokumentInDB(verfuegungsPDF, GeneratedDokumentTyp.VERFUEGUNG,
					gesuch, fileNameForDocTyp, writeProtectPDF);
			} else {
				throw new EbeguEntityNotFoundException("getVerfuegungDokumentAccessTokenGeneratedDokument",
					ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "Betreuung not found: " + betreuung.getId());
			}
		}
		return persistedDokument;
	}

	@Override
	public GeneratedDokument getMahnungDokumentAccessTokenGeneratedDokument(Mahnung mahnung, Boolean generateFinalVersion) throws MimeTypeParseException, IOException, MergeDocException {

		Gesuch gesuch = mahnung.getGesuch();
		GeneratedDokument persistedDokument = null;


		Mahnung mahnungDB = persistence.find(Mahnung.class, mahnung.getId());

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.MAHNUNG,
			mahnungDB == null ? "ENTWURF" : Constants.FILENAME_DATE_TIME_FORMATTER.format(mahnungDB.getTimestampErstellt()));


		if (mahnungDB != null) {
			persistedDokument = findGeneratedDokument(gesuch.getId(), fileNameForGeneratedDokumentTyp,
				ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());
		}


		if (persistedDokument == null || generateFinalVersion) {

			// generateFinalVersion only true, when button "Mahnung erstellen" is pressed. Therefore we can use as trigger for write protection
			boolean writeProtectPDF = generateFinalVersion;

			GeneratedDokumentTyp dokumentTyp = GeneratedDokumentTyp.MAHNUNG;

			Optional<Mahnung> vorgaengerMahnung;

			if (mahnung.hasVorgaenger()) {
				vorgaengerMahnung = mahnungService.findMahnung(mahnung.getVorgaengerId());
			} else {
				vorgaengerMahnung = mahnungService.findAktiveErstMahnung(gesuch);
			}

			byte[] data = pdfService.generateMahnung(mahnung, vorgaengerMahnung, writeProtectPDF);

			persistedDokument = (GeneratedDokument) saveGeneratedDokumentInDB(data, dokumentTyp, gesuch,
				fileNameForGeneratedDokumentTyp, writeProtectPDF);
		}
		return persistedDokument;

	}

	@Override
	public GeneratedDokument getNichteintretenDokumentAccessTokenGeneratedDokument(Betreuung betreuung, Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException {

		Gesuch gesuch = betreuung.extractGesuch();
		GeneratedDokumentTyp dokumentTyp = GeneratedDokumentTyp.NICHTEINTRETEN;

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(dokumentTyp, betreuung.getBGNummer());

		GeneratedDokument persistedDokument = null;

		if (!forceCreation && Betreuungsstatus.NICHT_EINGETRETEN.equals(betreuung.getBetreuungsstatus())) {
			persistedDokument = (GeneratedDokument) getWriteProtectedDokument(gesuch.getId(), dokumentTyp, fileNameForGeneratedDokumentTyp);
		}

		if (!Betreuungsstatus.NICHT_EINGETRETEN.equals(betreuung.getBetreuungsstatus()) || persistedDokument == null) {

			// persistedDokument == null:  Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen

			boolean writeProtectPDF = forceCreation || gesuch.getStatus().isAnyStatusOfVerfuegt();
			byte[] data = pdfService.generateNichteintreten(betreuung, writeProtectPDF);

			// Wenn in einem Zustand isAnyStatusOfVerfuegt, soll das Dokument schreibgeschützt sein!
			persistedDokument = (GeneratedDokument) saveGeneratedDokumentInDB(data, dokumentTyp, gesuch,
				fileNameForGeneratedDokumentTyp, writeProtectPDF);
		}
		return persistedDokument;

	}

	@Override
	public Pain001Dokument getPain001DokumentAccessTokenGeneratedDokument(Zahlungsauftrag zahlungsauftrag, Boolean forceCreation) throws MimeTypeParseException {


		Pain001Dokument persistedDokument = null;

		GeneratedDokumentTyp dokumentTyp = GeneratedDokumentTyp.PAIN001;

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(dokumentTyp, zahlungsauftrag.getFilename());

		if (!forceCreation && !ZahlungauftragStatus.ENTWURF.equals(zahlungsauftrag.getStatus())) {
			persistedDokument = (Pain001Dokument) getWriteProtectedDokument(zahlungsauftrag.getId(), dokumentTyp, fileNameForGeneratedDokumentTyp);
		}

		if (ZahlungauftragStatus.ENTWURF.equals(zahlungsauftrag.getStatus()) || persistedDokument == null) {

			// persistedDokument == null:  Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen

			boolean writeProtectPDF = forceCreation || !ZahlungauftragStatus.ENTWURF.equals(zahlungsauftrag.getStatus());

			byte[] data = pain001Service.getPainFileContent(wrapZahlungsauftrag(zahlungsauftrag));

			// Wenn nicht Entwurf, soll das Dokument schreibgeschützt sein!
			persistedDokument = (Pain001Dokument) saveGeneratedDokumentInDB(data, dokumentTyp, zahlungsauftrag,
				fileNameForGeneratedDokumentTyp, writeProtectPDF);
		}
		return persistedDokument;

	}

	private Pain001DTO wrapZahlungsauftrag(Zahlungsauftrag zahlungsauftrag) {
		Pain001DTO pain001DTO = new Pain001DTO();

		pain001DTO.setAuszahlungsDatum(zahlungsauftrag.getDatumFaellig());
		pain001DTO.setAuszahlungsDatum(LocalDate.now());

		String debtor_name = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_NAME);
		String debtor_bic = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_BIC);
		String debtor_iban = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_IBAN);
		String debtor_iban_gebuehren = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_IBAN_GEBUEHREN);

		pain001DTO.setSchuldnerName(debtor_name == null ? DEF_DEBTOR_NAME : debtor_name);
		pain001DTO.setSchuldnerIBAN(debtor_iban == null ? DEF_DEBTOR_IBAN : debtor_iban);
		pain001DTO.setSchuldnerBIC(debtor_bic == null ? DEF_DEBTOR_BIC : debtor_bic);
		pain001DTO.setSchuldnerIBAN_gebuehren(debtor_iban_gebuehren == null ? pain001DTO.getSchuldnerIBAN() : debtor_iban_gebuehren);
		pain001DTO.setSoftwareName("Ki-Tax");

		pain001DTO.setAuszahlungen(new ArrayList<>());
		zahlungsauftrag.getZahlungen().stream()
			.filter(zahlung -> zahlung.getBetragTotalZahlung().signum() == 1)
			.forEach(zahlung -> {
				AuszahlungDTO auszahlungDTO = new AuszahlungDTO();
				auszahlungDTO.setBetragTotalZahlung(zahlung.getBetragTotalZahlung());
				auszahlungDTO.setZahlungsempfaegerName(zahlung.getInstitutionStammdaten().getInstitution().getName());
				auszahlungDTO.setZahlungsempfaegerStrasse(zahlung.getInstitutionStammdaten().getAdresse().getStrasse());
				auszahlungDTO.setZahlungsempfaegerHausnummer(zahlung.getInstitutionStammdaten().getAdresse().getHausnummer());
				auszahlungDTO.setZahlungsempfaegerPlz(zahlung.getInstitutionStammdaten().getAdresse().getPlz());
				auszahlungDTO.setZahlungsempfaegerOrt(zahlung.getInstitutionStammdaten().getAdresse().getOrt());
				auszahlungDTO.setZahlungsempfaegerLand(zahlung.getInstitutionStammdaten().getAdresse().getLand().toString());
				auszahlungDTO.setZahlungsempfaegerIBAN(zahlung.getInstitutionStammdaten().getIban().toString());
				auszahlungDTO.setZahlungsempfaegerBankClearingNumber(zahlung.getInstitutionStammdaten().getIban().extractClearingNumberWithoutLeadingZeros());

				pain001DTO.getAuszahlungen().add(auszahlungDTO);
			});


		return pain001DTO;
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public void removeAllGeneratedDokumenteFromGesuch(@Nonnull Gesuch gesuch) {
		LOG.info("Searching GeneratedDokuments of Gesuch: " + gesuch.getFall().getFallNummer() + " / " + gesuch.getGesuchsperiode().getGesuchsperiodeString());
		Collection<GeneratedDokument> genDokFromGesuch = findGeneratedDokumentsFromGesuch(gesuch);
		for (GeneratedDokument generatedDokument : genDokFromGesuch) {
			LOG.info("Deleting Dokument: " + generatedDokument.getId());
			persistence.remove(GeneratedDokument.class, generatedDokument.getId());
		}
	}

	@Override
	public Collection<GeneratedDokument> findGeneratedDokumentsFromGesuch(Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		this.authorizer.checkReadAuthorization(gesuch);
		return criteriaQueryHelper.getEntitiesByAttribute(GeneratedDokument.class, gesuch, GeneratedDokument_.gesuch);
	}
}
