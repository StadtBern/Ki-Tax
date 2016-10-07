package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;

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
import java.util.Objects;

/**
 * Service fuer GeneratedDokument
 */
@Stateless
@Local(GeneratedDokumentService.class)
public class GeneratedDokumentServiceBean extends AbstractBaseService implements GeneratedDokumentService {

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
			persistedDokument = findGeneratedDokument(gesuch.getId(), fileNameForGeneratedDokumentTyp,
				ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());
		}
		if ((!AntragStatus.VERFUEGT.equals(gesuch.getStatus()) && !AntragStatus.VERFUEGEN.equals(gesuch.getStatus()))
			|| persistedDokument == null) {
			// Wenn das Dokument nicht geladen werden konnte, heisst es dass es nicht existiert und wir muessen es trotzdem erstellen
			finanzielleSituationService.calculateFinanzDaten(gesuch);

			byte[] data;
			if (GeneratedDokumentTyp.FINANZIELLE_SITUATION.equals(dokumentTyp)) {
				data = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch);
			} else if (GeneratedDokumentTyp.BEGLEITSCHREIBEN.equals(dokumentTyp)) {
				data = printBegleitschreibenPDFService.printBegleitschreiben(gesuch);
			} else {
				return null;
			}

			persistedDokument = updateGeneratedDokument(data, dokumentTyp, gesuch,
				fileNameForGeneratedDokumentTyp);
		}
		return persistedDokument;
	}

	@Override
	public GeneratedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(final Gesuch gesuch, Betreuung betreuung, String manuelleBemerkungen,
																			   Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException {

		GeneratedDokument persistedDokument = null;

		if (!forceCreation && Betreuungsstatus.VERFUEGT.equals(betreuung.getBetreuungsstatus())) {
			persistedDokument = findGeneratedDokument(gesuch.getId(),
				DokumenteUtil.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.VERFUEGUNG,
					betreuung.getBGNummer()), ebeguConfiguration.getDocumentFilePath() + "/" + gesuch.getId());
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

				final byte[] verfuegungsPDF = verfuegungsGenerierungPDFService.printVerfuegungForBetreuung(matchedBetreuung);

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
