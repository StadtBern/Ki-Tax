package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;

/**
 * Service fuer Historization
 */
@Stateless
@Local(HistorizationService.class)
@RolesAllowed({UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
public class HistorizationServiceBean extends AbstractBaseService implements HistorizationService {

	@Inject
	private Persistence<AbstractEntity> persistence;

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
