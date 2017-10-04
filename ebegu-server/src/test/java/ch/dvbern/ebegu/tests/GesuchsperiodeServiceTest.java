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

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
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
 * Arquillian Tests fuer die Klasse GesuchsperiodeService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private Persistence persistence;

	@Test
	public void createGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		insertNewEntity(true);

		Collection<Gesuchsperiode> allGesuchsperioden = gesuchsperiodeService.getAllGesuchsperioden();
		Assert.assertEquals(1, allGesuchsperioden.size());
		Gesuchsperiode nextGesuchsperiode = allGesuchsperioden.iterator().next();
		Assert.assertEquals(GesuchsperiodeStatus.AKTIV, nextGesuchsperiode.getStatus());
	}

	@Test
	public void updateGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		Gesuchsperiode insertedGesuchsperiode = insertNewEntity(true);
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(insertedGesuchsperiode.getId());
		Assert.assertEquals(GesuchsperiodeStatus.AKTIV, gesuchsperiode.get().getStatus());

		gesuchsperiode.get().setStatus(GesuchsperiodeStatus.GESCHLOSSEN);
		Gesuchsperiode updateGesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode.get());
		Assert.assertEquals(GesuchsperiodeStatus.GESCHLOSSEN, updateGesuchsperiode.getStatus());
		Assert.assertEquals(GesuchsperiodeStatus.GESCHLOSSEN, gesuchsperiodeService.findGesuchsperiode(updateGesuchsperiode.getId()).get().getStatus());
	}

	@Test
	public void removeGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		Assert.assertEquals(0, gesuchsperiodeService.getAllGesuchsperioden().size());

		Gesuchsperiode insertedGesuchsperiode = insertNewEntity(true);
		Assert.assertEquals(1, gesuchsperiodeService.getAllGesuchsperioden().size());
		insertedGesuchsperiode.setStatus(GesuchsperiodeStatus.GESCHLOSSEN);
		persistence.merge(insertedGesuchsperiode);

		gesuchsperiodeService.removeGesuchsperiode(insertedGesuchsperiode.getId());
		Assert.assertEquals(0, gesuchsperiodeService.getAllGesuchsperioden().size());
	}

	@Test
	public void getAllActiveGesuchsperiodenTest() {
		Gesuchsperiode insertedGesuchsperiode = insertNewEntity(true);
		insertNewEntity(false);

		Collection<Gesuchsperiode> allGesuchsperioden = gesuchsperiodeService.getAllGesuchsperioden();
		Assert.assertEquals(2, allGesuchsperioden.size());

		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		Assert.assertEquals(1, allActiveGesuchsperioden.size());
		Assert.assertEquals(insertedGesuchsperiode, allActiveGesuchsperioden.iterator().next());
	}

	// HELP METHODS

	private Gesuchsperiode insertNewEntity(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		if (active) {
			gesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		} else {
			gesuchsperiode.setStatus(GesuchsperiodeStatus.ENTWURF);
		}
		gesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}
}
