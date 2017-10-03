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
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Set;

/**
 * Dokumente für Familiensituation:
 * <p>
 * Trennungsvereinbarung / Scheidungsurteil / Sonstiger Nachweis über Trennung / Eheschutzverfahren:
 * <p>
 * Wird nur bei Mutation der Familiensituation verlangt, nicht bei Erstgesuch.
 * Notwendig beim Wechsel von zwei Gesuchsteller auf einen.
 * Nur eines der drei Dokumente ist notwendig. Die Dokumente werden im Anlageverzeichnis als 1 Punkt geführt
 **/
public class FamiliensituationDokumente extends AbstractDokumente<Familiensituation, Familiensituation> {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {
		add(getDokument(DokumentTyp.NACHWEIS_TRENNUNG, gesuch.extractFamiliensituationErstgesuch(), gesuch.extractFamiliensituation(),
			null, null, null, DokumentGrundTyp.FAMILIENSITUATION), anlageVerzeichnis);
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Familiensituation dataForDocument) {
		return false;
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Familiensituation familiensituationErstgesuch, Familiensituation familiensituationMutation) {
		if (familiensituationErstgesuch == null || familiensituationMutation == null) {
			return false;
		}
		switch (dokumentTyp) {
			case NACHWEIS_TRENNUNG:
				//überprüfen, ob ein Wechsel von zwei Gesuchsteller auf einen stattgefunden hat.
				return familiensituationErstgesuch.hasSecondGesuchsteller() && !familiensituationMutation.hasSecondGesuchsteller();
			default:
				return false;
		}
	}
}
