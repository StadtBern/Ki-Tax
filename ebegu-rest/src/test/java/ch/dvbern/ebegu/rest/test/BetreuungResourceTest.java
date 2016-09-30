package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.api.resource.*;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.PensumFachstelleService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.Set;

/**
 * Testet BetreuungResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class BetreuungResourceTest extends AbstractEbeguRestTest {

	private static final UriInfo RESTEASY_URI_INFO = new ResteasyUriInfo("test", "test", "test");

	@Inject
	private BetreuungService betreuungService;
	@Inject
	private BetreuungResource betreuungResource;
	@Inject
	private KindResource kindResource;
	@Inject
	private GesuchResource gesuchResource;
	@Inject
	private GesuchsperiodeResource gesuchsperiodeResource;
	@Inject
	private FallResource fallResource;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private FachstelleResource fachstelleResource;
	@Inject
	private PensumFachstelleService pensumFachstelleService;
	@Inject
	private JaxBConverter converter;
	@Inject
	private Persistence<?> persistence;


	@Test
	public void createBetreuung() throws EbeguException {
		TestDataUtil.createDummyAdminAnonymous(persistence);
		KindContainer returnedKind = persistKindAndDependingObjects(RESTEASY_URI_INFO);
		Betreuung testBetreuung = TestDataUtil.createDefaultBetreuung();
		persistStammdaten(testBetreuung.getInstitutionStammdaten());
		JaxBetreuung testJaxBetreuung = converter.betreuungToJAX(testBetreuung);

		JaxBetreuung jaxBetreuung = betreuungResource.saveBetreuung(converter.toJaxId(returnedKind), testJaxBetreuung, RESTEASY_URI_INFO, null);
		Assert.assertEquals(new Integer(1), jaxBetreuung.getBetreuungNummer());
		Assert.assertNotNull(jaxBetreuung);
	}

	private void persistStammdaten(InstitutionStammdaten institutionStammdaten) {
		persistence.persist(institutionStammdaten.getInstitution().getMandant());
		persistence.persist(institutionStammdaten.getInstitution().getTraegerschaft());
		persistence.persist(institutionStammdaten.getInstitution());
		persistence.persist(institutionStammdaten);
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void updateBetreuungTest() throws EbeguException {
		Betreuung initialBetr = this.storeInitialBetreung();
		//im moment haben wir kein find fuer einen einzelnen Container
		Set<JaxBetreuung> betreuungenBeforeUpdate = kindResource.findKind(converter.toJaxId(initialBetr.getKind())).getBetreuungen();
		Assert.assertEquals(1, betreuungenBeforeUpdate.size());
		JaxBetreuung betreuung = betreuungenBeforeUpdate.iterator().next();
		Assert.assertEquals(0, betreuung.getBetreuungspensumContainers().size());

		JaxBetreuungspensumContainer containerToAdd = TestJaxDataUtil.createBetreuungspensumContainer(LocalDate.now().getYear());

		betreuung.getBetreuungspensumContainers().add(containerToAdd);
		JaxBetreuung updatedBetr = betreuungResource.saveBetreuung(converter.toJaxId(initialBetr.getKind()), betreuung, RESTEASY_URI_INFO, null);
		Assert.assertEquals(1, updatedBetr.getBetreuungspensumContainers().size());
		Assert.assertEquals(Integer.valueOf(1), updatedBetr.getBetreuungNummer());
		checkNextNumberBetreuung(converter.toJaxId(initialBetr.getKind()), Integer.valueOf(2));
	}

	/**
	 * Testet, dass das entfernen eines Betreuungspensums auf dem Client dieses aus der Liste auf dem Server loescht.
	 * @throws EbeguException
	 */
	@Test
	public void updateShouldRemoveBetreuungspensumContainerTest() throws EbeguException {
		Betreuung initialBetr = this.storeInitialBetreung();
		//im moment haben wir kein find fuer einen einzelnen Container
		Set<JaxBetreuung> betreuungenBeforeUpdate = kindResource.findKind(converter.toJaxId(initialBetr.getKind())).getBetreuungen();
		Assert.assertEquals(1, betreuungenBeforeUpdate.size());
		JaxBetreuung betreuung = betreuungenBeforeUpdate.iterator().next();

		betreuung.getBetreuungspensumContainers().add(TestJaxDataUtil.createBetreuungspensumContainer(LocalDate.now().minusYears(1).getYear()));
		betreuung.getBetreuungspensumContainers().add(TestJaxDataUtil.createBetreuungspensumContainer(LocalDate.now().getYear()));
		betreuung.getBetreuungspensumContainers().add(TestJaxDataUtil.createBetreuungspensumContainer(LocalDate.now().plusYears(1).getYear()));
		JaxBetreuung updatedBetr = betreuungResource.saveBetreuung(converter.toJaxId(initialBetr.getKind()), betreuung, RESTEASY_URI_INFO, null);

		Assert.assertNotNull(updatedBetr.getBetreuungspensumContainers());
		Assert.assertEquals(3, updatedBetr.getBetreuungspensumContainers().size());
		Assert.assertEquals(new Integer(1), updatedBetr.getBetreuungNummer());
		checkNextNumberBetreuung(converter.toJaxId(initialBetr.getKind()), Integer.valueOf(2));

		updatedBetr.getBetreuungspensumContainers().clear(); //alle bestehenden entfernen
		updatedBetr.getBetreuungspensumContainers().add(TestJaxDataUtil.createBetreuungspensumContainer(LocalDate.now().plusYears(2).getYear())); //einen neuen einfuegen

		updatedBetr = betreuungResource.saveBetreuung(converter.toJaxId(initialBetr.getKind()), updatedBetr, RESTEASY_URI_INFO, null);
		Assert.assertEquals(1, updatedBetr.getBetreuungspensumContainers().size());
		Assert.assertEquals(new Integer(1), updatedBetr.getBetreuungNummer());
		checkNextNumberBetreuung(converter.toJaxId(initialBetr.getKind()), Integer.valueOf(2));

	}


	// HELP

	private KindContainer persistKindAndDependingObjects(UriInfo uri) throws EbeguException {
		JaxGesuch jaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		Mandant persistedMandant = persistence.persist(converter.mandantToEntity(TestJaxDataUtil.createTestMandant(), new Mandant()));
		jaxGesuch.getFall().getVerantwortlicher().setMandant(converter.mandantToJAX(persistedMandant));
		benutzerService.saveBenutzer(converter.authLoginElementToBenutzer(jaxGesuch.getFall().getVerantwortlicher(), new Benutzer()));
		JaxFall returnedFall = fallResource.saveFall(jaxGesuch.getFall(), uri, null);
		jaxGesuch.setGesuchsperiode(gesuchsperiodeResource.saveGesuchsperiode(jaxGesuch.getGesuchsperiode(), uri, null));
		jaxGesuch.setFall(returnedFall);
		JaxGesuch returnedGesuch = (JaxGesuch) gesuchResource.create(jaxGesuch, uri, null).getEntity();

		KindContainer returnedKind = TestDataUtil.createDefaultKindContainer();
		JaxKindContainer jaxKind = converter.kindContainerToJAX(returnedKind);
		JaxPensumFachstelle jaxPensumFachstelle = jaxKind.getKindGS().getPensumFachstelle();
		jaxPensumFachstelle.setFachstelle(fachstelleResource.saveFachstelle(jaxPensumFachstelle.getFachstelle(), null, null));
		PensumFachstelle returnedPensumFachstelle = pensumFachstelleService.savePensumFachstelle(
			converter.pensumFachstelleToEntity(jaxPensumFachstelle, new PensumFachstelle()));
		JaxPensumFachstelle convertedPensumFachstelle = converter.pensumFachstelleToJax(returnedPensumFachstelle);
		jaxKind.getKindGS().setPensumFachstelle(convertedPensumFachstelle);
		jaxKind.getKindJA().setPensumFachstelle(convertedPensumFachstelle);
		kindResource.saveKind(converter.toJaxId(returnedGesuch), jaxKind, uri, null);
		return returnedKind;
	}

	private Betreuung storeInitialBetreung() throws EbeguException {
		TestDataUtil.createDummyAdminAnonymous(persistence);
		KindContainer returnedKind = persistKindAndDependingObjects(RESTEASY_URI_INFO);
		Betreuung testBetreuung = TestDataUtil.createDefaultBetreuung();
		persistStammdaten(testBetreuung.getInstitutionStammdaten());
		testBetreuung.setKind(returnedKind);
		Betreuung betreuung = betreuungService.saveBetreuung(testBetreuung);
		checkNextNumberBetreuung(converter.toJaxId(betreuung.getKind()), Integer.valueOf(2));
		return betreuung;
	}

	private void checkNextNumberBetreuung(JaxId kindId, Integer number) throws EbeguException {
		final JaxKindContainer updatedKind = kindResource.findKind(kindId);
		Assert.assertEquals(number, updatedKind.getNextNumberBetreuung());
	}
}
