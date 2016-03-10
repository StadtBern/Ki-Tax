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
 * Created by imanol on 02.03.16.
 * Oberklasse fuer Runtime Exceptions in ebegu
 */
@ApplicationException(rollback = true)
public class EbeguRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 306424922900479199L;

	private String methodName;
	private List<Serializable> args;
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
		super();
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
