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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Exception die geworfen wird, wenn es bereits ein offener Antrag existiert, der
 * die Erstellung einer Mutation/Follgegesuch blockiert
 */
public class EbeguExistingAntragException extends EbeguRuntimeException {

	private static final long serialVersionUID = 7990451269130155438L;

	private final String gesuchId;


	public EbeguExistingAntragException(@Nullable String methodName, @Nonnull ErrorCodeEnum code,
										@Nonnull String gesuchID, @Nonnull Serializable... args) {
		super(methodName, code, args);
		this.gesuchId = gesuchID;
	}

	public EbeguExistingAntragException(@Nullable String methodName, @Nonnull ErrorCodeEnum code,
										@Nullable Throwable cause, @Nonnull String gesuchID, @Nonnull Serializable... args) {
		super(methodName, code, cause, args);
		this.gesuchId = gesuchID;
	}

	public String getGesuchId() {
		return gesuchId;
	}
}
