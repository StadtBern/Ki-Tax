package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.util.UploadFileInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


	private static final Logger LOG = LoggerFactory.getLogger(FileSaverServiceBean.class);


	@Inject
	private EbeguConfiguration ebeguConfiguration;


	@Override
	public boolean save(UploadFileInfo uploadFileInfo, String gesuchId) {
		Validate.notNull(uploadFileInfo);
		Validate.notNull(uploadFileInfo.getFilename());

		UUID uuid = UUID.randomUUID();

		final String absoluteFilePath = ebeguConfiguration.getDocumentFilePath() + "/" + gesuchId + "/" + uuid + "_" + uploadFileInfo.getFilename();
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

}
