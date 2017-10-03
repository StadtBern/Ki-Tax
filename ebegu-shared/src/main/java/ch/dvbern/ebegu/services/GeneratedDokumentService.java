package ch.dvbern.ebegu.services;

import java.io.IOException;
import java.util.Collection;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Pain001Dokument;
import ch.dvbern.ebegu.entities.WriteProtectedDokument;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;

/**
 * Service zum Verwalten von GeneratedDokumenten
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public interface GeneratedDokumentService {

	/**
	 * Erstellt ein neues GeneratedDokument wenn es noch nicht existiert und sonst aktualisiert das Bestehende
	 */
	@Nonnull
	WriteProtectedDokument saveDokument(@Nonnull WriteProtectedDokument dokument);

	@Nullable
	WriteProtectedDokument findGeneratedDokument(String gesuchId, String filename);

	Pain001Dokument findPain001Dokument(String zahlungsauftragId, String filename);

	@Nonnull
	WriteProtectedDokument saveGeneratedDokumentInDB(byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, AbstractEntity entity, String fileName, boolean writeProtected) throws MimeTypeParseException;

	WriteProtectedDokument getFinSitDokumentAccessTokenGeneratedDokument(Gesuch gesuch,
		Boolean forceCreation) throws MimeTypeParseException, MergeDocException;

	WriteProtectedDokument getBegleitschreibenDokument(Gesuch gesuch) throws MimeTypeParseException, MergeDocException;

	WriteProtectedDokument getFreigabequittungAccessTokenGeneratedDokument(Gesuch gesuch,
		Boolean forceCreation, Zustelladresse zustelladresse) throws MimeTypeParseException, MergeDocException;

	WriteProtectedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(Gesuch gesuch, Betreuung betreuung, String manuelleBemerkungen,
		Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException;

	WriteProtectedDokument getMahnungDokumentAccessTokenGeneratedDokument(Mahnung mahnung,
		Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException;

	WriteProtectedDokument getNichteintretenDokumentAccessTokenGeneratedDokument(Betreuung betreuung,
		Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException;

	WriteProtectedDokument getPain001DokumentAccessTokenGeneratedDokument(Zahlungsauftrag zahlungsauftrag, Boolean forceCreation) throws MimeTypeParseException;

	void removeAllGeneratedDokumenteFromGesuch(Gesuch gesuch);

	Collection<GeneratedDokument> findGeneratedDokumentsFromGesuch(Gesuch gesuch);
}
