package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(FinanzielleSituationService.class)
public class FinanzielleSituationServiceBean extends AbstractBaseService implements FinanzielleSituationService {

	@Inject
	private Persistence<FinanzielleSituationContainer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private FinanzielleSituationRechner finSitRechner;

	@Nonnull
	@Override
	public FinanzielleSituationContainer saveFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation) {
		Objects.requireNonNull(finanzielleSituation);
		return persistence.merge(finanzielleSituation);
	}

	@Nonnull
	@Override
	public Optional<FinanzielleSituationContainer> findFinanzielleSituation(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		FinanzielleSituationContainer finanzielleSituation =  persistence.find(FinanzielleSituationContainer.class, id);
		return Optional.ofNullable(finanzielleSituation);
	}

	@Nonnull
	@Override
	public Collection<FinanzielleSituationContainer> getAllFinanzielleSituationen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(FinanzielleSituationContainer.class));
	}

	@Override
	public void removeFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation) {
		Validate.notNull(finanzielleSituation);
		finanzielleSituation.getGesuchsteller().setFinanzielleSituationContainer(null);
		persistence.merge(finanzielleSituation.getGesuchsteller());

		Optional<FinanzielleSituationContainer> propertyToRemove = findFinanzielleSituation(finanzielleSituation.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFinanzielleSituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, finanzielleSituation));
		persistence.remove(FinanzielleSituationContainer.class, propertyToRemove.get().getId());
	}

	@Override
	@Nonnull
	public FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch) {
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, calculateEndOfPreviousYear(gesuch.getGesuchsperiode()));
		BigDecimal abzugAufgrundFamiliengroesse = finSitRechner
			.calculateAbzugAufgrundFamiliengroesse(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), familiengroesse);
		return new FinanzielleSituationResultateDTO(gesuch, familiengroesse, abzugAufgrundFamiliengroesse);
	}

	/**
	 * Gibt 31.12.XXXX zurueck, wo XXXX ist das Vorjahr vom Wert gueltigAb der Gesuchsperiode. Sollte etwas nicht stimmen gibt null zurueck.
	 * @param gesuchsperiode
	 * @return
     */
	private LocalDate calculateEndOfPreviousYear(Gesuchsperiode gesuchsperiode) {
		if (gesuchsperiode != null) {
			LocalDate endOfPreviousYear = gesuchsperiode.getGueltigkeit().getGueltigAb();
			endOfPreviousYear = endOfPreviousYear.minusYears(1).withMonth(12).withDayOfMonth(31);
			return endOfPreviousYear;
		}
		return null;
	}

}
