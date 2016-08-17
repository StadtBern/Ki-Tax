package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.TempDokument;
import ch.dvbern.ebegu.services.TempDokumentService;
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
public class TempDokumentServiceTest extends AbstractEbeguTest {

	@Inject
	private Persistence<DokumentGrund> persistence;

	@Inject
	private TempDokumentService tempDokumentService;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createAndFindTempDokument() {


		DokumentGrund dokumentGrund = TestDataUtil.createDefaultDokumentGrund();
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		persistence.persist(gesuch.getGesuchsperiode());
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch);
		dokumentGrund.setGesuch(gesuch);

		persistence.persist(dokumentGrund);

		final Dokument dokument = dokumentGrund.getDokumente().iterator().next();

		Assert.assertNotNull(dokument);

		final TempDokument tempDokument = tempDokumentService.create(dokument, "1.2.3.4");

		Assert.assertNotNull(dokument);

		final TempDokument tempDownloadByAccessToken = tempDokumentService.getTempDownloadByAccessToken(tempDokument.getAccessToken());

		Assert.assertEquals(tempDokument, tempDownloadByAccessToken);

	}

}
