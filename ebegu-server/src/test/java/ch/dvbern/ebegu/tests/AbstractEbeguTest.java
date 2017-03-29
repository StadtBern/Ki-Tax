package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.services.TraegerschaftService;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.ebegu.tets.util.LoginmoduleAndCacheSetupTask;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import java.io.File;

/**
 * Diese Klasse implementiert die Methode "Deployment" fuer die Arquillian Tests und muss von allen Testklassen
 * erweitert werden.
 */
@ArquillianSuiteDeployment
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
@ServerSetup(LoginmoduleAndCacheSetupTask.class)
public abstract class AbstractEbeguTest {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEbeguTest.class);

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private Persistence<?> persistence;

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

	@Deployment
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createTestArchive() {

		return createTestArchive(null);
	}

	public static Archive<?> createTestArchive(@Nullable Class[] classesToAdd) {

		PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
		File[] runtimeDeps = pom.importRuntimeDependencies().resolve().withTransitivity().asFile();
		File[] testDeps = pom.importTestDependencies().resolve().withoutTransitivity().asFile();

		// wir fuegen die packages einzeln hinzu weil sonst klassen die im shared sind und das gleiche package haben
		// doppelt eingefuegt werden

		WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war").addPackages(true, "ch/dvbern/ebegu/persistence").addPackages(true, "ch/dvbern/ebegu/rechner")
			.addPackages(true, "ch/dvbern/ebegu/rules").addPackages(true, "ch/dvbern/ebegu/services").addPackages(true, "ch/dvbern/ebegu/validation")
			.addPackages(true, "ch/dvbern/ebegu/vorlagen")
			.addPackages(true, "ch/dvbern/ebegu/tests")
			.addPackages(true, "ch/dvbern/ebegu/tests/util")
			.addPackages(true, "ch/dvbern/ebegu/mail")
			.addPackages(true, "ch/dvbern/ebegu/ws/personensuche/service")
			.addPackages(true, "ch/dvbern/ebegu/ewk")
			// .addPackages(true, "ch/dvbern/ebegu/enums")
			.addClasses(AbstractEbeguLoginTest.class, Persistence.class, ISessionContextService.class, AbstractEntity.class)
			.addPackages(true, "ch/dvbern/ebegu/services/authentication")
//			.addClass(Authorizer.class)
			.addAsLibraries(runtimeDeps).addAsLibraries(testDeps)
			.addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
			.addAsResource("reporting/GesuchStichtag.xlsx", "reporting/GesuchStichtag.xlsx")
			.addAsResource("reporting/GesuchZeitraum.xlsx", "reporting/GesuchZeitraum.xlsx")
			.addAsResource("reporting/ZahlungAuftrag.xlsx", "reporting/ZahlungAuftrag.xlsx")
			.addAsResource("reporting/Kanton.xlsx", "reporting/Kanton.xlsx")
			.addAsResource("reporting/Mitarbeiterinnen.xlsx", "reporting/Mitarbeiterinnen.xlsx")
			.addAsResource("reporting/GesuchstellerKinderBetreuung.xlsx", "reporting/GesuchstellerKinderBetreuung.xlsx")
			.addAsResource("vorlagen/Verfuegungsmuster_kita.docx", "vorlagen/Verfuegungsmuster_kita.docx")
			.addAsResource("vorlagen/Verfuegungsmuster_tageseltern_kleinkinder.docx", "vorlagen/Verfuegungsmuster_tageseltern_kleinkinder.docx")
			.addAsResource("vorlagen/Verfuegungsmuster_tageseltern_schulkinder.docx", "vorlagen/Verfuegungsmuster_tageseltern_schulkinder.docx")
			.addAsResource("vorlagen/Verfuegungsmuster_tagesstaette_schulkinder.docx", "vorlagen/Verfuegungsmuster_tagesstaette_schulkinder.docx")
			.addAsResource("vorlagen/Berechnungsgrundlagen.docx", "vorlagen/Berechnungsgrundlagen.docx")
			.addAsResource("vorlagen/Begleitschreiben.docx", "vorlagen/Begleitschreiben.docx")
			.addAsResource("vorlagen/1_Mahnung.docx", "vorlagen/1_Mahnung.docx")
			.addAsResource("vorlagen/2_Mahnung.docx", "vorlagen/2_Mahnung.docx")
			.addAsResource("mail/templates/InfoBetreuungAbgelehnt.ftl", "mail/templates/InfoBetreuungAbgelehnt.ftl")
			.addAsResource("mail/templates/InfoBetreuungenBestaetigt.ftl", "mail/templates/InfoBetreuungenBestaetigt.ftl")
			.addAsResource("mail/templates/InfoMahnung.ftl", "mail/templates/InfoMahnung.ftl")
			.addAsResource("mail/templates/InfoVerfuegtGesuch.ftl", "mail/templates/InfoVerfuegtGesuch.ftl")
			.addAsResource("mail/templates/InfoVerfuegtMutation.ftl", "mail/templates/InfoVerfuegtMutation.ftl")
			.addAsResource("font/sRGB.profile", "font/sRGB.profile")
			.addAsWebInfResource("META-INF/test-beans.xml", "beans.xml")
			.addAsResource("META-INF/test-orm.xml", "META-INF/orm.xml")
			.addAsManifestResource("META-INF/TEST-MANIFEST.MF", "MANIFEST.MF")
			//deploy our test loginmodule
			.addAsResource("testogin-users.properties", "users.properties")
			.addAsResource("testlogin-roles.properties", "roles.properties")
			.addAsWebInfResource("META-INF/test-jboss-web.xml", "jboss-web.xml")
			// Deploy our test datasource
			.addAsWebInfResource("test-ds.xml");
		if (classesToAdd != null) {
			webArchive.addClasses(classesToAdd);
		}
		// Folgende Zeile gibt im /tmp dir das archiv aus zum debuggen nuetzlich
		new ZipExporterImpl(webArchive).exportTo(new File(System.getProperty("java.io.tmpdir"), "myWebArchive.war"), true);
		return webArchive;
	}

	/**
	 * Helper für init. Speichert Gesuchsperiode in DB
	 */
	protected Gesuchsperiode createGesuchsperiode(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createGesuchsperiode1718();
		gesuchsperiode.setActive(active);
		return gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
	}

	/**
	 * Helper für init. Speichert Traegerschaften, Mandant und Institution in DB
	 */
	protected Mandant insertInstitutionen() {

		final InstitutionStammdaten institutionStammdatenKitaAaregg = TestDataUtil.createInstitutionStammdatenKitaWeissenstein();
		final InstitutionStammdaten institutionStammdatenKitaBruennen = TestDataUtil.createInstitutionStammdatenKitaBruennen();
		final InstitutionStammdaten institutionStammdatenTagiAaregg = TestDataUtil.createInstitutionStammdatenTagiWeissenstein();

		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		traegerschaftService.saveTraegerschaft(traegerschaft);
		institutionStammdatenKitaAaregg.getInstitution().setTraegerschaft(traegerschaft);
		institutionStammdatenKitaBruennen.getInstitution().setTraegerschaft(traegerschaft);
		institutionStammdatenTagiAaregg.getInstitution().setTraegerschaft(traegerschaft);

		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		institutionStammdatenKitaAaregg.getInstitution().setMandant(mandant);
		institutionStammdatenKitaBruennen.getInstitution().setMandant(mandant);
		institutionStammdatenTagiAaregg.getInstitution().setMandant(mandant);

		institutionService.createInstitution(institutionStammdatenKitaAaregg.getInstitution());
		institutionStammdatenService.saveInstitutionStammdaten(institutionStammdatenKitaAaregg);
		institutionStammdatenService.saveInstitutionStammdaten(institutionStammdatenTagiAaregg);

		institutionService.createInstitution(institutionStammdatenKitaBruennen.getInstitution());
		institutionStammdatenService.saveInstitutionStammdaten(institutionStammdatenKitaBruennen);

		Assert.assertNotNull(institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_WEISSENSTEIN_KITA));
		Assert.assertNotNull(institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_BRUENNEN_KITA));
		Assert.assertNotNull(institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_WEISSENSTEIN_TAGI));
		return mandant;
	}

	/**
	 * Helper für init. Speichert Benutzer in DB
	 */
	protected void createBenutzer(Mandant mandant) {
		try{
			JBossLoginContextFactory.createLoginContext("superadmin", "superadmin").login();
		} catch (LoginException ex){
			LOG.error("could not login as admin user for test");
		}
		Benutzer i = TestDataUtil.createBenutzer(UserRole.ADMIN, "admin", null, null, mandant);
		persistence.persist(i);
	}
}
