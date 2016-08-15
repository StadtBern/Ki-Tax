package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Kindern
 */
public interface DokumentGrundService {

	/**
	 * Speichert den DokumentGrund neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende DokumentGrund aktualisiert
	 * @param dokumentGrund Das DokumentGrund als DTO
	 */
	@Nonnull
	DokumentGrund saveDokumentGrund(@Nonnull DokumentGrund dokumentGrund);

	/**
	 * @param key PK (id) des Kindes
	 * @return DokumentGrund mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<DokumentGrund> findDokumentGrund(@Nonnull String key);

	@Nonnull
	Collection<DokumentGrund> getAllDokumentGrundByGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Aktualisiert die DokumentGrund in der DB
	 * @param dokumentGrund Die DokumentGrund als DTO
	 */
	@Nullable
	DokumentGrund updateDokumentGrund(@Nonnull DokumentGrund dokumentGrund);


}
