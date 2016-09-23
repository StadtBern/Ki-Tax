package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.resource.FallResource;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
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

import javax.inject.Inject;

/**
 * Testet FallResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FallResourceTest extends AbstractEbeguRestTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private FallResource fallResource;
	@Inject
	private AuthService authService;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private JaxBConverter converter;


	@Test
	public void testFindGesuchForInstitution() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());
		TestDataUtil.createDummyAdminAnonymous(persistence);
//		persistUser(UserRole.SACHBEARBEITER_INSTITUTION,
//			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution(),
//			null, gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getMandant());
		final JaxFall foundFall = fallResource.findFall(converter.toJaxId(gesuch.getFall()));

		Assert.assertNotNull(foundFall);
		Assert.assertNull(foundFall.getVerantwortlicher());

		Assert.assertNotNull(foundFall.getId());
		Assert.assertNotNull(foundFall.getFallNummer());
		Assert.assertNotNull(foundFall.getNextNumberKind());

	}


	@Test
	public void testUpdateVerantwortlicherUserForFall() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());
		Benutzer admin = TestDataUtil.createDummyAdminAnonymous(persistence);
//		persistUser(UserRole.SACHBEARBEITER_INSTITUTION,
//			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution(),
//			null, gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getMandant());
		final JaxFall foundFall = fallResource.findFall(converter.toJaxId(gesuch.getFall()));

		Assert.assertNotNull(foundFall);
		Assert.assertNull(foundFall.getVerantwortlicher());

		JaxAuthLoginElement userToSet = converter.benutzerToAuthLoginElement(admin);
		foundFall.setVerantwortlicher(userToSet);
		JaxFall updatedFall = fallResource.saveFall(foundFall, null, null);
		Assert.assertNotNull(updatedFall.getVerantwortlicher());
		Assert.assertEquals(admin.getUsername(), updatedFall.getVerantwortlicher().getUsername());


	}




//	@Test
//	public void testUpdateStatus() throws EbeguException {
//		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence);
//		persistUser(UserRole.GESUCHSTELLER, null,
//			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getTraegerschaft(),
//			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getMandant());
//
//		Response response = fallResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.ERSTE_MAHNUNG);
//		final JaxGesuch persistedGesuch = fallResource.findGesuch(new JaxId(gesuch.getId()));
//
//		Assert.assertEquals(200, response.getStatus());
//		Assert.assertEquals(AntragStatusDTO.ERSTE_MAHNUNG, persistedGesuch.getStatus());
//	}


	// HELP METHODS

	private void persistUser(final UserRole role, final Institution institution, final Traegerschaft traegerschaft, final Mandant mandant) {
		Benutzer benutzer = TestDataUtil.createDefaultBenutzer();
		benutzer.setRole(role);
		benutzer.setUsername("anonymous");
		benutzer.setInstitution(institution);
		benutzer.setTraegerschaft(traegerschaft);
		benutzer.setMandant(mandant);
		benutzerService.saveBenutzer(benutzer);
	}

	private void changeStatusToWarten(KindContainer kindContainer) {
		for (Betreuung betreuung : kindContainer.getBetreuungen()) {
			betreuung.setBetreuungsstatus(Betreuungsstatus.WARTEN);
			persistence.merge(betreuung);
		}
	}
}
