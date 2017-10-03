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

package ch.dvbern.ebegu.rules.anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Set;


/**
 * Abstrakte Klasse zum berechnen der benötigten Dokumente
 *
 * @param <T1>
 */
abstract class AbstractDokumente<T1, T2> {

	public abstract void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis);

	public abstract boolean isDokumentNeeded(DokumentTyp dokumentTyp, T1 dataForDocument);

	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, T1 dataForDocument1, T2 dataForDocument2) {
		return isDokumentNeeded(dokumentTyp, dataForDocument1);
	}

	void add(DokumentGrund dokumentGrund, Set<DokumentGrund> anlageVerzeichnis) {
		if (dokumentGrund != null) {
			anlageVerzeichnis.add(dokumentGrund);
		}
	}

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T1 dataForDocument, String tag,
							  DokumentGrundPersonType personType, Integer personNumber, DokumentGrundTyp dokumentGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, dataForDocument)) {
			return new DokumentGrund(dokumentGrundTyp, tag, personType, personNumber, dokumentTyp);
		}
		return null;
	}

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T1 dataForDocument1, T2 dataForDocument2, String tag,
							  DokumentGrundPersonType personType, Integer personNumber, DokumentGrundTyp dokumentGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, dataForDocument1, dataForDocument2)) {
			return new DokumentGrund(dokumentGrundTyp, tag, personType, personNumber, dokumentTyp);
		}
		return null;
	}
}
