package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Mahnungen
 */
public interface MahnungService {

	/**
	 * Erstellt eine neue Mahnung in der DB, falls der key noch nicht existiert
	 */
	@Nonnull
	Mahnung createMahnung(@Nonnull Mahnung mahnung);

	/**
	 * Gibt die Mahnung mit der uebergebenen Id zurueck.
	 */
	@Nonnull
	Optional<Mahnung> findMahnung(@Nonnull String mahnungId);

	/**
	 * Gibt alle (aktiven und vergangenen) Mahnungen fuer das uebergebene Gesuch zurueck
	 */
	@Nonnull
	Collection<Mahnung> findMahnungenForGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Setzt den Status zurueck auf "in Bearbeitung". Setzt die offenen Mahnungen auf inaktiv.
	 */
	void mahnlaufBeenden(@Nonnull Gesuch gesuch);

	/**
	 * Generiert den Vorschlag für die Bemerkungen aus den fehlenden Dokumenten.
     */
	@Nonnull
	String getInitialeBemerkungen(@Nonnull Gesuch gesuch);

	/**
	 * Ueberprueft fuer alle aktiven Mahnungen, ob deren Ablauffrist eingetreten ist
	 */
	void fristAblaufTimer();

	/**
	 * Gibt die (einzige aktive erstmahnung zurueck)
	 */
	Optional<Mahnung> findAktiveErstMahnung(Gesuch gesuch);
}
