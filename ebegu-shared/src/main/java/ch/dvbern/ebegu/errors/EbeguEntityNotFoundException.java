package ch.dvbern.ebegu.errors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by imanol on 01.03.16.
 * Exception die geworfen wird wenn kein Element gefunden wurde
 */
public class EbeguEntityNotFoundException extends EbeguException {

	private static final long serialVersionUID = 7990458569130165438L;

	public EbeguEntityNotFoundException(@Nonnull String methodeName, @Nonnull String message, @Nonnull String... messageArgs) {
		super(methodeName, message, messageArgs);
	}

	public EbeguEntityNotFoundException(@Nonnull String methodeName, @Nonnull String message, @Nullable Throwable cause, @Nonnull String... messageArgs) {
		super(methodeName, message, cause, messageArgs);
	}
}
