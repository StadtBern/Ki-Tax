/*
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.dvbern.ebegu;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.services.ApplicationPropertyService;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;


/**
 * Dies ist ein Beispiel einer Arquillian Test Klasse. Es wird vor jedem Test die Datenbank mit dem leeren Dataset
 * initialisiert.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class ApplicationPropertyServiceTest {


	@Deployment
	public static Archive<?> createTestArchive() {

		PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
		File[] runtimeDeps = pom.importRuntimeDependencies().resolve().withTransitivity().asFile();
		File[] testDeps = pom.importTestDependencies().resolve().withTransitivity().asFile();

		// wir fuegen die packages einzeln hinzu weil sonst klassen die im shared sind und das gleiche package haben doppelt eingefuegt werden
		WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
			.addPackages(true, "ch/dvbern/ebegu/persistence")
			.addPackages(true, "ch/dvbern/ebegu/services")
			.addPackages(true, "ch/dvbern/ebegu/validation")
			.addClasses(Persistence.class, ISessionContextService.class, AbstractEntity.class)

			.addAsLibraries(runtimeDeps)
			.addAsLibraries(testDeps)

			.addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
			.addAsWebInfResource("META-INF/test-beans.xml", "beans.xml")
			.addAsResource("META-INF/test-orm.xml", "META-INF/orm.xml")
			// Deploy our test datasource
			.addAsWebInfResource("test-ds.xml");
		//Folgende Zeile gibt im /tmp dir das archiv aus zum debuggen nuetzlich
		new ZipExporterImpl(webArchive).exportTo(new File(System.getProperty("java.io.tmpdir"), "myWebArchive.war"), true);
		return webArchive;
	}

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private Persistence<ApplicationProperty> persistence;


	@Test
	public void saveOrUpdateApplicationPropertyTest() {
		Assert.assertNotNull(applicationPropertyService);
		applicationPropertyService.saveOrUpdateApplicationProperty("testKey", "testValue");
		Assert.assertEquals(1, applicationPropertyService.getAllApplicationProperties().size());
		Assert.assertEquals("testValue", applicationPropertyService.readApplicationProperty("testKey").get().getValue());

	}

	@Test
	public void removeApplicationPropertyTest() {
		insertNewEntity();
		applicationPropertyService.removeApplicationProperty("testKey");
		Assert.assertEquals(0, applicationPropertyService.getAllApplicationProperties().size());

	}
	@Test
	public void updateApplicationPropertyTest() {
		insertNewEntity();
		applicationPropertyService.saveOrUpdateApplicationProperty("testKey","changed");
		Assert.assertEquals("changed", applicationPropertyService.readApplicationProperty("testKey").get().getValue());

	}

	// Help Methods

	private void insertNewEntity() {
		persistence.persist(new ApplicationProperty("testKey", "testValue"));
		Assert.assertEquals(1, applicationPropertyService.getAllApplicationProperties().size());
		Assert.assertNotNull(applicationPropertyService.readApplicationProperty("testKey"));
		Assert.assertEquals("testValue", applicationPropertyService.readApplicationProperty("testKey").get().getValue());
	}

}
