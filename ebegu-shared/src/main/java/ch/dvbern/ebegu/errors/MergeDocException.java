package ch.dvbern.ebegu.errors;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 16.08.2016
*/

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.ApplicationException;
import java.io.Serializable;

@ApplicationException(rollback = true)
public class MergeDocException extends EbeguException {

	private static final long serialVersionUID = 1289688844437918486L;

	/**
	 * Fall bei der Mergen der Vorlgage eine Fehler auftritt wird diese Exception geworfen
	 *
	 * @param methodeName Methoden Name
	 * @param message Fehlermeldungstext
	 * @param cause die cause
	 * @param args die Argumente
	 */
	public MergeDocException(@Nullable String methodeName, @Nullable String message, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(methodeName, message, ErrorCodeEnum.ERROR_PRINT_PDF, cause, args);
	}
}
