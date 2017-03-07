package ch.dvbern.ebegu.reporting.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExcelMergeException extends Exception {
	private static final long serialVersionUID = 7296329429118050852L;

	public ExcelMergeException(@Nonnull String message) {
		super(message);
	}

	public ExcelMergeException(@Nonnull String message, @Nullable Throwable cause) {
		super(message, cause);
	}
}
