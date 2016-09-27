/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Mapped Superclass fuer Entities die mit einem mananten verknuepft sind. (fragt sich ob das lohnt)
 */
@MappedSuperclass
@Audited
public abstract class AbstractMandantEntity extends AbstractEntity {

	private static final long serialVersionUID = 1115113871605366824L;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_mandant_id"), nullable = false)
	@Nullable
	private Mandant mandant = null;

	@Nullable
	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(@Nullable Mandant mandant) {
		this.mandant = mandant;
	}

}
