package ch.dvbern.ebegu.errors;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

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

	public EbeguEntityNotFoundException(@Nullable String methodName,  @Nonnull  String message,  @Nullable ErrorCodeEnum errorCodeEnum,  @Nonnull Serializable... args) {
		super(methodName, message, errorCodeEnum, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodName,  @Nullable ErrorCodeEnum errorCodeEnum, @Nonnull  Serializable... args) {
		super(methodName,  errorCodeEnum, args);
	}

	public EbeguEntityNotFoundException(@Nullable String methodName,  @Nullable  ErrorCodeEnum errorCodeEnum, @Nullable Throwable cause, @Nonnull  Serializable... args) {
		super(methodName, errorCodeEnum, cause, args);
	}
}
