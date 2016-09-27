package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.services.GeneratedDokumentService;
import ch.dvbern.ebegu.services.InstitutionService;
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

import javax.activation.MimeTypeParseException;
import javax.inject.Inject;

/**
 * Testet GeneratedDokumentService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GeneratedDokumentServiceTest extends AbstractEbeguTest {

	@Inject
	private GeneratedDokumentService generatedDokumentService;
	@Inject
	private InstitutionService instService;
	@Inject
	private Persistence<Gesuch> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void findGeneratedDokumentTest() {
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		final GeneratedDokument dokument = TestDataUtil.createGeneratedDokument(gesuch);
		persistence.persist(dokument);

		generatedDokumentService.saveGeneratedDokument(dokument);

		final GeneratedDokument generatedDokument = generatedDokumentService.findGeneratedDokument(gesuch.getId(), dokument.getFilename(), dokument.getFilepfad());

		Assert.assertNotNull(generatedDokument);
		Assert.assertEquals(dokument, generatedDokument);
	}

	@Test
	public void updateGeneratedDokumentTest() throws MimeTypeParseException {
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence);
		final GeneratedDokument dokument = TestDataUtil.createGeneratedDokument(gesuch);
		persistence.persist(dokument);

		generatedDokumentService.saveGeneratedDokument(dokument);

		byte[] data = new byte[0];
		final String newFileName = "Newname.pdf";
		final GeneratedDokument generatedDokument = generatedDokumentService
			.updateGeneratedDokument(data, GeneratedDokumentTyp.BEGLEITSCHREIBEN, gesuch, newFileName);

		Assert.assertNotNull(generatedDokument);
		Assert.assertEquals(newFileName, generatedDokument.getFilename());
	}
}
