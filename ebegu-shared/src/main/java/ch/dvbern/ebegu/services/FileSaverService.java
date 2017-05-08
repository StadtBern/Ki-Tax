package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.FileMetadata;
import ch.dvbern.ebegu.util.UploadFileInfo;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service zum Speichern von Files auf dem File-System
 * Kann ev. durch modshape ersetzt werden
 */
public interface FileSaverService {

	boolean save(UploadFileInfo uploadFileInfo, String GesuchId);

	boolean remove(String dokumentPaths);

	UploadFileInfo save(byte[] bytes, String fileName, String folderName) throws MimeTypeParseException;

	@Nullable
	UploadFileInfo save(byte[] bytes, String fileName, String folderName, MimeType contentType);

	boolean copy(FileMetadata fileToCopy, String folderName);

	boolean removeAll(@Nonnull String gesuchId);
}
