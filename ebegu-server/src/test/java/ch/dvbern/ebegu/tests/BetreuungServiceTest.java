package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.services.BetreuungService;
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
import java.util.Optional;

/**
 * Tests fuer die Klasse betreuungService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class BetreuungServiceTest extends AbstractEbeguTest {

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private Persistence<Betreuung> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createAndUpdateBetreuungTest() {
		Assert.assertNotNull(betreuungService);
		Betreuung persitedBetreuung = persistBetreuung();
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertTrue(betreuung.isPresent());
		Betreuung savedBetreuung = betreuung.get();
		Assert.assertEquals(persitedBetreuung.getBemerkungen(), savedBetreuung.getBemerkungen());
		Assert.assertEquals(persitedBetreuung.getBetreuungsstatus(), savedBetreuung.getBetreuungsstatus());

		Assert.assertNotEquals("Neue Bemerkung", savedBetreuung.getBemerkungen());
		savedBetreuung.setBemerkungen("Neue Bemerkung");
		betreuungService.saveBetreuung(savedBetreuung);
		Optional<Betreuung> updatedBetreuung = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertTrue(updatedBetreuung.isPresent());
		Assert.assertEquals("Neue Bemerkung", updatedBetreuung.get().getBemerkungen());
	}

	@Test
	public void removeBetreuungTest() {
		Assert.assertNotNull(betreuungService);
		Betreuung persitedBetreuung = persistBetreuung();
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertTrue(betreuung.isPresent());
		betreuungService.removeBetreuung(betreuung.get().getId());
		Optional<Betreuung> betreuungAfterRemove = betreuungService.findBetreuung(persitedBetreuung.getId());
		Assert.assertFalse(betreuungAfterRemove.isPresent());
	}

	// HELP

	private Betreuung persistBetreuung() {
		Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		for (BetreuungspensumContainer container : betreuung.getBetreuungspensumContainers()) {
			persistence.persist(container);
		}
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getTraegerschaft());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getMandant());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution());
		persistence.persist(betreuung.getInstitutionStammdaten());
		persistence.persist(betreuung.getKind().getKindGS().getPensumFachstelle().getFachstelle());
		persistence.persist(betreuung.getKind().getKindJA().getPensumFachstelle().getFachstelle());

		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch);
		betreuung.getKind().setGesuch(gesuch);
		persistence.persist(betreuung.getKind());

		betreuungService.saveBetreuung(betreuung);

		return betreuung;

	}

}
