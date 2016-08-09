package ch.dvbern.ebegu.services;

import javax.ejb.Local;
import javax.ejb.Stateless;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service fuer Institution
 */
@Stateless
@Local(FileSaverService.class)
public class FileSaverServiceBean implements FileSaverService {

	private static final String path = "/tmp/ebegu/";

	@Override
	public String save(byte[] bytes, String fileName, String gesuchId) {

		final String absulutFilePath = path + gesuchId + "/" + fileName;
		Path file = Paths.get(absulutFilePath);
		try {

			if (!Files.exists(file.getParent())) {
				Files.createDirectories(file.getParent());
			}

			Files.write(file, bytes);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return absulutFilePath;
	}

}
