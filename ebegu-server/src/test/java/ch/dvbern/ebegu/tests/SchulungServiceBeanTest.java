package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private MandantService mandantService;

	@Inject
	private Persistence<AbstractEntity> persistence;


	@Test
	public void resetSchulungsdaten() throws Exception {
		Gesuchsperiode gesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(TestDataUtil.createCurrentGesuchsperiode());
		createAndSaveInstitutionStammdatenForTestfaelle();
		TestDataUtil.prepareParameters(gesuchsperiode.getGueltigkeit(), persistence);

		assertEmpty();
		schulungService.createSchulungsdaten();

		Assert.assertEquals(94, adresseService.getAllAdressen().size());
		Assert.assertEquals(6, institutionStammdatenService.getAllInstitutionStammdaten().size());
		Assert.assertEquals(4, institutionService.getAllInstitutionen().size());
		Assert.assertEquals(1, traegerschaftService.getAllTraegerschaften().size());
		Assert.assertEquals(anzahlUserSchonVorhanden+anzahlGesuchsteller+anzahlInstitutionsBenutzer, benutzerService.getAllBenutzer().size());

		schulungService.deleteSchulungsdaten();
		assertEmpty();
	}

	@Test
	public void deleteSchulungsdaten() {
		// Es muss auch "geloescht" werden koennen, wenn es schon (oder teilweise) geloescht ist
		schulungService.deleteSchulungsdaten();
		schulungService.deleteSchulungsdaten();
	}

	private void assertEmpty() {
		Assert.assertEquals(3, adresseService.getAllAdressen().size());
		Assert.assertEquals(3, institutionStammdatenService.getAllInstitutionStammdaten().size());
		Assert.assertEquals(2, institutionService.getAllInstitutionen().size());
		Assert.assertTrue(traegerschaftService.getAllTraegerschaften().isEmpty());
		Assert.assertEquals(anzahlUserSchonVorhanden, benutzerService.getAllBenutzer().size());
	}

	private List<InstitutionStammdaten> createAndSaveInstitutionStammdatenForTestfaelle() {
		Mandant mandant = mandantService.getFirst();
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
		for (InstitutionStammdaten institutionStammdaten : institutionStammdatenList) {
			institutionStammdaten.getInstitution().setTraegerschaft(null);
			institutionStammdaten.getInstitution().setMandant(mandant);
			if (!institutionService.findInstitution(institutionStammdaten.getInstitution().getId()).isPresent()) {
				institutionService.createInstitution(institutionStammdaten.getInstitution());
			}
			institutionStammdatenService.saveInstitutionStammdaten(institutionStammdaten);
		}
		return institutionStammdatenList;
	}
}
