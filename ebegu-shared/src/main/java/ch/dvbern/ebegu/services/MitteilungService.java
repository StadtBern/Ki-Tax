package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Mitteilung;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Mitteilungen
 */
public interface MitteilungService {

	@Nonnull
	Mitteilung createMitteilung(@Nonnull Mitteilung mitteilung);

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

	void removeAllMitteilungenForFall(Fall fall);
}
