package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Diese Klasse implementiert die Methode "Deployment" fuer die Arquillian Tests und muss
 * von allen Testklassen erweitert werden.
 */
public abstract class AbstractEbeguTest {

	public static Archive<?> createTestArchive(@Nullable Class[] classesToAdd) {

		PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
		File[] runtimeDeps = pom.importRuntimeDependencies().resolve().withTransitivity().asFile();
		File[] testDeps = pom.importTestDependencies().resolve().withoutTransitivity().asFile();


		// wir fuegen die packages einzeln hinzu weil sonst klassen die im shared sind und das gleiche package haben doppelt eingefuegt werden
		WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
			.addPackages(true, "ch/dvbern/ebegu/persistence")
			.addPackages(true, "ch/dvbern/ebegu/services")
			.addPackages(true, "ch/dvbern/ebegu/validation")
//			.addPackages(true, "ch/dvbern/ebegu/errors")
//			.addPackages(true, "ch/dvbern/ebegu/entities")
			.addPackages(true, "ch/dvbern/ebegu/tests")
//			.addPackages(true, "ch/dvbern/ebegu/enums")
			.addClasses(AbstractEbeguTest.class, Persistence.class,
				ISessionContextService.class, AbstractEntity.class)

			.addAsLibraries(runtimeDeps)
			.addAsLibraries(testDeps)

			.addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
			.addAsWebInfResource("META-INF/test-beans.xml", "beans.xml")
			.addAsResource("META-INF/test-orm.xml", "META-INF/orm.xml")
				// Deploy our test datasource
			.addAsWebInfResource("test-ds.xml");
		if (classesToAdd != null) {
			webArchive.addClasses(classesToAdd);
		}
		//Folgende Zeile gibt im /tmp dir das archiv aus zum debuggen nuetzlich
		new ZipExporterImpl(webArchive).exportTo(new File(System.getProperty("java.io.tmpdir"), "myWebArchive.war"), true);
		return webArchive;
	}

	public static Archive<?> createTestArchive() {

		return createTestArchive(null);
	}

}
