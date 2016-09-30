package ch.dvbern.ebegu.rest.test.util;

import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Test fuer RestUtil
 */
public class RestUtilTest {

	public static final String institutionID1 = "11111111-1111-1111-1111-111111111103";
	public static final String institutionID2 = "11111111-1111-1111-1111-111111111104";

	@Test
	public void purgeKinderAndBetreuungenOfInstitutionenTestNoInstitution() {
		final JaxKindContainer kind = prepareKindData();
		Collection<JaxKindContainer> kinder = new ArrayList<>();
		kinder.add(kind);

		RestUtil.purgeKinderAndBetreuungenOfInstitutionen(kinder, new ArrayList<>());

		Assert.assertNotNull(kinder);
		Assert.assertEquals(0, kinder.size());
	}

	@Test
	public void purgeKinderAndBetreuungenOfInstitutionenTestOneBetreuung() {
		final JaxKindContainer kind = prepareKindData();
		Collection<JaxKindContainer> kinder = new ArrayList<>();
		kinder.add(kind);
		Collection<Institution> institutionen = createArrayWithOneInstitution();

		RestUtil.purgeKinderAndBetreuungenOfInstitutionen(kinder, institutionen);

		Assert.assertNotNull(kinder);
		Assert.assertEquals(1, kinder.size());Assert.assertNotNull(kind.getBetreuungen());
		final JaxKindContainer kindContainer = kinder.iterator().next();
		Assert.assertEquals(1, kindContainer.getBetreuungen().size());
		Assert.assertEquals(institutionID1,
			kindContainer.getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getId());
	}

	@Test
	public void purgeSingleKindAndBetreuungenOfInstitutionenTestNoInstitution() {
		final JaxKindContainer kind = prepareKindData();

		RestUtil.purgeSingleKindAndBetreuungenOfInstitutionen(kind, new ArrayList<>());

		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(0, kind.getBetreuungen().size());

	}

	@Test
	public void purgeSingleKindAndBetreuungenOfInstitutionenTestOneOfTwoInstitution() {
		final JaxKindContainer kind = prepareKindData();

		Collection<Institution> institutionen = createArrayWithOneInstitution();

		RestUtil.purgeSingleKindAndBetreuungenOfInstitutionen(kind, institutionen);

		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(1, kind.getBetreuungen().size());
		Assert.assertEquals(institutionID1,
			kind.getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getId());
	}

	@Test
	public void purgeSingleKindAndBetreuungenOfInstitutionenTestTwoOfTwoInstitution() {
		final JaxKindContainer kind = prepareKindData();

		Collection<Institution> institutionen = createArrayWithTwoInstitutions();

		RestUtil.purgeSingleKindAndBetreuungenOfInstitutionen(kind, institutionen);

		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(2, kind.getBetreuungen().size());
		final Iterator<JaxBetreuung> iterator = kind.getBetreuungen().iterator();
		Assert.assertEquals(institutionID2,
			iterator.next().getInstitutionStammdaten().getInstitution().getId());
		Assert.assertEquals(institutionID1,
			iterator.next().getInstitutionStammdaten().getInstitution().getId());
	}

	@Test
	public void purgeSingleKindAndBetreuungenOfInstitutionenTestStatusSCHULAMT() {
		final JaxKindContainer kind = prepareKindData();
		kind.getBetreuungen().iterator().next().setBetreuungsstatus(Betreuungsstatus.SCHULAMT);
		Collection<Institution> institutionen = createArrayWithTwoInstitutions();

		RestUtil.purgeSingleKindAndBetreuungenOfInstitutionen(kind, institutionen);

		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(1, kind.getBetreuungen().size());
		final JaxBetreuung betreuung = kind.getBetreuungen().iterator().next();
		Assert.assertEquals(institutionID1,
			betreuung.getInstitutionStammdaten().getInstitution().getId());
		Assert.assertNotEquals(Betreuungsstatus.SCHULAMT,
			betreuung.getBetreuungsstatus());
	}


	// HELP METHODS

	@Nonnull
	private JaxKindContainer prepareKindData() {
		final JaxKindContainer kind = TestJaxDataUtil.createTestJaxKindContainer();
		Set<JaxBetreuung> betreuungen = new HashSet<>();

		final JaxBetreuung betreuung1 = TestJaxDataUtil.createTestJaxBetreuung();
		JaxInstitution institution1 = TestJaxDataUtil.createTestJaxInstitution();
		institution1.setId(institutionID1);
		betreuung1.setId("11111111-1111-1111-1111-789456123256");
		betreuung1.getInstitutionStammdaten().setInstitution(institution1);
		betreuungen.add(betreuung1);

		final JaxBetreuung betreuung2 = TestJaxDataUtil.createTestJaxBetreuung();
		JaxInstitution institution2 = TestJaxDataUtil.createTestJaxInstitution();
		institution2.setId(institutionID2);
		betreuung2.setId("11111111-1111-1111-1111-789456123288");
		betreuung2.setBetreuungNummer(2);
		betreuung2.getInstitutionStammdaten().setInstitution(institution2);
		betreuungen.add(betreuung2);

		kind.setBetreuungen(betreuungen);

		return kind;
	}

	@Nonnull
	private Collection<Institution> createArrayWithOneInstitution() {
		Collection<Institution> institutionen = new ArrayList<>();
		final Institution institution = TestDataUtil.createDefaultInstitution();
		institution.setId(institutionID1);
		institutionen.add(institution);
		return institutionen;
	}

	@Nonnull
	private Collection<Institution> createArrayWithTwoInstitutions() {
		Collection<Institution> institutionen = createArrayWithOneInstitution();
		final Institution institution2 = TestDataUtil.createDefaultInstitution();
		institution2.setId(institutionID2);
		institutionen.add(institution2);
		return institutionen;
	}
}
