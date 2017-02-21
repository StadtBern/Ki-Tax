package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungExportDTO;
import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungenExportDTO;
import ch.dvbern.ebegu.dto.dataexport.v1.ZeitabschnittExportDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.StreamsUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * Tests fuer die Klasse AdresseService
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class ExportServiceBeanTest extends AbstractEbeguLoginTest {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEbeguTest.class);



	@Inject
	private InstitutionService institutionService;


	@Inject
	private ExportService exportService;

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private InstitutionService instService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private VerfuegungService verfuegungService;

	private Gesuchsperiode gesuchsperiode;
	private List<InstitutionStammdaten> institutionStammdatenList;


	@Before
	public void init() {
		final Gesuchsperiode gesuchsperiode = createGesuchsperiode(true);
		final Mandant mandant = insertInstitutionen();
		createBenutzer(mandant);
		TestDataUtil.prepareParameters(gesuchsperiode.getGueltigkeit(), persistence);
	}


	/**
	 * Helper für init. Speichert Gesuchsperiode in DB
	 */
	protected Gesuchsperiode createGesuchsperiode(boolean active) {
		gesuchsperiode = TestDataUtil.createCustomGesuchsperiode(2016, 2017);
		gesuchsperiode.setActive(active);
		gesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}


	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Test
	public void exportTest() {

		Gesuch yvonneGesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(instService, persistence, LocalDate.now());
		VerfuegungenExportDTO verfuegungenExportDTO = exportService.exportAllVerfuegungenOfAntrag(yvonneGesuch.getId());
		Assert.assertNotNull(verfuegungenExportDTO);
		Assert.assertNotNull(verfuegungenExportDTO.getVerfuegungen());
		Assert.assertEquals(0, verfuegungenExportDTO.getVerfuegungen().size());
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Test
	public void exportTestVorVerfuegt() {

		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.WaeltiDagmar, true, true);

		Assert.assertNotNull(gesuch.getKindContainers().stream().findFirst().get().getBetreuungen().stream().findFirst().get().getVerfuegung());
		VerfuegungenExportDTO exportedVerfuegungen = exportService.exportAllVerfuegungenOfAntrag(gesuch.getId());
		Assert.assertNotNull(exportedVerfuegungen);
		Assert.assertNotNull(exportedVerfuegungen.getVerfuegungen());
		Assert.assertEquals(2, exportedVerfuegungen.getVerfuegungen().size());
		Assert.assertEquals(12, exportedVerfuegungen.getVerfuegungen().iterator().next().getZeitabschnitte().size());
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				Assert.assertNotNull(betreuung.getVerfuegung());
				VerfuegungExportDTO matchingVerfuegungExport = exportedVerfuegungen.getVerfuegungen().stream()
					.filter(verfuegungExportDTO -> verfuegungExportDTO.getRefnr().equals(betreuung.getBGNummer())).reduce(StreamsUtil.toOnlyElement()).get();
				checkExportedValuesCorrect(betreuung.getVerfuegung(), matchingVerfuegungExport);

			}
		}
	}

	private void checkExportedValuesCorrect(Verfuegung verfuegung, VerfuegungExportDTO matchingVerfuegungExport) {
		Assert.assertEquals(verfuegung.getZeitabschnitte().size(), matchingVerfuegungExport.getZeitabschnitte().size());
		Assert.assertEquals(verfuegung.getBetreuung().getBetreuungsangebotTyp(), matchingVerfuegungExport.getBetreuung().getBetreuungsArt());

		Assert.assertEquals(verfuegung.getBetreuung().getInstitutionStammdaten().getInstitution().getId(), matchingVerfuegungExport.getBetreuung().getInstitution().getId());
		Assert.assertNotNull(verfuegung.getBetreuung().extractGesuch().getGesuchsteller1());
		Assert.assertEquals(verfuegung.getBetreuung().extractGesuch().getGesuchsteller1().getGesuchstellerJA().getMail(), matchingVerfuegungExport.getGesuchsteller().getEmail());
		Assert.assertEquals(verfuegung.getBetreuung().extractGesuch().getGesuchsteller1().getGesuchstellerJA().getNachname(), matchingVerfuegungExport.getGesuchsteller().getNachname());
		Assert.assertEquals(verfuegung.getBetreuung().extractGesuch().getGesuchsteller1().getGesuchstellerJA().getVorname(), matchingVerfuegungExport.getGesuchsteller().getVorname());
		Assert.assertEquals(verfuegung.getBetreuung().getKind().getKindJA().getNachname(), matchingVerfuegungExport.getKind().getNachname());

		for (VerfuegungZeitabschnitt verfZeitabschn : verfuegung.getZeitabschnitte()) {
			ZeitabschnittExportDTO matchingZeitAbschn = matchingVerfuegungExport.getZeitabschnitte().stream().filter(zeitabschnExpo -> zeitabschnExpo.getVon().equals(verfZeitabschn.getGueltigkeit().getGueltigAb())).reduce(StreamsUtil.toOnlyElement()).get();
			Assert.assertEquals(matchingZeitAbschn.getBis(),(verfZeitabschn.getGueltigkeit().getGueltigBis()));
			Assert.assertEquals(0, verfZeitabschn.getVerguenstigung().compareTo(matchingZeitAbschn.getVerguenstigung()));
			Assert.assertEquals(verfZeitabschn.getAnspruchberechtigtesPensum(), matchingZeitAbschn.getAnspruchPct());
		}
	}
}
