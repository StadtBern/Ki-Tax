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

package ch.dvbern.ebegu.api.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

/**
 * Jax wrapper um Mitteilungen-Liste zurueckgeben zu koennen. Ohne den Wrapper verliert man die Information
 * der verschiedenen Subtypen
 */
public class JaxMitteilungen implements Serializable {

	private static final long serialVersionUID = -5915063223908835792L;

	@Nonnull
	@NotNull
	private Collection<JaxMitteilung> mitteilungen = new ArrayList<>();

	public JaxMitteilungen() {}

	public JaxMitteilungen(Collection<JaxMitteilung> mitteilungen) {
		this.mitteilungen = mitteilungen;
	}

	@Nonnull
	public Collection<JaxMitteilung> getMitteilungen() {
		return mitteilungen;
	}

	public void setMitteilungen(@Nonnull Collection<JaxMitteilung> mitteilungen) {
		this.mitteilungen = mitteilungen;
	}
}
