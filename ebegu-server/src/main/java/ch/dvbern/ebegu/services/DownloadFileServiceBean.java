/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.entities.AbstractDateRangedEntity_;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.DownloadFile_;
import ch.dvbern.ebegu.entities.FileMetadata;
import ch.dvbern.ebegu.enums.TokenLifespan;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service fuer den Download von Dokumenten
 */
@Stateless
@Local(DownloadFileService.class)
@PermitAll
public class DownloadFileServiceBean implements DownloadFileService {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileServiceBean.class);

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private FileSaverService fileSaverService;


	@Nonnull
	@Override
	public DownloadFile create(@Nonnull FileMetadata fileMetadata, @Nonnull String ip) {
		Objects.requireNonNull(fileMetadata);
		Objects.requireNonNull(ip);

		return persistence.persist(new DownloadFile(fileMetadata, ip));
	}

	@Nonnull
	@Override
	public DownloadFile create(@Nonnull UploadFileInfo fileInfo, @Nonnull TokenLifespan lifespan, @Nonnull String ip) {
		Objects.requireNonNull(fileInfo);
		Objects.requireNonNull(lifespan);
		Objects.requireNonNull(ip);
		final DownloadFile downloadFile = new DownloadFile(fileInfo, ip);
		downloadFile.setLifespan(lifespan);
		return persistence.persist(downloadFile);
	}

	//	EBEGU-1663 Wildfly 10 hack, this can be removed as soon as WF11 runs and download file can be generated when report is finsihed
	@Nonnull
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DownloadFile insertDirectly(@Nonnull String fileIdToUpdate, @Nonnull UploadFileInfo fileInfo, @Nonnull TokenLifespan lifespan, @Nonnull String ip) {
		Objects.requireNonNull(fileIdToUpdate);
		Objects.requireNonNull(fileInfo);
		Objects.requireNonNull(lifespan);
		Objects.requireNonNull(ip);
		final DownloadFile downloadFile = new DownloadFile(fileInfo, ip);
		downloadFile.setLifespan(lifespan);
		final int updatedRows = updateByQuery(fileIdToUpdate, downloadFile);
		if (updatedRows != 1) {
			LOG.warn("Should have updated exactly one row but updated " + updatedRows);
		}
		return downloadFile;
	}

	private int updateByQuery(@Nonnull String accessTokenIdToUpdate, @Nonnull DownloadFile downloadFile) {

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaUpdate<DownloadFile> query = cb.createCriteriaUpdate(DownloadFile.class);
		Root<DownloadFile> root = query.from(DownloadFile.class);

		ParameterExpression<String> accessTokenIdParam = cb.parameter(String.class, "accessTokenIdParam");
		Predicate idPredicate = cb.equal(root.get(DownloadFile_.accessToken), accessTokenIdParam);

		ParameterExpression<String> filenameParam = cb.parameter(String.class, "filenameParam");
		ParameterExpression<String>filepfadParam = cb.parameter(String.class, "filepfadParam");
		ParameterExpression<String> filesizeParam = cb.parameter(String.class, "filesizeParam");

		query.set(root.get(DownloadFile_.filename), filenameParam);
		query.set(root.get(DownloadFile_.filepfad), filepfadParam);
		query.set(root.get(DownloadFile_.filesize), filesizeParam);

		query.where(idPredicate);
		final Query q = persistence.getEntityManager().createQuery(query);

		q.setParameter(accessTokenIdParam, accessTokenIdToUpdate);
		q.setParameter(filenameParam, downloadFile.getFilename());
		q.setParameter(filepfadParam, downloadFile.getFilepfad());
		q.setParameter(filesizeParam, downloadFile.getFilesize());
		return q.executeUpdate();
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
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cleanUp() {
		deleteShortTermAccessTokens();
		deleteLongTermAccessTokens();
		// Auch die physischen Files loeschen
		fileSaverService.deleteAllFilesInTempReportsFolder();
	}

	private void deleteShortTermAccessTokens() {
		LocalDateTime deleteOlderThan = LocalDateTime.now().minus(Constants.MAX_SHORT_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES);
		LOG.debug("Deleting {} TempDocuments before {}", TokenLifespan.SHORT, deleteOlderThan);

		try {
			Objects.requireNonNull(deleteOlderThan);
			this.deleteAllTokensBefore(DownloadFile.class, TokenLifespan.SHORT, deleteOlderThan);
		} catch (RuntimeException rte) {
			// timer methods may not throw exceptions or the timer will get cancelled (as per spec)
			String msg = "Unexpected error while deleting old TempDocuments";
			LOG.error(msg, rte);
		}
	}

	private void deleteLongTermAccessTokens() {
		LocalDateTime deleteOlderThan = LocalDateTime.now().minus(Constants.MAX_LONGER_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES);
		LOG.debug("Deleting {} TempDocuments before {}", TokenLifespan.LONG, deleteOlderThan);

		try {
			Objects.requireNonNull(deleteOlderThan);
			this.deleteAllTokensBefore(DownloadFile.class, TokenLifespan.LONG, deleteOlderThan);
		} catch (RuntimeException rte) {
			// timer methods may not throw exceptions or the timer will get cancelled (as per spec)
			String msg = "Unexpected error while deleting old TempDocuments";
			LOG.error(msg, rte);
		}
	}

	private <T extends DownloadFile> int deleteAllTokensBefore(@Nonnull Class<T> entityClazz, @Nonnull TokenLifespan lifespan, @Nonnull LocalDateTime before) {
		checkNotNull(entityClazz);
		checkNotNull(before);

		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClazz);
		Root<T> root = delete.from(entityClazz);

		ParameterExpression<LocalDateTime> beforeParam = cb.parameter(LocalDateTime.class, "before");
		Predicate timePred = cb.lessThan(root.get(AbstractDateRangedEntity_.timestampMutiert), beforeParam);

		ParameterExpression<TokenLifespan> lifespanParam = cb.parameter(TokenLifespan.class, "type");
		root.get(DownloadFile_.ip);
		Predicate lifespanPred = cb.equal(root.get(DownloadFile_.lifespan), lifespanParam);

		delete.where(timePred, lifespanPred);
		Query query = persistence.getEntityManager().createQuery(delete);
		query.setParameter(beforeParam, before);
		query.setParameter(lifespanParam, lifespan);
		return query.executeUpdate();
	}

	/**
	 * Access Token fuer Download ist nur fuer eine bestimmte Zeitspanne (3Min) gueltig
	 */
	private boolean isFileDownloadExpired(@Nonnull DownloadFile tempBlob) {
		LocalDateTime timestampMutiert = checkNotNull(tempBlob.getTimestampMutiert());
		if (tempBlob.getLifespan().equals(TokenLifespan.SHORT)) {
			return timestampMutiert.isBefore(LocalDateTime.now().minus(Constants.MAX_SHORT_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES));
		} else {
			return timestampMutiert.isBefore(LocalDateTime.now().minus(Constants.MAX_LONGER_TEMP_DOWNLOAD_AGE_MINUTES, ChronoUnit.MINUTES));
		}
	}
}
