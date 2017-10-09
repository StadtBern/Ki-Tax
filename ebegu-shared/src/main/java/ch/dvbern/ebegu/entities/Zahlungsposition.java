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
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.ebegu.util.MathUtil;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von Zahlungspositionen in der Datenbank.
 */
@Audited
@Entity
public class Zahlungsposition extends AbstractEntity implements Comparable<Zahlungsposition> {

	private static final long serialVersionUID = -8403487439884700618L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlungsposition_zahlung_id"), nullable = false)
	private Zahlung zahlung;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlungsposition_verfuegungZeitabschnitt_id"), nullable = false)
	private VerfuegungZeitabschnitt verfuegungZeitabschnitt;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ZahlungspositionStatus status;

	@NotNull
	@Column(nullable = false)
	private BigDecimal betrag;

	@NotNull
	@Column(nullable = false)
	private boolean ignoriert;

	public Zahlung getZahlung() {
		return zahlung;
	}

	public void setZahlung(Zahlung zahlung) {
		this.zahlung = zahlung;
	}

	public VerfuegungZeitabschnitt getVerfuegungZeitabschnitt() {
		return verfuegungZeitabschnitt;
	}

	public void setVerfuegungZeitabschnitt(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		this.verfuegungZeitabschnitt = verfuegungZeitabschnitt;
	}

	public ZahlungspositionStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungspositionStatus status) {
		this.status = status;
	}

	public BigDecimal getBetrag() {
		return betrag;
	}

	public void setBetrag(BigDecimal betrag) {
		this.betrag = betrag;
	}

	public Kind getKind() {
		return verfuegungZeitabschnitt.getVerfuegung().getBetreuung().getKind().getKindJA();
	}

	public boolean isIgnoriert() {
		return ignoriert;
	}

	public void setIgnoriert(boolean ignoriert) {
		this.ignoriert = ignoriert;
	}

	@Override
	public int compareTo(@Nonnull Zahlungsposition o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getKind().getNachname(), o.getKind().getNachname());
		builder.append(this.getKind().getVorname(), o.getKind().getVorname());
		builder.append(this.getKind().getGeburtsdatum(), o.getKind().getGeburtsdatum());
		builder.append(this.getVerfuegungZeitabschnitt().getGueltigkeit().getGueltigAb(), o.getVerfuegungZeitabschnitt().getGueltigkeit().getGueltigAb());
		builder.append(this.getBetrag(), o.getBetrag());
		return builder.toComparison();
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
		if (!(other instanceof Zahlungsposition)) {
			return false;
		}
		final Zahlungsposition otherZahlungsposition = (Zahlungsposition) other;
		return EbeguUtil.isSameObject(getVerfuegungZeitabschnitt(), otherZahlungsposition.getVerfuegungZeitabschnitt()) &&
			Objects.equals(getStatus(), otherZahlungsposition.getStatus()) &&
			MathUtil.isSame(getBetrag(), otherZahlungsposition.getBetrag()) &&
			Objects.equals(isIgnoriert(), otherZahlungsposition.isIgnoriert());
	}
}
