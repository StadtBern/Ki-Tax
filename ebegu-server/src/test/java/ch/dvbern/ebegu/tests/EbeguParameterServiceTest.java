/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_FIXBETRAG_STADT_PRO_TAG_KITA;

/**
 * Testet den EbeguParameterService.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class EbeguParameterServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private EbeguParameterService parameterService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	private static final EbeguParameterKey PARAM_KEY = EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA;

	@Test
	public void createEbeguParameterTest() {
		Assert.assertNotNull(parameterService);
		EbeguParameter insertedEbeguParameter = createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, Constants.GESUCHSPERIODE_17_18);;

		Collection<EbeguParameter> allEbeguParameter = parameterService.getAllEbeguParameter();
		Assert.assertEquals(1, allEbeguParameter.size());
		EbeguParameter nextEbeguParameter = allEbeguParameter.iterator().next();
		Assert.assertEquals(insertedEbeguParameter.getName(), nextEbeguParameter.getName());
		Assert.assertEquals(insertedEbeguParameter.getValue(), nextEbeguParameter.getValue());
	}

	@Test
	public void createEbeguParameterDuplicateTest() {
		createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, Constants.GESUCHSPERIODE_17_18);;
		try {
			createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, Constants.GESUCHSPERIODE_17_18);
			Assert.fail("It cannot create the same EbeguParameter twice. An Exception should've been thrown");
		} catch(Exception e) {
			//nop
		}
	}

	@Test
	public void updateEbeguParameterTest() {
		Assert.assertNotNull(parameterService);
		EbeguParameter insertedEbeguParameter = createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, Constants.GESUCHSPERIODE_17_18);

		Optional<EbeguParameter> ebeguParameterOptional = parameterService.findEbeguParameter(insertedEbeguParameter.getId());
		Assert.assertTrue(ebeguParameterOptional.isPresent());
		EbeguParameter persistedInstStammdaten = ebeguParameterOptional.get();
		Assert.assertEquals(insertedEbeguParameter.getValue(), persistedInstStammdaten.getValue());

		persistedInstStammdaten.setValue("Mein Test Wert");
		EbeguParameter updatedEbeguParameter = parameterService.saveEbeguParameter(persistedInstStammdaten);
		Assert.assertEquals(persistedInstStammdaten.getValue(), updatedEbeguParameter.getValue());
	}

	@Test
	public void getAllEbeguParameterByDateTest() {
		Assert.assertNotNull(parameterService);
		createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, Constants.GESUCHSPERIODE_17_18);
		Collection<EbeguParameter> allEbeguParameterByDate = parameterService.getAllEbeguParameterByDate(LocalDate.now());
		Assert.assertEquals(1, allEbeguParameterByDate.size());
	}

	@Test
	public void saveEbeguParameter() throws Exception {
		// Noch keine Params
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Optional<EbeguParameter> currentParameterOptional = parameterService.getEbeguParameterByKeyAndDate(EbeguParameterServiceTest.PARAM_KEY, LocalDate.now());

		Assert.assertTrue(allParameter.isEmpty());
		Assert.assertFalse(currentParameterOptional.isPresent());

		EbeguParameter param1 = TestDataUtil.createDefaultEbeguParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA);
		parameterService.saveEbeguParameter(param1);

		allParameter = parameterService.getAllEbeguParameter();
		currentParameterOptional = parameterService.getEbeguParameterByKeyAndDate(EbeguParameterServiceTest.PARAM_KEY, LocalDate.now());

		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertTrue(currentParameterOptional.isPresent());

		EbeguParameter currentParameter = currentParameterOptional.get();
		Assert.assertEquals(Constants.START_OF_TIME, currentParameter.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Constants.END_OF_TIME, currentParameter.getGueltigkeit().getGueltigBis());

	}

	@Test
	public void getEbeguParameterByGesuchsperiode() throws Exception {
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Assert.assertTrue(allParameter.isEmpty());

		Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiode.setGueltigkeit(new DateRange(LocalDate.of(2015, Month.AUGUST, 1), LocalDate.of(2016, Month.JULY, 31)));

		createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, gesuchsperiode.getGueltigkeit());

		allParameter = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertEquals(1, allParameter.size());
	}

	@Test
	public void getEbeguParameterByJahr() throws Exception {
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Assert.assertTrue(allParameter.isEmpty());

		createAndPersistParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, new DateRange(2015));

		allParameter = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertEquals(1, allParameter.size());
	}

	@Test
	public void testCopyEbeguParameterListToNewGesuchsperiode() {
		final Gesuchsperiode gesuchsperiode17 = createAndPersistPeriode1718();

		createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, gesuchsperiode17.getGueltigkeit());
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertEquals(1, allParameter.size());

		Gesuchsperiode gesuchsperiode18 = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiode18.setGueltigkeit(Constants.GESUCHSPERIODE_18_19);

		parameterService.copyEbeguParameterListToNewGesuchsperiode(gesuchsperiode18);

		Collection<EbeguParameter> allParameter2 = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter2.isEmpty());
		Assert.assertEquals(2, allParameter2.size());

		Collection<EbeguParameter> allParameter18 = parameterService.getEbeguParameterByGesuchsperiode(gesuchsperiode18);
		Assert.assertFalse(allParameter18.isEmpty());
		Assert.assertEquals(1, allParameter18.size());
		Assert.assertEquals(gesuchsperiode18.getGueltigkeit(), allParameter18.iterator().next().getGueltigkeit());
	}

	/**
	 * It doenst' get duplicated. If it exists it just doesn't create it again
	 */
	@Test
	public void testCopyEbeguParameterListToNewGesuchsperiodeDuplicate() {
		final Gesuchsperiode gesuchsperiode17 = createAndPersistPeriode1718();

		createAndPersistParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA, gesuchsperiode17.getGueltigkeit());
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertEquals(1, allParameter.size());

		parameterService.copyEbeguParameterListToNewGesuchsperiode(gesuchsperiode17);

		Collection<EbeguParameter> allParameter2 = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter2.isEmpty());
		Assert.assertEquals(1, allParameter2.size());
	}

	@Test
	public void testCreateEbeguParameterListForJahr() {
		createAndPersistPeriode1718();

		createAndPersistParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, new DateRange(2018));
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertEquals(1, allParameter.size());

		parameterService.createEbeguParameterListForJahr(2019);
		Collection<EbeguParameter> allParameter2 = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter2.isEmpty());
		Assert.assertEquals(2, allParameter2.size());

		Collection<EbeguParameter> allParameter2018 = parameterService.getEbeguParametersByJahr(2019);
		Assert.assertFalse(allParameter2018.isEmpty());
		Assert.assertEquals(1, allParameter2018.size());
		Assert.assertEquals(2019, allParameter2018.iterator().next().getGueltigkeit().getGueltigAb().getYear());
	}

	/**
	 * It doenst' get duplicated. If it exists it just doesn't create it again
	 */
	@Test
	public void testCreateEbeguParameterListForJahrDuplicate() {
		createAndPersistPeriode1718();

		createAndPersistParameter(PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, new DateRange(2017));
		Collection<EbeguParameter> allParameter = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter.isEmpty());
		Assert.assertEquals(1, allParameter.size());

		parameterService.createEbeguParameterListForJahr(2017);
		Collection<EbeguParameter> allParameter2 = parameterService.getAllEbeguParameter();
		Assert.assertFalse(allParameter2.isEmpty());
		Assert.assertEquals(1, allParameter2.size());
		Assert.assertEquals(2017, allParameter2.iterator().next().getGueltigkeit().getGueltigAb().getYear());
	}

	@Test
	public void getEbeguParameterByKeyAndDate() throws Exception {
		EbeguParameter param1 = TestDataUtil.createDefaultEbeguParameter(EbeguParameterKey.PARAM_ANZAL_TAGE_MAX_KITA);
		parameterService.saveEbeguParameter(param1);

		Optional<EbeguParameter> optional = parameterService.getEbeguParameterByKeyAndDate(EbeguParameterServiceTest.PARAM_KEY, LocalDate.now());
		Assert.assertTrue(optional.isPresent());
	}

	private EbeguParameter createAndPersistParameter(EbeguParameterKey paramAnzalTageMaxKita, DateRange gueltigkeit) {
		EbeguParameter parameter = TestDataUtil.createDefaultEbeguParameter(paramAnzalTageMaxKita);
		parameter.setGueltigkeit(gueltigkeit);
		return parameterService.saveEbeguParameter(parameter);
	}

	private Gesuchsperiode createAndPersistPeriode1718() {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiode.setGueltigkeit(Constants.GESUCHSPERIODE_17_18);
		gesuchsperiode.setStatus(GesuchsperiodeStatus.ENTWURF);
		return gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
	}
}
