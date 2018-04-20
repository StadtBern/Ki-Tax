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

package ch.dvbern.ebegu.rest.test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.api.dtos.JaxInstitutionStammdaten;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.resource.BetreuungResource;
import ch.dvbern.ebegu.api.resource.GesuchResource;
import ch.dvbern.ebegu.api.resource.InstitutionResource;
import ch.dvbern.ebegu.api.resource.InstitutionStammdatenResource;
import ch.dvbern.ebegu.api.resource.KindResource;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Fachstelle;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.TestdataCreationService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.TestfallName;
import ch.dvbern.ebegu.util.testdata.ErstgesuchConfig;
import ch.dvbern.ebegu.util.testdata.TestdataSetupConfig;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer den JaxBConverter. Insbesondere wird geprüft, dass beim Speichern von Gesuchsdaten keine Stammdaten verändert werden dürfen.
 * Gesuchsperiode
 * - beim Speichern von Gesuch => gesuchSpeichernDarfGesuchsperiodeNichtUpdaten
 * Mandant
 * - beim Speichern von Institution => institutionSpeichernDarfMandantUndTraegerschaftNichtUpdaten
 * Trägerschaft
 * - beim Speichern von Institution => institutionSpeichernDarfMandantUndTraegerschaftNichtUpdaten
 * Institution
 *  - beim Speichern von InstitutionsStammdaten => institutionsStammdatenSpeichernDarfInstitutionNichtUpdaten
 * InstitutionsStammdaten
 * - beim Speichern von Betreuung => betreuungSpeichernDarfInstitutionsStammdatenNichtUpdaten
 * Fachstelle
 * - beim Speichern von PensumFachstelle => pensumFachstelleSpeichernDarfFachstelleNichtUpdaten
 */
@SuppressWarnings({ "LocalVariableNamingConvention", "InstanceMethodNamingConvention", "InstanceVariableNamingConvention" })
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
public class JaxBConverterTest extends AbstractEbeguRestLoginTest {

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private TestdataCreationService testdataCreationService;

	@Inject
	private GesuchResource gesuchResource;

	@Inject
	private InstitutionResource institutionResource;

	@Inject
	private InstitutionStammdatenResource institutionStammdatenResource;

	@Inject
	private BetreuungResource betreuungResource;

	@Inject
	private KindResource kindResource;

	@Inject
	private Persistence persistence;

	private JaxBConverter converter = new JaxBConverter();
	private UriInfo uri = new ResteasyUriInfo("test", "test", "test");

