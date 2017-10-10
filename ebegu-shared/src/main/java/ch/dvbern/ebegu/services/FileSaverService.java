/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
