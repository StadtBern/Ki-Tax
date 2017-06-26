package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckBetreuungspensum;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

import static ch.dvbern.ebegu.tests.util.ValidationTestHelper.assertNotViolated;
import static ch.dvbern.ebegu.tests.util.ValidationTestHelper.assertViolated;


/**
 * Tests der den Validator fuer die Werte in Betreuungspensum checkt
 */
public class CheckBetreuungspensumValidatorTest {

	private ValidatorFactory customFactory;

	@Before
	public void setUp() throws Exception {
		// see https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/chapter-bootstrapping.html#_constraintvalidatorfactory
		Configuration<?> config = Validation.byDefaultProvider().configure();
		config.constraintValidatorFactory(new ValidationTestConstraintValidatorFactory(null));
		this.customFactory = config.buildValidatorFactory();
	}

	@Test
	public void testKitaGSWrongValue() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.KITA, 9, 9);
		// Das passiert weil wir nur den ersten falschen Werten checken. Deswegen als wir den Fehler in betreuungspensumGS finden, checken
		// wir nicht weiter und betreuungspensumJA wirft keine Violation
		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testKitaJAWrongValue() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.KITA, 10, 9);
		// Jetzt ist betreuungspensumGS richtig und wir finden den Fehler in betreuungspensumJA
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testKitaRightValues() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.KITA, 10, 10);
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testTageselternGSWrongValue() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND, 19, 19);
		// Das passiert weil wir nur den ersten falschen Werten checken. Deswegen als wir den Fehler in betreuungspensumGS finden, checken
		// wir nicht weiter und betreuungspensumJA wirft keine Violation
		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testTageselternJAWrongValue() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND, 20, 19);
		// Jetzt ist betreuungspensumGS richtig und wir finden den Fehler in betreuungspensumJA
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testTageselternRightValues() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND, 20, 20);
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testTagiGSWrongValue() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGI, 59, 59);
		// Das passiert weil wir nur den ersten falschen Werten checken. Deswegen als wir den Fehler in betreuungspensumGS finden, checken
		// wir nicht weiter und betreuungspensumJA wirft keine Violation
		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testTagiJAWrongValue() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGI, 60, 59);
		// Jetzt ist betreuungspensumGS richtig und wir finden den Fehler in betreuungspensumJA
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testTagiRightValues() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGI, 60, 60);
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	/**
	 * Fuer Tagesschule gibt es keinen Minimalwert
	 */
	@Test
	public void testTagesschule() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGESSCHULE, 1, 1);
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[0].betreuungspensumJA.pensum");
	}

	@Test
	public void testSeveralBetreuungspensumContainers() {
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.TAGI, 60, 60);

		BetreuungspensumContainer betPensContainer = TestDataUtil.createBetPensContainer(betreuung);
		betPensContainer.getBetreuungspensumGS().setPensum(59);
		betPensContainer.getBetreuungspensumJA().setPensum(60);
		betreuung.getBetreuungspensumContainers().add(betPensContainer);

		//es ist ein Set. Daher muessen wir den Index finden
		//es ist etwas problematisch dass wir den Index dem Client uebergeben wenn es eigentlich auf dem Server ein Set (und nicht eine Liste ist). Der Index wird durch die Sortierung gegeben
		int i = 0;
		for (BetreuungspensumContainer betreuungspensumContainer : betreuung.getBetreuungspensumContainers()) {
			if (betreuungspensumContainer.equals(betPensContainer)) {
				break;
			}
			i++;
		}

		assertViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[" + i + "].betreuungspensumGS.pensum");
		assertNotViolated(CheckBetreuungspensum.class, betreuung, customFactory, "betreuungspensumContainers[" + i + "].betreuungspensumJA.pensum");
	}


	// HELP METHODS

	/**
	 * creates a Betreeung with {@link BetreuungspensumContainer} gs and ja with the specified Pensum
	 */
	@Nonnull
	private Betreuung createBetreuung(BetreuungsangebotTyp betreuungsangebotTyp, int pensumGS, int pensumJA) {
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1718());
		Betreuung betreuung = TestDataUtil.createDefaultBetreuung();
		betreuung.getKind().setGesuch(gesuch); // Aktuell nur in 1 Richtung verknuepft
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(betreuungsangebotTyp);
		BetreuungspensumContainer betPensContainer = TestDataUtil.createBetPensContainer(betreuung);
		Set<BetreuungspensumContainer> containerSet = new HashSet<>();
		containerSet.add(betPensContainer);
		betPensContainer.getBetreuungspensumGS().setPensum(pensumGS);
		betPensContainer.getBetreuungspensumJA().setPensum(pensumJA);
		betreuung.setBetreuungspensumContainers(containerSet);
		return betreuung;
	}
}
