package ch.dvbern.ebegu.errors;

import ch.dvbern.ebegu.entities.AbstractEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by imanol on 01.03.16.
 */
public class EbeguException extends Exception {
	private static final long serialVersionUID = -8018060653200749874L;

	private final List<Serializable> args;

	protected EbeguException(@Nonnull Serializable... messageArgs) {
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	protected EbeguException(@Nullable Throwable cause, @Nonnull Serializable... messageArgs) {
		super(cause);
		this.args = Collections.unmodifiableList(Arrays.asList(messageArgs));
	}

	@Override
	public String getMessage() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append(Arrays.toString(args.toArray()))
			.build();
	}

	public List<Serializable> getArgs() {
		return args;
	}
}
