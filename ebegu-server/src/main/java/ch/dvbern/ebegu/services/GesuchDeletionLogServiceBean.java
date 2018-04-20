/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.GesuchDeletionLog;
import ch.dvbern.ebegu.entities.GesuchDeletionLog_;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service zum Verwalten von GesuchDeletionLogs
 */
@Stateless
@Local(GesuchDeletionLogService.class)
public class GesuchDeletionLogServiceBean extends AbstractBaseService implements GesuchDeletionLogService {

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public GesuchDeletionLog saveGesuchDeletionLog(@Nonnull GesuchDeletionLog logEintrag) {
		Objects.requireNonNull(logEintrag);
		return persistence.persist(logEintrag);
	}

	@Nonnull
	@Override
	public Optional<GesuchDeletionLog> findGesuchDeletionLogByGesuch(@Nonnull String gesuchId) {
		Objects.requireNonNull(gesuchId, "id muss gesetzt sein");
		Collection<GesuchDeletionLog> logs = criteriaQueryHelper.getEntitiesByAttribute(GesuchDeletionLog.class, gesuchId, GesuchDeletionLog_.gesuchId);
		if (logs.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(logs.iterator().next());
	}
}
