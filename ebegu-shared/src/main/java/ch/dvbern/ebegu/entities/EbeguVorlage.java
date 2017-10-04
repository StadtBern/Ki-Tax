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


import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Entitaet zum Speichern von zeitabhängigen Vorlagen in Ki-Tax
 */
@Audited
@Entity
public class EbeguVorlage extends AbstractDateRangedEntity implements Comparable<EbeguVorlage> {


	private static final long serialVersionUID = 8704632842261673111L;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private EbeguVorlageKey name;

	@Nullable
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ebeguvorlage_vorlage_id"), nullable = true)
	private Vorlage vorlage;

	@Column(nullable = false)
	@Nullable
	private boolean proGesuchsperiode = true;

	public EbeguVorlage() {
	}

	public EbeguVorlage(EbeguVorlageKey name) {
		this(name, Constants.DEFAULT_GUELTIGKEIT, true);
	}

	public EbeguVorlage(EbeguVorlageKey name, DateRange gueltigkeit) {
		this(name, gueltigkeit, true);
	}

	public EbeguVorlage(EbeguVorlageKey name, DateRange gueltigkeit, boolean proGesuchsperiode) {
		this.name = name;
		this.proGesuchsperiode = proGesuchsperiode;
		this.setGueltigkeit(gueltigkeit);
	}

	@Nonnull
	public EbeguVorlageKey getName() {
		return name;
	}

	public void setName(@Nonnull EbeguVorlageKey name) {
		this.name = name;
	}

	public Vorlage getVorlage() {
		return vorlage;
	}

	public void setVorlage(Vorlage vorlage) {
		this.vorlage = vorlage;
	}

	@Nullable
	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(@Nullable boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	/**
	 * @param gueltigkeit
	 * @return a copy of the current Param with the gueltigkeit set to the passed DateRange
	 */
	public EbeguVorlage copy(DateRange gueltigkeit) {
		EbeguVorlage copiedParam = new EbeguVorlage();
		copiedParam.setGueltigkeit(new DateRange(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis()));
		copiedParam.setName(this.getName());
		copiedParam.setProGesuchsperiode(this.isProGesuchsperiode());
		return copiedParam;
	}

	@Override
	public int compareTo(EbeguVorlage o) {
		return this.getName().compareTo(o.getName());
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
		if (!(other instanceof EbeguVorlage)) {
			return false;
		}
		final EbeguVorlage otherEbeguVorlage = (EbeguVorlage) other;
		return Objects.equals(getName(), otherEbeguVorlage.getName()) &&
			EbeguUtil.isSameObject(getVorlage(), otherEbeguVorlage.getVorlage()) &&
			isProGesuchsperiode() == otherEbeguVorlage.isProGesuchsperiode();
	}
}
