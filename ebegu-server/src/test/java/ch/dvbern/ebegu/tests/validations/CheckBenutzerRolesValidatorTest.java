package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckBenutzerRolesValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests fuer {@link ch.dvbern.ebegu.validators.CheckBenutzerRolesValidator}
 */
public class CheckBenutzerRolesValidatorTest {

	private CheckBenutzerRolesValidator validator;

	@Before
	public void setUp() {
		validator = new CheckBenutzerRolesValidator();
	}


	@Test
	public void testCheckBenutzerRoleInstitutionWithoutInstitution() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_INSTITUTION, null, null, null);
		Assert.assertFalse(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleInstitutionWithInstitution() {
		final Institution institution = new Institution();
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_INSTITUTION, null, institution, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleTraegerschaftWithoutTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, null, null, null);
		Assert.assertFalse(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleTraegerschaftWithTraegerschaft() {
		final Traegerschaft traegerschaft = new Traegerschaft();
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, traegerschaft, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleAdminNoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.ADMIN, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleGesuchstellerNoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleJuristNoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.JURIST, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleSchulamtNoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.SCHULAMT, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleRevisorNoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.REVISOR, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleJANoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleSteueramtNoInstitutionTraegerschaft() {
		Benutzer benutzer = TestDataUtil.createBenutzer(UserRole.STEUERAMT, null, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	// HELP METHODS

}
