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

package ch.dvbern.ebegu.rest.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.resource.GesuchResource;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testet GesuchResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private GesuchResource gesuchResource;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence persistence;
	@Inject
	private JaxBConverter converter;

	/**
	 * fuer diesen service logen wir uns immer als jemand anderes ein
	 */
	@Test
	public void testFindGesuchForInstitution() {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());
		persistUser(UserRole.SACHBEARBEITER_INSTITUTION, "sainst",
			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution(),
			null);
		final JaxGesuch gesuchForInstitution = gesuchResource.findGesuchForInstitution(converter.toJaxId(gesuch));

		Assert.assertNull(gesuchForInstitution.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuchForInstitution.getGesuchsteller1());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getErwerbspensenContainers());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getFinanzielleSituationContainer());

		Assert.assertNull(gesuchForInstitution.getGesuchsteller2()); //GS2 ist von Anfang an nicht gesetzt

		Assert.assertNotNull(gesuchForInstitution.getKindContainers());
		Assert.assertEquals(1, gesuchForInstitution.getKindContainers().size());

		final Iterator<JaxKindContainer> iterator = gesuchForInstitution.getKindContainers().iterator();
		final JaxKindContainer kind = iterator.next();
		Assert.assertNotNull(kind);
		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(1, kind.getBetreuungen().size());
	}

	@Test
	public void testFindGesuchForTraegerschaft() {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());

		persistUser(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "satraeg", null,
			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getTraegerschaft());

		final JaxGesuch gesuchForInstitution = gesuchResource.findGesuchForInstitution(converter.toJaxId(gesuch));

		Assert.assertNull(gesuchForInstitution.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuchForInstitution.getGesuchsteller1());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getErwerbspensenContainers());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getFinanzielleSituationContainer());

		Assert.assertNull(gesuchForInstitution.getGesuchsteller2()); //GS2 ist von Anfang an nicht gesetzt

		Assert.assertNotNull(gesuchForInstitution.getKindContainers());
		Assert.assertEquals(1, gesuchForInstitution.getKindContainers().size());

		final Iterator<JaxKindContainer> iterator = gesuchForInstitution.getKindContainers().iterator();
		final JaxKindContainer kind = iterator.next();
		Assert.assertNotNull(kind);
		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(1, kind.getBetreuungen().size());
	}

	@Test
	public void testFindGesuchForOtherRole() {
		persistUser(UserRole.GESUCHSTELLER, "gesuchst", null, null);
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, null);

		final JaxGesuch gesuchForInstitution = gesuchResource.findGesuchForInstitution(converter.toJaxId(gesuch));

		Assert.assertNull(gesuchForInstitution.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuchForInstitution.getGesuchsteller1());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getErwerbspensenContainers());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getFinanzielleSituationContainer());

		Assert.assertNull(gesuchForInstitution.getGesuchsteller2()); //GS2 ist von Anfang an nicht gesetzt

		Assert.assertNotNull(gesuchForInstitution.getKindContainers());
		Assert.assertEquals(1, gesuchForInstitution.getKindContainers().size());
	}

	@Test
	public void testUpdateStatus() {
		persistUser(UserRole.SACHBEARBEITER_JA, "saja", null, null);

		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Response response = gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.ERSTE_MAHNUNG);
		final JaxGesuch persistedGesuch = gesuchResource.findGesuch(new JaxId(gesuch.getId()));

		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals(AntragStatusDTO.ERSTE_MAHNUNG, persistedGesuch.getStatus());
	}

	@Test
	public void testAntragMutieren() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuch.setGueltig(true);
		gesuch.setTimestampVerfuegt(LocalDateTime.now());
		gesuch = persistence.merge(gesuch);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.GEPRUEFT);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGEN);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT).getEntity();

		final Response response = gesuchResource.antragMutieren(new JaxId(gesuch.getId()), LocalDate.now().toString(), null, null);

		Assert.assertNotNull(response);
		final Object entity = response.getEntity();
		Assert.assertNotNull(entity);
	}

	@Test
	public void testGesuchBySTVFreigeben_NotExistingGesuch() {
		try {
			gesuchResource.gesuchBySTVFreigeben(new JaxId("dfafdasf"), null, null);
			Assert.fail("Das Gesuch existiert nicht. Muss eine Exception werfen");
		} catch (EbeguEntityNotFoundException e) {
			//nop
		}
	}

	@Test
	public void testGesuchBySTVFreigeben() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.GEPRUEFT);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGEN);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.PRUEFUNG_STV);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.IN_BEARBEITUNG_STV);

		final Response response = gesuchResource.gesuchBySTVFreigeben(new JaxId(gesuch.getId()), null, null);

		final Object entity = response.getEntity();
		Assert.assertTrue(entity instanceof JaxGesuch);
		final JaxGesuch jaxGesuch = (JaxGesuch) entity;
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT_STV, jaxGesuch.getStatus());
		Assert.assertTrue(jaxGesuch.isGeprueftSTV());
	}

	@Test
	public void testGesuchBySTVFreigeben_NotInBearbeitungSTV() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		try {
			gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), null, null, null);
			Assert.fail("Das Gesuch ist nicht In Bearbeitung STV. Muss eine Exception werfen");
		} catch (EbeguRuntimeException e) {
			//nop
		}
	}

	@Test
	public void testSendGesuchToSTV_NotExistingGesuch() {
		try {
			gesuchResource.sendGesuchToSTV(new JaxId("dfafdasf"), null, null, null);
			Assert.fail("Das Gesuch existiert nicht. Muss eine Exception werfen");
		} catch (EbeguEntityNotFoundException e) {
			//nop
		}
	}

	@Test
	public void testSendGesuchToSTV_NotVerfuegt() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		try {
			gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), null, null, null);
			Assert.fail("Das Gesuch ist nicht verfuegt. Muss eine Exception werfen");
		} catch (EbeguRuntimeException e) {
			//nop
		}
	}

	@Test
	public void testSendGesuchToSTV_NullBemerkung() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.GEPRUEFT);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGEN);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT);

		final Response response = gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), null, null, null);

		final Object entity = response.getEntity();
		Assert.assertTrue(entity instanceof JaxGesuch);
		final JaxGesuch jaxGesuch = (JaxGesuch) entity;
		Assert.assertNull(jaxGesuch.getBemerkungenSTV());
		Assert.assertEquals(AntragStatusDTO.PRUEFUNG_STV, jaxGesuch.getStatus());
	}

	@Test
	public void testSendGesuchToSTV() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.GEPRUEFT);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGEN);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT);

		final Response response = gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), "bemerkSTV", null, null);

		final Object entity = response.getEntity();
		Assert.assertTrue(entity instanceof JaxGesuch);
		final JaxGesuch jaxGesuch = (JaxGesuch) entity;
		Assert.assertEquals("bemerkSTV", jaxGesuch.getBemerkungenSTV());
		Assert.assertEquals(AntragStatusDTO.PRUEFUNG_STV, jaxGesuch.getStatus());
	}

	// HELP METHODS

	private Benutzer persistUser(final UserRole role, final String username, final Institution institution, final Traegerschaft traegerschaft) {
		Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		Benutzer benutzer = TestDataUtil.createBenutzer(role, username, traegerschaft, institution, mandant);
		persistence.persist(benutzer);
		try {
			JBossLoginContextFactory.createLoginContext(username, username).login();
		} catch (LoginException e) {
			String message = "could not log in as user " + username;
			throw new RuntimeException(message, e);
		}
		return benutzer;
	}

	private void changeStatusToWarten(KindContainer kindContainer) {
		if (kindContainer != null && kindContainer.getBetreuungen() != null) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				betreuung.setBetreuungsstatus(Betreuungsstatus.WARTEN);
				persistence.merge(betreuung);
			}
		}
	}
}
