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

import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxVerfuegung;
import ch.dvbern.ebegu.api.resource.VerfuegungResource;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.Month;

/**
 * Testet VerfuegungResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class VerfuegungResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private VerfuegungResource verfuegungResource;
	@Inject
	private InstitutionService instService;
	@Inject
	private Persistence persistence;

	@Test
	public void saveVerfuegungTest() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		betreuung.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
		persistence.merge(betreuung);

		final JaxVerfuegung verfuegungJax = new JaxVerfuegung();
		verfuegungJax.setGeneratedBemerkungen("genBemerkung");
		verfuegungJax.setManuelleBemerkungen("manBemerkung");

		final JaxVerfuegung persistedVerfuegung = verfuegungResource.saveVerfuegung(new JaxId(gesuch.getId()), new JaxId(betreuung.getId()), false, verfuegungJax);

		Assert.assertEquals(verfuegungJax.getGeneratedBemerkungen(), persistedVerfuegung.getGeneratedBemerkungen());
		Assert.assertEquals(verfuegungJax.getManuelleBemerkungen(), persistedVerfuegung.getManuelleBemerkungen());

	}

	@Test
	public void nichtEintretenTest() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Betreuung betreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		betreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
		Betreuung storedBetr = persistence.merge(betreuung);

		final JaxVerfuegung verfuegungJax = new JaxVerfuegung();
		verfuegungJax.setGeneratedBemerkungen("genBemerkung");
		verfuegungJax.setManuelleBemerkungen("manBemerkung");

		final JaxVerfuegung persistedVerfuegung = verfuegungResource.schliessenNichtEintreten(new JaxId(storedBetr.getId()),  verfuegungJax);

		Assert.assertNotNull(persistedVerfuegung);
		persistedVerfuegung.getZeitabschnitte().forEach(jaxVerfZeitabsch -> Assert.assertEquals(0, jaxVerfZeitabsch.getAnspruchberechtigtesPensum()));
		Betreuung storedBetreuung = persistence.find(Betreuung.class, betreuung.getId());
		Assert.assertEquals(Betreuungsstatus.NICHT_EINGETRETEN, storedBetreuung.getBetreuungsstatus());
		Assert.assertEquals(verfuegungJax.getGeneratedBemerkungen(), persistedVerfuegung.getGeneratedBemerkungen());
		Assert.assertEquals(verfuegungJax.getManuelleBemerkungen(), persistedVerfuegung.getManuelleBemerkungen());


	}

	@Test
	public void testCalculateVerfuegung() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(instService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		TestDataUtil.prepareParameters(gesuch.getGesuchsperiode().getGueltigkeit(), persistence);

		Response response = verfuegungResource.calculateVerfuegung(new JaxId(gesuch.getId()), null, null);

		Assert.assertNotNull(response);
	}
}
