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
package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;


/**
 * Testet den EbeguParameterService.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class EbeguParameterServiceTest extends AbstractEbeguTest {

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private Persistence<EbeguParameter> persistence;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createEbeguParameterTest() {
		Assert.assertNotNull(ebeguParameterService);
		EbeguParameter insertedEbeguParameter = insertEbeguParameter();

		Collection<EbeguParameter> allEbeguParameter = ebeguParameterService.getAllEbeguParameter();
		Assert.assertEquals(1, allEbeguParameter.size());
		EbeguParameter nextEbeguParameter = allEbeguParameter.iterator().next();
		Assert.assertEquals(insertedEbeguParameter.getName(), nextEbeguParameter.getName());
		Assert.assertEquals(insertedEbeguParameter.getValue(), nextEbeguParameter.getValue());
	}

	@Test
	public void updateEbeguParameterTest() {
		Assert.assertNotNull(ebeguParameterService);
		EbeguParameter insertedEbeguParameter = insertEbeguParameter();

		Optional<EbeguParameter> ebeguParameterOptional = ebeguParameterService.findEbeguParameter(insertedEbeguParameter.getId());
		Assert.assertTrue(ebeguParameterOptional.isPresent());
		EbeguParameter persistedInstStammdaten= ebeguParameterOptional.get();
		Assert.assertEquals(insertedEbeguParameter.getValue(), persistedInstStammdaten.getValue());

		persistedInstStammdaten.setValue("Mein Test Wert");
		EbeguParameter updatedEbeguParameter = ebeguParameterService.saveEbeguParameter(persistedInstStammdaten);
		Assert.assertEquals(persistedInstStammdaten.getValue(), updatedEbeguParameter.getValue());
	}

	@Test
	public void getAllEbeguParameterByDateTest() {
		Assert.assertNotNull(ebeguParameterService);
		EbeguParameter insertedEbeguParameter = insertEbeguParameter();

		Collection<EbeguParameter> allEbeguParameterByDate = ebeguParameterService.getAllEbeguParameterByDate(LocalDate.now());
		Assert.assertEquals(0, allEbeguParameterByDate.size());

		insertedEbeguParameter.setGueltigkeit(new DateRange(LocalDate.of(2010,1,1), Constants.END_OF_TIME));
		ebeguParameterService.saveEbeguParameter(insertedEbeguParameter);
		Collection<EbeguParameter> allEbeguParameterByDate2 = ebeguParameterService.getAllEbeguParameterByDate(LocalDate.now());
		Assert.assertEquals(1, allEbeguParameterByDate2.size());
	}

	private EbeguParameter insertEbeguParameter() {
		EbeguParameter ebeguParameter = TestDataUtil.createDefaultEbeguParameter();
		return ebeguParameterService.saveEbeguParameter(ebeguParameter);
	}
}
