package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxPendenzJA;
import ch.dvbern.ebegu.api.resource.PendenzResource;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
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
import java.util.HashSet;
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
	private Persistence<?> persistence;
	@Inject
	private JaxBConverter converter;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void getAllPendenzenJATest() {
		Gesuch gesuch1 = TestDataUtil.createDefaultGesuch();
		persistEntities(gesuch1);
		Gesuch gesuch2 = TestDataUtil.createDefaultGesuch();
		persistEntities(gesuch2);

		List<JaxPendenzJA> pendenzenList = pendenzResource.getAllPendenzenJA();

		Assert.assertNotNull(pendenzenList);
		Assert.assertEquals(2, pendenzenList.size());

		Assert.assertEquals(gesuch1.getFall().getFallNummer(), pendenzenList.get(0).getFallNummer());
		Assert.assertEquals(gesuch1.getGesuchsteller1().getNachname(), pendenzenList.get(0).getFamilienName());
		Assert.assertEquals(gesuch1.getEingangsdatum(), pendenzenList.get(0).getEingangsdatum());

		Set<BetreuungsangebotTyp> angeboteList = new HashSet<>();
		angeboteList.add(BetreuungsangebotTyp.KITA);
		Assert.assertEquals(angeboteList, pendenzenList.get(0).getAngebote());

		Assert.assertEquals(AntragTyp.GESUCH, pendenzenList.get(0).getAntragTyp());

		Set<String> institutionen = new HashSet<>();
		institutionen.add("Institution1");
		Assert.assertEquals(institutionen, pendenzenList.get(0).getInstitutionen());
		Assert.assertEquals(converter.gesuchsperiodeToJAX(gesuch1.getGesuchsperiode()), pendenzenList.get(0).getGesuchsperiode());
	}


	// HELP METHOD


	private void persistEntities(Gesuch gesuch) {
		persistence.persist(gesuch.getFall());
		gesuch.setGesuchsteller1(TestDataUtil.createDefaultGesuchsteller());
		persistence.persist(gesuch.getGesuchsperiode());

		Set<KindContainer> kindContainers = new HashSet<>();
		Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		KindContainer kind = betreuung.getKind();

		Set<Betreuung> betreuungen = new HashSet<>();
		betreuungen.add(betreuung);
		kind.setBetreuungen(betreuungen);

		persistence.persist(kind.getKindGS().getPensumFachstelle().getFachstelle());
		persistence.persist(kind.getKindJA().getPensumFachstelle().getFachstelle());
		kind.setGesuch(gesuch);
		kindContainers.add(kind);
		gesuch.setKindContainers(kindContainers);


		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getTraegerschaft());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution().getMandant());
		persistence.persist(betreuung.getInstitutionStammdaten().getInstitution());
		persistence.persist(betreuung.getInstitutionStammdaten());

		persistence.persist(gesuch);
	}
}
