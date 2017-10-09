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
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.ErwerbspensumResource;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testet die Erwerbspensum Resource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private GesuchstellerResource gesuchstellerResource;
	@Inject
	private ErwerbspensumResource erwerbspensumResource;
	@Inject
	private Persistence persistence;
	@Inject
	private JaxBConverter converter;
	@Inject
	private EbeguParameterService ebeguParameterService;
	private JaxId gesuchJAXPId;

	@Before
	public void setUp() {
		final Gesuch testGesuch = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(testGesuch, persistence);
		gesuchJAXPId = new JaxId(testGesuch.getId());
	}

	@Test
	public void createGesuchstelelrWithErwerbspensumTest() throws EbeguException {
		JaxGesuchstellerContainer testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchstellerWithErwerbsbensum();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, testJaxGesuchsteller, null, null);
		Assert.assertNotNull(jaxGesuchsteller);

	}

	@Test
	public void createErwerbspensumTest() throws EbeguException {
		JaxGesuchstellerContainer jaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchstellerContainer gesuchsteller = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, jaxGesuchsteller, null, null);
		Response response = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(gesuchsteller), TestJaxDataUtil.createTestJaxErwerbspensumContainer(), null, null);
		JaxErwerbspensumContainer jaxErwerbspensum = (JaxErwerbspensumContainer) response.getEntity();
		Assert.assertNotNull(jaxErwerbspensum);

	}

	@Test
	public void updateGesuchstellerTest() throws EbeguException {
		JaxGesuchstellerContainer jaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchstellerContainer storedGS = gesuchstellerResource.saveGesuchsteller(gesuchJAXPId, 1, false, jaxGesuchsteller, null, null);
		Response response = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(storedGS), TestJaxDataUtil.createTestJaxErwerbspensumContainer(), null, null);
		JaxErwerbspensumContainer jaxErwerbspensum = (JaxErwerbspensumContainer) response.getEntity();
		JaxErwerbspensumContainer loadedEwp = erwerbspensumResource.findErwerbspensum(converter.toJaxId(jaxErwerbspensum));
		Assert.assertNotNull(loadedEwp);
		Assert.assertNotEquals(Integer.valueOf(20), loadedEwp.getErwerbspensumGS().getZuschlagsprozent());

		jaxErwerbspensum.getErwerbspensumGS().setZuschlagsprozent(18);
		Response result = erwerbspensumResource.saveErwerbspensum(gesuchJAXPId, converter.toJaxId(storedGS), jaxErwerbspensum, null, null);
		JaxErwerbspensumContainer updatedContainer = (JaxErwerbspensumContainer) result.getEntity();
		Assert.assertEquals(Integer.valueOf(18), updatedContainer.getErwerbspensumGS().getZuschlagsprozent());

	}

}
