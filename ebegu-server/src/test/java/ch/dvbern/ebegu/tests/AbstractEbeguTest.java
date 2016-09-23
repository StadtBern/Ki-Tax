package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Diese Klasse implementiert die Methode "Deployment" fuer die Arquillian Tests und muss von allen Testklassen
 * erweitert werden.
 */
@ArquillianSuiteDeployment
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public abstract class AbstractEbeguTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

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
			// .addPackages(true, "ch/dvbern/ebegu/vorlagen/finanziellesituation")
			// .addPackages(true, "ch/dvbern/ebegu/errors")
			// .addPackages(true, "ch/dvbern/ebegu/entities")
			.addPackages(true, "ch/dvbern/ebegu/tests")
			// .addPackages(true, "ch/dvbern/ebegu/enums")
			.addClasses(AbstractEbeguTest.class, Persistence.class, ISessionContextService.class, AbstractEntity.class)

			.addAsLibraries(runtimeDeps).addAsLibraries(testDeps)

			.addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml").addAsResource("vorlagen/Verfuegungsmuster.docx", "vorlagen/Verfuegungsmuster.docx")
			.addAsResource("vorlagen/Berechnungsgrundlagen.docx", "vorlagen/Berechnungsgrundlagen.docx")
			.addAsResource("vorlagen/Begleitschreiben.docx", "vorlagen/Begleitschreiben.docx")
			.addAsResource("font/sRGB.profile", "font/sRGB.profile").addAsWebInfResource("META-INF/test-beans.xml", "beans.xml")
			.addAsResource("META-INF/test-orm.xml", "META-INF/orm.xml")
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
	 * Erstellt das byte Dokument in einem temp File in einem temp folder
	 * <p/>
	 * <b>ACHTUNG: </b> die temp files werden nach dem Test <b>sofort wieder geloescht</b>
	 *
	 * @param data
	 * @param fileName
	 * @return das Temp file oder <code>null</code>
	 * @throws IOException
	 */
	protected final File writeToTempDir(final byte[] data, final String fileName) throws IOException {

		File tempFile = null;

		FileOutputStream fos = null;
		try {
			// create temp file in junit temp folder
			tempFile = tempFolder.newFile(fileName);
			System.out.println("Writing tempfile to: " + tempFile);
			fos = new FileOutputStream(tempFile);
			fos.write(data);
			fos.close();
		} finally {
			if (fos != null) {
				fos.close();
			}

		}
		return tempFile;
	}
}
