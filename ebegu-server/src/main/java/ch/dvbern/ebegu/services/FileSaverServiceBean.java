package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.FileMetadata;
import ch.dvbern.ebegu.util.UploadFileInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@SuppressFBWarnings({"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
@Stateless
@Local(FileSaverService.class)
public class FileSaverServiceBean implements FileSaverService {


	private static final Logger LOG = LoggerFactory.getLogger(FileSaverServiceBean.class.getSimpleName());


	@Inject
	private EbeguConfiguration ebeguConfiguration;


	@Override
	public boolean save(UploadFileInfo uploadFileInfo, String folderName) {
		Validate.notNull(uploadFileInfo);
		Validate.notNull(uploadFileInfo.getFilename());

		UUID uuid = UUID.randomUUID();

		String ending = getFileNameEnding(uploadFileInfo.getFilename());

		// Wir speichern der Name des Files nicht im FS. Kann sonst Probleme mit Umlauten geben
		final String absoluteFilePath = ebeguConfiguration.getDocumentFilePath() + "/" + folderName + "/" + uuid + "." + ending;
		uploadFileInfo.setPath(absoluteFilePath);

		Path file = Paths.get(absoluteFilePath);
		try {

			if (!Files.exists(file.getParent())) {
				Files.createDirectories(file.getParent());
				LOG.info("Save file in FileSystem: " + absoluteFilePath);
			}
			uploadFileInfo.setSize(Files.size(Files.write(file, uploadFileInfo.getBytes())));

		} catch (IOException e) {
			LOG.info("Can't save file in FileSystem: " + uploadFileInfo.getFilename(), e);
			return false;
		}
		return true;
	}

	@Override
	public UploadFileInfo save(byte[] bytes, String fileName, String folderName) throws MimeTypeParseException {
		MimeType contentType = new MimeType("application/pdf");
		return save(bytes, fileName, folderName, contentType);
	}

	@Nullable
	@Override
	public UploadFileInfo save(byte[] bytes, String fileName, String folderName, MimeType contentType) {
		final UploadFileInfo uploadFileInfo = new UploadFileInfo(fileName, contentType);
		uploadFileInfo.setBytes(bytes);
		if (save(uploadFileInfo, folderName)){
			return uploadFileInfo;
		}
		return null;
	}

	@Override
	public boolean copy(FileMetadata fileToCopy, String folderName) {
		Validate.notNull(fileToCopy);
		Validate.notNull(folderName);

		Path oldfile = Paths.get(fileToCopy.getFilepfad());

		UUID uuid = UUID.randomUUID();

		String ending = getFileNameEnding(fileToCopy.getFilename());

		// Wir speichern der Name des Files nicht im FS. Kann sonst Probleme mit Umlauten geben
		final String absoluteFilePath = ebeguConfiguration.getDocumentFilePath() + "/" + folderName + "/" + uuid + "." + ending;
		fileToCopy.setFilepfad(absoluteFilePath);

		Path newfile = Paths.get(absoluteFilePath);

		try {

			if (!Files.exists(newfile.getParent())) {
				Files.createDirectories(newfile.getParent());
				LOG.info("Save file in FileSystem: " + absoluteFilePath);
			}
			Files.copy(oldfile, newfile);

		} catch (IOException e) {
			LOG.info("Can't save file in FileSystem: " + fileToCopy.getFilename(), e);
			return false;
		}
		return true;
	}

	private String getFileNameEnding(String filename) {

		String extension = "";
		int i = filename.lastIndexOf('.');
		if (i > 0) {
			extension = filename.substring(i + 1);
		}
		return extension;
	}

	@Override
	public boolean remove(String dokumentPaths) {
		final Path path = Paths.get(dokumentPaths);

		try {
			if (Files.exists(path)) {
				Files.delete(path);

				LOG.info("Delete file in FileSystem: " + dokumentPaths);
			}
		} catch (IOException e) {
			LOG.info("Can't remove file in FileSystem: " + dokumentPaths, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean removeAll(@Nonnull String gesuchId) {
		final String absoluteFilePath = ebeguConfiguration.getDocumentFilePath() + "/" + gesuchId + "/";
		Path file = Paths.get(absoluteFilePath);
		try {
			if (Files.exists(file) && Files.isDirectory(file)) {
				FileUtils.cleanDirectory(file.toFile());
				Files.deleteIfExists(file);
				LOG.info("Deleting directory : " + absoluteFilePath);
			}
			return true;
		} catch (IOException e) {
			LOG.info("Can't delete directory: " + absoluteFilePath, e);
			return false;
		}
	}
}
