package ch.dvbern.ebegu.services;


import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import javax.annotation.security.PermitAll;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * Uebergeordneter Service. Alle Services sollten von diesem Service erben. Wird verwendet um Interceptors einzuschalten
 */
public abstract class AbstractBaseService {

	@Inject
	private Persistence<Fall> persistence;

	@PermitAll
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateLuceneIndex(Class<? extends AbstractEntity> clazz, String id) {
		// Den Lucene-Index manuell nachführen, da es bei unidirektionalen Relationen nicht automatisch geschieht!
		Session session = persistence.getEntityManager().unwrap(Session.class);
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		// Den Index loeschen...
		fullTextSession.purge(clazz, id);
		// ... und neu erstellen
		Object customer = fullTextSession.load(clazz, id);
		fullTextSession.index(customer);
	}
}
