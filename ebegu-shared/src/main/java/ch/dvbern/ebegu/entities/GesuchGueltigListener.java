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

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * For using the unique key "UK_gueltiges_gesuch" we need to use false as null (there can be only one false but more than one null gueltigkeit)
 * So we set gueltig zu null when we mean guelitgkeit false
 */
public class GesuchGueltigListener {

	@PreUpdate
	@PrePersist
	public void preUpdate(Gesuch gesuch) {
		if (gesuch.getGueltig() != null && !gesuch.getGueltig()) {
			gesuch.setGueltig(null);
		}
	}

}
