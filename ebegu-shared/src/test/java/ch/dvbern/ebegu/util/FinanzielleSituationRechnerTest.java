package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Kinderabzug;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests fuer FinanzielleSituationRechner
 */
public class FinanzielleSituationRechnerTest {

	private static final double DELTA = 1e-15;
	public static final LocalDate DATE = LocalDate.of(2005, 12, 31);

	private FinanzielleSituationRechner finSitRechner;


	@Test
	public void testCalculateFamiliengroesseNullGesuch() {
		finSitRechner = new FinanzielleSituationRechner();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(null, null);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNullDate() {
		Gesuch gesuch = createEmptyGesuch();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, null);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNoGesuchSteller() {
		Gesuch gesuch = createEmptyGesuch();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseOneGesuchSteller() {
		Gesuch gesuch = createEmptyGesuch();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuch.setGesuchsteller1(gesuchsteller);
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(1, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseTwoGesuchSteller() {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithGanzerAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithHalberAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.HALBER_ABZUG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(2.5, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeinAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEIN_ABZUG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeineStErklaerungKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithWrongGeburtsdatum() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithCorrectGeburtsdatum() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE);
		Assert.assertEquals(3, familiengroesse, DELTA);
	}


	// HELP METHODS

	@Nonnull
	private Gesuch createGesuchWithTwoGesuchsteller() {
		Gesuch gesuch = createEmptyGesuch();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuch.setGesuchsteller1(gesuchsteller);
		gesuch.setGesuchsteller2(gesuchsteller);
		return gesuch;
	}

	@Nonnull
	private Gesuch createEmptyGesuch() {
		finSitRechner = new FinanzielleSituationRechner();
		return new Gesuch();
	}

	@Nonnull
	private Gesuch createGesuchWithKind(Kinderabzug abzug) {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		Set<KindContainer> kindContainers = new HashSet<>();
		KindContainer kindContainer = new KindContainer();
		Kind kindJA = new Kind();
		kindJA.setKinderabzug(abzug);
		kindJA.setGeburtsdatum(LocalDate.of(2006, 5, 25));
		kindContainer.setKindJA(kindJA);
		kindContainers.add(kindContainer);
		gesuch.setKindContainers(kindContainers);
		return gesuch;
	}

}
