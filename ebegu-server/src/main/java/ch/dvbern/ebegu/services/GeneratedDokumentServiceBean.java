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
import org.apache.commons.lang.StringUtils;
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


	private static final Logger LOG = LoggerFactory.getLogger(GeneratedDokumentServiceBean.class);

	@Inject
	private Persistence<GeneratedDokument> persistence;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private EbeguConfiguration ebeguConfiguration;

	@Inject
	private PrintBegleitschreibenPDFService printBegleitschreibenPDFService;

	@Inject
	private PrintVerfuegungPDFService verfuegungsGenerierungPDFService;

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


	@Override
	@Nonnull
	public GeneratedDokument saveGeneratedDokument(@Nonnull GeneratedDokument dokument) {
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

	/**
	 * Sucht ein GeneratedDokument mit demselben Namen und Pfad und vom selben Gesuch. Wen das Dokument existiert, wird dieses gelöscht
	 * und mit dem Neuen ersetzt. Wenn es nicht existiert, ein neues wird erstellt.
	 *
	 * @param dokumentTyp
	 * @param gesuch
	 * @param fileName
	 * @return
	 */
	@Nonnull
	@Override
	public GeneratedDokument updateGeneratedDokument(byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, Gesuch gesuch, String fileName) throws MimeTypeParseException {

		GeneratedDokument generatedDokument = this.findGeneratedDokument(gesuch.getId(),
			fileName, ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());

		return updateGeneratedDokument(generatedDokument, data, dokumentTyp, gesuch, fileName);
	}

	private GeneratedDokument updateGeneratedDokument(GeneratedDokument generatedDokument, byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, Gesuch gesuch, String fileName) throws MimeTypeParseException {

		final UploadFileInfo savedDokument = fileSaverService.save(data,
			fileName, gesuch.getId());

		String filePathToRemove = null;

		if (generatedDokument == null) {
			generatedDokument = new GeneratedDokument();
		} else {
			//Die Datei wird am Ende geloscht, um unvollstaenige Daten zu vermeiden falls was kaputt geht
			filePathToRemove = generatedDokument.getFilepfad();
		}
		generatedDokument.setFilename(savedDokument.getFilename());
		generatedDokument.setFilepfad(savedDokument.getPath());
		generatedDokument.setFilesize(savedDokument.getSizeString());
		generatedDokument.setTyp(dokumentTyp);
		generatedDokument.setGesuch(gesuch);

		if (filePathToRemove != null) {
			fileSaverService.remove(filePathToRemove);
		}
		return this.saveGeneratedDokument(generatedDokument);
	}

	@Override
	public GeneratedDokument getDokumentAccessTokenGeneratedDokument(final Gesuch gesuch, final GeneratedDokumentTyp dokumentTyp,
																	 Boolean forceCreation) throws MimeTypeParseException, MergeDocException {
		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(dokumentTyp, gesuch.getAntragNummer());
		GeneratedDokument persistedDokument = null;
		if (!forceCreation && gesuch.getStatus().isAnyStatusOfVerfuegt() || AntragStatus.VERFUEGEN.equals(gesuch.getStatus())) {
			persistedDokument = getGeneratedDokument(gesuch, dokumentTyp, fileNameForGeneratedDokumentTyp);
		}
		if ((!gesuch.getStatus().isAnyStatusOfVerfuegt() && !AntragStatus.VERFUEGEN.equals(gesuch.getStatus()))
			|| persistedDokument == null) {
			// Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen
			authorizer.checkReadAuthorizationFinSit(gesuch);
			finanzielleSituationService.calculateFinanzDaten(gesuch);

			byte[] data;
			if (GeneratedDokumentTyp.FINANZIELLE_SITUATION.equals(dokumentTyp)) {
				final BetreuungsgutscheinEvaluator evaluator = initEvaluator(gesuch);
				final Verfuegung famGroessenVerfuegung = evaluator.evaluateFamiliensituation(gesuch);
				data = pdfService.generateFinanzielleSituation(gesuch, famGroessenVerfuegung);
			} else if (GeneratedDokumentTyp.BEGLEITSCHREIBEN.equals(dokumentTyp)) {
				data = printBegleitschreibenPDFService.printBegleitschreiben(gesuch);
			} else {
				LOG.warn("Unerwarter Dokumenttyp " + dokumentTyp.name() + " erwarte FinanzielleSituation oder Begleitschreiben");
				return null;
			}

			persistedDokument = updateGeneratedDokument(data, dokumentTyp, gesuch,
				fileNameForGeneratedDokumentTyp);
		}
		return persistedDokument;
	}

	@Override
	public GeneratedDokument getFreigabequittungAccessTokenGeneratedDokument(final Gesuch gesuch,
																			 Boolean forceCreation, Zustelladresse zustelladresse) throws MimeTypeParseException, MergeDocException {

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.FREIGABEQUITTUNG, gesuch.getAntragNummer());

		GeneratedDokument persistedDokument = getGeneratedDokument(gesuch, GeneratedDokumentTyp.FREIGABEQUITTUNG, fileNameForGeneratedDokumentTyp);

		if (persistedDokument == null || forceCreation) {

			authorizer.checkReadAuthorizationFinSit(gesuch);

			if (!gesuch.getStatus().inBearbeitung() && persistedDokument == null) {
				LOG.warn(GeneratedDokumentTyp.FREIGABEQUITTUNG.name() + " für Gesuch " + gesuch.getAntragNummer() + " nicht gefunden.");
			}

			gesuchService.antragFreigabequittungErstellen(gesuch, AntragStatus.FREIGABEQUITTUNG);
			byte[] data = pdfService.generateFreigabequittung(gesuch, zustelladresse);

			persistedDokument = updateGeneratedDokument(data, GeneratedDokumentTyp.FREIGABEQUITTUNG, gesuch,
				fileNameForGeneratedDokumentTyp);
		}

		return persistedDokument;
	}

	@Nullable
	private GeneratedDokument getGeneratedDokument(Gesuch gesuch, GeneratedDokumentTyp dokumentTyp, String fileNameForGeneratedDokumentTyp) {

		String expectedFilepath = ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId();

		final GeneratedDokument persistedDokument = findGeneratedDokument(gesuch.getId(), fileNameForGeneratedDokumentTyp,
			expectedFilepath);

		if (persistedDokument == null) {
			LOG.warn("Das Dokument vom Typ: {} fuer Antragnummer {} konnte unter dem Pfad {} " +
				"nicht gefunden  werden obwohl es existieren muesste. Wird neu generiert!", dokumentTyp, gesuch.getAntragNummer(), expectedFilepath);
		}

		if (persistedDokument != null && !Files.exists(Paths.get(persistedDokument.getFilepfad()))) {
			LOG.warn("Die Datei {} könnte nicht gefunden werdern!", persistedDokument.getFilepfad());
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
				verfuegungsPDF = verfuegungsGenerierungPDFService.printVerfuegungForBetreuung(matchedBetreuung, letztesVerfDatum);


				final String fileNameForDocTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.VERFUEGUNG,
					matchedBetreuung.getBGNummer());

				persistedDokument = updateGeneratedDokument(verfuegungsPDF, GeneratedDokumentTyp.VERFUEGUNG,
					gesuch, fileNameForDocTyp);
			} else {
				throw new EbeguEntityNotFoundException("getVerfuegungDokumentAccessTokenGeneratedDokument",
					ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "Betreuung not found: " + betreuung.getId());
			}
		}
		return persistedDokument;
	}

	@Override
	public GeneratedDokument getMahnungDokumentAccessTokenGeneratedDokument(Mahnung mahnung, Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException {

		Gesuch gesuch = mahnung.getGesuch();
		Mahnung mahnungDB = persistence.find(Mahnung.class, mahnung.getId());
		GeneratedDokumentTyp dokumentTyp = mahnungDB == null ? GeneratedDokumentTyp.MAHNUNG_VORSCHAU : GeneratedDokumentTyp.MAHNUNG;

		final String previewNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.MAHNUNG_VORSCHAU, StringUtils.EMPTY);

		final String fileNameForGeneratedDokumentTyp = mahnungDB == null ?
			previewNameForGeneratedDokumentTyp :
			DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.MAHNUNG,
				Constants.FILENAME_DATE_TIME_FORMATTER.format(mahnungDB.getTimestampErstellt()));

		//überprufen ob ein Vorschau existiert
		GeneratedDokument vorschauDokument = findGeneratedDokument(gesuch.getId(), previewNameForGeneratedDokumentTyp,
			ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());

		//überprufen ob die Mahnung existiert
		GeneratedDokument persistedDokument = findGeneratedDokument(gesuch.getId(), fileNameForGeneratedDokumentTyp,
			ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());

		// Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen
		if (persistedDokument == null || dokumentTyp == GeneratedDokumentTyp.MAHNUNG_VORSCHAU || forceCreation) {

			Optional<Mahnung> vorgaengerMahnung = Optional.empty();

			if (mahnung.hasVorgaenger()) {
				vorgaengerMahnung = mahnungService.findMahnung(mahnung.getVorgaengerId());
			} else if (mahnung.getMahnungTyp() == MahnungTyp.ZWEITE_MAHNUNG && dokumentTyp == GeneratedDokumentTyp.MAHNUNG_VORSCHAU) {
				vorgaengerMahnung = mahnungService.
					findAktiveErstMahnung(gesuch);

			}

			byte[] data = pdfService.generateMahnung(mahnung, vorgaengerMahnung);

			persistedDokument = vorschauDokument == null ?
				updateGeneratedDokument(data, dokumentTyp, gesuch,
					fileNameForGeneratedDokumentTyp) :
				updateGeneratedDokument(vorschauDokument, data, dokumentTyp, gesuch,
					fileNameForGeneratedDokumentTyp);

		}
		return persistedDokument;

	}

	@Override
	public GeneratedDokument getNichteintretenDokumentAccessTokenGeneratedDokument(Betreuung betreuung, Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException {

		Gesuch gesuch = betreuung.extractGesuch();
		GeneratedDokumentTyp dokumentTyp = GeneratedDokumentTyp.NICHTEINTRETEN;

		final String fileNameForGeneratedDokumentTyp = DokumenteUtil.getFileNameForGeneratedDokumentTyp(dokumentTyp, betreuung.getBGNummer());

		GeneratedDokument persistedDokument = null;

		if (!forceCreation && gesuch.getStatus().isAnyStatusOfVerfuegt() || AntragStatus.VERFUEGEN.equals(gesuch.getStatus())) {
			persistedDokument = findGeneratedDokument(gesuch.getId(), fileNameForGeneratedDokumentTyp,
				ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());
		}

		if ((!gesuch.getStatus().isAnyStatusOfVerfuegt() && !AntragStatus.VERFUEGEN.equals(gesuch.getStatus()))
			|| persistedDokument == null) {
			// Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen

			byte[] data = pdfService.generateNichteintreten(betreuung);

			persistedDokument = updateGeneratedDokument(data, dokumentTyp, gesuch,
				fileNameForGeneratedDokumentTyp);
		}
		return persistedDokument;

	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public void removeAllGeneratedDokumenteFromGesuch(Gesuch gesuch) {
		Collection<GeneratedDokument> genDokFromGesuch = findGeneratedDokumentsFromGesuch(gesuch);
		for (GeneratedDokument generatedDokument : genDokFromGesuch) {
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
