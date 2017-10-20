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

package ch.dvbern.ebegu.vorlagen;

import java.time.LocalDate;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import org.apache.commons.lang.StringUtils;

public class BriefPrintImpl implements BriefPrint {

	protected final Gesuch gesuch;

	public BriefPrintImpl(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	@Override
	public String getZustellAdresse() {

		String newlineMSWord = "\n";
		String zustellAdresse = "";
		String organisation = PrintUtil.getOrganisation(gesuch);

		if (StringUtils.isNotEmpty(organisation)) {
			zustellAdresse += organisation;
		} else {
			zustellAdresse += ServerMessageUtil.getMessage("BriefPrintImpl_FAMILIE");
			zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerName(gesuch);
		}

		zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerStrasse(gesuch);

		String adrZusatz = PrintUtil.getAdresszusatz(gesuch);

		if (StringUtils.isNotEmpty(adrZusatz)) {
			zustellAdresse += newlineMSWord + adrZusatz;
		}

		zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerPLZStadt(gesuch);

		return zustellAdresse;

	}

	@Override
	public String getZustellDatum() {
		return Constants.DATE_FORMATTER.format(LocalDate.now());
	}

	@Override
	public String getPeriode() {
		return getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()
			+ "/" + getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigBis().getYear();
	}

	@Override
	public String getFallNummer() {
		return PrintUtil.createFallNummerString(getGesuch());
	}

	@Override
	public String getGesuchstellerNames() {
		String gesuchstellerNames = "";

		if (gesuch.getGesuchsteller1() != null) {
			gesuchstellerNames = gesuch.getGesuchsteller1().extractFullName();
		}
		if (gesuch.getGesuchsteller2() != null) {
			gesuchstellerNames += ", " + gesuch.getGesuchsteller2().extractFullName();
		}

		return gesuchstellerNames;
	}

	@Override
	public String getGesuchEingangsDatum() {
		return Constants.DATE_FORMATTER.format(gesuch.getEingangsdatum());
	}

	@Override
	public String getUnterzeichner() {
		if (gesuch.getFall().getVerantwortlicher() != null) {
			return gesuch.getFall().getVerantwortlicher().getFullName();
		}
		return "";
	}
}
