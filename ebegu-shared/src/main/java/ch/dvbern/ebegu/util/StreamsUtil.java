package ch.dvbern.ebegu.util;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;

/**
 *
 */
public class StreamsUtil {
	private StreamsUtil() {
	}


	public static <T> BinaryOperator<T> toOnlyElement() {
		return toOnlyElementThrowing(() -> new EbeguRuntimeException("toOnlyElement", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, ""));
	}

	public static <T, E extends RuntimeException> BinaryOperator<T>
	toOnlyElementThrowing(Supplier<E> exception) {
		return (element, otherElement) -> {
			throw exception.get();
		};
	}
}
