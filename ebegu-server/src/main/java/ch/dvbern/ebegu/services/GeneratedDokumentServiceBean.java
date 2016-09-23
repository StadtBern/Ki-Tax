package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.GeneratedDokument_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
}
