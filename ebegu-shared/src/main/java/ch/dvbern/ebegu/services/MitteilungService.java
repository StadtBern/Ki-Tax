package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Mitteilung;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Mitteilungen
 */
public interface MitteilungService {

	@Nonnull
	Mitteilung sendMitteilung(@Nonnull Mitteilung mitteilung);

	@Nonnull
	Mitteilung saveEntwurf(@Nonnull Mitteilung mitteilung);

	@Nonnull
	Mitteilung setMitteilungGelesen(@Nonnull String mitteilungsId);

	@Nonnull
	Mitteilung setMitteilungErledigt(@Nonnull String mitteilungsId);

	@Nonnull
	Optional<Mitteilung> findMitteilung(@Nonnull String key);

	@Nonnull
	Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Fall fall);

	@Nonnull
	Collection<Mitteilung> getMitteilungenForPosteingang();

	@Nullable
	Mitteilung getEntwurfForCurrentRolle(Fall fall);

	void removeMitteilung(Mitteilung mitteilung);

	void removeAllMitteilungenForFall(Fall fall);

	/**
	 * Sucht alle Mitteilungen des uebergebenen Falls und fuer jede, die im Status NEU ist, wechselt
	 * ihren Status auf GELESEN.
	 */
	Collection<Mitteilung> setAllNewMitteilungenOfFallGelesen(Fall fall);

	Collection<Mitteilung> getNewMitteilungenForCurrentRolle(Fall fall);
}
