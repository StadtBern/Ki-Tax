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
