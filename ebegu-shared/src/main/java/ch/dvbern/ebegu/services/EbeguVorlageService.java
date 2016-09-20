package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service zum Verwalten von Kindern
 */
public interface EbeguVorlageService {

	/**
	 * Speichert den EbeguVorlage neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende EbeguVorlage aktualisiert
	 *
	 * @param ebeguVorlage Das EbeguVorlage als DTO
	 */
	@Nonnull
	EbeguVorlage saveEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage);

	@Nonnull
	Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey);

	@Nonnull
	List<EbeguVorlage> getALLEbeguVorlageByGesuchsperiode(Gesuchsperiode gesuchsperiode);

		/**
         * Aktualisiert die EbeguVorlage in der DB
         *
         * @param ebeguVorlage Die EbeguVorlage als DTO
         */
	@Nullable
	EbeguVorlage updateEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage);


	void remove(@Nonnull String id);

	Optional<EbeguVorlage> findById(@Nonnull final String id);

}
