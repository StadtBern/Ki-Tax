package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.resource.FallResource;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.InstitutionService;
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
import java.time.LocalDate;
import java.time.Month;

/**
 * Testet FallResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class FallResourceTest extends AbstractEbeguRestLoginTest {


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
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());
		final JaxFall foundFall = fallResource.findFall(converter.toJaxId(gesuch.getFall()));

		Assert.assertNotNull(foundFall);
		Assert.assertNull(foundFall.getVerantwortlicher());

		Assert.assertNotNull(foundFall.getId());
		Assert.assertNotNull(foundFall.getFallNummer());
		Assert.assertNotNull(foundFall.getNextNumberKind());
	}

	@Test
	public void testUpdateVerantwortlicherUserForFall() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());
		Benutzer sachbearbeiter = TestDataUtil.createAndPersistBenutzer(persistence);
		final JaxFall foundFall = fallResource.findFall(converter.toJaxId(gesuch.getFall()));

		Assert.assertNotNull(foundFall);
		Assert.assertNull(foundFall.getVerantwortlicher());

		JaxAuthLoginElement userToSet = converter.benutzerToAuthLoginElement(sachbearbeiter);
		foundFall.setVerantwortlicher(userToSet);
		JaxFall updatedFall = fallResource.saveFall(foundFall, null, null);
		Assert.assertNotNull(updatedFall.getVerantwortlicher());
		Assert.assertEquals(sachbearbeiter.getUsername(), updatedFall.getVerantwortlicher().getUsername());
	}

	// HELP METHODS

	private void changeStatusToWarten(KindContainer kindContainer) {
		for (Betreuung betreuung : kindContainer.getBetreuungen()) {
			betreuung.setBetreuungsstatus(Betreuungsstatus.WARTEN);
			persistence.merge(betreuung);
		}
	}
}
