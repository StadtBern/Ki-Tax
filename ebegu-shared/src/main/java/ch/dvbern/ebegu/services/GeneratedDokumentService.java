package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.errors.MergeDocException;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Service zum Verwalten von GeneratedDokumenten
 */
public interface GeneratedDokumentService {

	/**
	 * Erstellt ein neues GeneratedDokument wenn es noch nicht existiert und sonst aktualisiert das Bestehende
	 * @param dokument
	 * @return
	 */
	@Nonnull
	GeneratedDokument saveGeneratedDokument(@Nonnull GeneratedDokument dokument);

	@Nullable
	GeneratedDokument findGeneratedDokument(String gesuchId, String filename, String path);

	@Nonnull
	GeneratedDokument updateGeneratedDokument(byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, Gesuch gesuch, String fileName) throws MimeTypeParseException;

	GeneratedDokument getDokumentAccessTokenGeneratedDokument(Gesuch gesuch, GeneratedDokumentTyp dokumentTyp,
															  Boolean forceCreation) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(Gesuch gesuch, Betreuung betreuung, String manuelleBemerkungen,
																		Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException;
}
