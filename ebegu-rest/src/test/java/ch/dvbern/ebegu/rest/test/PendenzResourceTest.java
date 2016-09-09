package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxPendenzJA;
import ch.dvbern.ebegu.api.resource.PendenzResource;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Testet PendenzResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PendenzResourceTest extends AbstractEbeguRestTest {

	@Inject
	private PendenzResource pendenzResource;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private JaxBConverter converter;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void getAllPendenzenJATest() {
		Gesuch gesuch1 = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(gesuch1, persistence);
		Gesuch gesuch2 = TestDataUtil.createDefaultGesuch();
		TestDataUtil.persistEntities(gesuch2, persistence);

		List<JaxPendenzJA> pendenzenList = pendenzResource.getAllPendenzenJA();

		Assert.assertNotNull(pendenzenList);
		Assert.assertEquals(2, pendenzenList.size());

		assertGesuchDaten(gesuch1, pendenzenList.get(0));
		assertGesuchDaten(gesuch2, pendenzenList.get(1));

		Set<BetreuungsangebotTyp> angeboteList = new LinkedHashSet<>();
		angeboteList.add(BetreuungsangebotTyp.KITA);
		Assert.assertEquals(angeboteList, pendenzenList.get(0).getAngebote());

		Assert.assertEquals(AntragTyp.GESUCH, pendenzenList.get(0).getAntragTyp());

		Set<String> institutionen = new LinkedHashSet<>();
		institutionen.add("Institution1");
		Assert.assertEquals(institutionen, pendenzenList.get(0).getInstitutionen());
		Assert.assertEquals(converter.gesuchsperiodeToJAX(gesuch1.getGesuchsperiode()), pendenzenList.get(0).getGesuchsperiode());
	}


	// HELP METHOD

	private void assertGesuchDaten(Gesuch gesuch1, JaxPendenzJA pendenzenList) {
		Assert.assertEquals(gesuch1.getFall().getFallNummer(), pendenzenList.getFallNummer());
		Assert.assertEquals(gesuch1.getGesuchsteller1().getNachname(), pendenzenList.getFamilienName());
		Assert.assertEquals(gesuch1.getEingangsdatum(), pendenzenList.getEingangsdatum());
		Assert.assertEquals(gesuch1.getStatus(), pendenzenList.getStatus());
	}
}
