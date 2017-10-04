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

import java.util.Set;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

/**
 * Dokumente für Kinder:
 * <p>
 * Fachstellenbestätigung (soziale Indikation):
 * Notwendig, wenn Frage nach Kind Fachstelle involviert mit Ja beantwortet, und es wird nicht die „Fachstelle für
 * Behinderung“ ausgewählt (Eine bestimmte Fachstelle ist für die Bestätigung von Behinderungen zuständig)
 * Es ist entweder dieses Dokument oder die Fachstellenbestätigung Behinderung gefordert, aber nie beide
 * <p>
 * Fachstellenbestätigung (Behinderung):
 * Notwendig, wenn Frage nach Kind Fachstelle involviert mit Ja beantwortet, und es wird die „Fachstelle für Behinderung“
 * ausgewählt (Eine bestimmte Fachstelle ist für die Bestätigung von Behinderungen zuständig)
 * Es ist entweder dieses Dokument oder die Fachstellenbestätigung Soziale Indikation gefordert, aber nie beide
 **/
public class KindDokumente extends AbstractDokumente<Kind, Object> {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {

		final Set<KindContainer> kindContainers = gesuch.getKindContainers();
		if (kindContainers == null || kindContainers.isEmpty()) {
			return;
		}

		for (KindContainer kindContainer : kindContainers) {
			final Kind kindJA = kindContainer.getKindJA();

			add(getDokument(DokumentTyp.FACHSTELLENBEST_SOZ, kindJA, kindJA.getFullName(), null, DokumentGrundPersonType.KIND,
				kindContainer.getKindNummer(), DokumentGrundTyp.KINDER), anlageVerzeichnis);
			add(getDokument(DokumentTyp.FACHSTELLENBEST_BEH, kindJA, kindJA.getFullName(), null, DokumentGrundPersonType.KIND,
				kindContainer.getKindNummer(), DokumentGrundTyp.KINDER), anlageVerzeichnis);

		}
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Kind kind) {
		if (kind != null) {
			switch (dokumentTyp) {
			case FACHSTELLENBEST_SOZ:
				return kind.getPensumFachstelle() != null && kind.getPensumFachstelle().getFachstelle() != null
					&& !kind.getPensumFachstelle().getFachstelle().isBehinderungsbestaetigung();
			case FACHSTELLENBEST_BEH:
				return kind.getPensumFachstelle() != null && kind.getPensumFachstelle().getFachstelle() != null
					&& kind.getPensumFachstelle().getFachstelle().isBehinderungsbestaetigung();
			default:
				return false;
			}
		}
		return false;
	}

}
