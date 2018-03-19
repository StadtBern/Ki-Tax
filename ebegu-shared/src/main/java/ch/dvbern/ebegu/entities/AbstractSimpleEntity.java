/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.util.Constants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.Hibernate;

@MappedSuperclass
public abstract class AbstractSimpleEntity implements Serializable {

	private static final long serialVersionUID = -979317154050183445L;

	@Id
	@Column(unique = true, nullable = false, updatable = false, length = Constants.UUID_LENGTH)
	@Size(min = Constants.UUID_LENGTH, max = Constants.UUID_LENGTH)
	private String id;

	@Version
	@NotNull
	private long version;


	public AbstractSimpleEntity() {
		//da wir teilweise schon eine id brauchen bevor die Entities gespeichert werden initialisieren wir die uuid hier
		id = UUID.randomUUID().toString();
	}


	public String getId() {
		return id;
	}

	public void setId(@Nullable String id) {
		this.id = id;
	}

	// Nullable, da erst im PrePersist gesetzt
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@SuppressFBWarnings(value = "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS", justification = "Es wird Hibernate.getClass genutzt um von Proxies (LazyInit) die konkrete Klasse zu erhalten")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}

		AbstractSimpleEntity that = (AbstractSimpleEntity) o;

		Objects.requireNonNull(getId());
		Objects.requireNonNull(that.getId());

		return getId().equals(that.getId());
	}

	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}
