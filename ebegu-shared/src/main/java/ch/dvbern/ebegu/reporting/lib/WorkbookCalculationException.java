/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.reporting.lib;

import org.apache.poi.ss.util.CellReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("UncheckedExceptionClass")
public class WorkbookCalculationException extends RuntimeException {
	private static final long serialVersionUID = 8695767334650650138L;

	private final int row;
	private final int col;
	@Nonnull
	private final ErrorCode code;
	@Nullable
	private final Serializable value;

	public enum ErrorCode {
		WORKBOOK_SERIALIZATION,
		CELLTYPE_NOT_SUPPORTED,
		FORMAT_NUMERIC,
		FORMAT_STRING,
		FORMAT_BOOLEAN,
		FORMAT_INTEGER,
		FORMAT_KINDERGARTEN_BELEGUNG,
		VALUE_IS_NULL,
		FORMAT_DATE
	}

	public WorkbookCalculationException(@Nonnull ErrorCode code, @Nullable Throwable cause) {
		super(code.name(), cause);
		this.code = code;
		this.row = -1;
		this.col = -1;
		this.value = null;
	}

	public WorkbookCalculationException(@Nonnull CellReference pos, @Nonnull ErrorCode code, @Nullable Serializable value, @Nullable Throwable cause) {
		super(code.name() + '@' + pos + '=' + value, cause);
		// CellReference ist leider nicht serializable, drum separat speichern :(
		this.row = pos.getRow();
		this.col = pos.getCol();
		this.code = checkNotNull(code);
		this.value = value;
	}

	public WorkbookCalculationException(@Nonnull CellReference pos, @Nonnull ErrorCode code, @Nullable Serializable value) {
		this(pos, code, value, null);
	}


	@Nonnull
	public ErrorCode getCode() {
		return code;
	}

	@Nullable
	public CellReference getPos() {
		return new CellReference(row, col);
	}

	@Nullable
	public Serializable getValue() {
		return value;
	}
}
