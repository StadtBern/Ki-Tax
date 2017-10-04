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

package ch.dvbern.ebegu.vorlagen.verfuegung;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 12.08.2016
*/

import java.math.BigDecimal;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.Constants;

public class VerfuegungZeitabschnittPrintImpl implements VerfuegungZeitabschnittPrint {

	private final VerfuegungZeitabschnitt verfuegungZeitabschnitt;

	public VerfuegungZeitabschnittPrintImpl(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		this.verfuegungZeitabschnitt = verfuegungZeitabschnitt;
	}

	/**
	 * @return Von
	 */
	@Override
	public String getVon() {

		return Constants.DATE_FORMATTER.format(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb());
	}

	/**
	 * @return Bis
	 */
	@Override
	public String getBis() {

		return Constants.DATE_FORMATTER.format(verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis());
	}

	/**
	 * @return Betreuung
	 */
	@Override
	public int getBetreuung() {

		return verfuegungZeitabschnitt.getBetreuungspensum();
	}

	/**
	 * @return Anspruch
	 */
	@Override
	public int getAnspruch() {

		return verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
	}

	/**
	 * @return BGPensum
	 */
	@Override
	public int getBGPensum() {
		//hier wird das Minimum von (Rest)anspruch und von Betreuung zurueckgegeben. Dies enspricht der Definition des BG-Pensum
		return Math.min(getBetreuung(), getAnspruch());
	}

	/**
	 * @return Vollkosten
	 */
	@Override
	public BigDecimal getVollkosten() {

		return verfuegungZeitabschnitt.getVollkosten();
	}

	/**
	 * @return Elternbeitrag
	 */
	@Override
	public BigDecimal getElternbeitrag() {

		return verfuegungZeitabschnitt.getElternbeitrag();
	}

	/**
	 * @return Verguenstigung
	 */
	@Override
	public BigDecimal getVerguenstigung() {

		return verfuegungZeitabschnitt.getVerguenstigung();
	}
}
