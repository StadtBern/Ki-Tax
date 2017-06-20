package ch.dvbern.ebegu.entities;

import java.util.Objects;

import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entitaet zum Speichern von Fachstellen in der Datenbank.
 */
@Audited
@Entity
public class Fachstelle extends AbstractEntity {

	private static final long serialVersionUID = -7687613920281069860L;

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String name;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String beschreibung;

	@Column(nullable = false)
	private boolean behinderungsbestaetigung;


	public Fachstelle() {
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nullable
	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(@Nullable String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public boolean isBehinderungsbestaetigung() {
		return behinderungsbestaetigung;
	}

	public void setBehinderungsbestaetigung(boolean behinderungsbestaetigung) {
		this.behinderungsbestaetigung = behinderungsbestaetigung;
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
		final Fachstelle otherGesuchsteller = (Fachstelle) other;
		return Objects.equals(getName(), otherGesuchsteller.getName()) &&
			Objects.equals(getBeschreibung(), otherGesuchsteller.getBeschreibung()) &&
			Objects.equals(isBehinderungsbestaetigung(), otherGesuchsteller.isBehinderungsbestaetigung());
	}
}
