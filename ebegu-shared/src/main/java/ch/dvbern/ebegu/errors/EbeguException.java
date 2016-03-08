package ch.dvbern.ebegu.errors;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.ApplicationException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	public EbeguException(@Nullable String methodName, @Nullable String message, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull  Serializable... args) {
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
		this.args =Collections.unmodifiableList(Arrays.asList(args));
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
