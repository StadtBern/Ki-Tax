/*
 * Copyright © 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.util;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class PrecisionTooLargeException extends RuntimeException {
	private static final long serialVersionUID = 8715107775399719120L;

	@Nonnull
	private final BigDecimal value;

	private final int expectedPrecision;

	PrecisionTooLargeException(@Nonnull BigDecimal value, int expectedPrecision) {
		super("Resulting precision > max-precision for value " + value + '(' + value.precision() + " > " + expectedPrecision + ')');
		this.value = value;
		this.expectedPrecision = expectedPrecision;
	}

	@Nonnull
	public BigDecimal getValue() {
		return value;
	}

	public int getExpectedPrecision() {
		return expectedPrecision;
	}
}
