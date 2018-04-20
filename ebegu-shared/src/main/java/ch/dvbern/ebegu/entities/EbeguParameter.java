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

package ch.dvbern.ebegu.entities;

import java.math.BigDecimal;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von zeitabhängigen Parametern in Ki-Tax
 */
@Audited
@Entity
@Table(
	// Dieses Index muss erweitert werden, wenn die Parameter pro Mandant existieren, da sie dann dupliziert werden
	uniqueConstraints = @UniqueConstraint(columnNames = { "name", "gueltigAb", "gueltigBis" }, name = "UK_ebegu_parameter")
)
public class EbeguParameter extends AbstractDateRangedEntity {

	private static final long serialVersionUID = 8704632842261673111L;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private EbeguParameterKey name;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String value;

	public EbeguParameter() {
	}

	public EbeguParameter(EbeguParameterKey name, String value) {
		this(name, value, Constants.DEFAULT_GUELTIGKEIT);
	}

	public EbeguParameter(EbeguParameterKey name, String value, DateRange gueltigkeit) {
		this.name = name;
		this.value = value;
		this.setGueltigkeit(gueltigkeit);
	}

	@Nonnull
	public EbeguParameterKey getName() {
		return name;
	}

	public void setName(@Nonnull EbeguParameterKey name) {
		this.name = name;
	}

	@Nonnull
	public String getValue() {
		return value;
	}

	public void setValue(@Nonnull String value) {
		this.value = value;
	}

	/**
	 * @return a copy of the current Param with the gueltigkeit set to the passed DateRange
	 */
	public EbeguParameter copy(DateRange gueltigkeit) {
		EbeguParameter copiedParam = new EbeguParameter();
		copiedParam.setGueltigkeit(gueltigkeit);
		copiedParam.setName(this.getName());
		copiedParam.setValue(this.getValue());
		return copiedParam;
	}

	public BigDecimal getValueAsBigDecimal() {
		return new BigDecimal(value);
	}

	public Integer getValueAsInteger() {
		return Integer.valueOf(value);
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!super.isSame(other)) {
			return false;
		}
		if (!(other instanceof EbeguParameter)) {
			return false;
		}
		final EbeguParameter otherEbeguParameter = (EbeguParameter) other;
		return getName() == otherEbeguParameter.getName() &&
			Objects.equals(getValue(), otherEbeguParameter.getValue());
	}
}
