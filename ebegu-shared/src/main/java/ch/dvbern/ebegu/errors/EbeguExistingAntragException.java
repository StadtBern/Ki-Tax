package ch.dvbern.ebegu.errors;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Exception die geworfen wird, wenn es bereits ein offener Antrag existiert, der
 * die Erstellung einer Mutation/Follgegesuch blockiert
 */
public class EbeguExistingAntragException extends EbeguRuntimeException {

	private static final long serialVersionUID = 7990451269130155438L;

	private final String gesuchId;


	public EbeguExistingAntragException(@Nullable String methodName, @Nonnull ErrorCodeEnum code,
										@Nonnull String gesuchID, @Nonnull Serializable... args) {
		super(methodName, code, args);
		this.gesuchId = gesuchID;
	}

	public EbeguExistingAntragException(@Nullable String methodName, @Nonnull ErrorCodeEnum code,
										@Nullable Throwable cause, @Nonnull String gesuchID, @Nonnull Serializable... args) {
		super(methodName, code, cause, args);
		this.gesuchId = gesuchID;
	}

	public String getGesuchId() {
		return gesuchId;
	}
}
