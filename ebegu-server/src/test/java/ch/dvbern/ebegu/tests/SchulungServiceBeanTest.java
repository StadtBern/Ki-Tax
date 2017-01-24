package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.services.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Test fuer den SchulungsService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class SchulungServiceBeanTest extends AbstractEbeguLoginTest {

	private int anzahlUserSchonVorhanden = 1;
	private int anzahlGesuchsteller = 15;
	private int anzahlInstitutionsBenutzer = 2;

	@Inject
	private SchulungService schulungService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private AdresseService adresseService;

	@Inject
	private BenutzerService benutzerService;


	@Test
	public void resetSchulungsdaten() throws Exception {
		assertEmpty();
		schulungService.createSchulungsdaten();

		Assert.assertEquals(3, adresseService.getAllAdressen().size());
		Assert.assertEquals(3, institutionStammdatenService.getAllInstitutionStammdaten().size());
		Assert.assertEquals(2, institutionService.getAllInstitutionen().size());
		Assert.assertEquals(1, traegerschaftService.getAllTraegerschaften().size());
		Assert.assertEquals(anzahlUserSchonVorhanden+anzahlGesuchsteller+anzahlInstitutionsBenutzer, benutzerService.getAllBenutzer().size());

		schulungService.deleteSchulungsdaten();
		assertEmpty();
	}

	private void assertEmpty() {
		Assert.assertTrue(adresseService.getAllAdressen().isEmpty());
		Assert.assertTrue(institutionStammdatenService.getAllInstitutionStammdaten().isEmpty());
		Assert.assertTrue(institutionService.getAllInstitutionen().isEmpty());
		Assert.assertTrue(traegerschaftService.getAllTraegerschaften().isEmpty());
		Assert.assertEquals(anzahlUserSchonVorhanden, benutzerService.getAllBenutzer().size());
	}
}
