package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.services.PersonenSucheService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBException;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;

/**
 * Arquillian Tests fuer den PersonenSuche Service
 */
@SuppressWarnings("InstanceMethodNamingConvention")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class PersonenSucheServiceTest extends AbstractEbeguLoginTest {

	private static final String ID_MARC_SCHMID = "1000028027";

	@Inject
	private PersonenSucheService personenSucheService;

	@Inject
	private Persistence<Gesuchsteller> persistence;

	@Test
	public void suchePersonById() throws Exception {
		EWKResultat ewkResultat = personenSucheService.suchePerson(ID_MARC_SCHMID);
		Assert.assertNotNull(ewkResultat);
		Assert.assertEquals(1, ewkResultat.getAnzahlResultate());
		Assert.assertEquals("Schmid", ewkResultat.getPersonen().get(0).getNachname());
	}

	@Test
	public void suchePersonByName() throws Exception {
		EWKResultat ewkResultat = personenSucheService.suchePerson("Schmid", "Marc", LocalDate.of(1953, Month.MAY, 23), Geschlecht.MAENNLICH);
		Assert.assertNotNull(ewkResultat);
		Assert.assertEquals(1, ewkResultat.getAnzahlResultate());
		Assert.assertEquals("Schmid", ewkResultat.getPersonen().get(0).getNachname());
	}

	@Test
	public void suchePersonByIdNoResult() throws Exception {
		EWKResultat ewkResultat = personenSucheService.suchePerson("ungueltigeID");
		Assert.assertNotNull(ewkResultat);
		Assert.assertEquals(0, ewkResultat.getAnzahlResultate());
	}

	@Test (expected = EJBException.class)
	public void suchePersonByGesuchstellerNochNichtGespeichert() throws Exception {
		Gesuchsteller defaultGesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		personenSucheService.suchePerson(defaultGesuchsteller);
	}

	@Test
	public void suchePersonByGesuchsteller() throws Exception {
		Gesuchsteller defaultGesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		defaultGesuchsteller = persistence.merge(defaultGesuchsteller);
		EWKResultat ewkResultat = personenSucheService.suchePerson(defaultGesuchsteller);
		Assert.assertNotNull(ewkResultat);
		Assert.assertEquals(2, ewkResultat.getAnzahlResultate()); // Als default kommt Herbert Gerber zurueck, dort sind zwei Personen drinn
	}

	@Test
	public void sucheSelectPerson() throws Exception {
		Gesuchsteller gs = TestDataUtil.createDefaultGesuchsteller();
		EWKResultat ewkResultat1 = personenSucheService.suchePerson(ID_MARC_SCHMID);
		Gesuchsteller gsMerged = personenSucheService.selectPerson(gs, ewkResultat1.getPersonen().get(0));
		Assert.assertNotNull(gsMerged);
		Assert.assertEquals(ID_MARC_SCHMID, gsMerged.getEwkPersonId());
	}
}
