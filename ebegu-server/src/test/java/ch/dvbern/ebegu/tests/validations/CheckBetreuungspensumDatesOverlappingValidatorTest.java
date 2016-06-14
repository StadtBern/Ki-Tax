package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlapping;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ch.dvbern.lib.beanvalidation.util.ValidationTestHelper.assertNotViolated;
import static ch.dvbern.lib.beanvalidation.util.ValidationTestHelper.assertViolated;

/**
 * Test fuer {@link ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlappingValidator}
 */
@Ignore //todo homa mock param service for this test
public class CheckBetreuungspensumDatesOverlappingValidatorTest {

	@Test
	public void testCheckBetreuungspensumDatesOverlapping() {
		Betreuung betreuung = createBetreuungWithOverlappedDates(true); //overlapping
		assertViolated(CheckBetreuungspensumDatesOverlapping.class, betreuung, "");
	}

	@Test
	public void testCheckBetreuungspensumDatesNotOverlapping() {
		Betreuung betreuung = createBetreuungWithOverlappedDates(false); // not overlapping
		assertNotViolated(CheckBetreuungspensumDatesOverlapping.class, betreuung, "");
	}

	@Nonnull
	private Betreuung createBetreuungWithOverlappedDates(boolean overlapping) {
		Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		Set<BetreuungspensumContainer> containerSet = new HashSet<>();

		BetreuungspensumContainer betPensContainer = TestDataUtil.createBetPensContainer(betreuung);
		betPensContainer.setBetreuungspensumGS(null); //wir wollen nur JA container testen
		betPensContainer.getBetreuungspensumJA().getGueltigkeit().setGueltigAb(LocalDate.of(2000, 10, 10));
		betPensContainer.getBetreuungspensumJA().getGueltigkeit().setGueltigBis(LocalDate.of(2005, 10, 10));
		containerSet.add(betPensContainer);
		BetreuungspensumContainer betPensContainer2 = TestDataUtil.createBetPensContainer(betreuung);
		betPensContainer.setBetreuungspensumGS(null);
		betPensContainer2.getBetreuungspensumJA().getGueltigkeit().setGueltigAb(overlapping ? LocalDate.of(2003, 10, 10) : LocalDate.of(2006, 10, 10));
		betPensContainer2.getBetreuungspensumJA().getGueltigkeit().setGueltigBis(LocalDate.of(2008, 10, 10));
		containerSet.add(betPensContainer2);
		betreuung.setBetreuungspensumContainers(containerSet);
		return betreuung;
	}

}
