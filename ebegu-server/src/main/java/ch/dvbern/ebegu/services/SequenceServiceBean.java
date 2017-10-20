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

import java.util.List;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Sequence;
import ch.dvbern.ebegu.entities.Sequence_;
import ch.dvbern.ebegu.enums.SequenceType;
import ch.dvbern.lib.cdipersistence.Persistence;

import static com.google.common.base.Preconditions.checkNotNull;

@Stateless
@Local
public class SequenceServiceBean implements SequenceService {

	private static final String SEQUENCE_TYPE = "sequenceType";

	@Inject
	private Persistence persistence;

	@Nonnull
	@Override
	// Damit die Nummer bei wiederholtem aufruf in derselben (parent-) Transaktion nicht immer dieselbe ist,
	// muss dieser Aufruf in einer neuen Transaktion ausgeführt werden.
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long createNumberTransactional(@Nonnull SequenceType seq, @Nonnull Mandant existingMandant) {
		checkNotNull(seq);

		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaQuery<Sequence> query = cb.createQuery(Sequence.class);
		Root<Sequence> root = query.from(Sequence.class);

		ParameterExpression<Mandant> mandantParam = cb.parameter(Mandant.class, Mandant.MANDANT_PARAMETER);
		Predicate mandantPredicate = cb.equal(root.get(Sequence_.mandant), mandantParam);

		ParameterExpression<SequenceType> typeParam = cb.parameter(SequenceType.class, SEQUENCE_TYPE);
		Predicate typePredicate = cb.equal(root.get(Sequence_.sequenceType), typeParam);

		query.where(mandantPredicate, typePredicate);

		TypedQuery<Sequence> q = persistence.getEntityManager().createQuery(query)
			.setParameter(mandantParam, existingMandant)
			.setParameter(typeParam, seq)
			.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		List<Sequence> resultList = q.getResultList();
		Sequence sequence;
		if (resultList.isEmpty()) {
			//wir sind hier mal liberal und initialisieren automatisch wenn noch nicht gemacht
			sequence = initFallNrSeqMandant(existingMandant);
		} else if (resultList.size() == 1) {
			sequence = resultList.get(0);
		} else {
			throw new IllegalStateException("TooMany Results for sequence query");
		}
		Long number = sequence.incrementAndGet();
		persistence.merge(sequence);
		return number;
	}

	@Override
	public Sequence initFallNrSeqMandant(@Nonnull Mandant mandant) {
		checkNotNull(mandant);

		Sequence seqFallNr = new Sequence(SequenceType.FALL_NUMMER, 0L);

		seqFallNr.setMandant(mandant);
		persistence.persist(seqFallNr);
		return seqFallNr;
	}
}
