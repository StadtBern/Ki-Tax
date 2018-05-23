/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Arquillian Tests fuer die Klasse BenutzerService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class BenutzerServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private Persistence persistence;

	@Test
	public void oneBerechtigung() {
		Benutzer benutzer = TestDataUtil.createDefaultBenutzer();
		persistence.merge(benutzer.getMandant());
		persistence.merge(benutzer);

		Set<Berechtigung> berechtigungen = benutzer.getBerechtigungen();
		Assert.assertNotNull(berechtigungen);
		Assert.assertEquals(1, berechtigungen.size());
	}

	@Test
	public void addBerechtigung() {
		LocalDate AB_ERSTE_BERECHTIGUNG = LocalDate.now();
		Benutzer benutzer = TestDataUtil.createDefaultBenutzer();
		benutzer.getCurrentBerechtigung().getGueltigkeit().setGueltigAb(AB_ERSTE_BERECHTIGUNG);
		persistence.merge(benutzer.getMandant());
		benutzer = persistence.merge(benutzer);
		Set<Berechtigung> berechtigungen = benutzer.getBerechtigungen();
		Assert.assertNotNull(berechtigungen);
		Assert.assertEquals(1, berechtigungen.size());
		Berechtigung firstBerechtigung = berechtigungen.iterator().next();
		Assert.assertEquals(AB_ERSTE_BERECHTIGUNG, firstBerechtigung.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, firstBerechtigung.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(UserRole.ADMIN, firstBerechtigung.getRole());

		// Eine zweite Berechtigung erfassen
		LocalDate AB_ZWEITE_BERECHTIGUNG = LocalDate.now().plusMonths(1);
		Berechtigung secondBerechtigung = new Berechtigung();
		secondBerechtigung.setBenutzer(benutzer);
		secondBerechtigung.setRole(UserRole.SACHBEARBEITER_JA);
		secondBerechtigung.getGueltigkeit().setGueltigAb(AB_ZWEITE_BERECHTIGUNG);
		benutzer.getBerechtigungen().add(secondBerechtigung);
		benutzer = benutzerService.saveBenutzer(benutzer);

		berechtigungen = benutzer.getBerechtigungen();
		Assert.assertNotNull(berechtigungen);
		Assert.assertEquals(2, berechtigungen.size());
		Iterator<Berechtigung> iterator = berechtigungen.iterator();
		firstBerechtigung = iterator.next();
		secondBerechtigung = iterator.next();

		Assert.assertEquals(AB_ERSTE_BERECHTIGUNG, firstBerechtigung.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(AB_ZWEITE_BERECHTIGUNG.minusDays(1), firstBerechtigung.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(AB_ZWEITE_BERECHTIGUNG, secondBerechtigung.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, secondBerechtigung.getGueltigkeit().getGueltigBis());
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void handleAbgelaufeneBerechtigung() {
		LocalDate AB_ERSTE_BERECHTIGUNG = LocalDate.now().minusYears(1);
		Benutzer benutzer = TestDataUtil.createDefaultBenutzer();
		benutzer.getCurrentBerechtigung().getGueltigkeit().setGueltigAb(AB_ERSTE_BERECHTIGUNG);
		persistence.merge(benutzer.getMandant());
		benutzer = persistence.merge(benutzer);

		// Timer durchlaufen lassen: Es ist immer noch dieselbe Berechtigung aktiv
		benutzerService.handleAbgelaufeneRollen(LocalDate.now());
		Optional<Benutzer> benutzerOptional = benutzerService.findBenutzer(benutzer.getUsername());
		Berechtigung currentBerechtigung = benutzerOptional.get().getCurrentBerechtigung();
		Assert.assertEquals(AB_ERSTE_BERECHTIGUNG, currentBerechtigung.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, currentBerechtigung.getGueltigkeit().getGueltigBis());

		// Eine zweite Berechtigung erfassen
		LocalDate AB_ZWEITE_BERECHTIGUNG = LocalDate.now().minusDays(1);
		Berechtigung secondBerechtigung = new Berechtigung();
		secondBerechtigung.setBenutzer(benutzer);
		secondBerechtigung.setRole(UserRole.SACHBEARBEITER_JA);
		secondBerechtigung.getGueltigkeit().setGueltigAb(AB_ZWEITE_BERECHTIGUNG);
		secondBerechtigung.getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		benutzer.getBerechtigungen().add(secondBerechtigung);
		benutzer = benutzerService.saveBenutzer(benutzer);
		Set<Berechtigung> berechtigungen = benutzer.getBerechtigungen();
		Assert.assertEquals(2, berechtigungen.size());

		// Timer durchlaufen lassen: Es ist jetzt die neue Berechtigung aktiv
		benutzerService.handleAbgelaufeneRollen(LocalDate.now());
		benutzerOptional = benutzerService.findBenutzer(benutzer.getUsername());
		currentBerechtigung = benutzerOptional.get().getCurrentBerechtigung();
		Assert.assertEquals(AB_ZWEITE_BERECHTIGUNG, currentBerechtigung.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, currentBerechtigung.getGueltigkeit().getGueltigBis());
	}
}
