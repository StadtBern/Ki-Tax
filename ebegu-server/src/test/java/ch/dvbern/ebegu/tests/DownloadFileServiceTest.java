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

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.DownloadFileService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse DokumentGrundService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class DownloadFileServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private Persistence persistence;

	@Inject
	private DownloadFileService downloadFileService;

	@Test
	public void createAndFindTempDokument() {

		DokumentGrund dokumentGrund = TestDataUtil.createDefaultDokumentGrund();
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		dokumentGrund.setGesuch(gesuch);

		persistence.persist(dokumentGrund);

		final Dokument dokument = dokumentGrund.getDokumente().iterator().next();

		Assert.assertNotNull(dokument);

		final DownloadFile downloadFile = downloadFileService.create(dokument, "1.2.3.4");

		Assert.assertNotNull(dokument);

		final DownloadFile tempDownloadByAccessToken = downloadFileService.getDownloadFileByAccessToken(downloadFile.getAccessToken());

		Assert.assertEquals(downloadFile, tempDownloadByAccessToken);

	}

}
