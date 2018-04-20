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

import javax.ejb.ApplicationException;

/**
 * Exception that gets thrown if the transmission of an email fails
 */
@ApplicationException(rollback = false)
public class MailException extends Exception {

	private static final long serialVersionUID = 663284993138581412L;

	public MailException() {
	}

	public MailException(final Throwable cause) {
		super(cause);
	}

	public MailException(final String message) {
		super(message);
	}

	public MailException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
