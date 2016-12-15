package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresseContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.entities.*;
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
		boolean allAdrAreSame  = gesuchsteller.getAdressen().stream().allMatch(
			adresse -> transformedEntity.getAdressen().stream().anyMatch(
				gsAdresseCont -> gsAdresseCont.getGesuchstellerAdresseJA().isSame(adresse.getGesuchstellerAdresseJA())));
		Assert.assertTrue(allAdrAreSame);

	}

	/**
	 * Testet das Umzugadresse konvertiert wird
	 */
	@Test
	public void convertJaxGesuchstellerWithUmzgTest(){
		JaxGesuchstellerContainer gesuchstellerWith3Adr = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		GesuchstellerContainer gesuchsteller = converter.gesuchstellerContainerToEntity(gesuchstellerWith3Adr, new GesuchstellerContainer());
		Assert.assertEquals(gesuchstellerWith3Adr.getGesuchstellerJA().getGeburtsdatum(), gesuchsteller.getGesuchstellerJA().getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWith3Adr.getGesuchstellerJA().getVorname(), gesuchsteller.getGesuchstellerJA().getVorname());
		Assert.assertEquals(gesuchstellerWith3Adr.getGesuchstellerJA().getNachname(), gesuchsteller.getGesuchstellerJA().getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWith3Adr.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(3, gesuchsteller.getAdressen().size());
		ImmutableListMultimap<AdresseTyp, GesuchstellerAdresseContainer> adrByTyp =
			Multimaps.index(gesuchsteller.getAdressen(), GesuchstellerAdresseContainer::extractAdresseTyp);

		GesuchstellerAdresseContainer altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
		Assert.assertNotNull("Korrespondenzadresse muss vorhanden sein", altAdr);
		Assert.assertTrue(altAdr.getGesuchstellerAdresseJA().isSame(converter.gesuchstellerAdresseContainerToEntity(gesuchstellerWith3Adr.getAlternativeAdresse(),
			new GesuchstellerAdresseContainer()).getGesuchstellerAdresseJA()));

		ImmutableList<GesuchstellerAdresseContainer> wohnAdressen = adrByTyp.get(AdresseTyp.WOHNADRESSE);
		Assert.assertEquals(LocalDate.of(1000, 1, 1), wohnAdressen.get(0).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuchstellerWith3Adr.getAdressen().get(1).getAdresseJA().getGueltigAb().minusDays(1), wohnAdressen.get(0).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigBis());
		Assert.assertEquals(gesuchstellerWith3Adr.getAdressen().get(1).getAdresseJA().getGueltigAb(), wohnAdressen.get(1).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(9999, 12, 31), wohnAdressen.get(1).getGesuchstellerAdresseJA().getGueltigkeit().getGueltigBis());
	}

	@Test
	public void datesRangeAddedOnEntityTest() {
		JaxAdresseContainer adr = TestJaxDataUtil.createTestJaxAdr(null);
		adr.getAdresseJA().setGueltigAb(null);
		adr.getAdresseJA().setGueltigBis(null);
		GesuchstellerAdresseContainer adrEntity = converter.gesuchstellerAdresseContainerToEntity(adr, new GesuchstellerAdresseContainer());
		Assert.assertEquals(Constants.START_OF_TIME, adrEntity.extractGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME,adrEntity.extractGueltigkeit().getGueltigBis());
	}


	private GesuchstellerContainer insertNewEntity() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		persistence.persist(gesuchsteller);
		return gesuchsteller;
	}

}
