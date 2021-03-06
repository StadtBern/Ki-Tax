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

package ch.dvbern.ebegu.vorlagen.finanziellesituation;

import java.math.BigDecimal;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.Constants;

public class BerechnungsblattPrintImpl implements BerechnungsblattPrint {

	private final VerfuegungZeitabschnitt verfuegungZeitabschnitt;

	public BerechnungsblattPrintImpl(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		this.verfuegungZeitabschnitt = verfuegungZeitabschnitt;
	}

	@Override
	public String getVon() {

		return Constants.DATE_FORMATTER.format(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb());
	}

	@Override
	public String getBis() {

		return Constants.DATE_FORMATTER.format(verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis());
	}

	@Override
	public String getJahr() {
		return String.valueOf(verfuegungZeitabschnitt.getEinkommensjahr());
	}

	@Override
	public BigDecimal getMassgebendesEinkommenVorAbzFamgr() {

		return verfuegungZeitabschnitt.getMassgebendesEinkommenVorAbzFamgr();
	}

	@Override
	public String getFamiliengroesse() {

		BigDecimal value = verfuegungZeitabschnitt.getFamGroesse() != null ? verfuegungZeitabschnitt.getFamGroesse() : BigDecimal.ZERO;
		if (value.compareTo(BigDecimal.valueOf(value.intValue())) > 0) {
			value = value.setScale(1, BigDecimal.ROUND_DOWN);
			return value.toString();
		} else {
			return Integer.toString(value.intValue());
		}
	}

	@Override
	public BigDecimal getAbzugFamGroesse() {

		return verfuegungZeitabschnitt.getAbzugFamGroesse();
	}

	@Override
	public BigDecimal getMassgebendesEinkommenNachAbzugFamgr() {

		return verfuegungZeitabschnitt.getMassgebendesEinkommen();
	}
}
