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

package ch.dvbern.ebegu.mocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.Eingangsart;
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
		MassenversandDataRow fall = new MassenversandDataRow();
		fall.setGesuchsperiode("2018/19");
		fall.setFall("001");
		fall.setGs1Vorname(gs1Vorname);
		fall.setGs1Name(gs1Name);
		fall.setGs1PersonId("123456");
		fall.setGs1Mail("gesuchsteller1@mailbucket.dvbern.ch");
		fall.setGs2Name("Partner");
		fall.setGs2Vorname("Paul");
		fall.setGs2PersonId("234567");
		fall.setGs2Mail("gesuchsteller2@mailbucket.dvbern.ch");
		fall.setAdresse("DV Bern AG\nNussbaumstrasse 21\n3006 Bern");
		fall.setKinderCols(new ArrayList<>());
		fall.setEinreichungsart(Eingangsart.ONLINE.name());
		fall.setStatus(AntragStatus.IN_BEARBEITUNG_JA.name());
		fall.setTyp(AntragTyp.ERSTGESUCH.name());
		return fall;
	}

	private MassenversandRepeatKindDataCol fakeKind(String kindName, String kindVorname) {
		MassenversandRepeatKindDataCol kind = new MassenversandRepeatKindDataCol();
		kind.setKindName(kindName);
		kind.setKindVorname(kindVorname);
		kind.setKindGeburtsdatum(LocalDate.now());
		kind.setKindDubletten("100, 101, 102");
		kind.setKindInstitutionKita("Brünnen");
		kind.setKindInstitutionTagi("Aaregg");
		kind.setKindInstitutionTeKleinkind("LeoLea");
		kind.setKindInstitutionTeSchulkind("LeoLea");
		kind.setKindInstitutionTagesschule("Tagesschule Manuel");
		kind.setKindInstitutionFerieninsel("Guarda");
		kind.setKindInstitutionenWeitere("Firlefanz, Brunnmatt");
		return kind;
	}
}
