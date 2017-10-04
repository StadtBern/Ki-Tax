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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

/**
 * Created by imanol on 01.03.16.
 * Exception die geworfen wird wenn kein Element gefunden wurde
 */
public class EbeguEntityNotFoundException extends EbeguRuntimeException {

	private static final long serialVersionUID = 7990458569130165438L;

	public EbeguEntityNotFoundException(@Nullable String methodeName, @Nonnull String message, @Nonnull Serializable... args) {
		super(methodeName, message, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodeName, @Nonnull String message, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(methodeName, message, cause, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodName, @Nonnull String message, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(methodName, message, errorCodeEnum, cause, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodName, @Nonnull String message, @Nullable ErrorCodeEnum errorCodeEnum, @Nonnull Serializable... args) {
		super(methodName, message, errorCodeEnum, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodName, @Nullable ErrorCodeEnum errorCodeEnum, @Nonnull Serializable... args) {
		super(methodName, errorCodeEnum, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodName, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(methodName, errorCodeEnum, cause, args);
	}
}
