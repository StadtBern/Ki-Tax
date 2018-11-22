/*
 * Copyright (C) 2018 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.mocks;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.MimeType;
import javax.annotation.Nonnull;

import ch.dvbern.ebegu.services.FileSaverServiceBean;
import ch.dvbern.ebegu.util.UploadFileInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSaverServiceMock extends FileSaverServiceBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSaverServiceMock.class);

	@Override
	public void save(UploadFileInfo uploadFileInfo, String folderName) {
		saveToTempDir(uploadFileInfo.getBytes(), uploadFileInfo.getFilename());
	}

	@Nonnull
	@Override
	public UploadFileInfo save(byte[] bytes, String fileName, String folderName) {
		saveToTempDir(bytes, fileName);
		return new UploadFileInfo(fileName, null);
	}

	@Nonnull
	@Override
	public UploadFileInfo save(byte[] bytes, String fileName, String folderName, MimeType contentType) {
		saveToTempDir(bytes, fileName);
		UploadFileInfo uploadFileInfo = new UploadFileInfo(fileName, null);
		uploadFileInfo.setBytes(bytes);
		return uploadFileInfo;
	}

	private void saveToTempDir(byte[] content, String filenameInklExtension) {
		String fileWithPath = FileUtils.getTempDirectoryPath() + '/' + filenameInklExtension;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fileWithPath);
			fileOutputStream.write(content);
			fileOutputStream.flush();
			fileOutputStream.close();
			LOGGER.info("Saved File to {}", fileWithPath);
		} catch (IOException e) {
			LOGGER.error("File konnte nicht gespeichert werden: {}", fileWithPath, e);
		}
	}
}
