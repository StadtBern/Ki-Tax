package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.WizardStep;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Service zum Verwalten von WizardStep
 */
public interface WizardStepService {

	/**
	 * Speichert den WizardStep neu in der DB falls der Key noch nicht existiert. Sonst wird der existierende WizardStep aktualisiert
	 * @param kind Der WizardStep als DTO
	 */
	@Nonnull
	WizardStep saveWizardStep(@Nonnull WizardStep kind);

	/**
	 * @param key PK (id) des WizardSteps
	 * @return WizardStep mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<WizardStep> findWizardStep(@Nonnull String key);

	/**
	 * Gibt alle WizardSteps vom gegebenen Gesuch zurueck.
	 * @param gesuchId ID des Gesuchs
	 * @return Emptylist wenn nichts gefunden. Sonst die WizardSteps
	 */
	List<WizardStep> findWizardStepsFromGesuch(String gesuchId);
}
