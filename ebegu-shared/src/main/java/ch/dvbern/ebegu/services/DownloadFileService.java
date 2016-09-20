package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DownloadFileService {

	DownloadFile create(@Nonnull File file, String ip);

	@Nullable
	DownloadFile getDownloadFileByAccessToken(@Nonnull String accessToken);

	void cleanUp();
}
