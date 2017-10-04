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

package ch.dvbern.ebegu.vorlagen.nichteintreten;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 28/11/2016.
 */
public class NichteintretenPrintImpl extends BriefPrintImpl implements NichteintretenPrint {

	private final Betreuung betreuung;

	public NichteintretenPrintImpl(Betreuung betreuung) {

		super(betreuung.extractGesuch());

		this.betreuung = betreuung;

	}

	@Override
	public String getAngebotVon() {
		return Constants.DATE_FORMATTER.format(getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb());
	}

	@Override
	public String getAngebotBis() {
		return Constants.DATE_FORMATTER.format(getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigBis());
	}

	@Override
	public String getAngebotName() {
		return betreuung.getKind().getKindJA().getFullName()
			+ ", Angebot " + betreuung.getInstitutionStammdaten().getInstitution().getName()
			+ " (" + betreuung.getBGNummer() + ")";
	}
}
