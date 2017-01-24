package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Sequence;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

@Stateless
@RolesAllowed({SUPER_ADMIN, ADMIN})
public class SearchIndexServiceBean implements SearchIndexService {

	private static final Logger LOG = LoggerFactory.getLogger(SearchIndexServiceBean.class);


	@Inject
	private Persistence<Sequence> persistence;



	@Override
	public void rebuildSearchIndex() {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(persistence.getEntityManager());
		try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			throw new IllegalStateException("Could not index data", e);
		}
	}
}
