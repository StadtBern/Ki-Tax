package ch.dvbern.ebegu.errors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by imanol on 02.03.16.
 * Oberklasse fuer Runtime Exceptions in ebegu
 */
public class EbeguRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 306424922900479199L;

	private final String methodName;
	private final List<String> args;


	public EbeguRuntimeException(@Nonnull String methodeName, @Nonnull String message, @Nonnull String... messageArgs) {
		super(message);
		methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	public EbeguRuntimeException(@Nonnull String methodeName, @Nonnull String message, @Nullable Throwable cause, @Nonnull String... messageArgs) {
		super(message,cause);
		this.methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	public String getMethodName() {
		return methodName;
	}

	public List<String> getArgs() {
		return args;
	}
}
