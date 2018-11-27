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

	/**
	 * tests that a JA-Gesuch is only returned when the method is called with inklBgGesuche==true
	 */
	@Test
	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeAngebotTypJA() {


//		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
//			Constants.START_OF_TIME,
//			Constants.END_OF_TIME,
//			verfuegtesGesuch.getGesuchsperiode().getId(),
//			true,
//			false,
//			false,
//			false,
//			"myText"
//		);

		//		final List<Gesuch> jaGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
//			Constants.START_OF_TIME,
//			Constants.END_OF_TIME,
//			verfuegtesGesuch.getGesuchsperiode().getId(),
//			true,
//			false,
//			false,
//			false
//		);
//		Assert.assertEquals(1, jaGesuche.size());
//		Assert.assertEquals(verfuegtesGesuch, jaGesuche.get(0));
//
//		final List<Gesuch> mischGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
//			Constants.START_OF_TIME,
//			Constants.END_OF_TIME,
//			verfuegtesGesuch.getGesuchsperiode().getId(),
//			false,
//			true,
//			false,
//			false
//		);
//		Assert.assertTrue(mischGesuche.isEmpty());
//
//		final List<Gesuch> schGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
//			Constants.START_OF_TIME,
//			Constants.END_OF_TIME,
//			verfuegtesGesuch.getGesuchsperiode().getId(),
//			false,
//			false,
//			true,
//			false
//		);
//		Assert.assertTrue(schGesuche.isEmpty());
	}
	//
	//	/**
	//	 * tests that a TS-Gesuch is only returned when the method is called with inklTSGesuche==true
	//	 */
	//	@Test
	//	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeAngebotTypTSGesuch() {
	//		Gesuch tsGesuch = TestDataUtil.createAndPersistASIV11(institutionService, persistence,
	//			LocalDate.of(1980, Month.MARCH, 25), AntragStatus.GEPRUEFT);
	//		final Gesuch verfuegtesTSGesuch = TestDataUtil.gesuchVerfuegen(tsGesuch, gesuchService);
	//
	//		final List<Gesuch> jaGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//			Constants.START_OF_TIME,
	//			Constants.END_OF_TIME,
	//			verfuegtesTSGesuch.getGesuchsperiode().getId(),
	//			true,
	//			false,
	//			false,
	//			false
	//		);
	//		Assert.assertTrue(jaGesuche.isEmpty());
	//
	//		final List<Gesuch> mischGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//			Constants.START_OF_TIME,
	//			Constants.END_OF_TIME,
	//			verfuegtesTSGesuch.getGesuchsperiode().getId(),
	//			false,
	//			true,
	//			false,
	//			false
	//		);
	//		Assert.assertTrue(mischGesuche.isEmpty());
	//
	//		final List<Gesuch> schGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//			Constants.START_OF_TIME,
	//			Constants.END_OF_TIME,
	//			verfuegtesTSGesuch.getGesuchsperiode().getId(),
	//			false,
	//			false,
	//			true,
	//			false
	//		);
	//		Assert.assertEquals(1, schGesuche.size());
	//		Assert.assertEquals(verfuegtesTSGesuch, schGesuche.get(0));
	//	}
	//
	//	/**
	//	 * tests that a TS-Gesuch is only returned when the method is called with inklTSGesuche==true
	//	 */
	//	@Test
	//	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeAngebotTypMischGesuch() {
	//		Gesuch tsGesuch = TestDataUtil.createAndPersistASIV12(institutionService, persistence,
	//			LocalDate.of(1980, Month.MARCH, 25), AntragStatus.GEPRUEFT);
	//		final Gesuch verfuegtesTSGesuch = TestDataUtil.gesuchVerfuegen(tsGesuch, gesuchService);
	//
	//		final List<Gesuch> jaGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//			Constants.START_OF_TIME,
	//			Constants.END_OF_TIME,
	//			verfuegtesTSGesuch.getGesuchsperiode().getId(),
	//			true,
	//			false,
	//			false,
	//			false
	//		);
	//		Assert.assertTrue(jaGesuche.isEmpty());
	//
	//		final List<Gesuch> mischGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//			Constants.START_OF_TIME,
	//			Constants.END_OF_TIME,
	//			verfuegtesTSGesuch.getGesuchsperiode().getId(),
	//			false,
	//			true,
	//			false,
	//			false
	//		);
	//		Assert.assertEquals(1, mischGesuche.size());
	//		Assert.assertEquals(verfuegtesTSGesuch, mischGesuche.get(0));
	//
	//		final List<Gesuch> schGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//			Constants.START_OF_TIME,
	//			Constants.END_OF_TIME,
	//			verfuegtesTSGesuch.getGesuchsperiode().getId(),
	//			false,
	//			false,
	//			true,
	//			false
	//		);
	//		Assert.assertTrue(schGesuche.isEmpty());
	//	}
	//
	//	/**
	//	 * tests that a MischGesuch is only returned when the method is called with inklMischGesuche==true
	//	 */
	//	@Test
	//	public void testGetGepruefteFreigegebeneGesucheForGesuchsperiodeAngebotNoGesuchTyp() {
	//		Gesuch mischGesuch = TestDataUtil.createAndPersistASIV12(institutionService, persistence,
	//			LocalDate.of(1980, Month.MARCH, 25), AntragStatus.GEPRUEFT);
	//		final Gesuch verfuegtesMischgesuch = TestDataUtil.gesuchVerfuegen(mischGesuch, gesuchService);
	//
	//		try {
	//			gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
	//				Constants.START_OF_TIME,
	//				Constants.END_OF_TIME,
	//				verfuegtesMischgesuch.getGesuchsperiode().getId(),
	//				false,
	//				false,
	//				false,
	//				false
	//			);
	//			Assert.fail("Should throw an exception because at least one Gesuchtyp must be selected");
	//		} catch(Exception e) {
	//			// nop
	//		}
	//	}
}
