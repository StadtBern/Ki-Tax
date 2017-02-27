package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DownloadFileService {

	DownloadFile create(@Nonnull FileMetadata fileMetadata, String ip);

	@Nullable
	DownloadFile getDownloadFileByAccessToken(@Nonnull String accessToken);

	void cleanUp();
}
