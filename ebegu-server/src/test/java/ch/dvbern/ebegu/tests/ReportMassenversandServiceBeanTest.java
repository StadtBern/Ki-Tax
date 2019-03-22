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

package ch.dvbern.ebegu.tests;

import java.time.LocalDate;

import ch.dvbern.ebegu.mocks.FileSaverServiceMock;
import ch.dvbern.ebegu.mocks.GesuchsperiodeServiceMock;
import ch.dvbern.ebegu.mocks.ReportMassenversandServiceMock;
import ch.dvbern.ebegu.reporting.ReportMassenversandService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.util.UploadFileInfo;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

@SuppressWarnings("unused")
public class ReportMassenversandServiceBeanTest {

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	private ReportMassenversandService reportService = new ReportMassenversandServiceMock();

	@InjectIntoMany
	private FileSaverService fileSaverService = new FileSaverServiceMock();

	@InjectIntoMany
	private GesuchsperiodeService gesuchsperiodeService = new GesuchsperiodeServiceMock();


	@Test
	public void generateExcelReportMassenversand() throws Exception {

		UploadFileInfo uploadFileInfo = reportService.generateExcelReportMassenversand(LocalDate.now(),
			LocalDate.now().plusDays(10),
			"2018/19",
			true,
			true,
			true,
			true,
			"Erinnerungsbrief Erneuerungsgesuch");

		assertNotNull(uploadFileInfo.getBytes());
	}
}
