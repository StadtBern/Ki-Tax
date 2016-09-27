package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;
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
public class ErwerbspensumConverterTest extends AbstractEbeguRestTest {


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
		JaxGesuchsteller gesuchstellerWithErwerbspensen = TestJaxDataUtil.createTestJaxGesuchstellerWithErwerbsbensum();
		Gesuchsteller gesuchsteller = converter.gesuchstellerToEntity(gesuchstellerWithErwerbspensen, new Gesuchsteller());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getGeburtsdatum(), gesuchsteller.getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getVorname(), gesuchsteller.getVorname());
		Assert.assertEquals(gesuchstellerWithErwerbspensen.getNachname(), gesuchsteller.getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWithErwerbspensen.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(2, gesuchsteller.getErwerbspensenContainers().size());
		gesuchsteller = persistence.persist(gesuchsteller);
		JaxGesuchsteller reconvertedJaxGesuchsteller = converter.gesuchstellerToJAX(gesuchsteller);
		Assert.assertEquals(2,reconvertedJaxGesuchsteller.getErwerbspensenContainers().size());
	}


	private ErwerbspensumContainer insertNewEntity() {
		ErwerbspensumContainer ewpContainer = TestDataUtil.createErwerbspensumContainer();
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		ewpContainer.setGesuchsteller(persistence.persist(gesuchsteller));
		return persistence.persist(ewpContainer);
	}

}
