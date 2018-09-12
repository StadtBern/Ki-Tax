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

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

@Stateless
public class DokumentenverzeichnisEvaluator {

	private final AbstractDokumente familiensituationDokumente = new FamiliensituationDokumente();
	private final AbstractDokumente kindAnlagen = new KindDokumente();
	private final AbstractDokumente erwerbspensumDokumente = new ErwerbspensumDokumente();
	private final AbstractDokumente finanzielleSituationDokumente = new FinanzielleSituationDokumente();
	private final AbstractDokumente einkommensverschlechterungDokumente = new EinkommensverschlechterungDokumente();

	public Set<DokumentGrund> calculate(Gesuch gesuch) {

		Set<DokumentGrund> anlageVerzeichnis = new HashSet<>();

		if (gesuch != null) {
			familiensituationDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			kindAnlagen.getAllDokumente(gesuch, anlageVerzeichnis);
			erwerbspensumDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			finanzielleSituationDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			einkommensverschlechterungDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
		}

		return anlageVerzeichnis;
	}

	public void addSonstige(Set<DokumentGrund> dokumentGrunds) {
		DokumentGrund dokumentGrund = new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.DIV);
		dokumentGrund.setNeeded(false);
		dokumentGrunds.add(dokumentGrund);
	}

	public void addPapiergesuch(Set<DokumentGrund> dokumentGrunds) {
		DokumentGrund dokumentGrund = new DokumentGrund(DokumentGrundTyp.PAPIERGESUCH, DokumentTyp.ORIGINAL_PAPIERGESUCH);
		dokumentGrund.setNeeded(false);
		dokumentGrunds.add(dokumentGrund);
	}

	public void addFreigabequittung(Set<DokumentGrund> dokumentGrunds) {
		DokumentGrund dokumentGrund = new DokumentGrund(DokumentGrundTyp.FREIGABEQUITTUNG, DokumentTyp.ORIGINAL_FREIGABEQUITTUNG);
		dokumentGrund.setNeeded(false);
		dokumentGrunds.add(dokumentGrund);
	}
}
