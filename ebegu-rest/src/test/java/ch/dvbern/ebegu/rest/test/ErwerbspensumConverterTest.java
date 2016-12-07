package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests der die Konvertierung von Erwerbspensen prueft
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class ErwerbspensumConverterTest extends AbstractEbeguRestLoginTest {


	@Inject
	private Persistence<Erwerbspensum> persistence;

	@Inject
	private JaxBConverter converter;


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

	/**
	 * Testet konviertiert einen gesuchsteller mit Erwerbspensen
	 */
	@Test
	public void convertJaxGesuchstellerErwerbspensen(){
		JaxGesuchstellerContainer gesuchstellerWithErwerbspensen = TestJaxDataUtil.createTestJaxGesuchstellerWithErwerbsbensum();
		GesuchstellerContainer gesuchsteller = converter.gesuchstellerContainerToEntity(gesuchstellerWithErwerbspensen, new GesuchstellerContainer());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGesuchstellerJA().getGeburtsdatum(), gesuchsteller.getGesuchstellerJA().getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGesuchstellerJA().getVorname(), gesuchsteller.getGesuchstellerJA().getVorname());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGesuchstellerJA().getNachname(), gesuchsteller.getGesuchstellerJA().getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWithErwerbspensen.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(2, gesuchsteller.getErwerbspensenContainers().size());
		gesuchsteller = persistence.persist(gesuchsteller);
		JaxGesuchstellerContainer reconvertedJaxGesuchsteller = converter.gesuchstellerContainerToJAX(gesuchsteller);
		Assert.assertEquals(2,reconvertedJaxGesuchsteller.getErwerbspensenContainers().size());
	}


	private ErwerbspensumContainer insertNewEntity() {
		ErwerbspensumContainer ewpContainer = TestDataUtil.createErwerbspensumContainer();
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer();
		ewpContainer.setGesuchsteller(persistence.persist(gesuchsteller));
		return persistence.persist(ewpContainer);
	}

}
