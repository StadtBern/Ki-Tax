package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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
	Optional<Betreuungsmitteilung> findBetreuungsmitteilung(@Nonnull String key);

	@Nonnull
	Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Fall fall);

	@Nonnull
	Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Betreuung betreuung);

	@Nonnull
	Collection<Mitteilung> getMitteilungenForPosteingang();

	@Nullable
	Mitteilung getEntwurfForCurrentRolle(@Nonnull Fall fall);

	@Nullable
	Mitteilung getEntwurfForCurrentRolle(@Nonnull Betreuung betreuung);

	void removeMitteilung(@Nonnull Mitteilung mitteilung);

	void removeAllMitteilungenForFall(@Nonnull Fall fall);

	/**
	 * Sucht alle Mitteilungen des uebergebenen Falls und fuer jede, die im Status NEU ist, wechselt
	 * ihren Status auf GELESEN.
	 */
	@Nonnull
	Collection<Mitteilung> setAllNewMitteilungenOfFallGelesen(@Nonnull Fall fall);

	@Nonnull
	Collection<Mitteilung> getNewMitteilungenForCurrentRolleAndFall(@Nonnull Fall fall);

	@Nonnull
	Long getAmountNewMitteilungenForCurrentBenutzer();

	Betreuungsmitteilung sendBetreuungsmitteilung(Betreuungsmitteilung betreuungsmitteilung);

	/**
	 * Applies all passed Betreuungspensen from the Betreuungsmitteilung to the existing Betreuung with the same number.
	 * If the newest Antrag is verfuegt, it will create a new Mutation out of it and apply the changes in this new Antrag.
	 * Returns the Antrag, in which the mitteilung was applied, which is much more useful than the mitteilung itself
	 * since normally you only need to know where the mitteilung was applied.
	 */
	Gesuch applyBetreuungsmitteilung(@NotNull Betreuungsmitteilung mitteilung);

	/**
	 * Returns the newest Betreuungsmitteilung for the given Betreuung
	 */
	Optional<Betreuungsmitteilung> findNewestBetreuungsmitteilung(String betreuungId);
}
