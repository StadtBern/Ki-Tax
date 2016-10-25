/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Optional;

public enum MathUtil {

	GANZZAHL(19, 0, RoundingMode.HALF_UP),
	EINE_NACHKOMMASTELLE(19, 1, RoundingMode.HALF_UP),
	DEFAULT(19, 2, RoundingMode.HALF_UP),	// Am Schluss muss immer mit diesem gerechnet werden, da sonst nicht in DB gespeichert werden kann!
	EXACT(30, 10, RoundingMode.HALF_UP);	// Für Zwischenresultate

	private final int precision;
	private final int scale;

	public static final BigDecimal HUNDRED = BigDecimal.valueOf(100, 0);
	public static final BigDecimal ROUNDING_INCREMENT = new BigDecimal("0.05");


	@Nonnull
	private final RoundingMode roundingMode;

	MathUtil(int precision, int scale, @Nonnull RoundingMode roundingMode) {
		this.precision = precision;
		this.scale = scale;
		this.roundingMode = roundingMode;
	}

	@Nonnull
	private BigDecimal validatePrecision(@Nonnull BigDecimal value) {
		if (value.precision() > precision) {
			throw new PrecisionTooLargeException(value, precision);
		}
		return value;
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal from(@Nullable BigDecimal src) {
		if (src == null) {
			return null;
		}
		BigDecimal val = BigDecimal.ZERO
			.setScale(scale, roundingMode)
			.add(src)
			.setScale(scale, roundingMode);
		return validatePrecision(val);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal from(@Nullable BigInteger src) {
		if (src == null) {
			return null;
		}
		BigDecimal val = new BigDecimal(src)
			.setScale(scale, roundingMode);
		return validatePrecision(val);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal from(@Nullable Long src) {
		if (src == null) {
			return null;
		}
		BigDecimal val = new BigDecimal(src)
			.setScale(scale, roundingMode);
		return validatePrecision(val);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal from(@Nullable Integer src) {
		if (src == null) {
			return null;
		}
		BigDecimal val = new BigDecimal(src)
			.setScale(scale, roundingMode);
		return validatePrecision(val);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal from(@Nullable Double src) {
		if (src == null) {
			return null;
		}
		BigDecimal val = new BigDecimal(String.valueOf(src))
			.setScale(scale, roundingMode);
		return validatePrecision(val);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal add(@Nullable BigDecimal value, @Nullable BigDecimal augment) {
		if (value == null || augment == null) {
			return null;
		}
		BigDecimal result = value
			.add(augment)
			.setScale(scale, roundingMode);
		return validatePrecision(result);
	}

	/**
	 * adds augement parameters to value, null values are treated as zero
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal add(@Nullable BigDecimal value, @Nullable BigDecimal... augment) {
		if ( augment.length == 0) {
			return null;
		}

		BigDecimal result = value!=null? value:BigDecimal.ZERO;
		for (BigDecimal valueToAdd : augment) {
			if (valueToAdd != null) {
				result = result
					.add(valueToAdd)
					.setScale(scale, roundingMode);
			}
		}
		return validatePrecision(result);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal subtract(@Nullable BigDecimal value, @Nullable BigDecimal subtrahend) {
		if (value == null || subtrahend == null) {
			return null;
		}
		BigDecimal result = value
			.subtract(subtrahend)
			.setScale(scale, roundingMode);
		return validatePrecision(result);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal multiply(@Nullable BigDecimal value, @Nullable BigDecimal multiplicand) {
		if (value == null || multiplicand == null) {
			return null;
		}
		BigDecimal result = value
			.multiply(multiplicand)
			.setScale(scale, roundingMode);
		return validatePrecision(result);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal multiply(@Nullable BigDecimal... value) {
		if (value == null) {
			return null;
		}
		BigDecimal result = BigDecimal.ONE;
		for (BigDecimal bigDecimal : value) {
			result = multiply(result, bigDecimal);
		}
		return validatePrecision(result);
	}

	/**
	 * @throws PrecisionTooLargeException if the resulting value exceeds the defined precision
	 */
	@Nullable
	public BigDecimal divide(@Nullable BigDecimal dividend, @Nullable BigDecimal divisor) {
		if (dividend == null || divisor == null) {
			return null;
		}
		if (0 == BigDecimal.ZERO.compareTo(divisor)) {
			throw new IllegalArgumentException("Divide by zero: " + dividend + '/' + divisor);
		}
		BigDecimal result = dividend.divide(divisor, scale, roundingMode);
		return validatePrecision(result);
	}

	/**
	 * Konvertiert eine Prozentzahl (z.B. 34%) in eine Bruchzahl (z.B. 0.34), i.E.: dividiert durch 100
	 */
	@Nullable
	public BigDecimal pctToFraction(@Nullable BigDecimal value) {
		if (value == null) {
			return null;
		}
		return divide(value, HUNDRED);
	}

	/**
	 * Konvertiert eine Bruchzahl (z.B. 0.34) in eine Prozentzahl (z.B. 34%), i.E.: multipliziert mit 100
	 */
	@Nullable
	public BigDecimal fractionToPct(@Nullable BigDecimal value) {
		if (value == null) {
			return null;
		}
		return multiply(value, HUNDRED);
	}

	/**
	 * Rundet einen BigDecimal auf 2 Nachkommastellen und auf 5 Rappen.
     */
	public static BigDecimal roundToFrankenRappen(BigDecimal amount) {
		// Ab welcher Nachkommastelle soll gerundet werden???
		// Wir runden zuerst die vierte auf die dritte...
		BigDecimal roundedUp = amount.multiply(MathUtil.HUNDRED).divide(MathUtil.HUNDRED, 3, BigDecimal.ROUND_HALF_UP);
		// ... dann davon auf 5-Rappen runden
		BigDecimal divided = GANZZAHL.divide(roundedUp, ROUNDING_INCREMENT);
		return DEFAULT.multiply(divided, ROUNDING_INCREMENT);
	}

	/**
	 * Vergleicht zwei optionale BigDecimal.
	 * @return TRUE, wenn beide Werte NULL sind, oder wenn beide BigDecimal (via compareTo) identisch sind. Sonst FALSE
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static boolean isSame(@Nonnull Optional<BigDecimal> a, @Nonnull Optional<BigDecimal> b) {
		return a.isPresent() && b.isPresent() && a.get().compareTo(b.get()) == 0 || !a.isPresent() && !b.isPresent();
	}

	/**
	 * Rundet die eingegebene Nummer in 10er Schritten.
	 * Beispiel
	 * 20 bis 24 = 20
	 * 25 bis 29 = 30
	 * @param pensumFachstelle
	 * @return
     */
	public static int roundIntToTens(int pensumFachstelle) {
		return (int) (Math.round((double) pensumFachstelle / 10) * 10);
	}


	/**
	 * rundet auf die naechste Ganzzahl groesser gleich 0
	 */
	public static BigDecimal positiveNonNullAndRound(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		// Returns the maximum of this BigDecimal and val.
		value = value.setScale(0, RoundingMode.HALF_UP);
		return value.max(BigDecimal.ZERO);
	}

	/**
	 * rundet auf die naechste Ganzzahl groesser gleich 0
	 */
	public static BigDecimal positiveNonNull(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.max(BigDecimal.ZERO);
	}
}
