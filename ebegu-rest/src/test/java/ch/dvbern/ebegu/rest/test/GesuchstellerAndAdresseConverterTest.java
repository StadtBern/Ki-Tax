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
import ch.dvbern.ebegu.api.dtos.JaxAdresseContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse AdresseService
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerAndAdresseConverterTest extends AbstractEbeguRestLoginTest {

	@Inject
	private Persistence persistence;

	@Inject
	private JaxBConverter converter;

	/**
	 * transformiert einen gespeicherten gesuchsteller nach jax und wieder zurueck. wir erwarten das daten gelich beliben
	 */
	@Test
	public void convertPersistedTestEntityToJax() {
		GesuchstellerContainer gesuchsteller = insertNewEntity();
		JaxGesuchstellerContainer jaxGesuchsteller = this.converter.gesuchstellerContainerToJAX(gesuchsteller);
		GesuchstellerContainer transformedEntity = this.converter.gesuchstellerContainerToEntity(jaxGesuchsteller, new GesuchstellerContainer());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getNachname(), transformedEntity.getGesuchstellerJA().getNachname());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getVorname(), transformedEntity.getGesuchstellerJA().getVorname());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getGeburtsdatum(), transformedEntity.getGesuchstellerJA().getGeburtsdatum());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getGeschlecht(), transformedEntity.getGesuchstellerJA().getGeschlecht());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getMail(), transformedEntity.getGesuchstellerJA().getMail());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getTelefon(), transformedEntity.getGesuchstellerJA().getTelefon());
		Assert.assertEquals(gesuchsteller.getGesuchstellerJA().getTelefonAusland(), transformedEntity.getGesuchstellerJA().getTelefonAusland());
		Assert.assertEquals(gesuchsteller.getAdressen().size(), transformedEntity.getAdressen().size());
		boolean allAdrAreSame = gesuchsteller.getAdressen().stream().allMatch(
			adresse -> transformedEntity.getAdressen().stream().anyMatch(
				gsAdresseCont -> gsAdresseCont.getGesuchstellerAdresseJA().isSame(adresse.getGesuchstellerAdresseJA())));
		Assert.assertTrue(allAdrAreSame);

	}

	/**
	 * Testet das Umzugadresse konvertiert wird
	 */
	@Test
	public void convertJaxGesuchstellerWithUmzgTest() {
		JaxGesuchstellerContainer gesuchstellerWith4Adr = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		GesuchstellerContainer gesuchsteller = converter.gesuchstellerContainerToEntity(gesuchstellerWith4Adr, new GesuchstellerContainer());
		Assert.assertEquals(gesuchstellerWith4Adr.getGesuchstellerJA().getGeburtsdatum(), gesuchsteller.getGesuchstellerJA().getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWith4Adr.getGesuchstellerJA().getVorname(), gesuchsteller.getGesuchstellerJA().getVorname());
		Assert.assertEquals(gesuchstellerWith4Adr.getGesuchstellerJA().getNachname(), gesuchsteller.getGesuchstellerJA().getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWith4Adr.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(4, gesuchsteller.getAdressen().size());
		ImmutableListMultimap<AdresseTyp, GesuchstellerAdresseContainer> adrByTyp =
			Multimaps.index(gesuchsteller.getAdressen(), GesuchstellerAdresseContainer::extractAdresseTyp);

		GesuchstellerAdresseContainer altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
		Assert.assertNotNull("Korrespondenzadresse muss vorhanden sein", altAdr);
		Assert.assertTrue(altAdr.getGesuchstellerAdresseJA().isSame(converter.gesuchstellerAdresseContainerToEntity(gesuchstellerWith4Adr.getAlternativeAdresse(),
			new GesuchstellerAdresseContainer()).getGesuchstellerAdresseJA()));

		GesuchstellerAdresseContainer rechnungsAdr = adrByTyp.get(AdresseTyp.RECHNUNGSADRESSE).get(0);
		Assert.assertNotNull("Rechnungsadresse muss vorhanden sein", rechnungsAdr);
		Assert.assertTrue(rechnungsAdr.getGesuchstellerAdresseJA().isSame(converter.gesuchstellerAdresseContainerToEntity(gesuchstellerWith4Adr.getRechnungsAdresse(),
			new GesuchstellerAdresseContainer()).getGesuchstellerAdresseJA()));

		ImmutableList<GesuchstellerAdresseContainer> wohnAdressen = adrByTyp.get(AdresseTyp.WOHNADRESSE);
		Assert.assertEquals(LocalDate.of(1000, 1, 1), wohnAdressen.get(0).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuchstellerWith4Adr.getAdressen().get(1).getAdresseJA().getGueltigAb().minusDays(1), wohnAdressen.get(0).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigBis());
		Assert.assertEquals(gesuchstellerWith4Adr.getAdressen().get(1).getAdresseJA().getGueltigAb(), wohnAdressen.get(1).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(9999, 12, 31), wohnAdressen.get(1).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigBis());
	}

	@Test
	public void datesRangeAddedOnEntityTest() {
		JaxAdresseContainer adr = TestJaxDataUtil.createTestJaxAdr(null);
		adr.getAdresseJA().setGueltigAb(null);
		adr.getAdresseJA().setGueltigBis(null);
		GesuchstellerAdresseContainer adrEntity = converter.gesuchstellerAdresseContainerToEntity(adr, new GesuchstellerAdresseContainer());
		Assert.assertEquals(Constants.START_OF_TIME, adrEntity.extractGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, adrEntity.extractGueltigkeit().getGueltigBis());
	}

	private GesuchstellerContainer insertNewEntity() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		persistence.persist(gesuchsteller);
		return gesuchsteller;
	}

}
