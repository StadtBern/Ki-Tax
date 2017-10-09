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
import java.util.Set;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.dto.KindDubletteDTO;
import ch.dvbern.ebegu.entities.KindContainer;

/**
 * Service zum Verwalten von Kindern
 */
public interface KindService {

	/**
	 * Speichert das Kind neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende Kind aktualisiert
	 *
	 * @param kind Das Kind als DTO
	 */
	@Nonnull
	KindContainer saveKind(@Nonnull KindContainer kind);

	/**
	 * @param key PK (id) des Kindes
	 * @return Kind mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<KindContainer> findKind(@Nonnull String key);

	/**
	 * Gibt alle KindContainer des Gesuchs zurueck
	 */
	@Nonnull
	List<KindContainer> findAllKinderFromGesuch(@Nonnull String gesuchId);

	/**
	 * entfernt ein Kind aus der Databse
	 *
	 * @param kindId Id des Kindes zu entfernen
	 */
	void removeKind(@Nonnull String kindId);

	/**
	 * entfernt ein Kind aus der Databse. Um diese Methode aufzurufen muss man sich vorher vergewissern, dass das Kind existiert
	 */
	void removeKind(@Nonnull KindContainer kind);

	/**
	 * Gibt alle Kinder zurueck, welche Mutationen betreffen, die verfügt sind und deren
	 * kindMutiert-Flag noch nicht gesetzt sind
	 */
	@Nonnull
	List<KindContainer> getAllKinderWithMissingStatistics();

	/**
	 * Sucht Kinder mit gleichen Merkmalen in anderen Faellen.
	 */
	@Nonnull
	Set<KindDubletteDTO> getKindDubletten(@Nonnull String gesuchId);
}
