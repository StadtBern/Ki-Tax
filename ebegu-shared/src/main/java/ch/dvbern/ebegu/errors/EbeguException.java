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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.ApplicationException;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

/**
 * Created by imanol on 01.03.16.
 * Oberklasse fuer checkedExceptions in ebegu
 */
@ApplicationException(rollback = true)
public class EbeguException extends Exception {

	private static final long serialVersionUID = -8018060653200749874L;

	private final String methodName;
	private final List<Serializable> args;
	private ErrorCodeEnum errorCodeEnum;
	private String customMessage;

	protected EbeguException(@Nullable String methodeName, @Nullable String message, @Nonnull Serializable... args) {
		super(message);
		methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(args));
	}

	protected EbeguException(@Nullable String methodeName, @Nullable String message, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(message, cause);
		this.methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(args));
	}

	public EbeguException(@Nullable String methodName, @Nullable String message, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(message, cause);
		this.errorCodeEnum = errorCodeEnum;
		this.methodName = methodName;
		this.args = Collections.unmodifiableList(Arrays.asList(args));
	}

	public EbeguException(@Nullable String methodName, @Nullable String message, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Serializable... args) {
		super(message);
		this.errorCodeEnum = errorCodeEnum;
		this.methodName = methodName;
		this.args = Collections.unmodifiableList(Arrays.asList(args));
	}

	public EbeguException(@Nullable String methodName, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Serializable... args) {
		super();
		this.errorCodeEnum = errorCodeEnum;
		this.methodName = methodName;
		this.args = Collections.unmodifiableList(Arrays.asList(args));
	}

	public List<Serializable> getArgs() {
		return args;
	}

	public String getMethodName() {
		return methodName;
	}

	public ErrorCodeEnum getErrorCodeEnum() {
		return errorCodeEnum;
	}

	public String getCustomMessage() {
		return customMessage;
	}
}
