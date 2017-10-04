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

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteProtectedDokumentListener {

	private static final Logger LOG = LoggerFactory.getLogger(WriteProtectedDokumentListener.class);

	@PostLoad
	public void postLoad(WriteProtectedDokument writeProtectedDokument) {
		writeProtectedDokument.setOrginalWriteProtected(writeProtectedDokument.isWriteProtected());
	}

	@PreUpdate
	public void preUpdate(WriteProtectedDokument writeProtectedDokument) {

		// Write Protection darf nicht entfernt werden
		if (writeProtectedDokument.isOrginalWriteProtected() && !writeProtectedDokument.isWriteProtected()) {
			LOG.warn("Write protection auf GeneratedDokument darf nicht mehr entfernt werden!");
			writeProtectedDokument.setWriteProtected(true);
		}

		// Wenn es writeProtection nicht neu ist, darf es nicht gespeichert werden! (Wenn WriteProtection neu gesetzt
		// wird, darf es noch genau einmal upgedated werden)
		if (writeProtectedDokument.isWriteProtected() && writeProtectedDokument.isOrginalWriteProtected()) {
			throw new IllegalStateException("GeneratedDokument darf nicht mehr verändert werden wenn writeProtected!");
		}
	}

}
