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

import java.util.List;

import javax.inject.Inject;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.resource.GesuchsperiodeResource;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testet die Gesuchsperiode Resource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private GesuchsperiodeResource gesuchsperiodeResource;

	@Inject
	private JaxBConverter converter;

	@Test
	public void createGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		// Gesuchsperiode muss zuerst als ENTWURF gespeichert werden
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.ENTWURF);
		JaxGesuchsperiode jaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		Assert.assertNotNull(jaxGesuchsperiode);
		Assert.assertEquals(testJaxGesuchsperiode.getStatus(), jaxGesuchsperiode.getStatus());

		findExistingObjectAndCompare(jaxGesuchsperiode);
	}

	@Test(expected = EbeguRuntimeException.class)
	public void createGesuchsperiodeAsAktivTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		// Gesuchsperiode muss zuerst als ENTWURF gespeichert werden
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
	}

	@Test
	public void removeGesuchsperiodeTest() {
		JaxGesuchsperiode testJaxGesuchsperiode = TestJaxDataUtil.createTestJaxGesuchsperiode();
		// Gesuchsperiode muss zuerst als ENTWURF gespeichert werden
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.ENTWURF);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.INAKTIV);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);
		testJaxGesuchsperiode.setStatus(GesuchsperiodeStatus.GESCHLOSSEN);
		testJaxGesuchsperiode = gesuchsperiodeResource.saveGesuchsperiode(testJaxGesuchsperiode, null, null);

		findExistingObjectAndCompare(testJaxGesuchsperiode);

		gesuchsperiodeResource.removeGesuchsperiode(converter.toJaxId(testJaxGesuchsperiode), null);

		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchsperiodeResource.findGesuchsperiode(converter.toJaxId(testJaxGesuchsperiode));
		Assert.assertNull(foundJaxGesuchsperiode);
	}

	@Test
	public void getAllGesuchsperiodenTest() {
		saveGesuchsperiodeInStatusEntwurf(TestJaxDataUtil.createTestJaxGesuchsperiode());
		saveGesuchsperiodeInStatusAktiv(TestJaxDataUtil.createTestJaxGesuchsperiode());
		saveGesuchsperiodeInStatusInaktiv(TestJaxDataUtil.createTestJaxGesuchsperiode());
		saveGesuchsperiodeInStatusGesperrt(TestJaxDataUtil.createTestJaxGesuchsperiode());

		List<JaxGesuchsperiode> listAll = gesuchsperiodeResource.getAllGesuchsperioden();
		Assert.assertNotNull(listAll);
		Assert.assertEquals(4, listAll.size());

		List<JaxGesuchsperiode> listActive = gesuchsperiodeResource.getAllActiveGesuchsperioden();
		Assert.assertNotNull(listActive);
		Assert.assertEquals(1, listActive.size());

		List<JaxGesuchsperiode> listActiveAndInaktiv = gesuchsperiodeResource.getAllNichtAbgeschlosseneGesuchsperioden();
		Assert.assertNotNull(listActiveAndInaktiv);
		Assert.assertEquals(2, listActiveAndInaktiv.size());
	}

	// HELP METHODS

	private void findExistingObjectAndCompare(JaxGesuchsperiode jaxGesuchsperiode) {
		JaxGesuchsperiode foundJaxGesuchsperiode = gesuchsperiodeResource.findGesuchsperiode(converter.toJaxId(jaxGesuchsperiode));
		Assert.assertNotNull(foundJaxGesuchsperiode);
		Assert.assertEquals(jaxGesuchsperiode, foundJaxGesuchsperiode);
	}
}
