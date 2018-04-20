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

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.enums.WizardStepName;

/**
 * Service zum Verwalten von WizardStep
 */
public interface WizardStepService {

	/**
	 * Speichert den WizardStep neu in der DB falls der Key noch nicht existiert. Sonst wird der existierende WizardStep aktualisiert
	 *
	 * @param wizardStep Der WizardStep als DTO
	 */
	@Nonnull
	WizardStep saveWizardStep(@Nonnull WizardStep wizardStep);

	/**
	 * @param key PK (id) des WizardSteps
	 * @return WizardStep mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<WizardStep> findWizardStep(@Nonnull String key);

	/**
	 * Gibt alle WizardSteps vom gegebenen Gesuch zurueck.
	 *
	 * @param gesuchId ID des Gesuchs
	 * @return Emptylist wenn nichts gefunden. Sonst die WizardSteps
	 */
	List<WizardStep> findWizardStepsFromGesuch(String gesuchId);

	/**
	 * Gibt den gewuenschten Step des gegebenen Gesuchs zurueck
	 */
	WizardStep findWizardStepFromGesuch(String gesuchId, WizardStepName stepName);

	/**
	 * Fuer das uebergebene Gesuch und das alte und neue Objekt, werden alle Steps berechnet und ihren Status dementsprechend gesetzt. Ruft die
	 * Methode updateSteps mit substep=null
	 *
	 * @param gesuchId Id des Gesuchs
	 * @param oldEntity Objekt mit den Daten vor der Aktualisierung. kann auch null sein, wenn die Daten nicht relevant sind
	 * @param newEntity Objekt mit den Daten nach der Aktualisierung. kann auch null sein, wenn die Daten nicht relevant sind
	 * @param stepName name des Steps der Aktualisiert wurde.
	 * @return die Liste mit allen aktualisierten Status
	 */
	List<WizardStep> updateSteps(String gesuchId, @Nullable AbstractEntity oldEntity, @Nullable AbstractEntity newEntity, WizardStepName stepName);

	/**
	 * Fuer das uebergebene Gesuch und das alte und neue Objekt, werden alle Steps berechnet und ihren Status dementsprechend gesetzt
	 *
	 * @param gesuchId Id des Gesuchs
	 * @param oldEntity Objekt mit den Daten vor der Aktualisierung. kann auch null sein, wenn die Daten nicht relevant sind
	 * @param newEntity Objekt mit den Daten nach der Aktualisierung. kann auch null sein, wenn die Daten nicht relevant sind
	 * @param stepName name des Steps der Aktualisiert wurde.
	 * @param substep Manchmal muss man auf dem Server wissen, in welchem Substep wir sind, um zu entscheiden ob etwas gemacht werden muss.
	 * @return die Liste mit allen aktualisierten Status
	 */
	List<WizardStep> updateSteps(String gesuchId, @Nullable AbstractEntity oldEntity, @Nullable AbstractEntity newEntity,
		WizardStepName stepName, @Nullable Integer substep);

	/**
	 * Erstellt eine Liste mit allen notwendigen WizardSteps fuer das gegebene Gesuch. Fuer Mutationen bekommen alle Steps
	 * den Status OK und werden verfuegbar.
	 *
	 * @param gesuch das Gesuch
	 */
	@Nonnull
	List<WizardStep> createWizardStepList(Gesuch gesuch);

	void gesuchVerfuegen(@NotNull WizardStep verfuegenWizardStep);

	/**
	 * Sets the Status of the given Step to OK or MUTIERT. In order to be set to MUTIERT the data must have been
	 * changed in comparisson to the vorgaenger. If not it will be set to OK.
	 */
	void setWizardStepOkOrMutiert(@NotNull WizardStep wizardStep);

	/**
	 * Damit ein Gesuch removed werden kann meussen allse sseine WizardSteps entfernt werden
	 */
	void removeSteps(Gesuch gesToRemove);

	/**
	 * Setzt den übergebenen Step für das übergebene Gesuch auf Okay.
	 */
	void setWizardStepOkay(String gesuchId, WizardStepName stepName);
}
