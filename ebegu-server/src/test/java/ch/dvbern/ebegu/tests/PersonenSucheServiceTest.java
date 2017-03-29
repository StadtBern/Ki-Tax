package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.services.PersonenSucheService;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;

/**
 * Arquillian Tests fuer den PersonenSuche Service
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class PersonenSucheServiceTest extends AbstractEbeguLoginTest {

	private static final String ID_MARC_SCHMID = "1000028027";

	@Inject
	private PersonenSucheService personenSucheService;

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
}
