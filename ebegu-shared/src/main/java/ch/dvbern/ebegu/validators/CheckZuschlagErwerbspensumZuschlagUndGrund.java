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

package ch.dvbern.ebegu.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import ch.dvbern.ebegu.entities.Erwerbspensum;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Wenn das {@link Erwerbspensum}#zuschlagZuErwerbspensum flag nicht gesetzt ist dann muss ein Zuschlagspensum gesetzt sein. Ansonsten soll nichts
 * gesetzt sein
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckZuschlagErwerbspensumZuschlagUndGrundValidator.class)
@Documented
public @interface CheckZuschlagErwerbspensumZuschlagUndGrund {

	String message() default "{invalid_zuschlag_zu_erwerbspensum}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
