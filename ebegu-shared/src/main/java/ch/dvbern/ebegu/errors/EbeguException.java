package ch.dvbern.ebegu.errors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by imanol on 01.03.16.
 * Oberklasse fuer checkedExceptions in ebegu
 */
public class EbeguException extends Exception {

	private static final long serialVersionUID = -8018060653200749874L;

	private final String methodName;
	private final List<String> args;


	protected EbeguException(@Nonnull String methodeName, @Nonnull String message, @Nonnull String... messageArgs) {
		super(message);
		methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	protected EbeguException(@Nonnull String methodeName, @Nonnull String message, @Nullable Throwable cause, @Nonnull String... messageArgs) {
		super(message, cause);
		this.methodName = methodeName;
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}


	public List<String> getArgs() {
		return args;
	}


	public String getMethodName() {
		return methodName;
	}



}
