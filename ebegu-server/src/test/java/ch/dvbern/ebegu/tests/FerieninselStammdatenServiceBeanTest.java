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

package ch.dvbern.ebegu.tests;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.FerieninselStammdaten;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.Ferienname;
import ch.dvbern.ebegu.services.FerieninselStammdatenService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FerieninselStammdatenServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private FerieninselStammdatenService ferieninselStammdatenService;

	@Inject
	private Persistence persistence;

	@Test
	public void saveFerieninselStammdaten() throws Exception {
		Assert.assertNotNull(ferieninselStammdatenService);
		FerieninselStammdaten ferieninselStammdaten = createFerieninselStammdaten();

		Collection<FerieninselStammdaten> allStammdaten = ferieninselStammdatenService.getAllFerieninselStammdaten();
		Assert.assertEquals(1, allStammdaten.size());
		FerieninselStammdaten firstStammdaten = allStammdaten.iterator().next();
		Assert.assertEquals(Ferienname.SOMMERFERIEN, firstStammdaten.getFerienname());

		firstStammdaten.setFerienname(Ferienname.HERBSTFERIEN);
		ferieninselStammdatenService.saveFerieninselStammdaten(firstStammdaten);

		allStammdaten = ferieninselStammdatenService.getAllFerieninselStammdaten();
		firstStammdaten = allStammdaten.iterator().next();
		Assert.assertEquals(Ferienname.HERBSTFERIEN, firstStammdaten.getFerienname());
	}

	@Test
	public void findFerieninselStammdaten() throws Exception {
		FerieninselStammdaten ferieninselStammdaten = createFerieninselStammdaten();
		Optional<FerieninselStammdaten> readStammdatenOptional = ferieninselStammdatenService.findFerieninselStammdaten(ferieninselStammdaten.getId());
		Assert.assertTrue(readStammdatenOptional.isPresent());
		Assert.assertEquals(readStammdatenOptional.get(), ferieninselStammdaten);
	}

	@Test
	public void getAllFerieninselStammdaten() throws Exception {
		createFerieninselStammdaten();
		Collection<FerieninselStammdaten> allStammdaten = ferieninselStammdatenService.getAllFerieninselStammdaten();
		Assert.assertNotNull(allStammdaten);
		Assert.assertEquals(1, allStammdaten.size());
		createFerieninselStammdaten();
		createFerieninselStammdaten();
		allStammdaten = ferieninselStammdatenService.getAllFerieninselStammdaten();
		Assert.assertNotNull(allStammdaten);
		Assert.assertEquals(3, allStammdaten.size());
	}

	@Test
	public void getFerieninselStammdatenForGesuchsperiode() throws Exception {
		Gesuchsperiode gp2017 = TestDataUtil.createCustomGesuchsperiode(2017, 2018);
		Gesuchsperiode gp2018 = TestDataUtil.createCustomGesuchsperiode(2018, 2019);
		gp2017 = persistence.merge(gp2017);
		gp2018 = persistence.merge(gp2018);

		ferieninselStammdatenService.saveFerieninselStammdaten(TestDataUtil.createDefaultFerieninselStammdaten(gp2017));
		ferieninselStammdatenService.saveFerieninselStammdaten(TestDataUtil.createDefaultFerieninselStammdaten(gp2017));
		ferieninselStammdatenService.saveFerieninselStammdaten(TestDataUtil.createDefaultFerieninselStammdaten(gp2018));

		Collection<FerieninselStammdaten> gp17Stammdaten = ferieninselStammdatenService.
			findFerieninselStammdatenForGesuchsperiode(gp2017.getId());
		Assert.assertNotNull(gp17Stammdaten);
		Assert.assertEquals(2, gp17Stammdaten.size());

		Collection<FerieninselStammdaten> gp18Stammdaten = ferieninselStammdatenService.
			findFerieninselStammdatenForGesuchsperiode(gp2018.getId());
		Assert.assertNotNull(gp18Stammdaten);
		Assert.assertEquals(1, gp18Stammdaten.size());
	}

	@Test
	public void removeFerieninselStammdaten() throws Exception {
		FerieninselStammdaten persistedStammdaten = createFerieninselStammdaten();
		Collection<FerieninselStammdaten> allStammdaten = ferieninselStammdatenService.getAllFerieninselStammdaten();
		Assert.assertNotNull(allStammdaten);
		Assert.assertEquals(1, allStammdaten.size());

		ferieninselStammdatenService.removeFerieninselStammdaten(persistedStammdaten.getId());
		allStammdaten = ferieninselStammdatenService.getAllFerieninselStammdaten();
		Assert.assertNotNull(allStammdaten);
		Assert.assertEquals(0, allStammdaten.size());
	}

	private FerieninselStammdaten createFerieninselStammdaten() {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createCurrentGesuchsperiode();
		gesuchsperiode = persistence.merge(gesuchsperiode);
		FerieninselStammdaten ferieninselStammdaten = TestDataUtil.createDefaultFerieninselStammdaten(gesuchsperiode);
		return ferieninselStammdatenService.saveFerieninselStammdaten(ferieninselStammdaten);
	}
}
