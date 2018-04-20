/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.util;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

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