	@Before
	public void init() {
		// Tests initialisieren
		Gesuchsperiode gesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class).iterator().next();
		final InstitutionStammdaten kitaAaregg = TestDataUtil.createInstitutionStammdatenKitaWeissenstein();
		final InstitutionStammdaten kitaBruennen = TestDataUtil.createInstitutionStammdatenKitaBruennen();
		final InstitutionStammdaten tagiAaregg = TestDataUtil.createInstitutionStammdatenTagiWeissenstein();
		Mandant mandant = TestDataUtil.createDefaultMandant();
		TestdataSetupConfig setupConfig = new TestdataSetupConfig(mandant, kitaBruennen, kitaAaregg, tagiAaregg, gesuchsperiode);
		testdataCreationService.setupTestdata(setupConfig);
	}

	@Test
	public void gesuchSpeichernDarfGesuchsperiodeNichtUpdaten() throws Exception {
		Gesuchsperiode gesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class).iterator().next();
		Assert.assertEquals(GesuchsperiodeStatus.AKTIV, gesuchsperiode.getStatus());

		Gesuch gesuch = testdataCreationService.createErstgesuch(ErstgesuchConfig.createErstgesuchVerfuegt(
			TestfallName.BECKER_NORA, gesuchsperiode, LocalDate.now(), LocalDateTime.now()));
		JaxGesuch jaxGesuch = TestJaxDataUtil.createTestJaxGesuch();
		jaxGesuch.setFall(converter.fallToJAX(gesuch.getFall()));
		jaxGesuch.setGesuchsperiode(converter.gesuchsperiodeToJAX(gesuchsperiode));
		jaxGesuch.getGesuchsperiode().setStatus(GesuchsperiodeStatus.INAKTIV);
		gesuchResource.create(jaxGesuch, uri, null);

		gesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class).iterator().next();
		Assert.assertEquals(GesuchsperiodeStatus.AKTIV, gesuchsperiode.getStatus());
	}

	@Test
	public void institutionSpeichernDarfMandantUndTraegerschaftNichtUpdaten() throws Exception {
		Mandant mandant = criteriaQueryHelper.getAll(Mandant.class).iterator().next();
		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		traegerschaft = persistence.persist(traegerschaft);
		Assert.assertEquals("Mandant1", mandant.getName());
		Assert.assertEquals("Traegerschaft1", traegerschaft.getName());

		Institution institution = TestDataUtil.createDefaultInstitution();
		institution.setTraegerschaft(traegerschaft);
		institution.setMandant(mandant);
		JaxInstitution jaxInstitution = converter.institutionToJAX(institution);
		jaxInstitution.getTraegerschaft().setName("ChangedTraegerschaft");
		jaxInstitution.getMandant().setName("ChangedMandant");
		institutionResource.createInstitution(jaxInstitution, uri, null);

		mandant = criteriaQueryHelper.getAll(Mandant.class).iterator().next();
		traegerschaft = criteriaQueryHelper.getAll(Traegerschaft.class).iterator().next();
		Assert.assertEquals("Mandant1", mandant.getName());
		Assert.assertEquals("Traegerschaft1", traegerschaft.getName());
	}

	@Test
	public void institutionsStammdatenSpeichernDarfInstitutionNichtUpdaten() throws Exception {
		Mandant mandant = criteriaQueryHelper.getAll(Mandant.class).iterator().next();
		final Gesuchsperiode gesuchsperiode1718 = TestDataUtil.createGesuchsperiode1718();
		persistence.persist(gesuchsperiode1718);
		Institution institution = TestDataUtil.createDefaultInstitution();
		institution.setMandant(mandant);
		institution.setTraegerschaft(null);
		institution = persistence.persist(institution);
		Assert.assertEquals("Institution1", institution.getName());

		JaxInstitutionStammdaten jaxStammdaten = TestJaxDataUtil.createTestJaxInstitutionsStammdaten();
		jaxStammdaten.setInstitution(converter.institutionToJAX(institution));
		jaxStammdaten.getInstitution().setName("ChangedInstitution");
		final JaxInstitutionStammdaten updatedInstitution = institutionStammdatenResource.saveInstitutionStammdaten(jaxStammdaten, uri, null);

		Assert.assertNotNull(updatedInstitution);
		Assert.assertEquals("Institution1", updatedInstitution.getInstitution().getName());
	}

	@Test
	public void betreuungSpeichernDarfInstitutionsStammdatenNichtUpdaten() {
		InstitutionStammdaten kitaBruennen = TestDataUtil.createInstitutionStammdatenKitaBruennen();
		Assert.assertEquals(Constants.START_OF_TIME, kitaBruennen.getGueltigkeit().getGueltigAb());

		Gesuchsperiode gesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class).iterator().next();
		Gesuch gesuch = testdataCreationService.createErstgesuch(ErstgesuchConfig.createErstgesuchVerfuegt(
			TestfallName.BECKER_NORA, gesuchsperiode, LocalDate.now(), LocalDateTime.now()));
		Betreuung betreuung = gesuch.extractAllBetreuungen().get(0);
		JaxBetreuung jaxBetreuung = converter.betreuungToJAX(betreuung);
		jaxBetreuung.setInstitutionStammdaten(converter.institutionStammdatenToJAX(kitaBruennen));
		jaxBetreuung.getInstitutionStammdaten().setGueltigAb(LocalDate.now());
		betreuungResource.saveBetreuung(converter.toJaxId(betreuung.getKind()), jaxBetreuung, false, uri, null);

		kitaBruennen = criteriaQueryHelper.getAll(InstitutionStammdaten.class).iterator().next();
		Assert.assertEquals(Constants.START_OF_TIME, kitaBruennen.getGueltigkeit().getGueltigAb());
	}

	@Test
	public void pensumFachstelleSpeichernDarfFachstelleNichtUpdaten() {
		Fachstelle fachstelle = TestDataUtil.createDefaultFachstelle();
		fachstelle = persistence.persist(fachstelle);
		Assert.assertEquals("Fachstelle1", fachstelle.getName());

		Gesuchsperiode gesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class).iterator().next();
		Gesuch gesuch = testdataCreationService.createErstgesuch(ErstgesuchConfig.createErstgesuchVerfuegt(
			TestfallName.BECKER_NORA, gesuchsperiode, LocalDate.now(), LocalDateTime.now()));
		KindContainer kindContainer = gesuch.getKindContainers().iterator().next();
		kindContainer.getKindJA().setPensumFachstelle(new PensumFachstelle());
		kindContainer.getKindJA().getPensumFachstelle().setFachstelle(fachstelle);
		kindContainer.getKindJA().getPensumFachstelle().setPensum(50);
		kindContainer = persistence.merge(kindContainer);
		JaxKindContainer jaxKindContainer = converter.kindContainerToJAX(kindContainer);
		jaxKindContainer.getKindJA().getPensumFachstelle().getFachstelle().setName("FachstelleChanged");
		kindResource.saveKind(converter.toJaxId(gesuch), jaxKindContainer, uri, null);

		fachstelle = criteriaQueryHelper.getAll(Fachstelle.class).iterator().next();
		Assert.assertEquals("Fachstelle1", fachstelle.getName());
	}
}
