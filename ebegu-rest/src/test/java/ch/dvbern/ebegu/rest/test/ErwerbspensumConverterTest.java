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

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests der die Konvertierung von Erwerbspensen prueft
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumConverterTest extends AbstractEbeguRestLoginTest {

	@Inject
	private Persistence persistence;

	@Inject
	private JaxBConverter converter;

	/**
	 * transformiert einen gespeichertes Erwerbspensum nach jax und wieder zurueck. wir erwarten das Daten gleich bleiben
	 */
	@Test
	public void convertPersistedTestEntityToJax() {
		ErwerbspensumContainer erwerbspensumContainer = insertNewEntity();
		JaxErwerbspensumContainer jaxErwerbspensum = this.converter.erwerbspensumContainerToJAX(erwerbspensumContainer);
		ErwerbspensumContainer ewbContEntity = this.converter.erwerbspensumContainerToEntity(jaxErwerbspensum, new ErwerbspensumContainer());
		Assert.assertTrue(erwerbspensumContainer.isSame(ewbContEntity));

	}

	/**
	 * Testet konviertiert einen gesuchsteller mit Erwerbspensen
	 */
	@Test
	public void convertJaxGesuchstellerErwerbspensen() {
		JaxGesuchstellerContainer gesuchstellerWithErwerbspensen = TestJaxDataUtil.createTestJaxGesuchstellerWithErwerbsbensum();
		GesuchstellerContainer gesuchsteller = converter.gesuchstellerContainerToEntity(gesuchstellerWithErwerbspensen, new GesuchstellerContainer());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGesuchstellerJA().getGeburtsdatum(), gesuchsteller.getGesuchstellerJA().getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGesuchstellerJA().getVorname(), gesuchsteller.getGesuchstellerJA().getVorname());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGesuchstellerJA().getNachname(), gesuchsteller.getGesuchstellerJA().getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWithErwerbspensen.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(2, gesuchsteller.getErwerbspensenContainers().size());
		gesuchsteller = persistence.persist(gesuchsteller);
		JaxGesuchstellerContainer reconvertedJaxGesuchsteller = converter.gesuchstellerContainerToJAX(gesuchsteller);
		Assert.assertEquals(2, reconvertedJaxGesuchsteller.getErwerbspensenContainers().size());
	}

	private ErwerbspensumContainer insertNewEntity() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		ErwerbspensumContainer ewpContainer = TestDataUtil.createErwerbspensumContainer();
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		ewpContainer.setGesuchsteller(persistence.persist(gesuchsteller));
		return persistence.persist(ewpContainer);
	}

}
