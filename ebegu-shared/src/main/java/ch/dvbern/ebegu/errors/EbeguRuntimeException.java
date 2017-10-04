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
 * Created by imanol on 02.03.16.
 * Oberklasse fuer Runtime Exceptions in ebegu
 */
@ApplicationException(rollback = true)
public class EbeguRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 306424922900479199L;

	private final String methodName;
	private final List<Serializable> args;
	private ErrorCodeEnum errorCodeEnum = null;
	private String customMessage;

	public EbeguRuntimeException(@Nullable String methodeName, @Nonnull String message, @Nonnull Serializable... messageArgs) {
		super(message);
		methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	public EbeguRuntimeException(@Nullable String methodeName, @Nonnull String message, @Nullable String customMessage, @Nonnull Serializable... messageArgs) {
		super(message);
		methodName = methodeName;
		this.customMessage = customMessage;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));

	}

	public EbeguRuntimeException(@Nullable String methodeName, @Nullable String message, @Nullable Throwable cause, @Nonnull Serializable... messageArgs) {
		super(message, cause);
		this.methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	protected EbeguRuntimeException(@Nullable String methodeName, @Nullable String message, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull Serializable... messageArgs) {
		super(message, cause);
		this.errorCodeEnum = errorCodeEnum;
		this.methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	protected EbeguRuntimeException(@Nullable String methodeName, @Nullable String message, @Nullable String customMessage, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull Serializable... messageArgs) {
		super(message, cause);
		this.errorCodeEnum = errorCodeEnum;
		this.customMessage = customMessage;
		this.methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	public EbeguRuntimeException(@Nullable String methodName, @Nullable ErrorCodeEnum errorCodeEnum, @Nonnull Serializable... args) {
		super(errorCodeEnum != null ? errorCodeEnum.name() : null);
		this.methodName = methodName;
		this.errorCodeEnum = errorCodeEnum;
		this.args = Collections.unmodifiableList(Arrays.asList(args));

	}

	public EbeguRuntimeException(@Nullable String methodName, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull Serializable... args) {
		super(cause);
		this.methodName = methodName;
		this.errorCodeEnum = errorCodeEnum;
		this.args = Collections.unmodifiableList(Arrays.asList(args));

	}

	public String getMethodName() {
		return methodName;
	}

	public List<Serializable> getArgs() {
		return args;
	}

	public ErrorCodeEnum getErrorCodeEnum() {
		return errorCodeEnum;
	}

	public String getCustomMessage() {
		return customMessage;
	}
}
