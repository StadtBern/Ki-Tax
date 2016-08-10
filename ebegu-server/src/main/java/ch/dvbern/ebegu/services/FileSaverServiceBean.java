package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.util.UploadFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Local;
import javax.ejb.Stateless;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Stateless
@Local(FileSaverService.class)
public class FileSaverServiceBean implements FileSaverService {


	private static final Logger LOG = LoggerFactory.getLogger(FileSaverServiceBean.class);


	// Todo: Path muss in JBoss oder DB gespeichert werden!!!!!
	private static final String path = "/tmp/ebegu/";


	@Override
	public boolean save(UploadFileInfo uploadFileInfo, String gesuchId) {

		final String absulutFilePath = path + gesuchId + "/" + uploadFileInfo.getFilename();
		uploadFileInfo.setPath(absulutFilePath);

		Path file = Paths.get(absulutFilePath);
		try {
			if (!Files.exists(file.getParent())) {
				Files.createDirectories(file.getParent());
			}
			uploadFileInfo.setSize(Files.size(Files.write(file, uploadFileInfo.getBytes())));

		} catch (IOException e) {
			LOG.error("Can't save file in FileSystem: " + uploadFileInfo.getFilename(), e);
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
			}
		} catch (IOException e) {
			LOG.error("Can't remove file in FileSystem: " + dokumentPaths, e);
			return false;
		}
		return true;
	}

}
