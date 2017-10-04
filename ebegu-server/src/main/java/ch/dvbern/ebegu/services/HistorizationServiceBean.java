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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.metamodel.EntityType;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

/**
 * Service fuer Historization
 */
@Stateless
@Local(HistorizationService.class)
@RolesAllowed({ UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA })
public class HistorizationServiceBean extends AbstractBaseService implements HistorizationService {

	@Inject
	private Persistence persistence;

	@Override
	public List<Object[]> getAllRevisionsById(@Nonnull String entityName, @Nonnull String entityId) {
		for (EntityType<?> entityType : persistence.getEntityManager().getMetamodel().getEntities()) {
			if (entityType.getName().equalsIgnoreCase(entityName)) {
				AuditQuery query = AuditReaderFactory.get(persistence.getEntityManager())
					.createQuery()
					.forRevisionsOfEntity(entityType.getJavaType(), false, true)
					.add(AuditEntity.id().eq(entityId));
				return query.getResultList();
			}
		}
		return new ArrayList<>();
	}

	@Override
	public List<AbstractEntity> getAllEntitiesByRevision(@Nonnull String entityName, @Nonnull Integer revision) {
		for (EntityType<?> entityType : persistence.getEntityManager().getMetamodel().getEntities()) {
			if (entityType.getName().equalsIgnoreCase(entityName)) {
				AuditQuery query = AuditReaderFactory.get(persistence.getEntityManager())
					.createQuery()
					.forEntitiesAtRevision(entityType.getJavaType(), revision);
				return query.getResultList();
			}
		}
		return new ArrayList<>();
	}

}
