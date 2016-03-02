/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.persistence;

import net.bull.javamelody.JpaWrapperPublic;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class DatabaseProducer {

	@SuppressWarnings("PMD.UnusedPrivateField")
	@PersistenceContext(name = "KitaSchedulerPersistenceUnit")
	private EntityManager em;

	@Produces
	EntityManager produceEntityManager() {
		return JpaWrapperPublic.wrapForMonitoring(em);
	}
}
