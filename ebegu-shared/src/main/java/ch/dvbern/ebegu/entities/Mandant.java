package ch.dvbern.ebegu.entities;

import java.util.Objects;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von Mandant in der Datenbank.
 */
@Audited
@Entity
public class Mandant extends AbstractEntity {

	private static final long serialVersionUID = -8433487433884700618L;
	public static final String MANDANT_PARAMETER = "mandant";

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String name;

	public Mandant() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof Mandant)) {
			return false;
		}
		final Mandant otherMandant = (Mandant) other;
		return Objects.equals(getName(), otherMandant.getName());
	}
}
