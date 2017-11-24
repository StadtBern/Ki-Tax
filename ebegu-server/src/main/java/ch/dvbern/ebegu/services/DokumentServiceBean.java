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

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service fuer Dokument
 */
@Stateless
@Local(DokumentService.class)
public class DokumentServiceBean extends AbstractBaseService implements DokumentService {

	@Inject
	private Persistence persistence;

	@Inject
	private Authorizer authorizer;

	@Override
	@Nonnull
	public Optional<Dokument> findDokument(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Dokument doc = persistence.find(Dokument.class, key);
		if (doc == null) {
			return Optional.empty();
		}
		final Gesuch gesuch = doc.getDokumentGrund().getGesuch(); //may not be null
		this.authorizer.checkReadAuthorization(gesuch);
		return Optional.of(doc);
	}

}
