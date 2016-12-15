package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.File;
import ch.dvbern.ebegu.util.UploadFileInfo;

import javax.activation.MimeTypeParseException;

/**
 * Service zum Speichern von Files auf dem File-System
 * Kann ev. durch modshape ersetzt werden
 */
public interface FileSaverService {

	boolean save(UploadFileInfo uploadFileInfo, String GesuchId);

	boolean remove(String dokumentPaths);

	UploadFileInfo save(byte[] bytes, String fileName, String folderName) throws MimeTypeParseException;

	boolean copy(File fileToCopy, String folderName);
}
