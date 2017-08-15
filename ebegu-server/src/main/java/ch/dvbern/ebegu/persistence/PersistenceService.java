package ch.dvbern.ebegu.persistence;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Implementation der Persistenz als Stateless Session Bean.
 */
@Stateless
@Local(Persistence.class)
public class PersistenceService implements Persistence {

	@PersistenceContext(unitName = "ebeguPersistenceUnit")
	private EntityManager em;

	@Override
	public <T> T persist(final T entity) {
		em.persist(entity);
		return entity;
	}

	@Override
	public <T> T merge(final T entity) {
		return em.merge(entity);
	}

	@Override
	public <T> void remove(final T entity) {
		em.remove(entity);
	}

	@Override
	public <T> T find(final Class<T> entityClass, final Object primaryKey) {
		return em.find(entityClass, primaryKey);
	}

	@Override
	public <T> void remove(final Class<T> entityClass, final Object primaryKey) {
		final T entity = em.find(entityClass, primaryKey);
		em.remove(entity);
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return em.getCriteriaBuilder();
	}

	@Override
	public <T> List<T> getCriteriaResults(final CriteriaQuery<T> query) {
		return em.createQuery(query).getResultList();
	}

	@Override
	public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
		return em.getReference(entityClass, primaryKey);
	}

	@Override
	public <T> List<T> getCriteriaResults(CriteriaQuery<T> query, int maxResults) {
		TypedQuery<T> query1 = em.createQuery(query);
		query1.setMaxResults(maxResults);
		return query1.getResultList();
	}

	@Override
	public <T> T getCriteriaSingleResult(CriteriaQuery<T> query) {
		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}
}
