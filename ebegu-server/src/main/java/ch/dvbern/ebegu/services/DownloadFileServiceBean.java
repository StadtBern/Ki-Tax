package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.DownloadFile_;
import ch.dvbern.ebegu.entities.FileMetadata;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Stateless
@Local(DownloadFileService.class)
public class DownloadFileServiceBean implements DownloadFileService {


	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileServiceBean.class);

	@Inject
	private Persistence<DownloadFile> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Override
	public DownloadFile create(@Nonnull FileMetadata fileMetadata, String ip) {
		Objects.requireNonNull(fileMetadata);
		Objects.requireNonNull(ip);

		return persistence.persist(new DownloadFile(fileMetadata, ip));
	}

	@Nullable
	@Override
	public DownloadFile getDownloadFileByAccessToken(@Nonnull String accessToken) {
		Objects.requireNonNull(accessToken);

		Optional<DownloadFile> tempDokumentOptional = criteriaQueryHelper.getEntityByUniqueAttribute(DownloadFile.class, accessToken, DownloadFile_.accessToken);

		if (!tempDokumentOptional.isPresent()) {
			return null;
		}

		DownloadFile downloadFile = tempDokumentOptional.get();

		if (isFileDownloadExpired(downloadFile)) {
			return null;
		}

		return downloadFile;

	}

	@Override
	public void cleanUp() {
		LocalDateTime deleteOlderThan = LocalDateTime.now().minus(Constants.MAX_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES);
		LOG.debug("Deleting TempDocuments before {}", deleteOlderThan);

		try {
			Objects.requireNonNull(deleteOlderThan);
			criteriaQueryHelper.deleteAllBefore(DownloadFile.class, deleteOlderThan);
		} catch (RuntimeException rte) {
			// timer methods may not throw exceptions or the timer will get cancelled (as per spec)
			String msg = "Unexpected error while deleting old TempDocuments";
			LOG.error(msg, rte);
		}

	}

	private boolean isFileDownloadExpired(@Nonnull DownloadFile tempBlob) {
		LocalDateTime timestampMutiert = checkNotNull(tempBlob.getTimestampMutiert());
		return timestampMutiert.isBefore(LocalDateTime.now().minus(Constants.MAX_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES));
	}


}
