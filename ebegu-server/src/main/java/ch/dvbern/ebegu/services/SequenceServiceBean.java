/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Sequence;
import ch.dvbern.ebegu.entities.Sequence_;
import ch.dvbern.ebegu.enums.SequenceType;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Stateless
@Local
public class SequenceServiceBean implements SequenceService {

	private static final String SEQUENCE_TYPE = "sequenceType";

	@Inject
	private Persistence<Sequence> persistence;


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
