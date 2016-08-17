package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.TempDokument;
import ch.dvbern.ebegu.entities.TempDokument_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
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
@Local(TempDokumentService.class)
public class TempDokumentServiceBean implements TempDokumentService {


	private static final Logger LOG = LoggerFactory.getLogger(TempDokumentServiceBean.class);

	@Inject
	private Persistence<TempDokument> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Override
	public TempDokument create(@Nonnull Dokument dokument, String ip) {
		Objects.requireNonNull(dokument);
		Objects.requireNonNull(ip);

		return persistence.persist(new TempDokument(dokument, ip));
	}

	@Nullable
	@Override
	public TempDokument getTempDownloadByAccessToken(@Nonnull String accessToken) {
		Objects.requireNonNull(accessToken);

		Optional<TempDokument> tempDokumentOptional = criteriaQueryHelper.getEntityByUniqueAttribute(TempDokument.class, accessToken, TempDokument_.accessToken);

		if (!tempDokumentOptional.isPresent()) {
			return null;
		}

		TempDokument tempDokument = tempDokumentOptional.get();

		if (isFileDownloadExpired(tempDokument)) {
			return null;
		}

		return tempDokument;
	}

	@Override
	public void cleanUp() {
		LocalDateTime deleteOlderThan = LocalDateTime.now().minus(Constants.MAX_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES);
		LOG.debug("Deleting TempDocuments before {}", deleteOlderThan);

		try {
			Objects.requireNonNull(deleteOlderThan);
			criteriaQueryHelper.deleteAllBefore(TempDokument.class, deleteOlderThan);
		} catch (RuntimeException rte) {
			// timer methods may not throw exceptions or the timer will get cancelled (as per spec)
			String msg = "Unexpected error while deleting old TempDocuments";
			LOG.error(msg, rte);
		}

	}

	private boolean isFileDownloadExpired(@Nonnull TempDokument tempBlob) {
		LocalDateTime timestampMutiert = checkNotNull(tempBlob.getTimestampMutiert());
		return timestampMutiert.isBefore(LocalDateTime.now().minus(Constants.MAX_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES));
	}


}
