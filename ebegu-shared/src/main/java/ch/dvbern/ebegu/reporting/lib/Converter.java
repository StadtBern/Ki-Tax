/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.lib;

import org.apache.poi.ss.usermodel.Cell;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

@FunctionalInterface
public interface Converter extends Serializable {

	default void setCellValue(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
		try {
			setCellValueImpl(cell, pattern, o);
		} catch (RuntimeException rte) {
			throw new RuntimeException( // NOPMD.PreserveStackTrace - dient nur zum Debugging, damit der Entwickler an row und column rankommt
				"Error converting data on cell " + cell.getRowIndex() + '/' + cell.getColumnIndex() + " with pattern " + pattern + " on object " + o,
				rte);
		}
	}

	void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o);


}
