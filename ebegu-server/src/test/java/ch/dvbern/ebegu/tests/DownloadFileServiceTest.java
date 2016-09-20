package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.DownloadFileService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests fuer die Klasse DokumentGrundService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class DownloadFileServiceTest extends AbstractEbeguTest {

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private DownloadFileService downloadFileService;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

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
