package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.TempDokument;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;

public interface TempDokumentService {

	TempDokument create(@Nonnull Dokument dokument, String ip);

	@Nullable
	TempDokument getTempDownloadByAccessToken(@Nonnull String accessToken);

	void cleanUp();
}
