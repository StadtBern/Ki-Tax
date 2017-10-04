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

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.EinkommensverschlechterungResource;
import ch.dvbern.ebegu.api.resource.GesuchstellerResource;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

/**
 * Testet GesuchstellerResource
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class EinkommensverschlechterungResourceTest extends AbstractEbeguRestLoginTest {


	@Inject
	private GesuchstellerResource gesuchstellerResource;

	@Inject
	private EinkommensverschlechterungResource einkommensverschlechterungResource;

	@Inject
	private JaxBConverter converter;
	@Inject
	private Persistence persistence;

	private final UriInfo uri = new ResteasyUriInfo("test", "test", "test");

	@Test
	public void createAndFindEinkommensverschlechterungsContainerTest() throws EbeguException {
		Gesuch testGesuch = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(testGesuch, persistence);
		JaxGesuchstellerContainer testJaxGesuchsteller = TestJaxDataUtil.createTestJaxGesuchsteller();
		JaxGesuchstellerContainer jaxGesuchsteller = gesuchstellerResource.saveGesuchsteller(new JaxId(testGesuch.getId()), 1, false, testJaxGesuchsteller, uri, null);
		Assert.assertNotNull(jaxGesuchsteller);

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainer = TestJaxDataUtil.createTestJaxEinkommensverschlechterungContianer();

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainerReturned =
			(JaxEinkommensverschlechterungContainer) einkommensverschlechterungResource.
				saveEinkommensverschlechterungContainer(new JaxId(testGesuch.getId()), converter.toJaxId(jaxGesuchsteller),
					jaxEinkommensverschlechterungContainer, uri, null).getEntity();

		Assert.assertNotNull(jaxEinkommensverschlechterungContainerReturned);

		final JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerFound =
			einkommensverschlechterungResource.findEinkommensverschlechterungContainer(
				converter.toJaxId(jaxEinkommensverschlechterungContainerReturned));
		Assert.assertNotNull(einkommensverschlechterungContainerFound);

	}

}
