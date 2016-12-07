package ch.dvbern.ebegu.tests.vorlagen;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Test fuer PrintUtil
 */
public class PrintUtilTest {

	@Test
	public void testGetGesuchstellerAdresseWithNoAdressen() {
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer();
		gesuchsteller.setAdressen(new ArrayList<>());

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertFalse(gesuchstellerAdresse.isPresent());
	}

	@Test
	public void testGetGesuchstellerAdresseWithJustWohnadresse() {
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer();

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(0), gesuchstellerAdresse.get());
	}

	@Test
	public void testGetGesuchstellerAdresseWithUmzugsadresse() {
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer();
		final GesuchstellerAdresseContainer umzugsadresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		umzugsadresse.getGesuchstellerAdresseJA().setStrasse("newStrasse");
		gesuchsteller.addAdresse(umzugsadresse);

		//update Gueltigkeiten
		final LocalDate now = LocalDate.now();
		gesuchsteller.getAdressen().get(0).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now.minusMonths(5), now.minusDays(1))); // before now
		gesuchsteller.getAdressen().get(1).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now, now.plusMonths(2))); // now liegt in dieser Periode

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(1), gesuchstellerAdresse.get());
		Assert.assertEquals("newStrasse", gesuchstellerAdresse.get().extractStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresse() {
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer();
		final GesuchstellerAdresseContainer korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(korrespondenzadresse, gesuchstellerAdresse.get());
		Assert.assertEquals("korrespondezStrasse", gesuchstellerAdresse.get().extractStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresseAndUmzugsadresse() {
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer();
		final GesuchstellerAdresseContainer korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);
		final GesuchstellerAdresseContainer umzugsadresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		umzugsadresse.getGesuchstellerAdresseJA().setStrasse("newStrasse");
		gesuchsteller.addAdresse(umzugsadresse);

		//update Gueltigkeiten
		final LocalDate now = LocalDate.now();
		final List<GesuchstellerAdresseContainer> wohnAdressen = gesuchsteller.getAdressen().stream()
			.filter(gesuchstellerAdresse -> !gesuchstellerAdresse.extractIsKorrespondenzAdresse()).sorted((o1, o2) ->
				o1.extractGueltigkeit().getGueltigAb().compareTo(o2.extractGueltigkeit().getGueltigAb()))
			.collect(Collectors.toList());
		wohnAdressen.get(0).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now.minusMonths(5), now.minusDays(1))); // before now
		wohnAdressen.get(1).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now, now.plusMonths(2))); // now liegt in dieser Periode

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(korrespondenzadresse, gesuchstellerAdresse.get());
		Assert.assertEquals("korrespondezStrasse", gesuchstellerAdresse.get().extractStrasse());
	}


	@Nonnull
	private GesuchstellerAdresseContainer createKorrespondenzadresse(GesuchstellerContainer gesuchsteller) {
		final GesuchstellerAdresseContainer korrespondenzadresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		korrespondenzadresse.getGesuchstellerAdresseJA().setStrasse("korrespondezStrasse");
		korrespondenzadresse.getGesuchstellerAdresseJA().setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		gesuchsteller.addAdresse(korrespondenzadresse);
		return korrespondenzadresse;
	}
}
