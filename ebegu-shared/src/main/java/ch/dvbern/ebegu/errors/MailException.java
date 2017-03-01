/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.errors;

import javax.ejb.ApplicationException;

/**
 *   Exception that gets thrown if the transmission of an email fails
 */
@ApplicationException(rollback = false)
public class MailException extends Exception {


	private static final long serialVersionUID = 663284993138581412L;

	public MailException() {
	}

	public MailException(final Throwable cause) {
		super(cause);
	}

	public MailException(final String message) {
		super(message);
	}

	public MailException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
