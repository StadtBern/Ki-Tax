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
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.listener.BenutzerChangedEntityListener;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Entity
@EntityListeners(BenutzerChangedEntityListener.class)
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "username", name = "UK_username"),
	indexes = @Index(columnList = "username", name = "IX_benutzer_username")
)
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Benutzer extends AbstractEntity {

	private static final long serialVersionUID = 6372688971894279665L;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String username = null;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Field
	private String nachname = null;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Field
	private String vorname = null;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String email = null;

	@Transient
	private Berechtigung currentBerechtigung;

	@Valid
	@SortNatural
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "benutzer")
	private Set<Berechtigung> berechtigungen = new TreeSet<>();

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_mandant_id"))
	private Mandant mandant;

	@NotNull
	@Column(nullable = false)
	private Boolean gesperrt = false;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Berechtigung> getBerechtigungen() {
		return berechtigungen;
	}

	public void setBerechtigungen(Set<Berechtigung> berechtigungen) {
		this.berechtigungen = berechtigungen;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}

	public Boolean getGesperrt() {
		return gesperrt;
	}

	public void setGesperrt(Boolean gesperrt) {
		this.gesperrt = gesperrt;
	}

	@Nonnull
	public String getFullName() {
		return (this.vorname != null ? this.vorname : "") + " "
			+ (this.nachname != null ? this.nachname : "");
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("username", username)
			.toString();
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
		if (!(other instanceof Benutzer)) {
			return false;
		}
		final Benutzer otherBenutzer = (Benutzer) other;
		return Objects.equals(getUsername(), otherBenutzer.getUsername());
	}

	@Nonnull
	public Berechtigung getCurrentBerechtigung() {
		if (currentBerechtigung == null) {
			for (Berechtigung berechtigung : berechtigungen) {
				if (berechtigung.isGueltig()) {
					currentBerechtigung = berechtigung;
				}
			}
		}
		Objects.requireNonNull(currentBerechtigung, "Keine aktive Berechtigung vorhanden fuer Benutzer " + username);
		return currentBerechtigung;
	}

	@Nonnull
	public UserRole getRole() {
		return getCurrentBerechtigung().getRole();
	}

	public void setRole(@Nonnull UserRole userRole) {
		getCurrentBerechtigung().setRole(userRole);
	}

	@Nullable
	public Institution getInstitution() {
		return getCurrentBerechtigung().getInstitution();
	}

	public void setInstitution(@Nullable Institution institution) {
		getCurrentBerechtigung().setInstitution(institution);
	}

	@Nullable
	public Traegerschaft getTraegerschaft() {
		return getCurrentBerechtigung().getTraegerschaft();
	}

	public void setTraegerschaft(@Nullable Traegerschaft traegerschaft) {
		getCurrentBerechtigung().setTraegerschaft(traegerschaft);
	}
}
