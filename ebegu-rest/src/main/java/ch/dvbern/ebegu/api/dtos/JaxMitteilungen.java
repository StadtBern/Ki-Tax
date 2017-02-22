package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Jax wrapper um Mitteilungen-Liste zurueckgeben zu koennen. Ohne den Wrapper verliert man die Information
 * der verschiedenen Subtypen
 */
public class JaxMitteilungen implements Serializable {

	private static final long serialVersionUID = -5915063223908835792L;

	@Nonnull
	@NotNull
	private Collection<JaxMitteilung> mitteilungen = new ArrayList<>();

	public JaxMitteilungen() {}

	public JaxMitteilungen(Collection<JaxMitteilung> mitteilungen) {
		this.mitteilungen = mitteilungen;
	}

	@Nonnull
	public Collection<JaxMitteilung> getMitteilungen() {
		return mitteilungen;
	}

	public void setMitteilungen(@Nonnull Collection<JaxMitteilung> mitteilungen) {
		this.mitteilungen = mitteilungen;
	}
}
