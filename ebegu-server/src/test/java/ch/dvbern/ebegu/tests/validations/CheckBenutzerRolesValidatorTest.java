package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.UserRole;
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
		Benutzer benutzer = createBenutzer(UserRole.SACHBEARBEITER_INSTITUTION, null, null);
		Assert.assertFalse(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleInstitutionWithInstitution() {
		final Institution institution = new Institution();
		Benutzer benutzer = createBenutzer(UserRole.SACHBEARBEITER_INSTITUTION, null, institution);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleTraegerschaftWithoutTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, null, null);
		Assert.assertFalse(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleTraegerschaftWithTraegerschaft() {
		final Traegerschaft traegerschaft = new Traegerschaft();
		Benutzer benutzer = createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, traegerschaft, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleAdminNoInstitutionTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.ADMIN, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleGesuchstellerNoInstitutionTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.GESUCHSTELLER, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleJuristNoInstitutionTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.JURIST, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleRevisorNoInstitutionTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.REVISOR, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleJANoInstitutionTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.SACHBEARBEITER_JA, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	@Test
	public void testCheckBenutzerRoleSteueramtNoInstitutionTraegerschaft() {
		Benutzer benutzer = createBenutzer(UserRole.STEUERAMT, null, null);
		Assert.assertTrue(validator.isValid(benutzer, null));
	}

	// HELP METHODS

	private Benutzer createBenutzer(UserRole role, Traegerschaft traegerschaft, Institution institution) {
		final Benutzer benutzer = new Benutzer();
		benutzer.setTraegerschaft(traegerschaft);
		benutzer.setInstitution(institution);
		benutzer.setRole(role);
		return benutzer;
	}


}
