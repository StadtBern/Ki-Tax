package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;

/**
 * Service zum Verwalten von Kindern
 */
public interface DokumentGrundService {

	/**
	 * Speichert den DokumentGrund neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende DokumentGrund aktualisiert
	 *
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

	/**
	 * findet in der DB alle Dokumentgruende eines bestimmten Gesuchs (wenn der user dafuer berechtigt ist)
	 * @param gesuch gesuch dessen Dokumentgruende geladen werden sollen
	 * @return Liste der Dokumentgruende
	 */
	@Nonnull
	Collection<DokumentGrund> findAllDokumentGrundByGesuch(@Nonnull Gesuch gesuch);

	/**
	 * findet in der DB alle Dokumentgreunde eines bestimmten Gesuches
	 * @param gesuch gesuch dessen Dokumentgruende geladen werden sollen
	 * @param doAuthCheck flag zum disabeln des authorization checks fuer interne methoden
	 * @return	 * @return Liste der Dokumentgruende
	 */
	@Nonnull
	Collection<DokumentGrund> findAllDokumentGrundByGesuch(@Nonnull Gesuch gesuch,  boolean doAuthCheck);

	@Nonnull
	Collection<DokumentGrund> findAllDokumentGrundByGesuchAndDokumentType(@Nonnull Gesuch gesuch, @Nonnull DokumentGrundTyp dokumentGrundTyp);

	/**
	 * Aktualisiert die DokumentGrund in der DB
	 *
	 * @param dokumentGrund Die DokumentGrund als DTO
	 */
	@Nullable
	DokumentGrund updateDokumentGrund(@Nonnull DokumentGrund dokumentGrund);


	void removeAllDokumentGrundeFromGesuch(Gesuch gesuch);
}
