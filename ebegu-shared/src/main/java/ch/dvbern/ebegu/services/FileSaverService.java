package ch.dvbern.ebegu.services;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.FileMetadata;
import ch.dvbern.ebegu.util.UploadFileInfo;

/**
 * Service zum Speichern von Files auf dem File-System
 * Kann ev. durch modshape ersetzt werden
 */
public interface FileSaverService {

	void save(UploadFileInfo uploadFileInfo, String folderName);

	boolean remove(String dokumentPaths);

	@Nonnull
	UploadFileInfo save(byte[] bytes, String fileName, String folderName) throws MimeTypeParseException;

	@Nonnull
	UploadFileInfo save(byte[] bytes, String fileName, String folderName, MimeType contentType);

	boolean copy(FileMetadata fileToCopy, String folderName);

	boolean removeAllFromSubfolder(@Nonnull String subfolder);
}
