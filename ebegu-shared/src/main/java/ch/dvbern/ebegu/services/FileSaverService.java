package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.util.UploadFileInfo;

/**
 * Service zum Speichern von Files auf dem File-System
 * Kann ev. durch modshape ersetzt werden
 */
public interface FileSaverService {

	boolean save(UploadFileInfo uploadFileInfo, String GesuchId);

	boolean remove(String dokumentPaths);
}
