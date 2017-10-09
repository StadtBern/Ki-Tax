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

import java.io.StringWriter;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ch.dvbern.ebegu.api.dtos.JaxBetreuungsmitteilung;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxMitteilung;
import ch.dvbern.ebegu.api.dtos.JaxMitteilungen;
import ch.dvbern.ebegu.api.resource.MitteilungResource;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testet BetreuungResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class MitteilungResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private MitteilungResource mitteilungResource;

	@Inject
	private Persistence persistence;

	@Inject
	private BetreuungService betreuungService;

	@Test
	public void testGetMitteilungenForCurrentRolleForFallNoFall() throws EbeguException {
		try {
			mitteilungResource.getMitteilungenForCurrentRolleForFall(new JaxId("123456789"), null, null);
			Assert.fail("Exception should be thrown. The Fall doesn't exist");
		} catch (EbeguEntityNotFoundException e) {
			// nop
		}
	}

	@Test
	public void testGetMitteilungenForCurrentRolleForFallNoMitteilungen() throws EbeguException {
		final Fall fall = createAndPersistFall();

		final JaxMitteilungen mitteilungen = mitteilungResource.getMitteilungenForCurrentRolleForFall(new JaxId(fall.getId()), null, null);

		Assert.assertNotNull(mitteilungen);
		Assert.assertEquals(0, mitteilungen.getMitteilungen().size());
	}

	@Test
	public void testGetMitteilungenForCurrentRolleForFallNormalMitteilungen() throws EbeguException {
		final Benutzer empfaengerJA = loginAsSachbearbeiterJA();
		final Fall fall = createAndPersistFall();
		final Benutzer sender = createAndPersistSender();

		final Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT, sender, MitteilungTeilnehmerTyp.INSTITUTION);
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		persistence.persist(mitteilung);

		final JaxMitteilungen mitteilungen = mitteilungResource.getMitteilungenForCurrentRolleForFall(new JaxId(fall.getId()), null, null);

		Assert.assertNotNull(mitteilungen);
		Assert.assertEquals(1, mitteilungen.getMitteilungen().size());
		Assert.assertEquals(JaxMitteilung.class, mitteilungen.getMitteilungen().iterator().next().getClass());
		Assert.assertEquals(mitteilung.getId(), mitteilungen.getMitteilungen().iterator().next().getId());
	}

	@Transactional(TransactionMode.DEFAULT)
	@Test
	public void testGetMitteilungenForCurrentRolleForFallBetreuungMitteilungen() throws EbeguException, JAXBException, JsonProcessingException {
		final Benutzer empfaengerJA = loginAsSachbearbeiterJA();
		final Fall fall = createAndPersistFall();
		final Benutzer sender = createAndPersistSender();

		final Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT, sender, MitteilungTeilnehmerTyp.INSTITUTION);
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		persistence.persist(mitteilung);

		final Betreuungsmitteilung betreuungMitteilung = TestDataUtil.createBetreuungmitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT, sender, MitteilungTeilnehmerTyp.INSTITUTION);
		betreuungMitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		final Betreuung betreuung = TestDataUtil.persistBetreuung(betreuungService, persistence);
		betreuungMitteilung.setBetreuung(betreuung);
		persistence.persist(betreuungMitteilung);

		final JaxMitteilungen mitteilungen = mitteilungResource.getMitteilungenForCurrentRolleForFall(new JaxId(fall.getId()), null, null);

		Assert.assertNotNull(mitteilungen);
		Assert.assertEquals(2, mitteilungen.getMitteilungen().size());
		final Iterator<JaxMitteilung> iterator = mitteilungen.getMitteilungen().iterator();

		// Test Marshalling values
		JAXBContext jaxbContext = JAXBContext.newInstance(JaxMitteilung.class, JaxBetreuungsmitteilung.class);
		final Marshaller marshaller = jaxbContext.createMarshaller();

		final StringWriter stringFirst = new StringWriter();
		final JaxMitteilung first = iterator.next();
		Assert.assertEquals(JaxMitteilung.class, first.getClass());
		Assert.assertEquals(mitteilung.getId(), first.getId());
		marshaller.marshal(first, stringFirst);
		Assert.assertFalse(stringFirst.toString().contains("betreuungspensen"));
		final ObjectMapper o = new ObjectMapper();
		final String s = o.writeValueAsString(first);
		Assert.assertFalse(s.contains("betreuungspensen"));

		final StringWriter stringSecond = new StringWriter();
		final JaxMitteilung second = iterator.next();
		Assert.assertEquals(JaxBetreuungsmitteilung.class, second.getClass());
		Assert.assertEquals(betreuungMitteilung.getId(), second.getId());
		marshaller.marshal(second, stringSecond);
		Assert.assertTrue(stringSecond.toString().contains("betreuungspensen"));
		final String s2 = o.writeValueAsString(second);
		Assert.assertTrue(s2.contains("betreuungspensen"));
	}

	// HELP METHODS

	@Nonnull
	private Fall createAndPersistFall() {
		final Fall fall = TestDataUtil.createDefaultFall();
		fall.setId(UUID.randomUUID().toString());
		persistence.persist(fall);
		return fall;
	}

	private Benutzer createAndPersistSender() {
		final Mandant mandant = persistence.find(Mandant.class, "e3736eb8-6eef-40ef-9e52-96ab48d8f220");
		final Traegerschaft traegerschaft = persistence.persist(TestDataUtil.createDefaultTraegerschaft());
		final Benutzer senderINST = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "insti", traegerschaft, null, mandant);
		persistence.persist(senderINST);
		return senderINST;
	}
}
