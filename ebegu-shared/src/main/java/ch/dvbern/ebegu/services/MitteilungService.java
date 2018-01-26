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

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mitteilung;

/**
 * Service zum Verwalten von Mitteilungen
 */
public interface MitteilungService {

	/**
	 * Sendet die uebergebene Mitteilung. Der Empfaenger wird automatisch gesetzt
	 */
	@Nonnull
	Mitteilung sendMitteilung(@Nonnull Mitteilung mitteilung);

	/**
	 * Speichert die uebergebene Mitteilung als Entwurf. Der Empfaenger wird automatisch gesetzt
	 */
	@Nonnull
	Mitteilung saveEntwurf(@Nonnull Mitteilung mitteilung);

	/**
	 * Setzt die Mitteilung mit der uebergebenen ID als gelesen
	 */
	@Nonnull
	Mitteilung setMitteilungGelesen(@Nonnull String mitteilungsId);

	/**
	 * Setzt die Mitteilung mit der uebergebenen ID als erledigt
	 */
	@Nonnull
	Mitteilung setMitteilungErledigt(@Nonnull String mitteilungsId);

	/**
	 * Sucht die Mitteilung mit der uebergebenen ID
	 */
	@Nonnull
	Optional<Mitteilung> findMitteilung(@Nonnull String key);

	/**
	 * Sucht die Betreuungsmitteilung mit der uebergebenen ID
	 */
	@Nonnull
	Optional<Betreuungsmitteilung> findBetreuungsmitteilung(@Nonnull String key);

	/**
	 * Returns all Betreuungsmitteilungen that are linked with the given Betreuung.
	 */
	@Nonnull
	Collection<Betreuungsmitteilung> findAllBetreuungsmitteilungenForBetreuung(@Nonnull Betreuung betreuung);

	/**
	 * Gibt alle (Betreuungs-) Mitteilungen fuer die uebergebene Betreuung zurueck
	 */
	@Nonnull
	Collection<Mitteilung> findAllMitteilungenForBetreuung(@Nonnull Betreuung betreuung);

	/**
	 * Gibt alle Mitteilungen fuer den uebergenen Fall zurueck, welche fuer den eingeloggten Benutzer sichtbar sind.
	 */
	@Nonnull
	Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Fall fall);

	/**
	 * Gibt alle Mitteilungen fuer die uebergebene Betreuung zurueck, welche fuer den eingeloggten Benutzer sichtbar sind.
	 */
	@Nonnull
	Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Betreuung betreuung);

	/**
	 * Gibt alle Mitteilungen zurueck, welche im Posteingang des eingeloggten Benutzers angezeigt werden sollen.
	 */
	@Nonnull
	Collection<Mitteilung> getMitteilungenForPosteingang();

	/**
	 * Gibt den Entwurf einer Mitteilung zurueck, welche zum uebergebenen Fall erfasst wurde. Es gibt einen Entwurf pro Amt, d.h. alle Mitarbeiter
	 * des Jugendamtes "teilen" sich einen Entwurf, dasselbe gilt fuer die Mitarbeiter des Schulamtes.
	 */
	@Nullable
	Mitteilung getEntwurfForCurrentRolle(@Nonnull Fall fall);

	/**
	 * Gibt den Entwurf einer Mitteilung zurueck, welche zur uebergebenen Betreuung erfasst wurde. Es gibt einen Entwurf pro Amt, d.h. alle Mitarbeiter
	 * des Jugendamtes "teilen" sich einen Entwurf, dasselbe gilt fuer die Mitarbeiter des Schulamtes.
	 */
	@Nullable
	Mitteilung getEntwurfForCurrentRolle(@Nonnull Betreuung betreuung);

	/**
	 * Loescht die uebergebene Mitteilung
	 */
	void removeMitteilung(@Nonnull Mitteilung mitteilung);

	/**
	 * Loescht alle Mitteilungen des uebergebenen Falles
	 */
	void removeAllMitteilungenForFall(@Nonnull Fall fall);

	/**
	 * Loescht alle Betreuungsmitteilungen des uebergebenen Gesuchs.
	 */
	void removeAllBetreuungMitteilungenForGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Sucht alle Mitteilungen des uebergebenen Falls und fuer jede, die im Status NEU ist, wechselt
	 * ihren Status auf GELESEN.
	 */
	@Nonnull
	Collection<Mitteilung> setAllNewMitteilungenOfFallGelesen(@Nonnull Fall fall);

	/**
	 * Gibt alle ungelesenen Mitteilungen (Status NEU) fuer den uebergebenen Fall zurueck, welche fuer den eingeloggten Benutzer sichtbar sind
	 */
	@Nonnull
	Collection<Mitteilung> getNewMitteilungenForCurrentRolleAndFall(@Nonnull Fall fall);

	/**
	 * Gibt die Anzahl aller ungelesenen Mitteilungen (Status NEU), welche fuer den eingeloggten Benutzer sichtbar sind.
	 */
	@Nonnull
	Long getAmountNewMitteilungenForCurrentBenutzer();

	/**
	 * Sendet die uebergebene Betreuungsmitteilung. Der Empfaenger wird automatisch gesetzt
	 */
	@Nonnull
	Betreuungsmitteilung sendBetreuungsmitteilung(@Nonnull Betreuungsmitteilung betreuungsmitteilung);

	/**
	 * Applies all passed Betreuungspensen from the Betreuungsmitteilung to the existing Betreuung with the same number.
	 * If the newest Antrag is verfuegt, it will create a new Mutation out of it and apply the changes in this new Antrag.
	 * Returns the Antrag, in which the mitteilung was applied, which is much more useful than the mitteilung itself
	 * since normally you only need to know where the mitteilung was applied.
	 */
	@Nonnull
	Gesuch applyBetreuungsmitteilung(@Nonnull Betreuungsmitteilung mitteilung);

	/**
	 * Returns the newest Betreuungsmitteilung for the given Betreuung
	 */
	@Nonnull
	Optional<Betreuungsmitteilung> findNewestBetreuungsmitteilung(@Nonnull String betreuungId);

	/**
	 * Die uebergebene Mitteilung wird ans Jugendamt delegiert. Dabei wird als Empfaenger der VerantwortlicheJA des Falls gesetzt, falls ein
	 * solcher vorhanden ist, sonst der Default-Verantwortliche des Jugendamtes.
	 * Die Meldung wird fuer den neuen Empfaenger wieder auf NEU gesetzt.
	 */
	@Nonnull
	Mitteilung mitteilungUebergebenAnJugendamt(@Nonnull String mitteilungId);

	/**
	 * Die uebergebene Mitteilung wird ans Schulamt delegiert. Dabei wird als Empfaenger der VerantwortlicheSCH des Falls gesetzt, falls ein
	 * solcher vorhanden ist, sonst der Default-Verantwortliche des Schulamtes
	 * Die Meldung wird fuer den neuen Empfaenger wieder auf NEU gesetzt.
	 */
	@Nonnull
	Mitteilung mitteilungUebergebenAnSchulamt(@Nonnull String mitteilungId);
}
