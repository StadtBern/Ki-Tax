package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AbstractEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Service zum Verwalten von Historization elementen
 */
public interface HistorizationService {

	/**
	 * Gibt eine Liste mit allen Revisions fuer einen Objekt der entity mit entityname und mit entityid zurueck.
	 *
	 * @param entityName Klassename der Entity
	 * @param entityId ID Nummer der Entity
	 * @return Eine Liste mit Object-Arrays. Jedes Array enthaelt ein DefaultRevisionEntity, ein RevisionType und eine AbstractEntity
	 */
	@Nullable
	List<Object[]> getAllRevisionsById(@Nonnull String entityName, @Nonnull String entityId);

	/**
	 * Gibt alle Objekte der Art entityName auf einer bestimmten Revision zurueck. Das heisst der Zustand
	 * einer "Tabelle" in Revision x
	 *
	 * @param entityName Klassename der Entity
	 * @param revision Revision
	 * @return Eine Liste mit allen AbstractEntities von der eingegebenen .Revision
	 */
	@Nullable
	List<AbstractEntity> getAllEntitiesByRevision(@Nonnull String entityName, @Nonnull Integer revision);

}
