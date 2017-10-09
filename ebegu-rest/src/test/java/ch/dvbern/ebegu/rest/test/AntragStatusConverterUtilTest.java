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

package ch.dvbern.ebegu.rest.test;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests der die Konvertierung vom AntragStatus
 */
public class AntragStatusConverterUtilTest {

	@Test
	public void convertStatusToDTOGEPRUEFTTest() {
		Gesuch gesuch = TestDataUtil.createTestgesuchDagmar(); // by default
		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOAlleBestaetigtTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus.ALLE_BESTAETIGT);

		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsAbgewiesenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus.ABGEWIESEN);

		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsWartenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus.WARTEN);

		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToEntityGEPRUEFTTest() {
		Assert.assertEquals(AntragStatus.GEPRUEFT, AntragStatusConverterUtil.convertStatusToEntity(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN));
		Assert.assertEquals(AntragStatus.GEPRUEFT, AntragStatusConverterUtil.convertStatusToEntity(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN));
		Assert.assertEquals(AntragStatus.GEPRUEFT, AntragStatusConverterUtil.convertStatusToEntity(AntragStatusDTO.GEPRUEFT));
	}

	// HELP METHODS

	@Nonnull
	private Gesuch prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus status) {
		final Gesuch gesuch = TestDataUtil.createTestgesuchDagmar();
		gesuch.setGesuchBetreuungenStatus(status);
		return gesuch;
	}
}
