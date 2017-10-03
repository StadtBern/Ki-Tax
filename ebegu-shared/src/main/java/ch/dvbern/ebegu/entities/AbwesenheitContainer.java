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

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.util.EbeguUtil;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entity fuer AbwesenheitContainer
 */
@Audited
@Entity
public class AbwesenheitContainer extends AbstractEntity implements Comparable<AbwesenheitContainer> {

	private static final long serialVersionUID = -8876987863152535840L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_container_betreuung_id"), nullable = false)
	private Betreuung betreuung;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_container_abwesenheit_gs"))
	private Abwesenheit abwesenheitGS;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_container_abwesenheit_ja"))
	private Abwesenheit abwesenheitJA;


	public AbwesenheitContainer() {
	}


	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}

	public Abwesenheit getAbwesenheitGS() {
		return abwesenheitGS;
	}

	public void setAbwesenheitGS(Abwesenheit abwesenheitGS) {
		this.abwesenheitGS = abwesenheitGS;
	}

	public Abwesenheit getAbwesenheitJA() {
		return abwesenheitJA;
	}

	public void setAbwesenheitJA(Abwesenheit abwesenheitJA) {
		this.abwesenheitJA = abwesenheitJA;
	}

	/**
	 * This method isSame is a bit different because it doesn't compare the Betreuung of this AbwesenheitContainer
	 * directly. Reason is that it doesn't matter if the Betreuung has changed or not, the only important thing is
	 * that the Abwesenheit "this" and "other" belong to the same Betreuung. To that porpouse we compare the
	 * Betreuungen just by the BetreuungNummer.
	 */
	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof AbwesenheitContainer)) {
			return false;
		}
		final AbwesenheitContainer otherAbwesenheitContainer = (AbwesenheitContainer) other;
		return EbeguUtil.isSameObject(getAbwesenheitGS(), otherAbwesenheitContainer.getAbwesenheitGS()) &&
			EbeguUtil.isSameObject(getAbwesenheitJA(), otherAbwesenheitContainer.getAbwesenheitJA()) &&
			Objects.equals(this.getBetreuung().getBetreuungNummer(), otherAbwesenheitContainer.getBetreuung().getBetreuungNummer());
	}

	@Override
	public int compareTo(AbwesenheitContainer o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getAbwesenheitJA(), o.getAbwesenheitJA());
		builder.append(this.getAbwesenheitJA().getId(), o.getAbwesenheitJA().getId());
		return builder.toComparison();
	}

	public AbwesenheitContainer copyForMutation(AbwesenheitContainer mutation, @Nonnull Betreuung betreuungMutation) {
		super.copyForMutation(mutation);
		mutation.setBetreuung(betreuungMutation);
		mutation.setAbwesenheitGS(null);
		mutation.setAbwesenheitJA(this.getAbwesenheitJA().copyForMutation(new Abwesenheit()));
		return mutation;
	}
}
