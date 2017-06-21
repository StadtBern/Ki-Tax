package ch.dvbern.ebegu.services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.FileMetadata;

public interface DownloadFileService {

	/**
	 * Erstellt ein neues DownloadFile
	 */
	@Nonnull
	DownloadFile create(@Nonnull FileMetadata fileMetadata, String ip);

	/**
	 * Sucht ein Download File aufgrund eines AccessTokens
	 */
	@Nullable
	DownloadFile getDownloadFileByAccessToken(@Nonnull String accessToken);

	/**
	 * Loescht alle DownloadFiles, deren AccessToken abgelaufen ist
	 */
	void cleanUp();
}
