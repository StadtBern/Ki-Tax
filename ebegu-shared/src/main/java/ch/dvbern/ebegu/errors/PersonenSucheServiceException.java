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

package ch.dvbern.ebegu.errors;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

/**
 * Exception, welche geworfen wird wenn beim Aufruf des EWK-Service ein Fehler passiert
 */
public class PersonenSucheServiceException extends EbeguException {

	private static final long serialVersionUID = 5438097529958118878L;

	public PersonenSucheServiceException(final String methodname, final String message) {
		super(methodname, message, ErrorCodeEnum.ERROR_PERSONENSUCHE_TECHNICAL);
	}

	public PersonenSucheServiceException(final String methodname, final String message, final Throwable cause) {
		super(methodname, message, ErrorCodeEnum.ERROR_PERSONENSUCHE_TECHNICAL, cause);
	}
}
