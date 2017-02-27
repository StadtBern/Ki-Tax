package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.services.Pain001Service;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests fuer die Klasse Pain001Service
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class Pain001ServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private Pain001Service pain001Service;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;


	@Inject
	private Persistence<?> persistence;

	Collection<InstitutionStammdaten> allInstitutionStammdaten;


	@Before
	public void init() {
		final Gesuchsperiode gesuchsperiode = createGesuchsperiode(true);
		final Mandant mandant = insertInstitutionen();
		//createBenutzer(mandant);
		TestDataUtil.prepareApplicationProperties(persistence);

		allInstitutionStammdaten = institutionStammdatenService.getAllInstitutionStammdaten();
	}

	@Test
	public void getPainFileContentTest() {
		Assert.assertNotNull(pain001Service);

		List<Zahlung> zahlungList = new ArrayList<>();

		for (InstitutionStammdaten stammdaten : allInstitutionStammdaten) {
			Zahlung zahlung = new Zahlung();
			zahlung.setInstitutionStammdaten(stammdaten);
			zahlung.setStatus(ZahlungStatus.AUSGELOEST);
			zahlungList.add(zahlung);
		}

		Zahlungsauftrag zahlungsauftrag = new Zahlungsauftrag();
		zahlungsauftrag.setDatumFaellig(LocalDate.now());
		zahlungsauftrag.setZahlungen(zahlungList);

		final String painFileContent = pain001Service.getPainFileContent(zahlungsauftrag);

		Assert.assertNotNull(painFileContent);
	}
}
