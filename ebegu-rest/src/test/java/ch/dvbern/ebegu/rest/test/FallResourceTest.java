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
import java.time.Month;

import javax.inject.Inject;

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
	private Persistence persistence;
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
