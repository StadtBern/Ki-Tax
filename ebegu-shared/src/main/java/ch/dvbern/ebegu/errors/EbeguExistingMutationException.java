package ch.dvbern.ebegu.errors;

import ch.dvbern.ebegu.enums.ErrorCodeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Exception die geworfen wird, wenn es bereits eine offene Mutation existiert
 */
public class EbeguExistingMutationException extends EbeguRuntimeException {

	private static final long serialVersionUID = 7990451269130155438L;

	private final String gesuchId;


	public EbeguExistingMutationException(@Nullable String methodName, @Nonnull String gesuchID, @Nonnull Serializable... args) {
		super(methodName, ErrorCodeEnum.ERROR_EXISTING_ONLINE_MUTATION, ErrorCodeEnum.ERROR_EXISTING_ONLINE_MUTATION, args);
		this.gesuchId = gesuchID;
	}

	public String getGesuchId() {
		return gesuchId;
	}
}
