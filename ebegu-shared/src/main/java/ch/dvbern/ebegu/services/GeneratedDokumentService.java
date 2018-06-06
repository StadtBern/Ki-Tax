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

import java.io.IOException;
import java.util.Collection;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Pain001Dokument;
import ch.dvbern.ebegu.entities.WriteProtectedDokument;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.errors.MergeDocException;

/**
 * Service zum Verwalten von GeneratedDokumenten
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public interface GeneratedDokumentService {

	/**
	 * Erstellt ein neues GeneratedDokument wenn es noch nicht existiert und sonst aktualisiert das Bestehende
	 */
	@Nonnull
	WriteProtectedDokument saveDokument(@Nonnull WriteProtectedDokument dokument);

	@Nullable
	WriteProtectedDokument findGeneratedDokument(@Nonnull String gesuchId, @Nonnull String filename);

	@Nullable
	Pain001Dokument findPain001Dokument(@Nonnull String zahlungsauftragId, @Nonnull String filename);

	@Nonnull
	WriteProtectedDokument saveGeneratedDokumentInDB(@Nonnull byte[] data, @Nonnull GeneratedDokumentTyp dokumentTyp, @Nonnull AbstractEntity entity,
		@Nonnull String fileName, boolean writeProtected) throws MimeTypeParseException;

	@Nonnull
	WriteProtectedDokument getFinSitDokumentAccessTokenGeneratedDokument(@Nonnull Gesuch gesuch, @Nonnull Boolean forceCreation)
		throws MimeTypeParseException, MergeDocException;

	@Nonnull
	WriteProtectedDokument getBegleitschreibenDokument(@Nonnull Gesuch gesuch, @Nonnull Boolean forceCreation)
		throws MimeTypeParseException, MergeDocException;

	@Nonnull
	WriteProtectedDokument getKompletteKorrespondenz(@Nonnull Gesuch gesuch) throws MimeTypeParseException, MergeDocException;

	@Nonnull
	WriteProtectedDokument getFreigabequittungAccessTokenGeneratedDokument(@Nonnull Gesuch gesuch, @Nonnull Boolean forceCreation)
		throws MimeTypeParseException, MergeDocException;

	@Nonnull
	WriteProtectedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(@Nonnull Gesuch gesuch, @Nonnull Betreuung betreuung,
		@Nonnull String	manuelleBemerkungen, @Nonnull Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException;

	@Nonnull
	WriteProtectedDokument getMahnungDokumentAccessTokenGeneratedDokument(@Nonnull Mahnung mahnung, @Nonnull Boolean createWriteProtected)
		throws MimeTypeParseException, IOException, MergeDocException;

	@Nonnull
	WriteProtectedDokument getNichteintretenDokumentAccessTokenGeneratedDokument(@Nonnull Betreuung betreuung, @Nonnull Boolean forceCreation)
		throws MimeTypeParseException, IOException, MergeDocException;

	@Nonnull
	WriteProtectedDokument getPain001DokumentAccessTokenGeneratedDokument(@Nonnull Zahlungsauftrag zahlungsauftrag, @Nonnull Boolean forceCreation)
		throws MimeTypeParseException;

	void removeAllGeneratedDokumenteFromGesuch(@Nonnull Gesuch gesuch);

	@Nonnull
	Collection<GeneratedDokument> findGeneratedDokumentsFromGesuch(@Nonnull Gesuch gesuch);
}
