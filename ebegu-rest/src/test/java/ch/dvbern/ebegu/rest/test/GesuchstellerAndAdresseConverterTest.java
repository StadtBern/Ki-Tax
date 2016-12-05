package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
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

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * Tests fuer die Klasse AdresseService
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerAndAdresseConverterTest extends AbstractEbeguRestLoginTest {


	@Inject
	private Persistence<Gesuchsteller> persistence;

	@Inject
	private JaxBConverter converter;


	/**
	 * transformiert einen gespeicherten gesuchsteller nach jax und wieder zurueck. wir erwarten das daten gelich beliben
	 */
	@Test
	public void convertPersistedTestEntityToJax(){
		Gesuchsteller gesuchsteller = insertNewEntity();
		JaxGesuchsteller jaxGesuchsteller = this.converter.gesuchstellerToJAX(gesuchsteller);
		Gesuchsteller transformedEntity = this.converter.gesuchstellerToEntity(jaxGesuchsteller, new Gesuchsteller());
		Assert.assertEquals(gesuchsteller.getNachname(), transformedEntity.getNachname());
		Assert.assertEquals(gesuchsteller.getVorname(), transformedEntity.getVorname());
		Assert.assertEquals(gesuchsteller.getGeburtsdatum(), transformedEntity.getGeburtsdatum());
		Assert.assertEquals(gesuchsteller.getGeschlecht(), transformedEntity.getGeschlecht());
		Assert.assertEquals(gesuchsteller.getMail(), transformedEntity.getMail());
		Assert.assertEquals(gesuchsteller.getTelefon(), transformedEntity.getTelefon());
		Assert.assertEquals(gesuchsteller.getTelefonAusland(), transformedEntity.getTelefonAusland());
		Assert.assertEquals(gesuchsteller.getAdressen().size(), transformedEntity.getAdressen().size());
		boolean allAdrAreSame  = gesuchsteller.getAdressen().stream().allMatch(
			adresse -> transformedEntity.getAdressen().stream().anyMatch(adresse::isSame));
		Assert.assertTrue(allAdrAreSame);

	}

	/**
	 * Testet das Umzugadresse konvertiert wird
	 */
	@Test
	public void convertJaxGesuchstellerWithUmzgTest(){
		JaxGesuchsteller gesuchstellerWith3Adr = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		Gesuchsteller gesuchsteller = converter.gesuchstellerToEntity(gesuchstellerWith3Adr, new Gesuchsteller());
		Assert.assertEquals(gesuchstellerWith3Adr.getGeburtsdatum(), gesuchsteller.getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWith3Adr.getVorname(), gesuchsteller.getVorname());
		Assert.assertEquals(gesuchstellerWith3Adr.getNachname(), gesuchsteller.getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWith3Adr.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(3, gesuchsteller.getAdressen().size());
		ImmutableListMultimap<AdresseTyp, GesuchstellerAdresse> adrByTyp = Multimaps.index(gesuchsteller.getAdressen(), GesuchstellerAdresse::getAdresseTyp);

		GesuchstellerAdresse altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
		Assert.assertNotNull("Korrespondenzadresse muss vorhanden sein", altAdr);
		Assert.assertTrue(altAdr.isSame(converter.gesuchstellerAdresseToEntity(gesuchstellerWith3Adr.getAlternativeAdresse(), new GesuchstellerAdresse())));

		ImmutableList<GesuchstellerAdresse> wohnAdressen = adrByTyp.get(AdresseTyp.WOHNADRESSE);
		Assert.assertEquals(LocalDate.of(1000, 1, 1), wohnAdressen.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuchstellerWith3Adr.getAdressen().get(1).getGueltigAb().minusDays(1), wohnAdressen.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertEquals(gesuchstellerWith3Adr.getAdressen().get(1).getGueltigAb(), wohnAdressen.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(9999, 12, 31), wohnAdressen.get(1).getGueltigkeit().getGueltigBis());
	}

	@Test
	public void datesRangeAddedOnEntityTest() {
		JaxAdresse adr = TestJaxDataUtil.createTestJaxAdr(null);
		adr.setGueltigAb(null);
		adr.setGueltigBis(null);
		GesuchstellerAdresse adrEntity = converter.gesuchstellerAdresseToEntity(adr, new GesuchstellerAdresse());
		Assert.assertEquals(Constants.START_OF_TIME, adrEntity.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME,adrEntity.getGueltigkeit().getGueltigBis());
	}


	private Gesuchsteller insertNewEntity() {
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		persistence.persist(gesuchsteller);
		return gesuchsteller;
	}

}
