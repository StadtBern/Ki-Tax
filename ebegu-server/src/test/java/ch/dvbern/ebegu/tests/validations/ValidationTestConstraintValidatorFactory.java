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

package ch.dvbern.ebegu.tests.validations;

import javax.persistence.EntityManagerFactory;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;

import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tests.services.EbeguDummyParameterServiceBean;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumValidator;

/**
 * This class helps us thest our ConstraintValidators without actually starting a CDI container.
 * Since we are using services inside the validators we need a way to initialize the Validator with a dummy.
 * This Factory allows us to initialize the Validator ourself, giving us the oppurtunity to use a DummyService for the validotr
 */
public class ValidationTestConstraintValidatorFactory implements ConstraintValidatorFactory {

	final EntityManagerFactory entityManagerFactory;

	public ValidationTestConstraintValidatorFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		if (key == CheckBetreuungspensumValidator.class) {
			//Mock Service for Parameters
			EbeguParameterService dummyParamService = new EbeguDummyParameterServiceBean();
			return (T) new CheckBetreuungspensumValidator(dummyParamService, entityManagerFactory);
		}
		ConstraintValidatorFactory delegate = Validation.byDefaultProvider().configure().getDefaultConstraintValidatorFactory();
		return delegate.getInstance(key);
	}

	@Override
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		//nothing to do
	}
}
