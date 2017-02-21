package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.IOException;
import java.util.Collection;

/**
 * Service zum Verwalten von GeneratedDokumenten
 */
public interface GeneratedDokumentService {

	/**
	 * Erstellt ein neues GeneratedDokument wenn es noch nicht existiert und sonst aktualisiert das Bestehende
	 *
	 * @param dokument
	 * @return
	 */
	@Nonnull
	WriteProtectedDokument saveDokument(@Nonnull WriteProtectedDokument dokument);

	@Nullable
	GeneratedDokument findGeneratedDokument(String gesuchId, String filename, String path);

	Pain001Dokument findPain001Dokument(String zahlungsauftragId, String filename, String path);

	@Nonnull
	WriteProtectedDokument saveGeneratedDokumentInDB(byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, AbstractEntity entity, String fileName, boolean writeProtected) throws MimeTypeParseException;

	GeneratedDokument getFinSitDokumentAccessTokenGeneratedDokument(Gesuch gesuch,
																	Boolean forceCreation) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getBegleitschreibenDokument(Gesuch gesuch,
												  Boolean forceCreation) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getFreigabequittungAccessTokenGeneratedDokument(Gesuch gesuch,
																	  Boolean forceCreation, Zustelladresse zustelladresse) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(Gesuch gesuch, Betreuung betreuung, String manuelleBemerkungen,
																		Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException;

	GeneratedDokument getMahnungDokumentAccessTokenGeneratedDokument(Mahnung mahnung,
																	 Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException;

	GeneratedDokument getNichteintretenDokumentAccessTokenGeneratedDokument(Betreuung betreuung,
																			Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException;

	Pain001Dokument getPain001DokumentAccessTokenGeneratedDokument(Zahlungsauftrag zahlungsauftrag, Boolean forceCreation) throws MimeTypeParseException;

	void removeAllGeneratedDokumenteFromGesuch(Gesuch gesuch);

	Collection<GeneratedDokument> findGeneratedDokumentsFromGesuch(Gesuch gesuch);
}
