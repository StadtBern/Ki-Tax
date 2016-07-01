package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.tests.AbstractEbeguTest;
import ch.dvbern.ebegu.tests.util.ValidationTestHelper;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlapping;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Test fuer {@link ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlappingValidator}
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class CheckBetreuungspensumDatesOverlappingValidatorTest extends AbstractEbeguTest {

	private ValidatorFactory customFactory;

	@PersistenceUnit(unitName = "ebeguPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Before
	public void setUp() throws Exception {
		// see https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/chapter-bootstrapping.html#_constraintvalidatorfactory
		Configuration<?> config = Validation.byDefaultProvider().configure();
		config.constraintValidatorFactory(new ValidationTestConstraintValidatorFactory(entityManagerFactory));
		this.customFactory = config.buildValidatorFactory();
	}

	@Test
	public void testCheckBetreuungspensumDatesOverlapping() {
		Betreuung betreuung = createBetreuungWithOverlappedDates(true); //overlapping
		ValidationTestHelper.assertViolated(CheckBetreuungspensumDatesOverlapping.class, betreuung, customFactory ,"" );
	}

	@Test
	public void testCheckBetreuungspensumDatesNotOverlapping() {
		Betreuung betreuung = createBetreuungWithOverlappedDates(false); // not overlapping
		ValidationTestHelper.assertNotViolated(CheckBetreuungspensumDatesOverlapping.class, betreuung, customFactory, "");
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
