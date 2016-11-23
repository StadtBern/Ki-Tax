package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.rechner.BGRechnerParameterDTO;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer GeneratedDokument
 */
@Stateless
@Local(GeneratedDokumentService.class)
public class GeneratedDokumentServiceBean extends AbstractBaseService implements GeneratedDokumentService {


	private static final Logger LOG = LoggerFactory.getLogger(GeneratedDokumentServiceBean.class);

	@Inject
	private Persistence<GeneratedDokument> persistence;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private PrintFinanzielleSituationPDFService printFinanzielleSituationPDFService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private EbeguConfiguration ebeguConfiguration;

	@Inject
	private PrintBegleitschreibenPDFService printBegleitschreibenPDFService;

	@Inject
	private PrintVerfuegungPDFService verfuegungsGenerierungPDFService;

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private MandantService mandantService;

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private RulesService rulesService;


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
		Predicate predGesuch = cb.equal(root.get(GeneratedDokument_.gesuch).get(Gesuch_.id), gesuchId);
		Predicate predFileName = cb.equal(root.get(GeneratedDokument_.filename), filename);
		Predicate predPath = cb.like(root.get(GeneratedDokument_.filepfad), path + "%");

		query.where(predGesuch, predFileName, predPath);
		return persistence.getCriteriaSingleResult(query);
	}

	/**
	 * Sucht ein GeneratedDokument mit demselben Namen und Pfad und vom selben Gesuch. Wen das Dokument existiert, wird dieses gelöscht
	 * und mit dem Neuen ersetzt. Wenn es nicht existiert, ein neues wird erstellt.
	 * @param dokumentTyp
	 * @param gesuch
	 * @param fileName
	 * @return
	 */
	@Nonnull
	@Override
	public GeneratedDokument updateGeneratedDokument(byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, Gesuch gesuch, String fileName) throws MimeTypeParseException {
		final UploadFileInfo savedDokument = fileSaverService.save(data,
			fileName, gesuch.getId());
		String filePathToRemove = null;

		GeneratedDokument generatedDokument = this.findGeneratedDokument(gesuch.getId(),
			savedDokument.getFilename(), savedDokument.getPathWithoutFileName());
		if (generatedDokument == null) {
			generatedDokument = new GeneratedDokument();
		}
		else {
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
		if (!forceCreation && AntragStatus.VERFUEGT.equals(gesuch.getStatus()) || AntragStatus.VERFUEGEN.equals(gesuch.getStatus())) {
			String expectedFilepath = ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId();
			persistedDokument = findGeneratedDokument(gesuch.getId(), fileNameForGeneratedDokumentTyp,
				expectedFilepath);
			if (persistedDokument == null) {
				LOG.warn("Das Dokument vom Typ: {} fuer Antragnummer {} konnte unter dem Pfad {} " +
					"nicht gefunden  werden obwohl es existieren muesste. Wird neu generiert!", dokumentTyp, gesuch.getAntragNummer(), expectedFilepath);
			}
		}
		if ((!AntragStatus.VERFUEGT.equals(gesuch.getStatus()) && !AntragStatus.VERFUEGEN.equals(gesuch.getStatus()))
			|| persistedDokument == null) {
			// Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen
			finanzielleSituationService.calculateFinanzDaten(gesuch);

			byte[] data;
			if (GeneratedDokumentTyp.FINANZIELLE_SITUATION.equals(dokumentTyp)) {
				final BetreuungsgutscheinEvaluator evaluator = initEvaluator(gesuch);
				final Verfuegung famGroessenVerfuegung = evaluator.evaluateFamiliensituation(gesuch);
				data = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch, famGroessenVerfuegung);
			} else if (GeneratedDokumentTyp.BEGLEITSCHREIBEN.equals(dokumentTyp)) {
				data = printBegleitschreibenPDFService.printBegleitschreiben(gesuch);
			} else {
				LOG.warn("Unerwarter Dokumenttyp " +dokumentTyp.name() + " erwarte FinanzielleSituation oder Begleitschreiben");
				return null;
			}

			persistedDokument = updateGeneratedDokument(data, dokumentTyp, gesuch,
				fileNameForGeneratedDokumentTyp);
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
				verfuegungsPDF = verfuegungsGenerierungPDFService.printVerfuegungForBetreuung(matchedBetreuung, letztesVerfDatum );


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
}
