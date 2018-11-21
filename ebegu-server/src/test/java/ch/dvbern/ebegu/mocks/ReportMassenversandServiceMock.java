/*
 * Copyright (C) 2018 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.mocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.reporting.massenversand.MassenversandDataRow;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandRepeatKindDataCol;
import ch.dvbern.ebegu.services.ReportMassenversandServiceBean;

public class ReportMassenversandServiceMock extends ReportMassenversandServiceBean {


	@Nonnull
	@Override
	public List<MassenversandDataRow> getReportMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nullable String gesuchPeriodeID,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nullable String text
	) {

		final List<MassenversandDataRow> reportDataMassenversand = new ArrayList<>();
		MassenversandDataRow fall1 = fakeFall("Muster", "Fridolin");
		fall1.getKinderCols().add(fakeKind("Muster", "Fridolinchen"));
		fall1.getKinderCols().add(fakeKind("Muster", "Fritzchen"));
		fall1.getKinderCols().add(fakeKind("Muster", "Friedalein"));
		reportDataMassenversand.add(fall1);
		MassenversandDataRow fall2 = fakeFall("Meier", "Hans");
		fall2.getKinderCols().add(fakeKind("Meier", "Hänschen"));
		fall2.getKinderCols().add(fakeKind("Meier", "Hämpelchen"));
		reportDataMassenversand.add(fall2);
		return reportDataMassenversand;
	}

	private MassenversandDataRow fakeFall(String gs1Name, String gs1Vorname) {
		MassenversandDataRow fall1 = new MassenversandDataRow();
		fall1.setGs1Vorname(gs1Vorname);
		fall1.setGs1Name(gs1Name);
		fall1.setKinderCols(new ArrayList<>());
		return fall1;
	}

	private MassenversandRepeatKindDataCol fakeKind(String kindName, String kindVorname) {
		MassenversandRepeatKindDataCol kind1_1 = new MassenversandRepeatKindDataCol();
		kind1_1.setKindName(kindName);
		kind1_1.setKindVorname(kindVorname);
		return kind1_1;
	}
}
