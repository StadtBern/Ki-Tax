package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests der die Konvertierung von Erwerbspensen prueft
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumConverterTest extends AbstractEbeguRestTest {


	@Inject
	private Persistence<Erwerbspensum> persistence;

	@Inject
	private JaxBConverter converter;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	/**
	 * transformiert einen gespeichertes Erwerbspensum nach jax und wieder zurueck. wir erwarten das Daten gleich bleiben
	 */
	@Test
	public void convertPersistedTestEntityToJax(){
		ErwerbspensumContainer erwerbspensumContainer = insertNewEntity();
		JaxErwerbspensumContainer jaxErwerbspensum = this.converter.erwerbspensumContainerToJAX(erwerbspensumContainer);
		ErwerbspensumContainer ewbContEntity = this.converter.erwerbspensumContainerToEntity(jaxErwerbspensum, new ErwerbspensumContainer());

		Assert.assertTrue(erwerbspensumContainer.isSame(ewbContEntity));

	}

//	/**
//	 * Testet das Umzugadresse konvertiert wird
//	 */
//	@Test
//	public void convertJaxGesuchstellerWithUmzgTest(){
//		JaxGesuchsteller gesuchstellerWith3Adr = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
//		Gesuchsteller gesuchsteller = converter.gesuchstellerToEntity(gesuchstellerWith3Adr, new Gesuchsteller());
//		Assert.assertEquals(gesuchstellerWith3Adr.getGeburtsdatum(), gesuchsteller.getGeburtsdatum());
//		Assert.assertEquals(gesuchstellerWith3Adr.getVorname(), gesuchsteller.getVorname());
//		Assert.assertEquals(gesuchstellerWith3Adr.getNachname(), gesuchsteller.getNachname());
//		//id wird serverseitig gesetzt
//		Assert.assertNull(gesuchstellerWith3Adr.getId());
//		Assert.assertNotNull(gesuchsteller.getId());
//		Assert.assertEquals(3, gesuchsteller.getAdressen().size());
//		ImmutableListMultimap<AdresseTyp, Adresse> adrByTyp = Multimaps.index(gesuchsteller.getAdressen(), Adresse::getAdresseTyp);
//		Adresse altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
//		Assert.assertTrue(altAdr.isSame(converter.adresseToEntity(gesuchstellerWith3Adr.getAlternativeAdresse(), new Adresse())));
//
//	}
//
//	@Test
//	public void datesRangeAddedOnEntityTest() {
//		JaxAdresse adr = TestJaxDataUtil.createTestJaxAdr(null);
//		adr.setGueltigAb(null);
//		adr.setGueltigBis(null);
//		Adresse adrEntity = converter.adresseToEntity(adr, new Adresse());
//		Assert.assertEquals(Constants.START_OF_TIME, adrEntity.getGueltigkeit().getGueltigAb());
//		Assert.assertEquals(Constants.END_OF_TIME,adrEntity.getGueltigkeit().getGueltigBis());
//	}
//
//
	private ErwerbspensumContainer insertNewEntity() {
		ErwerbspensumContainer erwerbspensumContainer = TestDataUtil.createErwerbspensumContainer();
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		erwerbspensumContainer.setGesuchsteller(persistence.persist(gesuchsteller));
		return persistence.persist(erwerbspensumContainer);
	}

}
