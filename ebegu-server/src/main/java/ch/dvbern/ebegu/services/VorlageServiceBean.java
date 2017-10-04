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

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service fuer Vorlage
 */
@Stateless
@Local(VorlageService.class)
public class VorlageServiceBean extends AbstractBaseService implements VorlageService {

	@Inject
	private Persistence persistence;

	@Override
	@Nonnull
	public Optional<Vorlage> findVorlage(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Vorlage a = persistence.find(Vorlage.class, key);
		return Optional.ofNullable(a);
	}

}
