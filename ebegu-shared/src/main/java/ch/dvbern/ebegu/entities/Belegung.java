package ch.dvbern.ebegu.entities;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.Valid;

import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;

/**
 * Entity for the Belegung of the Tageschulangebote in a Betreuung.
 */
@Audited
@Entity
public class Belegung extends AbstractEntity {

	private static final long serialVersionUID = -8403435739182708718L;

	@Nullable
	@Valid
	@SortNatural
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Modul> module = new TreeSet<>();

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		//noinspection RedundantIfStatement
		if (!(other instanceof Belegung)) {
			return false;
		}
		return true;
	}

	@Nullable
	public Set<Modul> getModule() {
		return module;
	}

	public void setModule(@Nullable Set<Modul> module) {
		this.module = module;
	}
}
