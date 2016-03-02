package ch.dvbern.ebegu.errors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AbstractEntity;
import java.io.Serializable;

/**
 * Created by imanol on 01.03.16.
 */
public class EbeguEntityNotFoundException extends EbeguException {

	private static final long serialVersionUID = 7990458569130165438L;

	public EbeguEntityNotFoundException(@Nonnull Class<? extends AbstractEntity> entityClass,
										@Nonnull final String attributeValue,
										@Nonnull final String attributeName) {
		this(null, entityClass, attributeValue, attributeName);
	}

	public EbeguEntityNotFoundException(@Nullable Throwable cause, @Nonnull Class<? extends AbstractEntity> entityClass,
										@Nonnull final String attributeValue,
										@Nonnull final String attributeName) {
		super(cause, entityClass.getSimpleName(), attributeValue, attributeName);
	}

	@SuppressWarnings("OverloadedVarargsMethod")
	public EbeguEntityNotFoundException(@Nonnull Class<? extends AbstractEntity> entityClass, @Nonnull Serializable... args) {
		super(entityClass, args);
	}

}
