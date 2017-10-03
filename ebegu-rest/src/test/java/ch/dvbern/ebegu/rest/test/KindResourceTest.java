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

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.dtos.JaxPensumFachstelle;
import ch.dvbern.ebegu.api.resource.FachstelleResource;
import ch.dvbern.ebegu.api.resource.FallResource;
import ch.dvbern.ebegu.api.resource.GesuchResource;
import ch.dvbern.ebegu.api.resource.GesuchsperiodeResource;
import ch.dvbern.ebegu.api.resource.KindResource;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.PensumFachstelleService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testet KindResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class KindResourceTest extends AbstractEbeguRestLoginTest {


	@Inject
	private KindResource kindResource;
	@Inject
	private GesuchResource gesuchResource;
	@Inject
	private GesuchsperiodeResource gesuchsperiodeResource;
	@Inject
	private FallResource fallResource;
	@Inject
	private FachstelleResource fachstelleResource;
	@Inject
	private PensumFachstelleService pensumFachstelleService;
	@Inject
	private BenutzerService benutzerService;

	@Inject
	private JaxBConverter converter;
	@Inject
	private Persistence persistence;


	@Test
	public void createKindTest() throws EbeguException {
		UriInfo uri = new ResteasyUriInfo("test", "test", "test");
		JaxGesuch jaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		Mandant persistedMandant = persistence.persist(converter.mandantToEntity(TestJaxDataUtil.createTestMandant(), new Mandant()));
		jaxGesuch.getFall().getVerantwortlicher().setMandant(converter.mandantToJAX(persistedMandant));
		benutzerService.saveBenutzer(converter.authLoginElementToBenutzer(jaxGesuch.getFall().getVerantwortlicher(), new Benutzer()));
		JaxFall returnedFall = fallResource.saveFall(jaxGesuch.getFall(), uri, null);
		JaxGesuchsperiode returnedGesuchsperiode = saveGesuchsperiodeInStatusAktiv(jaxGesuch.getGesuchsperiode());
		jaxGesuch.setFall(returnedFall);
		jaxGesuch.setGesuchsperiode(returnedGesuchsperiode);
		JaxGesuch returnedGesuch = (JaxGesuch) gesuchResource.create(jaxGesuch, uri, null).getEntity();

		JaxKindContainer testJaxKindContainer = TestJaxDataUtil.createTestJaxKindContainer();
		JaxPensumFachstelle jaxPensumFachstelle = testJaxKindContainer.getKindGS().getPensumFachstelle();
		jaxPensumFachstelle.setFachstelle(fachstelleResource.saveFachstelle(jaxPensumFachstelle.getFachstelle(), null, null));
		PensumFachstelle returnedPensumFachstelle = pensumFachstelleService.savePensumFachstelle(
			converter.pensumFachstelleToEntity(jaxPensumFachstelle, new PensumFachstelle()));
		JaxPensumFachstelle convertedPensumFachstelle = converter.pensumFachstelleToJax(returnedPensumFachstelle);
		testJaxKindContainer.getKindGS().setPensumFachstelle(convertedPensumFachstelle);
		testJaxKindContainer.getKindJA().setPensumFachstelle(convertedPensumFachstelle);

		JaxKindContainer jaxKindContainer = kindResource.saveKind(converter.toJaxId(returnedGesuch), testJaxKindContainer, null, null);

		Assert.assertNotNull(jaxKindContainer);
		Assert.assertEquals(Integer.valueOf(1), jaxKindContainer.getKindNummer());
		Assert.assertEquals(Integer.valueOf(1), jaxKindContainer.getNextNumberBetreuung());

		JaxGesuch updatedGesuch = gesuchResource.findGesuch(converter.toJaxId(returnedGesuch));
		Assert.assertEquals(Integer.valueOf(2), updatedGesuch.getFall().getNextNumberKind());
		Assert.assertEquals(1, updatedGesuch.getKindContainers().size());
		Assert.assertEquals(testJaxKindContainer.getKindGS().getPensumFachstelle().getPensum(), jaxKindContainer.getKindGS().getPensumFachstelle().getPensum());
	}
}
