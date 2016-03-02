package ch.dvbern.ebegu.errors;

import javax.annotation.Nonnull;

/**
 * Created by imanol on 02.03.16.
 */
public class EbeguRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 306424922900479199L;

	private final EbeguException ebeguException;


	public <T extends EbeguException> EbeguRuntimeException(@Nonnull T ebeguException) {
		this.ebeguException = ebeguException;
	}

	public EbeguException getEbeguException() {
		return ebeguException;
	}

}
