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

import javax.inject.Inject;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests der die Konvertierung von Gesuchsperiode prueft
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeConverterTest extends AbstractEbeguRestLoginTest {


	@Inject
	private Persistence persistence;

	@Inject
	private JaxBConverter converter;


	/**
	 * transformiert eine gespeicherte Gesuchsperiode nach jax und wieder zurueck. wir erwarten dass Daten gleich bleiben
	 */
	@Test
	public void convertPersistedTestEntityToJax(){
		LocalDate date = LocalDate.now();
		Gesuchsperiode gesuchsperiode = createNewEntity(date, date);
		JaxGesuchsperiode jaxGesuchsperiode = this.converter.gesuchsperiodeToJAX(gesuchsperiode);
		Gesuchsperiode gesuchsperiodeToEntity = this.converter.gesuchsperiodeToEntity(jaxGesuchsperiode, new Gesuchsperiode());
		Assert.assertTrue(gesuchsperiode.isSame(gesuchsperiodeToEntity));
	}

	// HELP METHODS

	private Gesuchsperiode createNewEntity(LocalDate ab, LocalDate bis) {
		Gesuchsperiode gesuchsperiode = new Gesuchsperiode();
		gesuchsperiode.setGueltigkeit(new DateRange(ab, bis));
		gesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		return gesuchsperiode;
	}

}
