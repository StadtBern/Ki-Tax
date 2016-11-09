package ch.dvbern.ebegu.tests.vorlagen;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Gueltigkeit;
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
		final Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		gesuchsteller.setAdressen(new ArrayList<>());

		final Optional<GesuchstellerAdresse> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertFalse(gesuchstellerAdresse.isPresent());
	}

	@Test
	public void testGetGesuchstellerAdresseWithJustWohnadresse() {
		final Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();

		final Optional<GesuchstellerAdresse> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(0), gesuchstellerAdresse.get());
	}

	@Test
	public void testGetGesuchstellerAdresseWithUmzugsadresse() {
		final Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		final GesuchstellerAdresse umzugsadresse = TestDataUtil.createDefaultGesuchstellerAdresse();
		umzugsadresse.setStrasse("newStrasse");
		gesuchsteller.addAdresse(umzugsadresse);

		//update Gueltigkeiten
		final LocalDate now = LocalDate.now();
		gesuchsteller.getAdressen().get(0).setGueltigkeit(new DateRange(now.minusMonths(5), now.minusDays(1))); // before now
		gesuchsteller.getAdressen().get(1).setGueltigkeit(new DateRange(now, now.plusMonths(2))); // now liegt in dieser Periode

		final Optional<GesuchstellerAdresse> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(1), gesuchstellerAdresse.get());
		Assert.assertEquals("newStrasse", gesuchstellerAdresse.get().getStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresse() {
		final Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		final GesuchstellerAdresse korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);

		final Optional<GesuchstellerAdresse> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(korrespondenzadresse, gesuchstellerAdresse.get());
		Assert.assertEquals("korrespondezStrasse", gesuchstellerAdresse.get().getStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresseAndUmzugsadresse() {
		final Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		final GesuchstellerAdresse korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);
		final GesuchstellerAdresse umzugsadresse = TestDataUtil.createDefaultGesuchstellerAdresse();
		umzugsadresse.setStrasse("newStrasse");
		gesuchsteller.addAdresse(umzugsadresse);

		//update Gueltigkeiten
		final LocalDate now = LocalDate.now();
		final List<GesuchstellerAdresse> wohnAdressen = gesuchsteller.getAdressen().stream()
			.filter(gesuchstellerAdresse -> !gesuchstellerAdresse.isKorrespondenzAdresse()).sorted(Gueltigkeit.GUELTIG_AB_COMPARATOR)
			.collect(Collectors.toList());
		wohnAdressen.get(0).setGueltigkeit(new DateRange(now.minusMonths(5), now.minusDays(1))); // before now
		wohnAdressen.get(1).setGueltigkeit(new DateRange(now, now.plusMonths(2))); // now liegt in dieser Periode

		final Optional<GesuchstellerAdresse> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(korrespondenzadresse, gesuchstellerAdresse.get());
		Assert.assertEquals("korrespondezStrasse", gesuchstellerAdresse.get().getStrasse());
	}


	@Nonnull
	private GesuchstellerAdresse createKorrespondenzadresse(Gesuchsteller gesuchsteller) {
		final GesuchstellerAdresse korrespondenzadresse = TestDataUtil.createDefaultGesuchstellerAdresse();
		korrespondenzadresse.setStrasse("korrespondezStrasse");
		korrespondenzadresse.setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		gesuchsteller.addAdresse(korrespondenzadresse);
		return korrespondenzadresse;
	}
}
